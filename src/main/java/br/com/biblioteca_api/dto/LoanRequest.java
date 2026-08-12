package br.com.biblioteca_api.dto;

import jakarta.validation.constraints.NotNull;

public record LoanRequest(
        @NotNull(message = "O id do cliente é obrigatório.")
        Long clientId,

        @NotNull(message = "O id do livro é obrigatório.")
        Long bookId
) {
}
