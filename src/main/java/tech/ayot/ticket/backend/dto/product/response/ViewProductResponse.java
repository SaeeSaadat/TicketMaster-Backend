package tech.ayot.ticket.backend.dto.product.response;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record ViewProductResponse (
    @NotBlank String name,
    String description,
    UUID imageId
){
}
