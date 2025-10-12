# ConsumoEnergetico

API simples para gerenciar registros de consumo energético.

Este projeto é uma aplicação Spring Boot (Java 17) que expõe endpoints REST para criar, listar, atualizar e remover registros de consumo energético.

## Principais conceitos

- Entidade: `ConsumoEnergetico`
  - id: Long (gerado pelo banco)
  - qtdConsumo: double
  - data: LocalDate (formato ISO: `YYYY-MM-DD`)
  - unidade: String

- DTO de exibição: `ConsumoExibicaoDto` (id, qtdConsumo, data, unidade)

## Estrutura do projeto

```
README.md
consumoenergetico/
  Dockerfile
  docker-compose.yml
  mvnw
  mvnw.cmd
  pom.xml
  prod/
    docker-compose.yml
  src/
    main/
      java/
        br/
          com/
            fiap/
              consumoenergetico/
                ConsumoenergeticoApplication.java
                advice/
                  ApplicationExceptionHandler.java
                controller/
                  ConsumoController.java
                dto/
                  ConsumoExibicaoDto.java
                exception/
                  ConsumoNaoEncontradoException.java
                model/
                  ConsumoEnergetico.java
                repo/
                  ConsumoRepo.java
                security/
                  SecurityConfig.java
                service/
                  ConsumoService.java
      resources/
        application.properties
        db/
          migration/
            V1_criar-tabela-consumo.sql
  staging/
    docker-compose.yml
  target/
    ... (build artifacts)
```

## Endpoints
Base path: `/api`

- POST `/api/consumo` — criar um consumo
  - Request: JSON com os campos `qtdConsumo`, `data` (YYYY-MM-DD) e `unidade`.
  - Validações: `qtdConsumo` e `unidade` não podem ser vazios; `data` é obrigatória.
  - Response: `201 Created` com o `ConsumoExibicaoDto` (JSON).

Exemplo de request:

```json
{
  "qtdConsumo": 123.45,
  "data": "2025-10-11",
  "unidade": "kWh"
}
```

- GET `/api/consumo` — listar consumos (paginado)
  - Aceita parâmetros de paginação do Spring Data `Pageable` (ex.: `?page=0&size=10&sort=data,desc`).
  - Response: `200 OK` com um objeto `Page<ConsumoExibicaoDto>`.

- PUT `/api/consumo` — atualizar um consumo
  - Request: JSON completo do `ConsumoEnergetico` (deve conter `id`).
  - Response: `200 OK` com a entidade atualizada.

- DELETE `/api/consumo/{id}` — remover por id
  - Response: `204 No Content` em sucesso.

## Tratamento de erros
- Quando validações de payload falham (ex.: campos obrigatórios), a API responde `400 Bad Request` com um JSON que mapeia os campos para mensagens de erro. (Classe: `ApplicationExceptionHandler`).
- Quando um recurso não é encontrado, o serviço lança `ConsumoNaoEncontradoException` (produz 500 por padrão no código atual); você pode tratá-la com um handler dedicado se desejar retornar `404 Not Found`.

## Segurança
- A configuração atual de segurança (`SecurityConfig`) desabilita CSRF e permite acesso livre (`permitAll`) aos endpoints `/api/**`. Para ambientes de produção, reveja essa configuração para exigir autenticação apropriada.

## Banco de dados
- A aplicação está configurada para conectar a um Oracle Database (ex.: `jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL`).
- Credenciais atuais (definidas em `application.properties`):
  - usuário: ``
  - senha: ``

- Script de criação (Flyway migration) em `src/main/resources/db/migration/V1_criar-tabela-consumo.sql` cria a sequência `SEQ_CONSUMO` e a tabela `TBL_CONSUMO` com colunas:
  - ID (integer, PK)
  - QTD_CONSUMO (NUMBER(10,2))
  - DATA_CONSUMO (DATE)
  - UNIDADE (VARCHAR2(100))

> Atenção: não deixe credenciais sensíveis em arquivos de configuração em repositórios públicos. Prefira variáveis de ambiente ou serviços de secrets.

## Build e execução

Requisitos: Java 17, Maven, Docker (opcional)

Executar localmente com Maven:

```powershell
./mvnw clean package -DskipTests; java -jar target/*.jar
```

Executar com Docker (imagem já definida no `Dockerfile`):

Build da imagem localmente:

```powershell
docker build -t consumoenergetico:local -f consumoenergetico/Dockerfile consumoenergetico
```

Rodar com docker-compose (arquivo `consumoenergetico/docker-compose.yml`):

```powershell
docker compose -f consumoenergetico/docker-compose.yml up -d
```

O container expõe a porta `8080` por padrão.

## Variáveis de ambiente úteis
- SPRING_DATASOURCE_URL
- SPRING_DATASOURCE_USERNAME
- SPRING_DATASOURCE_PASSWORD
- SPRING_PROFILES_ACTIVE (ex.: `prod`)
- JAVA_OPTS (ex.: `-Xms256m -Xmx512m`)

## Observações e próximos passos sugeridos
- Tratar `ConsumoNaoEncontradoException` para retornar `404 Not Found`.
- Adicionar testes de integração e unitários para os controllers e service.
- Proteger os endpoints com autenticação (JWT/OAuth2) para produção.
- Externalizar configurações sensíveis com `spring.config.import=optional:configserver:` ou variáveis de ambiente.

---
