package servicos;

import entidades.*;
import excecoes.EmailJaCadastradoException;
import excecoes.UsuarioInativoException;
import java.util.ArrayList;
import java.util.List;
import repositorio.RepositorioCentral;

public class UsuarioService {

    private final RepositorioCentral repositorio;
    private final CursoService cursoService;

    public UsuarioService(RepositorioCentral repositorio, CursoService cursoService) {
        this.repositorio = repositorio;
        this.cursoService = cursoService;
        popularDadosIniciais();
    }

    private void popularDadosIniciais() {
        Curso cursoPadrao = cursoService.listarTodos().isEmpty() ? null : cursoService.listarTodos().get(0);
        Ppc ppcPadrao = (cursoPadrao != null) ? cursoPadrao.getPpcAtual() : null;
        repositorio.salvarUsuario(new Administrador("Administrador", "0000", "admin@ufma.br", "admin123"));
        repositorio.salvarUsuario(new Discente("João",   "0001", "aluno1@ufma.br", "aluno123", ppcPadrao));
        repositorio.salvarUsuario(new Discente("Maria",  "0002", "aluno2@ufma.br", "aluno123", ppcPadrao));
        repositorio.salvarUsuario(new Coordenador("Josefina", "0003", "coord1@ufma.br", "coord123"));
        repositorio.salvarUsuario(new Comissao("Carlos", "0005", "comissao1@ufma.br", "com123"));
        repositorio.salvarUsuario(new Docente("Josélio", "0004", "doc@ufma.br",    "doc123"));
    }

    public void cadastrarUsuario(Usuario u) {
        if (repositorio.findUsuarioByEmail(u.getEmail()) != null) {
            throw new EmailJaCadastradoException(u.getEmail());
        }
        repositorio.salvarUsuario(u);
    }

    public Usuario autenticar(String email, String senha) {
        Usuario u = repositorio.findUsuarioByEmailESenha(email, senha);
        if (u != null && !u.isAtivo()) {
            throw new UsuarioInativoException(email);
        }
        return u;
    }

    public void desativarUsuario(String email){
        Usuario u =  repositorio.findUsuarioByEmail(email);
        if (u != null){
            u.desativarConta();
        }
    }

    public List<Usuario> listarTodos() {
        return repositorio.findAllUsuarios();
    }

    public List<Discente> listarDiscentes() {
        return repositorio.findAllDiscentes();
    }

    public List<Docente> listarDocentes() {
        return repositorio.findAllDocentes();
    }

    // so os ativos - inativo nao aparece nas selecoes
    public List<Discente> listarDiscentesAtivos() {
        List<Discente> ativos = new ArrayList<>();
        for (Discente d : repositorio.findAllDiscentes()) {
            if (d.isAtivo()) ativos.add(d);
        }
        return ativos;
    }

    public List<Docente> listarDocentesAtivos() {
        List<Docente> ativos = new ArrayList<>();
        for (Docente d : repositorio.findAllDocentes()) {
            if (d.isAtivo()) ativos.add(d);
        }
        return ativos;
    }

    public Docente buscarDocentePorIndice(int idx) {
        List<Docente> docentes = listarDocentes();
        if (idx >= 0 && idx < docentes.size()) {
            return docentes.get(idx);
        }
        return null;
    }

    public Discente buscarDiscentePorIndice(int idx) {
        List<Discente> discentes = listarDiscentes();
        if (idx >= 0 && idx < discentes.size()) {
            return discentes.get(idx);
        }
        return null;
    }

    public Docente buscarDocenteAtivoPorIndice(int idx) {
        List<Docente> docentes = listarDocentesAtivos();
        if (idx >= 0 && idx < docentes.size()) return docentes.get(idx);
        return null;
    }

    public Discente buscarDiscenteAtivoPorIndice(int idx) {
        List<Discente> discentes = listarDiscentesAtivos();
        if (idx >= 0 && idx < discentes.size()) return discentes.get(idx);
        return null;
    }
}