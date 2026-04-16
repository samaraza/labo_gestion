import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Labo } from '../models/labo';

@Injectable({ providedIn: 'root' })
export class LaboService {
  private baseUrl = 'http://localhost:8089/api/v1/labos';

  constructor(private http: HttpClient) {}

  getAllLabos(): Observable<Labo[]> {
    return this.http.get<Labo[]>(this.baseUrl);
  }

  addLabo(labo: any): Observable<Labo> {
    return this.http.post<Labo>(this.baseUrl, labo);
  }

  deleteLabo(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  updateLabo(labo: any, id: number): Observable<Labo> {
    return this.http.put<Labo>(`${this.baseUrl}/${id}`, labo);
  }
}
