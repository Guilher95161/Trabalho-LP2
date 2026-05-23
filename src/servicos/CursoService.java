package servicos;

import entidades.Curso;
import entidades.Ppc;
import entidades.UnidadeCurricular;
import entidades.Usuario;
import repositorio.RepositorioCentral;

import java.util.List;

/**
 * RF007/RF008/RF0009 - servico para gerenciar Cursos, versoes do PPC e UCEs.
 *
 * Regras:
 * - Atualizar carga horaria do PPC = criar NOVA versao (preserva historico).
 * - Cada versao do PPC tem suas proprias UCEs.
 * - Multiplos PPCs coexistem (turmas 2020 e 2025 do mesmo curso).
 */
public class CursoService {

    public static final int CARGA_PADRAO = 310;

    private final RepositorioCentral repositorio;

    public CursoService(RepositorioCentral repositorio) {
        this.repositorio = repositorio;
        popularDadosIniciais();
    }

    private void popularDadosIniciais() {
        Curso cc = new Curso("CC", "Ciencia da Computacao");
        // Versao inicial do PPC (sem autor - cadastro do sistema)
        Ppc ppc2020 = new Ppc(cc, "2020", 310, null);
        ppc2020.adicionarUce(new UnidadeCurricular("EXT0001", "Atividades de Extensao I", 60));
        ppc2020.adicionarUce(new UnidadeCurricular("EXT0002", "Atividades de Extensao II", 60));
        cc.adicionarVersaoPpc(ppc2020);
        repositorio.salvarCurso(cc);
    }

    // ==== Curso ====
    public boolean cadastrarCurso(Curso c) {
        if (repositorio.findCursoByCodigo(c.getCodigo()) != null) return false;
        repositorio.salvarCurso(c);
        return true;
    }

    public List<Curso> listarTodos() {
        return repositorio.findAllCursos();
    }

    public Curso buscarPorId(int id) {
        return repositorio.findCursoById(id);
    }

    public Curso buscarPorIndice(int idx) {
        List<Curso> cursos = listarTodos();
        if (idx >= 0 && idx < cursos.size()) return cursos.get(idx);
        return null;
    }

    public boolean atualizarNome(int id, String novoNome) {
        Curso c = repositorio.findCursoById(id);
        if (c == null || novoNome == null || novoNome.trim().isEmpty()) return false;
        c.setNome(novoNome);
        return true;
    }

    public boolean removerCurso(int id) {
        return repositorio.removerCurso(id);
    }

    // ==== PPC (RF007 + RF008) ====

    /**
     * Adiciona uma nova versao do PPC ao curso. Desativa a versao anterior
     * (mas preserva no historico).
     */
    public boolean cadastrarNovaVersaoPpc(int cursoId, String anoVigencia, int horas, Usuario autor) {
        Curso c = repositorio.findCursoById(cursoId);
        if (c == null || horas <= 0) return false;
        if (c.buscarPpcPorAno(anoVigencia) != null) return false; // duplicidade
        Ppc nova = new Ppc(c, anoVigencia, horas, autor);
        c.adicionarVersaoPpc(nova);
        return true;
    }

    public Ppc buscarPpcAtivo(int cursoId) {
        Curso c = repositorio.findCursoById(cursoId);
        return (c != null) ? c.getPpcAtual() : null;
    }

    public Ppc buscarPpcPorAno(int cursoId, String ano) {
        Curso c = repositorio.findCursoById(cursoId);
        return (c != null) ? c.buscarPpcPorAno(ano) : null;
    }

    public List<Ppc> listarHistoricoPpc(int cursoId) {
        Curso c = repositorio.findCursoById(cursoId);
        return (c != null) ? c.getVersoesPpc() : null;
    }

    // ==== UCE (RF0009) ====

    public boolean cadastrarUce(Ppc ppc, String codigo, String nome, int cargaHoraria) {
        if (ppc == null || cargaHoraria <= 0) return false;
        if (ppc.buscarUcePorCodigo(codigo) != null) return false;
        ppc.adicionarUce(new UnidadeCurricular(codigo, nome, cargaHoraria));
        return true;
    }

    public boolean removerUce(Ppc ppc, String codigo) {
        if (ppc == null) return false;
        UnidadeCurricular u = ppc.buscarUcePorCodigo(codigo);
        if (u == null) return false;
        return ppc.removerUce(u);
    }
}
