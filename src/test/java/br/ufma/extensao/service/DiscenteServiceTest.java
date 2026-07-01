package br.ufma.extensao.service;

import br.ufma.extensao.model.Curso;
import br.ufma.extensao.model.Discente;
import br.ufma.extensao.service.exceptions.RegraNegocioRunTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
@Transactional
class DiscenteServiceTest {

    @Autowired
    DiscenteService service;

    @Autowired
    CursoService cursoService;

    private Discente novoDiscente(String email) {
        Discente d = new Discente();
        d.setNome("Aluno Teste");
        d.setEmail(email);
        d.setSenha("senha123");
        d.setMatricula("DIS001");
        d.setAtivo(true);
        return d;
    }

    @Test
    void deveSalvarDiscente() {
        //cenario
        Discente discente = novoDiscente("disc@test.com");

        //acao
        Discente salvo = service.salvar(discente);

        //verificacao
        Assertions.assertNotNull(salvo.getId());
        Assertions.assertEquals("Aluno Teste", salvo.getNome());
    }

    @Test
    void deveLancarExcecaoAoSalvarDiscenteSemNome() {
        //cenario
        Discente d = novoDiscente("semnom@test.com");
        d.setNome(null);

        //acao e verificacao
        Assertions.assertThrows(RegraNegocioRunTime.class, () -> service.salvar(d));
    }

    @Test
    void deveLancarExcecaoAoSalvarDiscenteSemEmail() {
        //cenario
        Discente d = novoDiscente(null);

        //acao e verificacao
        Assertions.assertThrows(RegraNegocioRunTime.class, () -> service.salvar(d));
    }

    @Test
    void devePreservarCamposNaoEnviadosAoAtualizarDiscente() {
        //cenario
        Discente original = service.salvar(novoDiscente("patch@test.com"));

        //acao
        Discente patch = new Discente();
        patch.setId(original.getId());
        patch.setNome("Novo Nome");
        Discente atualizado = service.atualizar(patch);

        //verificacao
        Assertions.assertEquals("Novo Nome", atualizado.getNome());
        Assertions.assertEquals("patch@test.com", atualizado.getEmail());
        Assertions.assertEquals("DIS001", atualizado.getMatricula());
    }

    @Test
    void deveCalcularPercentualNoPainelDeHoras() {
        //cenario
        Curso curso = new Curso();
        curso.setNome("Engenharia de Software");
        curso.setCurriculo("2023");
        curso.setCargaHoraria(200);
        Curso cursoCriado = cursoService.salvar(curso);

        Discente d = novoDiscente("painel@test.com");
        d.setCurso(cursoCriado);
        d.setHorasCumpridas(50);
        Discente salvo = service.salvar(d);

        //acao
        Map<String, Object> painel = service.painelHoras(salvo.getId());

        //verificacao
        Assertions.assertEquals(50, painel.get("horasCumpridas"));
        Assertions.assertEquals(200, painel.get("metaHoras"));
        Assertions.assertEquals(150, painel.get("horasRestantes"));
        Assertions.assertEquals(25.0, painel.get("percentualConcluido"));
        Assertions.assertEquals(false, painel.get("concluido"));
    }

    @Test
    void deveMostrarConcluidoQuandoMetaAtingidaNoPainel() {
        //cenario
        Curso curso = new Curso();
        curso.setNome("Ciencias da Computacao");
        curso.setCurriculo("2023");
        curso.setCargaHoraria(100);
        Curso cursoCriado = cursoService.salvar(curso);

        Discente d = novoDiscente("concluido@test.com");
        d.setCurso(cursoCriado);
        d.setHorasCumpridas(100);
        Discente salvo = service.salvar(d);

        //acao
        Map<String, Object> painel = service.painelHoras(salvo.getId());

        //verificacao
        Assertions.assertEquals(true, painel.get("concluido"));
        Assertions.assertEquals(100.0, painel.get("percentualConcluido"));
        Assertions.assertEquals(0, painel.get("horasRestantes"));
    }
}
