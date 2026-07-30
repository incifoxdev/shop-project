package SpringBootRest.repositories;

import SpringBootRest.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    //Optional<Object> findById(Long userId);
    Optional<User> findByEmail(String email);
}
