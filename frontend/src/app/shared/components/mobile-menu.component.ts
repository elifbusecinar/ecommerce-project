import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

interface MenuItem {
  label: string;
  route: string;
  icon: string;
}

@Component({
  selector: 'app-mobile-menu',
  templateUrl: './mobile-menu.component.html',
  styleUrls: ['./mobile-menu.component.scss'],
  standalone: true,
  imports: [CommonModule, RouterModule]
})
export class MobileMenuComponent {
  @Input() isOpen = false;
  @Output() closeMenu = new EventEmitter<void>();

  menuItems: MenuItem[] = [
    { label: 'Home', route: '/home', icon: 'home' },
    { label: 'Products', route: '/products', icon: 'shopping_bag' },
    { label: 'Cart', route: '/cart', icon: 'shopping_cart' },
    { label: 'Orders', route: '/orders', icon: 'receipt' },
    { label: 'Profile', route: '/profile', icon: 'person' }
  ];

  close(): void {
    this.closeMenu.emit();
  }
}
