package SpringBootRest.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CartDTO {

    private UUID id;

    private int totalItems;

    private BigDecimal totalAmount;

    private List<CartItemDTO> items;
}
