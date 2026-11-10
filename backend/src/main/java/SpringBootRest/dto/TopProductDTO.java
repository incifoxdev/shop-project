package SpringBootRest.dto;

import java.math.BigDecimal;

public record TopProductDTO(
    String productName,
    long quantitySold,
    BigDecimal revenue
) {}