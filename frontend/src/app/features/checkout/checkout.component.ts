import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http'; 
import { switchMap } from 'rxjs/operators'; 

import { StripeService as NgxStripeService, StripeCardComponent } from 'ngx-stripe';
import { StripeElementsOptions, StripeCardElementOptions } from '@stripe/stripe-js';

import { CartService } from '../../core/services/cart.service';
import { AuthService } from '../../core/services/auth.service';
import { PaymentService, PaymentRequest, PaymentResponse } from '../../core/services/payment.service';
import { OrderService } from '../../core/services/order.service';
import { CartItem } from '../../core/models/cart-item.model';
import { Order, OrderItem, OrderStatus, PaymentStatus } from '../../core/models/order.model';
import { firstValueFrom } from 'rxjs';

import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-checkout',
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, StripeCardComponent]
})
export class CheckoutComponent implements OnInit {
  @ViewChild(StripeCardComponent) card!: StripeCardComponent;
  checkoutForm: FormGroup;
  cartItems: CartItem[] = [];
  totalPrice = 0;
  isProcessing = false;
  paymentError: string | null = null;

    cardOptions: StripeCardElementOptions = {
    style: {
      base: {
        iconColor: '#666EE8',
        color: '#31325F',
        fontWeight: '300',
        fontFamily: '"Helvetica Neue", Helvetica, sans-serif',
        fontSize: '18px',
        '::placeholder': {
          color: '#CFD7E0',
        },
      },
    },
  };

  elementsOptions: StripeElementsOptions = {
    locale: 'auto', // veya 'tr'
  };

  constructor(
    private fb: FormBuilder,
    private cartService: CartService,
    private paymentService: PaymentService,
    private authService: AuthService,
    private orderService: OrderService,
    private router: Router,
    private http: HttpClient,
    private ngxStripeService: NgxStripeService
  ) {
    this.checkoutForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      address: ['', Validators.required],
      city: ['', Validators.required],
      state: ['', Validators.required],
      zipCode: ['', [Validators.required, Validators.pattern('^[0-9]{5}$')]],
      // cardNumber: ['', [Validators.required, Validators.pattern('^[0-9]{16}$')]],
      // expiryDate: ['', [Validators.required, Validators.pattern('^(0[1-9]|1[0-2])\\/([0-9]{2})$')]],
      // cvv: ['', [Validators.required, Validators.pattern('^[0-9]{3,4}$')]]
    });
  }

  ngOnInit() {
    this.loadCartItems();
    this.loadUserData();
  }

  loadUserData(): void {
    this.authService.currentUser$.subscribe(user => {
      if (user) {
        this.checkoutForm.patchValue({
          email: user.email,
          firstName: user.firstName,
          lastName: user.lastName
        });
      }
    });
  }

  loadCartItems(): void {
    this.cartService.getCart().subscribe({
      next: (items) => {
        this.cartItems = items;
        this.calculateTotal();
      },
      error: (error) => {
        this.paymentError = 'Failed to load cart items';
        console.error('Error loading cart items:', error);
      }
    });
  }

  calculateTotal(): number {
    this.totalPrice = this.cartItems.reduce(
      (sum, item) => sum + ((item.product?.price || 0) * item.quantity),
      0
    );
    return this.totalPrice;
  }

  updateQuantity(productId: number | undefined, quantity: number): void {
    if (!productId || quantity < 1) return;

    const item = this.cartItems.find(item => item.product?.id === productId);
    if (item && item.product?.stock && quantity <= item.product.stock) {
      this.cartService.updateQuantity(productId, quantity).subscribe({
        next: (items) => {
          this.cartItems = items;
          this.calculateTotal();
        },
        error: (error) => {
          console.error('Error updating quantity:', error);
          this.paymentError = 'Error updating item quantity.';
        }
      });
    } else if (item && item.product?.stock && quantity > item.product.stock) {
        this.paymentError = `Cannot add more than ${item.product.stock} items for ${item.product.name}.`;
    }
  }

