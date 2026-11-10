export interface SaleDetail {
  productId: string;
  productName: string;
  quantity: number;
  price: number;
  subtotal: number;
}

export interface Sale {
  id: number;

  userId: string;
  userName: string;
  userEmail: string;

  createdAt: string;

  total: number;
  totalItems: number;

  details: SaleDetail[];
}