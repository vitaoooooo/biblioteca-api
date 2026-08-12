package br.com.biblioteca_api.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne(optional = false)
    @JoinColumn(name = "book_id")
    private Book book;

    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnedDate;

    private boolean returned;

    protected Loan() {
        // Necessário para o JPA
    }

    public Loan(Client client, Book book) {
        this.client = client;
        this.book = book;
        this.loanDate = LocalDate.now();
        this.dueDate = loanDate.plusDays(7);
        this.returned = false;
    }

    public void markAsReturned() {
        if (returned) {
            throw new IllegalStateException("Este empréstimo já foi devolvido.");
        }

        this.returned = true;
        this.returnedDate = LocalDate.now();
    }

    public boolean isLate() {
        return !returned && LocalDate.now().isAfter(dueDate);
    }

    public Long getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public Book getBook() {
        return book;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnedDate() {
        return returnedDate;
    }

    public boolean isReturned() {
        return returned;
    }
}