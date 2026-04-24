import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Produit } from '../models/produit';

@Injectable({ providedIn: 'root' })
export class ProduitService {
    // ✅ الرابط الأساسي الصحيح لـ API
    private apiBaseUrl = 'https://labo_gestion_api.onrender.com/api/v1';
    private produitsUrl = `${this.apiBaseUrl}/produits`;

    constructor(private http: HttpClient) {}

    private getHeaders(): HttpHeaders {
        const token = localStorage.getItem('token');
        return new HttpHeaders({
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        });
    }

    // ✅ جلب جميع منتجات المدرسة الحالية فقط
    public getAllProduits(): Observable<Produit[]> {
        return this.http.get<Produit[]>(`${this.produitsUrl}/my-school`, { headers: this.getHeaders() });
    }

    // ✅ إضافة منتج جديد للمدرسة الحالية
    public addProduit(produit: any): Observable<Produit> {
        return this.http.post<Produit>(`${this.produitsUrl}/my-school`, produit, { headers: this.getHeaders() });
    }

    // ✅ حذف منتج
    public deleteProduit(id: number): Observable<void> {
        return this.http.delete<void>(`${this.produitsUrl}/${id}`, { headers: this.getHeaders() });
    }

    // ✅ تحديث منتج
    public updateProduit(produit: any, id: number): Observable<Produit> {
        return this.http.put<Produit>(`${this.produitsUrl}/${id}`, produit, { headers: this.getHeaders() });
    }
}
