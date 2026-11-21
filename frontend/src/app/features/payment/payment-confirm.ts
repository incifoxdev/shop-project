import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-payment-confirm',
  standalone: true,
  imports: [CommonModule, MatProgressSpinnerModule, MatButtonModule, MatIconModule],
  template: `
    <div class="payment-container">
      @if (loading) {
        <mat-spinner></mat-spinner>
        <p>Confirmando tu pago...</p>
      } @else if (success) {
        <mat-icon class="success-icon">check_circle</mat-icon>
        <h2>¡Pago exitoso!</h2>
        <p>Tu compra ha sido procesada correctamente.</p>
        <p class="auth-code">Código de autorización: {{ authorizationCode }}</p>
        <button mat-raised-button color="primary" (click)="goToSales()">
          Ver mis compras
        </button>
      } @else {
        <mat-icon class="error-icon">error</mat-icon>
        <h2>Pago rechazado</h2>
        <p>{{ errorMessage }}</p>
        <button mat-raised-button color="primary" (click)="goToCart()">
          Volver al carrito
        </button>
      }
    </div>
  `,
  styles: [`
    .payment-container {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      min-height: 60vh;
      gap: 16px;
      text-align: center;
      padding: 24px;
    }
    .success-icon { color: #4caf50; font-size: 64px; width: 64px; height: 64px; }
    .error-icon { color: #f44336; font-size: 64px; width: 64px; height: 64px; }
    .auth-code { color: #666; font-size: 14px; }
  `]
})
export class PaymentConfirmComponent implements OnInit {

  loading = true;
  success = false;
  errorMessage = '';
  authorizationCode = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    const token = this.route.snapshot.queryParamMap.get('token_ws');
    const userId = this.route.snapshot.queryParamMap.get('userId');

    if (!token) {
      this.loading = false;
      this.errorMessage = 'Token de pago no encontrado';
      this.cdr.detectChanges();
      return;
    }

    let url = `${environment.apiUrl}/payment/confirm?token_ws=${token}`;
    if (userId) url += `&userId=${userId}`;

    this.http.post<any>(url, {}).subscribe({
      next: (response) => {
        this.loading = false;
        this.success = true;
        this.authorizationCode = response.authorizationCode || 'Procesado';
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.loading = false;
        if (err.error?.error === 'Cart is empty') {
          this.success = true;
          this.authorizationCode = 'Procesado';
        } else {
          this.errorMessage = err.error?.error || 'Error al confirmar el pago';
        }
        this.cdr.detectChanges();
      }
    });
  }

  goToSales() {
    this.router.navigate(['/sales']);
  }

  goToCart() {
    this.router.navigate(['/cart']);
  }
}
