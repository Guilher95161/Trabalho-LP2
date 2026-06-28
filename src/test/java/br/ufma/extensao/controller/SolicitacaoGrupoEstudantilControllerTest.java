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
class SolicitacaoGrupoEstudantilControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired PapelService papelService;
    @Autowired UsuarioService usuarioService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String json(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    private String tokenParaPapel(String nomePapel) throws Exception {
        Papel papel = papelService.salvar(Papel.builder().nome(nomePapel).build());
        String email = "sgct-" + UUID.randomUUID() + "@test.com";
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

    private Integer criarDiscente() throws Exception {
        String email = "sgct-d-" + UUID.randomUUID() + "@test.com";
        String resp = mockMvc.perform(post("/api/discentes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "nome", "Discente",
                                "email", email,
                                "senha", "senha123",
                                "matricula", UUID.randomUUID().toString().substring(0, 8)))))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(resp).get("id").asInt();
    }

    private Integer criarSolicitacao(Integer discenteId, String tokenQualquer) throws Exception {
        String resp = mockMvc.perform(post("/api/solicitacoes-grupo")
                        .header("Authorization", "Bearer " + tokenQualquer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "solicitanteId", discenteId,
                                "nomeGrupo", "Liga " + UUID.randomUUID()))))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(resp).get("id").asInt();
    }

    @Test
    void criar_comTokenValido_deveRetornar201() throws Exception {
        String token = tokenParaPapel("COORDENADOR");
        Integer discenteId = criarDiscente();
        mockMvc.perform(post("/api/solicitacoes-grupo")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "solicitanteId", discenteId,
                                "nomeGrupo", "Liga " + UUID.randomUUID()))))
                .andExpect(status().isCreated());
    }

    @Test
    void avaliar_comPapelCoordenador_deveRetornar200() throws Exception {
        String tokenCoord = tokenParaPapel("COORDENADOR");
        Integer discenteId = criarDiscente();
        Integer solId = criarSolicitacao(discenteId, tokenCoord);

        mockMvc.perform(post("/api/solicitacoes-grupo/" + solId + "/avaliar")
                        .header("Authorization", "Bearer " + tokenCoord)
                        .param("aprovado", "false"))
                .andExpect(status().isOk());
    }

    @Test
    void avaliar_semPapelCoordenador_deveRetornar403() throws Exception {
        String tokenCoord = tokenParaPapel("COORDENADOR");
        Integer discenteId = criarDiscente();
        Integer solId = criarSolicitacao(discenteId, tokenCoord);

        String email = "sgct-" + UUID.randomUUID() + "@test.com";
        mockMvc.perform(post("/api/discentes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("nome", "D", "email", email, "senha", "s", "matricula", "M"))));
        String resp = mockMvc.perform(post("/api/usuarios/autenticar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", email, "senha", "s"))))
                .andReturn().getResponse().getContentAsString();
        String tokenSemPapel = objectMapper.readTree(resp).get("token").asText();

        mockMvc.perform(post("/api/solicitacoes-grupo/" + solId + "/avaliar")
                        .header("Authorization", "Bearer " + tokenSemPapel)
                        .param("aprovado", "false"))
                .andExpect(status().isForbidden());
    }

    @Test
    void listarPendentes_comPapelCoordenador_deveRetornar200() throws Exception {
        String token = tokenParaPapel("COORDENADOR");
        mockMvc.perform(get("/api/solicitacoes-grupo/pendentes")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
