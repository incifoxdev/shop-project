package SpringBootRest.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class SaleDetailDTO {

    private UUID productId;

    private String productName;

    private double price;

    private int quantity;

    private double subtotal;
}
