// src/app/service/preparation_service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Preparation } from '../models/preparation';

@Injectable({ providedIn: 'root' })
export class PreparationService {
  private baseUrl = 'https://labo_gestion_api.onrender.com/api/v1/preparations';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  public getAllPreparations(): Observable<Preparation[]> {
        return this.http.get<Preparation[]>(`${this.baseUrl}/my-school`, { headers: this.getHeaders() });
  }

  public addPreparation(preparation: any): Observable<Preparation> {
    return this.http.post<Preparation>(`${this.baseUrl}/my-school`, preparation, { headers: this.getHeaders() });
  }

  public deletePreparation(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`, { headers: this.getHeaders() });
  }

  public updatePreparation(preparation: any, id: number): Observable<Preparation> {
    return this.http.put<Preparation>(`${this.baseUrl}/${id}`, preparation, { headers: this.getHeaders() });
  }
}
