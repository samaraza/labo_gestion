import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { Post } from '../models/post';

@Injectable({ providedIn: 'root' })
export class PostService {
  private baseUrl = 'http://localhost:8089/api/v1/posts';

  constructor(private httpClient: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  public getAllPosts(): Observable<Post[]> {
    return this.httpClient.get<Post[]>(this.baseUrl, { headers: this.getHeaders() });
  }

  public addPost(post: any): Observable<Post> {
    console.log('post', post);
    return this.httpClient.post<Post>(this.baseUrl, post, { headers: this.getHeaders() });
  }

  public deletePost(id: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.baseUrl}/${id}`, { headers: this.getHeaders() });
  }

  public updatePost(post: any, id: number): Observable<Post> {
    return this.httpClient.put<Post>(`${this.baseUrl}/${id}`, post, { headers: this.getHeaders() });
  }
}
