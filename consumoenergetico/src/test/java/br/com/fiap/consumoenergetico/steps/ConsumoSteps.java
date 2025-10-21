package br.com.fiap.consumoenergetico.steps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.E;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Steps em PT-BR para testar o ConsumoController.
 * - Usa Rest-Assured
 * - Valida contrato com JSON Schema (coloque os schemas em src/test/resources/schemas)
 * - Lê payloads de src/test/resources/payloads
 * - Suporta DataTable no formato: | campo | valor |
 */
public class ConsumoSteps {

    private String baseUrl;
    private Response lastResponse;
    private Long lastId;
    private String payloadTemporario; // usado nos cenários com DataTable

    private final ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setup() {
        // -DbaseUrl=http://localhost:8080/api  (padrão)
        String prop = System.getProperty("baseUrl", "http://localhost:8080/api");
        baseUrl = prop.endsWith("/") ? prop.substring(0, prop.length() - 1) : prop;
        RestAssured.baseURI = baseUrl;
    }

    // =========================
    // Utilitários de apoio
    // =========================

    private String readResource(String path) {
        InputStream is = getClass().getClassLoader().getResourceAsStream(path);
        if (is == null) throw new IllegalArgumentException("Recurso não encontrado: " + path);
        try (Scanner s = new Scanner(is, StandardCharsets.UTF_8)) {
            s.useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }
    }

    private String mapToJsonFromTable(DataTable table) throws Exception {
        Map<String, String> map = new LinkedHashMap<>();
        for (Map<String, String> row : table.asMaps(String.class, String.class)) {
            map.put(row.get("campo"), row.get("valor"));
        }
        return mapper.writeValueAsString(map);
    }

    // =========================
    // BACKGROUND / CONTEXTO
    // =========================

    @Dado("que a baseUrl está configurada")
    public void baseUrl_configurada() {
        // noop: feito no @Before
    }

    // =========================
    // Ações (Quando)
    // =========================

    @Quando("eu faço GET para {string}")
    public void eu_faco_get_para(String path) {
        lastResponse = RestAssured
                .given()
                .accept(ContentType.JSON)
                .when()
                .get(path)
                .then()
                .extract().response();
    }

    @Quando("eu faço POST para {string} com o payload {string}")
    public void eu_faco_post_para_com_payload(String path, String payloadFile) {
        String body = readResource("payloads/" + payloadFile);
        lastResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(path)
                .then()
                .extract().response();
    }

    @Quando("eu faço PUT para {string} com o payload {string} \\(injetando o id salvo)")
    public void eu_faco_put_para_injetando_id_saldo(String path, String payloadFile) throws Exception {
        String body = readResource("payloads/" + payloadFile);
        JsonNode root = mapper.readTree(body);
        if (!(root instanceof ObjectNode)) {
            throw new IllegalArgumentException("Payload de update deve ser um objeto JSON");
        }
        ((ObjectNode) root).put("id", lastId);

        lastResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(root))
                .when()
                .put(path)
                .then()
                .extract().response();
    }

    @Quando("eu faço DELETE para {string}")
    public void eu_faco_delete_para(String path) {
        // suporta placeholder {id-salvo}
        String resolved = path.replace("{id-salvo}", String.valueOf(lastId));
        lastResponse = RestAssured
                .given()
                .when()
                .delete(resolved)
                .then()
                .extract().response();
    }

    // ===== DataTable (seu exemplo em PT) =====

    @Dado("que eu tenha os seguintes dados do consumo:")
    public void que_eu_tenha_os_seguintes_dados_do_consumo(DataTable tabela) throws Exception {
        payloadTemporario = mapToJsonFromTable(tabela);
    }

    @Quando("eu enviar a requisição para o endpoint {string} de cadastro de consumos")
    public void eu_enviar_requisicao_para_endpoint_de_cadastro(String path) {
        lastResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(payloadTemporario)
                .when()
                .post(path)
                .then()
                .extract().response();
    }

    // =========================
    // Preparação de estado (Given)
    // =========================

    @Dado("que eu tenho um consumo cadastrado via {string}")
    public void que_eu_tenho_um_consumo_cadastrado_via(String payloadFile) {
        String body = readResource("payloads/" + payloadFile);
        lastResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/consumo")
                .then()
                .extract().response();

        if (lastResponse.statusCode() != 201) {
            throw new AssertionError("Esperava 201 ao preparar dado, recebi: "
                    + lastResponse.statusCode() + " body=" + lastResponse.asString());
        }
        lastId = lastResponse.jsonPath().getLong("id");
    }

    // =========================
    // Asserções (Então/E)
    // =========================

    @Entao("o status da resposta deve ser {int}")
    public void o_status_da_resposta_deve_ser(Integer status) {
        if (lastResponse.statusCode() != status) {
            System.err.println("Status recebido: " + lastResponse.statusCode());
            System.err.println("Body de erro: " + lastResponse.asString());
        }
        lastResponse.then().statusCode(status);
    }

    // alias, caso use “o status deve ser …”
    @Entao("o status deve ser {int}")
    public void o_status_deve_ser(Integer status) {
        if (lastResponse.statusCode() != status) {
            System.err.println("Status recebido: " + lastResponse.statusCode());
            System.err.println("Body de erro: " + lastResponse.asString());
        }
        lastResponse.then().statusCode(status);
    }

    @E("a resposta deve obedecer o schema {string}")
    public void a_resposta_deve_obedecer_o_schema(String schemaFile) {
        lastResponse.then().body(matchesJsonSchemaInClasspath("schemas/" + schemaFile));
    }

    @E("eu salvo o {string} retornado")
    public void eu_salvo_o_campo_retornado(String campo) {
        Object valor = lastResponse.jsonPath().get(campo);
        assertThat("Campo " + campo + " deve existir na resposta", valor, notNullValue());
        if ("id".equals(campo)) {
            lastId = ((Number) valor).longValue();
        }
    }
}