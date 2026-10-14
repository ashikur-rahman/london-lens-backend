package uk.co.londonlens.catalogue;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<PublicProductResponse> getActiveProducts() {
        return productRepository
            .findByStatusOrderByNameAsc("ACTIVE")
            .stream()
            .map(PublicProductResponse::from)
            .toList();
    }

    public PublicProductResponse getActiveProductBySlug(String slug) {
        Product product = productRepository
            .findBySlugAndStatus(slug, "ACTIVE")
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Product not found"
            ));

        return PublicProductResponse.from(product);
    }
}