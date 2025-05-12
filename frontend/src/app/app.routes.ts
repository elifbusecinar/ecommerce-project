import { Routes } from '@angular/router';
import { RegisterComponent } from './features/auth/register/register.component';
import { LoginComponent } from './features/auth/login/login.component';
import { HomeComponent } from './features/home/home.component';
import { ProductListComponent } from './features/products/product-list/product-list.component';
import { ProductDetailComponent } from './features/products/product-detail/product-detail.component';
import { CartComponent } from './features/cart/cart.component';
import { CheckoutComponent } from './features/checkout/checkout.component';
import { OrderConfirmationComponent } from './features/orders/order-confirmation/order-confirmation.component';
import { OrderTrackingComponent } from './features/orders/order-tracking/order-tracking.component';
import { ProfileComponent } from './features/profile/profile/profile.component';
import { ForbiddenComponent } from './features/forbidden/forbidden.component';
import { AuthGuard } from './core/guards/auth.guard';

export const appRoutes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: 'home', component: HomeComponent },
  { path: 'auth', loadChildren: () => import('./features/auth/auth-routing.module').then(m => m.AuthRoutingModule)},
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'products', component: ProductListComponent },
  { path: 'products/:id', component: ProductDetailComponent },
  { path: 'cart', component: CartComponent },
  { path: 'checkout', component: CheckoutComponent, canActivate: [AuthGuard] },
  { path: 'order-confirmation', component: OrderConfirmationComponent },
  { path: 'order-tracking', component: OrderTrackingComponent },
  { path: 'profile', component: ProfileComponent },
  { path: 'admin', loadChildren: () => import('./features/admin/admin-routing.module').then(m => m.AdminRoutingModule), canActivate: [AuthGuard], data: { roles: ['ADMIN'] } },
  { path: 'forbidden', component: ForbiddenComponent }
  
];
