package br.ufma.extensao.service;

import br.ufma.extensao.model.Certificado;
import br.ufma.extensao.repo.CertificadoRepository;
import br.ufma.extensao.service.exceptions.EntidadeNaoEncontradaException;
import br.ufma.extensao.service.exceptions.RegraNegocioRunTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CertificadoService {

    @Autowired
    CertificadoRepository repository;

    /**
     * Salva um novo certificado, validando os campos obrigatorios.
     * @param certificado certificado a ser cadastrado
     * @return o certificado salvo (com id gerado)
     * @throws RegraNegocioRunTime se o titulo nao for informado ou a carga horaria nao for positiva
     */
    @Transactional
    public Certificado salvar(Certificado certificado) {
        verificarCertificado(certificado);
        return repository.save(certificado);
    }

    /**
     * Atualiza os campos informados de um certificado existente (so altera o que vier preenchido).
     * @param patch certificado com o id e os campos a atualizar
     * @return o certificado atualizado
     * @throws RegraNegocioRunTime se o id nao for informado
     * @throws EntidadeNaoEncontradaException se nao existir certificado com esse id
     */
    @Transactional
    public Certificado atualizar(Certificado patch) {
        verificarId(patch);
        Certificado existente = repository.findById(patch.getId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Certificado nao encontrado."));
        if (patch.getTituloAtividade() != null && !patch.getTituloAtividade().isBlank())
            existente.setTituloAtividade(patch.getTituloAtividade());
        if (patch.getCargaHoraria() > 0)
            existente.setCargaHoraria(patch.getCargaHoraria());
        if (patch.getData() != null)
            existente.setData(patch.getData());
        return repository.save(existente);
    }

    /**
     * Remove o certificado informado.
     * @param certificado certificado a remover (precisa ter id)
     * @throws RegraNegocioRunTime se o id nao for informado
     */
    public void remover(Certificado certificado) {
        verificarId(certificado);
        repository.delete(certificado);
    }

    /**
     * Remove o certificado pelo id.
     * @param id id do certificado a remover
     * @throws EntidadeNaoEncontradaException se nao existir certificado com esse id
     */
    public void remover(Integer id) {
        Certificado certificado = repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Certificado nao encontrado."));
        remover(certificado);
    }

    /**
     * Busca um certificado pelo id.
     * @param id id do certificado
     * @return o certificado encontrado
     * @throws EntidadeNaoEncontradaException se nao existir certificado com esse id
     */
    public Certificado buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Certificado nao encontrado."));
    }

    /**
     * Busca certificados usando o filtro como exemplo (contains e ignorando maiusculas/minusculas).
     * @param filtro certificado com os campos que servem de criterio de busca
     * @return a lista de certificados que casam com o filtro
     */
    public List<Certificado> buscar(Certificado filtro) {
        Example<Certificado> example = Example.of(filtro,
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withIgnorePaths("cargaHoraria", "aproveitamentoSolicitado")
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
        return repository.findAll(example);
    }

    private void verificarId(Certificado certificado) {
        if ((certificado == null) || (certificado.getId() == null))
            throw new RegraNegocioRunTime("Certificado invalido (sem id).");
    }

    private void verificarCertificado(Certificado certificado) {
        if (certificado == null)
            throw new RegraNegocioRunTime("Um certificado valido deve ser informado.");
        if ((certificado.getTituloAtividade() == null) || (certificado.getTituloAtividade().isBlank()))
            throw new RegraNegocioRunTime("Titulo da atividade deve ser informado.");
        if (certificado.getCargaHoraria() <= 0)
            throw new RegraNegocioRunTime("Carga horaria deve ser maior que zero.");
    }
}
