import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Product } from '../models/product.model';

interface PaginatedResponse {
  content: Product[];
  totalPages: number;
}

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private apiUrl = `${environment.apiUrl}/products`;

  constructor(private http: HttpClient) {}

  getProducts(page: number, size: number, sort: string): Observable<PaginatedResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', sort);
    return this.http.get<PaginatedResponse>(this.apiUrl, { params });
  }

  getProduct(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/${id}`);
  }

  getCategories(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/categories`);
  }

  searchProducts(query: string, page: number, size: number): Observable<PaginatedResponse> {
    const params = new HttpParams()
      .set('query', query)
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PaginatedResponse>(`${this.apiUrl}/search`, { params });
  }

  getProductsByCategory(categoryId: string, page: number, size: number): Observable<PaginatedResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PaginatedResponse>(`${this.apiUrl}/category/${categoryId}`, { params });
  }

  getSimilarProducts(category: string, currentProductId: number): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/similar`, {
      params: {
        category,
        excludeId: currentProductId.toString()
      }
    });
  }

  addReview(productId: number, review: { rating: number; comment: string }): Observable<any> {
    return this.http.post(`${this.apiUrl}/${productId}/reviews`, review);
  }
} 