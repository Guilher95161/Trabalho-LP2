package br.ufma.extensao.service;

import br.ufma.extensao.model.Curso;
import br.ufma.extensao.model.Discente;
import br.ufma.extensao.service.exceptions.RegraNegocioRunTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    void salvar_devePersistirDiscente() {
        Discente salvo = service.salvar(novoDiscente("disc@test.com"));
        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getNome()).isEqualTo("Aluno Teste");
    }

    @Test
    void salvar_deveLancarExcecao_semNome() {
        Discente d = novoDiscente("semnom@test.com");
        d.setNome(null);
        assertThatThrownBy(() -> service.salvar(d))
                .isInstanceOf(RegraNegocioRunTime.class);
    }

    @Test
    void salvar_deveLancarExcecao_semEmail() {
        Discente d = novoDiscente(null);
        assertThatThrownBy(() -> service.salvar(d))
                .isInstanceOf(RegraNegocioRunTime.class);
    }

    @Test
    void atualizar_devePreservarCamposNaoEnviados() {
        Discente original = service.salvar(novoDiscente("patch@test.com"));

        Discente patch = new Discente();
        patch.setId(original.getId());
        patch.setNome("Novo Nome");
        Discente atualizado = service.atualizar(patch);

        assertThat(atualizado.getNome()).isEqualTo("Novo Nome");
        assertThat(atualizado.getEmail()).isEqualTo("patch@test.com");
        assertThat(atualizado.getMatricula()).isEqualTo("DIS001");
    }

    @Test
    void painelHoras_deveCalcularPercentualCorretamente() {
        Curso curso = new Curso();
        curso.setNome("Engenharia de Software");
        curso.setCurriculo("2023");
        curso.setCargaHoraria(200);
        Curso cursoCriado = cursoService.salvar(curso);

        Discente d = novoDiscente("painel@test.com");
        d.setCurso(cursoCriado);
        d.setHorasCumpridas(50);
        Discente salvo = service.salvar(d);

        Map<String, Object> painel = service.painelHoras(salvo.getId());

        assertThat(painel.get("horasCumpridas")).isEqualTo(50);
        assertThat(painel.get("metaHoras")).isEqualTo(200);
        assertThat(painel.get("horasRestantes")).isEqualTo(150);
        assertThat((Double) painel.get("percentualConcluido")).isEqualTo(25.0);
        assertThat(painel.get("concluido")).isEqualTo(false);
    }

    @Test
    void painelHoras_deveMostrarConcluidoQuandoMetaAtingida() {
        Curso curso = new Curso();
        curso.setNome("Ciencias da Computacao");
        curso.setCurriculo("2023");
        curso.setCargaHoraria(100);
        Curso cursoCriado = cursoService.salvar(curso);

        Discente d = novoDiscente("concluido@test.com");
        d.setCurso(cursoCriado);
        d.setHorasCumpridas(100);
        Discente salvo = service.salvar(d);

        Map<String, Object> painel = service.painelHoras(salvo.getId());

        assertThat(painel.get("concluido")).isEqualTo(true);
        assertThat((Double) painel.get("percentualConcluido")).isEqualTo(100.0);
        assertThat(painel.get("horasRestantes")).isEqualTo(0);
    }
}
