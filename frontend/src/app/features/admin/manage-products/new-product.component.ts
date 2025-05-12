import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../../core/services/product.service';
import { Product } from '../../../core/models/product.model';

@Component({
  selector: 'app-new-product',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './new-product.component.html',
  styleUrls: ['./new-product.component.scss']
})
export class NewProductComponent {
  product = {
    id: 0,
    name: '',
    price: 0,
    stockQuantity: 0,
    description: '',
    imageUrl: '',
    active: true
  };

  imageFile: File | null = null;
  imagePreview: string | null = null;

  constructor(private productService: ProductService, private router: Router) {}

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      this.imageFile = input.files[0];
      const reader = new FileReader();
      reader.onload = e => this.imagePreview = reader.result as string;
      reader.readAsDataURL(this.imageFile);
    }
  }

  onSubmit() {
    const formData = new FormData();
    Object.entries(this.product).forEach(([key, value]) => {
      if (key !== 'imageUrl' && value !== undefined && value !== null) {
        formData.append(key, value as string);
      }
    });
    if (this.imageFile) {
      formData.append('image', this.imageFile);
    }
    this.productService.addProduct(formData).subscribe({
      next: () => {
        alert('Product added successfully!');
        this.router.navigate(['/admin/products']);
      },
      error: (err: Error) => {
        console.error('Error adding product:', err);
        alert('Failed to add product. Please try again.');
      }
    });
  }
} 