removeItem(productId: number | undefined): void {
    if (!productId) return;

    this.cartService.removeItem(productId).subscribe({
      next: (items) => {
        this.cartItems = items;
        this.calculateTotal();
        if (items.length === 0) {
          this.router.navigate(['/cart']);
        }
      },
      error: (error) => {
        console.error('Error removing item:', error);
        this.paymentError = 'Error removing item from cart.';
      }
    });
  }

  async onSubmit() {
    if (this.checkoutForm.invalid) {
      this.paymentError = 'Please fill in all required shipping information.';
      this.checkoutForm.markAllAsTouched(); // Kullanıcıya eksik alanları göster
      return;
    }
    if (this.cartItems.length === 0) {
      this.paymentError = 'Your cart is empty.';
      return;
    }
    if (!this.card || !this.card.element) {
      this.paymentError = 'Stripe card element is not ready.';
      return;
    }

    this.isProcessing = true;
    this.paymentError = null;

    const billingDetails = {
      name: `${this.checkoutForm.get('firstName')?.value} ${this.checkoutForm.get('lastName')?.value}`,
      email: this.checkoutForm.get('email')?.value,
      address: {
        line1: this.checkoutForm.get('address')?.value,
        city: this.checkoutForm.get('city')?.value,
        state: this.checkoutForm.get('state')?.value,
        postal_code: this.checkoutForm.get('zipCode')?.value,
        country: 'TR', // Veya dinamik olarak alın
      },
    };

    try {
      // 1. Backend'den Payment Intent oluştur ve clientSecret'ı al
      const { clientSecret, paymentIntentId } = await firstValueFrom(
        this.http.post<{clientSecret: string, paymentIntentId: string}>(`${environment.apiUrl}/payments/create-payment-intent`, {
          amount: this.totalPrice, // Backend bu tutarı cent'e çevirecek
          currency: 'try' // Para birimini projenize göre ayarlayın (örn: 'usd', 'eur')
        })
      );

      if (!clientSecret) {
        this.paymentError = 'Could not initialize payment. Please try again.';
        this.isProcessing = false;
        return;
      }

      // 2. Stripe ile ödemeyi onayla (ngx-stripe kullanarak)
      this.ngxStripeService
        .confirmCardPayment(clientSecret, {
          payment_method: {
            card: this.card.element,
            billing_details: billingDetails,
          },
        })
        .subscribe(async (result) => {
          if (result.error) {
            this.paymentError = result.error.message || 'Ödeme sırasında bir hata oluştu.';
            this.isProcessing = false;
          } else {
            if (result.paymentIntent?.status === 'succeeded') {
              console.log('Ödeme başarılı! PaymentIntent ID:', result.paymentIntent.id);
              // 3. Ödeme başarılıysa backend'de siparişi oluştur/tamamla
              const orderItems: OrderItem[] = this.cartItems.map(item => ({
                productId: item.product.id,
                productName: item.product.name,
                quantity: item.quantity,
                unitPrice: item.product.price,
                totalPrice: item.product.price * item.quantity
              }));
              const orderData: Partial<Order> = {
                items: orderItems,
                totalAmount: this.totalPrice,
                status: OrderStatus.PROCESSING, // Ödeme başarılı olduğu için PROCESSING olabilir
                paymentStatus: PaymentStatus.COMPLETED, // Ödeme tamamlandı
                trackingNumber: paymentIntentId, // Stripe PaymentIntent ID'sini takip no olarak kullanabiliriz veya ayrı bir tane oluşturulabilir.
                shippingAddress: {
                  firstName: this.checkoutForm.get('firstName')?.value,
                  lastName: this.checkoutForm.get('lastName')?.value,
                  email: this.checkoutForm.get('email')?.value,
                  address: this.checkoutForm.get('address')?.value,
                  city: this.checkoutForm.get('city')?.value,
                  state: this.checkoutForm.get('state')?.value,
                  zipCode: this.checkoutForm.get('zipCode')?.value,
                },
                // user: await firstValueFrom(this.authService.currentUser$) // Kullanıcı bilgisi
            };
            // Kullanıcı ID'sini ekle
            const currentUser = await firstValueFrom(this.authService.currentUser$);
              if (currentUser && currentUser.id) {
                orderData.userId = currentUser.id;
            }
            this.orderService.createOrder(orderData).subscribe({
                next: (order: Order) => {
                  this.cartService.clearCart().subscribe(() => {
                    // Sipariş onay sayfasına yönlendirirken orderId ve paymentIntentId'yi gönder
                    this.router.navigate(['/order-confirmation'], { // app.routes.ts'deki order-confirmation yolu
                      queryParams: { orderId: order.id, paymentIntentId: result.paymentIntent?.id }
                    });
                  });
                },
                error: (orderError) => {
                  this.paymentError = 'Failed to create order after successful payment. Please contact support.';
                  console.error('Error creating order:', orderError);
                  this.isProcessing = false;
                  // Burada ödemenin iadesi veya manuel işlem gerekebilir.
                }
              });
            } else {
              this.paymentError = 'Payment was not successful. Please try again.';
              this.isProcessing = false;
            }
          }
        });
    } catch (error: any) {
      this.paymentError = error?.error?.error || error?.message || 'An unexpected error occurred while processing your payment.';
      this.isProcessing = false;
      console.error('Payment processing error:', error);
    }
  }
}

