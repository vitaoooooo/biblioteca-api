package br.com.biblioteca_api.service;

import br.com.biblioteca_api.dto.UserRequest;
import br.com.biblioteca_api.dto.RegistrationRequest;
import br.com.biblioteca_api.model.AppUser;
import br.com.biblioteca_api.model.Role;
import br.com.biblioteca_api.repository.AppUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import br.com.biblioteca_api.model.Client;
import br.com.biblioteca_api.repository.ClientRepository;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class UserService implements UserDetailsService {
    private final AppUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final ClientRepository clientRepository;

    public UserService(AppUserRepository repository, PasswordEncoder passwordEncoder, ClientRepository clientRepository) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.clientRepository = clientRepository;
    }
    @Override public UserDetails loadUserByUsername(String email) {
        AppUser user = repository.findByEmailAndActiveTrue(normalize(email))
                .orElseThrow(() -> new UsernameNotFoundException("Usuário inválido."));
        return User.withUsername(user.getEmail()).password(user.getPasswordHash())
                .authorities(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())).build();
    }
    public AppUser create(UserRequest request) {
        String email = normalize(request.email());
        if (repository.existsByEmail(email)) throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um usuário com este e-mail.");
        return repository.save(new AppUser(request.name().trim(), email, passwordEncoder.encode(request.password()), request.role()));
    }
    @Transactional
    public AppUser register(RegistrationRequest request) {
        String email = normalize(request.email());

        if (repository.existsByEmail(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Já existe um usuário com este e-mail."
            );
        }

        if (clientRepository.existsByEmail(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Já existe um cliente com este e-mail."
            );
        }

        Client client = new Client(
                request.name().trim(),
                email,
                null
        );

        client = clientRepository.save(client);

        AppUser user = new AppUser(
                request.name().trim(),
                email,
                passwordEncoder.encode(request.password()),
                Role.USER
        );

        user.setClient(client);

        return repository.save(user);
    }
    public List<AppUser> list() { return repository.findAll(); }
    public AppUser findByEmail(String email) {
        return repository.findByEmail(normalize(email))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));
    }
    public void deactivate(Long id) {
        AppUser user = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));
        if (user.getRole() == Role.ADMIN && user.isActive() && repository.countByRoleAndActiveTrue(Role.ADMIN) <= 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Não é possível inativar o último administrador.");
        }
        user.deactivate(); repository.save(user);
    }
    public void bootstrapAdmin(String email, String password) {
        if (repository.count() != 0) return;
        if (email == null || email.isBlank() || password == null || password.length() < 12) {
            throw new IllegalStateException("Defina APP_ADMIN_EMAIL e APP_ADMIN_PASSWORD (mínimo 12 caracteres) antes da primeira inicialização.");
        }
        repository.save(new AppUser("Administrador inicial", normalize(email), passwordEncoder.encode(password), Role.ADMIN));
    }
    private String normalize(String email) { return email.trim().toLowerCase(Locale.ROOT); }
}
