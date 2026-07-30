import {
  Component,
  Inject
} from '@angular/core';

import {
  CommonModule
} from '@angular/common';

import {
  FormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef
} from '@angular/material/dialog';

import {
  MatFormFieldModule
} from '@angular/material/form-field';

import {
  MatInputModule
} from '@angular/material/input';

import {
  MatButtonModule
} from '@angular/material/button';

import {
  MatIconModule
} from '@angular/material/icon';

import {
  Product
} from '../../../core/models/product';

export interface ProductDialogData {
  product?: Product;
}

export interface ProductDialogResult {
  name: string;
  price: number;
  stock: number;
}

@Component({
  selector: 'app-product-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule
  ],
  templateUrl: './product-dialog.html',
  styleUrl: './product-dialog.css'
})
export class ProductDialog {

  readonly isEditing: boolean;

  readonly form;

  constructor(
    private formBuilder: FormBuilder,
    private dialogRef: MatDialogRef<
      ProductDialog,
      ProductDialogResult
    >,
    @Inject(MAT_DIALOG_DATA)
    public data: ProductDialogData
  ) {
    this.isEditing = Boolean(data.product);

    this.form = this.formBuilder.nonNullable.group({
      name: [
        data.product?.name ?? '',
        [
          Validators.required,
          Validators.minLength(2),
          Validators.maxLength(120)
        ]
      ],

      price: [
        data.product?.price ?? 0,
        [
          Validators.required,
          Validators.min(0.01)
        ]
      ],

      stock: [
        data.product?.stock ?? 0,
        [
          Validators.required,
          Validators.min(0),
          Validators.pattern(/^\d+$/)
        ]
      ]
    });
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.getRawValue();

    this.dialogRef.close({
      name: value.name.trim(),
      price: Number(value.price),
      stock: Number(value.stock)
    });
  }

  cancel(): void {
    this.dialogRef.close();
  }
}