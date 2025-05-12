import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router'; // Import Router
import { CartService } from '../../services/cart.service';
import { AuthService, User } from '../../services/auth.service'; // Import User type

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  totalItems = 0;
  isLoggedIn = false;
  userName: string | null = null;
  isAdmin = false; // New property to check if user is admin

  constructor(
    private readonly cartService: CartService,
    private readonly authService: AuthService,
    private readonly router: Router // Inject Router
  ) {}

  ngOnInit() {
    this.cartService.getCart().subscribe(items => {
      // Assuming getCart returns items and getTotalItems calculates from these or another source
      this.totalItems = this.cartService.getTotalItems();
    });

    this.authService.currentUser$.subscribe(user => {
      this.isLoggedIn = !!user;
      if (user) {
        // Assuming user object has a 'firstName' or similar property.
        // If not, adjust to use 'username' or 'email' as before.
        this.userName = user.firstName || user.email || 'User';
        this.isAdmin = this.authService.hasRole('ADMIN'); // Check if user is admin
      } else {
        this.userName = null;
        this.isAdmin = false;
      }
    });
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/auth/login']); // Redirect to login page after logout
  }
}
