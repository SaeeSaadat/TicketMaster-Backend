package tech.ayot.ticket.backend.dto.ticket.request;

import jakarta.validation.constraints.NotNull;

public record CreateMessageRequest(
    @NotNull String content
) {
    public CreateMessageRequest(String content) {
        this.content = content;
    }
}
