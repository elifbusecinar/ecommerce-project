import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CartService } from '../../services/cart.service';
import { AuthService } from '../../services/auth.service';
import { BehaviorSubject } from 'rxjs';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
  standalone: true,
  imports: [CommonModule, RouterModule]
})
export class HeaderComponent implements OnInit {
  totalItems = 0;
  isLoggedIn = false;
  userName: string | null = null;

  constructor(
    private readonly cartService: CartService,
    private readonly authService: AuthService
  ) {}

  ngOnInit() {
    this.cartService.getCart().subscribe(items => {
      this.totalItems = this.cartService.getTotalItems();
    });

    this.authService.currentUser$.subscribe(user => {
      this.isLoggedIn = !!user;
      this.userName = user?.email || null;
    });
  }

  logout() {
    this.authService.logout();
  }
} 