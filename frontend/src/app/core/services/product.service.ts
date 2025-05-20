import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { Product, Review } from '../models/product.model'; // Güncellenmiş Product modelini kullan
import { Category } from '../models/category.model'; // Category modeli

// Backend'den gelen sayfalanmış yanıt için arayüz
interface PaginatedProductResponse {
  content: Product[]; // Artık Product (DTO) dizisi
  totalPages: number;
  totalElements: number;
  size: number;
  number: number; // current page number
  // Diğer sayfalama bilgileri de eklenebilir
}

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private apiUrl = `${environment.apiUrl}/products`;

  constructor(private http: HttpClient) {}

  // Ürünleri Product (DTO) olarak getiren metod
  getProducts(page: number, size: number, sort: string): Observable<PaginatedProductResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', sort); // sort formatı backend'e uygun olmalı, örn: "name,asc"
    return this.http.get<PaginatedProductResponse>(this.apiUrl, { params }).pipe(
      catchError(this.handleError)
    );
  }

  // Tek bir ürünü Product (DTO) olarak getiren metod
  getProduct(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.handleError)
    );
  }

  getCategories(): Observable<Category[]> { // Category modeli kullanılmalı
    // Backend'de /products/categories endpoint'i var mı kontrol et, yoksa oluştur.
    // Şimdilik geçici bir yol varsayıyorum, backend'e uygun olmalı.
    return this.http.get<Category[]>(`${environment.apiUrl}/categories`).pipe(
      catchError(this.handleError)
    ); // Veya `${this.apiUrl}/categories`
  }

  searchProducts(query: string, page: number, size: number): Observable<PaginatedProductResponse> {
    const params = new HttpParams()
      .set('query', query)
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PaginatedProductResponse>(`${this.apiUrl}/search`, { params }).pipe(
      catchError(this.handleError)
    );
  }

  getProductsByCategory(categoryId: number, page: number, size: number): Observable<PaginatedProductResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PaginatedProductResponse>(`${this.apiUrl}/category/${categoryId}`, { params }).pipe(
      catchError(this.handleError)
    );
  }

  getSimilarProducts(productId: number, categoryId: number, page: number, size: number): Observable<Product[]> {
    // Backend'deki endpoint /products/{id}/similar ve categoryId'yi query param olarak alıyor.
    // Veya direkt /products/similar?category=...&excludeId=... şeklinde olabilir.
    // Backend ProductController'daki tanıma göre güncellenmeli.
    // Şimdilik ProductController'daki /products/{id}/similar varsayımıyla devam ediyorum.
    const params = new HttpParams()
      .set('categoryId', categoryId.toString()) // Backend'deki @RequestParam categoryId ile eşleşmeli
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Product[]>(`${this.apiUrl}/${productId}/similar`, { params }).pipe(
      catchError(this.handleError)
    );
  }

  // --- Admin işlemleri ---
  createProduct(formData: FormData): Observable<Product> {
    return this.http.post<Product>(this.apiUrl, formData).pipe(
      catchError(this.handleError)
    );
  }

  updateProduct(id: number, formData: FormData): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/${id}`, formData).pipe(
      catchError(this.handleError)
    );
  }

  deleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.handleError)
    );
  }
  
  addReview(productId: number, review: { rating: number; comment: string }): Observable<Review> {
    return this.http.post<Review>(`${this.apiUrl}/${productId}/reviews`, review);
  }

  addProduct(product: Product | FormData): Observable<Product> {
    if (product instanceof FormData) {
      return this.http.post<Product>(this.apiUrl, product);
    }
    return this.http.post<Product>(this.apiUrl, product);
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'An error occurred';
    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = error.error.message;
    } else {
      // Server-side error
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
    }
    console.error(errorMessage);
    return throwError(() => new Error(errorMessage));
  }
}
