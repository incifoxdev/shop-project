import { Component } from '@angular/core';
import {
  ReactiveFormsModule,
  FormBuilder,
  Validators
} from '@angular/forms';

import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

import { Router } from '@angular/router';
import { finalize, switchMap } from 'rxjs';

import { Auth } from '../../../core/services/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule
  ],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {

  loading = false;
  errorMessage = '';

  loginForm;

  constructor(
    private readonly fb: FormBuilder,
    private readonly auth: Auth,
    private readonly router: Router
  ) {
    this.loginForm = this.fb.nonNullable.group({
      email: [
        '',
        [
          Validators.required,
          Validators.email
        ]
      ],
      password: [
        '',
        [
          Validators.required
        ]
      ]
    });
  }

  login(): void {
    if (this.loginForm.invalid || this.loading) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    const credentials = this.loginForm.getRawValue();

    this.auth.login(credentials)
      .pipe(
        switchMap(response => {
          this.auth.saveToken(response.token);
          return this.auth.loadCurrentUser();
        }),
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: user => {
          console.log('Usuario autenticado:', user);

          const destination = this.auth.isAdmin()
            ? '/dashboard'
            : '/products';

          this.router.navigate([destination]);
        },

        error: error => {
          console.error('Error de autenticación:', error);

          this.auth.logout();

          if (error.status === 401 || error.status === 403) {
            this.errorMessage = 'Correo o contraseña incorrectos.';
            return;
          }

          this.errorMessage =
            'No fue posible iniciar sesión. Inténtalo nuevamente.';
        }
      });
  }
}
