package br.com.biblioteca_api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record LoanRequest(
        @Positive(message = "O id do cliente deve ser positivo.")
        Long clientId,

        @NotNull(message = "O id do livro é obrigatório.")
        @Positive(message = "O id do livro deve ser positivo.")
        Long bookId
) {
}
