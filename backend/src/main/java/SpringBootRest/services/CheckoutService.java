package SpringBootRest.services;

import SpringBootRest.entities.*;
import SpringBootRest.exception.CartEmptyException;
import SpringBootRest.exception.CartNotFoundException;
import SpringBootRest.exception.InsufficientStockException;
import SpringBootRest.repositories.CartRepository;
import SpringBootRest.repositories.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final CartRepository cartRepository;
    private final SaleRepository saleRepository;

    public Sale checkout(User user) {

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() ->
                        new CartNotFoundException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new CartEmptyException("Cart is empty");
        }

        // validar stock
        for (CartItem item : cart.getItems()) {

            if (item.getQuantity() > item.getProduct().getStock()) {

                throw new InsufficientStockException(
                        "Insufficient stock for "
                                + item.getProduct().getName()
                );
            }
        }

        Sale sale = new Sale();
        sale.setUser(user);
        sale.setCreatedAt(LocalDateTime.now());

        List<SaleDetail> details = new ArrayList<>();

        double total = 0;

        for (CartItem item : cart.getItems()) {

            Product product = item.getProduct();

            // descontar stock
            product.setStock(
                    product.getStock() - item.getQuantity()
            );

            SaleDetail detail = new SaleDetail();

            detail.setSale(sale);
            detail.setProduct(product);
            detail.setQuantity(item.getQuantity());
            detail.setPrice(product.getPrice());

            details.add(detail);

            total += item.getQuantity() * product.getPrice();
        }

        sale.setTotal(total);
        sale.setDetails(details);

        Sale saved = saleRepository.save(sale);

        // limpiar carrito
        cart.getItems().clear();
        cartRepository.save(cart);

        return saved;
    }
}
