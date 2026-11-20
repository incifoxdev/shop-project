package SpringBootRest.services;

import SpringBootRest.entities.Cart;
import SpringBootRest.entities.CartItem;
import SpringBootRest.entities.Product;
import SpringBootRest.entities.User;
import SpringBootRest.exception.InsufficientStockException;
import SpringBootRest.exception.ProductNotFoundException;
import SpringBootRest.repositories.CartRepository;
import SpringBootRest.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartService cartService;

    private User user;
    private Cart cart;
    private Product product;
    private UUID productId;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("user@email.com");

        productId = UUID.randomUUID();

        product = new Product();
        product.setId(productId);
        product.setName("Producto Test");
        product.setPrice(new BigDecimal("1000"));
        product.setStock(10);

        cart = new Cart();
        cart.setUser(user);
        cart.setItems(new ArrayList<>());

        when(cartRepository.findByUser(user))
                .thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class)))
                .thenReturn(cart);
    }

    // ─── ADD PRODUCT ─────────────────────────────────────

    @Test
    void addProduct_deberiaAgregarProductoAlCarrito() {
        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        Cart result = cartService.addProduct(user, productId, 2);

        assertNotNull(result);
        assertEquals(1, cart.getItems().size());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        verify(cartRepository).save(cart);
    }

    @Test
    void addProduct_deberiaLanzarExcepcionSiProductoNoExiste() {
        when(productRepository.findById(productId))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> cartService.addProduct(user, productId, 2)
        );

        verify(cartRepository, never()).save(any());
    }

    @Test
    void addProduct_deberiaLanzarExcepcionSiStockInsuficiente() {
        product.setStock(1);

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        assertThrows(
                InsufficientStockException.class,
                () -> cartService.addProduct(user, productId, 5)
        );

        verify(cartRepository, never()).save(any());
    }

    @Test
    void addProduct_deberiaActualizarCantidadSiProductoYaEstaEnCarrito() {
        CartItem existingItem = new CartItem();
        existingItem.setProduct(product);
        existingItem.setQuantity(3);
        cart.setItems(new ArrayList<>(List.of(existingItem)));

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        cartService.addProduct(user, productId, 2);

        assertEquals(1, cart.getItems().size());
        assertEquals(5, cart.getItems().get(0).getQuantity()); // 3 + 2
    }

    // ─── UPDATE QUANTITY ──────────────────────────────────

    @Test
    void updateQuantity_deberiaLanzarExcepcionSiCantidadEsCero() {
        assertThrows(
                IllegalArgumentException.class,
                () -> cartService.updateQuantity(user, productId, 0)
        );
    }

    @Test
    void updateQuantity_deberiaLanzarExcepcionSiStockInsuficiente() {
        CartItem item = new CartItem();
        item.setProduct(product);
        item.setQuantity(2);
        cart.setItems(new ArrayList<>(List.of(item)));

        assertThrows(
                InsufficientStockException.class,
                () -> cartService.updateQuantity(user, productId, 20)
        );
    }

    @Test
    void updateQuantity_deberiaActualizarCantidad() {
        CartItem item = new CartItem();
        item.setProduct(product);
        item.setQuantity(2);
        cart.setItems(new ArrayList<>(List.of(item)));

        cartService.updateQuantity(user, productId, 5);

        assertEquals(5, item.getQuantity());
        verify(cartRepository).save(cart);
    }

    // ─── REMOVE PRODUCT ───────────────────────────────────

    @Test
    void removeProduct_deberiaEliminarProductoDelCarrito() {
        CartItem item = new CartItem();
        item.setProduct(product);
        item.setQuantity(2);
        cart.setItems(new ArrayList<>(List.of(item)));

        cartService.removeProduct(user, productId);

        assertTrue(cart.getItems().isEmpty());
        verify(cartRepository).save(cart);
    }

    @Test
    void removeProduct_deberiaLanzarExcepcionSiProductoNoEstaEnCarrito() {
        assertThrows(
                ProductNotFoundException.class,
                () -> cartService.removeProduct(user, productId)
        );
    }
}
