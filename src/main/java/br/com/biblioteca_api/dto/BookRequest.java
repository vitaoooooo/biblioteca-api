package br.com.biblioteca_api.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record BookRequest(

        @Pattern(regexp = "^(?:97[89])?\\d{9}[\\dXx]$", message = "Informe um ISBN válido (somente números ou X).")
        String isbn,

        @NotBlank(message = "O nome é obrigatório.")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres.")
        String name,

        @NotBlank(message = "O autor é obrigatório.")
        @Size(max = 150, message = "O autor deve ter no máximo 150 caracteres.")
        String author,

        @NotNull(message = "O preço é obrigatório.")
        @PositiveOrZero(message = "O preço não pode ser negativo.")
        BigDecimal price,

        @NotNull(message = "A quantidade é obrigatória.")
        @PositiveOrZero(message = "A quantidade não pode ser negativa.")
        @Max(value = 1000000, message = "A quantidade informada é inválida.")
        Integer quantity
) {
}
