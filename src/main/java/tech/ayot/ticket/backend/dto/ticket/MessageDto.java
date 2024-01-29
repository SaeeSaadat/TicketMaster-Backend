package tech.ayot.ticket.backend.dto.ticket;

import java.util.Date;

public record MessageDto(
    Long id,
    Long userId,
    String username,
    String content,
    Date date
) {
}
