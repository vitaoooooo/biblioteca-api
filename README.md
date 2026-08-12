# Biblioteca API

API REST para o gerenciamento de uma biblioteca: livros, clientes e empréstimos. O projeto também inclui uma interface estática inicial.

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

## Como executar localmente

1. Crie um banco MySQL chamado `biblioteca`.
2. Defina a senha do MySQL na variável de ambiente `MYSQL_PASSWORD` (use `.env.example` apenas como referência; não envie um arquivo `.env` ao GitHub).
3. Execute a aplicação:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

Por padrão, a aplicação estará disponível em `http://localhost:8080`.

## Endpoints principais

| Recurso | Base da API |
| --- | --- |
| Livros | `/api/books` |
| Clientes | `/api/clients` |
| Empréstimos | `/api/loans` |
| Empréstimos em atraso | `/api/loans/late` |

## Publicação no GitHub

Nome recomendado do repositório: `biblioteca-api`  
Descrição recomendada: `API REST para gerenciamento de biblioteca, desenvolvida com Java, Spring Boot e MySQL.`
