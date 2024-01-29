package tech.ayot.ticket.backend.dto.ticket.request;

import jakarta.validation.constraints.NotNull;
import tech.ayot.ticket.backend.model.enumuration.TicketStatus;

public record UpdateTicketRequest(
    @NotNull TicketStatus status
) {
    public UpdateTicketRequest(
        TicketStatus status
    ) {
        this.status = status;
    }
}
