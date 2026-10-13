package br.com.biblioteca_api.dto;

import br.com.biblioteca_api.model.Book;
import java.math.BigDecimal;

public record BookResponse(Long id, String isbn, String name, String author, BigDecimal price,
                           int quantity, int totalQuantity, boolean active) {
    public static BookResponse from(Book book) {
        return new BookResponse(book.getId(), book.getIsbn(), book.getName(), book.getAuthor(), book.getPrice(),
                book.getQuantity(), book.getTotalQuantity(), book.isActive());
    }
}
