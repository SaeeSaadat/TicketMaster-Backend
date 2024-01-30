package tech.ayot.ticket.backend.dto.ticket.response;

import tech.ayot.ticket.backend.dto.ticket.MessageDto;
import tech.ayot.ticket.backend.model.enumuration.TicketStatus;
import tech.ayot.ticket.backend.model.enumuration.TicketType;

import java.util.Date;
import java.util.List;

/**
 * Response body of view ticket request.
 */
public record ViewTicketResponse(
    Long ticketId,
    String username,
    Long productId,
    Date created,
    TicketType type,
    String title,
    String description,
    Date deadline,
    TicketStatus status,
    List<MessageDto> messages
) {
}
