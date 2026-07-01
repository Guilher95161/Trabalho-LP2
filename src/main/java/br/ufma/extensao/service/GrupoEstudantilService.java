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

    /**
     * Salva um novo grupo estudantil, validando os campos obrigatorios.
     * @param grupo grupo a ser cadastrado
     * @return o grupo salvo (com id gerado)
     * @throws RegraNegocioRunTime se o nome ou o responsavel nao forem informados
     */
    @Transactional
    public GrupoEstudantil salvar(GrupoEstudantil grupo) {
        verificarGrupo(grupo);
        return repository.save(grupo);
    }

    /**
     * Atualiza os campos informados de um grupo existente (so altera o que vier preenchido).
     * @param patch grupo com o id e os campos a atualizar
     * @return o grupo atualizado
     * @throws RegraNegocioRunTime se o id nao for informado
     * @throws EntidadeNaoEncontradaException se nao existir grupo com esse id
     */
    @Transactional
    public GrupoEstudantil atualizar(GrupoEstudantil patch) {
        verificarId(patch);
        GrupoEstudantil existente = buscarGrupo(patch.getId());
        if (patch.getNome() != null && !patch.getNome().isBlank())
            existente.setNome(patch.getNome());
        if (patch.getDescricao() != null)
            existente.setDescricao(patch.getDescricao());
        if (patch.getResponsavel() != null)
            existente.setResponsavel(patch.getResponsavel());
        return repository.save(existente);
    }

    /**
     * Remove o grupo informado.
     * @param grupo grupo a remover (precisa ter id)
     * @throws RegraNegocioRunTime se o id nao for informado
     */
    public void remover(GrupoEstudantil grupo) {
        verificarId(grupo);
        repository.delete(grupo);
    }

    /**
     * Remove o grupo pelo id.
     * @param id id do grupo a remover
     * @throws EntidadeNaoEncontradaException se nao existir grupo com esse id
     */
    public void remover(Integer id) {
        remover(buscarGrupo(id));
    }

    // ===== Gerencia de membros e cargos =====

    /**
     * Adiciona um discente como membro do grupo (cargo inicial MEMBRO) e registra no historico.
     * @param grupoId id do grupo
     * @param discenteId id do discente a adicionar
     * @return o grupo com o novo membro
     * @throws EntidadeNaoEncontradaException se o grupo ou o discente nao existirem
     * @throws OperacaoInvalidaException se o discente ja for membro do grupo
     */
    @Transactional
    public GrupoEstudantil adicionarMembro(Integer grupoId, Integer discenteId) {
        GrupoEstudantil grupo = buscarGrupo(grupoId);
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

    /**
     * Remove um membro do grupo, encerrando o cargo atual dele no historico.
     * @param grupoId id do grupo
     * @param discenteId id do discente a remover
     * @return o grupo sem o membro
     * @throws EntidadeNaoEncontradaException se o grupo ou o discente nao existirem
     * @throws OperacaoInvalidaException se o discente nao for membro do grupo
     */
    @Transactional
    public GrupoEstudantil removerMembro(Integer grupoId, Integer discenteId) {
        GrupoEstudantil grupo = buscarGrupo(grupoId);
        Discente discente = buscarDiscente(discenteId);
        MembroGrupo membro = encontrarMembro(grupo, discente);
        if (membro == null)
            throw new OperacaoInvalidaException("Discente nao e membro do grupo.");
        encerrarCargoAtual(grupo, discente);
        grupo.getMembros().remove(membro);
        return repository.save(grupo);
    }

    /**
     * Define o cargo de um membro, fechando o cargo atual no historico e abrindo um novo.
     * @param grupoId id do grupo
     * @param discenteId id do discente (deve ser membro)
     * @param cargo novo cargo do membro
     * @return o grupo com o cargo atualizado
     * @throws EntidadeNaoEncontradaException se o grupo ou o discente nao existirem
     * @throws OperacaoInvalidaException se o discente nao for membro do grupo
     */
    @Transactional
    public GrupoEstudantil definirCargo(Integer grupoId, Integer discenteId, CargoGrupo cargo) {
        GrupoEstudantil grupo = buscarGrupo(grupoId);
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

    /**
     * Retorna o cargo atual de um discente no grupo.
     * @param grupoId id do grupo
     * @param discenteId id do discente
     * @return o cargo do membro, ou null se o discente nao for membro do grupo
     * @throws EntidadeNaoEncontradaException se o grupo ou o discente nao existirem
     */
    public CargoGrupo getCargo(Integer grupoId, Integer discenteId) {
        GrupoEstudantil grupo = buscarGrupo(grupoId);
        Discente discente = buscarDiscente(discenteId);
        MembroGrupo membro = encontrarMembro(grupo, discente);
        return (membro != null) ? membro.getCargo() : null;
    }

    /**
     * Indica se o discente e lider (tem cargo diferente de MEMBRO) em algum grupo.
     * @param discenteId id do discente
     * @return true se ocupar um cargo de lideranca em pelo menos um grupo
     * @throws EntidadeNaoEncontradaException se o discente nao existir
     */
    public boolean isLider(Integer discenteId) {
        Discente discente = buscarDiscente(discenteId);
        for (GrupoEstudantil grupo : repository.findAll()) {
            MembroGrupo membro = encontrarMembro(grupo, discente);
            if (membro != null && membro.getCargo() != CargoGrupo.MEMBRO) return true;
        }
        return false;
    }

    // ===== Consultas =====

    /**
     * Lista os grupos em que o usuario participa (como responsavel ou como membro).
     * @param usuarioId id do usuario
     * @return os grupos ligados a esse usuario
     */
    public List<GrupoEstudantil> listarPorUsuario(Integer usuarioId) {
        return repository.findByUsuario(usuarioId);
    }

    /**
     * Busca grupos usando o filtro como exemplo (contains e ignorando maiusculas/minusculas).
     * @param filtro grupo com os campos que servem de criterio de busca
     * @return a lista de grupos que casam com o filtro
     */
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

    /**
     * Busca um grupo pelo id.
     * @param id id do grupo
     * @return o grupo encontrado
     * @throws EntidadeNaoEncontradaException se nao existir grupo com esse id
     */
    public GrupoEstudantil buscarPorId(Integer id) {
        return buscarGrupo(id);
    }

    private GrupoEstudantil buscarGrupo(Integer id) {
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

    private void verificarGrupo(GrupoEstudantil grupo) {
        if (grupo == null)
            throw new RegraNegocioRunTime("Um grupo valido deve ser informado.");
        if ((grupo.getNome() == null) || (grupo.getNome().isBlank()))
            throw new RegraNegocioRunTime("Nome do grupo deve ser informado.");
        if (grupo.getResponsavel() == null)
            throw new RegraNegocioRunTime("Responsavel do grupo deve ser informado.");
    }
}
