import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

import { Router, RouterLink, RouterOutlet } from '@angular/router';

import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';

import { Auth } from '../../core/services/auth';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    RouterLink,
    MatToolbarModule,
    MatButtonModule
  ],
  templateUrl: './layout.html',
  styleUrl: './layout.css',
})
export class Layout {

  constructor(
    private auth: Auth,
    private router: Router
  ) {}

  get email() {

    return this.auth.getEmail();

  }

  logout() {

    this.auth.logout();

    this.router.navigate(['/login']);

  }

}
