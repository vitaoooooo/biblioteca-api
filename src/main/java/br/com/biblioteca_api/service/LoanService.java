package br.com.biblioteca_api.service;

import br.com.biblioteca_api.dto.LoanRequest;
import br.com.biblioteca_api.model.Book;
import br.com.biblioteca_api.model.Client;
import br.com.biblioteca_api.model.Loan;
import br.com.biblioteca_api.repository.BookRepository;
import br.com.biblioteca_api.repository.ClientRepository;
import br.com.biblioteca_api.repository.LoanRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final ClientRepository clientRepository;
    private final BookRepository bookRepository;

    public LoanService(
            LoanRepository loanRepository,
            ClientRepository clientRepository,
            BookRepository bookRepository
    ) {
        this.loanRepository = loanRepository;
        this.clientRepository = clientRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public Loan createLoan(LoanRequest request) {
        Client client = clientRepository.findById(request.clientId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));

        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new IllegalArgumentException("Livro não encontrado."));

        if (book.getQuantity() <= 0) {
            throw new IllegalStateException("Não há exemplares disponíveis deste livro.");
        }

        book.setQuantity(book.getQuantity() - 1);

        Loan loan = new Loan(client, book);

        // O @Transactional garante que estoque e empréstimo sejam salvos juntos.
        bookRepository.save(book);
        return loanRepository.save(loan);
    }

    @Transactional
    public Loan returnLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Empréstimo não encontrado."));

        loan.markAsReturned();

        Book book = loan.getBook();
        book.setQuantity(book.getQuantity() + 1);

        bookRepository.save(book);
        return loanRepository.save(loan);
    }

    public List<Loan> listLoans() {
        return loanRepository.findAll();
    }

    public List<Loan> listLateLoans() {
        return loanRepository.findByReturnedFalseAndDueDateBefore(java.time.LocalDate.now());
    }
}