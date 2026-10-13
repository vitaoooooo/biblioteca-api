[1mdiff --git a/.github/workflows/ci.yml b/.github/workflows/ci.yml[m
[1mnew file mode 100644[m
[1mindex 0000000..a1216dd[m
[1m--- /dev/null[m
[1m+++ b/.github/workflows/ci.yml[m
[36m@@ -0,0 +1,17 @@[m
[32m+[m[32mname: Backend CI[m
[32m+[m
[32m+[m[32mon:[m
[32m+[m[32m  push:[m
[32m+[m[32m  pull_request:[m
[32m+[m
[32m+[m[32mjobs:[m
[32m+[m[32m  test:[m
[32m+[m[32m    runs-on: ubuntu-latest[m
[32m+[m[32m    steps:[m
[32m+[m[32m      - uses: actions/checkout@v4[m
[32m+[m[32m      - uses: actions/setup-java@v4[m
[32m+[m[32m        with:[m
[32m+[m[32m          distribution: temurin[m
[32m+[m[32m          java-version: '17'[m
[32m+[m[32m          cache: maven[m
[32m+[m[32m      - run: ./mvnw verify[m
[1mdiff --git a/Dockerfile b/Dockerfile[m
[1mnew file mode 100644[m
[1mindex 0000000..d2b0345[m
[1m--- /dev/null[m
[1m+++ b/Dockerfile[m
[36m@@ -0,0 +1,6 @@[m
[32m+[m[32mFROM eclipse-temurin:17-jre[m
[32m+[m[32mWORKDIR /app[m
[32m+[m[32mCOPY target/biblioteca-api-0.0.1-SNAPSHOT.jar app.jar[m
[32m+[m[32mEXPOSE 8080[m
[32m+[m[32mUSER 10001[m
[32m+[m[32mENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "/app/app.jar"][m
[1mdiff --git a/README.md b/README.md[m
[1mindex e56f0a5..6fa51cd 100644[m
[1m--- a/README.md[m
[1m+++ b/README.md[m
[36m@@ -2,6 +2,22 @@[m
 [m
 API REST para o gerenciamento de uma biblioteca: livros, clientes e empréstimos. O projeto também inclui uma interface estática inicial.[m
 [m
[32m+[m[32m## Execução segura[m
[32m+[m
[32m+[m[32mO backend usa autenticação HTTP Basic. Na primeira inicialização, informe obrigatoriamente um administrador inicial e as credenciais do banco:[m
[32m+[m
[32m+[m[32m```powershell[m
[32m+[m[32m$env:SPRING_PROFILES_ACTIVE="dev"[m
[32m+[m[32m$env:DB_PASSWORD="uma-senha-do-banco"[m
[32m+[m[32m$env:APP_ADMIN_EMAIL="admin@empresa.com"[m
[32m+[m[32m$env:APP_ADMIN_PASSWORD="uma-senha-forte-com-12-ou-mais-caracteres"[m
[32m+[m[32m.\mvnw.cmd spring-boot:run[m
[32m+[m[32m```[m
[32m+[m
[32m+[m[32mEm produção, use `SPRING_PROFILES_ACTIVE=prod`, um usuário MySQL exclusivo da aplicação e segredos fornecidos pelo ambiente/orquestrador. Não use `root`. As migrations em `src/main/resources/db/migration` são aplicadas pelo Flyway; não altere o esquema manualmente nem habilite `ddl-auto=update`.[m
[32m+[m
[32m+[m[32mRotas `/api/**` exigem um usuário com papel `STAFF` ou `ADMIN`, exceto `POST /api/auth/register`, que permite o autocadastro. Contas criadas por esse endpoint sempre recebem o papel `STAFF`; somente `ADMIN` pode editar/inativar livros, clientes e gerir usuários em `/api/users`. O endpoint público de disponibilidade é `/actuator/health`.[m
[32m+[m
 ## Tecnologias[m
 [m
 - Java 17[m
[36m@@ -26,4 +42,13 @@[m [mPor padrão, a aplicação estará disponível em `http://localhost:8080`.[m
 | Clientes | `/api/clients` |[m
 | Empréstimos | `/api/loans` |[m
 | Empréstimos em atraso | `/api/loans/late` |[m
[32m+[m[32m| Usuários (admin) | `/api/users` |[m
[32m+[m
[32m+[m[32m## Garantias do backend[m
[32m+[m
[32m+[m[32m- Estoque protegido por bloqueio pessimista e versão otimista, evitando empréstimos concorrentes acima do disponível.[m
[32m+[m[32m- Devoluções preservam o empréstimo original e gravam o histórico compatível em separado.[m
[32m+[m[32m- Exclusões passam a ser inativações, preservando rastreabilidade e relacionamentos históricos.[m
[32m+[m[32m- Respostas usam DTOs e erros seguem um corpo padronizado; entidades JPA não são expostas diretamente.[m
[32m+[m[32m- Health checks, métricas e ID de correlação por resposta estão disponíveis para monitoramento.[m
 [m
[1mdiff --git a/pom.xml b/pom.xml[m
[1mindex 67b349a..4ad4db4 100644[m
[1m--- a/pom.xml[m
[1m+++ b/pom.xml[m
[36m@@ -64,6 +64,23 @@[m
 			<groupId>org.springframework.boot</groupId>[m
 			<artifactId>spring-boot-starter-validation</artifactId>[m
 		</dependency>[m
[32m+[m		[32m<dependency>[m
[32m+[m			[32m<groupId>org.springframework.boot</groupId>[m
[32m+[m			[32m<artifactId>spring-boot-starter-security</artifactId>[m
[32m+[m		[32m</dependency>[m
[32m+[m		[32m<dependency>[m
[32m+[m			[32m<groupId>org.springframework.boot</groupId>[m
[32m+[m			[32m<artifactId>spring-boot-starter-actuator</artifactId>[m
[32m+[m		[32m</dependency>[m
[32m+[m		[32m<dependency>[m
[32m+[m			[32m<groupId>org.flywaydb</groupId>[m
[32m+[m			[32m<artifactId>flyway-mysql</artifactId>[m
[32m+[m		[32m</dependency>[m
[32m+[m		[32m<dependency>[m
[32m+[m			[32m<groupId>com.h2database</groupId>[m
[32m+[m			[32m<artifactId>h2</artifactId>[m
[32m+[m			[32m<scope>test</scope>[m
[32m+[m		[32m</dependency>[m
 	</dependencies>[m
 [m
 	<build>[m
[1mdiff --git a/src/main/java/br/com/biblioteca_api/config/BootstrapAdmin.java b/src/main/java/br/com/biblioteca_api/config/BootstrapAdmin.java[m
[1mnew file mode 100644[m
[1mindex 0000000..6b88e45[m
[1m--- /dev/null[m
[1m+++ b/src/main/java/br/com/biblioteca_api/config/BootstrapAdmin.java[m
[36m@@ -0,0 +1,20 @@[m
[32m+[m[32mpackage br.com.biblioteca_api.config;[m
[32m+[m
[32m+[m[32mimport br.com.biblioteca_api.service.UserService;[m
[32m+[m[32mimport org.springframework.beans.factory.annotation.Value;[m
[32m+[m[32mimport org.springframework.boot.ApplicationArguments;[m
[32m+[m[32mimport org.springframework.boot.ApplicationRunner;[m
[32m+[m[32mimport org.springframework.stereotype.Component;[m
[32m+[m
[32m+[m[32m@Component[m
[32m+[m[32mpublic class BootstrapAdmin implements ApplicationRunner {[m
[32m+[m[32m    private final UserService userService;[m
[32m+[m[32m    private final String email;[m
[32m+[m[32m    private final String password;[m
[32m+[m[32m    public BootstrapAdmin(UserService userService,[m
[32m+[m[32m                          @Value("${app.bootstrap-admin.email:}") String email,[m
[32m+[m[32m                          @Value("${app.bootstrap-admin.password:}") String password) {[m
[32m+[m[32m        this.userService = userService; this.email = email; this.password = password;[m
[32m+[m[32m    }[m
[32m+[m[32m    @Override public void run(ApplicationArguments args) { userService.bootstrapAdmin(email, password); }[m
[32m+[m[32m}[m
[1mdiff --git a/src/main/java/br/com/biblioteca_api/config/RequestCorrelationFilter.java b/src/main/java/br/com/biblioteca_api/config/RequestCorrelationFilter.java[m
[1mnew file mode 100644[m
[1mindex 0000000..b872dc0[m
[1m--- /dev/null[m
[1m+++ b/src/main/java/br/com/biblioteca_api/config/RequestCorrelationFilter.java[m
[36m@@ -0,0 +1,24 @@[m
[32m+[m[32mpackage br.com.biblioteca_api.config;[m
[32m+[m
[32m+[m[32mimport jakarta.servlet.FilterChain;[m
[32m+[m[32mimport jakarta.servlet.ServletException;[m
[32m+[m[32mimport jakarta.servlet.http.HttpServletRequest;[m
[32m+[m[32mimport jakarta.servlet.http.HttpServletResponse;[m
[32m+[m[32mimport org.slf4j.MDC;[m
[32m+[m[32mimport org.springframework.stereotype.Component;[m
[32m+[m[32mimport org.springframework.web.filter.OncePerRequestFilter;[m
[32m+[m
[32m+[m[32mimport java.io.IOException;[m
[32m+[m[32mimport java.util.UUID;[m
[32m+[m
[32m+[m[32m@Component[m
[32m+[m[32mpublic class RequestCorrelationFilter extends OncePerRequestFilter {[m
[32m+[m[32m    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)[m
[32m+[m[32m            throws ServletException, IOException {[m
[32m+[m[32m        String correlationId = request.getHeader("X-Correlation-Id");[m
[32m+[m[32m        if (correlationId == null || correlationId.length() > 100) correlationId = UUID.randomUUID().toString();[m
[32m+[m[32m        MDC.put("correlationId", correlationId);[m
[32m+[m[32m        response.setHeader("X-Correlation-Id", correlationId);[m
[32m+[m[32m        try { chain.doFilter(request, response); } finally { MDC.remove("correlationId"); }[m
[32m+[m[32m    }[m
[32m+[m[32m}[m
[1mdiff --git a/src/main/java/br/com/biblioteca_api/config/SecurityConfiguration.java b/src/main/java/br/com/biblioteca_api/config/SecurityConfiguration.java[m
[1mnew file mode 100644[m
[1mindex 0000000..55d065b[m
[1m--- /dev/null[m
[1m+++ b/src/main/java/br/com/biblioteca_api/config/SecurityConfiguration.java[m
[36m@@ -0,0 +1,43 @@[m
[32m+[m[32mpackage br.com.biblioteca_api.config;[m
[32m+[m
[32m+[m[32mimport org.springframework.context.annotation.Bean;[m
[32m+[m[32mimport org.springframework.context.annotation.Configuration;[m
[32m+[m[32mimport org.springframework.security.authentication.AuthenticationManager;[m
[32m+[m[32mimport org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;[m
[32m+[m[32mimport org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;[m
[32m+[m[32mimport org.springframework.security.config.annotation.web.builders.HttpSecurity;[m
[32m+[m[32mimport org.springframework.security.config.http.SessionCreationPolicy;[m
[32m+[m[32mimport org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;[m
[32m+[m[32mimport org.springframework.security.crypto.password.PasswordEncoder;[m
[32m+[m[32mimport org.springframework.security.web.SecurityFilterChain;[m
[32m+[m[32mimport org.springframework.http.HttpMethod;[m
[32m+[m
[32m+[m[32m@Configuration[m
[32m+[m[32m@EnableMethodSecurity[m
[32m+[m[32mpublic class SecurityConfiguration {[m
[32m+[m
[32m+[m[32m    @Bean[m
[32m+[m[32m    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {[m
[32m+[m[32m        return authenticationConfiguration.getAuthenticationManager();[m
[32m+[m[32m    }[m
[32m+[m
[32m+[m[32m    @Bean[m
[32m+[m[32m    PasswordEncoder passwordEncoder() {[m
[32m+[m[32m        return new BCryptPasswordEncoder();[m
[32m+[m[32m    }[m
[32m+[m
[32m+[m[32m    @Bean[m
[32m+[m[32m    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {[m
[32m+[m[32m        return http[m
[32m+[m[32m                .csrf(csrf -> csrf.disable())[m
[32m+[m[32m                .sessionManagement(session ->[m
[32m+[m[32m                        session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))[m
[32m+[m[32m                .authorizeHttpRequests(auth -> auth[m
[32m+[m[32m                        .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()[m
[32m+[m[32m                        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()[m
[32m+[m[32m                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()[m
[32m+[m[32m                        .requestMatchers("/api/**").authenticated()[m
[32m+[m[32m                        .anyRequest().permitAll())[m
[32m+[m[32m                .build();[m
[32m+[m[32m    }[m
[32m+[m[32m}[m
\ No newline at end of file[m
[1mdiff --git a/src/main/java/br/com/biblioteca_api/config/TimeConfiguration.java b/src/main/java/br/com/biblioteca_api/config/TimeConfiguration.java[m
[1mnew file mode 100644[m
[1mindex 0000000..65a332b[m
[1m--- /dev/null[m
[1m+++ b/src/main/java/br/com/biblioteca_api/config/TimeConfiguration.java[m
[36m@@ -0,0 +1,14 @@[m
[32m+[m[32mpackage br.com.biblioteca_api.config;[m
[32m+[m
[32m+[m[32mimport org.springframework.context.annotation.Bean;[m
[32m+[m[32mimport org.springframework.context.annotation.Configuration;[m
[32m+[m
[32m+[m[32mimport java.time.Clock;[m
[32m+[m
[32m+[m[32m@Configuration[m
[32m+[m[32mpublic class TimeConfiguration {[m
[32m+[m[32m    @Bean[m
[32m+[m[32m    Clock clock() {[m
[32m+[m[32m        return Clock.systemUTC();[m
[32m+[m[32m    }[m
[32m+[m[32m}[m
[1mdiff --git a/src/main/java/br/com/biblioteca_api/controllers/AccountController.java b/src/main/java/br/com/biblioteca_api/controllers/AccountController.java[m
[1mnew file mode 100644[m
[1mindex 0000000..9acbd65[m
[1m--- /dev/null[m
[1m+++ b/src/main/java/br/com/biblioteca_api/controllers/AccountController.java[m
[36m@@ -0,0 +1,19 @@[m
[32m+[m[32mpackage br.com.biblioteca_api.controllers;[m
[32m+[m
[32m+[m[32mimport br.com.biblioteca_api.dto.UserResponse;[m
[32m+[m[32mimport br.com.biblioteca_api.service.UserService;[m
[32m+[m[32mimport org.springframework.security.core.Authentication;[m
[32m+[m[32mimport org.springframework.web.bind.annotation.GetMapping;[m
[32m+[m[32mimport org.springframework.web.bind.annotation.RequestMapping;[m
[32m+[m[32mimport org.springframework.web.bind.annotation.RestController;[m
[32m+[m
[32m+[m[32m@RestController[m
[32m+[m[32m@RequestMapping("/api/account")[m
[32m+[m[32mpublic class AccountController {[m
[32m+[m[32m    private final UserService service;[m
[32m+[m[32m    public AccountController(UserService service) { this.service = service; }[m
[32m+[m[32m    @GetMapping("/me")[m
[32m+[m[32m    public UserResponse me(Authentication authentication) {[m
[32m+[m[32m        return UserResponse.from(service.findByEmail(authentication.getName()));[m
[32m+[m[32m    }[m
[32m+[m[32m}[m
[1mdiff --git a/src/main/java/br/com/biblioteca_api/controllers/ApiExceptionHandler.java b/src/main/java/br/com/biblioteca_api/controllers/ApiExceptionHandler.java[m
[1mnew file mode 100644[m
[1mindex 0000000..fdb23e7[m
[1m--- /dev/null[m
[1m+++ b/src/main/java/br/com/biblioteca_api/controllers/ApiExceptionHandler.java[m
[36m@@ -0,0 +1,57 @@[m
[32m+[m[32mpackage br.com.biblioteca_api.controllers;[m
[32m+[m
[32m+[m[32mimport br.com.biblioteca_api.dto.ApiError;[m
[32m+[m[32mimport jakarta.servlet.http.HttpServletRequest;[m
[32m+[m[32mimport jakarta.validation.ConstraintViolationException;[m
[32m+[m[32mimport org.slf4j.Logger;[m
[32m+[m[32mimport org.slf4j.LoggerFactory;[m
[32m+[m[32mimport org.springframework.dao.DataIntegrityViolationException;[m
[32m+[m[32mimport org.springframework.http.HttpStatus;[m
[32m+[m[32mimport org.springframework.http.ResponseEntity;[m
[32m+[m[32mimport org.springframework.http.converter.HttpMessageNotReadableException;[m
[32m+[m[32mimport org.springframework.validation.FieldError;[m
[32m+[m[32mimport org.springframework.web.bind.MethodArgumentNotValidException;[m
[32m+[m[32mimport org.springframework.web.bind.annotation.ExceptionHandler;[m
[32m+[m[32mimport org.springframework.web.bind.annotation.RestControllerAdvice;[m
[32m+[m[32mimport org.springframework.web.server.ResponseStatusException;[m
[32m+[m
[32m+[m[32mimport java.time.Instant;[m
[32m+[m[32mimport java.util.LinkedHashMap;[m
[32m+[m[32mimport java.util.Map;[m
[32m+[m
[32m+[m[32m@RestControllerAdvice[m
[32m+[m[32mpublic class ApiExceptionHandler {[m
[32m+[m[32m    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);[m
[32m+[m
[32m+[m[32m    @ExceptionHandler(ResponseStatusException.class)[m
[32m+[m[32m    ResponseEntity<ApiError> responseStatus(ResponseStatusException ex, HttpServletRequest request) {[m
[32m+[m[32m        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());[m
[32m+[m[32m        return response(status, "BUSINESS_RULE", ex.getReason(), request, Map.of());[m
[32m+[m[32m    }[m
[32m+[m[32m    @ExceptionHandler(MethodArgumentNotValidException.class)[m
[32m+[m[32m    ResponseEntity<ApiError> validation(MethodArgumentNotValidException ex, HttpServletRequest request) {[m
[32m+[m[32m        Map<String, String> fields = new LinkedHashMap<>();[m
[32m+[m[32m        for (FieldError error : ex.getBindingResult().getFieldErrors()) fields.putIfAbsent(error.getField(), error.getDefaultMessage());[m
[32m+[m[32m        return response(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Dados inválidos.", request, fields);[m
[32m+[m[32m    }[m
[32m+[m[32m    @ExceptionHandler(ConstraintViolationException.class)[m
[32m+[m[32m    ResponseEntity<ApiError> constraint(ConstraintViolationException ex, HttpServletRequest request) {[m
[32m+[m[32m        return response(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Parâmetros inválidos.", request, Map.of());[m
[32m+[m[32m    }[m
[32m+[m[32m    @ExceptionHandler(HttpMessageNotReadableException.class)[m
[32m+[m[32m    ResponseEntity<ApiError> unreadable(HttpMessageNotReadableException ex, HttpServletRequest request) {[m
[32m+[m[32m        return response(HttpStatus.BAD_REQUEST, "INVALID_JSON", "Corpo da requisição inválido.", request, Map.of());[m
[32m+[m[32m    }[m
[32m+[m[32m    @ExceptionHandler(DataIntegrityViolationException.class)[m
[32m+[m[32m    ResponseEntity<ApiError> integrity(DataIntegrityViolationException ex, HttpServletRequest request) {[m
[32m+[m[32m        return response(HttpStatus.CONFLICT, "DATA_CONFLICT", "A operação viola uma regra de unicidade ou integridade.", request, Map.of());[m
[32m+[m[32m    }[m
[32m+[m[32m    @ExceptionHandler(Exception.class)[m
[32m+[m[32m    ResponseEntity<ApiError> unexpected(Exception ex, HttpServletRequest request) {[m
[32m+[m[32m        log.error("Erro inesperado ao processar {} {}", request.getMethod(), request.getRequestURI(), ex);[m
[32m+[m[32m        return response(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Ocorreu um erro interno.", request, Map.of());[m
[32m+[m[32m    }[m
[32m+[m[32m    private ResponseEntity<ApiError> response(HttpStatus status, String code, String message, HttpServletRequest request, Map<String, String> fields) {[m
[32m+[m[32m        return ResponseEntity.status(status).body(new ApiError(Instant.now(), status.value(), code, message, request.getRequestURI(), fields));[m
[32m+[m[32m    }[m
[32m+[m[32m}[m
[1mdiff --git a/src/main/java/br/com/biblioteca_api/controllers/AuthController.java b/src/main/java/br/com/biblioteca_api/controllers/AuthController.java[m
[1mnew file mode 100644[m
[1mindex 0000000..2a5b64c[m
[1m--- /dev/null[m
[1m+++ b/src/main/java/br/com/biblioteca_api/controllers/AuthController.java[m
[36m@@ -0,0 +1,64 @@[m
[32m+[m[32mpackage br.com.biblioteca_api.controllers;[m
[32m+[m
[32m+[m[32mimport br.com.biblioteca_api.dto.LoginRequest;[m
[32m+[m[32mimport br.com.biblioteca_api.dto.RegistrationRequest;[m
[32m+[m[32mimport br.com.biblioteca_api.dto.UserResponse;[m
[32m+[m[32mimport br.com.biblioteca_api.service.UserService;[m
[32m+[m[32mimport jakarta.servlet.http.HttpServletRequest;[m
[32m+[m[32mimport jakarta.servlet.http.HttpServletResponse;[m
[32m+[m[32mimport jakarta.validation.Valid;[m
[32m+[m[32mimport org.springframework.beans.factory.annotation.Autowired;[m
[32m+[m[32mimport org.springframework.security.core.context.SecurityContext;[m
[32m+[m[32mimport org.springframework.http.HttpStatus;[m
[32m+[m[32mimport org.springframework.http.ResponseEntity;[m
[32m+[m[32mimport org.springframework.security.authentication.AuthenticationManager;[m
[32m+[m[32mimport org.springframework.security.authentication.UsernamePasswordAuthenticationToken;[m
[32m+[m[32mimport org.springframework.security.core.Authentication;[m
[32m+[m[32mimport org.springframework.security.core.context.SecurityContextHolder;[m
[32m+[m[32mimport org.springframework.security.core.context.SecurityContextHolderStrategy;[m
[32m+[m[32mimport org.springframework.security.web.context.HttpSessionSecurityContextRepository;[m
[32m+[m[32mimport org.springframework.security.web.context.SecurityContextRepository;[m
[32m+[m[32mimport org.springframework.web.bind.annotation.PostMapping;[m
[32m+[m[32mimport org.springframework.web.bind.annotation.RequestBody;[m
[32m+[m[32mimport org.springframework.web.bind.annotation.RequestMapping;[m
[32m+[m[32mimport org.springframework.web.bind.annotation.RestController;[m
[32m+[m
[32m+[m[32m@RestController[m
[32m+[m[32m@RequestMapping("/api/auth")[m
[32m+[m[32mpublic class AuthController {[m
[32m+[m
[32m+[m[32m    private final UserService userService;[m
[32m+[m[32m    private final AuthenticationManager authenticationManager;[m
[32m+[m[32m    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();[m
[32m+[m[32m    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();[m
[32m+[m
[32m+[m[32m    public AuthController(UserService userService, AuthenticationManager authenticationManager) {[m
[32m+[m[32m        this.userService = userService;[m
[32m+[m[32m        this.authenticationManager = authenticationManager;[m
[32m+[m[32m    }[m
[32m+[m
[32m+[m[32m    @PostMapping("/login")[m
[32m+[m[32m    public String login(@RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {[m
[32m+[m[32m        var authToken = new UsernamePasswordAuthenticationToken([m
[32m+[m[32m                loginRequest.email(),[m
[32m+[m[32m                loginRequest.password()[m
[32m+[m[32m        );[m
[32m+[m
[32m+[m[32m        Authentication authentication = authenticationManager.authenticate(authToken);[m
[32m+[m
[32m+[m[32m        SecurityContext context = securityContextHolderStrategy.createEmptyContext();[m
[32m+[m[32m        context.setAuthentication(authentication);[m
[32m+[m
[32m+[m[32m        securityContextHolderStrategy.setContext(context);[m
[32m+[m
[32m+[m[32m        securityContextRepository.saveContext(context, request, response);[m
[32m+[m
[32m+[m[32m        return "Usuário autenticado com sucesso!";[m
[32m+[m[32m    }[m
[32m+[m
[32m+[m
[32m+[m[32m    @PostMapping("/register")[m
[32m+[m[32m    public ResponseEntity<UserResponse> register(@RequestBody @Valid RegistrationRequest request) {[m
[32m+[m[32m        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(userService.register(request)));[m
[32m+[m[32m    }[m
[32m+[m[32m}[m
[1mdiff --git a/src/main/java/br/com/biblioteca_api/controllers/BookController.java b/src/main/java/br/com/biblioteca_api/controllers/BookController.java[m
[1mindex e3796d6..99878ba 100644[m
[1m--- a/src/main/java/br/com/biblioteca_api/controllers/BookController.java[m
[1m+++ b/src/main/java/br/com/biblioteca_api/controllers/BookController.java[m
[36m@@ -1,17 +1,28 @@[m
 package br.com.biblioteca_api.controllers;[m
 [m
 import br.com.biblioteca_api.dto.BookRequest;[m
[32m+[m[32mimport br.com.biblioteca_api.dto.BookResponse;[m
[32m+[m[32mimport br.com.biblioteca_api.dto.PageResponse;[m
 import br.com.biblioteca_api.model.Book;[m
 import br.com.biblioteca_api.service.BookService;[m
 import jakarta.validation.Valid;[m
 import org.springframework.http.HttpStatus;[m
 import org.springframework.http.ResponseEntity;[m
 import org.springframework.web.bind.annotation.*;[m
[32m+[m[32mimport org.springframework.security.access.prepost.PreAuthorize;[m
[32m+[m[32mimport jakarta.validation.constraints.Positive;[m
[32m+[m[32mimport jakarta.validation.constraints.NotBlank;[m
[32m+[m[32mimport jakarta.validation.constraints.Size;[m
[32m+[m[32mimport org.springframework.validation.annotation.Validated;[m
 [m
 import java.util.List;[m
[32m+[m[32mimport org.springframework.data.domain.PageRequest;[m
[32m+[m[32mimport jakarta.validation.constraints.Max;[m
[32m+[m[32mimport jakarta.validation.constraints.Min;[m
 [m
 @RestController[m
 @RequestMapping("/api/books")[m
[32m+[m[32m@Validated[m
 public class BookController {[m
 [m
     private BookService bookService;[m
[36m@@ -21,36 +32,47 @@[m [mpublic class BookController {[m
     }[m
 [m
     @PostMapping[m
[31m-    public ResponseEntity<Book> create(@RequestBody @Valid BookRequest request) {[m
[32m+[m[32m    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")[m
[32m+[m[32m    public ResponseEntity<BookResponse> create(@RequestBody @Valid BookRequest request) {[m
         Book book = bookService.create(request);[m
[31m-        return ResponseEntity.status(HttpStatus.CREATED).body(book);[m
[32m+[m[32m        return ResponseEntity.status(HttpStatus.CREATED).body(BookResponse.from(book));[m
     }[m
 [m
     @GetMapping[m
[31m-    public ResponseEntity<List<Book>> listAll() {[m
[31m-        return ResponseEntity.ok(bookService.listAll());[m
[32m+[m[32m    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")[m
[32m+[m[32m    public ResponseEntity<List<BookResponse>> listAll() {[m
[32m+[m[32m        return ResponseEntity.ok(bookService.listAll().stream().map(BookResponse::from).toList());[m
[32m+[m[32m    }[m
[32m+[m
[32m+[m[32m    @GetMapping("/page")[m
[32m+[m[32m    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")[m
[32m+[m[32m    public PageResponse<BookResponse> listPage(@RequestParam(defaultValue = "0") @Min(0) int page,[m
[32m+[m[32m                                               @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {[m
[32m+[m[32m        return PageResponse.from(bookService.listPage(PageRequest.of(page, size)), BookResponse::from);[m
     }[m
 [m
     @GetMapping("/{id}")[m
[31m-    public ResponseEntity<Book> findById(@PathVariable Long id) {[m
[31m-        return ResponseEntity.ok(bookService.findById(id));[m
[32m+[m[32m    public ResponseEntity<BookResponse> findById(@PathVariable @Positive Long id) {[m
[32m+[m[32m        return ResponseEntity.ok(BookResponse.from(bookService.findById(id)));[m
     }[m
 [m
     @GetMapping("/search")[m
[31m-    public ResponseEntity<List<Book>> search(@RequestParam String name) {[m
[31m-        return ResponseEntity.ok(bookService.searchByName(name));[m
[32m+[m[32m    public ResponseEntity<List<BookResponse>> search(@RequestParam @NotBlank @Size(max = 100) String name) {[m
[32m+[m[32m        return ResponseEntity.ok(bookService.searchByName(name).stream().map(BookResponse::from).toList());[m
     }[m
 [m
     @PutMapping("/{id}")[m
[31m-    public ResponseEntity<Book> update([m
[31m-            @PathVariable Long id,[m
[32m+[m[32m    @PreAuthorize("hasRole('ADMIN')")[m
[32m+[m[32m    public ResponseEntity<BookResponse> update([m
[32m+[m[32m            @PathVariable @Positive Long id,[m
             @RequestBody @Valid BookRequest request[m
     ) {[m
[31m-        return ResponseEntity.ok(bookService.update(id, request));[m
[32m+[m[32m        return ResponseEntity.ok(BookResponse.from(bookService.update(id, request)));[m
     }[m
 [m
     @DeleteMapping("/{id}")[m
[31m-    public ResponseEntity<Void> delete(@PathVariable Long id) {[m
[32m+[m[32m    @PreAuthorize("hasRole('ADMIN')")[m
[32m+[m[32m    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {[m
         bookService.delete(id);[m
         return ResponseEntity.noContent().build();[m
     }[m
[1mdiff --git a/src/main/java/br/com/biblioteca_api/controllers/ClientController.java b/src/main/java/br/com/biblioteca_api/controllers/ClientController.java[m
[1mindex 431f360..7f52f50 100644[m
[1m--- a/src/main/java/br/com/biblioteca_api/controllers/ClientController.java[m
[1m+++ b/src/main/java/br/com/biblioteca_api/controllers/ClientController.java[m
[36m@@ -1,17 +1,28 @@[m
 package br.com.biblioteca_api.controllers;[m
 [m
 import br.com.biblioteca_api.dto.ClientRequest;[m
[32m+[m[32mimport br.com.biblioteca_api.dto.ClientResponse;[m
[32m+[m[32mimport br.com.biblioteca_api.dto.PageResponse;[m
 import br.com.biblioteca_api.model.Client;[m
 import br.com.biblioteca_api.service.ClientService;[m
 import jakarta.validation.Valid;[m
 import org.springframework.http.HttpStatus;[m
 import org.springframework.http.ResponseEntity;[m
 import org.springframework.web.bind.annotation.*;[m
[32m+[m[32mimport org.springframework.security.access.prepost.PreAuthorize;[m
[32m+[m[32mimport jakarta.validation.constraints.Positive;[m
[32m+[m[32mimport jakarta.validation.constraints.NotBlank;[m
[32m+[m[32mimport jakarta.validation.constraints.Size;[m
[32m+[m[32mimport org.springframework.validation.annotation.Validated;[m
 [m
 import java.util.List;[m
[32m+[m[32mimport org.springframework.data.domain.PageRequest;[m
[32m+[m[32mimport jakarta.validation.constraints.Max;[m
[32m+[m[32mimport jakarta.validation.constraints.Min;[m
 [m
 @RestController[m
 @RequestMapping("/api/clients")[m
[32m+[m[32m@Validated[m
 public class ClientController {[m
 [m
     private final ClientService clientService;[m
[36m@@ -21,36 +32,47 @@[m [mpublic class ClientController {[m
     }[m
 [m
     @PostMapping[m
[31m-    public ResponseEntity<Client> create(@RequestBody @Valid ClientRequest request) {[m
[32m+[m[32m    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")[m
[32m+[m[32m    public ResponseEntity<ClientResponse> create(@RequestBody @Valid ClientRequest request) {[m
         Client client = clientService.create(request);[m
[31m-        return ResponseEntity.status(HttpStatus.CREATED).body(client);[m
[32m+[m[32m        return ResponseEntity.status(HttpStatus.CREATED).body(ClientResponse.from(client));[m
     }[m
 [m
     @GetMapping[m
[31m-    public ResponseEntity<List<Client>> listAll() {[m
[31m-        return ResponseEntity.ok(clientService.listAll());[m
[32m+[m[32m    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")[m
[32m+[m[32m    public ResponseEntity<List<ClientResponse>> listAll() {[m
[32m+[m[32m        return ResponseEntity.ok(clientService.listAll().stream().map(ClientResponse::from).toList());[m
[32m+[m[32m    }[m
[32m+[m
[32m+[m[32m    @GetMapping("/page")[m
[32m+[m[32m    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")[m
[32m+[m[32m    public PageResponse<ClientResponse> listPage(@RequestParam(defaultValue = "0") @Min(0) int page,[m
[32m+[m[32m                                                 @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {[m
[32m+[m[32m        return PageResponse.from(clientService.listPage(PageRequest.of(page, size)), ClientResponse::from);[m
     }[m
 [m
     @GetMapping("/{id}")[m
[31m-    public ResponseEntity<Client> findById(@PathVariable Long id) {[m
[31m-        return ResponseEntity.ok(clientService.findById(id));[m
[32m+[m[32m    public ResponseEntity<ClientResponse> findById(@PathVariable @Positive Long id) {[m
[32m+[m[32m        return ResponseEntity.ok(ClientResponse.from(clientService.findById(id)));[m
     }[m
 [m
     @GetMapping("/search")[m
[31m-    public ResponseEntity<List<Client>> search(@RequestParam String name) {[m
[31m-        return ResponseEntity.ok(clientService.searchByName(name));[m
[32m+[m[32m    public ResponseEntity<List<ClientResponse>> search(@RequestParam @NotBlank @Size(max = 100) String name) {[m
[32m+[m[32m        return ResponseEntity.ok(clientService.searchByName(name).stream().map(ClientResponse::from).toList());[m
     }[m
 [m
     @PutMapping("/{id}")[m
[31m-    public ResponseEntity<Client> update([m
[31m-            @PathVariable Long id,[m
[32m+[m[32m    @PreAuthorize("hasRole('ADMIN')")[m
[32m+[m[32m    public ResponseEntity<ClientResponse> update([m
[32m+[m[32m            @PathVariable @Positive Long id,[m
             @RequestBody @Valid ClientRequest request[m
     ) {[m
[31m-        return ResponseEntity.ok(clientService.update(id, request));[m
[32m+[m[32m        return ResponseEntity.ok(ClientResponse.from(clientService.update(id, request)));[m
     }[m
 [m
     @DeleteMapping("/{id}")[m
[31m-    public ResponseEntity<Void> delete(@PathVariable Long id) {[m
[32m+[m[32m    @PreAuthorize("hasRole('ADMIN')")[m
[32m+[m[32m    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {[m
         clientService.delete(id);[m
         return ResponseEntity.noContent().build();[m
     }[m
[1mdiff --git a/src/main/java/br/com/biblioteca_api/controllers/LoanController.java b/src/main/java/br/com/biblioteca_api/controllers/LoanController.java[m
[1mindex 0a78583..f25cf6