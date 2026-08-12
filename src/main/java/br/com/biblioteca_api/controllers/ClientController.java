package br.com.biblioteca_api.controllers;

import br.com.biblioteca_api.dto.ClientRequest;
import br.com.biblioteca_api.model.Client;
import br.com.biblioteca_api.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    public ResponseEntity<Client> create(@RequestBody @Valid ClientRequest request) {
        Client client = clientService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(client);
    }

    @GetMapping
    public ResponseEntity<List<Client>> listAll() {
        return ResponseEntity.ok(clientService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> findById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.findById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Client>> search(@RequestParam String name) {
        return ResponseEntity.ok(clientService.searchByName(name));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> update(
            @PathVariable Long id,
            @RequestBody @Valid ClientRequest request
    ) {
        return ResponseEntity.ok(clientService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
