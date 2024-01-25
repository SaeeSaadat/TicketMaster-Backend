package tech.ayot.ticket.backend.dto.product.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateProductRequest(
    @NotNull Long version,
    String description,
    UUID imageId
){
    public UpdateProductRequest(Long version, String description, UUID imageId) {
        this.version = version;
        this.description = description;
        this.imageId = imageId;
    }
}
