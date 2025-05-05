import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

interface OrderStatus {
  status: 'PENDING' | 'PROCESSING' | 'SHIPPED' | 'DELIVERED';
  date: Date;
  description: string;
  location?: string;
}

interface Order {
  id: number;
  status: OrderStatus;
  items: {
    productId: number;
    name: string;
    quantity: number;
    price: number;
  }[];
  total: number;
  shippingAddress: string;
  trackingNumber?: string;
}

@Component({
  selector: 'app-order-tracking',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="container mx-auto p-4">
      <h2 class="text-2xl font-bold mb-4">Track Your Order</h2>
      
      <div class="bg-white rounded-lg shadow-lg p-6">
        <div class="flex items-center justify-between mb-6">
          <div>
            <h3 class="text-lg font-semibold">Order #{{order?.id}}</h3>
            <p class="text-gray-600">Tracking Number: {{order?.trackingNumber}}</p>
          </div>
          <div class="text-right">
            <p class="text-lg font-bold">{{order?.total | currency}}</p>
            <p class="text-gray-600">{{order?.status?.date | date}}</p>
          </div>
        </div>

        <!-- Progress Timeline -->
        <div class="relative">
          <div class="h-1 bg-gray-200 absolute w-full top-1/2 -translate-y-1/2"></div>
          <div class="relative flex justify-between">
            <div *ngFor="let step of trackingSteps" 
                 class="flex flex-col items-center">
              <div [class]="'w-8 h-8 rounded-full flex items-center justify-center ' + 
                           (isStepCompleted(step) ? 'bg-green-500 text-white' : 'bg-gray-200')">
                <i class="fas fa-check"></i>
              </div>
              <p class="mt-2 text-sm font-medium">{{step}}</p>
            </div>
          </div>
        </div>

        <!-- Shipping Address -->
        <div class="mt-8">
          <h4 class="font-semibold mb-2">Shipping Address</h4>
          <p class="text-gray-600">{{order?.shippingAddress}}</p>
        </div>

        <!-- Order Items -->
        <div class="mt-8">
          <h4 class="font-semibold mb-4">Order Items</h4>
          <div *ngFor="let item of order?.items" 
               class="flex justify-between items-center py-2 border-b">
            <div>
              <p class="font-medium">{{item.name}}</p>
              <p class="text-gray-600">Qty: {{item.quantity}}</p>
            </div>
            <p class="font-medium">{{item.price * item.quantity | currency}}</p>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class OrderTrackingComponent implements OnInit {
  order: Order | null = null;
  trackingSteps = ['PENDING', 'PROCESSING', 'SHIPPED', 'DELIVERED'];

  constructor(private route: ActivatedRoute) {}

  ngOnInit() {
    // In a real app, you would fetch the order details from a service
    this.route.params.subscribe(params => {
      // Fetch order details using params.id
    });
  }

  isStepCompleted(step: string): boolean {
    if (!this.order?.status) return false;
    const currentIndex = this.trackingSteps.indexOf(this.order.status.status);
    const stepIndex = this.trackingSteps.indexOf(step);
    return stepIndex <= currentIndex;
  }
}
