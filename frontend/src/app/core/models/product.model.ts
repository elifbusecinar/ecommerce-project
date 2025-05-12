// frontend/src/app/core/models/product.model.ts

// Review ve Specification arayüzleri backend DTO'larına göre ayarlanmalı
// Şimdilik Product arayüzüne odaklanıyoruz.
export interface Review {
  id?: number; // Backend'den geliyorsa
  rating: number;
  comment: string;
  user?: string; // veya userName
  date?: string | Date; // Backend'den formatlı string gelebilir
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
  description?: string;
  price: number;
  stock?: number;
  stockQuantity?: number;
  imageUrl?: string;
  images?: string[];
  categoryName?: string;
  categoryId?: number;
  category?: string;
  rating?: number;
  active: boolean;
  reviews?: Review[];
  reviewCount?: number;
  specifications?: Specification[];
  createdAt?: string;
  updatedAt?: string;
}
