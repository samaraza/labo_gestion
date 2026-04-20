import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Commande } from '../models/commande';

@Injectable({ providedIn: 'root' })
export class CommandeService {
  private baseUrl = 'https://labo_gestion.onrender.com/api/v1/commandes';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  public getAllCommandes(): Observable<Commande[]> {
    return this.http.get<Commande[]>(this.baseUrl, { headers: this.getHeaders() });
  }

  public addCommande(commande: any): Observable<Commande> {
    return this.http.post<Commande>(this.baseUrl, commande, { headers: this.getHeaders() });
  }

  public deleteCommande(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`, { headers: this.getHeaders() });
  }

  public updateCommande(commande: any, id: number): Observable<Commande> {
    return this.http.put<Commande>(`${this.baseUrl}/${id}`, commande, { headers: this.getHeaders() });
  }

  public generatePDF(id: number): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/${id}/pdf`, {
      headers: this.getHeaders(),
      responseType: 'blob'
    });
  }
}
