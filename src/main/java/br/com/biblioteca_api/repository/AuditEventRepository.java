package br.com.biblioteca_api.repository;

import br.com.biblioteca_api.model.AuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditEventRepository extends JpaRepository<AuditEvent, Long> { }
