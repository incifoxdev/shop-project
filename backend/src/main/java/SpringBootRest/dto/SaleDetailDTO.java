package SpringBootRest.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class SaleDetailDTO {

    private UUID productId;

    private String productName;

    private BigDecimal price;

    private int quantity;

    private BigDecimal subtotal;
}