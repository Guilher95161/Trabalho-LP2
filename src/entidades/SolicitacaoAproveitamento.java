package entidades;

public class SolicitacaoAproveitamento {
    private static int contador = 1;

    private int id;
    private Discente solicitante;
    private String descricaoCurso;
    private int cargaHorariaPleitada;
    private String status; // "PENDENTE", "DEFERIDA", "INDEFERIDA"
    private String parecer;

    public SolicitacaoAproveitamento(Discente solicitante, String descricaoCurso, int cargaHorariaPleitada) {
        this.id = contador++;
        this.solicitante = solicitante;
        this.descricaoCurso = descricaoCurso;
        this.cargaHorariaPleitada = cargaHorariaPleitada;
        this.status = "PENDENTE";
        this.parecer = "";
    }

    public int getId(){
        return id;
    }
    public Discente getSolicitante(){
        return solicitante;
    }
    public String getDescricaoCurso(){
        return descricaoCurso;
    }
    public int getCargaHorariaPleitada(){
        return cargaHorariaPleitada;
    }
    public String getStatus(){
        return status;
    }
    public String getParecer(){
        return parecer;
    }

    public void setDescricaoCurso(String descricaoCurso){
        this.descricaoCurso = descricaoCurso;
    }
    public void setCargaHorariaPleitada(int cargaHorariaPleitada){
        this.cargaHorariaPleitada = cargaHorariaPleitada;
    }

    public void avaliarSolicitacao(boolean aprovado, String parecer) {
        this.parecer = parecer;
        if (aprovado) {
            this.status = "DEFERIDA";
        } else {
            this.status = "INDEFERIDA";
        }
    }

    public void reenviar() {
        this.status = "PENDENTE";
        this.parecer = "";
    }

    @Override
    public String toString() {
        return "[" + id + "] Solicitante: " + solicitante.getNome() + " | Curso: " + descricaoCurso + " | " + cargaHorariaPleitada + "h | Status: " + status;
    }
}