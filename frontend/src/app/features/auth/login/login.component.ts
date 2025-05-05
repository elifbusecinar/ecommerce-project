import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule]
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

    this.authService.login({ email: this.email, password: this.password }).subscribe({
      next: () => {
        this.router.navigate(['/']);
      },
      error: (error) => {
        this.error = 'An error occurred during login. Please try again.';
        this.loading = false;
      }
    });
  }
}
