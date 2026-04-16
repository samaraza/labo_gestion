import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';
import { User } from '../models/user';

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  private baseUrl = 'http://localhost:8089/api/v1/auth';

  public connectedUser: User = new User();
  public roles: string = "";
  public isLoggedIn: boolean = false;
  public loggedUser: string = "";

  constructor(
    private httpClient: HttpClient,
    private router: Router
  ) { }

  // ✅ تسجيل الدخول (POST)
  public signIn(payload: { email: string; password: string }): Observable<any> {
    return this.httpClient.post(`${this.baseUrl}/authenticate`, payload);
  }

  // ✅ تسجيل مستخدم جديد
  public register(payload: any): Observable<any> {
    return this.httpClient.post(`${this.baseUrl}/register`, payload);
  }

  // ✅ تسجيل الخروج
  public logOut() {
    this.isLoggedIn = false;
    this.loggedUser = "";
    this.roles = "";
    this.connectedUser = new User();
    localStorage.removeItem('token');
    localStorage.removeItem('loggedUser');
    localStorage.removeItem('user');      // حذف بيانات المستخدم
    localStorage.setItem('isLoggedIn', 'false');
    this.router.navigate(['login']);
  }

  // ✅ حفظ بيانات المستخدم بعد تسجيل الدخول
  public setUserData(user: any): void {
    this.connectedUser = user;
    this.roles = user.role;
    this.isLoggedIn = true;
    this.loggedUser = user.email;
    localStorage.setItem('user', JSON.stringify(user));
    localStorage.setItem('loggedUser', user.email);
    localStorage.setItem('isLoggedIn', 'true');
  }

  // ✅ استرجاع بيانات المستخدم من localStorage (لحفظها بعد تحديث الصفحة)
  public getUserData(): any {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      this.connectedUser = user;
      this.roles = user.role;
      this.isLoggedIn = true;
      this.loggedUser = user.email;
      return user;
    }
    return null;
  }

  // ✅ التحقق من حالة تسجيل الدخول
  public isAuthenticated(): boolean {
    return localStorage.getItem('isLoggedIn') === 'true';
  }
}
