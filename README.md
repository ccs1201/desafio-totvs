# Sistema de Contas a Pagar

## Visão Geral
Sistema de gerenciamento de contas a pagar desenvolvido com Java Spring Boot para processamento e validação de pagamentos.

Para que seja possível acessar a api primeiro cadastre um usuário no edpoint [/auth/cadastro](http://localhost:8080/swagger-ui/index.html#/Autentica%C3%A7%C3%A3o/register) consulte a documentação
nos links abaixo para conhecer os payloads das requisições.

Base de dados é populada pelo arquivo ___/resources/db.migration/testdata/afterMigrate.sql___ se não quiser popular
com dados de teste comente ou remova este arquivo.

## Documentação/OpenApi
- [SwaggerUI](http://localhost:8080/api-doc.html)
- [OpenAPI](http://localhost:8080/v3/api-docs)

## Tecnologias Utilizadas
- Java 21
- Spring Boot 3.4.2
- Beans Validation
- Spring Web MVC
- Spring Security
- SpringDocOpenApi
- Maven
- PostgreSQL 16.7
- Flyway
- Docker
- Virtual Threads

## Funcionalidades
- Processamento de pagamentos
- Validação de entrada de dados
- Tratamento personalizado de exceções
- Endpoints REST
- Autenticação

## Pré-requisitos
- Java 21 ou superior
- Maven
- Docker

## Instalação
1. Clone o repositório:
```bash
  git clone [url-do-repositorio]
```
2. Execute mvn package
```bash 
  mvn package
   
```
3. Execute o docker compose up
```bash
  docker compose up
```
