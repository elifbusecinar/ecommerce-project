import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CartService } from '../../../core/services/cart.service';
import { CartItem } from '../../../core/models/cart-item.model';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
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

  updateQuantity(productId: number, quantity: number) {
    if (quantity < 1) return;
    this.cartService.updateQuantity(productId, quantity)
      .subscribe(() => this.loadCart());
  }

  removeItem(productId: number) {
    this.cartService.removeItem(productId)
      .subscribe(() => this.loadCart());
  }

  calculateTotals() {
    this.subtotal = this.cartItems.reduce(
      (sum, item) => sum + item.subtotal,
      0
    );
    this.tax = this.subtotal * 0.1; // 10% tax
    this.total = this.subtotal + this.shipping + this.tax;
  }

  proceedToCheckout() {
    // TODO: Implement checkout navigation
    console.log('Proceeding to checkout...');
  }
} 