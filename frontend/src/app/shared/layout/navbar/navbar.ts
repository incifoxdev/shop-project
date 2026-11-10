import { Component } from '@angular/core';
import { Router } from '@angular/router';

import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';

import { Auth } from '../../../core/services/auth';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    MatToolbarModule,
    MatButtonModule
  ],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class Navbar {

  constructor(
    public readonly auth: Auth,
    private readonly router: Router
  ) {}

  get name(): string {
    return this.auth.currentUser()?.name ?? '';
  }

  get email(): string {
    return this.auth.currentUser()?.email ?? '';
  }

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }

}
