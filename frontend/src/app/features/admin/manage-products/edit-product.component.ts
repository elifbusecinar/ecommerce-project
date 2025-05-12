import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../../core/services/product.service';
import { Product } from '../../../core/models/product.model';

@Component({
  selector: 'app-edit-product',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  template: `
    <h2>Edit Product</h2>
    <form class="new-product-form" *ngIf="product" (ngSubmit)="onSubmit()" #productForm="ngForm">
      <div>
        <label>Product Name:</label>
        <input name="name" [(ngModel)]="product.name" required />
      </div>
      <div>
        <label>Price:</label>
        <input name="price" type="number" [(ngModel)]="product.price" required />
      </div>
      <div>
        <label>Stock:</label>
        <input name="stockQuantity" type="number" [(ngModel)]="product.stockQuantity" required />
      </div>
      <div>
        <label>Description:</label>
        <textarea name="description" [(ngModel)]="product.description" required></textarea>
      </div>
      <div>
        <label>Image Path (imageUrl):</label>
        <input name="imageUrl" [(ngModel)]="product.imageUrl" placeholder="assets/monitor.png" required />
      </div>
      <div *ngIf="product.imageUrl">
        <label>Image Preview:</label><br />
        <img [src]="product.imageUrl" alt="Product Image" width="150" style="border:1px solid #ccc;" />
      </div>
      <button type="submit" [disabled]="!productForm.form.valid">Save</button>
    </form>
  `
})
export class EditProductComponent implements OnInit {
  product: Product | null = null;

  constructor(
    private route: ActivatedRoute,
    private productService: ProductService,
    private router: Router
  ) {}

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.productService.getProduct(id).subscribe({
      next: (prod) => {
        if (prod.imageUrl && prod.imageUrl.includes('assets/')) {
          const idx = prod.imageUrl.indexOf('assets/');
          prod.imageUrl = prod.imageUrl.substring(idx);
        }
        this.product = prod;
      },
      error: () => alert('Failed to load product.')
    });
  }

  onSubmit() {
    if (!this.product) return;
    if (this.product.imageUrl && this.product.imageUrl.includes('assets/')) {
      const idx = this.product.imageUrl.indexOf('assets/');
      this.product.imageUrl = this.product.imageUrl.substring(idx);
    }
    const formData = new FormData();
    Object.entries(this.product).forEach(([key, value]) => {
      if (value !== undefined && value !== null) {
        formData.append(key, value as string);
      }
    });
    this.productService.updateProduct(this.product.id, formData).subscribe({
      next: () => {
        alert('Product updated successfully!');
        this.router.navigate(['/admin/products']);
      },
      error: () => alert('Failed to update product.')
    });
  }
} 