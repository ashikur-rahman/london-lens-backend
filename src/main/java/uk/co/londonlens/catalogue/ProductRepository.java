package uk.co.londonlens.catalogue;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository
        extends JpaRepository<Product, Long> {

    List<Product> findByStatusOrderByNameAsc(String status);

    Optional<Product> findBySlugAndStatus(
        String slug,
        String status
    );
}