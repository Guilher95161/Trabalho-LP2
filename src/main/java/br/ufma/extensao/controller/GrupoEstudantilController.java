package br.ufma.extensao.controller;

import br.ufma.extensao.model.GrupoEstudantil;
import br.ufma.extensao.model.Usuario;
import br.ufma.extensao.model.dto.GrupoEstudantilDTO;
import br.ufma.extensao.model.dto.GrupoEstudantilResponse;
import br.ufma.extensao.model.enums.CargoGrupo;
import br.ufma.extensao.service.GrupoEstudantilService;
import br.ufma.extensao.service.exceptions.RegraNegocioRunTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/grupos")
public class GrupoEstudantilController {

    @Autowired
    GrupoEstudantilService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('DOCENTE', 'COORDENADOR', 'ADMINISTRADOR')")
    public ResponseEntity salvar(@RequestBody GrupoEstudantilDTO dto) {
        GrupoEstudantil grupo = montar(null, dto);
        try {
            GrupoEstudantil salvo = service.salvar(grupo);
            return new ResponseEntity(GrupoEstudantilResponse.from(salvo), HttpStatus.CREATED);
        } catch (RegraNegocioRunTime e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAnyRole('DOCENTE', 'COORDENADOR')")
    public ResponseEntity atualizar(@PathVariable Integer id, @RequestBody GrupoEstudantilDTO dto) {
        GrupoEstudantil grupo = montar(id, dto);
        try {
            return ResponseEntity.ok(GrupoEstudantilResponse.from(service.atualizar(grupo)));
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
            return ResponseEntity.ok(GrupoEstudantilResponse.from(service.buscarPorId(id)));
        } catch (RegraNegocioRunTime e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/obter")
    public ResponseEntity buscar(@RequestParam(value = "nome", required = false) String nome) {
        GrupoEstudantil filtro = new GrupoEstudantil();
        filtro.setNome(nome);
        return ResponseEntity.ok(GrupoEstudantilResponse.fromList(service.buscar(filtro)));
    }

    @GetMapping("/por-usuario/{usuarioId}")
    public ResponseEntity listarPorUsuario(@PathVariable Integer usuarioId) {
        return ResponseEntity.ok(GrupoEstudantilResponse.fromList(service.listarPorUsuario(usuarioId)));
    }

    // ===== Membros e cargos =====

    @PostMapping("{id}/membros")
    @PreAuthorize("hasAnyRole('DOCENTE', 'COORDENADOR')")
    public ResponseEntity adicionarMembro(@PathVariable Integer id, @RequestParam Integer discenteId) {
        try {
            return ResponseEntity.ok(GrupoEstudantilResponse.from(service.adicionarMembro(id, discenteId)));
        } catch (RegraNegocioRunTime e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("{id}/membros/{discenteId}")
    @PreAuthorize("hasAnyRole('DOCENTE', 'COORDENADOR')")
    public ResponseEntity removerMembro(@PathVariable Integer id, @PathVariable Integer discenteId) {
        try {
            return ResponseEntity.ok(GrupoEstudantilResponse.from(service.removerMembro(id, discenteId)));
        } catch (RegraNegocioRunTime e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}/membros/{discenteId}/cargo")
    @PreAuthorize("hasAnyRole('DOCENTE', 'COORDENADOR')")
    public ResponseEntity definirCargo(@PathVariable Integer id, @PathVariable Integer discenteId,
                                       @RequestParam CargoGrupo cargo) {
        try {
            return ResponseEntity.ok(GrupoEstudantilResponse.from(service.definirCargo(id, discenteId, cargo)));
        } catch (RegraNegocioRunTime e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/discentes/{discenteId}/lider")
    public ResponseEntity isLider(@PathVariable Integer discenteId) {
        try {
            return ResponseEntity.ok(service.isLider(discenteId));
        } catch (RegraNegocioRunTime e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private GrupoEstudantil montar(Integer id, GrupoEstudantilDTO dto) {
        GrupoEstudantil grupo = new GrupoEstudantil();
        grupo.setId(id);
        grupo.setNome(dto.getNome());
        grupo.setDescricao(dto.getDescricao());
        if (dto.getResponsavelId() != null) {
            Usuario responsavel = new Usuario();
            responsavel.setId(dto.getResponsavelId());
            grupo.setResponsavel(responsavel);
        }
        return grupo;
    }
}
