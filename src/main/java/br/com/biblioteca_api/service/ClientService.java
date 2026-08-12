package br.com.biblioteca_api.service;

import br.com.biblioteca_api.dto.ClientRequest;
import br.com.biblioteca_api.model.Client;
import br.com.biblioteca_api.repository.ClientRepository;
import br.com.biblioteca_api.repository.LoanRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final LoanRepository loanRepository;

    public ClientService(ClientRepository clientRepository, LoanRepository loanRepository) {
        this.clientRepository = clientRepository;
        this.loanRepository = loanRepository;
    }

    public Client create(ClientRequest request) {
        if (clientRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um cliente com este e-mail.");
        }

        Client client = new Client(request.name(), request.email(), request.phoneNumber());
        return clientRepository.save(client);
    }

    public List<Client> listAll() {
        return clientRepository.findAll();
    }

    public Client findById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cliente não encontrado."
                ));
    }

    public List<Client> searchByName(String name) {
        return clientRepository.findByNameContainingIgnoreCase(name);
    }

    public Client update(Long id, ClientRequest request) {
        Client client = findById(id);

        if (clientRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um cliente com este e-mail.");
        }

        client.update(request.name(), request.email(), request.phoneNumber());
        return clientRepository.save(client);
    }

    public void delete(Long id) {
        Client client = findById(id);

        if (loanRepository.existsByClientId(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Não é possível excluir um cliente que possui empréstimos registrados."
            );
        }

        clientRepository.delete(client);
    }
}
