package br.ufma.config;

import br.ufma.model.entidades.Administrador;
import br.ufma.model.entidades.Comissao;
import br.ufma.model.entidades.Coordenador;
import br.ufma.model.entidades.Discente;
import br.ufma.model.entidades.Docente;
import br.ufma.model.repositorio.UsuarioRepository;
import br.ufma.servico.UsuarioServiceJpa;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Carga inicial dos usuarios de teste no H2 quando o Spring sobe
 * ({@code ExtensaoApplication}). Substitui o popularDadosIniciais() que o
 * UsuarioService legado faz no construtor. O CLI legado (Main) nao inicia o
 * Spring, entao este runner nao roda por la.
 *
 * Idempotente: so semeia se o banco estiver vazio (seguro com ddl-auto=update).
 * Os discentes entram com ppc = null; o seeding de Curso/Ppc fica para um passo
 * posterior do cutover.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UsuarioServiceJpa usuarioService;
    private final UsuarioRepository usuarioRepository;

    public DataSeeder(UsuarioServiceJpa usuarioService, UsuarioRepository usuarioRepository) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void run(String... args) {
        semear();
    }

    /** Semeia os usuarios de teste apenas se o banco estiver vazio. */
    public void semear() {
        if (usuarioRepository.count() > 0) {
            return;
        }
        usuarioService.cadastrarUsuario(new Administrador("Administrador", "0000", "admin@ufma.br", "admin123"));
        usuarioService.cadastrarUsuario(new Discente("João",  "0001", "aluno1@ufma.br", "aluno123"));
        usuarioService.cadastrarUsuario(new Discente("Maria", "0002", "aluno2@ufma.br", "aluno123"));
        usuarioService.cadastrarUsuario(new Coordenador("Josefina", "0003", "coord1@ufma.br", "coord123"));
        usuarioService.cadastrarUsuario(new Comissao("Carlos", "0005", "comissao1@ufma.br", "com123"));
        usuarioService.cadastrarUsuario(new Docente("Josélio", "0004", "doc@ufma.br", "doc123"));
        log.info("seed: {} usuarios criados", usuarioRepository.count());
    }
}
