package br.ufma.extensao.service;

import br.ufma.extensao.model.Certificado;
import br.ufma.extensao.service.exceptions.EntidadeNaoEncontradaException;
import br.ufma.extensao.service.exceptions.RegraNegocioRunTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class CertificadoServiceTest {

    @Autowired
    CertificadoService service;

    private Certificado novoCertificado() {
        return Certificado.builder()
                .tituloAtividade("Workshop de Testes")
                .cargaHoraria(8)
                .data(LocalDate.of(2026, 1, 15))
                .aproveitamentoSolicitado(false)
                .build();
    }

    @Test
    void salvar_devePersistirCertificado() {
        Certificado salvo = service.salvar(novoCertificado());
        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getTituloAtividade()).isEqualTo("Workshop de Testes");
    }

    @Test
    void salvar_deveLancarExcecaoSemTitulo() {
        assertThatThrownBy(() -> service.salvar(Certificado.builder().cargaHoraria(8).build()))
                .isInstanceOf(RegraNegocioRunTime.class);
    }

    @Test
    void salvar_deveLancarExcecaoCargaHorariaZero() {
        assertThatThrownBy(() -> service.salvar(
                Certificado.builder().tituloAtividade("X").cargaHoraria(0).build()))
                .isInstanceOf(RegraNegocioRunTime.class);
    }

    @Test
    void atualizar_devePreservarCamposNaoEnviados() {
        Certificado salvo = service.salvar(novoCertificado());
        Certificado patch = new Certificado();
        patch.setId(salvo.getId());
        patch.setTituloAtividade("Novo Titulo");
        Certificado atualizado = service.atualizar(patch);
        assertThat(atualizado.getTituloAtividade()).isEqualTo("Novo Titulo");
        assertThat(atualizado.getCargaHoraria()).isEqualTo(8);
        assertThat(atualizado.getData()).isEqualTo(LocalDate.of(2026, 1, 15));
    }

    @Test
    void remover_deveExcluirCertificado() {
        Certificado salvo = service.salvar(novoCertificado());
        service.remover(salvo.getId());
        assertThatThrownBy(() -> service.buscarPorId(salvo.getId()))
                .isInstanceOf(EntidadeNaoEncontradaException.class);
    }
}
