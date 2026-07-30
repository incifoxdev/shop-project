package SpringBootRest.dto;

public record TopProductDTO(
    String productName,
    long quantitySold,
    double revenue
) {}