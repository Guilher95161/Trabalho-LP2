package br.ufma.extensao.service;

import br.ufma.extensao.model.Oportunidade;
import br.ufma.extensao.model.enums.ModalidadeOportunidade;
import br.ufma.extensao.model.enums.StatusOportunidade;
import br.ufma.extensao.service.exceptions.OperacaoInvalidaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class OportunidadeServiceTest {

    @Autowired
    OportunidadeService service;

    private Oportunidade salvarRascunho() {
        Oportunidade o = new Oportunidade();
        o.setTitulo("Curso de Teste");
        o.setDescricao("Descricao");
        o.setModalidade(ModalidadeOportunidade.CURSO);
        o.setPeriodoRealizacao("01/01/2026 - 31/01/2026");
        o.setCargaHoraria(20);
        o.setVagas(10);
        o.setStatus(StatusOportunidade.RASCUNHO);
        return service.salvar(o);
    }

    @Test
    void salvar_devePersistirOportunidade() {
        Oportunidade salva = salvarRascunho();
        assertThat(salva.getId()).isNotNull();
        assertThat(salva.getStatus()).isEqualTo(StatusOportunidade.RASCUNHO);
    }

    @Test
    void submeter_deveMudarStatusParaAguardandoAprovacao() {
        Oportunidade o = salvarRascunho();
        Oportunidade submetida = service.submeter(o.getId());
        assertThat(submetida.getStatus()).isEqualTo(StatusOportunidade.AGUARDANDO_APROVACAO);
    }

    @Test
    void submeter_deveLancarExcecaoSeNaoForRascunho() {
        Oportunidade o = salvarRascunho();
        service.submeter(o.getId());
        assertThatThrownBy(() -> service.submeter(o.getId()))
                .isInstanceOf(OperacaoInvalidaException.class);
    }

    @Test
    void aprovar_deveMudarStatusParaAberta() {
        Oportunidade o = salvarRascunho();
        service.submeter(o.getId());
        Oportunidade aprovada = service.aprovar(o.getId());
        assertThat(aprovada.getStatus()).isEqualTo(StatusOportunidade.ABERTA);
    }

    @Test
    void aprovar_deveLancarExcecaoSeNaoForAguardandoAprovacao() {
        Oportunidade o = salvarRascunho();
        assertThatThrownBy(() -> service.aprovar(o.getId()))
                .isInstanceOf(OperacaoInvalidaException.class);
    }

    @Test
    void iniciar_deveMudarStatusParaEmExecucao() {
        Oportunidade o = salvarRascunho();
        service.submeter(o.getId());
        service.aprovar(o.getId());
        Oportunidade iniciada = service.iniciar(o.getId());
        assertThat(iniciada.getStatus()).isEqualTo(StatusOportunidade.EM_EXECUCAO);
    }

    @Test
    void encerrar_deveMudarStatusParaEncerrada() {
        Oportunidade o = salvarRascunho();
        service.submeter(o.getId());
        service.aprovar(o.getId());
        service.iniciar(o.getId());
        Oportunidade encerrada = service.encerrar(o.getId());
        assertThat(encerrada.getStatus()).isEqualTo(StatusOportunidade.ENCERRADA);
    }

    @Test
    void cancelar_deveMudarStatusEGravarMotivo() {
        Oportunidade o = salvarRascunho();
        service.submeter(o.getId());
        Oportunidade cancelada = service.cancelar(o.getId(), "Motivo teste");
        assertThat(cancelada.getStatus()).isEqualTo(StatusOportunidade.CANCELADA);
        assertThat(cancelada.getMotivoCancelamento()).isEqualTo("Motivo teste");
    }

    @Test
    void cancelar_deveLancarExcecaoSeJaEncerrada() {
        Oportunidade o = salvarRascunho();
        service.submeter(o.getId());
        service.aprovar(o.getId());
        service.iniciar(o.getId());
        service.encerrar(o.getId());
        assertThatThrownBy(() -> service.cancelar(o.getId(), "motivo"))
                .isInstanceOf(OperacaoInvalidaException.class);
    }

    @Test
    void atualizar_devePreservarCamposNaoInformados() {
        Oportunidade original = salvarRascunho();
        Oportunidade patch = new Oportunidade();
        patch.setId(original.getId());
        patch.setTitulo("Novo Titulo");
        Oportunidade atualizada = service.atualizar(patch);
        assertThat(atualizada.getTitulo()).isEqualTo("Novo Titulo");
        assertThat(atualizada.getModalidade()).isEqualTo(ModalidadeOportunidade.CURSO);
        assertThat(atualizada.getCargaHoraria()).isEqualTo(20);
    }
}
