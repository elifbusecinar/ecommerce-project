export interface Category {
  id: number;
  name: string;
  description?: string;
  products?: any[]; // Optional products array
  image?: string; // Kategori resmi
} 