package br.com.biblioteca_api.service;

import br.com.biblioteca_api.dto.LoanRequest;
import br.com.biblioteca_api.model.Book;
import br.com.biblioteca_api.model.Client;
import br.com.biblioteca_api.model.Loan;
import br.com.biblioteca_api.model.ReturnedLoan;
import br.com.biblioteca_api.repository.BookRepository;
import br.com.biblioteca_api.repository.ClientRepository;
import br.com.biblioteca_api.repository.LoanRepository;
import br.com.biblioteca_api.repository.ReturnedLoanRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Value;
import br.com.biblioteca_api.model.AppUser;
import br.com.biblioteca_api.model.Role;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final ClientRepository clientRepository;
    private final BookRepository bookRepository;
    private final ReturnedLoanRepository returnedLoanRepository;
    private final Clock clock;
    private final int loanDays;
    private final AuditService auditService;
    private final UserService userService;

    public LoanService(
            LoanRepository loanRepository,
            ClientRepository clientRepository,
            BookRepository bookRepository,
            ReturnedLoanRepository returnedLoanRepository,
            Clock clock,
            @Value("${library.loan.days:7}") int loanDays,
            AuditService auditService,
            UserService userService
    ) {
        this.loanRepository = loanRepository;
        this.clientRepository = clientRepository;
        this.bookRepository = bookRepository;
        this.returnedLoanRepository = returnedLoanRepository;
        this.clock = clock;
        this.loanDays = loanDays;
        this.auditService = auditService;
        this.userService = userService;
    }


    @Transactional
    public Loan createLoan(LoanRequest request, String authenticatedEmail) {
        AppUser user = userService.findByEmail(authenticatedEmail);

        Client client;

        if (user.getRole() == Role.USER) {
            client = user.getClient();

            if (client == null) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Sua conta ainda não possui um cadastro de cliente vinculado."
                );
            }
        } else {
            if (request.clientId() == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "O id do cliente é obrigatório para o administrador."
                );
            }

            client = clientRepository.findById(request.clientId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Cliente não encontrado."
                    ));
        }

        if (!client.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cliente está inativo."
            );
        }

        Book book = bookRepository.findByIdForUpdate(request.bookId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Livro não encontrado."
                ));

        if (!book.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Livro está inativo."
            );
        }

        if (book.getQuantity() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Não há exemplares disponíveis deste livro."
            );
        }

        book.setQuantity(book.getQuantity() - 1);

        LocalDate loanDate = LocalDate.now(clock);
        Loan loan = new Loan(client, book, loanDate, loanDate.plusDays(loanDays));

        bookRepository.save(book);
        Loan saved = loanRepository.save(loan);
        auditService.record("LOAN_CREATED", "LOAN", saved.getId());

        return saved;
    }

    @Transactional
    public Loan returnLoan(Long loanId, String authenticatedEmail) {
        AppUser user = userService.findByEmail(authenticatedEmail);

        Loan loan = loanRepository.findByIdForUpdate(loanId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Empréstimo não encontrado."
                ));

        if (user.getRole() == Role.USER) {
            Client client = user.getClient();

            if (client == null
                    || !client.getId().equals(loan.getClient().getId())) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Você só pode devolver seus próprios empréstimos."
                );
            }
        }

        try {
            loan.markAsReturned();
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    ex.getMessage(),
                    ex
            );
        }

        ReturnedLoan returnedLoan = new ReturnedLoan(loan);
        Book book = loan.getBook();
        book.setQuantity(book.getQuantity() + 1);

        returnedLoanRepository.save(returnedLoan);
        bookRepository.save(book);

        Loan saved = loanRepository.save(loan);
        auditService.record("LOAN_RETURNED", "LOAN", saved.getId());

        return saved;
    }

    @Transactional
    public List<Loan> listLoans(String authenticatedEmail) {
        AppUser user = userService.findByEmail(authenticatedEmail);

        if (user.getRole() == Role.ADMIN) {
            return loanRepository.findByReturnedFalse();
        }

        Client client = user.getClient();

        if (client == null) {
            return List.of();
        }

        return loanRepository.findByClientIdAndReturnedFalse(client.getId());
    }

    public Page<Loan> listLoanPage(Pageable pageable) { return loanRepository.findByReturnedFalse(pageable); }

    public List<Loan> listLateLoans() {
        return loanRepository.findByReturnedFalseAndDueDateBefore(LocalDate.now(clock));
    }

    @Transactional
    public List<ReturnedLoan> listReturnedLoans(String authenticatedEmail) {
        AppUser user = userService.findByEmail(authenticatedEmail);

        if (user.getRole() == Role.ADMIN) {
            return returnedLoanRepository.findAll();
        }

        if (user.getClient() == null) {
            return List.of();
        }

        return returnedLoanRepository.findByClientEmailIgnoreCase(
                user.getClient().getEmail()
        );

    }

    public Page<ReturnedLoan> listReturnedLoanPage(Pageable pageable) { return returnedLoanRepository.findAll(pageable); }
}
