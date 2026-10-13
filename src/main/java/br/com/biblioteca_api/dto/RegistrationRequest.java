package br.com.biblioteca_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrationRequest(
        @NotBlank(message = "O nome é obrigatório.") @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres.") String name,
        @NotBlank(message = "O e-mail é obrigatório.") @Email(message = "Informe um e-mail válido.") @Size(max = 254) String email,
        @NotBlank @Size(min = 12, max = 72, message = "A senha deve ter entre 12 e 72 caracteres.") String password
) { }
