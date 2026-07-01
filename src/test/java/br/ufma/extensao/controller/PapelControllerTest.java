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
class PapelControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired PapelService papelService;
    @Autowired UsuarioService usuarioService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String json(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    private String tokenParaPapel(String nomePapel) throws Exception {
        Papel papel = papelService.salvar(Papel.builder().nome(nomePapel).build());
        String email = "pct-" + UUID.randomUUID() + "@test.com";
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

    @Test
    void deveCriarComPapelAdminRetornando201() throws Exception {
        //cenario
        String token = tokenParaPapel("ADMINISTRADOR");

        //acao e verificacao
        mockMvc.perform(post("/api/papeis")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("nome", "PAPEL_TESTE_" + UUID.randomUUID()))))
                .andExpect(status().isCreated());
    }

    @Test
    void deveRetornar403AoCriarSemPapelAdmin() throws Exception {
        //cenario
        String token = tokenParaPapel("DOCENTE");

        //acao e verificacao
        mockMvc.perform(post("/api/papeis")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("nome", "OUTRO"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveBuscarComPapelAdminRetornando200() throws Exception {
        //cenario
        String token = tokenParaPapel("ADMINISTRADOR");

        //acao e verificacao
        mockMvc.perform(get("/api/papeis/obter")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void deveRetornar4xxAoCriarSemToken() throws Exception {
        //cenario
        //acao e verificacao
        mockMvc.perform(post("/api/papeis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("nome", "SEM_TOKEN"))))
                .andExpect(status().is4xxClientError());
    }
}
