export interface MonthlySales {
  month: string;
  total: number;
}

export interface TopProduct {
  productName: string;
  quantitySold: number;
  revenue: number;
}

export interface DashboardStats {
  totalProducts: number;
  totalUsers: number;
  totalSales: number;
  totalRevenue: number;
  salesPerMonth: MonthlySales[];
  topProducts: TopProduct[];
}