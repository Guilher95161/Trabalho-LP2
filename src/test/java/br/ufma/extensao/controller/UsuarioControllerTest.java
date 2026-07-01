package br.ufma.extensao.controller;

import br.ufma.extensao.model.Papel;
import br.ufma.extensao.model.Usuario;
import br.ufma.extensao.service.PapelService;
import br.ufma.extensao.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
    void deveSalvarRetornando201() throws Exception {
        //cenario
        Map<String, Object> corpo = Map.of(
                "nome", "Usuario Novo",
                "email", unicoEmail(),
                "senha", "abc123",
                "matricula", "M001");

        //acao e verificacao
        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(corpo)))
                .andExpect(status().isCreated());
    }

    @Test
    void deveRetornar400AoSalvarSemNome() throws Exception {
        //cenario
        Map<String, Object> corpo = Map.of(
                "email", unicoEmail(),
                "senha", "abc123");

        //acao e verificacao
        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(corpo)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar400AoSalvarComEmailDuplicado() throws Exception {
        //cenario
        String email = unicoEmail();
        String corpo = json(Map.of("nome", "Dup", "email", email, "senha", "abc123", "matricula", "M002"));
        mockMvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON).content(corpo));

        //acao e verificacao
        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveAutenticarRetornando200ComToken() throws Exception {
        //cenario
        String email = unicoEmail();
        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("nome", "Login User", "email", email, "senha", "senha123", "matricula", "LG001"))));

        //acao e verificacao
        mockMvc.perform(post("/api/usuarios/autenticar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", email, "senha", "senha123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void deveRetornar400AoAutenticarComSenhaErrada() throws Exception {
        //cenario
        String email = unicoEmail();
        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("nome", "Wrong Pass", "email", email, "senha", "certa", "matricula", "WP001"))));

        //acao e verificacao
        mockMvc.perform(post("/api/usuarios/autenticar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", email, "senha", "errada"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveNegarAcessoAoBuscarPorIdSemToken() throws Exception {
        //cenario
        //acao e verificacao
        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deveBuscarPorIdComTokenRetornando200() throws Exception {
        //cenario
        String email = unicoEmail();
        String respCadastro = mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("nome", "Auth Test", "email", email, "senha", "senha123", "matricula", "AT001"))))
                .andReturn().getResponse().getContentAsString();
        Integer id = objectMapper.readTree(respCadastro).get("id").asInt();
        String token = obterToken(email, "senha123");

        //acao e verificacao
        mockMvc.perform(get("/api/usuarios/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void deveDesativarComPapelAdminRetornando200() throws Exception {
        //cenario
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

        //acao e verificacao
        mockMvc.perform(put("/api/usuarios/" + alvoId + "/desativar")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk());
    }

    @Test
    void deveRetornar403AoDesativarSemPapelAdmin() throws Exception {
        //cenario
        String emailAlvo = unicoEmail();
        String respCadastro = mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("nome", "Alvo2", "email", emailAlvo, "senha", "abc123", "matricula", "ALV002"))))
                .andReturn().getResponse().getContentAsString();
        Integer alvoId = objectMapper.readTree(respCadastro).get("id").asInt();
        String tokenSemPapel = obterToken(emailAlvo, "abc123");

        //acao e verificacao
        mockMvc.perform(put("/api/usuarios/" + alvoId + "/desativar")
                        .header("Authorization", "Bearer " + tokenSemPapel))
                .andExpect(status().isForbidden());
    }
}
