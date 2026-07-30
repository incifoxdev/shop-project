package SpringBootRest.controllers;

import SpringBootRest.dto.ProductRequestDTO;
import SpringBootRest.dto.ProductResponseDTO;
import SpringBootRest.entities.Product;
import SpringBootRest.mappers.ProductMapper;
import SpringBootRest.services.ProductService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponseDTO create(
            @RequestBody ProductRequestDTO request) {

        Product saved = productService.create(request);

        return ProductMapper.toDTO(saved);
    }

    @GetMapping
    public List<ProductResponseDTO> list() {

        return productService.findAll()
                .stream()
                .map(ProductMapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ProductResponseDTO findById(@PathVariable UUID id) {

        return ProductMapper.toDTO(
                productService.findById(id)
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(
            @PathVariable UUID id,
            @RequestBody ProductRequestDTO request) {

        System.out.println("ENTRÓ AL PUT /products/" + id);
        System.out.println("Nombre: " + request.getName());
        System.out.println("Precio: " + request.getPrice());
        System.out.println("Stock: " + request.getStock());

        Product updated = productService.update(id, request);

        System.out.println("PRODUCTO ACTUALIZADO: " + updated.getId());

        return ResponseEntity.ok(ProductMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {

        System.out.println("ENTRÓ AL DELETE /products/" + id);

        productService.delete(id);

        System.out.println("PRODUCTO ELIMINADO");

        return ResponseEntity.noContent().build();
    }

}