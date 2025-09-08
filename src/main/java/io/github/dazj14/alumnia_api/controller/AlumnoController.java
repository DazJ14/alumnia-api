package io.github.dazj14.alumnia_api.controller;

import io.github.dazj14.alumnia_api.dto.KardexDto;
import io.github.dazj14.alumnia_api.dto.MateriaInscritaDto;
import io.github.dazj14.alumnia_api.service.AlumnoService;
import io.github.dazj14.alumnia_api.dto.DesgloseCalificacionesDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/alumno")
@RequiredArgsConstructor
public class AlumnoController {

    private final AlumnoService alumnoService;

    @GetMapping("/me/materias-inscritas")
    @PreAuthorize("hasRole('ALUMNO')")
    public ResponseEntity<List<MateriaInscritaDto>> getMisMaterias(Principal principal) {
        var materias = alumnoService.findMateriasInscritasActuales(principal.getName());
        return ResponseEntity.ok(materias);
    }

    @GetMapping("/me/materias-inscritas/{idMateriaInscrita}/calificaciones")
    @PreAuthorize("hasRole('ALUMNO')")
    public ResponseEntity<DesgloseCalificacionesDto> getDesgloseDeMateria(@PathVariable Integer idMateriaInscrita, Principal principal) {
        var desglose = alumnoService.findDesgloseCalificaciones(idMateriaInscrita, principal.getName());
        return ResponseEntity.ok(desglose);
    }

    @GetMapping("/me/kardex")
    @PreAuthorize("hasRole('ALUMNO')")
    public ResponseEntity<KardexDto> getMiKardex(Principal principal) {
        var kardex = alumnoService.findKardex(principal.getName());
        return ResponseEntity.ok(kardex);
    }
}