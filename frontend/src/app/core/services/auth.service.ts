import { Injectable, Injector } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { tap, map, catchError, switchMap } from 'rxjs/operators';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { User, UserDTO } from '../models/user.model'; // User ve UserDTO modelleriniz
// import { jwtDecode } from 'jwt-decode'; // npm install jwt-decode --save

// Normalde bu arayüzler src/app/core/models/auth.model.ts gibi bir dosyada olmalı
export interface LoginRequest {
  usernameOrEmail?: string;
  username?: string;
  email?: string;
  password?: string;
}

export interface LoginRequestPayload {
  usernameOrEmail?: string;
  username?: string;
  email?: string;
  password?: string;
}

export interface RegisterRequestPayload {
  username?: string;
  email?: string;
  password?: string;
  firstName?: string;
  lastName?: string;
  phoneNumber?: string;
}

export interface JwtAuthResponse {
  token: string;
  type: string; // Bearer
  id: number;
  username: string;
  email: string;
  roles: string[];
  firstName?: string; // Opsiyonel, backend login response'unda varsa
  lastName?: string;  // Opsiyonel
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`; // Backend auth endpoint'leri için temel URL
  private usersApiUrl = `${environment.apiUrl}/users`; // /users/profile gibi endpointler için

  private currentUserSubject: BehaviorSubject<User | null>;
  public currentUser$: Observable<User | null>;

  // Inject Router using Injector to break circular dependency if CartService or others need AuthService early
  private router!: Router;

  constructor(
    private http: HttpClient,
    private injector: Injector
    ) {
    // Router'ı manuel olarak inject et
    // Bu, CartService gibi servislerin constructor'ında AuthService'i kullanması durumunda
    // oluşabilecek dairesel bağımlılık sorunlarını çözmeye yardımcı olabilir.
    // Eğer böyle bir sorununuz yoksa direkt constructor'a private router: Router yazabilirsiniz.
    setTimeout(() => this.router = this.injector.get(Router));

    const storedUser = this.getStoredUser();
    this.currentUserSubject = new BehaviorSubject<User | null>(storedUser);
    this.currentUser$ = this.currentUserSubject.asObservable();

    if (storedUser) {
      // Uygulama yüklendiğinde token geçerliliğini kontrol et ve gerekirse profili yeniden yükle
      // this.validateTokenAndLoadProfile().subscribe();
    }
  }

  private getStoredUser(): User | null {
    if (typeof localStorage !== 'undefined') {
      const userJson = localStorage.getItem('currentUser');
      if (userJson) {
        try {
          return JSON.parse(userJson) as User;
        } catch (e) {
          console.error("Error parsing stored user data", e);
          localStorage.removeItem('currentUser');
          return null;
        }
      }
    }
    return null;
  }

  private storeUser(user: User | null): void {
    if (typeof localStorage !== 'undefined') {
      if (user) {
        localStorage.setItem('currentUser', JSON.stringify(user));
      } else {
        localStorage.removeItem('currentUser');
      }
    }
    this.currentUserSubject.next(user);
  }

  private storeToken(token: string | null): void {
    if (typeof localStorage !== 'undefined') {
      if (token) {
        localStorage.setItem('authToken', token);
      } else {
        localStorage.removeItem('authToken');
      }
    }
  }

  public get currentUserValue(): User | null {
    return this.currentUserSubject.value;
  }

  public getToken(): string | null {
    if (typeof localStorage !== 'undefined') {
      return localStorage.getItem('authToken');
    }
    return null;
  }

  public isLoggedIn(): boolean {
    const token = this.getToken();
    return !!token // && !this.isTokenExpired(token); // Token geçerliliğini de kontrol et
  }

  // Opsiyonel: Token'ın süresinin dolup dolmadığını kontrol etme
  // private isTokenExpired(token: string | null): boolean {
  //   if (!token) return true;
  //   try {
  //     const decoded: any = jwtDecode(token);
  //     if (decoded.exp * 1000 < Date.now()) {
  //       console.log('Token expired');
  //       return true;
  //     }
  //     return false;
  //   } catch (error) {
  //     console.error('Error decoding token:', error);
  //     return true; // Hatalı token'ı geçersiz say
  //   }
  // }

  login(credentials: LoginRequestPayload): Observable<User> {
    return this.http.post<JwtAuthResponse>(`${this.apiUrl}/login`, credentials)
      .pipe(
        map(response => {
          // Backend'den gelen JwtAuthResponse'u User modeline map'le
          const user: User = {
            id: response.id,
            username: response.username,
            email: response.email,
            firstName: response.firstName || '',
            lastName: response.lastName || '',
            roles: response.roles,
            token: response.token,
            active: true // Varsayılan olarak true, backend'den gelmiyorsa
          };
          this.storeToken(response.token);
          this.storeUser(user);
          return user;
        }),
        catchError(err => this.handleAuthError(err))
      );
  }

  register(userInfo: RegisterRequestPayload): Observable<any> { // Backend genellikle bir mesaj döner (örn: "User registered successfully!")
    return this.http.post<any>(`${this.apiUrl}/signup`, userInfo)
      .pipe(
        // Başarılı kayıt sonrası otomatik giriş yapılabilir veya kullanıcı login sayfasına yönlendirilebilir.
        // tap(() => {
        //   this.router.navigate(['/auth/login'], { queryParams: { registrationSuccess: true } });
        // }),
        catchError(err => this.handleAuthError(err))
      );
  }

  logout(): void {
    this.storeUser(null);
    this.storeToken(null);
    // Sepet gibi kullanıcıya özel diğer servislerdeki state'leri de temizle
    // this.cartService.clearLocalCart(); // Örnek
    if (this.router) { // Router inject edilmişse
        this.router.navigate(['/auth/login']);
    }
  }

  /**
   * Token varsa ve geçerliyse kullanıcı profilini backend'den yükler.
   * Genellikle uygulama başlangıcında veya sayfa yenilendiğinde çağrılır.
   */
  public loadUserProfile(): Observable<User | null> {
    const token = this.getToken();
    if (!token) {
    //   this.logout(); // Token yoksa direkt logout yapabiliriz.
      return of(null);
    }
    // if (this.isTokenExpired(token)) {
    //   this.logout();
    //   return of(null);
    // }

    return this.http.get<UserDTO>(`${this.usersApiUrl}/profile`) // Backend /users/profile endpoint'i UserDTO dönmeli
      .pipe(
        map(userDto => {
          const currentUserData = this.currentUserValue; // Mevcut token'ı korumak için
          const user: User = {
            id: userDto.id,
            username: userDto.username,
            email: userDto.email,
            firstName: userDto.firstName,
            lastName: userDto.lastName,
            phoneNumber: userDto.phoneNumber,
            active: userDto.active,
            roles: userDto.roles,
            lastLogin: userDto.lastLogin, // String formatında olduğunu varsayıyoruz
            createdAt: userDto.createdAt, // String formatında
            updatedAt: userDto.updatedAt, // String formatında
            token: currentUserData?.token || token // Token'ı koru
          };
          this.storeUser(user);
          return user;
        }),
        catchError(error => {
          console.error("Failed to load user profile", error);
          this.logout(); // Profil yükleme başarısız olursa çıkış yap
          return of(null);
        })
      );
  }

  /**
   * Uygulama başlangıcında token'ı doğrular ve profili yükler.
   * Eğer token geçerli değilse kullanıcıyı sistemden atar.
   */
  validateTokenAndLoadProfile(): Observable<User | null> {
    if (this.isLoggedIn()) {
      return this.loadUserProfile().pipe(
        catchError(() => {
          this.logout();
          return of(null);
        })
      );
    } else {
      this.logout(); // Token yoksa veya süresi dolmuşsa (isLoggedIn içinde kontrol ediliyorsa)
      return of(null);
    }
  }


  /**
   * Mevcut kullanıcının bilgilerini (örn: profil güncellemesi sonrası) günceller.
   * @param updatedUserDTO Backend'den gelen güncellenmiş UserDTO
   */
  updateCurrentUser(updatedUserDTO: UserDTO): void {
    const currentUser = this.currentUserValue;
    if (currentUser) {
      // UserDTO'dan User modeline (veya frontend UserDTO modeline) map'leme
      // Mevcut token gibi frontend'e özel alanları koru
      const updatedUser: User = {
        ...currentUser, // Önceki değerleri al (özellikle token)
        id: updatedUserDTO.id,
        username: updatedUserDTO.username,
        email: updatedUserDTO.email,
        firstName: updatedUserDTO.firstName,
        lastName: updatedUserDTO.lastName,
        phoneNumber: updatedUserDTO.phoneNumber,
        active: updatedUserDTO.active,
        roles: updatedUserDTO.roles,
        lastLogin: updatedUserDTO.lastLogin,
        createdAt: updatedUserDTO.createdAt,
        updatedAt: updatedUserDTO.updatedAt
        // Token currentUser'dan zaten geliyor olacak (...currentUser ile)
      };
      this.storeUser(updatedUser);
    }
  }

  public getRoles(): string[] {
    return this.currentUserValue?.roles || [];
  }

  public isAdmin(): boolean {
    return this.getRoles().some(role => role === 'ROLE_ADMIN' || role === 'ADMIN'); // Backend'den gelen role ismine göre
  }

  public isUser(): boolean {
    return this.getRoles().some(role => role === 'ROLE_USER' || role === 'USER'); // Backend'den gelen role ismine göre
  }

  public isAuthenticated(): boolean {
    return this.isLoggedIn();
  }

  public hasRole(role: string): boolean {
    return this.getRoles().some(r => r === role || r === `ROLE_${role}`);
  }

  private handleAuthError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'Bir hata oluştu. Lütfen tekrar deneyin.';
    if (error.error instanceof ErrorEvent) {
      // Client-side veya network hatası
      errorMessage = `Hata: ${error.error.message}`;
    } else {
      // Backend'den gelen hata
      if (error.status === 401) {
        errorMessage = 'Geçersiz kullanıcı adı veya şifre.';
        // Otomatik logout yapılabilir veya kullanıcıya bilgi verilebilir.
        // this.logout();
      } else if (error.status === 400 && error.error && error.error.message) {
        // Backend'den gelen spesifik validasyon veya iş mantığı hatası
        errorMessage = error.error.message;
      } else if (error.status === 400 && error.error && Array.isArray(error.error.errors) && error.error.errors.length > 0) {
        // Spring Boot validasyon hataları genellikle bu formatta gelir
        errorMessage = error.error.errors.map((e: any) => e.defaultMessage || e.message).join(', ');
      } else if (error.error && typeof error.error === 'string') {
        errorMessage = error.error;
      } else {
        errorMessage = `Sunucu Hatası: ${error.status}. ${error.message}`;
      }
    }
    console.error('AuthService Hata:', error, 'Mesaj:', errorMessage);
    // Kullanıcıya gösterilecek bir notification servisi kullanılabilir.
    // alert(errorMessage);
    return throwError(() => new Error(errorMessage));
  }
}