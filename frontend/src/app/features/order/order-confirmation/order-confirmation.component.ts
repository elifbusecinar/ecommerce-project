import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { OrderService } from '../../../core/services/order.service';
import { Order } from '../../../core/models/order.model';

@Component({
  selector: 'app-order-confirmation',
  templateUrl: './order-confirmation.component.html',
  styleUrls: ['./order-confirmation.component.scss'],
  standalone: true,
  imports: [CommonModule, RouterModule]
})
export class OrderConfirmationComponent implements OnInit {
  orderId: string | null = null;
  orderDetails: Order | null = null;
  loading = true;
  error: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private orderService: OrderService
  ) {}

  ngOnInit() {
    this.orderId = this.route.snapshot.queryParamMap.get('orderId');
    if (this.orderId) {
      this.loadOrderDetails();
    } else {
      this.error = 'No order ID provided';
      this.loading = false;
    }
  }

  private loadOrderDetails() {
    if (this.orderId) {
      this.orderService.getOrderById(Number(this.orderId)).subscribe({
        next: (order: Order) => {
          this.orderDetails = order;
          this.loading = false;
        },
        error: (error: any) => {
          this.error = 'Failed to load order details';
          this.loading = false;
        }
      });
    }
  }

  cancelOrder() {
    if (this.orderDetails && this.orderDetails.id) {
      this.loading = true;
      this.orderService.cancelOrder(this.orderDetails.id).subscribe({
        next: (order: Order) => {
          this.orderDetails = order;
          this.loading = false;
        },
        error: (error: any) => {
          this.error = 'Failed to cancel order';
          this.loading = false;
        }
      });
    }
  }
} 