import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly API_URL = '/auth/login';
  private isAuthenticated = false;

  constructor(private http: HttpClient) {}

  login(credentials: { username: string; password: string }): Observable<any> {

    // 🔹 REAL BACKEND CALL (uncomment when backend ready)
    /*
    return this.http.post(this.API_URL, credentials).pipe(
      tap(() => this.isAuthenticated = true)
    );
    */

    // 🔹 MOCK LOGIN
    if (credentials.username && credentials.password) {
      this.isAuthenticated = true;
      return of({ token: 'mock-token' });
    }

    return of(null);
  }

  logout() {
    this.isAuthenticated = false;
  }

  isLoggedIn(): boolean {
    return this.isAuthenticated;
  }
}
