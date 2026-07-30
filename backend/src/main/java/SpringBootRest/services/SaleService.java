package SpringBootRest.services;

import SpringBootRest.entities.*;
import SpringBootRest.exception.CartEmptyException;
import SpringBootRest.exception.CartNotFoundException;
import SpringBootRest.exception.InsufficientStockException;
import SpringBootRest.exception.SaleNotFoundException;
import SpringBootRest.repositories.CartRepository;
import SpringBootRest.repositories.ProductRepository;
import SpringBootRest.repositories.SaleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final CartRepository cartRepository;
    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Sale checkout(User user) {

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new CartNotFoundException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new CartEmptyException("Cart is empty");
        }

        Sale sale = new Sale();
        sale.setUser(user);
        sale.setCreatedAt(LocalDateTime.now());

        List<SaleDetail> details = new ArrayList<>();
        double total = 0;

        for (CartItem item : cart.getItems()) {

            Product product = item.getProduct();

            if (product.getStock() < item.getQuantity()) {
                throw new InsufficientStockException(
                        "Not enough stock: " + product.getName()
                );
            }

            product.setStock(
                    product.getStock() - item.getQuantity()
            );

            productRepository.save(product);

            SaleDetail detail = new SaleDetail();
            detail.setSale(sale);
            detail.setProduct(product);
            detail.setQuantity(item.getQuantity());
            detail.setPrice(product.getPrice());

            details.add(detail);

            total += product.getPrice() * item.getQuantity();
        }

        sale.setDetails(details);
        sale.setTotal(total);

        Sale saved = saleRepository.save(sale);

        cart.getItems().clear();
        cartRepository.save(cart);

        return saved;
    }

    public List<Sale> findAll() {
        return saleRepository.findAll();
    }

    public Sale findById(Long id) {
        return saleRepository.findById(id)
                .orElseThrow(() ->
                        new SaleNotFoundException("Sale not found"));
    }

    public List<Sale> findByUser(User user) {
    return saleRepository.findByUserOrderByCreatedAtDesc(user);
}
}