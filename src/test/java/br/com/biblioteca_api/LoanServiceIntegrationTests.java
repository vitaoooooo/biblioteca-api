package br.com.biblioteca_api;

import br.com.biblioteca_api.dto.BookRequest;
import br.com.biblioteca_api.dto.ClientRequest;
import br.com.biblioteca_api.dto.LoanRequest;
import br.com.biblioteca_api.model.Book;
import br.com.biblioteca_api.model.Client;
import br.com.biblioteca_api.repository.BookRepository;
import br.com.biblioteca_api.repository.ClientRepository;
import br.com.biblioteca_api.repository.LoanRepository;
import br.com.biblioteca_api.repository.ReturnedLoanRepository;
import br.com.biblioteca_api.service.BookService;
import br.com.biblioteca_api.service.ClientService;
import br.com.biblioteca_api.service.LoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class LoanServiceIntegrationTests {
    @Autowired BookService bookService;
    @Autowired ClientService clientService;
    @Autowired LoanService loanService;
    @Autowired LoanRepository loanRepository;
    @Autowired ReturnedLoanRepository returnedLoanRepository;
    @Autowired BookRepository bookRepository;
    @Autowired ClientRepository clientRepository;

    @BeforeEach void cleanDatabase() {
        returnedLoanRepository.deleteAll();
        loanRepository.deleteAll();
        bookRepository.deleteAll();
        clientRepository.deleteAll();
    }

    @Test void loanAndReturnKeepInventoryAndHistoryConsistent() {
        Book book = bookService.create(new BookRequest("9781234567897", "Clean Code", "Robert Martin", new BigDecimal("99.90"), 1));
        Client client = clientService.create(new ClientRequest("Ana", "ana@example.test", "11999999999"));

        var loan = loanService.createLoan(new LoanRequest(client.getId(), book.getId()));
        assertThat(bookRepository.findById(book.getId()).orElseThrow().getQuantity()).isZero();
        assertThatThrownBy(() -> loanService.createLoan(new LoanRequest(client.getId(), book.getId())))
                .isInstanceOf(ResponseStatusException.class).hasMessageContaining("Não há exemplares");

        loanService.returnLoan(loan.getId());
        assertThat(bookRepository.findById(book.getId()).orElseThrow().getQuantity()).isEqualTo(1);
        assertThat(loanRepository.findById(loan.getId()).orElseThrow().isReturned()).isTrue();
        assertThat(loanService.listLoans()).isEmpty();
        assertThat(loanService.listReturnedLoans()).hasSize(1);
        assertThatThrownBy(() -> loanService.returnLoan(loan.getId()))
                .isInstanceOf(ResponseStatusException.class).hasMessageContaining("já foi devolvido");
    }
}
