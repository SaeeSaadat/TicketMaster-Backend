package tech.ayot.ticket.backend.dto.ticket.response;

import tech.ayot.ticket.backend.dto.ticket.TicketDto;

import java.util.List;

public record ListTicketResponse(
    Integer totalPages,
    Integer pageNumber,
    Integer pageSize,
    List<TicketDto> content
) {
}
