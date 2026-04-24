import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  private baseUrl = 'https://labo_gestion_api.onrender.com/api/v1/auth';

  constructor(private httpClient: HttpClient) { }

  /**
   * تفعيل الحساب باستخدام التوكن
   * @param params { token: string }
   * @returns Observable<any>
   */
  confirm(params: { token: string }): Observable<any> {
    // الـ Backend يستخدم GET مع query parameter
    return this.httpClient.get(`${this.baseUrl}/activate-account`, {
      params: { token: params.token }
    });
  }
}
