package SpringBootRest.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CartDTO {

    private UUID id;

    private int totalItems;

    private double totalAmount;

    private List<CartItemDTO> items;
}
