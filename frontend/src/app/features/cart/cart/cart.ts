import {
  ChangeDetectorRef,
  Component,
  OnInit
} from '@angular/core';

import {
  CommonModule,
  CurrencyPipe
} from '@angular/common';

import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

import {
  MatSnackBar,
  MatSnackBarModule
} from '@angular/material/snack-bar';

import {
  MatProgressSpinnerModule
} from '@angular/material/progress-spinner';

import { Auth } from '../../../core/services/auth';
import { CartService } from '../../../core/services/cart';
import { SaleService } from '../../../core/services/sale';

import {
  Cart as CartModel,
  CartItem
} from '../../../core/models/cart';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [
    CommonModule,
    CurrencyPipe,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './cart.html',
  styleUrl: './cart.css'
})
export class Cart implements OnInit {

  userId: string | null = null;

  cart?: CartModel;

  loading = true;
  processingCheckout = false;
  clearingCart = false;

  errorMessage = '';

  updatingProductId: string | null = null;
  removingProductId: string | null = null;

  constructor(
    private cartService: CartService,
    private saleService: SaleService,
    private snackBar: MatSnackBar,
    private auth: Auth,
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

    this.loadCart();
  }

  loadCart(): void {
    if (!this.userId) {
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.cartService
      .getCart(this.userId)
      .subscribe({
        next: data => {
          console.log(
            'Carrito recibido:',
            data
          );

          this.cart = data;
          this.loading = false;

          this.cdr.markForCheck();
        },

        error: error => {
          console.error(
            'Error cargando carrito:',
            error
          );

          this.errorMessage =
            'No se pudo cargar el carrito de compras.';

          this.loading = false;

          this.cdr.markForCheck();
        }
      });
  }

  increaseQuantity(item: CartItem): void {
    const newQuantity = item.quantity + 1;

    this.updateQuantity(
      item,
      newQuantity
    );
  }

  decreaseQuantity(item: CartItem): void {
    if (item.quantity <= 1) {
      return;
    }

    const newQuantity = item.quantity - 1;

    this.updateQuantity(
      item,
      newQuantity
    );
  }

  updateQuantity(
    item: CartItem,
    quantity: number
  ): void {
    if (!this.userId) {
      return;
    }

    if (quantity < 1) {
      return;
    }

    const previousQuantity =
      item.quantity;

    this.updatingProductId =
      item.productId;

    /*
     * Actualización optimista:
     * la interfaz cambia inmediatamente.
     */
    item.quantity = quantity;
    item.subtotal =
      item.price * quantity;

    this.recalculateCartTotals();

    this.cdr.markForCheck();

    const data = {
      userId: this.userId,
      productId: item.productId,
      quantity
    };

    this.cartService
      .updateQuantity(data)
      .subscribe({
        next: updatedCart => {
          this.cart = updatedCart;

          this.updatingProductId = null;

          this.cdr.markForCheck();
        },

        error: error => {
          console.error(
            'Error actualizando cantidad:',
            error
          );

          /*
           * Si falla el backend, restauramos
           * la cantidad anterior.
           */
          item.quantity =
            previousQuantity;

          item.subtotal =
            item.price * previousQuantity;

          this.recalculateCartTotals();

          this.updatingProductId = null;

          this.snackBar.open(
            this.getBackendErrorMessage(
              error,
              'No se pudo actualizar la cantidad.'
            ),
            'Cerrar',
            {
              duration: 3500
            }
          );

          this.cdr.markForCheck();
        }
      });
  }

  removeProduct(item: CartItem): void {
    if (!this.userId) {
      return;
    }

    this.removingProductId =
      item.productId;

    this.cdr.markForCheck();

    this.cartService
      .removeProduct(
        this.userId,
        item.productId
      )
      .subscribe({
        next: updatedCart => {
          this.cart = updatedCart;

          this.removingProductId = null;

          this.snackBar.open(
            `${item.productName} fue eliminado del carrito.`,
            'Cerrar',
            {
              duration: 3000
            }
          );

          this.cdr.markForCheck();
        },

        error: error => {
          console.error(
            'Error eliminando producto:',
            error
          );

          this.removingProductId = null;

          this.snackBar.open(
            'No se pudo eliminar el producto.',
            'Cerrar',
            {
              duration: 3000
            }
          );

          this.cdr.markForCheck();
        }
      });
  }

  clearCart(): void {
    if (
      !this.userId ||
      !this.cart ||
      this.cart.totalItems === 0
    ) {
      return;
    }

    const confirmed = window.confirm(
      '¿Deseas eliminar todos los productos del carrito?'
    );

    if (!confirmed) {
      return;
    }

    this.clearingCart = true;

    this.cdr.markForCheck();

    this.cartService
      .clearCart(this.userId)
      .subscribe({
        next: () => {
          this.clearingCart = false;

          if (this.cart) {
            this.cart = {
              ...this.cart,
              totalItems: 0,
              totalAmount: 0,
              items: []
            };
          }

          this.snackBar.open(
            'El carrito fue vaciado.',
            'Cerrar',
            {
              duration: 3000
            }
          );

          this.cdr.markForCheck();
        },

        error: error => {
          console.error(
            'Error vaciando carrito:',
            error
          );

          this.clearingCart = false;

          this.snackBar.open(
            'No se pudo vaciar el carrito.',
            'Cerrar',
            {
              duration: 3000
            }
          );

          this.cdr.markForCheck();
        }
      });
  }

  checkout(): void {
    if (!this.userId) {
      this.snackBar.open(
        'No hay un usuario autenticado.',
        'Cerrar',
        {
          duration: 3000
        }
      );

      return;
    }

    if (
      !this.cart ||
      this.cart.totalItems === 0
    ) {
      this.snackBar.open(
        'El carrito está vacío.',
        'Cerrar',
        {
          duration: 3000
        }
      );

      return;
    }

    const confirmed = window.confirm(
      `¿Confirmas la compra por ${this.formatAmount(
        this.cart.totalAmount
      )}?`
    );

    if (!confirmed) {
      return;
    }

    this.processingCheckout = true;

    this.cdr.markForCheck();

    this.saleService
      .checkout(this.userId)
      .subscribe({
        next: () => {
          this.processingCheckout = false;

          this.snackBar.open(
            'Compra realizada correctamente.',
            'Cerrar',
            {
              duration: 3500
            }
          );

          this.cdr.markForCheck();

          this.loadCart();
        },

        error: error => {
          console.error(
            'Error realizando compra:',
            error
          );

          this.processingCheckout = false;

          this.snackBar.open(
            this.getBackendErrorMessage(
              error,
              'No se pudo realizar la compra.'
            ),
            'Cerrar',
            {
              duration: 3500
            }
          );

          this.cdr.markForCheck();
        }
      });
  }

  getSubtotal(
    price: number,
    quantity: number
  ): number {
    return price * quantity;
  }

  isUpdating(item: CartItem): boolean {
    return (
      this.updatingProductId ===
      item.productId
    );
  }

  isRemoving(item: CartItem): boolean {
    return (
      this.removingProductId ===
      item.productId
    );
  }

  isCartActionInProgress(): boolean {
    return (
      this.processingCheckout ||
      this.clearingCart ||
      this.updatingProductId !== null ||
      this.removingProductId !== null
    );
  }

  private recalculateCartTotals(): void {
    if (!this.cart) {
      return;
    }

    this.cart.totalItems =
      this.cart.items.reduce(
        (total, item) =>
          total + item.quantity,
        0
      );

    this.cart.totalAmount =
      this.cart.items.reduce(
        (total, item) =>
          total +
          item.price * item.quantity,
        0
      );
  }

  private getBackendErrorMessage(
    error: any,
    defaultMessage: string
  ): string {
    return (
      error?.error?.message ||
      error?.error?.error ||
      defaultMessage
    );
  }

  private formatAmount(
    amount: number
  ): string {
    return new Intl.NumberFormat(
      'es-ES',
      {
        style: 'currency',
        currency: 'USD'
      }
    ).format(amount);
  }
}