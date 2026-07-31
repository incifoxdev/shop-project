import {
  CommonModule,
  DatePipe
} from '@angular/common';

import {
  HttpErrorResponse
} from '@angular/common/http';

import {
  ChangeDetectorRef,
  Component,
  OnInit
} from '@angular/core';

import {
  FormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';


import {
  finalize
} from 'rxjs';

import {
  MatButtonModule
} from '@angular/material/button';

import {
  MatCardModule
} from '@angular/material/card';

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
  MatProgressSpinnerModule
} from '@angular/material/progress-spinner';

import {
  MatSlideToggleModule
} from '@angular/material/slide-toggle';

import {
  MatSnackBar,
  MatSnackBarModule
} from '@angular/material/snack-bar';

import {
  MatTableModule
} from '@angular/material/table';

import {
  CreateUserRequest,
  UpdateUserRequest,
  User
} from '../../../../core/models/user';

import {
  UserService
} from '../../../../core/services/user';

interface UserFormValue {
  name: string;
  email: string;
  password: string;
  enabled: boolean;
}

@Component({
  selector: 'app-admin-users',
  standalone: true,
 imports: [
    CommonModule,
    DatePipe,
    ReactiveFormsModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatProgressSpinnerModule,
    MatSlideToggleModule,
    MatSnackBarModule,
    MatTableModule
  ],
  templateUrl: './admin-users.html',
  styleUrl: './admin-users.css'
})
export class AdminUsers implements OnInit {

  users: User[] = [];

  readonly displayedColumns: string[] = [
    'name',
    'email',
    'roles',
    'enabled',
    'createdAt',
    'actions'
  ];

  loading = false;
  saving = false;
  deletingUserId: string | null = null;

  editingUserId: string | null = null;

  readonly userForm;

constructor(
  private readonly fb: FormBuilder,
  private readonly userService: UserService,
  private readonly snackBar: MatSnackBar,
  private readonly cdr: ChangeDetectorRef
) {
  this.userForm = this.fb.nonNullable.group({
    name: [
      '',
      [
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(100)
      ]
    ],
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
        Validators.minLength(6),
        Validators.maxLength(100)
      ]
    ],
    enabled: [true]
  });
}

  ngOnInit(): void {
    this.loadUsers();
  }

  get isEditing(): boolean {
    return this.editingUserId !== null;
  }

