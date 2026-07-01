package br.ufma.extensao.service;

import br.ufma.extensao.model.Oportunidade;
import br.ufma.extensao.model.enums.ModalidadeOportunidade;
import br.ufma.extensao.model.enums.StatusOportunidade;
import br.ufma.extensao.service.exceptions.OperacaoInvalidaException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
@Transactional
class OportunidadeServiceTest {

    @Autowired
    OportunidadeService service;

    private Oportunidade novaOportunidade() {
        Oportunidade o = new Oportunidade();
        o.setTitulo("Curso de Teste");
        o.setDescricao("Descricao");
        o.setModalidade(ModalidadeOportunidade.CURSO);
        o.setPeriodoRealizacao("01/01/2026 - 31/01/2026");
        o.setCargaHoraria(20);
        o.setVagas(10);
        return o;
    }

    private Oportunidade salvarRascunho() {
        Oportunidade o = novaOportunidade();
        o.setStatus(StatusOportunidade.RASCUNHO);
        return service.salvar(o);
    }

    @Test
    void deveSalvarOportunidade() {
        //cenario
        //acao
        Oportunidade salva = salvarRascunho();

        //verificacao
        Assertions.assertNotNull(salva.getId());
        Assertions.assertEquals(StatusOportunidade.RASCUNHO, salva.getStatus());
    }

    @Test
    void deveAbrirEmAbertaQuandoStatusNaoInformadoESemResponsavelDiscente() {
        //cenario
        Oportunidade o = novaOportunidade(); // sem status e sem responsavel discente

        //acao
        Oportunidade salva = service.salvar(o);

        //verificacao
        Assertions.assertEquals(StatusOportunidade.ABERTA, salva.getStatus());
    }

    @Test
    void deveSubmeterMudandoStatusParaAguardandoAprovacao() {
        //cenario
        Oportunidade o = salvarRascunho();

        //acao
        Oportunidade submetida = service.submeter(o.getId());

        //verificacao
        Assertions.assertEquals(StatusOportunidade.AGUARDANDO_APROVACAO, submetida.getStatus());
    }

    @Test
    void deveLancarExcecaoAoSubmeterSeNaoForRascunho() {
        //cenario
        Oportunidade o = salvarRascunho();
        service.submeter(o.getId());

        //acao e verificacao
        Assertions.assertThrows(OperacaoInvalidaException.class,
                () -> service.submeter(o.getId()));
    }

    @Test
    void deveAprovarMudandoStatusParaAberta() {
        //cenario
        Oportunidade o = salvarRascunho();
        service.submeter(o.getId());

        //acao
        Oportunidade aprovada = service.aprovar(o.getId());

        //verificacao
        Assertions.assertEquals(StatusOportunidade.ABERTA, aprovada.getStatus());
    }

    @Test
    void deveLancarExcecaoAoAprovarSeNaoForAguardandoAprovacao() {
        //cenario
        Oportunidade o = salvarRascunho();

        //acao e verificacao
        Assertions.assertThrows(OperacaoInvalidaException.class,
                () -> service.aprovar(o.getId()));
    }

    @Test
    void deveIniciarMudandoStatusParaEmExecucao() {
        //cenario
        Oportunidade o = salvarRascunho();
        service.submeter(o.getId());
        service.aprovar(o.getId());

        //acao
        Oportunidade iniciada = service.iniciar(o.getId());

        //verificacao
        Assertions.assertEquals(StatusOportunidade.EM_EXECUCAO, iniciada.getStatus());
    }

    @Test
    void deveEncerrarMudandoStatusParaEncerrada() {
        //cenario
        Oportunidade o = salvarRascunho();
        service.submeter(o.getId());
        service.aprovar(o.getId());
        service.iniciar(o.getId());

        //acao
        Oportunidade encerrada = service.encerrar(o.getId());

        //verificacao
        Assertions.assertEquals(StatusOportunidade.ENCERRADA, encerrada.getStatus());
    }

    @Test
    void deveCancelarGravandoMotivo() {
        //cenario
        Oportunidade o = salvarRascunho();
        service.submeter(o.getId());

        //acao
        Oportunidade cancelada = service.cancelar(o.getId(), "Motivo teste");

        //verificacao
        Assertions.assertEquals(StatusOportunidade.CANCELADA, cancelada.getStatus());
        Assertions.assertEquals("Motivo teste", cancelada.getMotivoCancelamento());
    }

    @Test
    void deveLancarExcecaoAoCancelarSeJaEncerrada() {
        //cenario
        Oportunidade o = salvarRascunho();
        service.submeter(o.getId());
        service.aprovar(o.getId());
        service.iniciar(o.getId());
        service.encerrar(o.getId());

        //acao e verificacao
        Assertions.assertThrows(OperacaoInvalidaException.class,
                () -> service.cancelar(o.getId(), "motivo"));
    }

    @Test
    void devePreservarCamposNaoInformadosAoAtualizar() {
        //cenario
        Oportunidade original = salvarRascunho();

        //acao
        Oportunidade patch = new Oportunidade();
        patch.setId(original.getId());
        patch.setTitulo("Novo Titulo");
        Oportunidade atualizada = service.atualizar(patch);

        //verificacao
        Assertions.assertEquals("Novo Titulo", atualizada.getTitulo());
        Assertions.assertEquals(ModalidadeOportunidade.CURSO, atualizada.getModalidade());
        Assertions.assertEquals(20, atualizada.getCargaHoraria());
    }
}
