package servicos;

import entidades.*;
import repositorio.RepositorioCentral;

import java.util.List;
import java.util.Optional;

//Contém todas as regras de negócio relacionadas à gestão de usuários.

public class UsuarioService {

    private final RepositorioCentral repositorio;

    public UsuarioService(RepositorioCentral repositorio) {
        this.repositorio = repositorio;
        popularDadosIniciais();
    }

//Registra os usuários pré-cadastrados no sistema.

    private void popularDadosIniciais() {
        repositorio.salvarUsuario(new Administrador("Administrador", "0000", "admin@ufma.br", "admin123"));
        repositorio.salvarUsuario(new Discente("João",   "0001", "aluno1@ufma.br", "aluno123"));
        repositorio.salvarUsuario(new Discente("Maria",  "0002", "aluno2@ufma.br", "aluno123"));
        repositorio.salvarUsuario(new Gestor("Josefina", "0003", "coord1@ufma.br", "coord123"));
        repositorio.salvarUsuario(new Docente("Josélio", "0004", "doc@ufma.br",    "doc123"));
    }

    public boolean cadastrarUsuario(Usuario u) {
        Usuario existente = repositorio.findUsuarioByEmail(u.getEmail());
        if (existente!=null) {
            return false;
        }
        repositorio.salvarUsuario(u);
        return true;
    }

    public Usuario autenticar(String email, String senha) {
        return repositorio.findUsuarioByEmailESenha(email, senha);
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
}