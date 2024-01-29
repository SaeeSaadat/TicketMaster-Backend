package tech.ayot.ticket.backend.dto.ticket.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tech.ayot.ticket.backend.model.enumuration.TicketType;

import java.util.Date;

public record CreateTicketRequest(
    @NotNull TicketType type,
    @NotBlank String title,
    @NotBlank String description,
    Date deadline
) {
    public CreateTicketRequest(
        TicketType type,
        String title,
        String description,
        Date deadline
    ) {
        this.type = type;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
    }
}
