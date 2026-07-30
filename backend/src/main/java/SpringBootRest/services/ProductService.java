package SpringBootRest.services;

import SpringBootRest.dto.ProductRequestDTO;
import SpringBootRest.entities.Product;
import SpringBootRest.entities.ProductImage;
import SpringBootRest.exception.ProductNotFoundException;
import SpringBootRest.mappers.ProductMapper;
import SpringBootRest.repositories.ProductImageRepository;
import SpringBootRest.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;


    public Product create(ProductRequestDTO request) {

        Product product = ProductMapper.toEntity(request);

        return productRepository.save(product);
    }


    public List<Product> findAll() {

        return productRepository.findAll();
    }


    public Product findById(UUID id) {

        return productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found"));
    }


    public Product update(UUID id, ProductRequestDTO request) {

    System.out.println("1. Buscando producto: " + id);

    Product product = findById(id);

    System.out.println("2. Producto encontrado: " + product.getName());

    ProductMapper.updateEntity(product, request);

    System.out.println("3. Datos aplicados");

    Product saved = productRepository.save(product);

    System.out.println("4. Producto guardado");

    return saved;
}


    public void delete(UUID id) {

        Product product = findById(id);

        productRepository.delete(product);
    }


    public ProductImage addImage(UUID productId, String url) {

        Product product = findById(productId);

        ProductImage image = new ProductImage();

        image.setUrl(url);
        image.setProduct(product);

        return productImageRepository.save(image);
    }
}
