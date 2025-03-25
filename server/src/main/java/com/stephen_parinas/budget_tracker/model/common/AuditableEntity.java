package com.stephen_parinas.budget_tracker.model.common;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/**
 * The base class for entities that can be created and modified.
 */
@MappedSuperclass
public abstract class AuditableEntity {
    /**
     * The unique identifier of the entity.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * The date that the entity was created.
     */
    @Column(nullable = false, updatable = false)
    private Instant createdDate;

    /**
     * The date that the entity was last modified.
     */
    @Column(nullable = false)
    private Instant modifiedDate;

    /**
     * Automatically set the createdDate before persisting.
     */
    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdDate = now;
        this.modifiedDate = now;
    }

    /**
     * Automatically update the modifiedDate before updating.
     */
    @PreUpdate
    protected void onUpdate() {
        this.modifiedDate = Instant.now();
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public Instant getModifiedDate() {
        return modifiedDate;
    }
}
