package br.ufma.extensao.repo;

import br.ufma.extensao.model.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

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
    void salvar_deveGerarIdAutoIncrementado() {
        Usuario salvo = repository.save(novoUsuario("repo@test.com"));
        assertThat(salvo.getId()).isNotNull().isPositive();
    }

    @Test
    void findByEmail_deveRetornarPresent_quandoEmailExiste() {
        repository.save(novoUsuario("find@test.com"));

        Optional<Usuario> resultado = repository.findByEmail("find@test.com");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNome()).isEqualTo("Teste Repo");
    }

    @Test
    void findByEmail_deveRetornarEmpty_quandoEmailNaoExiste() {
        Optional<Usuario> resultado = repository.findByEmail("nao-existe@test.com");
        assertThat(resultado).isEmpty();
    }

    @Test
    void existsByEmail_deveRetornarTrue_quandoEmailCadastrado() {
        repository.save(novoUsuario("exists@test.com"));
        assertThat(repository.existsByEmail("exists@test.com")).isTrue();
    }

    @Test
    void existsByEmail_deveRetornarFalse_quandoEmailNaoCadastrado() {
        assertThat(repository.existsByEmail("nao-tem@test.com")).isFalse();
    }
}
