package repositorio;

import entidades.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Simula um banco de dados em memória.
 * Responsabilidade única: armazenar e fornecer acesso às entidades do sistema.
 * Não contém nenhuma regra de negócio.
 */
public class RepositorioCentral {

    private List<Usuario> usuarios = new ArrayList<>();
    private List<Oportunidade> oportunidades = new ArrayList<>();
    private List<SolicitacaoAproveitamento> solicitacoes = new ArrayList<>();
    private List<GrupoEstudantil> grupos = new ArrayList<>();


    // -------------------------------------------------------------------------
    // Usuários
    // -------------------------------------------------------------------------

    public void salvarUsuario(Usuario u) {
        usuarios.add(u);
    }

    public List<Usuario> findAllUsuarios() {
        return new ArrayList<>(usuarios);
    }

    public Optional<Usuario> findUsuarioByEmail(String email) {
        return usuarios.stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst();
    }

    public Optional<Usuario> findUsuarioByEmailESenha(String email, String senha) {
        return usuarios.stream()
                .filter(u -> u.getEmail().equals(email) && u.getSenha().equals(senha))
                .findFirst();
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

    // -------------------------------------------------------------------------
    // Oportunidades
    // -------------------------------------------------------------------------

    public void salvarOportunidade(Oportunidade o) {
        oportunidades.add(o);
    }

    public List<Oportunidade> findAllOportunidades() {
        return new ArrayList<>(oportunidades);
    }

    public Optional<Oportunidade> findOportunidadeById(int id) {
        return oportunidades.stream()
                .filter(o -> o.getId() == id)
                .findFirst();
    }

    // -------------------------------------------------------------------------
    // Solicitações de Aproveitamento
    // -------------------------------------------------------------------------

    public void salvarSolicitacao(SolicitacaoAproveitamento s) {
        solicitacoes.add(s);
    }

    public List<SolicitacaoAproveitamento> findAllSolicitacoes() {
        return new ArrayList<>(solicitacoes);
    }

    public Optional<SolicitacaoAproveitamento> findSolicitacaoById(int id) {
        return solicitacoes.stream()
                .filter(s -> s.getId() == id)
                .findFirst();
    }

    public List<SolicitacaoAproveitamento> findSolicitacoesPendentes() {
        List<SolicitacaoAproveitamento> lista = new ArrayList<>();
        for (SolicitacaoAproveitamento s : solicitacoes) {
            if ("PENDENTE".equals(s.getStatus())) lista.add(s);
        }
        return lista;
    }

    // -------------------------------------------------------------------------
    // Grupos Estudantis
    // -------------------------------------------------------------------------

    public void salvarGrupo(GrupoEstudantil g) {
        grupos.add(g);
    }

    public List<GrupoEstudantil> findAllGrupos() {
        return new ArrayList<>(grupos);
    }

    public Optional<GrupoEstudantil> findGrupoById(int id) {
        return grupos.stream()
                .filter(g -> g.getId() == id)
                .findFirst();
    }
}