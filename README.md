# ⚡️ ConsumoEnergetico

API REST simples para gerenciar registros de consumo energético, utilizando **Spring Boot (Java 17)** com suporte a containerização via Docker.

---

## Visão Geral

Esta API permite:

* Criar, listar, atualizar e excluir registros de consumo energético.
* Conectar-se a banco de dados Oracle.
* Ser executada localmente via Maven ou com Docker.
* Estar preparada para CI/CD com GitHub Actions.

---

## Principais Conceitos

* **Entidade:** `ConsumoEnergetico`

  * `id`: Long (gerado via sequence no banco)
  * `qtdConsumo`: Double
  * `data`: LocalDate (formato ISO: `YYYY-MM-DD`)
  * `unidade`: String
* **DTO de exibição:** `ConsumoExibicaoDto`

  * Contém apenas os campos públicos (id, qtdConsumo, data, unidade)

---

## Estrutura do Projeto

```text
README.md
consumoenergetico/
├── Dockerfile
├── docker-compose.yml
├── mvnw / mvnw.cmd
├── pom.xml
├── prod/
│   └── docker-compose.yml
├── src/
│   └── main/
│       ├── java/br/com/fiap/consumoenergetico/
│       │   ├── ConsumoenergeticoApplication.java
│       │   ├── advice/ApplicationExceptionHandler.java
│       │   ├── controller/ConsumoController.java
│       │   ├── dto/ConsumoExibicaoDto.java
│       │   ├── exception/ConsumoNaoEncontradoException.java
│       │   ├── model/ConsumoEnergetico.java
│       │   ├── repo/ConsumoRepo.java
│       │   ├── security/SecurityConfig.java
│       │   └── service/ConsumoService.java
│       └── resources/
│           ├── application.properties
│           └── db/migration/V1_criar-tabela-consumo.sql
└── staging/
    └── docker-compose.yml
```

---

## Endpoints

**Base path:** `/api`

### ➕ POST `/api/consumo`

Cria um novo consumo.

* **Request (JSON):**

```json
{
  "qtdConsumo": 123.45,
  "data": "2025-10-11",
  "unidade": "kWh"
}
```

* **Response:** `201 Created` com `ConsumoExibicaoDto`
* **Validações:** `qtdConsumo`, `data`, `unidade` são obrigatórios

---

### GET `/api/consumo`

Lista consumos com paginação.

* Aceita: `?page=0&size=10&sort=data,desc`
* Retorna: `Page<ConsumoExibicaoDto>` (JSON)

---

### PUT `/api/consumo`

Atualiza um consumo.

* JSON completo com `id`
* **Response:** `200 OK` com entidade atualizada

---

###  DELETE `/api/consumo/{id}`

Remove um consumo por `id`.

* **Response:** `204 No Content`

---

## Tratamento de Erros

* **400 Bad Request** → para validações de payload (campos obrigatórios)

  * Via `ApplicationExceptionHandler`
* **500 Internal Server Error** → por padrão quando o `ConsumoNaoEncontradoException` é lançado
  **🔧 Sugestão:** Adicionar tratamento específico para retornar `404 Not Found`

---

## Segurança

* `SecurityConfig` atual permite livre acesso a `/api/**` (usando `permitAll()`).
* **🔒 Para produção**, recomenda-se:

  * Autenticação com JWT ou OAuth2
  * Reabilitar CSRF e restringir acessos

---

## Banco de Dados

* Conexão com **Oracle** (`jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL`)
* Flyway executa `V1_criar-tabela-consumo.sql`, que cria:

  * Sequência: `SEQ_CONSUMO`
  * Tabela: `TBL_CONSUMO` com colunas:

    * `ID` (PK)
    * `QTD_CONSUMO` (NUMBER)
    * `DATA_CONSUMO` (DATE)
    * `UNIDADE` (VARCHAR)

⚠️ **Não versionar credenciais** em `application.properties`. Use variáveis de ambiente ou um `Secrets Manager`.

---

## Execução

### ▶Maven

```bash
./mvnw clean package -DskipTests
java -jar target/*.jar
```

---

### 🐳 Docker

#### Build

```bash
docker build -t consumoenergetico:local -f consumoenergetico/Dockerfile consumoenergetico
```

#### Run

```bash
docker compose -f consumoenergetico/docker-compose.yml up -d
```

* Exposição: `localhost:8080`
* Use `SPRING_DATASOURCE_*` e `JAVA_OPTS` para configurar o ambiente

---

## ⚙️ Variáveis de Ambiente (principais)

* `SPRING_DATASOURCE_URL`
* `SPRING_DATASOURCE_USERNAME`
* `SPRING_DATASOURCE_PASSWORD`
* `SPRING_PROFILES_ACTIVE` (ex: `prod`)
* `JAVA_OPTS` (ex: `-Xms256m -Xmx512m`)

---

## Próximos passos

* [ ] Tratar `ConsumoNaoEncontradoException` com HTTP 404
* [ ] Implementar testes unitários e de integração
* [ ] Proteger a API com autenticação (JWT/OAuth2)
* [ ] Externalizar configurações sensíveis (ex: `spring.config.import=optional:configserver:`)

---

## 🛠 CI/CD (GitHub Actions)

Fluxo sugerido:

1. Build Maven
2. Testes
3. Análise estática (SpotBugs, SonarQube etc)
4. Docker Build & Push
5. Deploy (staging/prod)

Exemplo de `.github/workflows/ci.yml`:

```yaml
name: CI

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '17'
          cache: maven
      - name: Build with Maven
        run: mvn -B -ntp clean package
      - name: Build and push Docker image (optional)
        if: github.ref == 'refs/heads/main'
        uses: docker/build-push-action@v4
        with:
          push: true
          tags: ghcr.io/${{ github.repository }}:latest
```

---

## 🐳 Containerização

**Dockerfile** com multi-stage:

```Dockerfile
# Etapa 1: build
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /src

COPY pom.xml .
RUN mvn -B -ntp -q dependency:go-offline

COPY src ./src
RUN mvn -B -ntp clean package -DskipTests

# Etapa 2: runtime
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /src/target/*.jar /app/app.jar
EXPOSE 8080
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
```

---

## 🖼 Prints / Evidências

Coloque prints na pasta `docs/screenshots/`. Exemplos:

```markdown
![API rodando - docker ps](docs/screenshots/docker-ps.png)
![GET /api/consumo - Postman](docs/screenshots/get-consumo-postman.png)
```

---

## 🧰 Tecnologias Utilizadas

* **Java 17**
* **Spring Boot**
* **Spring Security**
* **Spring Data JPA + Hibernate**
* **Flyway**
* **Oracle**
* **Docker / Docker Compose**
* **Maven**
* **JUnit / Mockito** (implementar)
* **Lombok**
* **Jakarta Validation**

---
