package br.ufma.extensao.service;

import br.ufma.extensao.model.Certificado;
import br.ufma.extensao.model.Discente;
import br.ufma.extensao.model.SolicitacaoAproveitamento;
import br.ufma.extensao.model.enums.StatusSolicitacao;
import br.ufma.extensao.repo.CertificadoRepository;
import br.ufma.extensao.repo.DiscenteRepository;
import br.ufma.extensao.service.exceptions.OperacaoInvalidaException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
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
    void deveSalvarComStatusPendente() {
        //cenario
        //acao
        SolicitacaoAproveitamento s = criarSolicitacao();

        //verificacao
        Assertions.assertNotNull(s.getId());
        Assertions.assertEquals(StatusSolicitacao.PENDENTE, s.getStatus());
        Assertions.assertEquals(LocalDate.now(), s.getDataCriacao());
    }

    @Test
    void deveMarcarCertificadoComoSolicitadoAoSalvar() {
        //cenario
        //acao
        criarSolicitacao();

        //verificacao
        Certificado cert = certificadoRepository.findById(certificado.getId()).orElseThrow();
        Assertions.assertTrue(cert.isAproveitamentoSolicitado());
    }

    @Test
    void deveSomarHorasAoDiscenteAoDeferir() {
        //cenario
        SolicitacaoAproveitamento s = criarSolicitacao();
        int horasAntes = discenteRepository.findById(discente.getId()).orElseThrow().getHorasCumpridas();

        //acao
        service.avaliar(s.getId(), true, "Aprovado");

        //verificacao
        int horasDepois = discenteRepository.findById(discente.getId()).orElseThrow().getHorasCumpridas();
        Assertions.assertEquals(horasAntes + certificado.getCargaHoraria(), horasDepois);
    }

    @Test
    void deveMudarStatusParaDeferidaAoDeferir() {
        //cenario
        SolicitacaoAproveitamento s = criarSolicitacao();

        //acao
        SolicitacaoAproveitamento avaliada = service.avaliar(s.getId(), true, "OK");

        //verificacao
        Assertions.assertEquals(StatusSolicitacao.DEFERIDA, avaliada.getStatus());
        Assertions.assertEquals(LocalDate.now(), avaliada.getDataAvaliacao());
    }

    @Test
    void deveMudarStatusParaIndeferidaAoIndeferir() {
        //cenario
        SolicitacaoAproveitamento s = criarSolicitacao();

        //acao
        SolicitacaoAproveitamento avaliada = service.avaliar(s.getId(), false, "Insuficiente");

        //verificacao
        Assertions.assertEquals(StatusSolicitacao.INDEFERIDA, avaliada.getStatus());
    }

    @Test
    void deveLancarExcecaoAoAvaliarSeNaoEstiverPendente() {
        //cenario
        SolicitacaoAproveitamento s = criarSolicitacao();
        service.avaliar(s.getId(), true, "OK");

        //acao e verificacao
        Assertions.assertThrows(OperacaoInvalidaException.class,
                () -> service.avaliar(s.getId(), false, "re-avaliacao"));
    }

    @Test
    void deveReenviarDentroDoPrazo() {
        //cenario
        SolicitacaoAproveitamento s = criarSolicitacao();
        service.avaliar(s.getId(), false, "Motivo");

        //acao
        SolicitacaoAproveitamento reenviada = service.reenviar(s.getId());

        //verificacao
        Assertions.assertEquals(StatusSolicitacao.PENDENTE, reenviada.getStatus());
    }

    @Test
    void deveLancarExcecaoAoReenviarForaDoPrazo() {
        //cenario
        SolicitacaoAproveitamento s = criarSolicitacao();
        service.avaliar(s.getId(), false, "Motivo");
        SolicitacaoAproveitamento sol = service.buscarPorId(s.getId());
        sol.setDataAvaliacao(LocalDate.now().minusDays(10));

        //acao e verificacao
        Assertions.assertThrows(OperacaoInvalidaException.class,
                () -> service.reenviar(s.getId()));
    }

    @Test
    void deveMarcarComoDelegadaAoDelegar() {
        //cenario
        SolicitacaoAproveitamento s = criarSolicitacao();

        //acao
        SolicitacaoAproveitamento delegada = service.delegar(s.getId());

        //verificacao
        Assertions.assertTrue(delegada.isDelegadaParaComissao());
    }

    @Test
    void deveLiberarCertificadoAoCancelar() {
        //cenario
        SolicitacaoAproveitamento s = criarSolicitacao();

        //acao
        service.cancelar(s.getId());

        //verificacao
        Certificado cert = certificadoRepository.findById(certificado.getId()).orElseThrow();
        Assertions.assertFalse(cert.isAproveitamentoSolicitado());
    }
}
