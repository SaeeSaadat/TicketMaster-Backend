package tech.ayot.ticket.backend.dto.ticket.response;

import java.util.List;

public record ListUserTicketsProductsResponse(
    List<String> content
) {
}
