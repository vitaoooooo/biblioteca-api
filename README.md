# Biblioteca API

API REST para o gerenciamento de uma biblioteca: livros, clientes e empréstimos. O projeto também inclui uma interface estática inicial.

## Execução segura

O backend usa autenticação HTTP Basic. Na primeira inicialização, informe obrigatoriamente um administrador inicial e as credenciais do banco:

```powershell
$env:SPRING_PROFILES_ACTIVE="dev"
$env:DB_PASSWORD="uma-senha-do-banco"
$env:APP_ADMIN_EMAIL="admin@empresa.com"
$env:APP_ADMIN_PASSWORD="uma-senha-forte-com-12-ou-mais-caracteres"
.\mvnw.cmd spring-boot:run
```

Em produção, use `SPRING_PROFILES_ACTIVE=prod`, um usuário MySQL exclusivo da aplicação e segredos fornecidos pelo ambiente/orquestrador. Não use `root`. As migrations em `src/main/resources/db/migration` são aplicadas pelo Flyway; não altere o esquema manualmente nem habilite `ddl-auto=update`.

Rotas `/api/**` exigem um usuário com papel `STAFF` ou `ADMIN`, exceto `POST /api/auth/register`, que permite o autocadastro. Contas criadas por esse endpoint sempre recebem o papel `STAFF`; somente `ADMIN` pode editar/inativar livros, clientes e gerir usuários em `/api/users`. O endpoint público de disponibilidade é `/actuator/health`.

## Tecnologias

- Java 17
- Spring Boot
- Spring Data JPA
- MySQL
- Maven Wrapper

## Funcionalidades

- Cadastro, consulta, atualização, remoção e busca de livros
- Cadastro, consulta, atualização, remoção e busca de clientes
- Registro e consulta de empréstimos, incluindo empréstimos em atraso

Por padrão, a aplicação estará disponível em `http://localhost:8080`.

## Endpoints principais

| Recurso | Base da API |
| --- | --- |
| Livros | `/api/books` |
| Clientes | `/api/clients` |
| Empréstimos | `/api/loans` |
| Empréstimos em atraso | `/api/loans/late` |
| Usuários (admin) | `/api/users` |

## Garantias do backend

- Estoque protegido por bloqueio pessimista e versão otimista, evitando empréstimos concorrentes acima do disponível.
- Devoluções preservam o empréstimo original e gravam o histórico compatível em separado.
- Exclusões passam a ser inativações, preservando rastreabilidade e relacionamentos históricos.
- Respostas usam DTOs e erros seguem um corpo padronizado; entidades JPA não são expostas diretamente.
- Health checks, métricas e ID de correlação por resposta estão disponíveis para monitoramento.

