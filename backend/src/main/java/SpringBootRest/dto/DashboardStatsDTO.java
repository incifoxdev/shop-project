package SpringBootRest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class DashboardStatsDTO {

    private long totalProducts;
    private long totalUsers;
    private long totalSales;
    private BigDecimal totalRevenue;

    private List<MonthlySalesDTO> salesPerMonth;
    List<TopProductDTO> topProducts;
}