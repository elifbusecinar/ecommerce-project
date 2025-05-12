import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ProductService } from '../../../core/services/product.service';
import { Product } from '../../../core/models/product.model';

interface PaginatedProductResponse {
  content: Product[];
  totalPages: number;
  totalElements: number;
}

@Component({
  selector: 'app-manage-products',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './manage-products.component.html',
  styleUrls: ['./manage-products.component.scss']
})
export class ManageProductsComponent implements OnInit {
  products: Product[] = [];
  isLoading = false;
  errorMessage: string | null = null;

  // Sayfalama için değişkenler eklenebilir
  currentPage = 0; // Backend genellikle 0'dan başlar
  pageSize = 10;
  totalPages = 0;
  totalElements = 0;

  constructor(private productService: ProductService) { }

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.isLoading = true;
    this.errorMessage = null;
    this.productService.getProducts(this.currentPage, this.pageSize, 'id,asc').subscribe({
      next: (response: PaginatedProductResponse) => {
        this.products = response.content;
        this.totalPages = response.totalPages;
        this.totalElements = response.totalElements;
        this.isLoading = false;
      },
      error: (err: any) => {
        this.errorMessage = 'Failed to load products. Please try again later.';
        console.error('Error loading products:', err);
        this.isLoading = false;
      }
    });
  }

  // Ürün silme (pasif yapma)
  deleteProduct(productId: number): void {
    if (confirm('Are you sure you want to deactivate this product?')) {
      this.productService.deleteProduct(productId).subscribe({
        next: () => {
          this.products = this.products.filter(p => p.id !== productId);
        },
        error: (err: any) => {
          this.errorMessage = 'Failed to deactivate product.';
          console.error('Error deactivating product:', err);
        }
      });
    }
  }

  // Sayfa değiştirme metodu (eğer sayfalama eklenecekse)
  changePage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadProducts();
    }
  }
}
