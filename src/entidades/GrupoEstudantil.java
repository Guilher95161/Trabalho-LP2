package entidades;

import entidades.enums.CargoGrupo;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GrupoEstudantil {
    private static int contador = 1;

    private int id;
    private String nome;
    private String descricao;
    private Docente responsavel;

    // membro + cargo num so lugar pra nao desincronizar
    private Map<Discente, CargoGrupo> membrosECargos;

    private List<HistoricoCargo> historicoCargos;

    public GrupoEstudantil(String nome, String descricao, Docente responsavel) {
        this.id = contador++;
        this.nome = nome;
        this.descricao = descricao;
        this.responsavel = responsavel;
        this.membrosECargos = new LinkedHashMap<>();
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
        return new ArrayList<>(membrosECargos.keySet());
    }

    public List<CargoGrupo> getCargos() {
        return new ArrayList<>(membrosECargos.values());
    }

    public boolean isMembro(Discente d) {
        return membrosECargos.containsKey(d);
    }

    public void adicionarMembro(Discente d) {
        if (!membrosECargos.containsKey(d)) {
            membrosECargos.put(d, CargoGrupo.MEMBRO);
            historicoCargos.add(new HistoricoCargo(d, CargoGrupo.MEMBRO, hoje()));
        }
    }

    public void removerMembro(Discente d) {
        if (membrosECargos.containsKey(d)) {
            encerrarEntradaAtual(d);
            membrosECargos.remove(d);
        }
    }

    public void definirCargo(Discente d, CargoGrupo cargo) {
        if (membrosECargos.containsKey(d)) {
            encerrarEntradaAtual(d);
            membrosECargos.put(d, cargo);
            historicoCargos.add(new HistoricoCargo(d, cargo, hoje()));
        }
    }

    public CargoGrupo getCargo(Discente d) {
        return membrosECargos.get(d);
    }

    public List<HistoricoCargo> getHistoricoDeDiscente(Discente d) {
        List<HistoricoCargo> resultado = new ArrayList<>();
        for (HistoricoCargo h : historicoCargos) {
            if (h.getDiscente().equals(d)) resultado.add(h);
        }
        return resultado;
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

    private String hoje() {
        return LocalDate.now().toString();
    }

    @Override
    public String toString() {
        String desc = (descricao != null && !descricao.isEmpty()) ? " | " + descricao : "";
        return "[" + id + "] " + nome + desc + " | Resp: " + responsavel.getNome() + " | Membros: " + membrosECargos.size();
    }
}
