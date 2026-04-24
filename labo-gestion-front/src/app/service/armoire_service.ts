// src/app/service/armoire_service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Armoire } from '../models/armoire';

@Injectable({ providedIn: 'root' })
export class ArmoireService {
   private baseUrl = 'https://labo_gestion_api.onrender.com/api/v1/armoires';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  public getAllArmoires(): Observable<Armoire[]> {
     return this.http.get<Armoire[]>(`${this.baseUrl}/my-school`, { headers: this.getHeaders() });
  }

  public addArmoire(armoire: Armoire): Observable<Armoire> {
       return this.http.post<Armoire>(`${this.baseUrl}/my-school`, armoire, { headers: this.getHeaders() });
  }

  public deleteArmoire(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`, { headers: this.getHeaders() });
  }

  public updateArmoire(armoire: Armoire, id: number): Observable<Armoire> {
    return this.http.put<Armoire>(`${this.baseUrl}/${id}`, armoire, { headers: this.getHeaders() });
  }
}
