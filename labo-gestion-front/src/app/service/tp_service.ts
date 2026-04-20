import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { TpDTO } from '../models/tp.dto';

@Injectable({ providedIn: 'root' })
export class TpService {
  private baseUrl = 'https://labo_gestion.onrender.com/api/v1/tps';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  getAllTps(): Observable<TpDTO[]> {
    return this.http.get<TpDTO[]>(this.baseUrl, { headers: this.getHeaders() });
  }

  addTp(tp: TpDTO): Observable<TpDTO> {
    return this.http.post<TpDTO>(this.baseUrl, tp, { headers: this.getHeaders() });
  }

  updateTp(tp: TpDTO, id: number): Observable<TpDTO> {
    return this.http.put<TpDTO>(`${this.baseUrl}/${id}`, tp, { headers: this.getHeaders() });
  }

  deleteTp(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`, { headers: this.getHeaders() });
  }
}
