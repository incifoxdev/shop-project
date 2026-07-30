package SpringBootRest.controllers;

import SpringBootRest.dto.SaleDTO;
import SpringBootRest.entities.Sale;
import SpringBootRest.entities.User;
import SpringBootRest.exception.UserNotFoundException;
import SpringBootRest.mappers.SaleMapper;
import SpringBootRest.repositories.UserRepository;
import SpringBootRest.services.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;
    private final UserRepository userRepository;

    @PostMapping("/checkout/{userId}")
    public SaleDTO checkout(@PathVariable UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        Sale sale = saleService.checkout(user);

        return SaleMapper.toDTO(sale);
    }
    @GetMapping
    public List<SaleDTO> getAllSales() {

        return saleService.findAll()
                .stream()
                .map(SaleMapper::toDTO)
                .toList();
    }

    @GetMapping("/{saleId}")
    public SaleDTO getSaleById(@PathVariable Long saleId) {

        return SaleMapper.toDTO(
                saleService.findById(saleId)
        );
    }

    @GetMapping("/user/{userId}")
    public List<SaleDTO> getSalesByUser(
            @PathVariable UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return saleService.findByUser(user)
                .stream()
                .map(SaleMapper::toDTO)
                .toList();
    }
}