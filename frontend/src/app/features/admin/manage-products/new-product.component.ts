import { Component, OnInit } from '@angular/core'; // OnInit eklendi
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule, NgForm } from '@angular/forms'; // NgForm eklendi
import { ProductService } from '../../../core/services/product.service';
import { CategoryService } from '../../../core/services/category.service'; // CategoryService eklendi
import { Product } from '../../../core/models/product.model';
import { Category } from '../../../core/models/category.model'; // Category modeli eklendi
import { HttpErrorResponse } from '@angular/common/http';

interface ProductFormData {
  id?: number | null; // id opsiyonel ve null olabilir
  name: string;
  price: number | null;
  stockQuantity: number | null;
  description: string;
  active: boolean;
  categoryId: number | null; // Kategori ID'si eklendi
}

@Component({
  selector: 'app-new-product',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  providers: [ProductService, CategoryService],
  templateUrl: './new-product.component.html',
  styleUrls: ['./new-product.component.scss'] // new-product.component.scss olarak güncellendi
})
export class NewProductComponent implements OnInit { // OnInit implement edildi
  product: ProductFormData = { // Interface kullanıldı
    id: null,
    name: '',
    price: null,
    stockQuantity: null,
    description: '',
    active: true,
    categoryId: null // Kategori ID'si başlangıç değeri
  };

  categories: Category[] = []; // Kategorileri tutmak için
  imageFile: File | null = null;
  imagePreview: string | ArrayBuffer | null = null; // ArrayBuffer eklendi
  errorMessage: string | null = null;
  isLoading = false;

  constructor(
    private productService: ProductService,
    private categoryService: CategoryService, // CategoryService inject edildi
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories(): void {
    this.categoryService.getAllCategories().subscribe({
      next: (data: Category[]) => {
        this.categories = data;
      },
      error: (err: HttpErrorResponse) => {
        console.error('Error loading categories:', err);
        this.errorMessage = 'Failed to load categories. Please try again.';
      }
    });
  }

  onFileSelected(event: Event): void {
    const element = event.currentTarget as HTMLInputElement;
    let fileList: FileList | null = element.files;
    if (fileList && fileList[0]) {
      this.imageFile = fileList[0];
      const reader = new FileReader();
      reader.onload = e => this.imagePreview = reader.result;
      reader.readAsDataURL(this.imageFile);
    } else {
      this.imageFile = null;
      this.imagePreview = null;
    }
  }

  onSubmit(productForm: NgForm): void { // NgForm parametre olarak alındı
    this.errorMessage = null;
    if (productForm.invalid) {
      this.errorMessage = "Please correct the errors in the form.";
      Object.values(productForm.controls).forEach(control => {
        control.markAsTouched();
      });
      return;
    }
    if (this.isLoading) return;

    this.isLoading = true;
    const formData = new FormData();

    // ProductCreateDTO veya ProductUpdateDTO'ya uygun alanları ekleyin
    formData.append('name', this.product.name);
    if (this.product.price !== null) {
      formData.append('price', this.product.price.toString());
    }
    if (this.product.stockQuantity !== null) {
      formData.append('stockQuantity', this.product.stockQuantity.toString());
    }
    if (this.product.description) {
      formData.append('description', this.product.description);
    }
    formData.append('active', String(this.product.active));
    if (this.product.categoryId !== null) {
      formData.append('categoryId', this.product.categoryId.toString());
    }

    if (this.imageFile) {
      formData.append('imageFile', this.imageFile, this.imageFile.name);
    }

    // Backend'deki ProductController.createProduct, ProductCreateDTO bekliyor.
    // FormData yerine direkt DTO göndermek daha type-safe olabilir,
    // ancak dosya yükleme için FormData genellikle daha yaygındır.
    // ProductService.addProduct metodunu buna göre düzenlemeniz gerekebilir.
    // Şimdilik ProductService.addProduct'ın FormData kabul ettiğini varsayıyoruz.

    this.productService.addProduct(formData).subscribe({
      next: () => {
        this.isLoading = false;
        alert('Product added successfully!'); // Daha iyi bir kullanıcı deneyimi için toast/snackbar kullanın
        this.router.navigate(['/admin/products']);
      },
      error: (err: HttpErrorResponse) => {
        this.isLoading = false;
        console.error('Error adding product:', err);
        if (err.error && typeof err.error === 'string') {
            this.errorMessage = err.error;
        } else if (err.error && err.error.message) {
            this.errorMessage = err.error.message;
        } else if (err.message) {
            this.errorMessage = err.message;
        } else {
            this.errorMessage = 'Failed to add product. Please check the console for more details.';
        }
        // alert('Failed to add product. Please try again.');
      }
    });
  }
}