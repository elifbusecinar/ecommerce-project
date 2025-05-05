import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CartService } from '../../../core/services/cart.service';
import { CartItem } from '../../../core/models/cart-item.model';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.scss']
})
export class CartComponent implements OnInit {
  cartItems: CartItem[] = [];
  subtotal = 0;
  shipping = 5.99;
  tax = 0;
  total = 0;

  constructor(private cartService: CartService) {}

  ngOnInit() {
    this.loadCart();
  }

  loadCart() {
    this.cartService.getCart().subscribe(items => {
      this.cartItems = items;
      this.calculateTotals();
    });
  }

  increaseQuantity(item: CartItem) {
    this.cartService.updateQuantity(item.product.id, item.quantity + 1)
      .subscribe(() => this.loadCart());
  }

  decreaseQuantity(item: CartItem) {
    if (item.quantity > 1) {
      this.cartService.updateQuantity(item.product.id, item.quantity - 1)
        .subscribe(() => this.loadCart());
    }
  }

  removeItem(item: CartItem) {
    this.cartService.removeItem(item.product.id)
      .subscribe(() => this.loadCart());
  }

  calculateTotals() {
    this.subtotal = this.cartItems.reduce(
      (sum, item) => sum + (item.product.price * item.quantity),
      0
    );
    this.tax = this.subtotal * 0.1; // 10% tax
    this.total = this.subtotal + this.shipping + this.tax;
  }

  proceedToCheckout() {
    // Implement checkout navigation
    console.log('Proceeding to checkout...');
  }
} 