package br.ufma.extensao.service;

import br.ufma.extensao.model.Discente;
import br.ufma.extensao.model.GrupoEstudantil;
import br.ufma.extensao.model.HistoricoCargo;
import br.ufma.extensao.model.MembroGrupo;
import br.ufma.extensao.model.enums.CargoGrupo;
import br.ufma.extensao.repo.DiscenteRepository;
import br.ufma.extensao.repo.GrupoEstudantilRepository;
import br.ufma.extensao.service.exceptions.EntidadeNaoEncontradaException;
import br.ufma.extensao.service.exceptions.OperacaoInvalidaException;
import br.ufma.extensao.service.exceptions.RegraNegocioRunTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class GrupoEstudantilService {

    @Autowired
    GrupoEstudantilRepository repository;

    @Autowired
    DiscenteRepository discenteRepository;

    @Transactional
    public GrupoEstudantil salvar(GrupoEstudantil grupo) {
        verificaGrupo(grupo);
        return repository.save(grupo);
    }

    @Transactional
    public GrupoEstudantil atualizar(GrupoEstudantil patch) {
        verificarId(patch);
        GrupoEstudantil existente = buscarObrigatoria(patch.getId());
        if (patch.getNome() != null && !patch.getNome().isBlank())
            existente.setNome(patch.getNome());
        if (patch.getDescricao() != null)
            existente.setDescricao(patch.getDescricao());
        if (patch.getResponsavel() != null)
            existente.setResponsavel(patch.getResponsavel());
        return repository.save(existente);
    }

    public void remover(GrupoEstudantil grupo) {
        verificarId(grupo);
        repository.delete(grupo);
    }

    public void remover(Integer id) {
        remover(buscarObrigatoria(id));
    }

    // ===== Gerencia de membros e cargos =====

    @Transactional
    public GrupoEstudantil adicionarMembro(Integer grupoId, Integer discenteId) {
        GrupoEstudantil grupo = buscarObrigatoria(grupoId);
        Discente discente = buscarDiscente(discenteId);
        if (grupo.getMembros() == null) grupo.setMembros(new ArrayList<>());
        if (grupo.getHistoricoCargos() == null) grupo.setHistoricoCargos(new ArrayList<>());
        if (encontrarMembro(grupo, discente) != null)
            throw new OperacaoInvalidaException("Discente ja e membro do grupo.");
        LocalDate hoje = LocalDate.now();
        grupo.getMembros().add(MembroGrupo.builder()
                .discente(discente).cargo(CargoGrupo.MEMBRO).dataEntrada(hoje).build());
        grupo.getHistoricoCargos().add(HistoricoCargo.builder()
                .discente(discente).cargo(CargoGrupo.MEMBRO).dataInicio(hoje).build());
        return repository.save(grupo);
    }

    @Transactional
    public GrupoEstudantil removerMembro(Integer grupoId, Integer discenteId) {
        GrupoEstudantil grupo = buscarObrigatoria(grupoId);
        Discente discente = buscarDiscente(discenteId);
        MembroGrupo membro = encontrarMembro(grupo, discente);
        if (membro == null)
            throw new OperacaoInvalidaException("Discente nao e membro do grupo.");
        encerrarCargoAtual(grupo, discente);
        grupo.getMembros().remove(membro);
        return repository.save(grupo);
    }

    // fecha o cargo atual no historico e abre um novo
    @Transactional
    public GrupoEstudantil definirCargo(Integer grupoId, Integer discenteId, CargoGrupo cargo) {
        GrupoEstudantil grupo = buscarObrigatoria(grupoId);
        Discente discente = buscarDiscente(discenteId);
        MembroGrupo membro = encontrarMembro(grupo, discente);
        if (membro == null)
            throw new OperacaoInvalidaException("Discente nao e membro do grupo.");
        encerrarCargoAtual(grupo, discente);
        membro.setCargo(cargo);
        grupo.getHistoricoCargos().add(HistoricoCargo.builder()
                .discente(discente).cargo(cargo).dataInicio(LocalDate.now()).build());
        return repository.save(grupo);
    }

    public CargoGrupo getCargo(Integer grupoId, Integer discenteId) {
        GrupoEstudantil grupo = buscarObrigatoria(grupoId);
        Discente discente = buscarDiscente(discenteId);
        MembroGrupo membro = encontrarMembro(grupo, discente);
        return (membro != null) ? membro.getCargo() : null;
    }

    // lider = membro com cargo diferente de MEMBRO em algum grupo
    public boolean isLider(Integer discenteId) {
        Discente discente = buscarDiscente(discenteId);
        for (GrupoEstudantil grupo : repository.findAll()) {
            MembroGrupo membro = encontrarMembro(grupo, discente);
            if (membro != null && membro.getCargo() != CargoGrupo.MEMBRO) return true;
        }
        return false;
    }

    // ===== Consultas =====

    public List<GrupoEstudantil> listarPorUsuario(Integer usuarioId) {
        return repository.findByUsuario(usuarioId);
    }

    public List<GrupoEstudantil> buscar(GrupoEstudantil filtro) {
        Example<GrupoEstudantil> example = Example.of(filtro,
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
        return repository.findAll(example);
    }

    private MembroGrupo encontrarMembro(GrupoEstudantil grupo, Discente discente) {
        if (grupo.getMembros() == null) return null;
        for (MembroGrupo m : grupo.getMembros()) {
            if (m.getDiscente() != null && m.getDiscente().equals(discente)) return m;
        }
        return null;
    }

    private void encerrarCargoAtual(GrupoEstudantil grupo, Discente discente) {
        if (grupo.getHistoricoCargos() == null) return;
        for (int i = grupo.getHistoricoCargos().size() - 1; i >= 0; i--) {
            HistoricoCargo h = grupo.getHistoricoCargos().get(i);
            if (h.getDiscente() != null && h.getDiscente().equals(discente) && h.getDataFim() == null) {
                h.setDataFim(LocalDate.now());
                break;
            }
        }
    }

    public GrupoEstudantil buscarPorId(Integer id) {
        return buscarObrigatoria(id);
    }

    private GrupoEstudantil buscarObrigatoria(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Grupo estudantil nao encontrado."));
    }

    private Discente buscarDiscente(Integer id) {
        return discenteRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Discente nao encontrado."));
    }

    private void verificarId(GrupoEstudantil grupo) {
        if ((grupo == null) || (grupo.getId() == null))
            throw new RegraNegocioRunTime("Grupo invalido (sem id).");
    }

    private void verificaGrupo(GrupoEstudantil grupo) {
        if (grupo == null)
            throw new RegraNegocioRunTime("Um grupo valido deve ser informado.");
        if ((grupo.getNome() == null) || (grupo.getNome().trim().equals("")))
            throw new RegraNegocioRunTime("Nome do grupo deve ser informado.");
        if (grupo.getResponsavel() == null)
            throw new RegraNegocioRunTime("Responsavel do grupo deve ser informado.");
    }
}
