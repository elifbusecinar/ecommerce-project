import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Order, OrderStatus, PaymentStatus } from '../models/order.model';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = `${environment.apiUrl}/orders`;

  constructor(private http: HttpClient) {}

  getOrderById(orderId: number): Observable<Order> {
    return this.http.get<Order>(`${this.apiUrl}/${orderId}`);
  }

  getOrderByTrackingNumber(trackingNumber: string): Observable<Order> {
    return this.http.get<Order>(`${this.apiUrl}/tracking/${trackingNumber}`);
  }

  getUserOrders(userId: number): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.apiUrl}/user/${userId}`);
  }

  createOrder(orderData: Partial<Order>): Observable<Order> {
    return this.http.post<Order>(this.apiUrl, orderData);
  }

  updateOrderStatus(orderId: number, status: OrderStatus): Observable<Order> {
    return this.http.put<Order>(`${this.apiUrl}/${orderId}/status`, { status });
  }

  updatePaymentStatus(orderId: number, paymentStatus: PaymentStatus): Observable<Order> {
    return this.http.put<Order>(`${this.apiUrl}/${orderId}/payment-status`, { paymentStatus });
  }

  cancelOrder(orderId: number): Observable<Order> {
    return this.updateOrderStatus(orderId, OrderStatus.CANCELLED);
  }
} 