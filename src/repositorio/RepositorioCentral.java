package repositorio;

import entidades.*;
import entidades.enums.StatusSolicitacao;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Persistencia em memoria. Usa LinkedHashMap em vez de ArrayList para que
// as buscas por chave sejam O(1) e a ordem de cadastro seja preservada.
public class RepositorioCentral {

    private Map<String,  Usuario>                    usuarios            = new LinkedHashMap<>();
    private Map<Integer, Oportunidade>               oportunidades       = new LinkedHashMap<>();
    private Map<Integer, SolicitacaoAproveitamento>  solicitacoes        = new LinkedHashMap<>();
    private Map<Integer, GrupoEstudantil>            grupos              = new LinkedHashMap<>();
    private Map<Integer, SolicitacaoGrupoEstudantil> solicitacoesGrupos  = new LinkedHashMap<>();
    private Map<String,  Curso>                      cursos              = new LinkedHashMap<>();

    // ==== Usuarios ====

    public void salvarUsuario(Usuario u) {
        usuarios.put(u.getEmail(), u);
    }

    public List<Usuario> findAllUsuarios() {
        return new ArrayList<>(usuarios.values());
    }

    public Usuario findUsuarioByEmail(String email) {
        return usuarios.get(email);
    }

    public Usuario findUsuarioByEmailESenha(String email, String senha) {
        Usuario u = usuarios.get(email);
        if (u != null && u.getSenha().equals(senha)) return u;
        return null;
    }

    public List<Discente> findAllDiscentes() {
        List<Discente> lista = new ArrayList<>();
        for (Usuario u : usuarios.values()) {
            if (u instanceof Discente) lista.add((Discente) u);
        }
        return lista;
    }

    public List<Docente> findAllDocentes() {
        List<Docente> lista = new ArrayList<>();
        for (Usuario u : usuarios.values()) {
            if (u instanceof Docente) lista.add((Docente) u);
        }
        return lista;
    }

    // ==== Oportunidades ====

    public void salvarOportunidade(Oportunidade o) {
        oportunidades.put(o.getId(), o);
    }

    public List<Oportunidade> findAllOportunidades() {
        return new ArrayList<>(oportunidades.values());
    }

    public Oportunidade findOportunidadeById(int id) {
        return oportunidades.get(id);
    }

    // ==== Solicitacoes de Aproveitamento ====

    public void salvarSolicitacao(SolicitacaoAproveitamento s) {
        solicitacoes.put(s.getId(), s);
    }

    public List<SolicitacaoAproveitamento> findAllSolicitacoes() {
        return new ArrayList<>(solicitacoes.values());
    }

    public SolicitacaoAproveitamento findSolicitacaoById(int id) {
        return solicitacoes.get(id);
    }

    public List<SolicitacaoAproveitamento> findSolicitacoesPendentes() {
        List<SolicitacaoAproveitamento> lista = new ArrayList<>();
        for (SolicitacaoAproveitamento s : solicitacoes.values()) {
            if (s.getStatus() == StatusSolicitacao.PENDENTE) lista.add(s);
        }
        return lista;
    }

    // ==== Grupos Estudantis ====

    public void salvarGrupo(GrupoEstudantil g) {
        grupos.put(g.getId(), g);
    }

    public List<GrupoEstudantil> findAllGrupos() {
        return new ArrayList<>(grupos.values());
    }

    public List<GrupoEstudantil> findGrupoByUsuario(Usuario u) {
        List<GrupoEstudantil> lista = new ArrayList<>();
        for (GrupoEstudantil g : grupos.values()) {
            if (u.equals(g.getResponsavel())) lista.add(g);
        }
        return lista;
    }

    public GrupoEstudantil findGrupoById(int id) {
        return grupos.get(id);
    }

    // ==== Solicitacoes de Grupo ====

    public void salvarSolicitacaoGrupo(SolicitacaoGrupoEstudantil s) {
        solicitacoesGrupos.put(s.getId(), s);
    }

    public List<SolicitacaoGrupoEstudantil> findAllSolicitacoesGrupo() {
        return new ArrayList<>(solicitacoesGrupos.values());
    }

    public SolicitacaoGrupoEstudantil findSolicitacaoGrupoById(int id) {
        return solicitacoesGrupos.get(id);
    }

    // ==== Cursos (PPC) ====

    public void salvarCurso(Curso c) {
        cursos.put(c.getCodigo().toUpperCase(), c);
    }

    public List<Curso> findAllCursos() {
        return new ArrayList<>(cursos.values());
    }

    public Curso findCursoById(int id) {
        for (Curso c : cursos.values()) {
            if (c.getId() == id) return c;
        }
        return null;
    }

    public Curso findCursoByCodigo(String codigo) {
        if (codigo == null) return null;
        return cursos.get(codigo.toUpperCase());
    }

    public boolean removerCurso(int id) {
        Curso c = findCursoById(id);
        if (c == null) return false;
        return cursos.remove(c.getCodigo().toUpperCase()) != null;
    }
}
