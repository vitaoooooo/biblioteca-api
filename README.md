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

Por padrão, a aplicação estará disponível em `http://localhost:8080`.

## Endpoints principais

| Recurso | Base da API |
| --- | --- |
| Livros | `/api/books` |
| Clientes | `/api/clients` |
| Empréstimos | `/api/loans` |
| Empréstimos em atraso | `/api/loans/late` |

