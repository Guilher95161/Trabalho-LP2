package servicos;

import entidades.*;
import java.util.ArrayList;
import java.util.List;

public class GerenciadorCentral {

    private List<Usuario> usuarios;
    private List<Oportunidade> oportunidades;
    private List<SolicitacaoAproveitamento> solicitacoes;
    private List<GrupoEstudantil> grupos;

    public GerenciadorCentral() {
        usuarios = new ArrayList<>();
        oportunidades = new ArrayList<>();
        solicitacoes = new ArrayList<>();
        grupos = new ArrayList<>();

        // Administrador pré-cadastrado
        usuarios.add(new Administrador("Administrador", "0000", "admin@ufma.br", "admin123"));
        // Aluno1 pré-cadastrado
        usuarios.add(new Discente("João","0001","aluno1@ufma.br","aluno123"));
        // Aluno2 pré-cadastrado
        usuarios.add(new Discente("Maria","0003","aluno2@ufma.br","aluno123"));
        // Gestor pré-cadastrado
        usuarios.add(new Gestor("Josefina","0003","coord1@ufma.br","coord123"));
        // Docente pré-cadastrado
        usuarios.add(new Docente("Joselio","0004","doc@ufma.br","doc123"));
    }

    // Gestão Usuários

    public boolean cadastrarUsuario(Usuario u) {
        for (Usuario existente : usuarios) {
            if (existente.getEmail().equals(u.getEmail()))
                return false;
        }
        usuarios.add(u);
        return true;
    }

    public Usuario buscarPorEmailSenha(String email, String senha) {
        for (Usuario u : usuarios) {
            if (u.getEmail().equals(email) && u.getSenha().equals(senha))
                return u;
        }
        return null;
    }

    public List<Usuario> listarUsuarios() {
        return usuarios;
    }

    public List<Discente> listarDiscentes() {
        List<Discente> lista = new ArrayList<>();
        for (Usuario u : usuarios) {
            if (u instanceof Discente)
                lista.add((Discente) u);
        }
        return lista;
    }

    public List<Docente> listarDocentes() {
        List<Docente> lista = new ArrayList<>();
        for (Usuario u : usuarios) {
            if (u instanceof Docente)
                lista.add((Docente) u);
        }
        return lista;
    }

    public Docente buscarDocentePorId(int idx) {
        List<Docente> docentes = listarDocentes();
        if (idx >= 0 && idx < docentes.size())
            return docentes.get(idx);
        return null;
    }

    public Discente buscarDiscentePorId(int idx) {
        List<Discente> discentes = listarDiscentes();
        if (idx >= 0 && idx < discentes.size())
            return discentes.get(idx);
        return null;
    }

    // Gestão Oportunidades

    public void criarOportunidade(Oportunidade o) {
        oportunidades.add(o);
    }

    public List<Oportunidade> listarOportunidades() {
        return oportunidades;
    }

    public Oportunidade buscarOportunidadePorId(int id) {
        for (Oportunidade o : oportunidades) {
            if (o.getId() == id)
                return o;
        }
        return null;
    }

    public void inscreverDiscente(int oportunidadeId, Discente d) {
        Oportunidade o = buscarOportunidadePorId(oportunidadeId);
        if (o != null && o.getStatus().equals("ABERTA")) {
            o.inscreverDiscente(d);
        }
    }

    public void cancelarInscricao(int oportunidadeId, Discente d) {
        Oportunidade o = buscarOportunidadePorId(oportunidadeId);
        if (o != null)
            o.cancelarInscricao(d);
    }

    public void encerrarOportunidade(int oportunidadeId) {
        Oportunidade o = buscarOportunidadePorId(oportunidadeId);
        if (o == null)
            return;
        o.encerrar();
        // Certifica todos os inscritos
        String dataHoje = java.time.LocalDate.now().toString();
        for (Discente d : o.getInscritos()) {
            Certificado cert = new Certificado(o.getTitulo(), o.getCargaHoraria(), dataHoje);
            d.adicionarCertificado(cert);
            d.adicionarHoras(o.getCargaHoraria());
        }
    }

    // Gestão Aproveitamentos

    public void criarSolicitacao(SolicitacaoAproveitamento s) {
        solicitacoes.add(s);
    }

    public List<SolicitacaoAproveitamento> listarSolicitacoes() {
        return solicitacoes;
    }

    public List<SolicitacaoAproveitamento> listarSolicitacoesPendentes() {
        List<SolicitacaoAproveitamento> lista = new ArrayList<>();
        for (SolicitacaoAproveitamento s : solicitacoes) {
            if (s.getStatus().equals("PENDENTE")) lista.add(s);
        }
        return lista;
    }

    public SolicitacaoAproveitamento buscarSolicitacaoPorId(int id) {
        for (SolicitacaoAproveitamento s : solicitacoes) {
            if (s.getId() == id) return s;
        }
        return null;
    }

    public void avaliarSolicitacao(SolicitacaoAproveitamento s, boolean aprovado, String parecer) {
        s.avaliarSolicitacao(aprovado, parecer);
        if (aprovado) {
            s.getSolicitante().adicionarHoras(s.getCargaHorariaPleitada());
        }
    }

    // Gestão Grupos Estudantis

    public void criarGrupo(GrupoEstudantil g) {
        grupos.add(g);
    }

    public List<GrupoEstudantil> listarGrupos() {
        return grupos;
    }

    public GrupoEstudantil buscarGrupoPorId(int id) {
        for (GrupoEstudantil g : grupos) {
            if (g.getId() == id)
                return g;
        }
        return null;
    }
}