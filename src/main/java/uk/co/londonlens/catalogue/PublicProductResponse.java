package uk.co.londonlens.catalogue;

public record PublicProductResponse(
    Long id,
    String name,
    String slug,
    String shortDescription,
    String productType,
    Long priceMinor,
    String currency
) {
    public static PublicProductResponse from(Product product) {
        return new PublicProductResponse(
            product.getId(),
            product.getName(),
            product.getSlug(),
            product.getShortDescription(),
            product.getProductType(),
            product.getPriceMinor(),
            product.getCurrency()
        );
    }
}