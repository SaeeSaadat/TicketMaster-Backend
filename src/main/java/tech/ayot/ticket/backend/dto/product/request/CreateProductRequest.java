package tech.ayot.ticket.backend.dto.product.request;

import jakarta.validation.constraints.NotBlank;


import java.util.UUID;

public record CreateProductRequest(
    @NotBlank String name,
    String description,
    UUID imageId
) {
    public CreateProductRequest(String name, String description, UUID imageId) {
        this.name = name;
        this.description = description;
        this.imageId = imageId;
    }
}
