package br.ufma.extensao.service;

import br.ufma.extensao.model.Discente;
import br.ufma.extensao.model.GrupoEstudantil;
import br.ufma.extensao.model.Usuario;
import br.ufma.extensao.model.enums.CargoGrupo;
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
class GrupoEstudantilServiceTest {

    @Autowired GrupoEstudantilService service;
    @Autowired UsuarioService usuarioService;
    @Autowired DiscenteService discenteService;

    private Usuario novoResponsavel() {
        Usuario u = new Usuario();
        u.setNome("Responsavel");
        u.setEmail("resp-" + UUID.randomUUID() + "@test.com");
        u.setSenha("senha123");
        u.setMatricula(UUID.randomUUID().toString().substring(0, 8));
        u.setAtivo(true);
        return usuarioService.salvar(u);
    }

    private Discente novoDiscente() {
        Discente d = new Discente();
        d.setNome("Discente");
        d.setEmail("disc-" + UUID.randomUUID() + "@test.com");
        d.setSenha("senha123");
        d.setMatricula(UUID.randomUUID().toString().substring(0, 8));
        d.setAtivo(true);
        return discenteService.salvar(d);
    }

    private GrupoEstudantil novoGrupo(Usuario responsavel) {
        return service.salvar(GrupoEstudantil.builder()
                .nome("Grupo " + UUID.randomUUID())
                .responsavel(responsavel)
                .build());
    }

    @Test
    void deveSalvarGrupo() {
        //cenario
        Usuario responsavel = novoResponsavel();

        //acao
        GrupoEstudantil salvo = novoGrupo(responsavel);

        //verificacao
        Assertions.assertNotNull(salvo.getId());
    }

    @Test
    void deveAdicionarMembroComoMembro() {
        //cenario
        GrupoEstudantil grupo = novoGrupo(novoResponsavel());
        Discente discente = novoDiscente();

        //acao
        GrupoEstudantil atualizado = service.adicionarMembro(grupo.getId(), discente.getId());

        //verificacao
        Assertions.assertEquals(1, atualizado.getMembros().size());
        Assertions.assertEquals(CargoGrupo.MEMBRO, atualizado.getMembros().get(0).getCargo());
    }

    @Test
    void deveLancarExcecaoAoAdicionarMembroJaExistente() {
        //cenario
        GrupoEstudantil grupo = novoGrupo(novoResponsavel());
        Discente discente = novoDiscente();
        service.adicionarMembro(grupo.getId(), discente.getId());

        //acao e verificacao
        Assertions.assertThrows(OperacaoInvalidaException.class,
                () -> service.adicionarMembro(grupo.getId(), discente.getId()));
    }

    @Test
    void deveRemoverMembroDoGrupo() {
        //cenario
        GrupoEstudantil grupo = novoGrupo(novoResponsavel());
        Discente discente = novoDiscente();
        service.adicionarMembro(grupo.getId(), discente.getId());

        //acao
        GrupoEstudantil atualizado = service.removerMembro(grupo.getId(), discente.getId());

        //verificacao
        Assertions.assertTrue(atualizado.getMembros().isEmpty());
    }

    @Test
    void deveAlterarCargoDoMembroAoDefinirCargo() {
        //cenario
        GrupoEstudantil grupo = novoGrupo(novoResponsavel());
        Discente discente = novoDiscente();
        service.adicionarMembro(grupo.getId(), discente.getId());

        //acao
        GrupoEstudantil atualizado = service.definirCargo(grupo.getId(), discente.getId(), CargoGrupo.PRESIDENTE);

        //verificacao
        Assertions.assertEquals(CargoGrupo.PRESIDENTE, atualizado.getMembros().get(0).getCargo());
    }

    @Test
    void deveRegistrarHistoricoAoDefinirCargo() {
        //cenario
        GrupoEstudantil grupo = novoGrupo(novoResponsavel());
        Discente discente = novoDiscente();
        service.adicionarMembro(grupo.getId(), discente.getId());

        //acao
        GrupoEstudantil atualizado = service.definirCargo(grupo.getId(), discente.getId(), CargoGrupo.VICE);

        //verificacao
        // entrada + mudanca de cargo = 2 registros no historico
        Assertions.assertEquals(2, atualizado.getHistoricoCargos().size());
        // primeiro registro (entrada como MEMBRO) deve ter dataFim preenchida
        Assertions.assertNotNull(atualizado.getHistoricoCargos().get(0).getDataFim());
    }

    @Test
    void deveConfirmarQuePresidenteEhLider() {
        //cenario
        GrupoEstudantil grupo = novoGrupo(novoResponsavel());
        Discente discente = novoDiscente();
        service.adicionarMembro(grupo.getId(), discente.getId());
        service.definirCargo(grupo.getId(), discente.getId(), CargoGrupo.PRESIDENTE);

        //acao e verificacao
        Assertions.assertTrue(service.isLider(discente.getId()));
    }

    @Test
    void deveConfirmarQueMembroComumNaoEhLider() {
        //cenario
        GrupoEstudantil grupo = novoGrupo(novoResponsavel());
        Discente discente = novoDiscente();
        service.adicionarMembro(grupo.getId(), discente.getId());

        //acao e verificacao
        Assertions.assertFalse(service.isLider(discente.getId()));
    }
}
