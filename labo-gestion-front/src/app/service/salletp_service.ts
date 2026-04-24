import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class SalleTpService {
  private baseUrl = 'https://labo_gestion_api.onrender.com/api/v1/salle-tps';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  public getAllSalleTp(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/my-school`, { headers: this.getHeaders() });
  }

  public addSalleTp(salleTp: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/my-school`, salleTp, { headers: this.getHeaders() });
  }

  public updateSalleTp(salleTp: any, id: number): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}`, salleTp, { headers: this.getHeaders() });
  }

  public deleteSalleTp(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`, { headers: this.getHeaders() });
  }
}
