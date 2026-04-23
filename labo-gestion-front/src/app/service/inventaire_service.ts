import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { Inventaire } from '../models/inventaire';

@Injectable({ providedIn: 'root' })
export class InventaireService {
  private baseUrl = 'https://labo-gestion-api.onrender.com/api/v1/inventaires';

  constructor(private httpClient: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  public getAllInventaires(): Observable<Inventaire[]> {
    return this.httpClient.get<Inventaire[]>(this.baseUrl, { headers: this.getHeaders() });
  }

  public addInventaire(inventaire: any): Observable<Inventaire> {
    return this.httpClient.post<Inventaire>(this.baseUrl, inventaire, { headers: this.getHeaders() });
  }

  public deleteInventaire(id: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.baseUrl}/${id}`, { headers: this.getHeaders() });
  }

  public updateInventaire(inventaire: any, id: number): Observable<Inventaire> {
    return this.httpClient.put<Inventaire>(`${this.baseUrl}/${id}`, inventaire, { headers: this.getHeaders() });
  }
}
