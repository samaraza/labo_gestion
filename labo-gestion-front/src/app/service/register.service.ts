import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RegisterService {

  private baseUrl = 'https://labo-gestion.onrender.com/api/v1/auth';

  constructor(private httpClient: HttpClient) { }

  register(userData: any): Observable<any> {
    return this.httpClient.post(`${this.baseUrl}/register`, userData);
  }
}
