export interface User {
  id: number;
  email: string;
  name: string;
  role: 'user' | 'admin';
  createdAt: Date;
  updatedAt?: Date;
} 