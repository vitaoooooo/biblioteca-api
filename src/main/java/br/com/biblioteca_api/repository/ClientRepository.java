package br.com.biblioteca_api.repository;


import br.com.biblioteca_api.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findByNameContainingIgnoreCaseAndActiveTrue(String name);

    List<Client> findByActiveTrue();
    Page<Client> findByActiveTrue(Pageable pageable);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);
}
