package entidades;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa o curso de graduacao (RF007).
 *
 * Mantem todas as versoes do PPC (RF008 - historico) e permite vincular discentes
 * a versoes especificas (RF0009 - alunos de PPCs diferentes coexistem).
 *
 * Note: a carga horaria de extensao NAO mora mais aqui. Ela mora em cada Ppc.
 * O Curso e apenas a identidade institucional (codigo + nome).
 */
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

    /**
     * Adiciona uma nova versao do PPC. Desativa a versao anterior (se houver),
     * preservando-a no historico.
     */
    public void adicionarVersaoPpc(Ppc novaVersao) {
        // Desativa a versao atualmente ativa (mantida no historico)
        for (Ppc p : versoesPpc) {
            if (p.isAtiva()) p.desativar();
        }
        versoesPpc.add(novaVersao);
    }

    /**
     * Retorna a versao mais recente do PPC (a unica ativa). null se nao houver.
     */
    public Ppc getPpcAtual() {
        for (int i = versoesPpc.size() - 1; i >= 0; i--) {
            Ppc p = versoesPpc.get(i);
            if (p.isAtiva()) return p;
        }
        return null;
    }

    /**
     * Busca uma versao especifica do PPC (por ano de vigencia).
     */
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
