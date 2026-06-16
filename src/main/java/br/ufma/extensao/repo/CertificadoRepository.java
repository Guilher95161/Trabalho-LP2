package br.ufma.extensao.repo;

import br.ufma.extensao.model.Certificado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificadoRepository extends JpaRepository<Certificado, Integer> {
}
