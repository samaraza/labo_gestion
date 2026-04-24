import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Labo } from '../models/labo';

@Injectable({ providedIn: 'root' })
export class LaboService {
    private apiBaseUrl = 'https://labo_gestion_api.onrender.com/api/v1';
    private labosUrl = `${this.apiBaseUrl}/labos`;

    constructor(private http: HttpClient) {}

    // ✅ أضف هذه الدالة
    private getHeaders(): HttpHeaders {
        const token = localStorage.getItem('token');
        return new HttpHeaders({
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        });
    }

    getAllLabos(): Observable<Labo[]> {
        return this.http.get<Labo[]>(`${this.labosUrl}/my-school`, { headers: this.getHeaders() });
    }

    addLabo(labo: any): Observable<Labo> {
        return this.http.post<Labo>(`${this.labosUrl}/my-school`, labo, { headers: this.getHeaders() });
    }

    deleteLabo(id: number): Observable<void> {
        return this.http.delete<void>(`${this.labosUrl}/${id}`, { headers: this.getHeaders() });
    }

    updateLabo(labo: any, id: number): Observable<Labo> {
        return this.http.put<Labo>(`${this.labosUrl}/${id}`, labo, { headers: this.getHeaders() });
    }
}
