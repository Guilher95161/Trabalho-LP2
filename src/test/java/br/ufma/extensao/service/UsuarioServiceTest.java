package br.ufma.extensao.service;

import br.ufma.extensao.model.Papel;
import br.ufma.extensao.model.Usuario;
import br.ufma.extensao.service.exceptions.EmailJaCadastradoException;
import br.ufma.extensao.service.exceptions.RegraNegocioRunTime;
import br.ufma.extensao.service.exceptions.UsuarioInativoException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    void salvar_deveCodificarSenha() {
        Usuario salvo = service.salvar(novoUsuario("a@test.com"));
        assertThat(salvo.getId()).isNotNull();
        assertThat(encoder.matches("senha123", salvo.getSenha())).isTrue();
    }

    @Test
    void salvar_deveLancarExcecaoEmailDuplicado() {
        service.salvar(novoUsuario("dup@test.com"));
        assertThatThrownBy(() -> service.salvar(novoUsuario("dup@test.com")))
                .isInstanceOf(EmailJaCadastradoException.class);
    }

    @Test
    void efetuarLogin_deveRetornarTrueParaCredenciaisCorretas() {
        service.salvar(novoUsuario("login@test.com"));
        assertThat(service.efetuarLogin("login@test.com", "senha123")).isTrue();
    }

    @Test
    void efetuarLogin_deveLancarExcecaoSenhaErrada() {
        service.salvar(novoUsuario("wrong@test.com"));
        assertThatThrownBy(() -> service.efetuarLogin("wrong@test.com", "errada"))
                .isInstanceOf(RegraNegocioRunTime.class);
    }

    @Test
    void efetuarLogin_deveLancarExcecaoContaDesativada() {
        Usuario u = service.salvar(novoUsuario("inativo@test.com"));
        service.desativarUsuario(u.getId());
        assertThatThrownBy(() -> service.efetuarLogin("inativo@test.com", "senha123"))
                .isInstanceOf(UsuarioInativoException.class);
    }

    @Test
    void atualizar_devePreservarPapeis() {
        Papel papel = papelService.salvar(Papel.builder().nome("DOCENTE_TEST").build());
        Usuario u = new Usuario();
        u.setNome("Original");
        u.setEmail("papeis@test.com");
        u.setSenha("senha123");
        u.setMatricula("M1");
        u.setAtivo(true);
        u.setPapeis(List.of(papel));
        Usuario salvo = service.salvar(u);

        Usuario patch = new Usuario();
        patch.setId(salvo.getId());
        patch.setNome("Atualizado");
        Usuario atualizado = service.atualizar(patch);

        assertThat(atualizado.getNome()).isEqualTo("Atualizado");
        assertThat(atualizado.getPapeis()).hasSize(1);
        assertThat(atualizado.getPapeis().get(0).getNome()).isEqualTo("DOCENTE_TEST");
    }

    @Test
    void atualizar_devePreservarAtivo() {
        Usuario u = service.salvar(novoUsuario("ativo@test.com"));
        service.desativarUsuario(u.getId());

        Usuario patch = new Usuario();
        patch.setId(u.getId());
        patch.setNome("Novo Nome");
        Usuario atualizado = service.atualizar(patch);

        assertThat(atualizado.isAtivo()).isFalse();
    }

    @Test
    void desativarReativar_deveMudarFlagAtivo() {
        Usuario u = service.salvar(novoUsuario("toggle@test.com"));
        assertThat(u.isAtivo()).isTrue();

        Usuario desativado = service.desativarUsuario(u.getId());
        assertThat(desativado.isAtivo()).isFalse();

        Usuario reativado = service.reativarUsuario(u.getId());
        assertThat(reativado.isAtivo()).isTrue();
    }
}
