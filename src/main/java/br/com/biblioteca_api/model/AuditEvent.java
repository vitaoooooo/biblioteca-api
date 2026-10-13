package br.com.biblioteca_api.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "audit_event")
public class AuditEvent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 254) private String actor;
    @Column(nullable = false, length = 60) private String action;
    @Column(nullable = false, length = 60) private String resourceType;
    private Long resourceId;
    @Column(nullable = false) private Instant occurredAt;
    protected AuditEvent() { }
    public AuditEvent(String actor, String action, String resourceType, Long resourceId) {
        this.actor = actor; this.action = action; this.resourceType = resourceType; this.resourceId = resourceId;
        this.occurredAt = Instant.now();
    }
}
