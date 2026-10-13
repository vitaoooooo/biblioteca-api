package br.com.biblioteca_api.controllers;

import br.com.biblioteca_api.dto.UserRequest;
import br.com.biblioteca_api.dto.UserResponse;
import br.com.biblioteca_api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
    private final UserService service;
    public UserController(UserService service) { this.service = service; }
    @PostMapping public ResponseEntity<UserResponse> create(@RequestBody @Valid UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(service.create(request)));
    }
    @GetMapping public List<UserResponse> list() { return service.list().stream().map(UserResponse::from).toList(); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable Long id) { service.deactivate(id); }
}
