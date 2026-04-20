// src/app/service/fournisseur_service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Fournisseur } from '../models/fournisseur';

@Injectable({ providedIn: 'root' })
export class FournisseurService {
  private baseUrl = 'https://labo-gestion.onrender.com/api/v1/fournisseurs';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  public getAllFournisseurs(): Observable<Fournisseur[]> {
    return this.http.get<Fournisseur[]>(this.baseUrl, { headers: this.getHeaders() });
  }

  public addFournisseur(fournisseur: any): Observable<Fournisseur> {
    return this.http.post<Fournisseur>(this.baseUrl, fournisseur, { headers: this.getHeaders() });
  }

  public deleteFournisseur(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`, { headers: this.getHeaders() });
  }

  public updateFournisseur(fournisseur: any, id: number): Observable<Fournisseur> {
    return this.http.put<Fournisseur>(`${this.baseUrl}/${id}`, fournisseur, { headers: this.getHeaders() });
  }
}
