import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
  standalone: true,
  imports: [CommonModule, RouterModule]
})
export class HomeComponent {
  featuredProducts = [
    {
      id: 1,
      name: 'Featured Product 1',
      price: 99.99,
      image: 'assets/images/product1.jpg',
      description: 'This is a featured product'
    },
    // Add more featured products here
  ];

  categories = [
    {
      id: 1,
      name: 'Electronics',
      image: 'assets/images/electronics.jpg'
    },
    {
      id: 2,
      name: 'Clothing',
      image: 'assets/images/clothing.jpg'
    },
    // Add more categories here
  ];
}
