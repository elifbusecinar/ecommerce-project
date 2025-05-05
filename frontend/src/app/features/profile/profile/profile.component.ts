import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';

interface UserProfile {
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  addresses: {
    type: string;
    street: string;
    city: string;
    state: string;
    zip: string;
  }[];
  orders: {
    id: string;
    date: Date;
    total: number;
    status: string;
  }[];
}

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss'],
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule]
})
export class ProfileComponent implements OnInit {
  profile: UserProfile = {
    firstName: 'John',
    lastName: 'Doe',
    email: 'john.doe@example.com',
    phone: '(555) 123-4567',
    addresses: [
      {
        type: 'Home',
        street: '123 Main St',
        city: 'Anytown',
        state: 'ST',
        zip: '12345'
      },
      {
        type: 'Work',
        street: '456 Office Blvd',
        city: 'Worktown',
        state: 'ST',
        zip: '67890'
      }
    ],
    orders: [
      {
        id: 'ORD-001',
        date: new Date(2024, 2, 1),
        total: 150.00,
        status: 'Delivered'
      },
      {
        id: 'ORD-002',
        date: new Date(2024, 2, 15),
        total: 75.50,
        status: 'In Transit'
      }
    ]
  };

  activeTab: 'info' | 'addresses' | 'orders' = 'info';
  editMode = false;

  constructor() {}

  ngOnInit(): void {}

  saveProfile(): void {
    // Implement save logic here
    this.editMode = false;
  }

  addAddress(): void {
    this.profile.addresses.push({
      type: 'Other',
      street: '',
      city: '',
      state: '',
      zip: ''
    });
  }

  removeAddress(index: number): void {
    this.profile.addresses.splice(index, 1);
  }
}
