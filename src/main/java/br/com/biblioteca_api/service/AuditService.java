package br.com.biblioteca_api.service;

import br.com.biblioteca_api.model.AuditEvent;
import br.com.biblioteca_api.repository.AuditEventRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuditService {
    private final AuditEventRepository repository;
    public AuditService(AuditEventRepository repository) { this.repository = repository; }
    public void record(String action, String resourceType, Long resourceId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String actor = auth == null || !auth.isAuthenticated() ? "system" : auth.getName();
        repository.save(new AuditEvent(actor, action, resourceType, resourceId));
    }
}
