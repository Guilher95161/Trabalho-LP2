package br.ufma.extensao.service;

import br.ufma.extensao.model.Discente;
import br.ufma.extensao.model.GrupoEstudantil;
import br.ufma.extensao.model.Usuario;
import br.ufma.extensao.model.enums.CargoGrupo;
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
    void salvar_devePersistirGrupo() {
        GrupoEstudantil salvo = novoGrupo(novoResponsavel());
        assertThat(salvo.getId()).isNotNull();
    }

    @Test
    void adicionarMembro_deveEntrarComoMembro() {
        GrupoEstudantil grupo = novoGrupo(novoResponsavel());
        Discente discente = novoDiscente();
        GrupoEstudantil atualizado = service.adicionarMembro(grupo.getId(), discente.getId());
        assertThat(atualizado.getMembros()).hasSize(1);
        assertThat(atualizado.getMembros().get(0).getCargo()).isEqualTo(CargoGrupo.MEMBRO);
    }

    @Test
    void adicionarMembro_deveLancarExcecaoSeJaMembro() {
        GrupoEstudantil grupo = novoGrupo(novoResponsavel());
        Discente discente = novoDiscente();
        service.adicionarMembro(grupo.getId(), discente.getId());
        assertThatThrownBy(() -> service.adicionarMembro(grupo.getId(), discente.getId()))
                .isInstanceOf(OperacaoInvalidaException.class);
    }

    @Test
    void removerMembro_deveRetirarDoGrupo() {
        GrupoEstudantil grupo = novoGrupo(novoResponsavel());
        Discente discente = novoDiscente();
        service.adicionarMembro(grupo.getId(), discente.getId());
        GrupoEstudantil atualizado = service.removerMembro(grupo.getId(), discente.getId());
        assertThat(atualizado.getMembros()).isEmpty();
    }

    @Test
    void definirCargo_deveAlterarCargoDoMembro() {
        GrupoEstudantil grupo = novoGrupo(novoResponsavel());
        Discente discente = novoDiscente();
        service.adicionarMembro(grupo.getId(), discente.getId());
        GrupoEstudantil atualizado = service.definirCargo(grupo.getId(), discente.getId(), CargoGrupo.PRESIDENTE);
        assertThat(atualizado.getMembros().get(0).getCargo()).isEqualTo(CargoGrupo.PRESIDENTE);
    }

    @Test
    void definirCargo_deveRegistrarHistorico() {
        GrupoEstudantil grupo = novoGrupo(novoResponsavel());
        Discente discente = novoDiscente();
        service.adicionarMembro(grupo.getId(), discente.getId());
        GrupoEstudantil atualizado = service.definirCargo(grupo.getId(), discente.getId(), CargoGrupo.VICE);
        // entrada + mudança de cargo = 2 registros no historico
        assertThat(atualizado.getHistoricoCargos()).hasSize(2);
        // primeiro registro (entrada como MEMBRO) deve ter dataFim preenchida
        assertThat(atualizado.getHistoricoCargos().get(0).getDataFim()).isNotNull();
    }

    @Test
    void isLider_deveRetornarTrueParaPresidente() {
        GrupoEstudantil grupo = novoGrupo(novoResponsavel());
        Discente discente = novoDiscente();
        service.adicionarMembro(grupo.getId(), discente.getId());
        service.definirCargo(grupo.getId(), discente.getId(), CargoGrupo.PRESIDENTE);
        assertThat(service.isLider(discente.getId())).isTrue();
    }

    @Test
    void isLider_deveRetornarFalseParaMembro() {
        GrupoEstudantil grupo = novoGrupo(novoResponsavel());
        Discente discente = novoDiscente();
        service.adicionarMembro(grupo.getId(), discente.getId());
        assertThat(service.isLider(discente.getId())).isFalse();
    }
}
