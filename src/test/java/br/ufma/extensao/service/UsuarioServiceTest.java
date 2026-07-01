package br.ufma.extensao.service;

import br.ufma.extensao.model.Papel;
import br.ufma.extensao.model.Usuario;
import br.ufma.extensao.service.exceptions.EmailJaCadastradoException;
import br.ufma.extensao.service.exceptions.RegraNegocioRunTime;
import br.ufma.extensao.service.exceptions.UsuarioInativoException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
@Transactional
class UsuarioServiceTest {

    @Autowired
    UsuarioService service;

    @Autowired
    PapelService papelService;

    @Autowired
    PasswordEncoder encoder;

    private Usuario novoUsuario(String email) {
        Usuario u = new Usuario();
        u.setNome("Teste");
        u.setEmail(email);
        u.setSenha("senha123");
        u.setMatricula("MAT001");
        u.setAtivo(true);
        return u;
    }

    @Test
    void deveSalvarCodificandoASenha() {
        //cenario
        Usuario usuario = novoUsuario("a@test.com");

        //acao
        Usuario salvo = service.salvar(usuario);

        //verificacao
        Assertions.assertNotNull(salvo.getId());
        Assertions.assertTrue(encoder.matches("senha123", salvo.getSenha()));
    }

    @Test
    void deveLancarExcecaoAoSalvarEmailDuplicado() {
        //cenario
        service.salvar(novoUsuario("dup@test.com"));

        //acao e verificacao
        Assertions.assertThrows(EmailJaCadastradoException.class,
                () -> service.salvar(novoUsuario("dup@test.com")));
    }

    @Test
    void deveEfetuarLoginComCredenciaisCorretas() {
        //cenario
        service.salvar(novoUsuario("login@test.com"));

        //acao
        boolean logado = service.efetuarLogin("login@test.com", "senha123");

        //verificacao
        Assertions.assertTrue(logado);
    }

    @Test
    void deveLancarExcecaoNoLoginComSenhaErrada() {
        //cenario
        service.salvar(novoUsuario("wrong@test.com"));

        //acao e verificacao
        Assertions.assertThrows(RegraNegocioRunTime.class,
                () -> service.efetuarLogin("wrong@test.com", "errada"));
    }

    @Test
    void deveLancarExcecaoNoLoginComContaDesativada() {
        //cenario
        Usuario u = service.salvar(novoUsuario("inativo@test.com"));
        service.desativarUsuario(u.getId());

        //acao e verificacao
        Assertions.assertThrows(UsuarioInativoException.class,
                () -> service.efetuarLogin("inativo@test.com", "senha123"));
    }

    @Test
    void devePreservarPapeisAoAtualizar() {
        //cenario
        Papel papel = papelService.salvar(Papel.builder().nome("DOCENTE_TEST").build());
        Usuario u = new Usuario();
        u.setNome("Original");
        u.setEmail("papeis@test.com");
        u.setSenha("senha123");
        u.setMatricula("M1");
        u.setAtivo(true);
        u.setPapeis(List.of(papel));
        Usuario salvo = service.salvar(u);

        //acao
        Usuario patch = new Usuario();
        patch.setId(salvo.getId());
        patch.setNome("Atualizado");
        Usuario atualizado = service.atualizar(patch);

        //verificacao
        Assertions.assertEquals("Atualizado", atualizado.getNome());
        Assertions.assertEquals(1, atualizado.getPapeis().size());
        Assertions.assertEquals("DOCENTE_TEST", atualizado.getPapeis().get(0).getNome());
    }

    @Test
    void devePreservarAtivoAoAtualizar() {
        //cenario
        Usuario u = service.salvar(novoUsuario("ativo@test.com"));
        service.desativarUsuario(u.getId());

        //acao
        Usuario patch = new Usuario();
        patch.setId(u.getId());
        patch.setNome("Novo Nome");
        Usuario atualizado = service.atualizar(patch);

        //verificacao
        Assertions.assertFalse(atualizado.isAtivo());
    }

    @Test
    void deveDesativarEReativarUsuario() {
        //cenario
        Usuario u = service.salvar(novoUsuario("toggle@test.com"));

        //acao
        boolean ativoInicial = u.isAtivo();
        boolean aposDesativar = service.desativarUsuario(u.getId()).isAtivo();
        boolean aposReativar = service.reativarUsuario(u.getId()).isAtivo();

        //verificacao
        Assertions.assertTrue(ativoInicial);
        Assertions.assertFalse(aposDesativar);
        Assertions.assertTrue(aposReativar);
    }
}
