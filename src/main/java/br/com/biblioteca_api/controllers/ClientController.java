package br.com.biblioteca_api.controllers;

import br.com.biblioteca_api.dto.ClientRequest;
import br.com.biblioteca_api.dto.ClientResponse;
import br.com.biblioteca_api.dto.PageResponse;
import br.com.biblioteca_api.model.Client;
import br.com.biblioteca_api.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/clients")
@Validated
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ClientResponse> create(@RequestBody @Valid ClientRequest request) {
        Client client = clientService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ClientResponse.from(client));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<ClientResponse>> listAll() {
        return ResponseEntity.ok(clientService.listAll().stream().map(ClientResponse::from).toList());
    }

    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public PageResponse<ClientResponse> listPage(@RequestParam(defaultValue = "0") @Min(0) int page,
                                                 @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return PageResponse.from(clientService.listPage(PageRequest.of(page, size)), ClientResponse::from);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponse> findById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(ClientResponse.from(clientService.findById(id)));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ClientResponse>> search(@RequestParam @NotBlank @Size(max = 100) String name) {
        return ResponseEntity.ok(clientService.searchByName(name).stream().map(ClientResponse::from).toList());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClientResponse> update(
            @PathVariable @Positive Long id,
            @RequestBody @Valid ClientRequest request
    ) {
        return ResponseEntity.ok(ClientResponse.from(clientService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
