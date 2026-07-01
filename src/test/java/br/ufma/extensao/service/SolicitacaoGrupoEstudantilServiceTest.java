package br.ufma.extensao.service;

import br.ufma.extensao.model.Discente;
import br.ufma.extensao.model.SolicitacaoGrupoEstudantil;
import br.ufma.extensao.model.Usuario;
import br.ufma.extensao.model.enums.StatusSolicitacao;
import br.ufma.extensao.service.exceptions.OperacaoInvalidaException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
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
    void deveSalvarComStatusPendente() {
        //cenario
        //acao
        SolicitacaoGrupoEstudantil salva = novaSolicitacao(novoDiscente(), novoDocente());

        //verificacao
        Assertions.assertNotNull(salva.getId());
        Assertions.assertEquals(StatusSolicitacao.PENDENTE, salva.getStatus());
    }

    @Test
    void deveDeferirECriarGrupoAoAprovar() {
        //cenario
        Discente solicitante = novoDiscente();
        SolicitacaoGrupoEstudantil salva = novaSolicitacao(solicitante, novoDocente());

        //acao
        SolicitacaoGrupoEstudantil avaliada = service.avaliar(salva.getId(), true);

        //verificacao
        Assertions.assertEquals(StatusSolicitacao.DEFERIDA, avaliada.getStatus());
        // grupo materializado com o solicitante como PRESIDENTE
        Assertions.assertTrue(grupoService.isLider(solicitante.getId()));
    }

    @Test
    void deveIndeferirAoReprovar() {
        //cenario
        SolicitacaoGrupoEstudantil salva = novaSolicitacao(novoDiscente(), novoDocente());

        //acao
        SolicitacaoGrupoEstudantil avaliada = service.avaliar(salva.getId(), false);

        //verificacao
        Assertions.assertEquals(StatusSolicitacao.INDEFERIDA, avaliada.getStatus());
    }

    @Test
    void deveLancarExcecaoAoAvaliarSeNaoPendente() {
        //cenario
        SolicitacaoGrupoEstudantil salva = novaSolicitacao(novoDiscente(), novoDocente());
        service.avaliar(salva.getId(), true);

        //acao e verificacao
        Assertions.assertThrows(OperacaoInvalidaException.class,
                () -> service.avaliar(salva.getId(), true));
    }

    @Test
    void deveListarApenasPendentes() {
        //cenario
        novaSolicitacao(novoDiscente(), novoDocente());
        SolicitacaoGrupoEstudantil outra = novaSolicitacao(novoDiscente(), novoDocente());
        service.avaliar(outra.getId(), false);

        //acao e verificacao
        Assertions.assertTrue(service.listarPendentes().stream()
                .allMatch(s -> s.getStatus() == StatusSolicitacao.PENDENTE));
    }
}
