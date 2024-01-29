package tech.ayot.ticket.backend.dto.ticket;

import tech.ayot.ticket.backend.model.enumuration.TicketStatus;
import tech.ayot.ticket.backend.model.enumuration.TicketType;

import java.util.Date;

public record TicketDto(
    TicketType type,
    String title,
    String description,
    Date deadline,
    TicketStatus status,
    Long productId,
    String productName
) {
}
