import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../../core/services/product.service';
import { CartService } from '../../../core/services/cart.service';
import { Product } from '../../../core/models/product.model';
import { Category } from '../../../core/models/category.model';
import { CategoryService } from '../../../core/services/category.service'; // CategoryService import edildi

interface PaginatedResponse { // Bu interface ProductService'e taşınabilir veya ortak bir yere.
  content: Product[];
  totalPages: number;
}

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.scss']
})
export class ProductListComponent implements OnInit {
  products: Product[] = [];
  categories: Category[] = []; // Category tipini kullanıyoruz
  searchQuery = '';
  selectedCategory: number | null = null; // Kategori ID'si number olacak, "All" için null
  sortOption = 'name'; // Backend'in kabul ettiği sort parametrelerine göre güncellenmeli
  currentPage = 1; // Backend genellikle 0-indexed sayfa bekler, frontend 1-indexed olabilir. API'ye göre ayarlanmalı.
  totalPages = 1;
  pageSize = 12; // Backend'in varsayılanı veya kabul ettiği değerlerle uyumlu olmalı

  constructor(
    private productService: ProductService,
    private cartService: CartService,
    private categoryService: CategoryService // CategoryService inject edildi
  ) {}

  ngOnInit() {
    this.loadProducts();
    this.loadCategories();
  }

  loadProducts() {
    // Backend'in sayfa numaralandırması 0'dan başlıyorsa currentPage-1 gönderilmeli.
    // Şimdilik 1'den başladığını varsayıyorum, backend'e göre düzeltin.
    // Örneğin, Spring Pageable için genellikle page 0-indexed olur.
    const pageToFetch = this.currentPage > 0 ? this.currentPage - 1 : 0;

    if (this.selectedCategory) {
      this.productService.getProductsByCategory(this.selectedCategory, pageToFetch, this.pageSize)
        .subscribe((response: PaginatedResponse) => {
          this.products = response.content;
          this.totalPages = response.totalPages;
        });
    } else if (this.searchQuery.trim()) {
      this.productService.searchProducts(this.searchQuery.trim(), pageToFetch, this.pageSize)
        .subscribe((response: PaginatedResponse) => {
          this.products = response.content;
          this.totalPages = response.totalPages;
        });
    }
     else {
      this.productService.getProducts(pageToFetch, this.pageSize, this.sortOption)
        .subscribe((response: PaginatedResponse) => {
          this.products = response.content;
          this.totalPages = response.totalPages;
        });
    }
  }

  loadCategories() {
    this.categoryService.getCategories().subscribe((categories: Category[]) => { // CategoryService kullanılıyor
      this.categories = categories;
    });
  }

  search() {
    this.currentPage = 1; // Arama yapıldığında ilk sayfaya dön
    this.loadProducts();
  }

  filterByCategory() {
    this.currentPage = 1; // Filtre değiştiğinde ilk sayfaya dön
    // selectedCategory değeri HTML <select> elementinden string olarak gelebilir.
    // Eğer `null` değilse number'a çevirmemiz gerekebilir.
    // Ancak <option [value]="category.id"> kullandığımız için zaten number gelmeli.
    this.loadProducts();
  }

  sort() {
    this.currentPage = 1; // Sıralama değiştiğinde ilk sayfaya dön
    this.loadProducts();
  }

  changePage(page: number) {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      this.loadProducts();
    }
  }

  addToCart(product: Product) {
    this.cartService.addToCart(product, 1).subscribe({
      next: () => {
        // Kullanıcıya geri bildirim, örn: toast message
        console.log('Product added to cart:', product.name);
      },
      error: (error) => {
        console.error('Error adding product to cart:', error);
        // Kullanıcıya hata mesajı
      }
    });
  }

  // HTML'de sayfa numaraları için bir yardımcı fonksiyon (opsiyonel)
  getPagesArray(): number[] {
    return new Array(this.totalPages).fill(0).map((_, index) => index + 1);
  }
}
