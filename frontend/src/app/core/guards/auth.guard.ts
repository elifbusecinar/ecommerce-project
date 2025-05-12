import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    if (this.authService.isAuthenticated()) {
      // Admin route check
      if (route.data['roles'] && route.data['roles'].includes('ADMIN')) {
        if (this.authService.hasRole('ADMIN')) { // Değişiklik burada
          return true;
        }
        // Kullanıcı admin değilse ve admin route'una erişmeye çalışıyorsa forbidden sayfasına yönlendir
        this.router.navigate(['/forbidden']);
        return false;
      }
      // Admin route değilse ve kullanıcı authenticated ise erişime izin ver
      return true;
    }

    // Kullanıcı authenticated değilse login sayfasına yönlendir
    this.router.navigate(['/auth/login'], { queryParams: { returnUrl: state.url }});
    return false;
  }
}