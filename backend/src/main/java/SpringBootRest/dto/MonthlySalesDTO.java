package SpringBootRest.dto;

import java.math.BigDecimal;

public record MonthlySalesDTO(
        String month,
        BigDecimal total
) {
}