export interface ProductImage {

  id: string;

  url: string;

}

export interface Product {

  id: string;

  name: string;

  price: number;

  stock: number;

  images: ProductImage[];

}