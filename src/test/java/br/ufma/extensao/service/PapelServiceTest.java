package br.ufma.extensao.service;

import br.ufma.extensao.model.Papel;
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

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
@Transactional
class PapelServiceTest {

    @Autowired
    PapelService service;

    @Test
    void deveSalvarPapel() {
        //cenario
        Papel papel = Papel.builder().nome("DOCENTE_T").build();

        //acao
        Papel salvo = service.salvar(papel);

        //verificacao
        Assertions.assertNotNull(salvo.getId());
        Assertions.assertEquals("DOCENTE_T", salvo.getNome());
    }

    @Test
    void deveLancarExcecaoAoSalvarPapelSemNome() {
        //cenario
        Papel papel = Papel.builder().nome("").build();

        //acao e verificacao
        Assertions.assertThrows(RegraNegocioRunTime.class, () -> service.salvar(papel));
    }

    @Test
    void deveAlterarNomeAoAtualizarPapel() {
        //cenario
        Papel salvo = service.salvar(Papel.builder().nome("ANTIGO").build());

        //acao
        Papel patch = new Papel();
        patch.setId(salvo.getId());
        patch.setNome("NOVO");
        Papel atualizado = service.atualizar(patch);

        //verificacao
        Assertions.assertEquals("NOVO", atualizado.getNome());
    }

    @Test
    void deveRemoverPapel() {
        //cenario
        Papel salvo = service.salvar(Papel.builder().nome("TEMP").build());

        //acao
        service.remover(salvo.getId());

        //verificacao
        Assertions.assertThrows(EntidadeNaoEncontradaException.class,
                () -> service.buscarPorId(salvo.getId()));
    }
}
