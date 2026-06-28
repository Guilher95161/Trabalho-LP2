package br.ufma.extensao.service;

import br.ufma.extensao.model.Discente;
import br.ufma.extensao.model.SolicitacaoGrupoEstudantil;
import br.ufma.extensao.model.Usuario;
import br.ufma.extensao.model.enums.CargoGrupo;
import br.ufma.extensao.model.enums.StatusSolicitacao;
import br.ufma.extensao.service.exceptions.OperacaoInvalidaException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class SolicitacaoGrupoEstudantilServiceTest {

    @Autowired SolicitacaoGrupoEstudantilService service;
    @Autowired GrupoEstudantilService grupoService;
    @Autowired UsuarioService usuarioService;
    @Autowired DiscenteService discenteService;

    private Discente novoDiscente() {
        Discente d = new Discente();
        d.setNome("Discente");
        d.setEmail("disc-" + UUID.randomUUID() + "@test.com");
        d.setSenha("senha123");
        d.setMatricula(UUID.randomUUID().toString().substring(0, 8));
        d.setAtivo(true);
        return discenteService.salvar(d);
    }

    private Usuario novoDocente() {
        Usuario u = new Usuario();
        u.setNome("Docente");
        u.setEmail("doc-" + UUID.randomUUID() + "@test.com");
        u.setSenha("senha123");
        u.setMatricula(UUID.randomUUID().toString().substring(0, 8));
        u.setAtivo(true);
        return usuarioService.salvar(u);
    }

    private SolicitacaoGrupoEstudantil novaSolicitacao(Discente solicitante, Usuario docente) {
        SolicitacaoGrupoEstudantil s = new SolicitacaoGrupoEstudantil();
        s.setSolicitante(solicitante);
        s.setDocenteResponsavel(docente);
        s.setNomeGrupo("Liga " + UUID.randomUUID());
        s.setDescricao("Descricao");
        return service.salvar(s);
    }

    @Test
    void salvar_deveIniciarComStatusPendente() {
        SolicitacaoGrupoEstudantil salva = novaSolicitacao(novoDiscente(), novoDocente());
        assertThat(salva.getId()).isNotNull();
        assertThat(salva.getStatus()).isEqualTo(StatusSolicitacao.PENDENTE);
    }

    @Test
    void avaliar_aprovado_deveDeferirECriarGrupo() {
        Discente solicitante = novoDiscente();
        SolicitacaoGrupoEstudantil salva = novaSolicitacao(solicitante, novoDocente());
        SolicitacaoGrupoEstudantil avaliada = service.avaliar(salva.getId(), true);
        assertThat(avaliada.getStatus()).isEqualTo(StatusSolicitacao.DEFERIDA);
        // grupo materializado com o solicitante como PRESIDENTE
        assertThat(grupoService.isLider(solicitante.getId())).isTrue();
    }

    @Test
    void avaliar_reprovado_deveIndefirir() {
        SolicitacaoGrupoEstudantil salva = novaSolicitacao(novoDiscente(), novoDocente());
        SolicitacaoGrupoEstudantil avaliada = service.avaliar(salva.getId(), false);
        assertThat(avaliada.getStatus()).isEqualTo(StatusSolicitacao.INDEFERIDA);
    }

    @Test
    void avaliar_deveLancarExcecaoSeNaoPendente() {
        SolicitacaoGrupoEstudantil salva = novaSolicitacao(novoDiscente(), novoDocente());
        service.avaliar(salva.getId(), true);
        assertThatThrownBy(() -> service.avaliar(salva.getId(), true))
                .isInstanceOf(OperacaoInvalidaException.class);
    }

    @Test
    void listarPendentes_deveRetornarApenasPendentes() {
        novaSolicitacao(novoDiscente(), novoDocente());
        SolicitacaoGrupoEstudantil outra = novaSolicitacao(novoDiscente(), novoDocente());
        service.avaliar(outra.getId(), false);
        assertThat(service.listarPendentes())
                .allMatch(s -> s.getStatus() == StatusSolicitacao.PENDENTE);
    }
}
