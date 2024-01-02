package tech.ayot.ticket.backend.model.ticket;

import jakarta.persistence.*;
import tech.ayot.ticket.backend.model.BaseModel;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "tickets")
public abstract class Ticket extends BaseModel {

    @Column(length = 64, nullable = false)
    private String name;

    @Column(length = 4096)
    private String description;

}
