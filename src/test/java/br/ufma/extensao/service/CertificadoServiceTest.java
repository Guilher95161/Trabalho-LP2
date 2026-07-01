package br.ufma.extensao.service;

import br.ufma.extensao.model.Certificado;
import br.ufma.extensao.service.exceptions.EntidadeNaoEncontradaException;
import br.ufma.extensao.service.exceptions.RegraNegocioRunTime;
import org.junit.jupiter.api.Assertions;
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
    void deveSalvarCertificado() {
        //cenario
        Certificado certificado = novoCertificado();

        //acao
        Certificado salvo = service.salvar(certificado);

        //verificacao
        Assertions.assertNotNull(salvo.getId());
        Assertions.assertEquals("Workshop de Testes", salvo.getTituloAtividade());
    }

    @Test
    void deveLancarExcecaoAoSalvarCertificadoSemTitulo() {
        //cenario
        Certificado certificado = Certificado.builder().cargaHoraria(8).build();

        //acao e verificacao
        Assertions.assertThrows(RegraNegocioRunTime.class, () -> service.salvar(certificado));
    }

    @Test
    void deveLancarExcecaoAoSalvarCertificadoComCargaHorariaZero() {
        //cenario
        Certificado certificado = Certificado.builder().tituloAtividade("X").cargaHoraria(0).build();

        //acao e verificacao
        Assertions.assertThrows(RegraNegocioRunTime.class, () -> service.salvar(certificado));
    }

    @Test
    void devePreservarCamposNaoEnviadosAoAtualizarCertificado() {
        //cenario
        Certificado salvo = service.salvar(novoCertificado());

        //acao
        Certificado patch = new Certificado();
        patch.setId(salvo.getId());
        patch.setTituloAtividade("Novo Titulo");
        Certificado atualizado = service.atualizar(patch);

        //verificacao
        Assertions.assertEquals("Novo Titulo", atualizado.getTituloAtividade());
        Assertions.assertEquals(8, atualizado.getCargaHoraria());
        Assertions.assertEquals(LocalDate.of(2026, 1, 15), atualizado.getData());
    }

    @Test
    void deveRemoverCertificado() {
        //cenario
        Certificado salvo = service.salvar(novoCertificado());

        //acao
        service.remover(salvo.getId());

        //verificacao
        Assertions.assertThrows(EntidadeNaoEncontradaException.class,
                () -> service.buscarPorId(salvo.getId()));
    }
}
