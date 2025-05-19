import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router'; // Import Router
import { CartService } from '../../services/cart.service';
import { AuthService } from '../../services/auth.service';
import { User } from '../../models/user.model';
import { Subscription } from 'rxjs'; // Import Subscription for cleanup

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
  isAdmin = false;

  private cartSubscription: Subscription | undefined;
  private userSubscription: Subscription | undefined;

  constructor(
    private readonly cartService: CartService,
    private readonly authService: AuthService,
    private readonly router: Router
  ) {}

  ngOnInit() {
    // Subscribe to cart items to update totalItems
    this.cartSubscription = this.cartService.getCart().subscribe(items => {
      // Calculate total items based on the quantity of each item in the cart
      this.totalItems = items.reduce((acc, item) => acc + item.quantity, 0);
    });

    // Subscribe to current user changes
    this.userSubscription = this.authService.currentUser$.subscribe(user => {
      console.log('Current user in header:', user);
      this.isLoggedIn = !!user;
      if (user) {
        // Use firstName if available, otherwise fallback to email or a generic 'User'
        this.userName = user.firstName || user.email || 'User';
        this.isAdmin = this.authService.hasRole('ADMIN'); // Check if user is admin
        console.log('Is Admin in header:', this.isAdmin);
        console.log('User roles:', user.roles);
      } else {
        this.userName = null;
        this.isAdmin = false;
      }
    });
  }

  ngOnDestroy() {
    // Unsubscribe to prevent memory leaks
    if (this.cartSubscription) {
      this.cartSubscription.unsubscribe();
    }
    if (this.userSubscription) {
      this.userSubscription.unsubscribe();
    }
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/auth/login']); // Redirect to login page after logout
  }
}
