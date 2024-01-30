package tech.ayot.ticket.backend.dto.product.request;

import jakarta.validation.constraints.NotBlank;

public record CreateProductRequest(
    @NotBlank String name,
    String description,
    String imageId
) {
    public CreateProductRequest(String name, String description, String imageId) {
        this.name = name;
        this.description = description;
        this.imageId = imageId;
    }
}
