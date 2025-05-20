import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';
import { appRoutes } from './app.routes';
import { AuthInterceptor } from './core/interceptors/auth.interceptor';
import { NgxStripeModule } from 'ngx-stripe';
import { environment } from '../environments/environment'; // environment import edildi

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(appRoutes),
    provideHttpClient(withInterceptors([AuthInterceptor])),
    provideAnimations(),
    importProvidersFrom(NgxStripeModule.forRoot(environment.stripePublishableKey)) // environment'tan alındı
  ]
};