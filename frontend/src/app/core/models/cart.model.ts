import { CartItem } from './cart-item.model';

export interface Cart {
  id: number;
  userId?: number;
  guestCartId?: string;
  items: CartItem[];
  totalPrice: number;
  active: boolean;
  createdAt: string;
  updatedAt: string;
} 