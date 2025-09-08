package io.github.dazj14.alumnia_api.controller;

import io.github.dazj14.alumnia_api.dto.GrupoDisponibleDto;
import io.github.dazj14.alumnia_api.service.InscripcionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.github.dazj14.alumnia_api.dto.InscribirGrupoRequest;
import io.github.dazj14.alumnia_api.model.MateriaInscrita;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.security.Principal;

import java.util.List;

@RestController
@RequestMapping("/api/inscripcion")
@RequiredArgsConstructor
public class InscripcionController {

    private final InscripcionService inscripcionService;

    @GetMapping("/grupos-disponibles")
    @PreAuthorize("hasRole('ALUMNO')")
    public ResponseEntity<List<GrupoDisponibleDto>> getGruposDisponibles() {
        return ResponseEntity.ok(inscripcionService.findGruposDisponibles());
    }

    @PostMapping("/inscribir")
    @PreAuthorize("hasRole('ALUMNO')")
    public ResponseEntity<MateriaInscrita> inscribirGrupo(@RequestBody InscribirGrupoRequest request, Principal principal) {
        // Obtenemos el correo del usuario logueado (el 'name' en el token JWT)
        String alumnoCorreo = principal.getName();
        var nuevaInscripcion = inscripcionService.inscribirGrupo(request, alumnoCorreo);
        return new ResponseEntity<>(nuevaInscripcion, HttpStatus.CREATED);
    }
}