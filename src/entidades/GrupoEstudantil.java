package entidades;

import entidades.enums.CargoGrupo;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GrupoEstudantil {
    private static int contador = 1;

    private int id;
    private String nome;
    private Docente responsavel;
    private List<Discente> membros;
    private List<CargoGrupo> cargos;
    private List<HistoricoCargo> historicoCargos;

    public GrupoEstudantil(String nome, Docente responsavel) {
        this.id = contador++;
        this.nome = nome;
        this.responsavel = responsavel;
        this.membros = new ArrayList<>();
        this.cargos = new ArrayList<>();
        this.historicoCargos = new ArrayList<>();
    }

    public int getId()              { return id; }
    public String getNome()         { return nome; }
    public Docente getResponsavel() { return responsavel; }
    public List<Discente> getMembros()          { return membros; }
    public List<CargoGrupo> getCargos()         { return cargos; }
    public List<HistoricoCargo> getHistoricoCargos() { return historicoCargos; }

    public boolean isMembro(Discente d) {
        return membros.contains(d);
    }

    public void adicionarMembro(Discente d) {
        if (!membros.contains(d)) {
            membros.add(d);
            cargos.add(CargoGrupo.MEMBRO);
            historicoCargos.add(new HistoricoCargo(d, CargoGrupo.MEMBRO, hoje()));
        }
    }

    public void removerMembro(Discente d) {
        int idx = membros.indexOf(d);
        if (idx >= 0) {
            encerrarEntradaAtual(d);
            membros.remove(idx);
            cargos.remove(idx);
        }
    }

    public void definirCargo(Discente d, CargoGrupo cargo) {
        int idx = membros.indexOf(d);
        if (idx >= 0) {
            encerrarEntradaAtual(d);
            cargos.set(idx, cargo);
            historicoCargos.add(new HistoricoCargo(d, cargo, hoje()));
        }
    }

    // Retorna null se o discente não é membro
    public CargoGrupo getCargo(Discente d) {
        int idx = membros.indexOf(d);
        if (idx >= 0) return cargos.get(idx);
        return null;
    }

    public List<HistoricoCargo> getHistoricoDeDiscente(Discente d) {
        List<HistoricoCargo> resultado = new ArrayList<>();
        for (HistoricoCargo h : historicoCargos) {
            if (h.getDiscente().equals(d)) resultado.add(h);
        }
        return resultado;
    }

    // Fecha a entrada ainda aberta (dataFim == null) para esse discente
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
        return "[" + id + "] " + nome + " | Resp: " + responsavel.getNome() + " | Membros: " + membros.size();
    }
}
