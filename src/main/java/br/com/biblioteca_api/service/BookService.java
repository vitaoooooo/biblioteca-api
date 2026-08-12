package br.com.biblioteca_api.service;

import br.com.biblioteca_api.dto.BookRequest;
import br.com.biblioteca_api.model.Book;
import br.com.biblioteca_api.repository.BookRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book create(BookRequest request) {
        Book book = new Book(
                request.name(),
                request.author(),
                request.price(),
                request.quantity()
        );
        return bookRepository.save(book);
    }

    public List<Book> listAll() {
        return bookRepository.findAll();
    }

    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Livro nao encontrado."
                ));
    }

    public List<Book> searchByName(String name) {
        return bookRepository.findByNameContainingIgnoreCase(name);
    }

    public Book update(Long id, BookRequest request) {
        Book book = findById(id);

        book.update(
                request.name(),
                request.author(),
                request.price(),
                request.quantity()
        );
        return bookRepository.save(book);
    }

    public void delete(Long id) {
        Book book = findById(id);

        if(book.getQuantity()>0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Não é possivel excluir um livro ainda possui exemplares."
            );
        }
        bookRepository.delete(book);
    }
}
