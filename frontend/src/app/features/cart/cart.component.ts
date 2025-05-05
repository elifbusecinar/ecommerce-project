import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CartService } from '../../core/services/cart.service';
import { CartItem } from '../../core/models/cart-item.model';

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.scss'],
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule]
})
export class CartComponent implements OnInit {
  cartItems: CartItem[] = [];
  totalItems = 0;
  totalPrice = 0;
  loading = false;
  error: string | null = null;

  constructor(
    private cartService: CartService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadCartItems();
  }

  loadCartItems() {
    this.loading = true;
    this.cartService.getCartItems().subscribe({
      next: (items) => {
        this.cartItems = items;
        this.totalItems = this.cartService.getTotalItems();
        this.totalPrice = this.cartService.getTotalPrice();
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Sepet yüklenirken bir hata oluştu.';
        this.loading = false;
        console.error('Sepet yükleme hatası:', error);
      }
    });
  }

  updateQuantity(productId: number, quantity: number) {
    if (quantity > 0) {
      this.cartService.updateQuantity(productId, quantity).subscribe({
        next: (items) => {
          this.cartItems = items;
          this.totalItems = this.cartService.getTotalItems();
          this.totalPrice = this.cartService.getTotalPrice();
        },
        error: (error) => {
          this.error = 'Ürün miktarı güncellenirken bir hata oluştu.';
          console.error('Miktar güncelleme hatası:', error);
        }
      });
    }
  }

  removeItem(productId: number) {
    this.cartService.removeFromCart(productId).subscribe({
      next: (items) => {
        this.cartItems = items;
        this.totalItems = this.cartService.getTotalItems();
        this.totalPrice = this.cartService.getTotalPrice();
      },
      error: (error) => {
        this.error = 'Ürün sepetten çıkarılırken bir hata oluştu.';
        console.error('Ürün çıkarma hatası:', error);
      }
    });
  }

  clearCart() {
    this.cartService.clearCart().subscribe({
      next: () => {
        this.cartItems = [];
        this.totalItems = 0;
        this.totalPrice = 0;
      },
      error: (error) => {
        this.error = 'Sepet temizlenirken bir hata oluştu.';
        console.error('Sepet temizleme hatası:', error);
      }
    });
  }

  checkout() {
    if (this.cartItems.length > 0) {
      this.router.navigate(['/checkout']);
    }
  }
} 