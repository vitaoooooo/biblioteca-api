package br.com.biblioteca_api.repository;


import br.com.biblioteca_api.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findByNameContainingIgnoreCase(String name);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);
}
