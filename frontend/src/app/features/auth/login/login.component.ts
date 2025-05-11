import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService, LoginRequest } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule]
})
export class LoginComponent {
  email: string = '';
  password: string = '';
  loading = false;
  error: string | null = null;

  constructor(
    private readonly authService: AuthService,
    private readonly router: Router
  ) {}

  onSubmit() {
    this.loading = true;
    this.error = null;

    const loginData: LoginRequest = { email: this.email, password: this.password };

    this.authService.login(loginData).subscribe({
      next: (response) => {
        console.log('Login successful, token:', response.token);
        this.router.navigate(['/']); // Ana sayfaya veya istenen başka bir sayfaya yönlendir
      },
      error: (error) => {
        console.error('Login error:', error);
        this.error = error?.error?.message || 'Invalid email or password';
        this.loading = false;
      },
      complete: () => {
        this.loading = false;
      }
    });
  }
}
