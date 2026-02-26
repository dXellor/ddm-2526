import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { SearchResponse } from '../models/search-response';

@Injectable({
  providedIn: 'root'
})
export class SearchService {

  private readonly API_URL = `${environment.apiUrl}/search`;

  constructor(private http: HttpClient) {}

  parameterSearch(request: { fieldName: string; value: string }, page: number = 0, size: number = 10): Observable<SearchResponse> {

    const token = localStorage.getItem('token');
    const params = new HttpParams()
      .set('fieldName', request.fieldName)
      .set('value', request.value)
      .set('page', page)
      .set('size', size);

    return this.http.get<SearchResponse>(`${this.API_URL}/parameter`, {params, headers: { Authorization: `Bearer ${token}`}});
  }

  knnSearch(request: { query: string }, page: number = 0, size: number = 10): Observable<SearchResponse> {

    const token = localStorage.getItem('token');
    const params = new HttpParams()
      .set('query', request.query)
      .set('page', page)
      .set('size', size);

    return this.http.get<SearchResponse>(`${this.API_URL}/knn`, {params, headers: { Authorization: `Bearer ${token}`}});
  }
}
