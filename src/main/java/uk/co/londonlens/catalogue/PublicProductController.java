package uk.co.londonlens.catalogue;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
public class PublicProductController {

    private final ProductService productService;

    public PublicProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<PublicProductResponse> getProducts() {
        return productService.getActiveProducts();
    }

    @GetMapping("/{slug}")
    public PublicProductResponse getProduct(
            @PathVariable String slug) {
        return productService.getActiveProductBySlug(slug);
    }
}