export interface Review {
  id: number;
  rating: number;
  comment: string;
  user: string;
  date: Date;
  userName: string;
  createdAt: Date;
}

export interface Specification {
  name: string;
  value: string;
  key: string;
}

export interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  images: string[];
  imageUrl?: string;
  stock: number;
  category: string;
  rating: number;
  reviewCount: number;
  specifications: Specification[];
  reviews: Review[];
  createdAt: Date;
  updatedAt?: Date;
} 