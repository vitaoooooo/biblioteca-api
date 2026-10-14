package br.com.biblioteca_api.controllers;

import br.com.biblioteca_api.dto.LoginRequest;
import br.com.biblioteca_api.dto.RegistrationRequest;
import br.com.biblioteca_api.dto.UserResponse;
import br.com.biblioteca_api.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();

    public AuthController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken csrfToken) {
        return csrfToken;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        var authToken = new UsernamePasswordAuthenticationToken(
                loginRequest.email(),
                loginRequest.password()
        );

        Authentication authentication = authenticationManager.authenticate(authToken);

        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);

        securityContextHolderStrategy.setContext(context);

        securityContextRepository.saveContext(context, request, response);

        return "Usuário autenticado com sucesso!";
    }


    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody @Valid RegistrationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(userService.register(request)));
    }
}
