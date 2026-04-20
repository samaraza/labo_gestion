import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Produit } from '../models/produit';

@Injectable({ providedIn: 'root' })
export class ProduitService {
  private baseUrl = 'https://labo_gestion.onrender.com/api/v1/produits';
  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  public getAllProduits(): Observable<Produit[]> {
    return this.http.get<Produit[]>(this.baseUrl, { headers: this.getHeaders() });
  }

  public addProduit(produit: any): Observable<Produit> {
    return this.http.post<Produit>(this.baseUrl, produit, { headers: this.getHeaders() });
  }

  public deleteProduit(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`, { headers: this.getHeaders() });
  }

  public updateProduit(produit: any, id: number): Observable<Produit> {
    return this.http.put<Produit>(`${this.baseUrl}/${id}`, produit, { headers: this.getHeaders() });
  }
}
