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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    @PostMapping("/items")
    public CartDTO addToCart(
            @Valid @RequestBody AddToCartRequest request,
            Authentication authentication
    ) {
        User user = getAuthenticatedUser(authentication);

        Cart cart = cartService.addProduct(
                user,
                request.getProductId(),
                request.getQuantity()
        );

        return CartMapper.toDTO(cart);
    }

    @GetMapping("/me")
    public CartDTO getCart(
            Authentication authentication
    ) {
        User user = getAuthenticatedUser(authentication);

        Cart cart = cartService.getOrCreateCart(user);

        return CartMapper.toDTO(cart);
    }

    @PutMapping("/items")
    public CartDTO updateQuantity(
            @Valid @RequestBody UpdateCartItemRequest request,
            Authentication authentication
    ) {
        User user = getAuthenticatedUser(authentication);

        Cart cart = cartService.updateQuantity(
                user,
                request.getProductId(),
                request.getQuantity()
        );

        return CartMapper.toDTO(cart);
    }

    @DeleteMapping("/items/{productId}")
    public CartDTO removeProduct(
            @PathVariable UUID productId,
            Authentication authentication
    ) {
        User user = getAuthenticatedUser(authentication);

        Cart cart = cartService.removeProduct(
                user,
                productId
        );

        return CartMapper.toDTO(cart);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> clearCart(
            Authentication authentication
    ) {
        User user = getAuthenticatedUser(authentication);

        cartService.clearCart(user);

        return ResponseEntity.noContent().build();
    }

    private User getAuthenticatedUser(
            Authentication authentication
    ) {
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found"
                        )
                );
    }
}