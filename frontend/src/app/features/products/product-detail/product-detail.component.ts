import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../../core/services/product.service';
import { Product } from '../../../core/models/product.model';
import { CartService } from '../../../core/services/cart.service';
import { catchError, finalize } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-product-detail',
  templateUrl: './product-detail.component.html',
  styleUrls: ['./product-detail.component.scss'],
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule]
})
export class ProductDetailComponent implements OnInit {
  product: Product | null = null;
  loading = true;
  error: string | null = null;
  selectedImageIndex = 0;
  quantity = 1;
  newReview = {
    rating: 5,
    comment: ''
  };
  similarProducts: Product[] = [];
  submittingReview = false;

  constructor(
    private route: ActivatedRoute,
    private productService: ProductService,
    private cartService: CartService
  ) {}

  ngOnInit() {
    this.route.params.subscribe(params => {
      const productId = +params['id'];
      this.loadProduct(productId);
    });
  }

  private loadProduct(productId: number) {
    this.loading = true;
    this.error = null;

    this.productService.getProduct(productId)
      .pipe(
        catchError(error => {
          this.error = 'Failed to load product. Please try again later.';
          return of(null);
        }),
        finalize(() => this.loading = false)
      )
      .subscribe(product => {
        if (product) {
          this.product = product;
          this.loadSimilarProducts(product.category, product.id);
        }
      });
  }

  private loadSimilarProducts(category: string, currentProductId: number) {
    this.productService.getSimilarProducts(category, currentProductId)
      .subscribe(products => {
        this.similarProducts = products;
      });
  }

  selectImage(index: number) {
    this.selectedImageIndex = index;
  }

  addToCart() {
    if (this.product) {
      this.cartService.addToCart(this.product, this.quantity).subscribe({
        next: () => {
          console.log(`${this.quantity} adet ${this.product?.name} sepete eklendi`);
          // TODO: Kullanıcıya başarılı mesajı göster
        },
        error: (error) => {
          console.error('Sepete ekleme hatası:', error);
          // TODO: Kullanıcıya hata mesajı göster
        }
      });
    }
  }

  submitReview() {
    if (!this.product || !this.newReview.comment.trim()) return;

    this.submittingReview = true;
    this.productService.addReview(this.product.id, this.newReview)
      .pipe(
        catchError(error => {
          console.error('Failed to submit review:', error);
          return of(null);
        }),
        finalize(() => this.submittingReview = false)
      )
      .subscribe(response => {
        if (response && this.product) {
          // Refresh product data to show new review
          this.loadProduct(this.product.id);
          this.newReview.comment = '';
          this.newReview.rating = 5;
        }
      });
  }

  getRatingStars(rating: number): number[] {
    return Array(5).fill(0).map((_, index) => Math.floor(rating) > index ? 1 : 0);
  }

  reloadProduct() {
    if (this.product) {
      this.loadProduct(this.product.id);
    }
  }

  decreaseQuantity() {
    if (this.quantity > 1) {
      this.quantity--;
    }
  }

  increaseQuantity() {
    if (this.product && this.quantity < this.product.stock) {
      this.quantity++;
    }
  }
}
