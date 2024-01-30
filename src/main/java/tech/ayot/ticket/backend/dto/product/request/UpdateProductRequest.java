package tech.ayot.ticket.backend.dto.product.request;

import jakarta.validation.constraints.NotNull;

public record UpdateProductRequest(
    @NotNull Long version,
    String description,
    String imageId
){
    public UpdateProductRequest(Long version, String description, String imageId) {
        this.version = version;
        this.description = description;
        this.imageId = imageId;
    }
}
