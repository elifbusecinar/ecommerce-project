import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class WishlistService {
  private wishlistCountSubject = new BehaviorSubject<number>(0);
  wishlistCount$ = this.wishlistCountSubject.asObservable();

  constructor(private http: HttpClient) {
    this.loadWishlistCount();
  }

  private loadWishlistCount(): void {
    this.http.get<number>(`${environment.apiUrl}/wishlist/count`).subscribe(
      count => this.wishlistCountSubject.next(count),
      error => console.error('Error loading wishlist count:', error)
    );
  }

  addToWishlist(productId: number): Observable<any> {
    return this.http.post(`${environment.apiUrl}/wishlist/add`, { productId });
  }

  removeFromWishlist(productId: number): Observable<any> {
    return this.http.delete(`${environment.apiUrl}/wishlist/remove/${productId}`);
  }

  getWishlist(): Observable<any[]> {
    return this.http.get<any[]>(`${environment.apiUrl}/wishlist`);
  }
} 