// Path: frontend/src/app/features/profile/profile/profile.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../../core/services/user.service';
import { UserDTO, User } from '../../../core/models/user.model';
import { AuthService } from '../../../core/services/auth.service';

interface UserProfile { // Bu arayüz UserDTO'ya daha çok benzeyebilir
  id?: number; // Opsiyonel id
  firstName: string;
  lastName: string;
  email: string;
  phone?: string; // Opsiyonel telefon
  // Adresler ve siparişler ayrı API endpoint'lerinden veya user profile ile birlikte gelebilir
  addresses?: any[]; // Şimdilik any, spesifik Address modeliniz olmalı
  orders?: any[]; // Şimdilik any, spesifik Order modeliniz olmalı
}

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss'],
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule]
})
export class ProfileComponent implements OnInit {
  profile: User = {
    id: 0,
    username: '',
    email: '',
    firstName: '',
    lastName: '',
    active: true,
    roles: [],
    phoneNumber: '',
    addresses: [],
    orders: []
  };
  isLoading = true;
  errorMessage: string | null = null;

  activeTab: 'info' | 'addresses' | 'orders' = 'info';
  editMode = false;

  constructor(
    private userService: UserService,
    private authService: AuthService // AuthService inject edildi
  ) {}

  ngOnInit(): void {
    this.loadUserProfile();
  }

  loadUserProfile(): void {
    this.isLoading = true;
    this.errorMessage = null;
    // Mevcut kullanıcı bilgisini AuthService'den alabiliriz
    // veya direkt /users/profile endpoint'ini çağırabiliriz.
    // AuthService.currentUser$ daha önce yüklenmiş veriyi sağlayabilir.
    this.authService.currentUser$.subscribe({
      next: (user) => {
        if (user) {
          // User (entity) veya UserDTO (backend'den gelen) -> UserProfile dönüşümü
          this.profile = {
            id: user.id,
            username: user.username,
            firstName: user.firstName,
            lastName: user.lastName,
            email: user.email,
            phoneNumber: user.phoneNumber || '',
            active: user.active,
            roles: user.roles,
            addresses: [],
            orders: []
          };
          this.isLoading = false;
        } else {
          // Eğer currentUser$ null ise, /users/profile endpoint'ini çağırabiliriz
          this.userService.getCurrentUserProfile().subscribe({
            next: (userDto: UserDTO) => { // UserService'de getCurrentUserProfile metodu UserDTO dönmeli
              this.profile = {
                id: userDto.id,
                username: userDto.username,
                firstName: userDto.firstName,
                lastName: userDto.lastName,
                email: userDto.email,
                phoneNumber: userDto.phoneNumber || '',
                active: userDto.active,
                roles: userDto.roles,
                addresses: [],
                orders: []
              };
              this.isLoading = false;
            },
            error: (err) => {
              this.errorMessage = 'Failed to load user profile.';
              this.isLoading = false;
              console.error(err);
            }
          });
        }
      },
      error: (err) => {
          this.errorMessage = 'Failed to load user profile from auth service.';
          this.isLoading = false;
          console.error(err);
      }
    });
  }

  saveProfile(): void {
    if (!this.profile || !this.profile.id) {
      this.errorMessage = "User data is not available for saving.";
      return;
    }
    this.isLoading = true;
    this.errorMessage = null;

    // UserUpdateDTO gibi bir DTO oluşturup sadece güncellenecek alanları göndermek daha iyi olur.
    // Şimdilik profile objesini olduğu gibi gönderiyoruz, backend'de User entity'si bekliyor.
    const userUpdateData = {
        id: this.profile.id,
        firstName: this.profile.firstName,
        lastName: this.profile.lastName,
        email: this.profile.email,
        phoneNumber: this.profile.phoneNumber,
    };


    this.userService.updateUser(this.profile.id, userUpdateData).subscribe({ // userUpdateData UserUpdateDTO olmalı
      next: (updatedUser: UserDTO) => {
        // AuthServisindeki kullanıcıyı da güncellemek iyi bir pratik olabilir
        this.authService.updateCurrentUser(updatedUser); // AuthService'e böyle bir metod eklenebilir

        this.profile = {
            id: updatedUser.id,
            username: updatedUser.username,
            firstName: updatedUser.firstName,
            lastName: updatedUser.lastName,
            email: updatedUser.email,
            phoneNumber: updatedUser.phoneNumber || '',
            active: updatedUser.active,
            roles: updatedUser.roles,
            addresses: this.profile?.addresses || [],
            orders: this.profile?.orders || []
        };
        this.editMode = false;
        this.isLoading = false;
        // Başarı mesajı gösterilebilir (örn: snackbar)
      },
      error: (err) => {
        this.errorMessage = 'Failed to save profile.';
        this.isLoading = false;
        console.error(err);
      }
    });
  }

  // Adres ve Sipariş metodları için backend servis çağrıları eklenmeli
  addAddress(): void {
    if (this.profile) {
        // this.profile.addresses.push({ /* ... */ }); // Bu frontend'de geçici ekleme yapar.
        // Backend'e adres ekleme servisi çağrılmalı ve sonra liste güncellenmeli.
        console.log('Add address functionality to be implemented with backend.');
    }
  }

  removeAddress(index: number): void {
    if (this.profile && this.profile.addresses) {
        // const addressToRemove = this.profile.addresses[index];
        // Backend'e adres silme servisi çağrılmalı (addressToRemove.id ile)
        // Başarılı olursa: this.profile.addresses.splice(index, 1);
        console.log('Remove address functionality to be implemented with backend.');
    }
  }
}