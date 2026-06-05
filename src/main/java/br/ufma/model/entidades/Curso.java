package br.ufma.model.entidades;

import java.util.ArrayList;
import java.util.List;

// carga horaria de extensao fica no PPC, nao aqui, pq muda a cada versao
public class Curso {
    private static int contador = 1;

    private int id;
    private String codigo;
    private String nome;
    private List<Ppc> versoesPpc;

    public Curso(String codigo, String nome) {
        this.id = contador++;
        this.codigo = codigo;
        this.nome = nome;
        this.versoesPpc = new ArrayList<>();
    }

    public int getId()              { return id; }
    public String getCodigo()       { return codigo; }
    public String getNome()         { return nome; }
    public List<Ppc> getVersoesPpc(){ return versoesPpc; }

    public void setNome(String nome)     { this.nome = nome; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    // Vira a versao vigente; a antiga fica como historica.
    public void adicionarVersaoPpc(Ppc novaVersao) {
        for (Ppc p : versoesPpc) {
            if (p.isAtiva()) p.desativar();
        }
        versoesPpc.add(novaVersao);
    }

    public Ppc getPpcAtual() {
        for (int i = versoesPpc.size() - 1; i >= 0; i--) {
            Ppc p = versoesPpc.get(i);
            if (p.isAtiva()) return p;
        }
        return null;
    }

    public Ppc buscarPpcPorAno(String anoVigencia) {
        for (Ppc p : versoesPpc) {
            if (p.getAnoVigencia().equalsIgnoreCase(anoVigencia)) return p;
        }
        return null;
    }

    @Override
    public String toString() {
        Ppc atual = getPpcAtual();
        String pppAtualInfo = (atual != null)
            ? " | PPC atual: " + atual.getAnoVigencia() + " (" + atual.getHorasExtensaoNecessarias() + "h)"
            : " | Sem PPC cadastrado";
        return "[" + id + "] " + codigo + " - " + nome
             + " | Versoes PPC: " + versoesPpc.size()
             + pppAtualInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Curso)) return false;
        Curso outro = (Curso) o;
        return codigo != null && codigo.equals(outro.codigo);
    }

    @Override
    public int hashCode() {
        return codigo == null ? 0 : codigo.hashCode();
    }
}
