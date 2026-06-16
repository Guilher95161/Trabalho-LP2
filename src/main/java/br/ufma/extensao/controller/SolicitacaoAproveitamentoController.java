package br.ufma.extensao.controller;

import br.ufma.extensao.model.Certificado;
import br.ufma.extensao.model.Discente;
import br.ufma.extensao.model.SolicitacaoAproveitamento;
import br.ufma.extensao.model.dto.SolicitacaoAproveitamentoDTO;
import br.ufma.extensao.model.enums.StatusSolicitacao;
import br.ufma.extensao.service.SolicitacaoAproveitamentoService;
import br.ufma.extensao.service.exceptions.SistemaExtensaoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/solicitacoes")
public class SolicitacaoAproveitamentoController {

    @Autowired
    SolicitacaoAproveitamentoService service;

    @PostMapping
    public ResponseEntity salvar(@RequestBody SolicitacaoAproveitamentoDTO dto) {
        SolicitacaoAproveitamento solicitacao = montar(null, dto);
        try {
            SolicitacaoAproveitamento salvo = service.salvar(solicitacao);
            return new ResponseEntity(salvo, HttpStatus.CREATED);
        } catch (SistemaExtensaoException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable Integer id, @RequestBody SolicitacaoAproveitamentoDTO dto) {
        SolicitacaoAproveitamento solicitacao = montar(id, dto);
        try {
            return ResponseEntity.ok(service.atualizar(solicitacao));
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
    public ResponseEntity buscar(@RequestParam(value = "status", required = false) String status) {
        SolicitacaoAproveitamento filtro = new SolicitacaoAproveitamento();
        if (status != null)
            filtro.setStatus(StatusSolicitacao.valueOf(status));
        return ResponseEntity.ok(service.buscar(filtro));
    }

    private SolicitacaoAproveitamento montar(Integer id, SolicitacaoAproveitamentoDTO dto) {
        SolicitacaoAproveitamento solicitacao = new SolicitacaoAproveitamento();
        solicitacao.setId(id);
        solicitacao.setParecer(dto.getParecer());
        solicitacao.setDelegadaParaComissao(dto.isDelegadaParaComissao());
        solicitacao.setDataCriacao(dto.getDataCriacao());
        solicitacao.setDataAvaliacao(dto.getDataAvaliacao());
        if (dto.getStatus() != null)
            solicitacao.setStatus(StatusSolicitacao.valueOf(dto.getStatus()));
        if (dto.getSolicitanteId() != null) {
            Discente solicitante = new Discente();
            solicitante.setId(dto.getSolicitanteId());
            solicitacao.setSolicitante(solicitante);
        }
        if (dto.getCertificadoId() != null) {
            Certificado certificado = new Certificado();
            certificado.setId(dto.getCertificadoId());
            solicitacao.setCertificado(certificado);
        }
        return solicitacao;
    }
}
