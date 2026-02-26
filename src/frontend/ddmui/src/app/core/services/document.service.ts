import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { SecurityDocumentInfo } from '../models/security-document-info';

@Injectable({
  providedIn: 'root',
})
export class DocumentService {
  private readonly API_URL = `${environment.apiUrl}/document`;

  constructor(private http: HttpClient) {}

  downloadFile(id: number) {
    const token = localStorage.getItem('token');

    return this.http.get(`${this.API_URL}/${id}`, {
      responseType: 'blob',
      headers: { Authorization: `Bearer ${token}` },
    });
  }

  parseDocument(document: File): Observable<SecurityDocumentInfo> {
    const formData = new FormData();
    formData.append('document', document);
    const token = localStorage.getItem('token');

    return this.http.post<SecurityDocumentInfo>(
      `${this.API_URL}/parse`,
      formData,
      { headers: { Authorization: `Bearer ${token}` } },
    );
  }

  indexDocument(
    updatedInfo: SecurityDocumentInfo,
  ): Observable<SecurityDocumentInfo> {
    const token = localStorage.getItem('token');

    return this.http.post<SecurityDocumentInfo>(
      `${this.API_URL}/index`,
      updatedInfo,
      { headers: { Authorization: `Bearer ${token}` } },
    );
  }

  deleteDocument(id: number) {
    const token = localStorage.getItem('token');

    return this.http.delete(`${this.API_URL}/${id}`, {
      headers: { Authorization: `Bearer ${token}` },
    });
  }
}
