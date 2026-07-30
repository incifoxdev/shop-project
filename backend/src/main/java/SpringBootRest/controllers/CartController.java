package SpringBootRest.controllers;

import SpringBootRest.dto.AddToCartRequest;
import SpringBootRest.dto.CartDTO;
import SpringBootRest.dto.UpdateCartItemRequest;
import SpringBootRest.entities.Cart;
import SpringBootRest.entities.User;
import SpringBootRest.exception.UserNotFoundException;
import SpringBootRest.mappers.CartMapper;
import SpringBootRest.repositories.UserRepository;
import SpringBootRest.services.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    @PostMapping("/add")
    public CartDTO addToCart(
            @Valid @RequestBody AddToCartRequest request
    ) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Cart cart = cartService.addProduct(
                user,
                request.getProductId(),
                request.getQuantity()
        );

        return CartMapper.toDTO(cart);
    }

    @GetMapping("/{userId}")
    public CartDTO getCart(@PathVariable UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Cart cart = cartService.getOrCreateCart(user);

        return CartMapper.toDTO(cart);
    }
    @PutMapping("/update")
    public CartDTO updateQuantity(
            @Valid @RequestBody UpdateCartItemRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        Cart cart = cartService.updateQuantity(
                user,
                request.getProductId(),
                request.getQuantity());

        return CartMapper.toDTO(cart);
    }
    @DeleteMapping("/remove/{userId}/{productId}")
    public CartDTO removeProduct(
            @PathVariable UUID userId,
            @PathVariable UUID productId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        Cart cart = cartService.removeProduct(user, productId);

        return CartMapper.toDTO(cart);
    }
    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        cartService.clearCart(user);

        return ResponseEntity.noContent().build();
    }
}