loadUsers(): void {
  if (this.loading) {
    return;
  }

  this.loading = true;
  this.cdr.markForCheck();

  this.userService
    .getAll()
    .pipe(
      finalize(() => {
        this.loading = false;
        this.cdr.markForCheck();
      })
    )
    .subscribe({
      next: (users: User[]) => {
        this.users = [...users];
        this.cdr.markForCheck();
      },

      error: (error: HttpErrorResponse) => {
        console.error(
          'Error cargando usuarios:',
          error
        );

        this.showMessage(
          this.getBackendError(
            error,
            'No se pudo cargar la lista de usuarios.'
          )
        );

        this.cdr.markForCheck();
      }
    });
}
  saveUser(): void {
    if (this.saving) {
      return;
    }

    if (this.userForm.invalid) {
      this.userForm.markAllAsTouched();
      return;
    }

    const formValue = this.userForm.getRawValue();

    if (
      !this.isEditing &&
      !formValue.password.trim()
    ) {
      this.userForm.controls.password.setErrors({
        required: true
      });

      this.userForm.controls.password.markAsTouched();

      return;
    }

    this.saving = true;

    if (this.editingUserId) {
      this.updateUser(
        this.editingUserId,
        formValue
      );

      return;
    }

    this.createUser(formValue);
  }

  editUser(user: User): void {
    if (this.saving) {
      return;
    }

    this.editingUserId = user.id;

    this.userForm.reset({
      name: user.name,
      email: user.email,
      password: '',
      enabled: user.enabled
    });

    this.userForm.markAsPristine();
    this.userForm.markAsUntouched();

    window.scrollTo({
      top: 0,
      behavior: 'smooth'
    });
  }

  cancelEdit(): void {
    this.editingUserId = null;

    this.resetForm();
  }

  deleteUser(user: User): void {
    if (
      this.deletingUserId !== null ||
      this.saving
    ) {
      return;
    }

    const confirmed = window.confirm(
      `¿Deseas eliminar al usuario "${user.name}"? Esta acción no se puede deshacer.`
    );

    if (!confirmed) {
      return;
    }

    this.deletingUserId = user.id;

    this.userService
      .delete(user.id)
      .pipe(
        finalize(() => {
          this.deletingUserId = null;
        })
      )
      .subscribe({
        next: () => {
          this.users = this.users.filter(
  (currentUser: User) =>
    currentUser.id !== user.id
);

          if (this.editingUserId === user.id) {
            this.cancelEdit();
          }

          this.showMessage(
            'Usuario eliminado correctamente.'
          );
        },

        error: (error: HttpErrorResponse) => {
          console.error(
            'Error eliminando usuario:',
            error
          );

          this.showMessage(
            this.getBackendError(
              error,
              'No se pudo eliminar el usuario.'
            )
          );
        }
      });
  }

  getRolesLabel(roles: string[] | null | undefined): string {
    if (!roles?.length) {
      return 'Sin rol';
    }

    return roles
      .map((role: string) => {
        switch (role) {
          case 'ROLE_ADMIN':
            return 'Administrador';

          case 'ROLE_USER':
            return 'Usuario';

          default:
            return role.replace('ROLE_', '');
        }
      })
      .join(', ');
  }

  isDeleting(userId: string): boolean {
    return this.deletingUserId === userId;
  }

  private createUser(
    formValue: UserFormValue
  ): void {
    const request: CreateUserRequest = {
      name: formValue.name.trim(),
      email: formValue.email.trim().toLowerCase(),
      password: formValue.password.trim()
    };

    this.userService
      .create(request)
      .pipe(
        finalize(() => {
          this.saving = false;
        })
      )
      .subscribe({
        next: (createdUser: User) => {
          this.users = [
            ...this.users,
            createdUser
          ];

          this.resetForm();

          this.showMessage(
            'Usuario creado correctamente.'
          );
        },

        error: (error: HttpErrorResponse) => {
          console.error(
            'Error creando usuario:',
            error
          );

          this.showMessage(
            this.getBackendError(
              error,
              'No se pudo crear el usuario.'
            )
          );
        }
      });
  }

  private updateUser(
    id: string,
    formValue: UserFormValue
  ): void {
    const request: UpdateUserRequest = {
      name: formValue.name.trim(),
      email: formValue.email.trim().toLowerCase(),
      enabled: formValue.enabled
    };

    const password = formValue.password.trim();

    if (password) {
      request.password = password;
    }

    this.userService
      .update(id, request)
      .pipe(
        finalize(() => {
          this.saving = false;
        })
      )
      .subscribe({
        next: (updatedUser: User) => {
  this.users = this.users.map((user: User) =>
            user.id === updatedUser.id
              ? updatedUser
              : user
          );

          this.editingUserId = null;

          this.resetForm();

          this.showMessage(
            'Usuario actualizado correctamente.'
          );
        },

        error: (error: HttpErrorResponse) => {
          console.error(
            'Error actualizando usuario:',
            error
          );

          this.showMessage(
            this.getBackendError(
              error,
              'No se pudo actualizar el usuario.'
            )
          );
        }
      });
  }

  private resetForm(): void {
    this.userForm.reset({
      name: '',
      email: '',
      password: '',
      enabled: true
    });

    this.userForm.controls.password.setErrors(
      null
    );

    this.userForm.markAsPristine();
    this.userForm.markAsUntouched();
  }

  private showMessage(message: string): void {
    this.snackBar.open(
      message,
      'Cerrar',
      {
        duration: 3500,
        horizontalPosition: 'end',
        verticalPosition: 'top'
      }
    );
  }

  private getBackendError(
    error: HttpErrorResponse,
    fallback: string
  ): string {
    const response = error.error;

    if (
      response &&
      typeof response === 'object'
    ) {
      if (
        typeof response.message === 'string' &&
        response.message.trim()
      ) {
        return response.message;
      }

      if (
        typeof response.error === 'string' &&
        response.error.trim()
      ) {
        return response.error;
      }

      const validationMessage =
        this.getValidationError(response);

      if (validationMessage) {
        return validationMessage;
      }
    }

    if (
      typeof response === 'string' &&
      response.trim()
    ) {
      return response;
    }

    if (error.status === 0) {
      return 'No se pudo conectar con el servidor.';
    }

    if (error.status === 401) {
      return 'Tu sesión no es válida o ha expirado.';
    }

    if (error.status === 403) {
      return 'No tienes permisos para realizar esta acción.';
    }

    if (error.status === 404) {
      return 'El usuario no fue encontrado.';
    }

    if (error.status === 409) {
      return 'Ya existe un usuario con ese correo electrónico.';
    }

    return fallback;
  }

  private getValidationError(
    response: Record<string, unknown>
  ): string | null {
    const errors = response['errors'];

    if (!errors) {
      return null;
    }

    if (Array.isArray(errors)) {
      const firstError = errors[0];

      if (typeof firstError === 'string') {
        return firstError;
      }

      if (
        firstError &&
        typeof firstError === 'object' &&
        'message' in firstError &&
        typeof firstError.message === 'string'
      ) {
        return firstError.message;
      }
    }

    if (
      typeof errors === 'object' &&
      errors !== null
    ) {
      const firstValue = Object.values(errors)[0];

      if (typeof firstValue === 'string') {
        return firstValue;
      }

      if (
        Array.isArray(firstValue) &&
        typeof firstValue[0] === 'string'
      ) {
        return firstValue[0];
      }
    }

    return null;
  }
}