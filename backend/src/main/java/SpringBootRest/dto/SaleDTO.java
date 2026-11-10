package SpringBootRest.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class SaleDTO {

    private Long id;

    private UUID userId;

    private String userName;

    private String userEmail;

    private LocalDateTime createdAt;

    private BigDecimal total;

    private int totalItems;

    private List<SaleDetailDTO> details;
}