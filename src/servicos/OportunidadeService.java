package servicos;

import entidades.*;
import entidades.enums.StatusOportunidade;
import excecoes.EntidadeNaoEncontradaException;
import excecoes.OperacaoInvalidaException;
import excecoes.UsuarioInativoException;
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
        if (!d.isAtivo()) throw new UsuarioInativoException(d.getEmail());
        Oportunidade op = repositorio.findOportunidadeById(oportunidadeId);
        if (op == null) throw new EntidadeNaoEncontradaException("Oportunidade #" + oportunidadeId + " nao encontrada.");
        if (op.getStatus() != StatusOportunidade.ABERTA) {
            throw new OperacaoInvalidaException("Inscricao nao permitida: oportunidade esta " + op.getStatus() + ".");
        }
        op.solicitarInscricao(d);
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

    public void iniciarExecucao(int oportunidadeId) {
        Oportunidade op = repositorio.findOportunidadeById(oportunidadeId);
        if (op == null) throw new EntidadeNaoEncontradaException("Oportunidade #" + oportunidadeId + " nao encontrada.");
        if (op.getStatus() != StatusOportunidade.ABERTA) {
            throw new OperacaoInvalidaException("Execucao nao pode ser iniciada: oportunidade esta " + op.getStatus() + ".");
        }
        op.iniciarExecucao();
    }

    public void encerrarOportunidade(int oportunidadeId) {
        Oportunidade op = repositorio.findOportunidadeById(oportunidadeId);
        if (op == null) throw new EntidadeNaoEncontradaException("Oportunidade #" + oportunidadeId + " nao encontrada.");
        if (op.getStatus() != StatusOportunidade.ABERTA && op.getStatus() != StatusOportunidade.EM_EXECUCAO) {
            throw new OperacaoInvalidaException("Encerramento invalido: oportunidade esta " + op.getStatus() + ".");
        }
        op.encerrar();
    }

    public void cancelarOportunidade(int oportunidadeId, String motivo) {
        Oportunidade op = repositorio.findOportunidadeById(oportunidadeId);
        if (op == null) throw new EntidadeNaoEncontradaException("Oportunidade #" + oportunidadeId + " nao encontrada.");
        boolean cancelou = op.cancelar(motivo);
        if (!cancelou) {
            throw new OperacaoInvalidaException("Oportunidade nao pode ser cancelada no status atual: " + op.getStatus() + ".");
        }
    }

    public boolean submeterRascunho(int id) {
        Oportunidade op = repositorio.findOportunidadeById(id);
        if (op == null) return false;
        return op.submeterRascunho();
    }

    public boolean editarRascunho(int id, String titulo, String descricao,
                                  entidades.enums.ModalidadeOportunidade modalidade,
                                  String periodoRealizacao,
                                  int cargaHoraria, int vagas) {
        Oportunidade op = repositorio.findOportunidadeById(id);
        if (op == null) return false;
        return op.editarSeRascunho(titulo, descricao, modalidade, periodoRealizacao, cargaHoraria, vagas);
    }

    public List<Oportunidade> listarRascunhosDe(Usuario responsavel) {
        List<Oportunidade> rascunhos = new ArrayList<>();
        for (Oportunidade op : repositorio.findAllOportunidades()) {
            if (op.getStatus() == StatusOportunidade.RASCUNHO
                && op.getResponsavel().equals(responsavel)) {
                rascunhos.add(op);
            }
        }
        return rascunhos;
    }

    // emite o cert mas as horas so entram depois do aproveitamento ser deferido
    public void certificarDiscentes(int oportunidadeId, List<Discente> selecionados) {
        Oportunidade op = repositorio.findOportunidadeById(oportunidadeId);
        if (op == null || op.getStatus() != StatusOportunidade.ENCERRADA) return;
        String dataHoje = java.time.LocalDate.now().toString();
        for (Discente d : selecionados) {
            Certificado cert = new Certificado(
                op.getTitulo(),
                op.getCargaHoraria(),
                dataHoje
            );
            d.adicionarCertificado(cert);
        }
    }

    public boolean substituirParticipante(int oportunidadeId, Discente aRemover, Discente substituto) {
        Oportunidade op = repositorio.findOportunidadeById(oportunidadeId);
        if (op == null) return false;
        if (op.getStatus() != StatusOportunidade.ABERTA
                && op.getStatus() != StatusOportunidade.EM_EXECUCAO) return false;
        return op.substituirParticipante(aRemover, substituto);
    }
}
