package br.ufma.servico;

import br.ufma.excecao.EmailJaCadastradoException;
import br.ufma.excecao.UsuarioInativoException;
import br.ufma.model.entidades.Discente;
import br.ufma.model.entidades.Docente;
import br.ufma.model.entidades.Usuario;
import br.ufma.model.repositorio.DiscenteRepository;
import br.ufma.model.repositorio.DocenteRepository;
import br.ufma.model.repositorio.UsuarioRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Versao Spring do UsuarioService, ligada aos JpaRepository (store = H2).
 * Espelho do {@link UsuarioService} legado, que ainda usa o RepositorioCentral
 * em memoria. Classe paralela e temporaria: some no cutover geral dos services.
 */
@Service
public class UsuarioServiceJpa {

    private final UsuarioRepository usuarioRepository;
    private final DiscenteRepository discenteRepository;
    private final DocenteRepository docenteRepository;

    public UsuarioServiceJpa(UsuarioRepository usuarioRepository,
                             DiscenteRepository discenteRepository,
                             DocenteRepository docenteRepository) {
        this.usuarioRepository = usuarioRepository;
        this.discenteRepository = discenteRepository;
        this.docenteRepository = docenteRepository;
    }

    @Transactional
    public Usuario cadastrarUsuario(Usuario u) {
        if (usuarioRepository.findByEmail(u.getEmail()).isPresent()) {
            throw new EmailJaCadastradoException(u.getEmail());
        }
        return usuarioRepository.save(u);
    }

    public Usuario autenticar(String email, String senha) {
        Usuario u = usuarioRepository.findByEmail(email).orElse(null);
        if (u == null || !u.getSenha().equals(senha)) {
            return null;
        }
        if (!u.isAtivo()) {
            throw new UsuarioInativoException(email);
        }
        return u;
    }

    @Transactional
    public void desativarUsuario(String email) {
        usuarioRepository.findByEmail(email).ifPresent(u -> {
            u.desativarConta();
            usuarioRepository.save(u);
        });
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public List<Discente> listarDiscentes() {
        return discenteRepository.findAll();
    }

    public List<Docente> listarDocentes() {
        return docenteRepository.findAll();
    }

    // so os ativos - inativo nao aparece nas selecoes
    public List<Discente> listarDiscentesAtivos() {
        List<Discente> ativos = new ArrayList<>();
        for (Discente d : discenteRepository.findAll()) {
            if (d.isAtivo()) ativos.add(d);
        }
        return ativos;
    }

    public List<Docente> listarDocentesAtivos() {
        List<Docente> ativos = new ArrayList<>();
        for (Docente d : docenteRepository.findAll()) {
            if (d.isAtivo()) ativos.add(d);
        }
        return ativos;
    }
}
