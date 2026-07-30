package SpringBootRest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DashboardStatsDTO {

    private long totalProducts;
    private long totalUsers;
    private long totalSales;
    private double totalRevenue;

    private List<MonthlySalesDTO> salesPerMonth;
    List<TopProductDTO> topProducts;
}