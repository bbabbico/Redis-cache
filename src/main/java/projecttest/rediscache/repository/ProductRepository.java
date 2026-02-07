package projecttest.rediscache.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import projecttest.rediscache.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
