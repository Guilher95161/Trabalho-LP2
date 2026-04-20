package entidades;

public class Certificado {
    private String tituloAtividade;
    private int cargaHoraria;
    private String data;

    public Certificado(String tituloAtividade, int cargaHoraria, String data) {
        this.tituloAtividade = tituloAtividade;
        this.cargaHoraria = cargaHoraria;
        this.data = data;
    }

    public String getTituloAtividade(){
        return tituloAtividade;
    }
    public int getCargaHoraria(){
        return cargaHoraria;
    }
    public String getData(){
        return data;
    }

    @Override
    public String toString() {
        return "Certificado: " + tituloAtividade + " | " + cargaHoraria + "h | " + data;
    }
}