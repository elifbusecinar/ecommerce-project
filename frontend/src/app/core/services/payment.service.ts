import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface PaymentRequest {
  amount: number;
  cardNumber: string;
  expiryDate: string;
  cvv: string;
}

export interface PaymentResponse {
  success: boolean;
  error?: string;
  transactionId?: string;
}

@Injectable({
  providedIn: 'root'
})
export class PaymentService {
  private apiUrl = `${environment.apiUrl}/payments`;

  constructor(private http: HttpClient) {}

  processPayment(request: PaymentRequest): Observable<PaymentResponse> {
    return this.http.post<PaymentResponse>(`${this.apiUrl}/process`, request);
  }
} 