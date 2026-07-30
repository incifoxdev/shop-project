import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class Auth {

  private apiUrl = environment.apiUrl;

  constructor(
    private http: HttpClient
  ) {}

  login(data: any) {
    return this.http.post<any>(
      `${this.apiUrl}/auth/login`,
      data
    );
  }

  saveSession(response: any): void {
    localStorage.setItem(
      'token',
      response.token
    );

    localStorage.setItem(
      'userId',
      response.userId
    );

    localStorage.setItem(
      'email',
      response.email
    );

    localStorage.setItem(
      'role',
      response.role
    );
  }

  getToken(): string | null {
    return localStorage.getItem(
      'token'
    );
  }

  getUserId(): string | null {
    return localStorage.getItem(
      'userId'
    );
  }

  getEmail(): string | null {
    return localStorage.getItem(
      'email'
    );
  }

  getRole(): string | null {
    return localStorage.getItem(
      'role'
    );
  }

  isAdmin(): boolean {
    return this.getRole() === 'ROLE_ADMIN';
  }

  logout(): void {
    localStorage.removeItem(
      'token'
    );

    localStorage.removeItem(
      'userId'
    );

    localStorage.removeItem(
      'email'
    );

    localStorage.removeItem(
      'role'
    );
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }
}