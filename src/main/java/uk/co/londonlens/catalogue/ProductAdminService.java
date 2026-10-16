
package uk.co.londonlens.catalogue;

import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class ProductAdminService {

    private final ProductRepository repository;

    public ProductAdminService(
            ProductRepository repository,
            CategoryRepository categoryRepository) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
    }
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Product findById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Product not found"
            ));
    }

    public Product create(ProductForm form) {
        String slug = normalise(form.getSlug());
        ensureSlugAvailable(slug, null);

        Product product = new Product();
        applyForm(product, form, slug);

        return repository.save(product);
    }

    private void validateCategory(Long categoryId) {
        if (categoryId == null) {
            return;
        }

        boolean valid = categoryRepository.findById(categoryId)
            .filter(Category::isActive)
            .isPresent();

        if (!valid) {
            throw new InvalidCategoryException();
        }
    }

    public static class InvalidCategoryException
            extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    public Product update(Long id, ProductForm form) {
        Product product = findById(id);
        String slug = normalise(form.getSlug());

        ensureSlugAvailable(slug, id);
        applyForm(product, form, slug);

        return repository.save(product);
    }

    private void ensureSlugAvailable(String slug, Long currentId) {
        boolean exists = currentId == null
            ? repository.existsBySlug(slug)
            : repository.existsBySlugAndIdNot(slug, currentId);

        if (exists) {
            throw new DuplicateSlugException();
        }
    }

    private void applyForm(
            Product product, ProductForm form, String slug) {

        product.updateAdminFields(
            form.getCategoryId(),
            form.getName().trim(),
            slug,
            blankToNull(form.getShortDescription()),
            blankToNull(form.getDescription()),
            form.getProductType().toUpperCase(Locale.ROOT),
            form.getPriceMinor(),
            form.getCurrency().trim().toUpperCase(Locale.ROOT),
            form.getStatus()
        );
    }

    private String normalise(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    public static class DuplicateSlugException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }
}
