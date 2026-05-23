package repositorio;

import entidades.*;
import entidades.enums.StatusSolicitacao;
import java.util.ArrayList;
import java.util.List;

public class RepositorioCentral {

    private List<Usuario> usuarios = new ArrayList<>();
    private List<Oportunidade> oportunidades = new ArrayList<>();
    private List<SolicitacaoAproveitamento> solicitacoes = new ArrayList<>();
    private List<GrupoEstudantil> grupos = new ArrayList<>();
    private List<SolicitacaoGrupoEstudantil> solicitacoesGrupos = new ArrayList<>();
    private List<Curso> cursos = new ArrayList<>();
    // Usuários

    public void salvarUsuario(Usuario u) {
        usuarios.add(u);
    }

    public List<Usuario> findAllUsuarios() {
        return new ArrayList<>(usuarios);
    }

    public Usuario findUsuarioByEmail(String email) {
        for (Usuario u : usuarios){
            if (u.getEmail().equals(email)){
                return u;
            }
        }
        return null;
    }

    public Usuario findUsuarioByEmailESenha(String email, String senha) {
        for (Usuario u : usuarios){
            if (u.getEmail().equals(email) && u.getSenha().equals(senha)){
                return u;
            }
        }
        return null;
    }

    public List<Discente> findAllDiscentes() {
        List<Discente> lista = new ArrayList<>();
        for (Usuario u : usuarios) {
            if (u instanceof Discente) lista.add((Discente) u);
        }
        return lista;
    }

    public List<Docente> findAllDocentes() {
        List<Docente> lista = new ArrayList<>();
        for (Usuario u : usuarios) {
            if (u instanceof Docente)
                lista.add((Docente) u);
        }
        return lista;
    }

    // Oportunidades

    public void salvarOportunidade(Oportunidade o) {
        oportunidades.add(o);
    }

    public List<Oportunidade> findAllOportunidades() {
        return new ArrayList<>(oportunidades);
    }

    public Oportunidade findOportunidadeById(int id) {
        for (Oportunidade o : oportunidades) {
            if (o.getId() == id)
                return o;
        }
        return null;
    }

    // Solicitações de Aproveitamento

    public void salvarSolicitacao(SolicitacaoAproveitamento s) {
        solicitacoes.add(s);
    }

    public List<SolicitacaoAproveitamento> findAllSolicitacoes() {
        return new ArrayList<>(solicitacoes);
    }

    public SolicitacaoAproveitamento findSolicitacaoById(int id) {
        for(SolicitacaoAproveitamento solicitacao : solicitacoes){
            if (solicitacao.getId() == id){
                return solicitacao;
            }
        }
        return null;
    }

    public List<SolicitacaoAproveitamento> findSolicitacoesPendentes() {
        List<SolicitacaoAproveitamento> lista = new ArrayList<>();
        for (SolicitacaoAproveitamento s : solicitacoes) {
            if (s.getStatus() == StatusSolicitacao.PENDENTE)
                lista.add(s);
        }
        return lista;
    }

    // Grupos Estudantis

    public void salvarGrupo(GrupoEstudantil g) {
        grupos.add(g);
    }

    public List<GrupoEstudantil> findAllGrupos() {
        return new ArrayList<>(grupos);
    }

    public List<GrupoEstudantil> findGrupoByUsuario(Usuario u){
        List<GrupoEstudantil> lista = new ArrayList<>();
        for (GrupoEstudantil g : grupos){
            if(u.equals(g.getResponsavel())){
                lista.add(g);
            }
        }
        return lista;
    }

    public GrupoEstudantil findGrupoById(int id) {
        for (GrupoEstudantil g : grupos){
            if (g.getId() == id){
                return g;
            }
        }
        return null;
    }

    public void salvarSolicitacaoGrupo(SolicitacaoGrupoEstudantil s) {
        solicitacoesGrupos.add(s);
    }

    public List<SolicitacaoGrupoEstudantil> findAllSolicitacoesGrupo() {
        return new ArrayList<>(solicitacoesGrupos);
    }

    public SolicitacaoGrupoEstudantil findSolicitacaoGrupoById(int id) {
        for (SolicitacaoGrupoEstudantil solicitacaoGrupo : solicitacoesGrupos) {
            if (solicitacaoGrupo.getId() == id){
                return solicitacaoGrupo;
            }
        }
        return null;
    }

    // Cursos (PPC)

    public void salvarCurso(Curso c) {
        cursos.add(c);
    }

    public List<Curso> findAllCursos() {
        return new ArrayList<>(cursos);
    }

    public Curso findCursoById(int id) {
        for (Curso c : cursos) {
            if (c.getId() == id) return c;
        }
        return null;
    }

    public Curso findCursoByCodigo(String codigo) {
        for (Curso c : cursos) {
            if (c.getCodigo().equalsIgnoreCase(codigo)) return c;
        }
        return null;
    }

    public boolean removerCurso(int id) {
        Curso c = findCursoById(id);
        if (c == null) return false;
        return cursos.remove(c);
    }
}