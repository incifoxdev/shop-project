import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  ElementRef,
  OnDestroy,
  OnInit,
  ViewChild
} from '@angular/core';

import {
  CurrencyPipe,
  DatePipe
} from '@angular/common';

import { forkJoin } from 'rxjs';

import Chart from 'chart.js/auto';

import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';

import { Auth } from '../../core/services/auth';
import { ProductService } from '../../core/services/product';
import { SaleService } from '../../core/services/sale';
import { DashboardService } from '../../core/services/dashboard';

import { Product } from '../../core/models/product';
import { Sale } from '../../core/models/sale';
import { DashboardStats } from '../../core/models/dashboard-stats';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CurrencyPipe,
    DatePipe,
    MatCardModule,
    MatIconModule
  ],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard
  implements OnInit, AfterViewInit, OnDestroy {

  @ViewChild('salesChart')
  salesChartCanvas?: ElementRef<HTMLCanvasElement>;

  @ViewChild('topProductsChart')
  topProductsChartCanvas?: ElementRef<HTMLCanvasElement>;

  stats?: DashboardStats;

  totalProducts = 0;
  totalSales = 0;

  salesToday = 0;
  salesThisMonth = 0;
  lowStockProducts = 0;

  lowStockProductList: Product[] = [];
  recentSales: Sale[] = [];

  loading = true;
  errorMessage = '';

  private salesChart?: Chart<'bar', number[], string>;

  private topProductsChart?: Chart<
    'bar',
    number[],
    string
  >;

  private viewInitialized = false;

  constructor(
    private auth: Auth,
    private productService: ProductService,
    private saleService: SaleService,
    private dashboardService: DashboardService,
    private cdr: ChangeDetectorRef
  ) {}

  get email(): string {
    return this.auth.getEmail() ?? 'Usuario';
  }

  ngOnInit(): void {
    const userId = this.auth.getUserId();

    if (!userId) {
      this.loading = false;

      this.errorMessage =
        'No se encontró el usuario autenticado.';

      this.cdr.markForCheck();
      return;
    }

    this.loadDashboard(userId);
  }

  ngAfterViewInit(): void {
    this.viewInitialized = true;

    this.createCharts();
  }

  ngOnDestroy(): void {
    this.salesChart?.destroy();
    this.topProductsChart?.destroy();
  }

  private loadDashboard(userId: string): void {
    this.loading = true;
    this.errorMessage = '';

    forkJoin({
      products:
        this.productService.getProducts(),

      sales:
        this.saleService.getSales(userId),

      stats:
        this.dashboardService.getStats()
    }).subscribe({
      next: ({ products, sales, stats }) => {
        this.stats = stats;

        this.totalProducts =
          stats.totalProducts;

        this.totalSales =
          stats.totalSales;

        const today = new Date();

        this.salesToday = sales
          .filter(sale => {
            const saleDate =
              new Date(sale.createdAt);

            return this.isSameDay(
              saleDate,
              today
            );
          })
          .length;

        this.salesThisMonth = sales
          .filter(sale => {
            const saleDate =
              new Date(sale.createdAt);

            return (
              saleDate.getMonth() ===
                today.getMonth()
              &&
              saleDate.getFullYear() ===
                today.getFullYear()
            );
          })
          .reduce(
            (total, sale) =>
              total + sale.total,
            0
          );

        this.lowStockProductList = products
          .filter(product =>
            product.stock <= 5
          )
          .sort(
            (firstProduct, secondProduct) =>
              firstProduct.stock -
              secondProduct.stock
          );

        this.lowStockProducts =
          this.lowStockProductList.length;

        this.recentSales = [...sales]
          .sort(
            (firstSale, secondSale) =>
              new Date(
                secondSale.createdAt
              ).getTime()
              -
              new Date(
                firstSale.createdAt
              ).getTime()
          )
          .slice(0, 5);

        this.loading = false;

        /*
         * Los canvas están dentro de un @if.
         * detectChanges() permite que Angular los
         * agregue al DOM antes de crear los gráficos.
         */
        this.cdr.detectChanges();

        this.createCharts();
      },

      error: error => {
        console.error(
          'Error cargando dashboard:',
          error
        );

        this.errorMessage =
          'No se pudieron cargar los datos del dashboard.';

        this.loading = false;

        this.cdr.markForCheck();
      }
    });
  }

  private createCharts(): void {
    this.createSalesChart();
    this.createTopProductsChart();
  }

  private createSalesChart(): void {
    if (
      !this.viewInitialized ||
      this.loading ||
      !this.stats ||
      !this.salesChartCanvas
    ) {
      return;
    }

    this.salesChart?.destroy();

    const labels =
      this.stats.salesPerMonth.map(
        monthlySale =>
          monthlySale.month
      );

    const values =
      this.stats.salesPerMonth.map(
        monthlySale =>
          monthlySale.total
      );

    this.salesChart = new Chart(
      this.salesChartCanvas.nativeElement,
      {
        type: 'bar',

        data: {
          labels,

          datasets: [
            {
              label: 'Ingresos',
              data: values,
              borderWidth: 1,
              borderRadius: 8,
              borderSkipped: false
            }
          ]
        },

        options: {
          responsive: true,
          maintainAspectRatio: false,

          interaction: {
            mode: 'index',
            intersect: false
          },

          plugins: {
            legend: {
              display: false
            },

            tooltip: {
              callbacks: {
                label: context => {
                  const value =
                    Number(context.raw ?? 0);

                  return `Ingresos: ${
                    this.formatCurrency(value)
                  }`;
                }
              }
            }
          },

          scales: {
            x: {
              grid: {
                display: false
              },

              ticks: {
                maxRotation: 0,
                minRotation: 0
              }
            },

            y: {
              beginAtZero: true,

              ticks: {
                callback: value =>
                  this.formatCompactCurrency(
                    Number(value)
                  )
              }
            }
          }
        }
      }
    );
  }

  private createTopProductsChart(): void {
    if (
      !this.viewInitialized ||
      this.loading ||
      !this.stats ||
      !this.topProductsChartCanvas
    ) {
      return;
    }

    this.topProductsChart?.destroy();

    const labels =
      this.stats.topProducts.map(
        product =>
          product.productName
      );

    const values =
      this.stats.topProducts.map(
        product =>
          product.quantitySold
      );

    this.topProductsChart = new Chart(
      this.topProductsChartCanvas.nativeElement,
      {
        type: 'bar',

        data: {
          labels,

          datasets: [
            {
              label: 'Unidades vendidas',
              data: values,
              borderWidth: 1,
              borderRadius: 8,
              borderSkipped: false
            }
          ]
        },

        options: {
          indexAxis: 'y',

          responsive: true,
          maintainAspectRatio: false,

          interaction: {
            mode: 'index',
            intersect: false
          },

          plugins: {
            legend: {
              display: false
            },

            tooltip: {
              callbacks: {
                label: context => {
                  const quantity =
                    Number(context.raw ?? 0);

                  const product =
                    this.stats?.topProducts[
                      context.dataIndex
                    ];

                  if (!product) {
                    return `Unidades vendidas: ${quantity}`;
                  }

                  return [
                    `Unidades vendidas: ${quantity}`,
                    `Ingresos: ${
                      this.formatCurrency(
                        product.revenue
                      )
                    }`
                  ];
                }
              }
            }
          },

          scales: {
            x: {
              beginAtZero: true,

              ticks: {
                precision: 0,
                stepSize: 1
              },

              title: {
                display: true,
                text: 'Unidades vendidas'
              }
            },

            y: {
              grid: {
                display: false
              },

              ticks: {
                autoSkip: false
              }
            }
          }
        }
      }
    );
  }

  private formatCurrency(
    value: number
  ): string {
    return new Intl.NumberFormat(
      'es-ES',
      {
        style: 'currency',
        currency: 'USD',
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      }
    ).format(value);
  }

  private formatCompactCurrency(
    value: number
  ): string {
    return new Intl.NumberFormat(
      'es-ES',
      {
        style: 'currency',
        currency: 'USD',
        notation: 'compact',
        maximumFractionDigits: 1
      }
    ).format(value);
  }

  private isSameDay(
    firstDate: Date,
    secondDate: Date
  ): boolean {
    return (
      firstDate.getDate() ===
        secondDate.getDate()
      &&
      firstDate.getMonth() ===
        secondDate.getMonth()
      &&
      firstDate.getFullYear() ===
        secondDate.getFullYear()
    );
  }

  getSaleNumber(id: number): string {
    return `#${id
      .toString()
      .padStart(5, '0')}`;
  }
  get topProduct() {
  return this.stats?.topProducts?.[0];
}

get hasTopProduct(): boolean {
  return !!this.topProduct;
}
}