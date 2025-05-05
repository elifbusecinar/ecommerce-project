import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { Product } from '../models/product.model';
import { CartItem } from '../models/cart-item.model';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private apiUrl = `${environment.apiUrl}/cart`;
  private cartItems = new BehaviorSubject<CartItem[]>([]);

  constructor(private http: HttpClient) {
    this.loadCart();
  }

  private loadCart(): void {
    this.http.get<CartItem[]>(this.apiUrl).subscribe(
      items => this.cartItems.next(items),
      error => console.error('Error loading cart:', error)
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
      (total, item) => total + (item.product.price * item.quantity),
      0
    );
  }

  addToCart(product: Product, quantity: number = 1): Observable<CartItem[]> {
    return this.http.post<CartItem[]>(this.apiUrl, { productId: product.id, quantity }).pipe(
      map(items => {
        this.cartItems.next(items);
        return items;
      })
    );
  }

  removeFromCart(productId: number): Observable<CartItem[]> {
    return this.removeItem(productId);
  }

  updateQuantity(productId: number, quantity: number): Observable<CartItem[]> {
    return this.http.put<CartItem[]>(`${this.apiUrl}/${productId}`, { quantity }).pipe(
      map(items => {
        this.cartItems.next(items);
        return items;
      })
    );
  }

  removeItem(productId: number): Observable<CartItem[]> {
    return this.http.delete<CartItem[]>(`${this.apiUrl}/${productId}`).pipe(
      map(items => {
        this.cartItems.next(items);
        return items;
      })
    );
  }

  clearCart(): Observable<CartItem[]> {
    return this.http.delete<CartItem[]>(this.apiUrl).pipe(
      map(items => {
        this.cartItems.next([]);
        return items;
      })
    );
  }
} 