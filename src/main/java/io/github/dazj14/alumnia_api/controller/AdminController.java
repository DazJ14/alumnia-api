package io.github.dazj14.alumnia_api.controller;

import io.github.dazj14.alumnia_api.dto.*;
import io.github.dazj14.alumnia_api.model.Grupo;
import io.github.dazj14.alumnia_api.model.Materia;
import io.github.dazj14.alumnia_api.model.Usuario;
import io.github.dazj14.alumnia_api.service.GrupoService;
import io.github.dazj14.alumnia_api.service.MateriaService;
import io.github.dazj14.alumnia_api.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR')")
@Tag(name = "1. Admin Management", description = "Endpoints para operaciones del rol de Administrador.")
public class AdminController {

    private final MateriaService materiaService;
    private final UsuarioService usuarioService;
    private final GrupoService grupoService;

    @Operation(summary = "Obtener todas las materias creadas.",
            description = "Devuelve una lista de los grupos (clases) que el profesor está impartiendo en el periodo académico actual.")
    @GetMapping("/materias")
    public ResponseEntity<List<Materia>> getAllMaterias() {
        return ResponseEntity.ok(materiaService.findAll());
    }

    @GetMapping("/materias/{id}")
    public ResponseEntity<Materia> getMateriaById(@PathVariable Integer id) {
        return ResponseEntity.ok(materiaService.findById(id));
    }

    @PostMapping("/materias")
    public ResponseEntity<Materia> createMateria(@Valid @RequestBody CreateMateriaRequest request) {
        Materia nuevaMateria = materiaService.create(request);
        return new ResponseEntity<>(nuevaMateria, HttpStatus.CREATED);
    }

    @PutMapping("/materias/{id}")
    public ResponseEntity<Materia> updateMateria(@PathVariable Integer id, @RequestBody UpdateMateriaRequest request) {
        return ResponseEntity.ok(materiaService.update(id, request));
    }

    @DeleteMapping("/materias/{id}")
    public ResponseEntity<Void> deleteMateria(@PathVariable Integer id) {
        materiaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/usuarios")
    public ResponseEntity<CreateUsuarioResponse> createUsuario(@RequestBody CreateUsuarioRequest request) {
        CreateUsuarioResponse nuevoUsuario = usuarioService.create(request);
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioDto>> getAllUsuarios() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioDto> getUsuarioById(@PathVariable Integer id) {
        return ResponseEntity.ok(usuarioService.findById(id));
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioDto> updateUsuario(@PathVariable Integer id, @RequestBody UpdateUsuarioRequest request) {
        return ResponseEntity.ok(usuarioService.update(id, request));
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Integer id) {
        usuarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/grupos")
    public ResponseEntity<Grupo> createGrupo(@RequestBody CreateGrupoRequest request) {
        Grupo nuevoGrupo = grupoService.create(request);
        return new ResponseEntity<>(nuevoGrupo, HttpStatus.CREATED);
    }

    @GetMapping("/grupos")
    public ResponseEntity<List<GrupoDto>> getAllGrupos() {
        return ResponseEntity.ok(grupoService.findAll());
    }

    @GetMapping("/grupos/{id}")
    public ResponseEntity<GrupoDto> getGrupoById(@PathVariable Integer id) {
        return ResponseEntity.ok(grupoService.findById(id));
    }

    @PutMapping("/grupos/{id}")
    public ResponseEntity<GrupoDto> updateGrupo(@PathVariable Integer id, @RequestBody UpdateGrupoRequest request) {
        return ResponseEntity.ok(grupoService.update(id, request));
    }

    @DeleteMapping("/grupos/{id}")
    public ResponseEntity<Void> deleteGrupo(@PathVariable Integer id) {
        grupoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}