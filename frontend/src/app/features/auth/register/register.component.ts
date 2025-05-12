import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  registerForm: FormGroup;
  isLoading = false;
  errorMessage = '';
  showPassword = false;
  showConfirmPassword = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      username: ['', Validators.required],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required]
    }, {
      validators: this.passwordMatchValidator
    });
  }

  passwordMatchValidator(form: FormGroup) {
    const password = form.get('password');
    const confirmPassword = form.get('confirmPassword');

    if (password && confirmPassword && password.value !== confirmPassword.value) {
      confirmPassword.setErrors({ passwordMismatch: true });
    } else if (confirmPassword && confirmPassword.hasError('passwordMismatch')) {
      confirmPassword.setErrors(null);
    }
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.registerForm.get(fieldName);
    return field ? field.invalid && (field.dirty || field.touched) : false;
  }

  onSubmit() {
    if (this.registerForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';

      const { confirmPassword, ...userData } = this.registerForm.value;

      this.authService.register(userData).subscribe({
        next: (registeredUser) => {
          this.router.navigate(['/login']); // Başarılı kayıt sonrası login sayfasına yönlendir
        },
        error: (errorResponse) => {
                this.isLoading = false;
                // Backend'den gelen hata mesajını doğru şekilde al
                if (errorResponse.error && typeof errorResponse.error === 'string') {
                  // Eğer backend doğrudan string bir hata mesajı yolluyorsa
                  this.errorMessage = errorResponse.error;
                } else if (errorResponse.error && typeof errorResponse.error === 'object' && errorResponse.error.message) {
                  // Eğer backend { message: "hata" } gibi bir obje yolluyorsa
                  this.errorMessage = errorResponse.error.message;
                } else if (errorResponse.message) {
                  // Genel bir hata mesajı varsa
                  this.errorMessage = errorResponse.message;
                } else {
                  // Hiçbiri yoksa varsayılan bir mesaj
                  this.errorMessage = 'An unexpected error occurred during registration.';
                }
                console.error('Registration Error Full Response:', errorResponse); // Hatanın tamamını konsola yazdır
              },
              complete: () => {
                this.isLoading = false;
              }
            });
          } else {
            this.registerForm.markAllAsTouched();
          }
      }
}
