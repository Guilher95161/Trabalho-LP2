package br.ufma.extensao.service;

import br.ufma.extensao.model.Papel;
import br.ufma.extensao.service.exceptions.EntidadeNaoEncontradaException;
import br.ufma.extensao.service.exceptions.RegraNegocioRunTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class PapelServiceTest {

    @Autowired
    PapelService service;

    @Test
    void salvar_devePersistirPapel() {
        Papel salvo = service.salvar(Papel.builder().nome("DOCENTE_T").build());
        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getNome()).isEqualTo("DOCENTE_T");
    }

    @Test
    void salvar_deveLancarExcecaoSemNome() {
        assertThatThrownBy(() -> service.salvar(Papel.builder().nome("").build()))
                .isInstanceOf(RegraNegocioRunTime.class);
    }

    @Test
    void atualizar_deveAlterarNome() {
        Papel salvo = service.salvar(Papel.builder().nome("ANTIGO").build());
        Papel patch = new Papel();
        patch.setId(salvo.getId());
        patch.setNome("NOVO");
        Papel atualizado = service.atualizar(patch);
        assertThat(atualizado.getNome()).isEqualTo("NOVO");
    }

    @Test
    void remover_deveExcluirPapel() {
        Papel salvo = service.salvar(Papel.builder().nome("TEMP").build());
        service.remover(salvo.getId());
        assertThatThrownBy(() -> service.buscarPorId(salvo.getId()))
                .isInstanceOf(EntidadeNaoEncontradaException.class);
    }
}
