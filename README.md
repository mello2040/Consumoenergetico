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

✅ Pré-requisitos
- Docker e Docker Compose instalados.
- Acesso à internet (para baixar a imagem do GHCR).
- Banco de dados Oracle disponível (utilize as credenciais de teste fornecidas).

 🧩 Passos para execução

1. **Clonar o repositório**
   ```bash
   git clone https://github.com/mello2040/Consumoenergetico.git
   cd Consumoenergetico/consumoenergetico

2. **Criar o arquivo .env e configurar variáveis**

3. **Executar com Docker Compose**

4. **Conferir se está rodando**

Exemplo:
```text
docker compose up -d
docker compose ps
curl http://localhost:8080/api/consumo
```
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

* A pipeline foi configurada no GitHub Actions para automatizar as etapas de build, push da imagem Docker e deploy remoto.

🧱 Etapas principais

1. **Build e teste**

* Compila o projeto com Maven (mvn clean package -DskipTests).

* Garante que o JAR da aplicação é gerado corretamente.



2. **Docker Build & Push**

* Constrói a imagem Docker a partir do Dockerfile.

* Publica automaticamente no GitHub Container Registry (GHCR):

* ghcr.io/mello2040/consumoenergetico:latest


3. **Deploy Automático*

* O deploy é realizado via SSH usando appleboy/ssh-action.

* No servidor, executa:

docker compose pull

docker compose up -d

docker image prune -f
...
* Trecho simplificado do workflow:
```text
build_test:
  runs-on: ubuntu-latest
  steps:
    - uses: actions/checkout@v3
    - name: Build with Maven
      run: mvn clean package -DskipTests

docker_push:
  needs: build_test
  runs-on: ubuntu-latest
  steps:
    - name: Build & Push Docker image
      run: |
        docker build -t ghcr.io/mello2040/consumoenergetico:latest .
        echo ${{ secrets.GHCR_TOKEN }} | docker login ghcr.io -u ${{ secrets.GHCR_USER }} --password-stdin
        docker push ghcr.io/mello2040/consumoenergetico:latest
```
---

## 🐳 Containerização

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

💡 Estratégias adotadas

* Imagem base leve: eclipse-temurin:17-jre (menor e otimizada).

* Workdir isolado: /app para organização e segurança.

* Porta 8080 exposta: acesso direto à API REST.

* ENTRYPOINT: inicia automaticamente o JAR da aplicação.
```
```
---

## 🖼 Prints / Evidências

Coloque prints na pasta `docs/screenshots/`. Exemplos:

```markdown
![Imagem do WhatsApp de 2025-10-14 à(s) 20 04 58_eef9bee9](https://github.com/user-attachments/assets/9d563a8f-01e8-4907-8404-65187c1fa923)

![Imagem do WhatsApp de 2025-10-14 à(s) 20 06 29_f23f6152](https://github.com/user-attachments/assets/c7af26e6-72e2-44c4-b2ea-babb0221cb98)

![Imagem do WhatsApp de 2025-10-14 à(s) 20 07 31_627b8132](https://github.com/user-attachments/assets/f7490556-dcc7-4bf5-93fd-fe47977a8907)
![Imagem do WhatsApp de 2025-10-14 à(s) 20 08 17_d85fb0b9](https://github.com/user-attachments/assets/b72c5058-035a-4135-8de0-be4a6382b975)
![Imagem do WhatsApp de 2025-10-14 à(s) 20 09 01_fc4a1201](https://github.com/user-attachments/assets/6d18f23a-7050-4630-81bd-ba9aeb471616)
![Imagem do WhatsApp de 2025-10-14 à(s) 20 09 01_d8906ac5](https://github.com/user-attachments/assets/37f4c60a-97c7-4ffe-805c-841d25661930)
![Imagem do WhatsApp de 2025-10-14 à(s) 20 09 01_57ccf608](https://github.com/user-attachments/assets/750fa513-ad37-49b6-819c-bebec26f6c36)
![Imagem do WhatsApp de 2025-10-14 à(s) 20 09 28_6667f409](https://github.com/user-attachments/assets/ee7e30cb-cd95-431c-8ea1-d87afb927764)













---
```
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
