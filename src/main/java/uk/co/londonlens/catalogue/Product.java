package uk.co.londonlens.catalogue;


import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import jakarta.persistence.*;
import java.time.Instant;


@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, unique = true, length = 220)
    private String slug;

    @Column(name = "short_description", length = 500)
    private String shortDescription;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "product_type", nullable = false, length = 30)
    private String productType;

    @Column(name = "price_minor", nullable = false)
    private Long priceMinor;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(
        name = "currency",
        nullable = false,
        columnDefinition = "char(3)"
    )
    private String currency;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private Instant updatedAt;

    protected Product() {
        // Required by JPA
    }

    public Long getId() {
        return id;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public String getProductType() {
        return productType;
    }

    public Long getPriceMinor() {
        return priceMinor;
    }

    public String getCurrency() {
        return currency;
    }

    public String getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    void updateAdminFields(
            Long categoryId,
            String name,
            String slug,
            String shortDescription,
            String description,
            String productType,
            Long priceMinor,
            String currency,
            String status) {

        this.categoryId = categoryId;
        this.name = name;
        this.slug = slug;
        this.shortDescription = shortDescription;
        this.description = description;
        this.productType = productType;
        this.priceMinor = priceMinor;
        this.currency = currency;
        this.status = status;
    }


}