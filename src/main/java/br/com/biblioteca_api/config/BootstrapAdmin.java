package br.com.biblioteca_api.config;

import br.com.biblioteca_api.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class BootstrapAdmin implements ApplicationRunner {
    private final UserService userService;
    private final String email;
    private final String password;
    public BootstrapAdmin(UserService userService,
                          @Value("${app.bootstrap-admin.email:}") String email,
                          @Value("${app.bootstrap-admin.password:}") String password) {
        this.userService = userService; this.email = email; this.password = password;
    }
    @Override public void run(ApplicationArguments args) { userService.bootstrapAdmin(email, password); }
}
