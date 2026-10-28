# Restaurante API

Trabalho da disciplina **Backend: Web Services** (PUCPR) - Opcao 1, extensao do servico `authserver` visto em aula.

## Integrantes

- Henrique Dias (henriquediasjr)
- _(adicionar demais membros da equipe)_

## Video de apresentacao

_(adicionar link do YouTube)_

## Stack

Kotlin 2.2, Spring Boot 4, Spring Data JPA (H2 em memoria), Spring Security + JWT (jjwt), springdoc (Swagger), JUnit 5 + kotest + mockk.

## Como executar

```bash
./gradlew bootRun      # sobe em http://localhost:8080/api
./gradlew test         # testes unitarios
```

- Swagger UI: http://localhost:8080/api/swagger-ui.html
- Console H2: http://localhost:8080/api/h2-console (jdbc:h2:mem:db, sa/sa)
- Usuario administrador criado no bootstrap: `admin@authserver.com` / `admin`
- O segredo do JWT pode ser trocado com a variavel de ambiente `JWT_SECRET` (ver `security.yaml`).

## Modelo e relacionamentos

| Relacao | Tipo |
|---|---|
| Category -> Product | um-para-muitos (`@ManyToOne` em Product) |
| Product <-> Tag | muitos-para-muitos (tabela `ProductTag`) |
| Order <-> Product | muitos-para-muitos (tabela `OrderProduct`) |
| Order -> User | muitos-para-um |
| User <-> Role | muitos-para-muitos (da aula) |

Novas classes: `Category`, `Product`, `Tag`, `Order`.

## Endpoints

Autenticacao: `POST /users/login` retorna o token; enviar `Authorization: Bearer <token>`.

| Metodo | Rota | Acesso |
|---|---|---|
| GET | `/categories`, `/categories/{id}` | publico |
| POST / PUT | `/categories`, `/categories/{id}` | autenticado |
| DELETE | `/categories/{id}` | ADMIN |
| GET | `/products` (filtros abaixo), `/products/{id}` | publico |
| POST / PUT | `/products`, `/products/{id}` | autenticado |
| DELETE | `/products/{id}` | ADMIN |
| PUT / DELETE | `/products/{id}/tags/{name}` | autenticado / ADMIN |
| GET / POST | `/tags` | publico / autenticado |
| GET / POST | `/orders`, `/orders/{id}` | autenticado |
| PUT | `/orders/{id}/products/{productId}` | autenticado |
| DELETE | `/orders/{id}/products/{productId}`, `/orders/{id}` | ADMIN |
| POST | `/users`, `/users/login` | publico |

### Consulta com filtro e ordenacao

`GET /products?name=&categoryId=&minPrice=&maxPrice=&tag=&sortBy=name|price|id&sortDir=ASC|DESC`

## Boas praticas aplicadas

- **Logs** (SLF4J) nos services: `info` em criacoes/alteracoes, `warn` em exclusoes e falhas de login; senhas nunca sao logadas.
- **Excecoes** tratadas em `GlobalExceptionHandler` (RFC 9457 `ProblemDetail`): 400, 401, 403, 404, 409, 422 e 500.
- **Validacao** com Bean Validation nos requests.
- **Configuracao externa** em `security.yaml` via `@ConfigurationProperties` validado.
- Senhas com BCrypt.
