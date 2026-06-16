package br.ufma.extensao.controller;

import br.ufma.extensao.model.Discente;
import br.ufma.extensao.model.SolicitacaoGrupoEstudantil;
import br.ufma.extensao.model.Usuario;
import br.ufma.extensao.model.dto.SolicitacaoGrupoEstudantilDTO;
import br.ufma.extensao.model.enums.StatusSolicitacao;
import br.ufma.extensao.service.SolicitacaoGrupoEstudantilService;
import br.ufma.extensao.service.exceptions.SistemaExtensaoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/solicitacoes-grupo")
public class SolicitacaoGrupoEstudantilController {

    @Autowired
    SolicitacaoGrupoEstudantilService service;

    @PostMapping
    public ResponseEntity salvar(@RequestBody SolicitacaoGrupoEstudantilDTO dto) {
        SolicitacaoGrupoEstudantil solicitacao = montar(null, dto);
        try {
            SolicitacaoGrupoEstudantil salvo = service.salvar(solicitacao);
            return new ResponseEntity(salvo, HttpStatus.CREATED);
        } catch (SistemaExtensaoException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable Integer id, @RequestBody SolicitacaoGrupoEstudantilDTO dto) {
        SolicitacaoGrupoEstudantil solicitacao = montar(id, dto);
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
        SolicitacaoGrupoEstudantil filtro = new SolicitacaoGrupoEstudantil();
        if (status != null)
            filtro.setStatus(StatusSolicitacao.valueOf(status));
        return ResponseEntity.ok(service.buscar(filtro));
    }

    private SolicitacaoGrupoEstudantil montar(Integer id, SolicitacaoGrupoEstudantilDTO dto) {
        SolicitacaoGrupoEstudantil solicitacao = new SolicitacaoGrupoEstudantil();
        solicitacao.setId(id);
        solicitacao.setNomeGrupo(dto.getNomeGrupo());
        solicitacao.setDescricao(dto.getDescricao());
        if (dto.getStatus() != null)
            solicitacao.setStatus(StatusSolicitacao.valueOf(dto.getStatus()));
        if (dto.getSolicitanteId() != null) {
            Discente solicitante = new Discente();
            solicitante.setId(dto.getSolicitanteId());
            solicitacao.setSolicitante(solicitante);
        }
        if (dto.getDocenteResponsavelId() != null) {
            Usuario docente = new Usuario();
            docente.setId(dto.getDocenteResponsavelId());
            solicitacao.setDocenteResponsavel(docente);
        }
        return solicitacao;
    }
}
