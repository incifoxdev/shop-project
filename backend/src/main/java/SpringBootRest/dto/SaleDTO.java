package SpringBootRest.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class SaleDTO {

    private Long id;

    private UUID userId;

    private LocalDateTime createdAt;

    private double total;

    private int totalItems;

    private List<SaleDetailDTO> details;
}