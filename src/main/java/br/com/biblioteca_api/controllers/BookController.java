package br.com.biblioteca_api.controllers;

import br.com.biblioteca_api.dto.BookRequest;
import br.com.biblioteca_api.dto.BookResponse;
import br.com.biblioteca_api.dto.PageResponse;
import br.com.biblioteca_api.model.Book;
import br.com.biblioteca_api.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/books")
@Validated
public class BookController {

    private BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<BookResponse> create(@RequestBody @Valid BookRequest request) {
        Book book = bookService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(BookResponse.from(book));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<BookResponse>> listAll() {
        return ResponseEntity.ok(bookService.listAll().stream().map(BookResponse::from).toList());
    }

    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public PageResponse<BookResponse> listPage(@RequestParam(defaultValue = "0") @Min(0) int page,
                                               @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return PageResponse.from(bookService.listPage(PageRequest.of(page, size)), BookResponse::from);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> findById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(BookResponse.from(bookService.findById(id)));
    }

    @GetMapping("/search")
    public ResponseEntity<List<BookResponse>> search(@RequestParam @NotBlank @Size(max = 100) String name) {
        return ResponseEntity.ok(bookService.searchByName(name).stream().map(BookResponse::from).toList());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponse> update(
            @PathVariable @Positive Long id,
            @RequestBody @Valid BookRequest request
    ) {
        return ResponseEntity.ok(BookResponse.from(bookService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
