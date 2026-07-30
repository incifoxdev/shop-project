package SpringBootRest.repositories;

import SpringBootRest.entities.Sale;
import SpringBootRest.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import SpringBootRest.dto.TopProductDTO;
import java.util.List;
import java.time.LocalDateTime;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    List<Sale> findByUserOrderByCreatedAtDesc(User user);

    @Query("""
        SELECT COALESCE(SUM(s.total), 0)
        FROM Sale s
    """)
    double sumTotalRevenue();

    List<Sale> findByCreatedAtGreaterThanEqualOrderByCreatedAtAsc(
            LocalDateTime startDate
    );
    @Query("""
    SELECT new SpringBootRest.dto.TopProductDTO(
        sd.product.name,
        SUM(sd.quantity),
        SUM(sd.quantity * sd.price)
    )
    FROM SaleDetail sd
    GROUP BY sd.product.id, sd.product.name
    ORDER BY SUM(sd.quantity) DESC
""")
List<TopProductDTO> findTopSellingProducts();
}