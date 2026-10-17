package uk.co.londonlens.catalogue;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository
        extends JpaRepository<Product, Long> {


    List<Product> findByStatusOrderByNameAsc(String status);

    Optional<Product> findBySlugAndStatus(
        String slug,
        String status
    );

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);

        @Query("""
        SELECT p FROM Product p
        WHERE (
            :search = ''
            OR LOWER(p.name) LIKE CONCAT('%', LOWER(:search), '%')
            OR LOWER(p.slug) LIKE CONCAT('%', LOWER(:search), '%')
        )
        AND (
            :status = ''
            OR p.status = :status
        )
        """)
    Page<Product> searchForAdmin(
        @Param("search") String search,
        @Param("status") String status,
        Pageable pageable
    );
}