package br.com.biblioteca_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank(message = "O email é obrigatorio") @Email(message = "Informe um email valido") String email,
    @NotBlank(message = "A senha é obrigatorio") String password
) {}
