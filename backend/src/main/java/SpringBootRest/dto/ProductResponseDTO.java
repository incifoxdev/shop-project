package SpringBootRest.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class ProductResponseDTO {

    private UUID id;
    private String name;
    private BigDecimal price;
    private int stock;
    private List<String> imageUrls;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
}
