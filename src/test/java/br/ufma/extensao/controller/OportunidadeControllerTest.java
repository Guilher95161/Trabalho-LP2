package br.ufma.extensao.controller;

import br.ufma.extensao.model.Papel;
import br.ufma.extensao.model.Usuario;
import br.ufma.extensao.service.PapelService;
import br.ufma.extensao.service.UsuarioService;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class OportunidadeControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired PapelService papelService;
    @Autowired UsuarioService usuarioService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String unicoEmail() {
        return "opc-" + UUID.randomUUID() + "@test.com";
    }

    private String json(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    private String tokenParaPapel(String nomePapel) throws Exception {
        Papel papel = papelService.salvar(Papel.builder().nome(nomePapel).build());
        String email = unicoEmail();
        Usuario u = new Usuario();
        u.setNome("Teste " + nomePapel);
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

    private String tokenSemPapel() throws Exception {
        String email = unicoEmail();
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

    private Map<String, Object> oportunidadeBody() {
        return Map.of(
                "titulo", "Oportunidade " + UUID.randomUUID(),
                "descricao", "Descricao de teste",
                "modalidade", "CURSO",
                "status", "RASCUNHO",
                "periodoRealizacao", "01/01/2026 - 31/01/2026",
                "cargaHoraria", 20,
                "vagas", 10);
    }

    @Test
    void deveCriarComPapelDocenteRetornando201() throws Exception {
        //cenario
        String token = tokenParaPapel("DOCENTE");

        //acao e verificacao
        mockMvc.perform(post("/api/oportunidades")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(oportunidadeBody())))
                .andExpect(status().isCreated());
    }

    @Test
    void deveRetornar403AoCriarSemPapelAdequado() throws Exception {
        //cenario
        String token = tokenSemPapel();

        //acao e verificacao
        mockMvc.perform(post("/api/oportunidades")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(oportunidadeBody())))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveAprovarComPapelCoordenadorRetornando200() throws Exception {
        //cenario
        String tokenDocente = tokenParaPapel("DOCENTE");
        String tokenCoord = tokenParaPapel("COORDENADOR");

        String criado = mockMvc.perform(post("/api/oportunidades")
                        .header("Authorization", "Bearer " + tokenDocente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(oportunidadeBody())))
                .andReturn().getResponse().getContentAsString();
        Integer id = objectMapper.readTree(criado).get("id").asInt();

        mockMvc.perform(post("/api/oportunidades/" + id + "/submeter")
                .header("Authorization", "Bearer " + tokenDocente));

        //acao e verificacao
        mockMvc.perform(post("/api/oportunidades/" + id + "/aprovar")
                        .header("Authorization", "Bearer " + tokenCoord))
                .andExpect(status().isOk());
    }

    @Test
    void deveRetornar403AoAprovarComPapelDocente() throws Exception {
        //cenario
        String tokenDocente = tokenParaPapel("DOCENTE");

        String criado = mockMvc.perform(post("/api/oportunidades")
                        .header("Authorization", "Bearer " + tokenDocente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(oportunidadeBody())))
                .andReturn().getResponse().getContentAsString();
        Integer id = objectMapper.readTree(criado).get("id").asInt();

        mockMvc.perform(post("/api/oportunidades/" + id + "/submeter")
                .header("Authorization", "Bearer " + tokenDocente));

        //acao e verificacao
        mockMvc.perform(post("/api/oportunidades/" + id + "/aprovar")
                        .header("Authorization", "Bearer " + tokenDocente))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRemoverComPapelAdminRetornando204() throws Exception {
        //cenario
        String tokenAdmin = tokenParaPapel("ADMINISTRADOR");
        String tokenDocente = tokenParaPapel("DOCENTE");

        String criado = mockMvc.perform(post("/api/oportunidades")
                        .header("Authorization", "Bearer " + tokenDocente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(oportunidadeBody())))
                .andReturn().getResponse().getContentAsString();
        Integer id = objectMapper.readTree(criado).get("id").asInt();

        //acao e verificacao
        mockMvc.perform(delete("/api/oportunidades/" + id)
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornar403AoRemoverComPapelDocente() throws Exception {
        //cenario
        String tokenDocente = tokenParaPapel("DOCENTE");

        String criado = mockMvc.perform(post("/api/oportunidades")
                        .header("Authorization", "Bearer " + tokenDocente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(oportunidadeBody())))
                .andReturn().getResponse().getContentAsString();
        Integer id = objectMapper.readTree(criado).get("id").asInt();

        //acao e verificacao
        mockMvc.perform(delete("/api/oportunidades/" + id)
                        .header("Authorization", "Bearer " + tokenDocente))
                .andExpect(status().isForbidden());
    }
}
