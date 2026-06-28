package br.ufma.extensao.service;

import br.ufma.extensao.model.Certificado;
import br.ufma.extensao.model.Discente;
import br.ufma.extensao.model.SolicitacaoAproveitamento;
import br.ufma.extensao.model.enums.StatusSolicitacao;
import br.ufma.extensao.repo.CertificadoRepository;
import br.ufma.extensao.repo.DiscenteRepository;
import br.ufma.extensao.service.exceptions.OperacaoInvalidaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class SolicitacaoAproveitamentoServiceTest {

    @Autowired
    SolicitacaoAproveitamentoService service;

    @Autowired
    DiscenteService discenteService;

    @Autowired
    CertificadoRepository certificadoRepository;

    @Autowired
    DiscenteRepository discenteRepository;

    private Discente discente;
    private Certificado certificado;

    @BeforeEach
    void setup() {
        discente = new Discente();
        discente.setNome("Aluno Teste");
        discente.setEmail("aluno_sol@test.com");
        discente.setSenha("senha");
        discente.setMatricula("SOL001");
        discente.setAtivo(true);
        discente = discenteService.salvar(discente);

        certificado = new Certificado();
        certificado.setTituloAtividade("Workshop de Teste");
        certificado.setCargaHoraria(10);
        certificado.setData(LocalDate.now());
        certificado.setAproveitamentoSolicitado(false);
        certificado = certificadoRepository.save(certificado);
    }

    private SolicitacaoAproveitamento criarSolicitacao() {
        SolicitacaoAproveitamento s = new SolicitacaoAproveitamento();
        s.setSolicitante(discente);
        s.setCertificado(certificado);
        return service.salvar(s);
    }

    @Test
    void salvar_deveCriarComStatusPendente() {
        SolicitacaoAproveitamento s = criarSolicitacao();
        assertThat(s.getId()).isNotNull();
        assertThat(s.getStatus()).isEqualTo(StatusSolicitacao.PENDENTE);
        assertThat(s.getDataCriacao()).isEqualTo(LocalDate.now());
    }

    @Test
    void salvar_deveMarcarcertificadoComoSolicitado() {
        criarSolicitacao();
        Certificado cert = certificadoRepository.findById(certificado.getId()).orElseThrow();
        assertThat(cert.isAproveitamentoSolicitado()).isTrue();
    }

    @Test
    void avaliar_deferido_deveSomarHorasAoDiscente() {
        SolicitacaoAproveitamento s = criarSolicitacao();
        int horasAntes = discenteRepository.findById(discente.getId()).orElseThrow().getHorasCumpridas();

        service.avaliar(s.getId(), true, "Aprovado");

        int horasDepois = discenteRepository.findById(discente.getId()).orElseThrow().getHorasCumpridas();
        assertThat(horasDepois).isEqualTo(horasAntes + certificado.getCargaHoraria());
    }

    @Test
    void avaliar_deferido_deveMudarStatusParaDeferida() {
        SolicitacaoAproveitamento s = criarSolicitacao();
        SolicitacaoAproveitamento avaliada = service.avaliar(s.getId(), true, "OK");
        assertThat(avaliada.getStatus()).isEqualTo(StatusSolicitacao.DEFERIDA);
        assertThat(avaliada.getDataAvaliacao()).isEqualTo(LocalDate.now());
    }

    @Test
    void avaliar_indeferido_deveMudarStatusParaIndeferida() {
        SolicitacaoAproveitamento s = criarSolicitacao();
        SolicitacaoAproveitamento avaliada = service.avaliar(s.getId(), false, "Insuficiente");
        assertThat(avaliada.getStatus()).isEqualTo(StatusSolicitacao.INDEFERIDA);
    }

    @Test
    void avaliar_deveLancarExcecaoSeNaoEstiverPendente() {
        SolicitacaoAproveitamento s = criarSolicitacao();
        service.avaliar(s.getId(), true, "OK");
        assertThatThrownBy(() -> service.avaliar(s.getId(), false, "re-avaliacao"))
                .isInstanceOf(OperacaoInvalidaException.class);
    }

    @Test
    void reenviar_deveFuncionarDentroDoPrazo() {
        SolicitacaoAproveitamento s = criarSolicitacao();
        service.avaliar(s.getId(), false, "Motivo");

        SolicitacaoAproveitamento reenviada = service.reenviar(s.getId());
        assertThat(reenviada.getStatus()).isEqualTo(StatusSolicitacao.PENDENTE);
    }

    @Test
    void reenviar_deveLancarExcecaoForaDoPrazo() {
        SolicitacaoAproveitamento s = criarSolicitacao();
        service.avaliar(s.getId(), false, "Motivo");

        SolicitacaoAproveitamento sol = service.buscarPorId(s.getId());
        sol.setDataAvaliacao(LocalDate.now().minusDays(10));

        assertThatThrownBy(() -> service.reenviar(s.getId()))
                .isInstanceOf(OperacaoInvalidaException.class);
    }

    @Test
    void delegar_deveMarcarcComoDelegar() {
        SolicitacaoAproveitamento s = criarSolicitacao();
        SolicitacaoAproveitamento delegada = service.delegar(s.getId());
        assertThat(delegada.isDelegadaParaComissao()).isTrue();
    }

    @Test
    void cancelar_deveLiberarCertificado() {
        SolicitacaoAproveitamento s = criarSolicitacao();
        service.cancelar(s.getId());

        Certificado cert = certificadoRepository.findById(certificado.getId()).orElseThrow();
        assertThat(cert.isAproveitamentoSolicitado()).isFalse();
    }
}
