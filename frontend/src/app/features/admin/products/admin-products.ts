import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  OnInit,
  ViewChild
} from '@angular/core';
import {
  ConfirmDeleteDialog
} from '../confirm-delete-dialog/confirm-delete-dialog';
import {
  CommonModule,
  CurrencyPipe
} from '@angular/common';

import {
  MatButtonModule
} from '@angular/material/button';

import {
  MatCardModule
} from '@angular/material/card';

import {
  MatDialog,
  MatDialogModule
} from '@angular/material/dialog';

import {
  MatFormFieldModule
} from '@angular/material/form-field';

import {
  MatIconModule
} from '@angular/material/icon';

import {
  MatInputModule
} from '@angular/material/input';

import {
  MatPaginator,
  MatPaginatorModule
} from '@angular/material/paginator';

import {
  MatProgressSpinnerModule
} from '@angular/material/progress-spinner';

import {
  MatSnackBar,
  MatSnackBarModule
} from '@angular/material/snack-bar';

import {
  MatSort,
  MatSortModule
} from '@angular/material/sort';

import {
  MatTableDataSource,
  MatTableModule
} from '@angular/material/table';

import {
  MatTooltipModule
} from '@angular/material/tooltip';

import {
  Product
} from '../../../core/models/product';

import {
  ProductService
} from '../../../core/services/product';

import {
  ProductDialog,
  ProductDialogResult
} from '../product-dialog/product-dialog';

@Component({
  selector: 'app-admin-products',
  standalone: true,
  imports: [
    CommonModule,
    CurrencyPipe,
    MatButtonModule,
    MatCardModule,
    MatDialogModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatSortModule,
    MatTableModule,
    MatTooltipModule
  ],
  templateUrl: './admin-products.html',
  styleUrl: './admin-products.css'
})
export class AdminProducts implements OnInit, AfterViewInit {

  displayedColumns: string[] = [
    'name',
    'price',
    'stock',
    'actions'
  ];

  dataSource = new MatTableDataSource<Product>([]);

  loading = false;

  errorMessage = '';

  @ViewChild(MatPaginator)
  paginator!: MatPaginator;

  @ViewChild(MatSort)
  sort!: MatSort;

  constructor(
    private productService: ProductService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog,
    private cdr: ChangeDetectorRef
  ) {
    this.dataSource.filterPredicate = (
      product: Product,
      filter: string
    ): boolean => {
      const searchValue = filter
        .trim()
        .toLowerCase();

      return (
        product.name
          .toLowerCase()
          .includes(searchValue) ||

        product.price
          .toString()
          .includes(searchValue) ||

        product.stock
          .toString()
          .includes(searchValue)
      );
    };
  }

  ngOnInit(): void {
    this.loadProducts();
  }

  ngAfterViewInit(): void {
    this.connectTableControls();
  }

  private connectTableControls(): void {
    if (this.paginator) {
      this.dataSource.paginator = this.paginator;
    }

    if (this.sort) {
      this.dataSource.sort = this.sort;
    }
  }

  loadProducts(): void {
    this.loading = true;
    this.errorMessage = '';

    this.productService
      .getProducts()
      .subscribe({
        next: products => {
          this.dataSource.data = products;

          this.loading = false;

          this.connectTableControls();

          this.cdr.detectChanges();
        },

        error: error => {
          console.error(
            'Error cargando productos:',
            error
          );

          this.errorMessage =
            'No se pudieron cargar los productos.';

          this.loading = false;

          this.cdr.detectChanges();
        }
      });
  }

  applyFilter(event: Event): void {
    const input =
      event.target as HTMLInputElement;

    this.dataSource.filter =
      input.value.trim().toLowerCase();

    this.dataSource.paginator
      ?.firstPage();
  }

  clearFilter(input: HTMLInputElement): void {
    input.value = '';

    this.dataSource.filter = '';

    this.dataSource.paginator
      ?.firstPage();
  }

  createProduct(): void {
    const dialogRef = this.dialog.open(
      ProductDialog,
      {
        width: '560px',
        maxWidth: '95vw',
        disableClose: true,
        data: {}
      }
    );

    dialogRef
      .afterClosed()
      .subscribe(
        (result?: ProductDialogResult) => {
          if (!result) {
            return;
          }

          this.productService
            .createProduct(result)
            .subscribe({
              next: createdProduct => {
                this.dataSource.data = [
                  ...this.dataSource.data,
                  createdProduct
                ];

                this.snackBar.open(
                  'Producto creado correctamente.',
                  'Cerrar',
                  {
                    duration: 3000
                  }
                );

                this.cdr.detectChanges();
              },

              error: error => {
                console.error(
                  'Error creando producto:',
                  error
                );

                this.snackBar.open(
                  'No se pudo crear el producto.',
                  'Cerrar',
                  {
                    duration: 3000
                  }
                );
              }
            });
        }
      );
  }

  editProduct(product: Product): void {
    const dialogRef = this.dialog.open(
      ProductDialog,
      {
        width: '560px',
        maxWidth: '95vw',
        disableClose: true,
        data: {
          product
        }
      }
    );

    dialogRef
      .afterClosed()
      .subscribe(
        (result?: ProductDialogResult) => {
          if (!result) {
            return;
          }

          this.productService
            .updateProduct(
              product.id,
              result
            )
            .subscribe({
              next: updatedProduct => {
                this.dataSource.data =
                  this.dataSource.data.map(
                    currentProduct =>
                      currentProduct.id ===
                      updatedProduct.id
                        ? updatedProduct
                        : currentProduct
                  );

                this.snackBar.open(
                  'Producto actualizado correctamente.',
                  'Cerrar',
                  {
                    duration: 3000
                  }
                );

                this.cdr.detectChanges();
              },

              error: error => {
                console.error(
                  'Error actualizando producto:',
                  error
                );

                this.snackBar.open(
                  'No se pudo actualizar el producto.',
                  'Cerrar',
                  {
                    duration: 3000
                  }
                );
              }
            });
        }
      );
  }

deleteProduct(product: Product): void {
  const dialogRef = this.dialog.open(
    ConfirmDeleteDialog,
    {
      width: '440px',
      maxWidth: '95vw',
      disableClose: true,
      data: {
        title: 'Eliminar producto',
        message:
          `¿Deseas eliminar el producto "${product.name}"?`
      }
    }
  );

  dialogRef
    .afterClosed()
    .subscribe(
      (confirmed: boolean | undefined) => {
        if (confirmed !== true) {
          return;
        }

        this.productService
          .deleteProduct(product.id)
          .subscribe({
            next: () => {
              this.dataSource.data =
                this.dataSource.data.filter(
                  currentProduct =>
                    currentProduct.id !== product.id
                );

              this.snackBar.open(
                'Producto eliminado correctamente.',
                'Cerrar',
                {
                  duration: 3000
                }
              );

              this.cdr.detectChanges();
            },

            error: error => {
              console.error(
                'Error eliminando producto:',
                error
              );

              let message =
                'No se pudo eliminar el producto.';

              if (error.status === 403) {
                message =
                  'No tienes permisos para eliminar productos.';
              } else if (error.status === 404) {
                message =
                  'El producto ya no existe.';
              } else if (
                error.status === 409 ||
                error.status === 500
              ) {
                message =
                  'No se puede eliminar porque el producto está asociado a un carrito o una venta.';
              }

              this.snackBar.open(
                message,
                'Cerrar',
                {
                  duration: 5000
                }
              );
            }
          });
      }
    );
}
}