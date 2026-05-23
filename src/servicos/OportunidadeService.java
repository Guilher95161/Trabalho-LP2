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

    public boolean inscreverDiscente(int oportunidadeId, Discente d) {
        if (!d.isAtivo()) return false;
        Oportunidade op = repositorio.findOportunidadeById(oportunidadeId);
        if (op != null && op.getStatus() == StatusOportunidade.ABERTA) {
            op.solicitarInscricao(d);
            return true;
        }
        return false;
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

    public boolean cancelarInscricao(int oportunidadeId, Discente d) {
        Oportunidade op = repositorio.findOportunidadeById(oportunidadeId);
        if (op != null && op.getStatus() == StatusOportunidade.ABERTA) {
            op.cancelarInscricao(d);
            return true;
        }
        return false;
    }

    public void encerrarOportunidade(int oportunidadeId) {
        Oportunidade op = repositorio.findOportunidadeById(oportunidadeId);
        if (op != null && op.getStatus() == StatusOportunidade.ABERTA) {
            op.encerrar();
        }
    }

    public void certificarDiscentes(int oportunidadeId, List<Discente> selecionados) {
        Oportunidade op = repositorio.findOportunidadeById(oportunidadeId);
        if (op == null || op.getStatus() != StatusOportunidade.ENCERRADA) return;
        String dataHoje = java.time.LocalDate.now().toString();
        for (Discente d : selecionados) {
            Certificado cert = new Certificado(op.getTitulo(), op.getCargaHoraria(), dataHoje);
            d.adicionarCertificado(cert);
            // Horas só são adicionadas após deferimento do aproveitamento
        }
    }

    public boolean substituirParticipante(int oportunidadeId, Discente aRemover, Discente substituto) {
        Oportunidade op = repositorio.findOportunidadeById(oportunidadeId);
        if (op == null || op.getStatus() != StatusOportunidade.ABERTA) return false;
        return op.substituirParticipante(aRemover, substituto);
    }

    public int calcularHorasPendentes(Discente d) {
        int total = 0;
        for (Oportunidade op : repositorio.findAllOportunidades()) {
            boolean emAndamento = op.getStatus() == StatusOportunidade.ABERTA
                               || op.getStatus() == StatusOportunidade.EM_EXECUCAO;
            if (emAndamento && op.getInscritosAprovados().contains(d)) {
                total += op.getCargaHoraria();
            }
        }
        return total;
    }
}