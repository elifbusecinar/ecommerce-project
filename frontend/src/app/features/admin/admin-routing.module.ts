import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '../../core/guards/auth.guard';

import { DashboardComponent } from './dashboard/dashboard.component';
import { ManageProductsComponent } from './manage-products/manage-products.component';
import { ManageUsersComponent } from './manage-users/manage-users.component';
import { OrderManagementComponent } from './order-management/order-management.component';
import { AnalyticsComponent } from './analytics/analytics.component';
import { NewProductComponent } from './manage-products/new-product.component';
import { EditProductComponent } from './manage-products/edit-product.component';

const routes: Routes = [
  {
    path: '',
    canActivate: [AuthGuard],
    data: { roles: ['ADMIN'] },
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: DashboardComponent },
      { path: 'products', component: ManageProductsComponent },
      { path: 'products/new', component: NewProductComponent },
      { path: 'products/edit/:id', component: EditProductComponent },
      { path: 'users', component: ManageUsersComponent },
      { path: 'orders', component: OrderManagementComponent },
      { path: 'analytics', component: AnalyticsComponent }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdminRoutingModule { }
