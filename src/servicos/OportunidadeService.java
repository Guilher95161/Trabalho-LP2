package servicos;

import entidades.*;
import repositorio.RepositorioCentral;

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
        Oportunidade op = repositorio.findOportunidadeById(oportunidadeId);

        if(op != null){
            if("ABERTA".equals(op.getStatus()) && op.getInscritos().size() < op.getVagas()){
                op.inscreverDiscente(d);
            }
        }
    }

    public void cancelarInscricao(int oportunidadeId, Discente d) {
        Oportunidade op = repositorio.findOportunidadeById(oportunidadeId);

        if(op != null && op.getStatus().equals("ABERTA")){
            op.cancelarInscricao(d);
        }

    }

    /*
     Encerra uma oportunidade e gera automaticamente certificados para todos os discentes inscritos.
     Regra de negócio: ao encerrar, cada inscrito recebe um {@link Certificado} e tem suas horas
     acumuladas incrementadas pela carga horária da oportunidade.
     */
    public void encerrarOportunidade(int oportunidadeId) {
        Oportunidade op = repositorio.findOportunidadeById(oportunidadeId);
        if(op != null && op.getStatus().equals("ABERTA")){
            op.encerrar();

            String dataHoje = java.time.LocalDate.now().toString();
            for (Discente d : op.getInscritos()) {
                Certificado cert = new Certificado(op.getTitulo(), op.getCargaHoraria(), dataHoje);
                d.adicionarCertificado(cert);
                d.adicionarHoras(op.getCargaHoraria());
            }
        }
    }
}