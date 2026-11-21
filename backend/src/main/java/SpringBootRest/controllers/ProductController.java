package SpringBootRest.controllers;

import SpringBootRest.dto.ProductRequestDTO;
import SpringBootRest.dto.ProductResponseDTO;
import SpringBootRest.entities.Product;
import SpringBootRest.mappers.ProductMapper;
import SpringBootRest.services.ProductService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/products")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Value("${app.images.path:/app/images}")
    private String imagesPath;

    @Value("${app.images.url:https://incifox.org/images}")
    private String imagesUrl;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponseDTO create(@RequestBody ProductRequestDTO request) {
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
        return ProductMapper.toDTO(productService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(
            @PathVariable UUID id,
            @RequestBody ProductRequestDTO request) {
        Product updated = productService.update(id, request);
        return ResponseEntity.ok(ProductMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/image")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> uploadImage(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El archivo debe ser una imagen"));
            }

            Path uploadDir = Paths.get(imagesPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            String originalName = file.getOriginalFilename();
            String extension = (originalName != null && originalName.contains("."))
                    ? originalName.substring(originalName.lastIndexOf("."))
                    : ".jpg";
            String fileName = "product-" + id + "-" + System.currentTimeMillis() + extension;
            Path filePath = uploadDir.resolve(fileName);

            Files.write(filePath, file.getBytes());

            String imageUrl = imagesUrl + "/" + fileName;
            productService.addImage(id, imageUrl);

            return ResponseEntity.ok(Map.of(
                    "imageUrl", imageUrl,
                    "message", "Imagen subida correctamente"
            ));

        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al guardar la imagen"));
        }
    }
}
