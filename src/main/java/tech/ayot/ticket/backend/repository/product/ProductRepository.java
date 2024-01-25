package tech.ayot.ticket.backend.repository.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.ayot.ticket.backend.model.product.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsProductByName(String name);

    Product findProductById(Long id);
}
