package br.com.biblioteca_api.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record BookRequest(

        @NotBlank(message = "O nome é obrigatório.")
        String name,

        @NotBlank(message = "O autor é obrigatório.")
        String author,

        @NotNull(message = "O preço é obrigatório.")
        @PositiveOrZero(message = "O preço não pode ser negativo.")
        double price,

        @NotNull(message = "A quantidade é obrigatória.")
        @PositiveOrZero(message = "A quantidade não pode ser negativa.")
        Integer quantity
) {
}