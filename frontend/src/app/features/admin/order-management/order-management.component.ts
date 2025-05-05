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
  template: `
    <div class="container mx-auto p-4">
      <h2 class="text-2xl font-bold mb-4">Order Management</h2>
      
      <!-- Filters -->
      <div class="bg-white p-4 rounded-lg shadow mb-4">
        <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div>
            <label class="block text-sm font-medium text-gray-700">Status</label>
            <select class="mt-1 block w-full rounded-md border-gray-300">
              <option>All</option>
              <option>Pending</option>
              <option>Processing</option>
              <option>Shipped</option>
              <option>Delivered</option>
            </select>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700">Date Range</label>
            <input type="date" class="mt-1 block w-full rounded-md border-gray-300">
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700">Search</label>
            <input type="text" placeholder="Order ID or Customer" 
                   class="mt-1 block w-full rounded-md border-gray-300">
          </div>
        </div>
      </div>

      <!-- Orders Table -->
      <div class="bg-white rounded-lg shadow overflow-hidden">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Order ID
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Customer
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Date
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Status
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Total
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Payment
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Actions
              </th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr *ngFor="let order of orders">
              <td class="px-6 py-4 whitespace-nowrap">
                {{order.id}}
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                {{order.customerName}}
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                {{order.orderDate | date}}
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span [class]="'px-2 inline-flex text-xs leading-5 font-semibold rounded-full ' + 
                              (order.status === 'Delivered' ? 'bg-green-100 text-green-800' : 
                               order.status === 'Pending' ? 'bg-yellow-100 text-yellow-800' : 
                               'bg-blue-100 text-blue-800')">
                  {{order.status}}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                {{order.total | currency}}
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span [class]="'px-2 inline-flex text-xs leading-5 font-semibold rounded-full ' + 
                              (order.paymentStatus === 'Paid' ? 'bg-green-100 text-green-800' : 
                               'bg-red-100 text-red-800')">
                  {{order.paymentStatus}}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <button class="text-indigo-600 hover:text-indigo-900 mr-4">View</button>
                <button class="text-red-600 hover:text-red-900">Cancel</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination -->
      <div class="bg-white px-4 py-3 flex items-center justify-between border-t border-gray-200 sm:px-6">
        <div class="flex-1 flex justify-between sm:hidden">
          <button class="relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50">
            Previous
          </button>
          <button class="ml-3 relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50">
            Next
          </button>
        </div>
        <div class="hidden sm:flex-1 sm:flex sm:items-center sm:justify-between">
          <div>
            <p class="text-sm text-gray-700">
              Showing <span class="font-medium">1</span> to <span class="font-medium">10</span> of
              <span class="font-medium">97</span> results
            </p>
          </div>
          <div>
            <nav class="relative z-0 inline-flex rounded-md shadow-sm -space-x-px">
              <button class="relative inline-flex items-center px-2 py-2 rounded-l-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50">
                Previous
              </button>
              <button class="relative inline-flex items-center px-4 py-2 border border-gray-300 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50">
                1
              </button>
              <button class="relative inline-flex items-center px-4 py-2 border border-gray-300 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50">
                2
              </button>
              <button class="relative inline-flex items-center px-4 py-2 border border-gray-300 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50">
                3
              </button>
              <button class="relative inline-flex items-center px-2 py-2 rounded-r-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50">
                Next
              </button>
            </nav>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: []
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