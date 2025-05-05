import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-checkout-success',
  templateUrl: './checkout-success.component.html',
  styleUrls: ['./checkout-success.component.scss'],
  standalone: true,
  imports: [CommonModule, RouterModule]
})
export class CheckoutSuccessComponent implements OnInit {
  orderNumber: string;
  today = new Date();

  constructor() {
    // Generate a random order number
    this.orderNumber = 'ORD-' + Math.random().toString(36).substr(2, 9).toUpperCase();
  }

  ngOnInit(): void {
    // Scroll to top of the page
    window.scrollTo(0, 0);
  }
} 