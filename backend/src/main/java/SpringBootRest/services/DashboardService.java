package SpringBootRest.services;

import SpringBootRest.dto.DashboardStatsDTO;
import SpringBootRest.dto.MonthlySalesDTO;
import SpringBootRest.dto.TopProductDTO;
import SpringBootRest.entities.Sale;
import SpringBootRest.repositories.ProductRepository;
import SpringBootRest.repositories.SaleDetailRepository;
import SpringBootRest.repositories.SaleRepository;
import SpringBootRest.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final SaleRepository saleRepository;
    private final SaleDetailRepository saleDetailRepository;

    public DashboardStatsDTO getStats() {

        long totalProducts =
                productRepository.count();

        long totalUsers =
                userRepository.count();

        long totalSales =
                saleRepository.count();

        double totalRevenue =
                saleRepository.sumTotalRevenue();

        List<MonthlySalesDTO> salesPerMonth =
                getSalesPerMonth();

        List<TopProductDTO> topProducts =
                getTopProducts();

        return new DashboardStatsDTO(
                totalProducts,
                totalUsers,
                totalSales,
                totalRevenue,
                salesPerMonth,
                topProducts
        );
    }

    private List<MonthlySalesDTO> getSalesPerMonth() {

        YearMonth currentMonth =
                YearMonth.now();

        YearMonth firstMonth =
                currentMonth.minusMonths(5);

        LocalDateTime startDate =
                firstMonth
                        .atDay(1)
                        .atStartOfDay();

        List<Sale> sales =
                saleRepository
                        .findByCreatedAtGreaterThanEqualOrderByCreatedAtAsc(
                                startDate
                        );

        Map<YearMonth, Double> totalsByMonth =
                sales.stream()
                        .collect(
                                Collectors.groupingBy(
                                        sale -> YearMonth.from(
                                                sale.getCreatedAt()
                                        ),
                                        Collectors.summingDouble(
                                                Sale::getTotal
                                        )
                                )
                        );

        List<MonthlySalesDTO> result =
                new ArrayList<>();

        Locale spanishLocale =
                Locale.forLanguageTag("es-ES");

        for (int index = 0; index < 6; index++) {

            YearMonth month =
                    firstMonth.plusMonths(index);

            String monthName =
                    month.getMonth()
                            .getDisplayName(
                                    TextStyle.SHORT,
                                    spanishLocale
                            );

            String label =
                    monthName.substring(0, 1).toUpperCase()
                            + monthName.substring(1)
                            + " "
                            + month.getYear();

            double total =
                    totalsByMonth.getOrDefault(
                            month,
                            0.0
                    );

            result.add(
                    new MonthlySalesDTO(
                            label,
                            total
                    )
            );
        }

        return result;
    }

    private List<TopProductDTO> getTopProducts() {

        return saleDetailRepository
                .findTopSellingProducts()
                .stream()
                .limit(5)
                .toList();
    }
}