package br.ufma.model.entidades;

import br.ufma.model.entidades.enums.CargoGrupo;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GrupoEstudantil {
    private static int contador = 1;

    private int id;
    private String nome;
    private String descricao;
    private Docente responsavel;

    // membro + cargo num so lugar pra nao desincronizar
    private List<MembroGrupo> membros;

    private List<HistoricoCargo> historicoCargos;

    public GrupoEstudantil(String nome, String descricao, Docente responsavel) {
        this.id = contador++;
        this.nome = nome;
        this.descricao = descricao;
        this.responsavel = responsavel;
        this.membros = new ArrayList<>();
        this.historicoCargos = new ArrayList<>();
    }

    public GrupoEstudantil(String nome, Docente responsavel) {
        this(nome, "", responsavel);
    }

    public int getId()              { return id; }
    public String getNome()         { return nome; }
    public String getDescricao()    { return descricao; }
    public Docente getResponsavel() { return responsavel; }
    public List<HistoricoCargo> getHistoricoCargos() { return historicoCargos; }

    public void setDescricao(String descricao) { this.descricao = descricao; }

    public List<Discente> getMembros() {
        List<Discente> lista = new ArrayList<>();
        for (MembroGrupo m : membros) lista.add(m.getDiscente());
        return lista;
    }

    public List<CargoGrupo> getCargos() {
        List<CargoGrupo> lista = new ArrayList<>();
        for (MembroGrupo m : membros) lista.add(m.getCargo());
        return lista;
    }

    public boolean isMembro(Discente d) {
        return findMembro(d) != null;
    }

    public void adicionarMembro(Discente d) {
        if (findMembro(d) == null) {
            LocalDate agora = hoje();
            membros.add(new MembroGrupo(d, CargoGrupo.MEMBRO, agora));
            historicoCargos.add(new HistoricoCargo(d, CargoGrupo.MEMBRO, agora));
        }
    }

    public void removerMembro(Discente d) {
        MembroGrupo m = findMembro(d);
        if (m != null) {
            encerrarEntradaAtual(d);
            membros.remove(m);
        }
    }

    public void definirCargo(Discente d, CargoGrupo cargo) {
        MembroGrupo m = findMembro(d);
        if (m != null) {
            encerrarEntradaAtual(d);
            m.setCargo(cargo);
            historicoCargos.add(new HistoricoCargo(d, cargo, hoje()));
        }
    }

    public CargoGrupo getCargo(Discente d) {
        MembroGrupo m = findMembro(d);
        return (m != null) ? m.getCargo() : null;
    }

    public List<HistoricoCargo> getHistoricoDeDiscente(Discente d) {
        List<HistoricoCargo> resultado = new ArrayList<>();
        for (HistoricoCargo h : historicoCargos) {
            if (h.getDiscente().equals(d)) resultado.add(h);
        }
        return resultado;
    }

    private MembroGrupo findMembro(Discente d) {
        for (MembroGrupo m : membros) {
            if (m.getDiscente().equals(d)) return m;
        }
        return null;
    }

    // fecha o cargo atual do discente antes de mudar
    private void encerrarEntradaAtual(Discente d) {
        for (int i = historicoCargos.size() - 1; i >= 0; i--) {
            HistoricoCargo h = historicoCargos.get(i);
            if (h.getDiscente().equals(d) && h.isAtual()) {
                h.encerrar(hoje());
                break;
            }
        }
    }

    private LocalDate hoje() {
        return LocalDate.now();
    }

    @Override
    public String toString() {
        String desc = (descricao != null && !descricao.isEmpty()) ? " | " + descricao : "";
        return "[" + id + "] " + nome + desc + " | Resp: " + responsavel.getNome() + " | Membros: " + membros.size();
    }
}
