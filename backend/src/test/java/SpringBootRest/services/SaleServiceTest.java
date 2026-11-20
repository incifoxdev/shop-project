package SpringBootRest.services;

import SpringBootRest.entities.*;
import SpringBootRest.exception.CartEmptyException;
import SpringBootRest.exception.CartNotFoundException;
import SpringBootRest.exception.InsufficientStockException;
import SpringBootRest.repositories.CartRepository;
import SpringBootRest.repositories.ProductRepository;
import SpringBootRest.repositories.SaleRepository;
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
class SaleServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private SaleService saleService;

    private User user;
    private Cart cart;
    private Product product;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("user@email.com");

        product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("Producto Test");
        product.setPrice(new BigDecimal("1000"));
        product.setStock(10);

        cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(2);

        cart = new Cart();
        cart.setUser(user);
        cart.setItems(new ArrayList<>(List.of(cartItem)));
    }

    @Test
    void checkout_deberiaCrearVentaYDescontarStock() {
        when(cartRepository.findByUser(user))
                .thenReturn(Optional.of(cart));

        Sale saleMock = new Sale();
        saleMock.setId(1L);
        saleMock.setTotal(new BigDecimal("2000"));
        saleMock.setDetails(new ArrayList<>());

        when(saleRepository.save(any(Sale.class)))
                .thenReturn(saleMock);

        Sale result = saleService.checkout(user);

        assertNotNull(result);
        assertEquals(8, product.getStock()); // 10 - 2
        verify(productRepository).save(product);
        verify(saleRepository).save(any(Sale.class));
        verify(cartRepository, times(1)).save(cart);
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    void checkout_deberiaLanzarExcepcionSiCarritoNoExiste() {
        when(cartRepository.findByUser(user))
                .thenReturn(Optional.empty());

        assertThrows(
                CartNotFoundException.class,
                () -> saleService.checkout(user)
        );

        verify(saleRepository, never()).save(any());
    }

    @Test
    void checkout_deberiaLanzarExcepcionSiCarritoEstaVacio() {
        cart.setItems(new ArrayList<>());

        when(cartRepository.findByUser(user))
                .thenReturn(Optional.of(cart));

        assertThrows(
                CartEmptyException.class,
                () -> saleService.checkout(user)
        );

        verify(saleRepository, never()).save(any());
    }

    @Test
    void checkout_deberiaLanzarExcepcionSiStockInsuficiente() {
        product.setStock(1); // stock menor que cantidad pedida (2)

        when(cartRepository.findByUser(user))
                .thenReturn(Optional.of(cart));

        assertThrows(
                InsufficientStockException.class,
                () -> saleService.checkout(user)
        );

        verify(saleRepository, never()).save(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void checkout_deberiaCalcularTotalCorrectamente() {
        when(cartRepository.findByUser(user))
                .thenReturn(Optional.of(cart));

        Sale saleMock = new Sale();
        saleMock.setId(1L);
        saleMock.setTotal(new BigDecimal("2000"));
        saleMock.setDetails(new ArrayList<>());

        when(saleRepository.save(any(Sale.class))).thenAnswer(invocation -> {
            Sale s = invocation.getArgument(0);
            assertEquals(new BigDecimal("2000"), s.getTotal()); // 1000 * 2
            return saleMock;
        });

        saleService.checkout(user);
    }
}
