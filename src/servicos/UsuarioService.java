package servicos;

import entidades.*;
import repositorio.RepositorioCentral;

import java.util.List;
import java.util.Optional;

/**
 * Contém todas as regras de negócio relacionadas à gestão de usuários.
 */
public class UsuarioService {

    private final RepositorioCentral repositorio;

    public UsuarioService(RepositorioCentral repositorio) {
        this.repositorio = repositorio;
        popularDadosIniciais();
    }

    /**
     * Registra os usuários pré-cadastrados no sistema.
     */
    private void popularDadosIniciais() {
        repositorio.salvarUsuario(new Administrador("Administrador", "0000", "admin@ufma.br", "admin123"));
        repositorio.salvarUsuario(new Discente("João",   "0001", "aluno1@ufma.br", "aluno123"));
        repositorio.salvarUsuario(new Discente("Maria",  "0002", "aluno2@ufma.br", "aluno123"));
        repositorio.salvarUsuario(new Gestor("Josefina", "0003", "coord1@ufma.br", "coord123"));
        repositorio.salvarUsuario(new Docente("Joselio", "0004", "doc@ufma.br",    "doc123"));
    }

    /**
     * Cadastra um novo usuário, garantindo unicidade de e-mail.
     *
     * @return true se o cadastro foi bem-sucedido; false se o e-mail já existe.
     */
    public boolean cadastrarUsuario(Usuario u) {
        Optional<Usuario> existente = repositorio.findUsuarioByEmail(u.getEmail());
        if (existente.isPresent()) {
            return false;
        }
        repositorio.salvarUsuario(u);
        return true;
    }

    /**
     * Autentica um usuário pelo par e-mail/senha.
     *
     * @return o {@link Usuario} autenticado, ou {@code null} se as credenciais forem inválidas.
     */
    public Usuario autenticar(String email, String senha) {
        return repositorio.findUsuarioByEmailESenha(email, senha).orElse(null);
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

    /**
     * Busca um {@link Docente} pelo índice na lista de docentes.
     *
     * @return o Docente encontrado, ou {@code null} se o índice for inválido.
     */
    public Docente buscarDocentePorIndice(int idx) {
        List<Docente> docentes = listarDocentes();
        if (idx >= 0 && idx < docentes.size()) {
            return docentes.get(idx);
        }
        return null;
    }

    /**
     * Busca um {@link Discente} pelo índice na lista de discentes.
     *
     * @return o Discente encontrado, ou {@code null} se o índice for inválido.
     */
    public Discente buscarDiscentePorIndice(int idx) {
        List<Discente> discentes = listarDiscentes();
        if (idx >= 0 && idx < discentes.size()) {
            return discentes.get(idx);
        }
        return null;
    }
}