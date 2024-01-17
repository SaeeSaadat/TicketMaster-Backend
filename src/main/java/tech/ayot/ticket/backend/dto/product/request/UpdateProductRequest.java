package tech.ayot.ticket.backend.dto.product.request;

import java.util.UUID;

public record UpdateProductRequest(
    Long productId,
    String description,
    UUID imageId
){
    public UpdateProductRequest(Long productId, String description, UUID imageId) {
        this.productId = productId;
        this.description = description;
        this.imageId = imageId;
    }
}
