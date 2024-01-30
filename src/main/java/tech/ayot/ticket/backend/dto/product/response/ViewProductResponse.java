package tech.ayot.ticket.backend.dto.product.response;

public record ViewProductResponse(
    Long version,
    String name,
    String description,
    String imageId
) {
}
