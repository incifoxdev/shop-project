package SpringBootRest.mappers;

import SpringBootRest.dto.ProductRequestDTO;
import SpringBootRest.dto.ProductResponseDTO;
import SpringBootRest.entities.Product;
import SpringBootRest.entities.ProductImage;

import java.util.List;

public class ProductMapper {

    public static Product toEntity(ProductRequestDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        return product;
    }

    public static ProductResponseDTO toDTO(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());

        if (product.getImages() != null) {
            List<String> imageUrls = product.getImages()
                    .stream()
                    .map(ProductImage::getUrl)
                    .toList();
            dto.setImageUrls(imageUrls);
        }

        return dto;
    }

    public static void updateEntity(Product product, ProductRequestDTO dto) {
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
    }
}
