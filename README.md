# Biblioteca API

API REST para gerenciamento de uma biblioteca, desenvolvida com Java e Spring Boot. O projeto permite administrar livros, clientes e empréstimos, além de contar com autenticação e uma interface web para acessar as funcionalidades.

## Tecnologias

- Java 17
- Spring Boot
- Spring Data JPA
- Spring Security
- MySQL
- Maven
- Flyway

## Funcionalidades

- Cadastro, consulta, atualização e busca de livros
- Gerenciamento de clientes
- Registro e consulta de empréstimos
- Consulta de empréstimos em atraso
- Controle de estoque durante os empréstimos
- Autenticação e controle de acesso por perfil
- Gerenciamento de usuários com permissões administrativas
- Histórico de devoluções e inativação de registros
- Interface web integrada à API

## Como executar

### Pré-requisitos

- Java 17
- MySQL
- Git

### 1. Clone o repositório

```bash
git clone URL_DO_REPOSITORIO
cd biblioteca-api
```

Substitua `URL_DO_REPOSITORIO` pelo endereço deste repositório.

### 2. Configure o banco de dados

Crie um banco de dados no MySQL para a aplicação e configure as variáveis de ambiente no terminal.

Exemplo para PowerShell:

```powershell
$env:SPRING_PROFILES_ACTIVE="dev"
$env:DB_URL="jdbc:mysql://localhost:3306/biblioteca"
$env:DB_USERNAME="biblioteca_user"
$env:DB_PASSWORD="sua_senha"
$env:APP_ADMIN_EMAIL="admin@empresa.com"
$env:APP_ADMIN_PASSWORD="uma_senha_forte_com_12_caracteres"
```

Use as credenciais do seu ambiente. O usuário do banco deve ter apenas as permissões necessárias para a aplicação.

### 3. Inicie a aplicação

No Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

No Linux ou macOS:

```bash
./mvnw spring-boot:run
```

Após a inicialização, a aplicação estará disponível em:

`http://localhost:8080`

As migrations do banco são gerenciadas pelo Flyway.

## Endpoints

| Recurso | Endpoint |
| --- | --- |
| Autenticação | `/api/auth` |
| Livros | `/api/books` |
| Clientes | `/api/clients` |
| Empréstimos | `/api/loans` |
| Empréstimos em atraso | `/api/loans/late` |
| Usuários | `/api/users` |
| Saúde da aplicação | `/actuator/health` |

## Autenticação e permissões

A API utiliza autenticação para proteger os endpoints privados.

- **STAFF:** acesso às operações permitidas para funcionários da biblioteca.
- **ADMIN:** acesso administrativo, incluindo o gerenciamento de usuários e operações restritas.
- **Cadastro:** novos usuários registrados pelo endpoint público recebem o perfil `STAFF`.

O acesso às operações administrativas é controlado no backend. A interface não substitui as verificações de autorização da API.

## Banco de dados e integridade

O projeto utiliza Flyway para versionar as alterações do esquema do banco.

Também conta com mecanismos para manter a integridade das operações:

- Controle de concorrência no estoque para evitar empréstimos acima da quantidade disponível.
- Registro do histórico de devoluções.
- Inativação de registros para preservar relacionamentos e rastreabilidade.
- Uso de DTOs para estruturar as requisições e respostas da API.
- Tratamento padronizado de erros.
- Identificador de correlação nas requisições para facilitar o rastreamento.
- Health checks e métricas para monitoramento.

## Estrutura do projeto

O código é organizado em camadas, separando responsabilidades entre controladores, serviços, repositórios, modelos, DTOs e configurações.

As migrations do banco ficam em `src/main/resources/db/migration`, e os arquivos da interface web ficam nos recursos estáticos da aplicação.

## Segurança

Não versione senhas, tokens ou credenciais reais. Configure os valores sensíveis por variáveis de ambiente e utilize credenciais próprias para cada ambiente.

Em produção, utilize o perfil `prod`, um usuário MySQL exclusivo para a aplicação e um mecanismo seguro para fornecer os segredos.

---

Desenvolvido como projeto de estudo de Engenharia de Software.
