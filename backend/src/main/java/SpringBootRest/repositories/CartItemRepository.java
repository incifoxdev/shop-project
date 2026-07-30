package SpringBootRest.repositories;

import SpringBootRest.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CartItemRepository
        extends JpaRepository<CartItem, UUID> {
}
