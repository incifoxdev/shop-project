package SpringBootRest.services;

import SpringBootRest.entities.Product;
import SpringBootRest.entities.ProductImage;
import SpringBootRest.repositories.ProductImageRepository;
import SpringBootRest.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ProductImageService {


    private final ProductRepository productRepository;

    private final ProductImageRepository imageRepository;

    private final FileStorageService storageService;



    public ProductImage upload(
            UUID productId,
            MultipartFile file
    ){

        Product product =
                productRepository.findById(productId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Product not found")
                        );


        String url =
                storageService.save(file);


        ProductImage image =
                new ProductImage();

        image.setUrl(url);

        image.setProduct(product);


        return imageRepository.save(image);
    }
}
