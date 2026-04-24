// user.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../models/user';
import { map } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class UserService {
  private baseUrl = 'https://labo_gestion_api.onrender.com/api/v1/users';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  // دالة تحويل: تحويل roles (مصفوفة) إلى role (قيمة واحدة)
  private transformUser(user: any): User {
    if (user && user.roles && user.roles.length > 0) {
      user.role = user.roles[0].name;
    } else if (user && user.role && user.role.name) {
      user.role = user.role.name;
    }
    return user;
  }

  // ❌ هذا يجلب جميع المستخدمين من جميع المدارس (للمدير العام فقط)
  public getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.baseUrl, { headers: this.getHeaders() })
      .pipe(
        map(users => users.map(u => this.transformUser(u)))
      );
  }

  // ✅ هذه الدالة الجديدة تجلب فقط مستخدمي المدرسة الحالية
  public getUsersForCurrentSchool(): Observable<User[]> {
    return this.http.get<User[]>(`${this.baseUrl}/my-school/all`, { headers: this.getHeaders() })
      .pipe(
        map(users => users.map(u => this.transformUser(u)))
      );
  }

  // ✅ جلب الأساتذة فقط من المدرسة الحالية
  public getProfesseursForCurrentSchool(): Observable<User[]> {
    return this.http.get<User[]>(`${this.baseUrl}/my-school/professeurs`, { headers: this.getHeaders() })
      .pipe(
        map(users => users.map(u => this.transformUser(u)))
      );
  }

  // ✅ جلب المحضرين فقط من المدرسة الحالية
  public getPreparateursForCurrentSchool(): Observable<User[]> {
    return this.http.get<User[]>(`${this.baseUrl}/my-school/preparateurs`, { headers: this.getHeaders() })
      .pipe(
        map(users => users.map(u => this.transformUser(u)))
      );
  }

  // ✅ إضافة مستخدم جديد للمدرسة الحالية
  public addUserForCurrentSchool(user: User): Observable<User> {
    return this.http.post<User>(`${this.baseUrl}/my-school/all`, user, { headers: this.getHeaders() });
  }

  public getUserById(id: number): Observable<User> {
    return this.http.get<User>(`${this.baseUrl}/${id}`, { headers: this.getHeaders() });
  }

  public addUser(user: User): Observable<User> {
    return this.http.post<User>(this.baseUrl, user, { headers: this.getHeaders() });
  }

  public updateUser(user: User, id: number): Observable<User> {
    return this.http.put<User>(`${this.baseUrl}/${id}`, user, { headers: this.getHeaders() });
  }

  public updateFromAdmin(user: User, id: number): Observable<User> {
    return this.http.put<User>(`${this.baseUrl}/${id}`, user, { headers: this.getHeaders() });
  }

  public deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`, { headers: this.getHeaders() });
  }

  // NEW METHOD: Assign user to the director's school
  public affecterUserToEcole(userId: number | string | undefined, roleDansEcole: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/${userId}/affecter`, { role: roleDansEcole }, { headers: this.getHeaders() });
  }
}
