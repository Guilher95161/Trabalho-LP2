package entidades;

import java.util.ArrayList;
import java.util.List;

public class GrupoEstudantil {
    private static int contador = 1;

    private int id;
    private String nome;
    private Docente responsavel;
    private List<Discente> membros;
    // cargo do membro é representado como texto paralelo
    private List<String> cargos;

    public GrupoEstudantil(String nome, Docente responsavel) {
        this.id = contador++;
        this.nome = nome;
        this.responsavel = responsavel;
        this.membros = new ArrayList<>();
        this.cargos = new ArrayList<>();
    }

    public int getId(){
        return id;
    }
    public String getNome(){
        return nome;
    }
    public Docente getResponsavel(){
        return responsavel;
    }
    public List<Discente> getMembros(){
        return membros;
    }

    public void adicionarMembro(Discente d) {
        if (!membros.contains(d)) {
            membros.add(d);
            cargos.add("MEMBRO");
        }
    }

    public void removerMembro(Discente d) {
        int idx = membros.indexOf(d);
        if (idx >= 0) {
            membros.remove(idx);
            cargos.remove(idx);
        }
    }

    public void definirCargo(Discente d, String cargo) {
        int idx = membros.indexOf(d);
        if (idx >= 0) {
            cargos.set(idx, cargo.toUpperCase());
        }
    }

    public List<String> getCargos(){
        return cargos;
    }

    public String getCargo(Discente d) {
        int idx = membros.indexOf(d);
        if (idx >= 0)
            return cargos.get(idx);
        return "NAO_MEMBRO";
    }

    @Override
    public String toString() {
        return "[" + id + "] " + nome + " | Resp: " + responsavel.getNome() + " | Membros: " + membros.size();
    }
}