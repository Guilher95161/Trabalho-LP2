package br.ufma.extensao.controller;

import br.ufma.extensao.model.Discente;
import br.ufma.extensao.model.SolicitacaoGrupoEstudantil;
import br.ufma.extensao.model.Usuario;
import br.ufma.extensao.model.dto.SolicitacaoGrupoEstudantilDTO;
import br.ufma.extensao.model.dto.SolicitacaoGrupoEstudantilResponse;
import br.ufma.extensao.model.enums.StatusSolicitacao;
import br.ufma.extensao.service.SolicitacaoGrupoEstudantilService;
import br.ufma.extensao.service.exceptions.RegraNegocioRunTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
            return new ResponseEntity(SolicitacaoGrupoEstudantilResponse.from(salvo), HttpStatus.CREATED);
        } catch (RegraNegocioRunTime e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity atualizar(@PathVariable Integer id, @RequestBody SolicitacaoGrupoEstudantilDTO dto) {
        SolicitacaoGrupoEstudantil solicitacao = montar(id, dto);
        try {
            return ResponseEntity.ok(SolicitacaoGrupoEstudantilResponse.from(service.atualizar(solicitacao)));
        } catch (RegraNegocioRunTime e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity remover(@PathVariable Integer id) {
        try {
            service.remover(id);
            return ResponseEntity.noContent().build();
        } catch (RegraNegocioRunTime e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("{id}")
    public ResponseEntity buscarPorId(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(SolicitacaoGrupoEstudantilResponse.from(service.buscarPorId(id)));
        } catch (RegraNegocioRunTime e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/obter")
    public ResponseEntity buscar(@RequestParam(value = "status", required = false) String status) {
        SolicitacaoGrupoEstudantil filtro = new SolicitacaoGrupoEstudantil();
        if (status != null)
            filtro.setStatus(StatusSolicitacao.valueOf(status));
        return ResponseEntity.ok(SolicitacaoGrupoEstudantilResponse.fromList(service.buscar(filtro)));
    }

    @GetMapping("/pendentes")
    @PreAuthorize("hasRole('COORDENADOR')")
    public ResponseEntity listarPendentes() {
        return ResponseEntity.ok(SolicitacaoGrupoEstudantilResponse.fromList(service.listarPendentes()));
    }

    @PostMapping("{id}/avaliar")
    @PreAuthorize("hasRole('COORDENADOR')")
    public ResponseEntity avaliar(@PathVariable Integer id, @RequestParam boolean aprovado) {
        try {
            return ResponseEntity.ok(SolicitacaoGrupoEstudantilResponse.from(service.avaliar(id, aprovado)));
        } catch (RegraNegocioRunTime e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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
