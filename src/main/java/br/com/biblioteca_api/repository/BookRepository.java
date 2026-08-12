package br.com.biblioteca_api.repository;

import br.com.biblioteca_api.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByNameContainingIgnoreCase(String name);
}