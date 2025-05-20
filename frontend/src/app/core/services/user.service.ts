import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../models/user.model';
import { environment } from '../../../environments/environment';
import { UserDTO } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) { }

  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl);
  }

  activateUser(userId: number): Observable<User> {
    return this.http.patch<User>(`${this.apiUrl}/${userId}/activate`, {});
  }

  deactivateUser(userId: number): Observable<User> {
    return this.http.patch<User>(`${this.apiUrl}/${userId}/deactivate`, {});
  }

  getCurrentUserProfile(): Observable<UserDTO> { // UserDTO dönecek şekilde güncellendi
    return this.http.get<UserDTO>(`${this.apiUrl}/profile`);
  }

  updateUser(id: number, userData: Partial<UserDTO>): Observable<UserDTO> { // Partial<UserDTO> veya UserUpdateDTO
    return this.http.put<UserDTO>(`${this.apiUrl}/${id}`, userData);
  }
} 