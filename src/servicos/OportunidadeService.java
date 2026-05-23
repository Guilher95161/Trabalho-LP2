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

    public boolean iniciarExecucao(int oportunidadeId) {
        Oportunidade op = repositorio.findOportunidadeById(oportunidadeId);
        if (op != null && op.getStatus() == StatusOportunidade.ABERTA) {
            op.iniciarExecucao();
            return true;
        }
        return false;
    }

    public void encerrarOportunidade(int oportunidadeId) {
        Oportunidade op = repositorio.findOportunidadeById(oportunidadeId);
        if (op != null && (op.getStatus() == StatusOportunidade.ABERTA
                        || op.getStatus() == StatusOportunidade.EM_EXECUCAO)) {
            op.encerrar();
        }
    }

    /**
     * RF012 - cancela uma oportunidade. So permite quando ainda nao foi ENCERRADA.
     * Retorna true em sucesso.
     */
    public boolean cancelarOportunidade(int oportunidadeId) {
        Oportunidade op = repositorio.findOportunidadeById(oportunidadeId);
        if (op == null) return false;
        return op.cancelar();
    }

    public void certificarDiscentes(int oportunidadeId, List<Discente> selecionados) {
        Oportunidade op = repositorio.findOportunidadeById(oportunidadeId);
        if (op == null || op.getStatus() != StatusOportunidade.ENCERRADA) return;
        String dataHoje = java.time.LocalDate.now().toString();
        for (Discente d : selecionados) {
            Certificado cert = new Certificado(
                op.getTitulo(),
                op.getCargaHoraria(),
                dataHoje,
                op.isUce(),
                op.getComponenteCurricular(),
                op.getUceVinculada()
            );
            d.adicionarCertificado(cert);
            // Horas só são adicionadas após deferimento do aproveitamento
        }
    }

    public boolean substituirParticipante(int oportunidadeId, Discente aRemover, Discente substituto) {
        Oportunidade op = repositorio.findOportunidadeById(oportunidadeId);
        if (op == null) return false;
        // Substituicao permitida enquanto a oportunidade nao foi encerrada
        if (op.getStatus() != StatusOportunidade.ABERTA
                && op.getStatus() != StatusOportunidade.EM_EXECUCAO) return false;
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

    // RF009 - soma horas concluidas (deferidas) que vieram de oportunidades UCE
    // Conta apenas certificados de UCE cujo aproveitamento ja foi deferido
    public int calcularHorasUceConcluidas(Discente d) {
        int total = 0;
        for (Certificado c : d.getCertificados()) {
            if (c.isUce() && c.isAproveitamentoSolicitado()) {
                // aproveitamentoSolicitado fica true e nao volta a false quando deferido;
                // como precisamos do estado final, conferimos via solicitacoes
                // (alternativa: marcar deferimento direto no certificado)
                if (foiDeferida(d, c)) total += c.getCargaHoraria();
            }
        }
        return total;
    }

    private boolean foiDeferida(Discente d, Certificado c) {
        for (SolicitacaoAproveitamento s : repositorio.findAllSolicitacoes()) {
            if (s.getSolicitante().equals(d)
                && s.getCertificado() == c
                && s.getStatus() == entidades.enums.StatusSolicitacao.DEFERIDA) {
                return true;
            }
        }
        return false;
    }
}