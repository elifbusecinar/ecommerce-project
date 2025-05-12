import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Category } from '../models/category.model'; // Category modelini import ediyoruz

@Injectable({
  providedIn: 'root'
})
export class CategoryService {
  private apiUrl = `${environment.apiUrl}/categories`; // Backend API URL'si (application.properties'e göre /api/categories olacak)

  constructor(private http: HttpClient) {}

  /**
   * Tüm kategorileri getirir.
   * @returns Kategorilerin Observable listesi.
   */
  getCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(this.apiUrl);
  }

  /**
   * Belirli bir ID'ye sahip kategoriyi getirir.
   * @param id Getirilecek kategorinin ID'si.
   * @returns Kategorinin Observable'ı.
   */
  getCategoryById(id: number): Observable<Category> {
    return this.http.get<Category>(`${this.apiUrl}/${id}`);
  }

  /**
   * Yeni bir kategori oluşturur (Sadece Admin).
   * @param category Oluşturulacak kategori nesnesi.
   * @returns Oluşturulan kategorinin Observable'ı.
   */
  createCategory(category: Omit<Category, 'id'>): Observable<Category> {
    // Omit<Category, 'id'> kullanarak id'siz bir tip oluşturabiliriz,
    // çünkü id backend'de otomatik atanacak.
    // Alternatif olarak backend'e gönderilecek bir CategoryCreateDTO da tanımlanabilir.
    return this.http.post<Category>(this.apiUrl, category);
  }

  /**
   * Mevcut bir kategoriyi günceller (Sadece Admin).
   * @param id Güncellenecek kategorinin ID'si.
   * @param category Güncel kategori detayları.
   * @returns Güncellenen kategorinin Observable'ı.
   */
  updateCategory(id: number, category: Category): Observable<Category> {
    return this.http.put<Category>(`${this.apiUrl}/${id}`, category);
  }

  /**
   * Bir kategoriyi siler (Sadece Admin).
   * @param id Silinecek kategorinin ID'si.
   * @returns Observable<void> (işlem başarılıysa boş döner).
   */
  deleteCategory(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
