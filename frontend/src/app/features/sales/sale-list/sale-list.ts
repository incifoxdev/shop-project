import {
  ChangeDetectorRef,
  Component,
  OnInit
} from '@angular/core';

import {
  CommonModule,
  CurrencyPipe,
  DatePipe
} from '@angular/common';

import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatExpansionModule } from '@angular/material/expansion';

import {
  MatProgressSpinnerModule
} from '@angular/material/progress-spinner';

import {
  MatSnackBar,
  MatSnackBarModule
} from '@angular/material/snack-bar';

import { Auth } from '../../../core/services/auth';
import { SaleService } from '../../../core/services/sale';
import { Sale } from '../../../core/models/sale';

@Component({
  selector: 'app-sale-list',
  standalone: true,
  imports: [
    CommonModule,
    CurrencyPipe,
    DatePipe,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatExpansionModule,
    MatProgressSpinnerModule,
    MatSnackBarModule
  ],
  templateUrl: './sale-list.html',
  styleUrl: './sale-list.css'
})
export class SaleList implements OnInit {

  sales: Sale[] = [];
  userId: string | null = null;

  loading = true;
  errorMessage = '';

  constructor(
    private saleService: SaleService,
    private auth: Auth,
    private snackBar: MatSnackBar,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.userId = this.auth.getUserId();

    if (!this.userId) {
      this.loading = false;
      this.errorMessage =
        'No hay un usuario autenticado.';

      this.snackBar.open(
        this.errorMessage,
        'Cerrar',
        {
          duration: 3000
        }
      );

      this.cdr.markForCheck();
      return;
    }

    this.loadSales();
  }

  loadSales(): void {
    if (!this.userId) {
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.saleService
      .getSales(this.userId)
      .subscribe({
        next: data => {
         this.sales = data;

          this.loading = false;
          this.cdr.markForCheck();
        },

        error: error => {
          console.error(
            'Error cargando ventas:',
            error
          );

          this.errorMessage =
            'No se pudo cargar el historial de compras.';

          this.loading = false;
          this.cdr.markForCheck();
        }
      });
  }

  getSaleNumber(id: number): string {
    return `#${id.toString().padStart(5, '0')}`;
  }

  getTotalSpent(): number {
    return this.sales.reduce(
      (total, sale) => total + sale.total,
      0
    );
  }

  getTotalUnits(): number {
    return this.sales.reduce(
      (total, sale) => total + sale.totalItems,
      0
    );
  }

  getDetailSubtotal(
    price: number,
    quantity: number
  ): number {
    return price * quantity;
  }

  getProductsLabel(totalItems: number): string {
    return totalItems === 1
      ? '1 producto'
      : `${totalItems} productos`;
  }
}