import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { Product } from '../models/product.model';
import { CartItem } from '../models/cart-item.model';
import { Cart } from '../models/cart.model';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private apiUrl = `${environment.apiUrl}/cart`;
  private cartItems = new BehaviorSubject<CartItem[]>([]);
  private cartItemCountSubject = new BehaviorSubject<number>(0);
  cartItemCount$ = this.cartItemCountSubject.asObservable();

  constructor(private http: HttpClient) {
    this.loadCart();
    this.loadCartCount();
  }

  private loadCart(): void {
    this.http.get<Cart>(this.apiUrl, { withCredentials: true }).subscribe(
      cart => this.cartItems.next(cart.items),
      error => console.error('Error loading cart:', error)
    );
  }

  private loadCartCount(): void {
    this.getCart().subscribe(
      items => this.cartItemCountSubject.next(items.reduce((sum, item) => sum + item.quantity, 0)),
      error => console.error('Error loading cart count:', error)
    );
  }

  getCart(): Observable<CartItem[]> {
    return this.cartItems.asObservable();
  }

  getCartItems(): Observable<CartItem[]> {
    return this.getCart();
  }

  getTotalItems(): number {
    return this.cartItems.value.reduce((total, item) => total + item.quantity, 0);
  }

  getTotalPrice(): number {
    return this.cartItems.value.reduce(
      (total, item) => total + item.subtotal,
      0
    );
  }

  addToCart(product: Product, quantity: number = 1): Observable<CartItem[]> {
    return this.http.post<Cart>(this.apiUrl, { productId: product.id, quantity }, { withCredentials: true }).pipe(
      map(cart => {
        this.cartItems.next(cart.items);
        return cart.items;
      })
    );
  }

  removeFromCart(productId: number): Observable<CartItem[]> {
    return this.removeItem(productId);
  }

  updateQuantity(productId: number, quantity: number): Observable<CartItem[]> {
    return this.http.put<Cart>(`${this.apiUrl}/${productId}`, { quantity }, { withCredentials: true }).pipe(
      map(cart => {
        this.cartItems.next(cart.items);
        return cart.items;
      })
    );
  }

  removeItem(productId: number): Observable<CartItem[]> {
    return this.http.delete<Cart>(`${this.apiUrl}/${productId}`, { withCredentials: true }).pipe(
      map(cart => {
        this.cartItems.next(cart.items);
        return cart.items;
      })
    );
  }

  clearCart(): Observable<CartItem[]> {
    return this.http.delete<Cart>(this.apiUrl, { withCredentials: true }).pipe(
      map(cart => {
        this.cartItems.next(cart.items);
        return cart.items;
      })
    );
  }

  private updateCartCount(items: CartItem[]): void {
    this.cartItemCountSubject.next(items.reduce((sum, item) => sum + item.quantity, 0));
  }
} 