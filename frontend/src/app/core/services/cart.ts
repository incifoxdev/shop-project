import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';

import { Cart } from '../models/cart';

import {
  environment
} from '../../../environments/environment';

interface UpdateCartItemRequest {
  userId: string;
  productId: string;
  quantity: number;
}

interface AddToCartRequest {
  userId: string;
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
    private http: HttpClient
  ) {}

  addProduct(
    data: AddToCartRequest
  ): Observable<Cart> {
    return this.http.post<Cart>(
      `${this.apiUrl}/cart/add`,
      data
    );
  }

  getCart(
    userId: string
  ): Observable<Cart> {
    return this.http.get<Cart>(
      `${this.apiUrl}/cart/${userId}`
    );
  }

  updateQuantity(
    data: UpdateCartItemRequest
  ): Observable<Cart> {
    return this.http.put<Cart>(
      `${this.apiUrl}/cart/update`,
      data
    );
  }

  removeProduct(
    userId: string,
    productId: string
  ): Observable<Cart> {
    return this.http.delete<Cart>(
      `${this.apiUrl}/cart/remove/${userId}/${productId}`
    );
  }

  clearCart(
    userId: string
  ): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/cart/clear/${userId}`
    );
  }
}
