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

Arquivo fonte principal: `consumoenergetico/src/main/java/br/com/fiap/consumoenergetico/ConsumoenergeticoApplication.java`

Se quiser, eu atualizo o README com exemplos de resposta paginada (ex.: estrutura do Page) ou adiciono um exemplo de Postman/Insomnia.

## Projeto - Cidades ESGInteligentes

Este README também contém a documentação exigida para o projeto "Cidades ESGInteligentes" (mapear e documentar execução, containerização, pipeline e evidências).

### Como executar localmente com Docker

Passos mínimos para subir a aplicação localmente usando Docker:

1. (Opcional) Build da imagem localmente:

```powershell
docker build -t consumoenergetico:local -f consumoenergetico/Dockerfile consumoenergetico
```

2. Subir usando o docker-compose do repositório (usa variáveis de ambiente para credenciais):

```powershell
docker compose -f consumoenergetico/docker-compose.yml up -d
```

3. Verificar logs e endpoints:

```powershell
docker compose -f consumoenergetico/docker-compose.yml logs -f api
# Acessar: http://localhost:8080/api/consumo
```

Notas:
- Configure as variáveis `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` e `SPRING_DATASOURCE_PASSWORD` no ambiente ou no arquivo `docker-compose.yml` antes de subir em ambientes reais.
- Para desenvolvimento local sem Oracle, use um profile com H2 (não incluso por padrão) ou aponte para um container Oracle compatível.

### Pipeline CI/CD

Se o repositório ainda não possui um pipeline, uma opção recomendada é usar GitHub Actions (ou GitLab CI / Azure Pipelines). O pipeline típico:

- Gatilho: push em `main/master` e pull requests.
- Etapas:
  1. Checkout do código
  2. Setup JDK 17
  3. Cache de dependências Maven
  4. Build e execução de testes unitários (`mvn test`)
  5. Análise estática (opcional): SpotBugs, Checkstyle, SonarQube
  6. Build do artefato (`mvn package -DskipTests`) e verificação mínima
  7. Build da imagem Docker e push para registry (GHCR, Docker Hub, Azure Container Registry)
  8. Deploy para staging (ex.: via Docker Compose, SSH, ou Kubernetes)
  9. (Opcional) Promotion para produção após aprovação manual

Exemplo mínimo de workflow GitHub Actions (colocar em `.github/workflows/ci.yml`):

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

Adapte a parte de push de imagem para o registry que você usa e adicione etapas de deploy conforme o ambiente (staging/prod).

### Containerização

Dockerfile (presente em `consumoenergetico/Dockerfile`):

```dockerfile
# Etapa 1: build (Maven já instalado)
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /src

COPY pom.xml .
RUN mvn -B -ntp -q dependency:go-offline

COPY src ./src
RUN mvn -B -ntp clean package -DskipTests

# Etapa 2: runtime (JRE)
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /src/target/*.jar /app/app.jar
EXPOSE 8080
ENV JAVA_OPTS=""
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
```

Estratégias adotadas:
- Multi-stage build para manter a imagem final enxuta; todas as dependências e o build ocorrem na imagem `maven`, o runtime usa apenas a JRE.
- Cache das dependências com `dependency:go-offline` para acelerar builds em ambientes CI.
- Exposição da porta 8080 e uso de `JAVA_OPTS` para ajustar memória em runtime.

Arquivos `docker-compose.yml` (staging/prod) também estão no repositório e demonstram como parametrizar variáveis de ambiente para conexão com o banco.

### Prints do funcionamento

Inclua evidências em `docs/screenshots/` no repositório. Exemplos de imagens que valem a pena incluir:

- Tela com container em execução (`docker ps` / logs)
- Resposta de um GET em `/api/consumo` no browser/Postman
- Deploy ou logs do pipeline indicando sucesso

Exemplo de inserção no README (substitua os arquivos reais):

```markdown
![API rodando - docker ps](docs/screenshots/docker-ps.png)
![GET /api/consumo - Postman](docs/screenshots/get-consumo-postman.png)
```

### Tecnologias utilizadas

- Linguagem: Java 17
- Framework: Spring Boot
- Segurança: Spring Security
- Persistência: Spring Data JPA, Hibernate
- Migração de banco: Flyway
- Banco de dados (destino): Oracle
- Build: Maven (wrapper `mvnw` incluso)
- Containerização: Docker, Docker Compose
- Testes: JUnit, Mockito (adicionar conforme necessário)
- Outros: Lombok, Jakarta Validation

---

