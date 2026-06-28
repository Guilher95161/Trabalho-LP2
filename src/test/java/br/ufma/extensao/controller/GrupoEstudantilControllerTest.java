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
class GrupoEstudantilControllerTest {

    record TokenComId(String token, Integer id) {}

    @Autowired MockMvc mockMvc;
    @Autowired PapelService papelService;
    @Autowired UsuarioService usuarioService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String json(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    private TokenComId criarUsuarioComPapel(String nomePapel) throws Exception {
        Papel papel = papelService.salvar(Papel.builder().nome(nomePapel).build());
        String email = "gct-" + UUID.randomUUID() + "@test.com";
        Usuario u = new Usuario();
        u.setNome("Teste " + nomePapel);
        u.setEmail(email);
        u.setSenha("senha123");
        u.setMatricula(UUID.randomUUID().toString().substring(0, 8));
        u.setAtivo(true);
        u.setPapeis(List.of(papel));
        Usuario salvo = usuarioService.salvar(u);

        String resp = mockMvc.perform(post("/api/usuarios/autenticar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", email, "senha", "senha123"))))
                .andReturn().getResponse().getContentAsString();
        String token = objectMapper.readTree(resp).get("token").asText();
        return new TokenComId(token, salvo.getId());
    }

    private Integer criarGrupo(String token, Integer responsavelId) throws Exception {
        String resp = mockMvc.perform(post("/api/grupos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "nome", "Grupo " + UUID.randomUUID(),
                                "responsavelId", responsavelId))))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(resp).get("id").asInt();
    }

    @Test
    void criar_comPapelDocente_deveRetornar201() throws Exception {
        TokenComId docente = criarUsuarioComPapel("DOCENTE");
        mockMvc.perform(post("/api/grupos")
                        .header("Authorization", "Bearer " + docente.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "nome", "Grupo " + UUID.randomUUID(),
                                "responsavelId", docente.id()))))
                .andExpect(status().isCreated());
    }

    @Test
    void criar_semPapelAdequado_deveRetornar403() throws Exception {
        String email = "gct-" + UUID.randomUUID() + "@test.com";
        mockMvc.perform(post("/api/discentes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("nome", "D", "email", email, "senha", "s", "matricula", "M"))));
        String resp = mockMvc.perform(post("/api/usuarios/autenticar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", email, "senha", "s"))))
                .andReturn().getResponse().getContentAsString();
        String token = objectMapper.readTree(resp).get("token").asText();

        mockMvc.perform(post("/api/grupos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("nome", "Grupo X", "responsavelId", 1))))
                .andExpect(status().isForbidden());
    }

    @Test
    void buscar_comToken_deveRetornar200() throws Exception {
        TokenComId docente = criarUsuarioComPapel("DOCENTE");
        mockMvc.perform(get("/api/grupos/obter")
                        .header("Authorization", "Bearer " + docente.token()))
                .andExpect(status().isOk());
    }

    @Test
    void remover_comPapelAdmin_deveRetornar204() throws Exception {
        TokenComId docente = criarUsuarioComPapel("DOCENTE");
        TokenComId admin = criarUsuarioComPapel("ADMINISTRADOR");
        Integer grupoId = criarGrupo(docente.token(), docente.id());

        mockMvc.perform(delete("/api/grupos/" + grupoId)
                        .header("Authorization", "Bearer " + admin.token()))
                .andExpect(status().isNoContent());
    }

    @Test
    void remover_comPapelDocente_deveRetornar403() throws Exception {
        TokenComId docente = criarUsuarioComPapel("DOCENTE");
        Integer grupoId = criarGrupo(docente.token(), docente.id());

        mockMvc.perform(delete("/api/grupos/" + grupoId)
                        .header("Authorization", "Bearer " + docente.token()))
                .andExpect(status().isForbidden());
    }
}
