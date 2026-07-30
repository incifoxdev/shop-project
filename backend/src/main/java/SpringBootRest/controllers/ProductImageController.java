package SpringBootRest.controllers;


import SpringBootRest.entities.ProductImage;
import SpringBootRest.services.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.UUID;


@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductImageController {


    private final ProductImageService service;



    @PostMapping("/{id}/images")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductImage upload(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file
    ){

        return service.upload(id,file);
    }

}
