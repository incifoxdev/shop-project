export interface SaleDetail {

  productId: string;

  productName: string;

  quantity: number;

  price: number;

  subtotal: number;

}


export interface Sale {

  id: number;

  createdAt: string;

  total: number;

  totalItems: number;

  userId: string;

  details: SaleDetail[];

}