package br.com.biblioteca_api.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "returned_loan")
public class ReturnedLoan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_name", nullable = false)
    private String clientName;

    @Column(name = "client_email", nullable = false)
    private String clientEmail;

    @Column(name = "book_name", nullable = false)
    private String bookName;

    @Column(name = "book_author", nullable = false)
    private String bookAuthor;

    @Column(name = "loan_date", nullable = false)
    private LocalDate loanDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "returned_date", nullable = false)
    private LocalDate returnedDate;

    protected ReturnedLoan() {
        // Necessário para o JPA
    }

    public ReturnedLoan(Loan loan) {
        this.clientName = loan.getClient().getName();
        this.clientEmail = loan.getClient().getEmail();
        this.bookName = loan.getBook().getName();
        this.bookAuthor = loan.getBook().getAuthor();
        this.loanDate = loan.getLoanDate();
        this.dueDate = loan.getDueDate();
        this.returnedDate = loan.getReturnedDate();
    }

    public Long getId() {
        return id;
    }

    public String getClientName() {
        return clientName;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public String getBookName() {
        return bookName;
    }

    public String getBookAuthor() {
        return bookAuthor;
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
}
