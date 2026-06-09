package br.ufma.model.repositorio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.ufma.model.entidades.Discente;
import br.ufma.model.entidades.Docente;
import br.ufma.model.entidades.Usuario;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest
class UsuarioRepositoryTest {

    @Autowired UsuarioRepository usuarioRepository;
    @Autowired DiscenteRepository discenteRepository;
    @Autowired DocenteRepository docenteRepository;

    @Test
    void salvaEBuscaPorEmail() {
        usuarioRepository.save(new Discente("Ana", "100", "ana@ufma.br", "s"));

        Optional<Usuario> achado = usuarioRepository.findByEmail("ana@ufma.br");
        assertTrue(achado.isPresent());
        assertEquals("Ana", achado.get().getNome());
    }

    @Test
    void findByEmailInexistenteRetornaVazio() {
        assertTrue(usuarioRepository.findByEmail("naoexiste@ufma.br").isEmpty());
    }

    @Test
    void herancaJoinedPersisteSubtipo() {
        Usuario salvo = usuarioRepository.save(new Discente("Bia", "101", "bia@ufma.br", "s"));

        Usuario recarregado = usuarioRepository.findById(salvo.getId()).orElseThrow();
        assertInstanceOf(Discente.class, recarregado);
        assertEquals(1, discenteRepository.findAll().size());
    }

    @Test
    void emailEhUnico() {
        usuarioRepository.saveAndFlush(new Discente("C1", "102", "dup@ufma.br", "s"));
        assertThrows(DataIntegrityViolationException.class, () ->
            usuarioRepository.saveAndFlush(new Docente("C2", "103", "dup@ufma.br", "s")));
    }

    @Test
    void subtiposSaoSeparados() {
        usuarioRepository.save(new Discente("D", "104", "disc@ufma.br", "s"));
        usuarioRepository.save(new Docente("Doc", "105", "doc2@ufma.br", "s"));

        assertEquals(1, discenteRepository.findAll().size());
        assertEquals(1, docenteRepository.findAll().size());
        assertEquals(2, usuarioRepository.findAll().size());
    }
}
