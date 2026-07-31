import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';

import {
  CreateUserRequest,
  UpdateUserRequest,
  User
} from '../models/user';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private readonly apiUrl = `${environment.apiUrl}/users`;

  constructor(
    private readonly http: HttpClient
  ) {}

  getAll(): Observable<User[]> {
    return this.http.get<User[]>(
      this.apiUrl
    );
  }

  getById(id: string): Observable<User> {
    return this.http.get<User>(
      `${this.apiUrl}/${id}`
    );
  }

  create(
    request: CreateUserRequest
  ): Observable<User> {
    return this.http.post<User>(
      this.apiUrl,
      request
    );
  }

  update(
    id: string,
    request: UpdateUserRequest
  ): Observable<User> {
    return this.http.put<User>(
      `${this.apiUrl}/${id}`,
      request
    );
  }

  delete(id: string): Observable<string> {
    return this.http.delete(
      `${this.apiUrl}/${id}`,
      {
        responseType: 'text'
      }
    );
  }
}