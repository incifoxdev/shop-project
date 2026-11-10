package SpringBootRest.controllers;

import SpringBootRest.dto.SaleDTO;
import SpringBootRest.entities.Sale;
import SpringBootRest.entities.User;
import SpringBootRest.exception.UserNotFoundException;
import SpringBootRest.mappers.SaleMapper;
import SpringBootRest.repositories.UserRepository;
import SpringBootRest.services.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;
    private final UserRepository userRepository;

    @PostMapping("/checkout")
    @PreAuthorize("isAuthenticated()")
    public SaleDTO checkout(
            Authentication authentication
    ) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found"
                        )
                );

        Sale sale = saleService.checkout(user);

        return SaleMapper.toDTO(sale);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<SaleDTO> getAllSales() {
        return saleService.findAll()
                .stream()
                .map(SaleMapper::toDTO)
                .toList();
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public List<SaleDTO> getMySales(
            Authentication authentication
    ) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found"
                        )
                );

        return saleService.findByUser(user)
                .stream()
                .map(SaleMapper::toDTO)
                .toList();
    }

    @GetMapping("/{saleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public SaleDTO getSaleById(
            @PathVariable Long saleId
    ) {
        return SaleMapper.toDTO(
                saleService.findById(saleId)
        );
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<SaleDTO> getSalesByUser(
            @PathVariable UUID userId
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found"
                        )
                );

        return saleService.findByUser(user)
                .stream()
                .map(SaleMapper::toDTO)
                .toList();
    }
}