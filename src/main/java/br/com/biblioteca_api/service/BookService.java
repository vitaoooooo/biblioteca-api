package br.com.biblioteca_api.service;

import br.com.biblioteca_api.dto.BookRequest;
import br.com.biblioteca_api.model.Book;
import br.com.biblioteca_api.model.Loan;
import br.com.biblioteca_api.model.ReturnedLoan;
import br.com.biblioteca_api.repository.BookRepository;
import br.com.biblioteca_api.repository.LoanRepository;
import br.com.biblioteca_api.repository.ReturnedLoanRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final ReturnedLoanRepository returnedLoanRepository;
    private final AuditService auditService;

    public BookService(
            BookRepository bookRepository,
            LoanRepository loanRepository,
            ReturnedLoanRepository returnedLoanRepository,
            AuditService auditService
    ) {
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
        this.returnedLoanRepository = returnedLoanRepository;
        this.auditService = auditService;
    }

    public Book create(BookRequest request) {
        String isbn = normalizeIsbn(request.isbn());
        if (isbn != null && bookRepository.existsByIsbn(isbn)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um livro com este ISBN.");
        }
        Book book = new Book(
                isbn,
                request.name(),
                request.author(),
                request.price(),
                request.quantity()
        );
        Book saved = bookRepository.save(book);
        auditService.record("BOOK_CREATED", "BOOK", saved.getId());
        return saved;
    }

    public List<Book> listAll() {
        return bookRepository.findByActiveTrue();
    }

    public Page<Book> listPage(Pageable pageable) { return bookRepository.findByActiveTrue(pageable); }

    public Book findById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Livro nao encontrado."
                ));
        if (!book.isActive()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Livro nao encontrado.");
        }
        return book;
    }

    public List<Book> searchByName(String name) {
        return bookRepository.findByNameContainingIgnoreCaseAndActiveTrue(name.trim());
    }

    public Book update(Long id, BookRequest request) {
        Book book = findById(id);

        String isbn = normalizeIsbn(request.isbn());
        if (isbn != null && bookRepository.existsByIsbnAndIdNot(isbn, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um livro com este ISBN.");
        }
        int activeLoans = Math.toIntExact(loanRepository.countByBookIdAndReturnedFalse(id));
        try {
            book.update(
                isbn,
                request.name(),
                request.author(),
                request.price(),
                request.quantity(),
                activeLoans
            );
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage(), ex);
        }
        Book saved = bookRepository.save(book);
        auditService.record("BOOK_UPDATED", "BOOK", saved.getId());
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        Book book = findById(id);

        if (loanRepository.existsByBookIdAndReturnedFalse(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Não é possível excluir este livro porque existem empréstimos em aberto."
            );
        }

        book.deactivate();
        bookRepository.save(book);
        auditService.record("BOOK_DEACTIVATED", "BOOK", book.getId());
    }

    private String normalizeIsbn(String isbn) {
        if (isbn == null || isbn.isBlank()) return null;
        return isbn.replace("-", "").trim().toUpperCase();
    }
}
