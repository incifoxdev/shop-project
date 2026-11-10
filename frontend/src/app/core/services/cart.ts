import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';

import { Cart } from '../models/cart';
import { environment } from '../../../environments/environment';

export interface UpdateCartItemRequest {
  productId: string;
  quantity: number;
}

export interface AddToCartRequest {
  productId: string;
  quantity: number;
}

@Injectable({
  providedIn: 'root'
})
export class CartService {

  private readonly apiUrl =
    environment.apiUrl;

  constructor(
    private readonly http: HttpClient
  ) {}

  addProduct(
    data: AddToCartRequest
  ): Observable<Cart> {
    return this.http.post<Cart>(
      `${this.apiUrl}/cart/items`,
      data
    );
  }

  getCart(): Observable<Cart> {
    return this.http.get<Cart>(
      `${this.apiUrl}/cart/me`
    );
  }

  updateQuantity(
    data: UpdateCartItemRequest
  ): Observable<Cart> {
    return this.http.put<Cart>(
      `${this.apiUrl}/cart/items`,
      data
    );
  }

  removeProduct(
    productId: string
  ): Observable<Cart> {
    return this.http.delete<Cart>(
      `${this.apiUrl}/cart/items/${productId}`
    );
  }

  clearCart(): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/cart/me`
    );
  }
}