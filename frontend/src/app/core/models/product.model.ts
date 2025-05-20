// frontend/src/app/core/models/product.model.ts

import { Category } from './category.model';

// Review ve Specification arayüzleri backend DTO'larına göre ayarlanmalı
// Şimdilik Product arayüzüne odaklanıyoruz.
export interface Review {
  id: number;
  productId: number;
  userId: number;
  rating: number;
  comment: string;
  createdAt: Date;
  updatedAt: Date;
  // Backend'deki Review modeline göre alanlar eklenebilir
}

export interface Specification {
  name: string;
  value: string;
  // key?: string; // Backend'de yoksa kaldırılabilir
}

export interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  stockQuantity: number;
  stock: number;
  imageUrl?: string;
  images?: string[];
  categoryId: number;
  category?: Category;
  active: boolean;
  createdAt: Date;
  updatedAt: Date;
  reviews?: Review[];
  specifications?: ProductSpecification[];
  reviewCount?: number;
  rating?: number;
}

export interface ProductSpecification {
  id: number;
  productId: number;
  name: string;
  value: string;
}

export interface PaginatedProductResponse {
  content: Product[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
