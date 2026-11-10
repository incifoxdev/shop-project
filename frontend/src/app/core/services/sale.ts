import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { Sale } from '../models/sale';

@Injectable({
  providedIn: 'root'
})
export class SaleService {

  private readonly apiUrl = environment.apiUrl;

  constructor(
    private readonly http: HttpClient
  ) {}

  getAllSales(): Observable<Sale[]> {
    return this.http.get<Sale[]>(
      `${this.apiUrl}/sales`
    );
  }

  getMySales(): Observable<Sale[]> {
    return this.http.get<Sale[]>(
      `${this.apiUrl}/sales/me`
    );
  }

  getSales(userId: string): Observable<Sale[]> {
    return this.http.get<Sale[]>(
      `${this.apiUrl}/sales/user/${userId}`
    );
  }

  getSaleById(saleId: number): Observable<Sale> {
    return this.http.get<Sale>(
      `${this.apiUrl}/sales/${saleId}`
    );
  }

  checkout(): Observable<Sale> {
    return this.http.post<Sale>(
      `${this.apiUrl}/sales/checkout`,
      {}
    );
  }
}