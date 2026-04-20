package servicos;

import entidades.*;
import repositorio.RepositorioCentral;

import java.util.List;

/**
 * Contém todas as regras de negócio relacionadas à gestão de oportunidades.
 */
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

    /**
     * Busca uma oportunidade pelo seu ID.
     *
     * @return a {@link Oportunidade} encontrada, ou {@code null} se não existir.
     */
    public Oportunidade buscarPorId(int id) {
        return repositorio.findOportunidadeById(id).orElse(null);
    }

    /**
     * Inscreve um discente em uma oportunidade aberta.
     * Regra de negócio: a inscrição só é realizada se a oportunidade existir e estiver com status "ABERTA".
     */
    public void inscreverDiscente(int oportunidadeId, Discente d) {
        repositorio.findOportunidadeById(oportunidadeId).ifPresent(o -> {
            if ("ABERTA".equals(o.getStatus())) {
                o.inscreverDiscente(d);
            }
        });
    }

    /**
     * Cancela a inscrição de um discente em uma oportunidade.
     */
    public void cancelarInscricao(int oportunidadeId, Discente d) {
        repositorio.findOportunidadeById(oportunidadeId).ifPresent(o -> o.cancelarInscricao(d));
    }

    /**
     * Encerra uma oportunidade e gera automaticamente certificados para todos os discentes inscritos.
     * Regra de negócio: ao encerrar, cada inscrito recebe um {@link Certificado} e tem suas horas
     * acumuladas incrementadas pela carga horária da oportunidade.
     */
    public void encerrarOportunidade(int oportunidadeId) {
        repositorio.findOportunidadeById(oportunidadeId).ifPresent(o -> {
            o.encerrar();

            String dataHoje = java.time.LocalDate.now().toString();
            for (Discente d : o.getInscritos()) {
                Certificado cert = new Certificado(o.getTitulo(), o.getCargaHoraria(), dataHoje);
                d.adicionarCertificado(cert);
                d.adicionarHoras(o.getCargaHoraria());
            }
        });
    }
}