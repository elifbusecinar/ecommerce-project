// import { Injectable } from '@angular/core';
// import { HttpClient } from '@angular/common/http';
// import { Observable, tap } from 'rxjs';

// @Injectable({
//   providedIn: 'root'
// })
// export class AuthService {
//   private apiUrl = 'http://localhost:8080/api/auth';

//   constructor(private http: HttpClient) {}

//   login(credentials: { username: string; password: string }): Observable<any> {
//     return this.http.post(`${this.apiUrl}/login`, credentials).pipe(
//       tap((res: any) => localStorage.setItem('token', res.token))
//     );
//   }

//   register(data: { username: string; email: string; password: string }): Observable<any> {
//     return this.http.post(`${this.apiUrl}/register`, data);
//   }

//   logout(): void {
//     localStorage.removeItem('token');
//   }

//   isLoggedIn(): boolean {
//     return !!localStorage.getItem('token');
//   }

//   getToken(): string | null {
//     return localStorage.getItem('token');
//   }
// }
