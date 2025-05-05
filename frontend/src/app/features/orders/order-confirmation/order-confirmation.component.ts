import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-order-confirmation',
  templateUrl: './order-confirmation.component.html',
  styleUrls: ['./order-confirmation.component.scss'],
  standalone: true,
  imports: [CommonModule, RouterModule]
})
export class OrderConfirmationComponent implements OnInit {
  orderNumber = Math.floor(100000 + Math.random() * 900000); // Simulate order number
  orderDate = new Date();
  estimatedDelivery = new Date(Date.now() + 7 * 24 * 60 * 60 * 1000); // 7 days from now

  constructor() {}

  ngOnInit() {
    // In a real app, we would get the order details from a service
  }
} 