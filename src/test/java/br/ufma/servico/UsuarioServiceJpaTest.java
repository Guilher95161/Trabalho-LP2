package br.ufma.servico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.ufma.excecao.EmailJaCadastradoException;
import br.ufma.excecao.UsuarioInativoException;
import br.ufma.model.entidades.Discente;
import br.ufma.model.entidades.Docente;
import br.ufma.model.entidades.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(UsuarioServiceJpa.class)
class UsuarioServiceJpaTest {

    @Autowired UsuarioServiceJpa service;

    @Test
    void cadastraEPersiste() {
        service.cadastrarUsuario(new Discente("Ana", "1", "ana@ufma.br", "s"));
        assertNotNull(service.autenticar("ana@ufma.br", "s"));
    }

    @Test
    void cadastroComEmailDuplicadoFalha() {
        service.cadastrarUsuario(new Discente("Ana", "1", "ana@ufma.br", "s"));
        assertThrows(EmailJaCadastradoException.class, () ->
            service.cadastrarUsuario(new Docente("Outro", "2", "ana@ufma.br", "x")));
    }

    @Test
    void autenticaCorreto() {
        service.cadastrarUsuario(new Discente("Ana", "1", "ana@ufma.br", "segredo"));
        Usuario u = service.autenticar("ana@ufma.br", "segredo");
        assertNotNull(u);
        assertEquals("ana@ufma.br", u.getEmail());
    }

    @Test
    void autenticaSenhaErradaRetornaNull() {
        service.cadastrarUsuario(new Discente("Ana", "1", "ana@ufma.br", "segredo"));
        assertNull(service.autenticar("ana@ufma.br", "errada"));
    }

    @Test
    void autenticaInativoLancaExcecao() {
        service.cadastrarUsuario(new Discente("Ana", "1", "ana@ufma.br", "s"));
        service.desativarUsuario("ana@ufma.br");
        assertThrows(UsuarioInativoException.class, () ->
            service.autenticar("ana@ufma.br", "s"));
    }

    @Test
    void desativaPersiste() {
        service.cadastrarUsuario(new Discente("Ana", "1", "ana@ufma.br", "s"));
        service.desativarUsuario("ana@ufma.br");
        assertFalse(service.listarTodos().get(0).isAtivo());
    }

    @Test
    void listaSeparaSubtipos() {
        service.cadastrarUsuario(new Discente("D", "1", "d@ufma.br", "s"));
        service.cadastrarUsuario(new Docente("Doc", "2", "doc@ufma.br", "s"));
        assertEquals(1, service.listarDiscentes().size());
        assertEquals(1, service.listarDocentes().size());
    }

    @Test
    void listaAtivosExcluiInativos() {
        service.cadastrarUsuario(new Discente("D1", "1", "d1@ufma.br", "s"));
        service.cadastrarUsuario(new Discente("D2", "2", "d2@ufma.br", "s"));
        service.desativarUsuario("d1@ufma.br");
        assertEquals(1, service.listarDiscentesAtivos().size());
    }
}
