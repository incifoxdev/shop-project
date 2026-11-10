package SpringBootRest.mappers;

import SpringBootRest.dto.SaleDTO;
import SpringBootRest.dto.SaleDetailDTO;
import SpringBootRest.entities.Sale;

import java.math.BigDecimal;

public class SaleMapper {

    public static SaleDTO toDTO(Sale sale) {

        SaleDTO dto = new SaleDTO();

        dto.setId(sale.getId());
        dto.setCreatedAt(sale.getCreatedAt());

        dto.setUserId(
                sale.getUser().getId()
        );

        dto.setUserName(
                sale.getUser().getName()
        );

        dto.setUserEmail(
                sale.getUser().getEmail()
        );

        dto.setDetails(
                sale.getDetails()
                        .stream()
                        .map(detail -> {

                            SaleDetailDTO saleDetailDTO =
                                    new SaleDetailDTO();

                            saleDetailDTO.setProductId(
                                    detail.getProduct().getId()
                            );

                            saleDetailDTO.setProductName(
                                    detail.getProduct().getName()
                            );

                            saleDetailDTO.setPrice(
                                    detail.getPrice()
                            );

                            saleDetailDTO.setQuantity(
                                    detail.getQuantity()
                            );

                            saleDetailDTO.setSubtotal(
                                    detail.getPrice()
                                            .multiply(BigDecimal.valueOf(
                                                    detail.getQuantity()
                                            ))
                            );

                            return saleDetailDTO;
                        })
                        .toList()
        );

        dto.setTotalItems(
                sale.getDetails()
                        .stream()
                        .mapToInt(detail ->
                                detail.getQuantity()
                        )
                        .sum()
        );

        dto.setTotal(
                sale.getDetails()
                        .stream()
                        .map(detail ->
                                detail.getPrice()
                                        .multiply(BigDecimal.valueOf(
                                                detail.getQuantity()
                                        ))
                        )
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );

        return dto;
    }
}
