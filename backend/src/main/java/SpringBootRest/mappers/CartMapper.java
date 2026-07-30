package SpringBootRest.mappers;

import SpringBootRest.dto.CartDTO;
import SpringBootRest.dto.CartItemDTO;
import SpringBootRest.entities.Cart;

import java.util.stream.Collectors;

public class CartMapper {

    public static CartDTO toDTO(Cart cart) {

        CartDTO dto = new CartDTO();

        dto.setId(cart.getId());

        dto.setItems(
                cart.getItems()
                        .stream()
                        .map(item -> {

                            CartItemDTO i = new CartItemDTO();

                            i.setProductId(item.getProduct().getId());
                            i.setProductName(item.getProduct().getName());
                            i.setPrice(item.getProduct().getPrice());
                            i.setQuantity(item.getQuantity());

                            i.setSubtotal(
                                    item.getQuantity() * item.getProduct().getPrice()
                            );

                            return i;
                        })
                        .collect(Collectors.toList())
        );

        dto.setTotalItems(
                cart.getItems()
                        .stream()
                        .mapToInt(item -> item.getQuantity())
                        .sum()
        );

        dto.setTotalAmount(
                cart.getItems()
                        .stream()
                        .mapToDouble(item ->
                                item.getQuantity() * item.getProduct().getPrice()
                        )
                        .sum()
        );

        return dto;
    }
}
