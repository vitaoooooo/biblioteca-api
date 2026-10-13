package br.com.biblioteca_api.controllers;

import br.com.biblioteca_api.dto.UserResponse;
import br.com.biblioteca_api.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    private final UserService service;
    public AccountController(UserService service) { this.service = service; }
    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {
        return UserResponse.from(service.findByEmail(authentication.getName()));
    }
}
