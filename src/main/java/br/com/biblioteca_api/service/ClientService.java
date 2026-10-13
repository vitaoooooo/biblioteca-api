package br.com.biblioteca_api.service;

import br.com.biblioteca_api.dto.ClientRequest;
import br.com.biblioteca_api.model.Client;
import br.com.biblioteca_api.model.Loan;
import br.com.biblioteca_api.model.ReturnedLoan;
import br.com.biblioteca_api.repository.ClientRepository;
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
public class ClientService {

    private final ClientRepository clientRepository;
    private final LoanRepository loanRepository;
    private final ReturnedLoanRepository returnedLoanRepository;
    private final AuditService auditService;

    public ClientService(
            ClientRepository clientRepository,
            LoanRepository loanRepository,
            ReturnedLoanRepository returnedLoanRepository,
            AuditService auditService
    ) {
        this.clientRepository = clientRepository;
        this.loanRepository = loanRepository;
        this.returnedLoanRepository = returnedLoanRepository;
        this.auditService = auditService;
    }

    public Client create(ClientRequest request) {
        String email = normalizeEmail(request.email());
        if (clientRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um cliente com este e-mail.");
        }

        Client client = new Client(request.name().trim(), email, request.phoneNumber());
        Client saved = clientRepository.save(client);
        auditService.record("CLIENT_CREATED", "CLIENT", saved.getId());
        return saved;
    }

    public List<Client> listAll() {
        return clientRepository.findByActiveTrue();
    }

    public Page<Client> listPage(Pageable pageable) { return clientRepository.findByActiveTrue(pageable); }

    public Client findById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cliente não encontrado."
                ));
        if (!client.isActive()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado.");
        }
        return client;
    }

    public List<Client> searchByName(String name) {
        return clientRepository.findByNameContainingIgnoreCaseAndActiveTrue(name.trim());
    }

    public Client update(Long id, ClientRequest request) {
        Client client = findById(id);

        String email = normalizeEmail(request.email());
        if (clientRepository.existsByEmailAndIdNot(email, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um cliente com este e-mail.");
        }

        client.update(request.name().trim(), email, request.phoneNumber());
        Client saved = clientRepository.save(client);
        auditService.record("CLIENT_UPDATED", "CLIENT", saved.getId());
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        Client client = findById(id);

        if (loanRepository.existsByClientIdAndReturnedFalse(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Não é possível excluir este cliente porque ele possui empréstimos em aberto."
            );
        }

        client.deactivate();
        clientRepository.save(client);
        auditService.record("CLIENT_DEACTIVATED", "CLIENT", client.getId());
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(java.util.Locale.ROOT);
    }
}
