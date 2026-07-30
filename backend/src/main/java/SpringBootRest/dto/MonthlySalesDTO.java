package SpringBootRest.dto;

public record MonthlySalesDTO(
        String month,
        double total
) {
}