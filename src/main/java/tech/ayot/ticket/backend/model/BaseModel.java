package tech.ayot.ticket.backend.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tech.ayot.ticket.backend.model.user.User;

import java.util.Date;

/**
 * Represents base model for entities
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseModel {

    /**
     * The entity's id
     */
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    /**
     * The entity's version
     * <p>
     * This value is used to ensure integrity when updating the entity
     * </p>
     */
    @Version
    @Column(nullable = false)
    protected Integer version;

    /**
     * The user who created the entity
     */
    @CreatedBy
    @ManyToOne
    protected User createdBy;

    /**
     * The creation date of the entity
     */
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    protected Date creationDate;

    /**
     * The user who last updated the entity
     */
    @LastModifiedBy
    @ManyToOne
    protected User lastModifiedBy;

    /**
     * The date of the last update to the entity
     */
    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    protected Date lastModifiedDate;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer crc) {
        this.version = crc;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public User getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(User lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
