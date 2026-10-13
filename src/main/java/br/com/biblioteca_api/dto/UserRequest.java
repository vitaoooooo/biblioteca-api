package br.com.biblioteca_api.dto;

import br.com.biblioteca_api.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @NotBlank @Size(max = 150) String name,
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @Size(min = 12, max = 72, message = "A senha deve ter entre 12 e 72 caracteres.") String password,
        @NotNull Role role
) { }
