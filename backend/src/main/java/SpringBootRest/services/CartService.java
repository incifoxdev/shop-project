package SpringBootRest.services;

import SpringBootRest.entities.Cart;
import SpringBootRest.entities.CartItem;
import SpringBootRest.entities.Product;
import SpringBootRest.entities.User;
import SpringBootRest.exception.InsufficientStockException;
import SpringBootRest.exception.ProductNotFoundException;
import SpringBootRest.repositories.CartRepository;
import SpringBootRest.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    return cartRepository.save(cart);
                });
    }

    public Cart addProduct(User user, UUID productId, int quantity) {

        Cart cart = getOrCreateCart(user);

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found"));
        if (product.getStock() < quantity) {
            throw new InsufficientStockException(
                    "Only " + product.getStock() + " units available"
            );
        }

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            int newQuantity =
                    existingItem.get().getQuantity() + quantity;

            if (newQuantity > product.getStock()) {
                throw new InsufficientStockException(
                        "Only " + product.getStock() + " units available"
                );
            }

            existingItem.get().setQuantity(newQuantity);

        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(quantity);

            cart.getItems().add(item);
        }

        return cartRepository.save(cart);
    }
    @Transactional
public Cart updateQuantity(
        User user,
        UUID productId,
        int quantity
) {

    if (quantity < 1) {
        throw new IllegalArgumentException(
                "Quantity must be greater than zero"
        );
    }

    Cart cart = getOrCreateCart(user);

    CartItem item = cart.getItems()
            .stream()
            .filter(cartItem ->
                    cartItem.getProduct()
                            .getId()
                            .equals(productId)
            )
            .findFirst()
            .orElseThrow(() ->
                    new ProductNotFoundException(
                            "Product not found in cart"
                    )
            );

    Product product = item.getProduct();

    if (quantity > product.getStock()) {
        throw new InsufficientStockException(
                "Only " +
                product.getStock() +
                " units available"
        );
    }

    item.setQuantity(quantity);

    return cartRepository.save(cart);
}
    public Cart removeProduct(User user, UUID productId) {

        Cart cart = getOrCreateCart(user);

        CartItem item = cart.getItems()
                .stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found in cart"));

        cart.getItems().remove(item);

        return cartRepository.save(cart);
    }
    @Transactional
    public void clearCart(User user) {

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getItems().clear();

        cartRepository.save(cart);
    }
}