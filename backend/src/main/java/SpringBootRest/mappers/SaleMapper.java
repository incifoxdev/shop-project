package SpringBootRest.mappers;

import SpringBootRest.dto.SaleDTO;
import SpringBootRest.dto.SaleDetailDTO;
import SpringBootRest.entities.Sale;

public class SaleMapper {

    public static SaleDTO toDTO(Sale sale) {

        SaleDTO dto = new SaleDTO();

        dto.setId(sale.getId());
        dto.setCreatedAt(sale.getCreatedAt());
        dto.setUserId(sale.getUser().getId());

        dto.setDetails(
                sale.getDetails()
                        .stream()
                        .map(d -> {

                            SaleDetailDTO sd = new SaleDetailDTO();

                            sd.setProductId(d.getProduct().getId());
                            sd.setProductName(d.getProduct().getName());
                            sd.setPrice(d.getPrice());
                            sd.setQuantity(d.getQuantity());

                            sd.setSubtotal(
                                    d.getPrice() * d.getQuantity()
                            );

                            return sd;
                        })
                        .toList()
        );

        dto.setTotalItems(
                sale.getDetails()
                        .stream()
                        .mapToInt(d -> d.getQuantity())
                        .sum()
        );

        dto.setTotal(
                sale.getDetails()
                        .stream()
                        .mapToDouble(d ->
                                d.getPrice() * d.getQuantity()
                        )
                        .sum()
        );

        return dto;
    }
}
