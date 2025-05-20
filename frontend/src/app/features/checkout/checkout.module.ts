import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { CheckoutRoutingModule } from './checkout-routing.module';
import { CheckoutComponent } from './checkout.component';
import { NgxStripeModule } from 'ngx-stripe';
import { environment } from '../../../environments/environment'; // environment import edildi

@NgModule({
  declarations: [
    // CheckoutComponent standalone olduğu için burada deklare edilmeyebilir.
  ],
  imports: [
    CommonModule,
    CheckoutRoutingModule,
    ReactiveFormsModule,
    CheckoutComponent,
    // NgxStripeModule.forRoot() app.config.ts'de zaten yapıldıysa burada tekrar çağırmaya gerek yok.
    // Eğer sadece bu modülde kullanılacaksa ve app.config.ts'de yoksa buraya ekleyebilirsiniz:
    // NgxStripeModule.forRoot(environment.stripePublishableKey)
  ]
})
export class CheckoutModule { }