import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';

import { AdminRoutingModule } from './admin-routing.module';
import { DashboardComponent } from './dashboard/dashboard.component';
import { ManageProductsComponent } from './manage-products/manage-products.component';
import { ManageUsersComponent } from './manage-users/manage-users.component';
import { OrderManagementComponent } from './order-management/order-management.component';
import { AnalyticsComponent } from './analytics/analytics.component';

@NgModule({
  imports: [
    CommonModule,
    AdminRoutingModule,
    RouterModule,
    ReactiveFormsModule,
    DashboardComponent,
    ManageProductsComponent,
    ManageUsersComponent,
    OrderManagementComponent,
    AnalyticsComponent
  ]
})
export class AdminModule { }
