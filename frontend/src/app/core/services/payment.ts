import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface PaymentInitResponse {
  url: string;
  token: string;
}

@Injectable({
  providedIn: 'root'
})
export class PaymentService {

  private apiUrl = `${environment.apiUrl}/payment`;

  constructor(private http: HttpClient) {}

  initiatePayment(): Observable<PaymentInitResponse> {
    return this.http.post<PaymentInitResponse>(
      `${this.apiUrl}/initiate`, {}
    );
  }
}
