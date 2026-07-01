package br.ufma.extensao.service;

import br.ufma.extensao.model.Curso;
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
class CursoServiceTest {

    @Autowired
    CursoService service;

    private Curso novoCurso() {
        return Curso.builder().nome("Engenharia de Software").curriculo("2022").cargaHoraria(240).build();
    }

    @Test
    void deveSalvarCurso() {
        //cenario
        Curso curso = novoCurso();

        //acao
        Curso salvo = service.salvar(curso);

        //verificacao
        Assertions.assertNotNull(salvo.getId());
        Assertions.assertEquals("Engenharia de Software", salvo.getNome());
    }

    @Test
    void deveLancarExcecaoAoSalvarCursoSemNome() {
        //cenario
        Curso curso = Curso.builder().cargaHoraria(100).build();

        //acao e verificacao
        Assertions.assertThrows(RegraNegocioRunTime.class, () -> service.salvar(curso));
    }

    @Test
    void devePreservarCamposNaoEnviadosAoAtualizarCurso() {
        //cenario
        Curso salvo = service.salvar(novoCurso());

        //acao
        Curso patch = new Curso();
        patch.setId(salvo.getId());
        patch.setNome("Ciencia da Computacao");
        Curso atualizado = service.atualizar(patch);

        //verificacao
        Assertions.assertEquals("Ciencia da Computacao", atualizado.getNome());
        Assertions.assertEquals("2022", atualizado.getCurriculo());
        Assertions.assertEquals(240, atualizado.getCargaHoraria());
    }

    @Test
    void deveRemoverCurso() {
        //cenario
        Curso salvo = service.salvar(novoCurso());

        //acao
        service.remover(salvo.getId());

        //verificacao
        Assertions.assertThrows(EntidadeNaoEncontradaException.class,
                () -> service.buscarPorId(salvo.getId()));
    }
}
