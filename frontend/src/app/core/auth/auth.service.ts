import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { map, Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { User } from '../models/user.model';

const TOKEN_STORAGE_KEY = 'auth_token';

interface AuthResponse {
  token?: string;
  accessToken?: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly apiUrl = environment.apiUrl;

  login(email: string, password: string): Observable<void> {
    return this.http
      .post<AuthResponse>(`${this.apiUrl}/api/auth/login`, { email, password })
      .pipe(
        map((response) => {
          const token = response.token ?? response.accessToken;

          if (!token) {
            throw new Error('Authentication token missing in response.');
          }

          localStorage.setItem(TOKEN_STORAGE_KEY, token);
        })
      );
  }

  register(email: string, password: string): Observable<void> {
    return this.http
      .post<void>(`${this.apiUrl}/api/auth/register`, { email, password })
      .pipe(map(() => void 0));
  }

  logout(): void {
    localStorage.removeItem(TOKEN_STORAGE_KEY);
    void this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_STORAGE_KEY);
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  getCurrentUser(): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/api/auth/me`);
  }
}
