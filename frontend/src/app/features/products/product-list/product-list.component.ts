import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../../core/services/product.service';
import { CartService } from '../../../core/services/cart.service';
import { Product } from '../../../core/models/product.model';
import { Category } from '../../../core/models/category.model';

interface PaginatedResponse {
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
  categories: Category[] = [];
  searchQuery = '';
  selectedCategory = '';
  sortOption = 'name';
  currentPage = 1;
  totalPages = 1;
  pageSize = 12;

  constructor(
    private productService: ProductService,
    private cartService: CartService
  ) {}

  ngOnInit() {
    this.loadProducts();
    this.loadCategories();
  }

  loadProducts() {
    this.productService.getProducts(this.currentPage, this.pageSize, this.sortOption)
      .subscribe((response: PaginatedResponse) => {
        this.products = response.content;
        this.totalPages = response.totalPages;
      });
  }

  loadCategories() {
    this.productService.getCategories().subscribe((categories: Category[]) => {
      this.categories = categories;
    });
  }

  search() {
    if (this.searchQuery.trim()) {
      this.productService.searchProducts(this.searchQuery, this.currentPage, this.pageSize)
        .subscribe((response: PaginatedResponse) => {
          this.products = response.content;
          this.totalPages = response.totalPages;
        });
    } else {
      this.loadProducts();
    }
  }

  filterByCategory() {
    if (this.selectedCategory) {
      this.productService.getProductsByCategory(this.selectedCategory, this.currentPage, this.pageSize)
        .subscribe((response: PaginatedResponse) => {
          this.products = response.content;
          this.totalPages = response.totalPages;
        });
    } else {
      this.loadProducts();
    }
  }

  sort() {
    this.loadProducts();
  }

  changePage(page: number) {
    this.currentPage = page;
    this.loadProducts();
  }

  addToCart(product: Product) {
    this.cartService.addToCart(product, 1).subscribe({
      next: () => {
        console.log('Product added to cart:', product.name);
      },
      error: (error) => {
        console.error('Error adding product to cart:', error);
      }
    });
  }
}
