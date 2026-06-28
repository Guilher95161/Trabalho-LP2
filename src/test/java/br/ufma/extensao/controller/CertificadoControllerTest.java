package br.ufma.extensao.controller;

import br.ufma.extensao.model.Papel;
import br.ufma.extensao.model.Usuario;
import br.ufma.extensao.service.PapelService;
import br.ufma.extensao.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CertificadoControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired PapelService papelService;
    @Autowired UsuarioService usuarioService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String json(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    private String tokenParaPapel(String nomePapel) throws Exception {
        Papel papel = papelService.salvar(Papel.builder().nome(nomePapel).build());
        String email = "cft-" + UUID.randomUUID() + "@test.com";
        Usuario u = new Usuario();
        u.setNome("Teste");
        u.setEmail(email);
        u.setSenha("senha123");
        u.setMatricula(UUID.randomUUID().toString().substring(0, 8));
        u.setAtivo(true);
        u.setPapeis(List.of(papel));
        usuarioService.salvar(u);

        String resp = mockMvc.perform(post("/api/usuarios/autenticar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", email, "senha", "senha123"))))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(resp).get("token").asText();
    }

    private Map<String, Object> certificadoBody() {
        return Map.of("tituloAtividade", "Curso " + UUID.randomUUID(), "cargaHoraria", 16);
    }

    @Test
    void criar_comPapelDocente_deveRetornar201() throws Exception {
        String token = tokenParaPapel("DOCENTE");
        mockMvc.perform(post("/api/certificados")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(certificadoBody())))
                .andExpect(status().isCreated());
    }

    @Test
    void criar_semPapelAdequado_deveRetornar403() throws Exception {
        String email = "cft-" + UUID.randomUUID() + "@test.com";
        mockMvc.perform(post("/api/discentes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("nome", "D", "email", email, "senha", "s", "matricula", "M"))));
        String resp = mockMvc.perform(post("/api/usuarios/autenticar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", email, "senha", "s"))))
                .andReturn().getResponse().getContentAsString();
        String token = objectMapper.readTree(resp).get("token").asText();

        mockMvc.perform(post("/api/certificados")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(certificadoBody())))
                .andExpect(status().isForbidden());
    }

    @Test
    void buscarPorId_comToken_deveRetornar200() throws Exception {
        String token = tokenParaPapel("DOCENTE");
        String resp = mockMvc.perform(post("/api/certificados")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(certificadoBody())))
                .andReturn().getResponse().getContentAsString();
        Integer id = objectMapper.readTree(resp).get("id").asInt();

        mockMvc.perform(get("/api/certificados/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void remover_comPapelAdmin_deveRetornar204() throws Exception {
        String tokenDocente = tokenParaPapel("DOCENTE");
        String tokenAdmin = tokenParaPapel("ADMINISTRADOR");

        String resp = mockMvc.perform(post("/api/certificados")
                        .header("Authorization", "Bearer " + tokenDocente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(certificadoBody())))
                .andReturn().getResponse().getContentAsString();
        Integer id = objectMapper.readTree(resp).get("id").asInt();

        mockMvc.perform(delete("/api/certificados/" + id)
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNoContent());
    }
}
