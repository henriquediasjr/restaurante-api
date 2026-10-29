# Resumo do trabalho

## O que é o projeto

API REST de restaurante em Kotlin + Spring Boot, feita como extensão do `authserver` visto em aula (usuários, roles, testes). Foi escolhida a **Opção 1 - Estendendo os serviços**. Cadastra categorias, produtos, tags e pedidos, com login JWT e rotas destrutivas restritas a ADMIN.

Repositório: https://github.com/henriquediasjr/restaurante-api

## Como rodar

```bash
./gradlew bootRun                 # sobe em http://localhost:8080/api
./gradlew test                    # testes unitários
./scripts/demo.sh                 # demo passo a passo (NOPAUSE=1 para rodar direto)
```

- O primeiro build baixa o JDK 21 sozinho (pode levar alguns minutos).
- Swagger: http://localhost:8080/api/swagger-ui.html
- Admin criado no bootstrap: `admin@authserver.com` / `admin`
- Reinicie o servidor antes de gravar a demo (banco H2 em memória).

## O que o trabalho pediu e o que já foi feito

| Requisito | Status | Onde |
|---|---|---|
| Pelo menos 4 classes novas | Feito | `Category`, `Product`, `Tag`, `Order` |
| Pelo menos 2 tipos de relação | Feito | 1-N (Category→Product), N-N (Product↔Tag, Order↔Product), N-1 (Order→User) |
| CRUD completo de 2 classes | Feito | `Category` e `Product` |
| Autenticação nas ações destrutivas | Feito | JWT; `DELETE` só para ADMIN (`SecurityConfig`) |
| Endpoints de associação | Feito | `PUT/DELETE /products/{id}/tags/{name}`, `PUT/DELETE /orders/{id}/products/{pid}` |
| Consulta com filtro e ordenação | Feito | `GET /products?name=&categoryId=&minPrice=&maxPrice=&tag=&sortBy=&sortDir=` |
| Logs | Feito | SLF4J nos services (`info`, `warn`) |
| Exceções de pelo menos 2 serviços | Feito | `GlobalExceptionHandler` e `ResponseStatusException` em Category, Product, Order, Tag e User |
| Configuração externa (aula 08) | Feito | `security.yaml` + `SecurityProperties` |
| Testes unitários (aula 06) | Feito | `ProductServiceTest`, `CategoryServiceTest`, `UserServiceTest` |
| README com nomes dos membros | Feito | `README.md` |
| Vídeo no YouTube com o link no README | **Pendente** | gravar e colar o link |
| Apresentação parcial em 29/10 (30%) | **Pendente** | mais da metade dos endpoints já está pronta |

## Responsabilidade de cada um

| Pessoa | Parte no vídeo e na apresentação |
|---|---|
| **Henrique** | Abertura, modelo e relacionamentos, CRUD de Category e Product, validação (422) e conflito (409) |
| **Lucas** | Associações (tags e pedidos), total do pedido, consulta com filtros e ordenação (`@Query` JPQL) |
| **Júlia** | Login e JWT, 401/403 por perfil, `security.yaml`, logs, `GlobalExceptionHandler` e testes |

Todos participam do fechamento e do vídeo, como exige o enunciado.

## Falta fazer

1. Gravar o vídeo (cerca de 8 a 10 minutos) e subir no YouTube (pode ser não listado).
2. Colar o link do vídeo no `README.md`.
3. Apresentar a parcial em 29/10.
