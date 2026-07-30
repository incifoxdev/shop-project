export interface CartItem {

  productId: string;

  productName: string;

  price: number;

  quantity: number;

  subtotal: number;

}



export interface Cart {

  id: string;

  totalItems: number;

  totalAmount: number;

  items: CartItem[];

}