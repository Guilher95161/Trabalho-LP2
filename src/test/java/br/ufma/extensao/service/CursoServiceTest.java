package br.ufma.extensao.service;

import br.ufma.extensao.model.Curso;
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
class CursoServiceTest {

    @Autowired
    CursoService service;

    private Curso novoCurso() {
        return Curso.builder().nome("Engenharia de Software").curriculo("2022").cargaHoraria(240).build();
    }

    @Test
    void salvar_devePersistirCurso() {
        Curso salvo = service.salvar(novoCurso());
        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getNome()).isEqualTo("Engenharia de Software");
    }

    @Test
    void salvar_deveLancarExcecaoSemNome() {
        assertThatThrownBy(() -> service.salvar(Curso.builder().cargaHoraria(100).build()))
                .isInstanceOf(RegraNegocioRunTime.class);
    }

    @Test
    void atualizar_devePreservarCamposNaoEnviados() {
        Curso salvo = service.salvar(novoCurso());
        Curso patch = new Curso();
        patch.setId(salvo.getId());
        patch.setNome("Ciencia da Computacao");
        Curso atualizado = service.atualizar(patch);
        assertThat(atualizado.getNome()).isEqualTo("Ciencia da Computacao");
        assertThat(atualizado.getCurriculo()).isEqualTo("2022");
        assertThat(atualizado.getCargaHoraria()).isEqualTo(240);
    }

    @Test
    void remover_deveExcluirCurso() {
        Curso salvo = service.salvar(novoCurso());
        service.remover(salvo.getId());
        assertThatThrownBy(() -> service.buscarPorId(salvo.getId()))
                .isInstanceOf(EntidadeNaoEncontradaException.class);
    }
}
