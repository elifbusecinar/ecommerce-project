import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CartService } from '../../core/services/cart.service';
import { AuthService } from '../../core/services/auth.service';
import { PaymentService, PaymentRequest, PaymentResponse } from '../../core/services/payment.service';
import { OrderService } from '../../core/services/order.service';
import { CartItem } from '../../core/models/cart-item.model';
import { Order, OrderItem, OrderStatus } from '../../core/models/order.model';
import { firstValueFrom } from 'rxjs';

@Component({
  selector: 'app-checkout',
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule]
})
export class CheckoutComponent implements OnInit {
  checkoutForm: FormGroup;
  cartItems: CartItem[] = [];
  totalPrice = 0;
  isProcessing = false;
  paymentError: string | null = null;

  constructor(
    private fb: FormBuilder,
    private cartService: CartService,
    private paymentService: PaymentService,
    private authService: AuthService,
    private orderService: OrderService,
    private router: Router
  ) {
    this.checkoutForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      address: ['', Validators.required],
      city: ['', Validators.required],
      state: ['', Validators.required],
      zipCode: ['', [Validators.required, Validators.pattern('^[0-9]{5}$')]],
      cardNumber: ['', [Validators.required, Validators.pattern('^[0-9]{16}$')]],
      expiryDate: ['', [Validators.required, Validators.pattern('^(0[1-9]|1[0-2])\\/([0-9]{2})$')]],
      cvv: ['', [Validators.required, Validators.pattern('^[0-9]{3,4}$')]]
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
        }
      });
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
      }
    });
  }

  async onSubmit() {
    if (this.checkoutForm.valid && this.cartItems.length > 0) {
      this.isProcessing = true;
      this.paymentError = null;

      try {
        const paymentRequest: PaymentRequest = {
          amount: this.totalPrice,
          cardNumber: this.checkoutForm.get('cardNumber')?.value,
          expiryDate: this.checkoutForm.get('expiryDate')?.value,
          cvv: this.checkoutForm.get('cvv')?.value
        };

        const paymentResult = await firstValueFrom(this.paymentService.processPayment(paymentRequest));

        if (paymentResult.success) {
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
            status: OrderStatus.PENDING,
            shippingAddress: {
              firstName: this.checkoutForm.get('firstName')?.value,
              lastName: this.checkoutForm.get('lastName')?.value,
              email: this.checkoutForm.get('email')?.value,
              address: this.checkoutForm.get('address')?.value,
              city: this.checkoutForm.get('city')?.value,
              state: this.checkoutForm.get('state')?.value,
              zipCode: this.checkoutForm.get('zipCode')?.value
            }
          };

          this.orderService.createOrder(orderData).subscribe({
            next: (order: Order) => {
              this.cartService.clearCart().subscribe(() => {
                this.router.navigate(['/order/confirmation'], {
                  queryParams: { orderId: order.id }
                });
              });
            },
            error: (error) => {
              this.paymentError = 'Failed to create order. Please try again.';
              this.isProcessing = false;
              console.error('Error creating order:', error);
            }
          });
        } else {
          this.paymentError = paymentResult.error || 'Payment failed. Please try again.';
          this.isProcessing = false;
        }
      } catch (error) {
        this.paymentError = 'An error occurred while processing your payment.';
        this.isProcessing = false;
        console.error('Payment processing error:', error);
      }
    }
  }
} 