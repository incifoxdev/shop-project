import {
  ChangeDetectorRef,
  Component,
  OnInit
} from '@angular/core';

import {
  CurrencyPipe,
  DatePipe
} from '@angular/common';

import { HttpErrorResponse } from '@angular/common/http';

import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

import {
  MatSnackBar,
  MatSnackBarModule
} from '@angular/material/snack-bar';

import {
  Observable,
  finalize
} from 'rxjs';

import { Auth } from '../../../core/services/auth';
import { SaleService } from '../../../core/services/sale';
import { Sale } from '../../../core/models/sale';

@Component({
  selector: 'app-sale-list',
  standalone: true,
  imports: [
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

  loading = true;
  errorMessage = '';

  constructor(
    private readonly saleService: SaleService,
    public readonly auth: Auth,
    private readonly snackBar: MatSnackBar,
    private readonly cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadSales();
  }

  loadSales(): void {
    if (!this.auth.isLoggedIn()) {
      this.handleMissingUser();
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    const salesRequest: Observable<Sale[]> =
      this.auth.isAdmin()
        ? this.saleService.getAllSales()
        : this.saleService.getMySales();

    salesRequest
      .pipe(
        finalize(() => {
          this.loading = false;
          this.cdr.markForCheck();
        })
      )
      .subscribe({
        next: (sales: Sale[]) => {
          this.sales = sales;
          this.cdr.markForCheck();
        },

        error: (error: HttpErrorResponse) => {
          console.error(
            'Error cargando ventas:',
            error
          );

          this.sales = [];

          this.errorMessage = this.auth.isAdmin()
            ? 'No se pudo cargar el listado de ventas.'
            : 'No se pudo cargar tu historial de compras.';

          this.snackBar.open(
            this.errorMessage,
            'Cerrar',
            {
              duration: 3000
            }
          );

          this.cdr.markForCheck();
        }
      });
  }

  get pageTitle(): string {
    return this.auth.isAdmin()
      ? 'Ventas'
      : 'Mis compras';
  }

  get emptyMessage(): string {
    return this.auth.isAdmin()
      ? 'Todavía no se han registrado ventas.'
      : 'Todavía no has realizado compras.';
  }

  getSaleNumber(id: number): string {
    return `#${id
      .toString()
      .padStart(5, '0')}`;
  }

  getTotalSpent(): number {
    return this.sales.reduce(
      (
        total: number,
        sale: Sale
      ) => total + sale.total,
      0
    );
  }

  getTotalUnits(): number {
    return this.sales.reduce(
      (
        total: number,
        sale: Sale
      ) => total + sale.totalItems,
      0
    );
  }

  getDetailSubtotal(
    price: number,
    quantity: number
  ): number {
    return price * quantity;
  }

  getProductsLabel(
    totalItems: number
  ): string {
    return totalItems === 1
      ? '1 producto'
      : `${totalItems} productos`;
  }

  private handleMissingUser(): void {
    this.sales = [];
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
  }
}