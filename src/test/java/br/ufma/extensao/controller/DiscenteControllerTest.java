package br.ufma.extensao.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DiscenteControllerTest {

    @Autowired MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String unicoEmail() {
        return "dct-" + UUID.randomUUID() + "@test.com";
    }

    private String json(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    private String cadastrarEObterToken(String email) throws Exception {
        mockMvc.perform(post("/api/discentes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of(
                        "nome", "Discente Teste",
                        "email", email,
                        "senha", "senha123",
                        "matricula", UUID.randomUUID().toString().substring(0, 8)))));

        String resp = mockMvc.perform(post("/api/usuarios/autenticar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", email, "senha", "senha123"))))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(resp).get("token").asText();
    }

    @Test
    void salvar_semToken_deveRetornar201() throws Exception {
        mockMvc.perform(post("/api/discentes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "nome", "Novo Discente",
                                "email", unicoEmail(),
                                "senha", "senha123",
                                "matricula", UUID.randomUUID().toString().substring(0, 8)))))
                .andExpect(status().isCreated());
    }

    @Test
    void salvar_semNome_deveRetornar400() throws Exception {
        mockMvc.perform(post("/api/discentes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "email", unicoEmail(),
                                "senha", "senha123",
                                "matricula", "M001"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buscarPorId_comToken_deveRetornar200() throws Exception {
        String email = unicoEmail();
        String respCadastro = mockMvc.perform(post("/api/discentes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "nome", "Discente Get",
                                "email", email,
                                "senha", "senha123",
                                "matricula", UUID.randomUUID().toString().substring(0, 8)))))
                .andReturn().getResponse().getContentAsString();
        Integer id = objectMapper.readTree(respCadastro).get("id").asInt();
        String token = cadastrarEObterToken(unicoEmail());

        mockMvc.perform(get("/api/discentes/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void painelHoras_comToken_deveRetornar200() throws Exception {
        String email = unicoEmail();
        String respCadastro = mockMvc.perform(post("/api/discentes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "nome", "Painel Teste",
                                "email", email,
                                "senha", "senha123",
                                "matricula", UUID.randomUUID().toString().substring(0, 8)))))
                .andReturn().getResponse().getContentAsString();
        Integer id = objectMapper.readTree(respCadastro).get("id").asInt();
        // discente já criado — apenas autentica sem criar novamente
        String authResp = mockMvc.perform(post("/api/usuarios/autenticar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", email, "senha", "senha123"))))
                .andReturn().getResponse().getContentAsString();
        String token = objectMapper.readTree(authResp).get("token").asText();

        mockMvc.perform(get("/api/discentes/" + id + "/painel-horas")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.discenteId").value(id));
    }

    @Test
    void buscarPorId_semToken_deveRetornar4xx() throws Exception {
        mockMvc.perform(get("/api/discentes/1"))
                .andExpect(status().is4xxClientError());
    }
}
