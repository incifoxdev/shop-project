import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Sale } from '../models/sale';


@Injectable({
  providedIn: 'root'
})
export class SaleService {


  private apiUrl = environment.apiUrl;


  constructor(
    private http: HttpClient
  ){}



  getSales(userId: string){

    return this.http.get<Sale[]>(
      `${this.apiUrl}/sales/user/${userId}`
    );

  }


  checkout(userId: string){

    return this.http.post<Sale>(
      `${this.apiUrl}/sales/checkout/${userId}`,
      {}
    );

  }


}
