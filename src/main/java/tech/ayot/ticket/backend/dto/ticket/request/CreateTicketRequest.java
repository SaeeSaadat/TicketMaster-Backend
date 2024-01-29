package tech.ayot.ticket.backend.dto.ticket.request;

import jakarta.validation.constraints.NotBlank;
import java.util.Date;

public record CreateTicketRequest (
    @NotBlank String title,
    String description,
    String productName,
    Date deadline,
    String type

) {
    public CreateTicketRequest(String title, String description, String productName, Date deadline, String type) {
        this.title = title;
        this.description = description;
        this.productName = productName;
        this.deadline = deadline;
        this.type = type;
    }
}
