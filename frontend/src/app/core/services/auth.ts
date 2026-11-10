import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import {
  Observable,
  catchError,
  finalize,
  of,
  tap
} from 'rxjs';

import { environment } from '../../../environments/environment';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
}

export interface CurrentUser {
  id: string;
  name: string;
  email: string;
  enabled: boolean;
  createdAt: string | null;
  roles: string[];
}

@Injectable({
  providedIn: 'root'
})
export class Auth {

  private readonly apiUrl = environment.apiUrl;

  private readonly currentUserSignal = signal<CurrentUser | null>(
    this.readStoredUser()
  );

  private readonly initializedSignal = signal(false);

  readonly currentUser = this.currentUserSignal.asReadonly();
  readonly initialized = this.initializedSignal.asReadonly();

  constructor(
    private readonly http: HttpClient
  ) {}

  login(data: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(
      `${this.apiUrl}/auth/login`,
      data
    );
  }

  saveToken(token: string): void {
    localStorage.setItem('token', token);
  }

  loadCurrentUser(): Observable<CurrentUser> {
    return this.http.get<CurrentUser>(
      `${this.apiUrl}/users/me`
    ).pipe(
      tap(user => {
        localStorage.setItem(
          'currentUser',
          JSON.stringify(user)
        );

        this.currentUserSignal.set(user);
      })
    );
  }

  restoreSession(): Observable<CurrentUser | null> {
    const token = this.getToken();

    if (!token) {
      localStorage.removeItem('currentUser');

      this.currentUserSignal.set(null);
      this.initializedSignal.set(true);

      return of(null);
    }

    return this.loadCurrentUser().pipe(
      catchError(error => {
        console.error(
          'No fue posible restaurar la sesión:',
          error
        );

        this.clearSession();

        return of(null);
      }),
      finalize(() => {
        this.initializedSignal.set(true);
      })
    );
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getCurrentUser(): CurrentUser | null {
    return this.currentUser();
  }

  getUserId(): string | null {
    return this.currentUser()?.id ?? null;
  }

  getUserName(): string {
    return this.currentUser()?.name ?? '';
  }

  getEmail(): string | null {
    return this.currentUser()?.email ?? null;
  }

  getRoles(): string[] {
    return this.currentUser()?.roles ?? [];
  }

  hasRole(role: string): boolean {
    return this.getRoles().includes(role);
  }

  isAdmin(): boolean {
    return this.hasRole('ROLE_ADMIN');
  }

  isUser(): boolean {
    return this.hasRole('ROLE_USER');
  }

  isLoggedIn(): boolean {
    return this.getToken() !== null;
  }

  logout(): void {
    this.clearSession();
    this.initializedSignal.set(true);
  }

  private clearSession(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('currentUser');

    this.currentUserSignal.set(null);
  }

  private readStoredUser(): CurrentUser | null {
    const storedUser = localStorage.getItem('currentUser');

    if (!storedUser) {
      return null;
    }

    try {
      return JSON.parse(storedUser) as CurrentUser;
    } catch {
      localStorage.removeItem('currentUser');

      return null;
    }
  }
}