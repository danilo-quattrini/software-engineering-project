package it.unicam.cs.ids2425.product.repository;

import it.unicam.cs.ids2425.product.Product;
import it.unicam.cs.ids2425.product.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByUserId(Long userId);
    List<Product> findByStatus(ProductStatus status);
}
