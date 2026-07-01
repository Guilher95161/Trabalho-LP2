package br.ufma.extensao.repo;

import br.ufma.extensao.model.Usuario;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
class UsuarioRepositoryTest {

    @Autowired
    UsuarioRepository repository;

    private Usuario novoUsuario(String email) {
        Usuario u = new Usuario();
        u.setNome("Teste Repo");
        u.setEmail(email);
        u.setSenha("$2a$10$hashbcrypt");
        u.setMatricula("MAT-REPO");
        u.setAtivo(true);
        return u;
    }

    @Test
    void deveGerarIdAutoIncrementadoAoSalvar() {
        //cenario
        Usuario usuario = novoUsuario("repo@test.com");

        //acao
        Usuario salvo = repository.save(usuario);

        //verificacao
        Assertions.assertNotNull(salvo.getId());
        Assertions.assertTrue(salvo.getId() > 0);
    }

    @Test
    void deveEncontrarUsuarioPorEmailExistente() {
        //cenario
        repository.save(novoUsuario("find@test.com"));

        //acao
        Optional<Usuario> resultado = repository.findByEmail("find@test.com");

        //verificacao
        Assertions.assertTrue(resultado.isPresent());
        Assertions.assertEquals("Teste Repo", resultado.get().getNome());
    }

    @Test
    void deveRetornarVazioAoBuscarEmailInexistente() {
        //cenario
        //acao
        Optional<Usuario> resultado = repository.findByEmail("nao-existe@test.com");

        //verificacao
        Assertions.assertTrue(resultado.isEmpty());
    }

    @Test
    void deveConfirmarQueEmailCadastradoExiste() {
        //cenario
        repository.save(novoUsuario("exists@test.com"));

        //acao e verificacao
        Assertions.assertTrue(repository.existsByEmail("exists@test.com"));
    }

    @Test
    void deveConfirmarQueEmailNaoCadastradoNaoExiste() {
        //cenario
        //acao e verificacao
        Assertions.assertFalse(repository.existsByEmail("nao-tem@test.com"));
    }
}
