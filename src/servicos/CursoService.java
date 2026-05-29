package servicos;

import entidades.Curso;
import entidades.Ppc;
import entidades.Usuario;
import excecoes.EntidadeNaoEncontradaException;
import excecoes.OperacaoInvalidaException;
import repositorio.RepositorioCentral;

import java.util.List;

// nova versao de PPC cria uma nova entrada; a antiga vira historica
public class CursoService {

    // carga da UCE conforme art. 1 das normas de extensao da UFMA (CC)
    public static final int CARGA_PADRAO = 345;

    private final RepositorioCentral repositorio;

    public CursoService(RepositorioCentral repositorio) {
        this.repositorio = repositorio;
        popularDadosIniciais();
    }

    private void popularDadosIniciais() {
        Curso cc = new Curso("CC", "Ciencia da Computacao");
        // PPC inicial do seed - 345h conforme regulamento
        Ppc ppc2020 = new Ppc(cc, "2020", 345, null);
        cc.adicionarVersaoPpc(ppc2020);
        repositorio.salvarCurso(cc);
    }

    // --- Curso ---
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

    // --- PPC ---

    // cria nova versao e torna vigente; a antiga fica no historico
    public void cadastrarNovaVersaoPpc(int cursoId, String anoVigencia, int horasUce, Usuario autor) {
        Curso c = repositorio.findCursoById(cursoId);
        if (c == null) throw new EntidadeNaoEncontradaException("Curso #" + cursoId + " nao encontrado.");
        if (horasUce <= 0) throw new OperacaoInvalidaException("Carga horaria da UCE deve ser maior que zero.");
        if (c.buscarPpcPorAno(anoVigencia) != null) {
            throw new OperacaoInvalidaException("Ja existe um PPC com o ano '" + anoVigencia + "' para este curso.");
        }
        Ppc nova = new Ppc(c, anoVigencia, horasUce, autor);
        c.adicionarVersaoPpc(nova);
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
}
