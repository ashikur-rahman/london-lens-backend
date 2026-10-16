
package uk.co.londonlens.catalogue;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Positive;

public class ProductForm {

    @Positive(message = "Select a valid category.")
    private Long categoryId;

    @NotBlank(message = "Product name is required.")
    @Size(max = 200, message = "Name must be at most 200 characters.")
    private String name;

    @NotBlank(message = "Product slug is required.")
    @Size(max = 220, message = "Slug must be at most 220 characters.")
    @Pattern(
        regexp = "^[A-Za-z0-9]+(?:-[A-Za-z0-9]+)*$",
        message = "Use letters, numbers and single hyphens only."
    )
    private String slug;

    @Size(max = 500, message = "Short description must be at most 500 characters.")
    private String shortDescription;

    @Size(max = 10000, message = "Description must be at most 10,000 characters.")
    private String description;

    @NotBlank(message = "Product type is required.")
    @Pattern(
        regexp = "DIGITAL|PHYSICAL|SERVICE|MEMBERSHIP",
        message = "Select a valid product type."
    )
    private String productType = "DIGITAL";

    @NotNull(message = "Price is required.")
    @PositiveOrZero(message = "Price cannot be negative.")
    private Long priceMinor = 0L;

    @NotBlank(message = "Currency is required.")
    @Pattern(regexp = "[A-Za-z]{3}", message = "Currency must be a three-letter code.")
    private String currency = "GBP";

    @NotBlank(message = "Status is required.")
    @Pattern(
        regexp = "DRAFT|ACTIVE|ARCHIVED",
        message = "Select a valid product status."
    )
    private String status = "DRAFT";

    public static ProductForm from(Product product) {
        ProductForm form = new ProductForm();
        form.setName(product.getName());
        form.setSlug(product.getSlug());
        form.setShortDescription(product.getShortDescription());
        form.setDescription(product.getDescription());
        form.setProductType(product.getProductType());
        form.setPriceMinor(product.getPriceMinor());
        form.setCurrency(product.getCurrency().trim());
        form.setStatus(product.getStatus());
        form.setCategoryId(product.getCategoryId());
        return form;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getShortDescription() { return shortDescription; }
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getProductType() { return productType; }
    public void setProductType(String productType) {
        this.productType = productType;
    }

    public Long getPriceMinor() { return priceMinor; }
    public void setPriceMinor(Long priceMinor) {
        this.priceMinor = priceMinor;
    }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
