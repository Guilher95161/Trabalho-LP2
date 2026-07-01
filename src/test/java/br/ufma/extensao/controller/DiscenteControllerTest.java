package br.ufma.extensao.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
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
    void deveSalvarSemTokenRetornando201() throws Exception {
        //cenario
        Map<String, Object> corpo = Map.of(
                "nome", "Novo Discente",
                "email", unicoEmail(),
                "senha", "senha123",
                "matricula", UUID.randomUUID().toString().substring(0, 8));

        //acao e verificacao
        mockMvc.perform(post("/api/discentes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(corpo)))
                .andExpect(status().isCreated());
    }

    @Test
    void deveRetornar400AoSalvarSemNome() throws Exception {
        //cenario
        Map<String, Object> corpo = Map.of(
                "email", unicoEmail(),
                "senha", "senha123",
                "matricula", "M001");

        //acao e verificacao
        mockMvc.perform(post("/api/discentes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(corpo)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveBuscarPorIdComTokenRetornando200() throws Exception {
        //cenario
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

        //acao e verificacao
        mockMvc.perform(get("/api/discentes/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void deveRetornarPainelHorasComTokenRetornando200() throws Exception {
        //cenario
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
        // discente ja criado - apenas autentica sem criar novamente
        String authResp = mockMvc.perform(post("/api/usuarios/autenticar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", email, "senha", "senha123"))))
                .andReturn().getResponse().getContentAsString();
        String token = objectMapper.readTree(authResp).get("token").asText();

        //acao e verificacao
        mockMvc.perform(get("/api/discentes/" + id + "/painel-horas")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.discenteId").value(id));
    }

    @Test
    void deveRetornar4xxAoBuscarPorIdSemToken() throws Exception {
        //cenario
        //acao e verificacao
        mockMvc.perform(get("/api/discentes/1"))
                .andExpect(status().is4xxClientError());
    }
}
