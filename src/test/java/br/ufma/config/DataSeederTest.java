package br.ufma.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.ufma.model.repositorio.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DataSeederTest {

    @Autowired UsuarioRepository usuarioRepository;
    @Autowired DataSeeder dataSeeder;

    @Test
    void seedCriaSeisUsuarios() {
        assertEquals(6, usuarioRepository.count());
        assertTrue(usuarioRepository.findByEmail("admin@ufma.br").isPresent());
    }

    @Test
    void seedEhIdempotente() {
        dataSeeder.semear(); // segunda chamada nao deve duplicar
        assertEquals(6, usuarioRepository.count());
    }
}
