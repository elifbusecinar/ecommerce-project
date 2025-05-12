import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

interface Order {
  id: number;
  customerName: string;
  orderDate: Date;
  status: string;
  total: number;
  paymentStatus: string;
}

@Component({
  selector: 'app-order-management',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './order-management.component.html',
  styleUrls: ['./order-management.component.scss']
})
export class OrderManagementComponent implements OnInit {
  orders: Order[] = [
    {
      id: 1,
      customerName: 'John Doe',
      orderDate: new Date(),
      status: 'Pending',
      total: 299.99,
      paymentStatus: 'Paid'
    },
    // Add more sample orders as needed
  ];

  constructor() { }

  ngOnInit(): void {
    // Fetch orders from service
  }
} 