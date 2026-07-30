import { Routes } from '@angular/router';
import { MainLayout } from './shared/layout/main-layout/main-layout';
import { authGuard } from './core/guards/auth-guard';

export const routes: Routes = [

  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login/login')
        .then(m => m.Login)
  },

  {
    path: '',
    component: MainLayout,
    canActivate: [authGuard],
    children: [

      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/dashboard/dashboard')
            .then(m => m.Dashboard)
      },

      {
        path: 'products',
        loadComponent: () =>
          import('./features/products/product-list/product-list')
            .then(m => m.ProductList)
      },

     {
  path: 'admin/products',
  loadComponent: () =>
    import('./features/admin/products/admin-products')
      .then(m => m.AdminProducts)
},

      {
        path: 'cart',
        loadComponent: () =>
          import('./features/cart/cart/cart')
            .then(m => m.Cart)
      },

      {
        path: 'checkout',
        loadComponent: () =>
          import('./features/checkout/checkout/checkout')
            .then(m => m.Checkout)
      },

      {
        path: 'sales',
        loadComponent: () =>
          import('./features/sales/sale-list/sale-list')
            .then(m => m.SaleList)
      }

    ]
  },

  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },

  {
    path: '**',
    redirectTo: 'login'
  }

];