package br.ufma.extensao.service;

import br.ufma.extensao.model.Discente;
import br.ufma.extensao.repo.DiscenteRepository;
import br.ufma.extensao.service.exceptions.EntidadeNaoEncontradaException;
import br.ufma.extensao.service.exceptions.RegraNegocioRunTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DiscenteService {

    @Autowired
    DiscenteRepository repository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Transactional
    public Discente salvar(Discente discente) {
        verificaDiscente(discente);
        if (discente.getSenha() != null && !discente.getSenha().trim().isEmpty())
            discente.setSenha(passwordEncoder.encode(discente.getSenha()));
        return repository.save(discente);
    }

    @Transactional
    public Discente atualizar(Discente patch) {
        verificarId(patch);
        Discente existente = repository.findById(patch.getId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Discente nao encontrado."));
        if (patch.getNome() != null && !patch.getNome().isBlank())
            existente.setNome(patch.getNome());
        if (patch.getEmail() != null && !patch.getEmail().isBlank())
            existente.setEmail(patch.getEmail());
        if (patch.getMatricula() != null)
            existente.setMatricula(patch.getMatricula());
        if (patch.getSenha() != null && !patch.getSenha().isBlank())
            existente.setSenha(passwordEncoder.encode(patch.getSenha()));
        if (patch.getCurso() != null)
            existente.setCurso(patch.getCurso());
        if (patch.getHorasCumpridas() > 0)
            existente.setHorasCumpridas(patch.getHorasCumpridas());
        return repository.save(existente);
    }

    public void remover(Discente discente) {
        verificarId(discente);
        repository.delete(discente);
    }

    public void remover(Integer id) {
        Discente discente = repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Discente nao encontrado."));
        remover(discente);
    }

    public List<Discente> buscar(Discente filtro) {
        Example<Discente> example = Example.of(filtro,
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withIgnorePaths("ativo", "horasCumpridas")
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
        return repository.findAll(example);
    }

    // painel de progresso: horas cumpridas x meta do curso (carga horaria da UCE)
    public Map<String, Object> painelHoras(Integer id) {
        Discente discente = repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Discente nao encontrado."));
        int horas = discente.getHorasCumpridas();
        int meta = (discente.getCurso() != null && discente.getCurso().getCargaHoraria() != null)
                ? discente.getCurso().getCargaHoraria() : 0;
        int restantes = Math.max(0, meta - horas);
        double percentual = (meta > 0) ? Math.min(100.0, horas * 100.0 / meta) : 0.0;
        Map<String, Object> painel = new LinkedHashMap<>();
        painel.put("discenteId", discente.getId());
        painel.put("nome", discente.getNome());
        painel.put("horasCumpridas", horas);
        painel.put("metaHoras", meta);
        painel.put("horasRestantes", restantes);
        painel.put("percentualConcluido", percentual);
        painel.put("concluido", meta > 0 && horas >= meta);
        return painel;
    }

    public Discente buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Discente nao encontrado."));
    }

    private void verificarId(Discente discente) {
        if ((discente == null) || (discente.getId() == null))
            throw new RegraNegocioRunTime("Discente invalido (sem id).");
    }

    private void verificaDiscente(Discente discente) {
        if (discente == null)
            throw new RegraNegocioRunTime("Um discente valido deve ser informado.");
        if ((discente.getNome() == null) || (discente.getNome().trim().equals("")))
            throw new RegraNegocioRunTime("Nome do discente deve ser informado.");
        if ((discente.getEmail() == null) || (discente.getEmail().trim().equals("")))
            throw new RegraNegocioRunTime("Email do discente deve ser informado.");
    }
}
