import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

interface SalesData {
  date: string;
  revenue: number;
  orders: number;
}

interface ProductPerformance {
  name: string;
  sales: number;
  revenue: number;
  rating: number;
}

@Component({
  selector: 'app-analytics',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="container mx-auto p-4">
      <h2 class="text-2xl font-bold mb-4">Analytics Dashboard</h2>
      
      <!-- Summary Cards -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
        <div class="bg-white rounded-lg shadow p-4">
          <h3 class="text-lg font-semibold text-gray-600">Total Revenue</h3>
          <p class="text-2xl font-bold">$45,678</p>
          <p class="text-sm text-green-600">+12.5% from last month</p>
        </div>
        <div class="bg-white rounded-lg shadow p-4">
          <h3 class="text-lg font-semibold text-gray-600">Orders</h3>
          <p class="text-2xl font-bold">1,234</p>
          <p class="text-sm text-green-600">+8.3% from last month</p>
        </div>
        <div class="bg-white rounded-lg shadow p-4">
          <h3 class="text-lg font-semibold text-gray-600">Average Order Value</h3>
          <p class="text-2xl font-bold">$37.50</p>
          <p class="text-sm text-red-600">-2.1% from last month</p>
        </div>
        <div class="bg-white rounded-lg shadow p-4">
          <h3 class="text-lg font-semibold text-gray-600">Active Customers</h3>
          <p class="text-2xl font-bold">892</p>
          <p class="text-sm text-green-600">+15.7% from last month</p>
        </div>
      </div>

      <!-- Sales Chart -->
      <div class="bg-white rounded-lg shadow p-4 mb-6">
        <h3 class="text-lg font-semibold mb-4">Sales Overview</h3>
        <div class="h-64 bg-gray-100 rounded">
          <!-- Chart will be rendered here -->
          <p class="text-center py-24 text-gray-500">Chart Placeholder</p>
        </div>
      </div>

      <!-- Top Products -->
      <div class="bg-white rounded-lg shadow overflow-hidden">
        <div class="p-4 border-b">
          <h3 class="text-lg font-semibold">Top Performing Products</h3>
        </div>
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Product
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Sales
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Revenue
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Rating
              </th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr *ngFor="let product of topProducts">
              <td class="px-6 py-4 whitespace-nowrap">
                {{product.name}}
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                {{product.sales}}
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                {{product.revenue | currency}}
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                  <span class="text-yellow-400">★</span>
                  <span class="ml-1">{{product.rating}}</span>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  `,
  styles: []
})
export class AnalyticsComponent implements OnInit {
  salesData: SalesData[] = [];
  topProducts: ProductPerformance[] = [
    {
      name: 'Product A',
      sales: 1234,
      revenue: 45678,
      rating: 4.5
    },
    {
      name: 'Product B',
      sales: 987,
      revenue: 34567,
      rating: 4.3
    },
    // Add more products as needed
  ];

  constructor() { }

  ngOnInit(): void {
    // Fetch analytics data from service
  }
} 