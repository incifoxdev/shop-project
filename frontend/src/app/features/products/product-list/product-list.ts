import {
  ChangeDetectorRef,
  Component,
  OnInit
} from '@angular/core';

import {
  CommonModule,
  CurrencyPipe
} from '@angular/common';

import { FormsModule } from '@angular/forms';

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

import {
  MatFormFieldModule
} from '@angular/material/form-field';

import {
  MatInputModule
} from '@angular/material/input';

import {
  MatSelectModule
} from '@angular/material/select';

import {
  ProductService
} from '../../../core/services/product';

import {
  CartService
} from '../../../core/services/cart';

import {
  Auth
} from '../../../core/services/auth';

import {
  Product
} from '../../../core/models/product';

type StockFilter =
  | 'all'
  | 'available'
  | 'low'
  | 'out';

type SortOption =
  | 'name-asc'
  | 'name-desc'
  | 'price-asc'
  | 'price-desc'
  | 'stock-asc'
  | 'stock-desc';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    CurrencyPipe,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule
  ],
  templateUrl: './product-list.html',
  styleUrl: './product-list.css'
})
export class ProductList implements OnInit {

  products: Product[] = [];

  loading = true;
  errorMessage = '';

  addingProductId: string | null = null;

  quantities: Record<string, number> = {};

  searchTerm = '';

  stockFilter: StockFilter = 'all';

  sortOption: SortOption = 'name-asc';

  constructor(
    private readonly productService: ProductService,
    private readonly cartService: CartService,
    private readonly auth: Auth,
    private readonly snackBar: MatSnackBar,
    private readonly cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.loading = true;
    this.errorMessage = '';

    this.productService
      .getProducts()
      .subscribe({
        next: (data: Product[]) => {
          this.products = data;
          this.loading = false;

          this.initializeQuantities(data);

          this.cdr.markForCheck();
        },

        error: error => {
          console.error(
            'Error cargando productos:',
            error
          );

          this.errorMessage =
            'No se pudieron cargar los productos.';

          this.loading = false;

          this.cdr.markForCheck();
        }
      });
  }

  get filteredProducts(): Product[] {
    const search = this.searchTerm
      .trim()
      .toLowerCase();

    const filteredProducts =
      this.products.filter(
        (product: Product) => {
          const productName =
            product.name.toLowerCase();

          const matchesSearch =
            search === '' ||
            productName.includes(search);

          const matchesStock =
            this.matchesStockFilter(product);

          return (
            matchesSearch &&
            matchesStock
          );
        }
      );

    return [...filteredProducts]
      .sort(
        (
          firstProduct: Product,
          secondProduct: Product
        ) => {
          switch (this.sortOption) {

            case 'name-desc':
              return secondProduct.name.localeCompare(
                firstProduct.name
              );

            case 'price-asc':
              return (
                firstProduct.price -
                secondProduct.price
              );

            case 'price-desc':
              return (
                secondProduct.price -
                firstProduct.price
              );

            case 'stock-asc':
              return (
                firstProduct.stock -
                secondProduct.stock
              );

            case 'stock-desc':
              return (
                secondProduct.stock -
                firstProduct.stock
              );

            case 'name-asc':
            default:
              return firstProduct.name.localeCompare(
                secondProduct.name
              );
          }
        }
      );
  }

  private matchesStockFilter(
    product: Product
  ): boolean {
    switch (this.stockFilter) {

      case 'available':
        return product.stock > 0;

      case 'low':
        return (
          product.stock > 0 &&
          product.stock <= 5
        );

      case 'out':
        return product.stock === 0;

      case 'all':
      default:
        return true;
    }
  }

  private initializeQuantities(
    products: Product[]
  ): void {
    for (const product of products) {
      if (
        this.quantities[product.id] ===
        undefined
      ) {
        this.quantities[product.id] = 1;
      }
    }
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.stockFilter = 'all';
    this.sortOption = 'name-asc';
  }

  getQuantity(
    product: Product
  ): number {
    return (
      this.quantities[product.id] ?? 1
    );
  }

  increaseQuantity(
    product: Product
  ): void {
    const currentQuantity =
      this.getQuantity(product);

    if (product.stock <= 0) {
      return;
    }

    if (
      currentQuantity <
      product.stock
    ) {
      this.quantities[product.id] =
        currentQuantity + 1;

      this.cdr.markForCheck();
      return;
    }

    this.snackBar.open(
      `Solo hay ${product.stock} unidades disponibles.`,
      'Cerrar',
      {
        duration: 2500
      }
    );
  }

  decreaseQuantity(
    product: Product
  ): void {
    const currentQuantity =
      this.getQuantity(product);

    if (currentQuantity > 1) {
      this.quantities[product.id] =
        currentQuantity - 1;

      this.cdr.markForCheck();
    }
  }

  addToCart(
    product: Product
  ): void {
    if (!this.auth.isLoggedIn()) {
      this.snackBar.open(
        'No hay un usuario autenticado.',
        'Cerrar',
        {
          duration: 3000
        }
      );

      return;
    }

    if (product.stock <= 0) {
      this.snackBar.open(
        'Este producto no tiene stock disponible.',
        'Cerrar',
        {
          duration: 3000
        }
      );

      return;
    }

    const quantity =
      this.getQuantity(product);

    if (
      quantity < 1 ||
      quantity > product.stock
    ) {
      this.snackBar.open(
        'La cantidad seleccionada no es válida.',
        'Cerrar',
        {
          duration: 3000
        }
      );

      return;
    }

    const data = {
      productId: product.id,
      quantity
    };

    this.addingProductId =
      product.id;

    this.cdr.markForCheck();

    this.cartService
      .addProduct(data)
      .subscribe({
        next: () => {
          this.snackBar.open(
            quantity === 1
              ? `${product.name} fue agregado al carrito.`
              : `${quantity} unidades de ${product.name} fueron agregadas al carrito.`,
            'Cerrar',
            {
              duration: 3000
            }
          );

          this.quantities[product.id] =
            1;

          this.addingProductId =
            null;

          this.cdr.markForCheck();
        },

        error: error => {
          console.error(
            'Error agregando producto:',
            error
          );

          this.snackBar.open(
            error?.error?.message ??
              'No se pudo agregar el producto.',
            'Cerrar',
            {
              duration: 3000
            }
          );

          this.addingProductId =
            null;

          this.cdr.markForCheck();
        }
      });
  }

  trackByProductId(
    index: number,
    product: Product
  ): string {
    return product.id;
  }
}