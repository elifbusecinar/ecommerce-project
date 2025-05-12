// Role arayüzü artık direkt User içinde kullanılmayacaksa kaldırılabilir veya yorumlanabilir.
// export interface Role {
//   id?: number;
//   name: string;
// }

export interface User {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  phoneNumber?: string;
  active: boolean;
  lastLogin?: string; // Backend DTO'dan formatlı string olarak gelecek
  roles: string[]; // Backend UserDTO'daki Set<String> rollerine karşılık gelir
  createdAt?: string; // Backend DTO'dan formatlı string olarak gelecek
  updatedAt?: string; // Backend DTO'dan formatlı string olarak gelecek
}
