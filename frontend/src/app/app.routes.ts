import { Routes } from '@angular/router';

import { MainLayout } from './shared/layout/main-layout/main-layout';

import { authGuard } from './core/guards/auth-guard';
import { adminGuard } from './core/guards/admin-guard';

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
        path: '',
        redirectTo: 'products',
        pathMatch: 'full'
      },

      {
        path: 'dashboard',
        canActivate: [adminGuard],
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
        canActivate: [adminGuard],
        loadComponent: () =>
          import('./features/admin/products/admin-products')
            .then(m => m.AdminProducts)
      },

      {
        path: 'admin/users',
        canActivate: [adminGuard],
        loadComponent: () =>
          import('./features/admin/users/admin-users/admin-users')
            .then(m => m.AdminUsers)
      },

      {
        path: 'cart',
        loadComponent: () =>
          import('./features/cart/cart/cart')
            .then(m => m.Cart)
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
    path: '**',
    redirectTo: ''
  }
];