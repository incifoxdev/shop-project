import { Component } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';

import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

import { Auth } from '../../../core/services/auth';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule
  ],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {

  loginForm;


  constructor(
  private fb: FormBuilder,
  private auth: Auth,
  private router: Router
) {

    this.loginForm = this.fb.group({

      email: [
        '',
        [
          Validators.required,
          Validators.email
        ]
      ],

      password: [
        '',
        [
          Validators.required
        ]
      ]

    });

  }


  login(){

    if(this.loginForm.valid){

      this.auth.login(
        this.loginForm.value
      )
      .subscribe({

       next: response => {

  this.auth.saveSession(response);

  console.log(
    'Sesión guardada:',
    response
  );

  this.router.navigate(['/dashboard']);

},
        error: error => {

          console.error(
            'Error login:',
            error
          );

        }

      });

    }

  }

}
