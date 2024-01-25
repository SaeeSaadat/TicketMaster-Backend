package tech.ayot.ticket.backend.dto.product.response;

import java.util.UUID;

public record ViewProductResponse(
    Long version,
    String name,
    String description,
    UUID imageId
) {
}
