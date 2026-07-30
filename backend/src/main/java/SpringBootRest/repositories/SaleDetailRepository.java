package SpringBootRest.repositories;

import SpringBootRest.dto.TopProductDTO;
import SpringBootRest.entities.SaleDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SaleDetailRepository
        extends JpaRepository<SaleDetail, Long> {

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