package servicos;

import entidades.*;
import entidades.enums.StatusOportunidade;
import repositorio.RepositorioCentral;

import java.util.ArrayList;
import java.util.List;

public class OportunidadeService {

    private final RepositorioCentral repositorio;

    public OportunidadeService(RepositorioCentral repositorio) {
        this.repositorio = repositorio;
    }

    public void criarOportunidade(Oportunidade o) {
        repositorio.salvarOportunidade(o);
    }

    public List<Oportunidade> listarTodas() {
        return repositorio.findAllOportunidades();
    }

    public Oportunidade buscarPorId(int id) {
        return repositorio.findOportunidadeById(id);
    }

    public void inscreverDiscente(int oportunidadeId, Discente d) {

        if (!d.isAtivo()){
            System.out.println("Erro: Discente com conta desativada!");
            return;
        }

        Oportunidade op = repositorio.findOportunidadeById(oportunidadeId);
        if(op != null && op.getStatus() == entidades.enums.StatusOportunidade.ABERTA){
            op.solicitarInscricao(d);
        }
    }

    public void aprovarOportunidade(int id){
        Oportunidade op = repositorio.findOportunidadeById(id);
        if (op != null){
            op.aprovarProposta();
        }
    }

    public List<Oportunidade> listarAguardandoAprovacao(){
        List<Oportunidade> aguardando = new ArrayList<Oportunidade>();
        for( Oportunidade op : repositorio.findAllOportunidades()){
            if (op.getStatus() == StatusOportunidade.AGUARDANDO_APROVACAO){
                aguardando.add(op);
            }
        }
        return aguardando;
    }

    public void avaliarInscricao(int oportunidadeId, Discente d, boolean aprovar) {
        Oportunidade op = repositorio.findOportunidadeById(oportunidadeId);
        if( op != null && op.getStatus() == entidades.enums.StatusOportunidade.ABERTA){
            op.avaliarInscricao(d,aprovar);
        }
    }

    public void cancelarInscricao(int oportunidadeId, Discente d) {
        Oportunidade op = repositorio.findOportunidadeById(oportunidadeId);

        if(op != null && op.getStatus().equals("ABERTA")){
            op.cancelarInscricao(d);
        }

    }

    public void encerrarOportunidade(int oportunidadeId) {
        Oportunidade op = repositorio.findOportunidadeById(oportunidadeId);
        if(op != null && op.getStatus() ==  entidades.enums.StatusOportunidade.ABERTA){
            op.encerrar();

            String dataHoje = java.time.LocalDate.now().toString();
            for (Discente d : op.getInscritosAprovados()) {
                Certificado cert = new Certificado(op.getTitulo(), op.getCargaHoraria(), dataHoje);
                d.adicionarCertificado(cert);
                d.adicionarHoras(op.getCargaHoraria());
            }
        }
    }
}