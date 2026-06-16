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

    @Transactional
    public Certificado salvar(Certificado certificado) {
        verificaCertificado(certificado);
        return repository.save(certificado);
    }

    @Transactional
    public Certificado atualizar(Certificado certificado) {
        verificarId(certificado);
        return repository.save(certificado);
    }

    public void remover(Certificado certificado) {
        verificarId(certificado);
        repository.delete(certificado);
    }

    public void remover(Integer id) {
        Certificado certificado = repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Certificado nao encontrado."));
        remover(certificado);
    }

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

    private void verificaCertificado(Certificado certificado) {
        if (certificado == null)
            throw new RegraNegocioRunTime("Um certificado valido deve ser informado.");
        if ((certificado.getTituloAtividade() == null) || (certificado.getTituloAtividade().trim().equals("")))
            throw new RegraNegocioRunTime("Titulo da atividade deve ser informado.");
        if (certificado.getCargaHoraria() <= 0)
            throw new RegraNegocioRunTime("Carga horaria deve ser maior que zero.");
    }
}
