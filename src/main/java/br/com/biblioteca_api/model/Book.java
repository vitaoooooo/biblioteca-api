package br.com.biblioteca_api.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(unique = true, length = 20)
    private String isbn;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 150)
    private String author;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    /** Quantidade disponível para novos empréstimos. */
    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private int totalQuantity;

    @Column(nullable = false)
    private boolean active = true;

    protected Book() {
        // Necessário para o JPA
    }

    public Book(String isbn, String name, String author, BigDecimal price, int quantity){
        this.isbn = isbn;
        this.name = name;
        this.author = author;
        this.price = price;
        this.quantity = quantity;
        this.totalQuantity = quantity;
    }

    public void update(String isbn, String name, String author, BigDecimal price, int totalQuantity, int activeLoans) {
        if (totalQuantity < activeLoans) {
            throw new IllegalArgumentException("A quantidade total não pode ser menor que os empréstimos em aberto.");
        }
        this.isbn = isbn;
        this.name = name;
        this.author = author;
        this.price = price;
        this.totalQuantity = totalQuantity;
        this.quantity = totalQuantity - activeLoans;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getIsbn() { return isbn; }
    public int getTotalQuantity() { return totalQuantity; }
    public boolean isActive() { return active; }
    public void deactivate() { this.active = false; }
    public void reactivate() { this.active = true; }

}
