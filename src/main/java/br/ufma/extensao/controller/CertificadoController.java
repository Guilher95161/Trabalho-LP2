package br.ufma.extensao.controller;

import br.ufma.extensao.model.Certificado;
import br.ufma.extensao.model.dto.CertificadoDTO;
import br.ufma.extensao.service.CertificadoService;
import br.ufma.extensao.service.exceptions.SistemaExtensaoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/certificados")
public class CertificadoController {

    @Autowired
    CertificadoService service;

    @PostMapping
    public ResponseEntity salvar(@RequestBody CertificadoDTO dto) {
        Certificado certificado = montar(null, dto);
        try {
            Certificado salvo = service.salvar(certificado);
            return new ResponseEntity(salvo, HttpStatus.CREATED);
        } catch (SistemaExtensaoException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable Integer id, @RequestBody CertificadoDTO dto) {
        Certificado certificado = montar(id, dto);
        try {
            return ResponseEntity.ok(service.atualizar(certificado));
        } catch (SistemaExtensaoException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity remover(@PathVariable Integer id) {
        try {
            service.remover(id);
            return ResponseEntity.noContent().build();
        } catch (SistemaExtensaoException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/obter")
    public ResponseEntity buscar(@RequestParam(value = "tituloAtividade", required = false) String tituloAtividade) {
        Certificado filtro = new Certificado();
        filtro.setTituloAtividade(tituloAtividade);
        return ResponseEntity.ok(service.buscar(filtro));
    }

    private Certificado montar(Integer id, CertificadoDTO dto) {
        Certificado certificado = new Certificado();
        certificado.setId(id);
        certificado.setTituloAtividade(dto.getTituloAtividade());
        if (dto.getCargaHoraria() != null)
            certificado.setCargaHoraria(dto.getCargaHoraria());
        certificado.setData(dto.getData());
        certificado.setAproveitamentoSolicitado(dto.isAproveitamentoSolicitado());
        return certificado;
    }
}
