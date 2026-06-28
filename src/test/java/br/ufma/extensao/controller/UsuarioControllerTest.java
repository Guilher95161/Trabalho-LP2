package br.ufma.extensao.controller;

import br.ufma.extensao.model.Papel;
import br.ufma.extensao.model.Usuario;
import br.ufma.extensao.service.PapelService;
import br.ufma.extensao.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UsuarioControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PapelService papelService;

    @Autowired
    UsuarioService usuarioService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String unicoEmail() {
        return "ctrl-" + UUID.randomUUID() + "@test.com";
    }

    private String json(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    private String obterToken(String email, String senha) throws Exception {
        String resp = mockMvc.perform(post("/api/usuarios/autenticar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", email, "senha", senha))))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(resp).get("token").asText();
    }

    @Test
    void salvar_deveRetornar201() throws Exception {
        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "nome", "Usuario Novo",
                                "email", unicoEmail(),
                                "senha", "abc123",
                                "matricula", "M001"))))
                .andExpect(status().isCreated());
    }

    @Test
    void salvar_deveRetornar400_semNome() throws Exception {
        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "email", unicoEmail(),
                                "senha", "abc123"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void salvar_deveRetornar400_emailDuplicado() throws Exception {
        String email = unicoEmail();
        String corpo = json(Map.of("nome", "Dup", "email", email, "senha", "abc123", "matricula", "M002"));
        mockMvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON).content(corpo));
        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isBadRequest());
    }

    @Test
    void autenticar_deveRetornar200ComToken() throws Exception {
        String email = unicoEmail();
        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("nome", "Login User", "email", email, "senha", "senha123", "matricula", "LG001"))));

        mockMvc.perform(post("/api/usuarios/autenticar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", email, "senha", "senha123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void autenticar_deveRetornar400_senhaErrada() throws Exception {
        String email = unicoEmail();
        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("nome", "Wrong Pass", "email", email, "senha", "certa", "matricula", "WP001"))));

        mockMvc.perform(post("/api/usuarios/autenticar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", email, "senha", "errada"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buscarPorId_semToken_deveNegarAcesso() throws Exception {
        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void buscarPorId_comToken_deveRetornar200() throws Exception {
        String email = unicoEmail();
        String respCadastro = mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("nome", "Auth Test", "email", email, "senha", "senha123", "matricula", "AT001"))))
                .andReturn().getResponse().getContentAsString();
        Integer id = objectMapper.readTree(respCadastro).get("id").asInt();

        String token = obterToken(email, "senha123");

        mockMvc.perform(get("/api/usuarios/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void desativar_comPapelAdmin_deveRetornar200() throws Exception {
        Papel pAdmin = papelService.salvar(Papel.builder().nome("ADMINISTRADOR").build());
        String emailAdmin = "adm-" + UUID.randomUUID() + "@test.com";
        Usuario admin = new Usuario();
        admin.setNome("Admin Teste");
        admin.setEmail(emailAdmin);
        admin.setSenha("adm123");
        admin.setMatricula(UUID.randomUUID().toString().substring(0, 8));
        admin.setAtivo(true);
        admin.setPapeis(List.of(pAdmin));
        usuarioService.salvar(admin);

        String tokenAdmin = obterToken(emailAdmin, "adm123");

        String emailAlvo = unicoEmail();
        String respCadastro = mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("nome", "Alvo", "email", emailAlvo, "senha", "abc123", "matricula", "ALV001"))))
                .andReturn().getResponse().getContentAsString();
        Integer alvoId = objectMapper.readTree(respCadastro).get("id").asInt();

        mockMvc.perform(put("/api/usuarios/" + alvoId + "/desativar")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk());
    }

    @Test
    void desativar_semPapelAdmin_deveRetornar403() throws Exception {
        String emailAlvo = unicoEmail();
        String respCadastro = mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("nome", "Alvo2", "email", emailAlvo, "senha", "abc123", "matricula", "ALV002"))))
                .andReturn().getResponse().getContentAsString();
        Integer alvoId = objectMapper.readTree(respCadastro).get("id").asInt();

        String tokenSemPapel = obterToken(emailAlvo, "abc123");

        mockMvc.perform(put("/api/usuarios/" + alvoId + "/desativar")
                        .header("Authorization", "Bearer " + tokenSemPapel))
                .andExpect(status().isForbidden());
    }
}
