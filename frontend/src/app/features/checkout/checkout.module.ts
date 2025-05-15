import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms'; // Zaten CheckoutComponent'te var
import { CheckoutRoutingModule } from './checkout-routing.module';
import { CheckoutComponent } from './checkout.component';
import { NgxStripeModule } from 'ngx-stripe'; // ngx-stripe importu

@NgModule({
  declarations: [
    // CheckoutComponent standalone olduğu için burada deklare edilmeyebilir.
  ],
  imports: [
    CommonModule,
    CheckoutRoutingModule,
    ReactiveFormsModule, // CheckoutComponent standalone olduğu için kendi imports'unda olabilir
    CheckoutComponent,   // Standalone component importu
    NgxStripeModule.forRoot('pk_test_SENIN_YAYINLANABILIR_ANAHTARIN') // Stripe Publishable Key
  ]
})
export class CheckoutModule { }