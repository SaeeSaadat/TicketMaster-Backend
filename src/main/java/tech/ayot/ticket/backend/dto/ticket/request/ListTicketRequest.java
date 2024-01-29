package tech.ayot.ticket.backend.dto.ticket.request;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Sort;
import tech.ayot.ticket.backend.model.enumuration.TicketStatus;
import tech.ayot.ticket.backend.model.enumuration.TicketType;

import java.util.Date;

public record ListTicketRequest(
    @NotNull Integer page,
    Integer pageSize,
    String order,
    Sort.Direction direction,
    TicketType type,
    String productName,
    Date createdAfter,
    Date createdBefore,
    TicketStatus status
) {
    public ListTicketRequest(
        Integer page,
        Integer pageSize,
        String order,
        Sort.Direction direction,
        TicketType type,
        String productName,
        Date createdAfter,
        Date createdBefore,
        TicketStatus status
    ) {
        this.page = page;
        this.pageSize = pageSize;
        this.order = order;
        this.direction = direction;
        this.type = type;
        this.productName = productName;
        this.createdAfter = createdAfter;
        this.createdBefore = createdBefore;
        this.status = status;
    }
}
