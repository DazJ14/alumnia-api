package io.github.dazj14.alumnia_api.controller;

import io.github.dazj14.alumnia_api.dto.AlumnoInscritoDto;
import io.github.dazj14.alumnia_api.dto.GrupoAsignadoDto;
import io.github.dazj14.alumnia_api.dto.ActividadDto;
import io.github.dazj14.alumnia_api.dto.CreateActividadRequest;
import io.github.dazj14.alumnia_api.service.ProfesorService;
import io.github.dazj14.alumnia_api.dto.AsignarCalificacionRequest;
import io.github.dazj14.alumnia_api.dto.CalificacionDto;
import io.github.dazj14.alumnia_api.model.CalificacionActividad;
import io.github.dazj14.alumnia_api.dto.UpdateActividadRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/profesor")
@RequiredArgsConstructor
@Tag(name = "3. Profesor Management", description = "Endpoints para operaciones del rol de Profesor.")
public class ProfesorController {

    private final ProfesorService profesorService;

    @Operation(summary = "Obtener los grupos asignados al profesor autenticado",
            description = "Devuelve una lista de los grupos (clases) que el profesor está impartiendo en el periodo académico actual.")
    @GetMapping("/me/grupos")
    @PreAuthorize("hasRole('MAESTRO')")
    public ResponseEntity<List<GrupoAsignadoDto>> getMisGrupos(Principal principal) {
        String profesorCorreo = principal.getName();
        return ResponseEntity.ok(profesorService.findGruposAsignados(profesorCorreo));
    }

    @Operation(summary = "Obtener la lista de alumnos de un grupo específico",
            description = "Devuelve todos los alumnos que están inscritos en un grupo. Se debe proporcionar el ID del grupo.")
    @GetMapping("/grupos/{idGrupo}/alumnos")
    @PreAuthorize("hasRole('MAESTRO')")
    public ResponseEntity<List<AlumnoInscritoDto>> getAlumnosDeGrupo(@PathVariable Integer idGrupo) {
        return ResponseEntity.ok(profesorService.findAlumnosPorGrupo(idGrupo));
    }

    @Operation(summary = "Obtener todas las actividades de un grupo",
            description = "Devuelve una lista de todas las actividades (tareas, exámenes, etc.) que el profesor ha creado para un grupo específico.")
    @GetMapping("/grupos/{idGrupo}/actividades")
    @PreAuthorize("hasRole('MAESTRO')")
    public ResponseEntity<List<ActividadDto>> getActividadesDeGrupo(@PathVariable Integer idGrupo, Principal principal) {
        var actividades = profesorService.findActividadesPorGrupo(idGrupo, principal.getName());
        return ResponseEntity.ok(actividades);
    }

    @Operation(summary = "Crear una nueva actividad para un grupo",
            description = "Permite al profesor crear una nueva actividad evaluable. El profesor autenticado debe ser el titular del grupo.")
    @PostMapping("/grupos/{idGrupo}/actividades")
    @PreAuthorize("hasRole('MAESTRO')")
    public ResponseEntity<ActividadDto> createActividad(@PathVariable Integer idGrupo,
                                                        @RequestBody CreateActividadRequest request,
                                                        Principal principal) {
        var nuevaActividad = profesorService.createActividad(request, idGrupo, principal.getName());
        return new ResponseEntity<>(nuevaActividad, HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener calificaciones de una actividad",
            description = "Permite al profesor obtener todas las calificaciones de los alumnos en cierta actividad.")
    @GetMapping("/actividades/{idActividad}/calificaciones")
    @PreAuthorize("hasRole('MAESTRO')")
    public ResponseEntity<List<CalificacionDto>> getCalificacionesDeActividad(@PathVariable Integer idActividad, Principal principal) {
        var calificaciones = profesorService.findCalificacionesPorActividad(idActividad, principal.getName());
        return ResponseEntity.ok(calificaciones);
    }

    @Operation(summary = "Asignar calificacion",
            description = "Permite al maestro asignar una calificacion en la actividad.")
    @PostMapping("/actividades/{idActividad}/calificaciones")
    @PreAuthorize("hasRole('MAESTRO')")
    public ResponseEntity<CalificacionActividad> asignarCalificacion(@PathVariable Integer idActividad,
                                                                     @RequestBody AsignarCalificacionRequest request,
                                                                     Principal principal) {
        var calificacionGuardada = profesorService.asignarCalificacion(request, idActividad, principal.getName());
        return ResponseEntity.ok(calificacionGuardada);
    }

    @Operation(summary = "Actualizar una actividad existente",
            description = "Permite al profesor modificar el título, descripción o calificación máxima de una actividad que haya creado previamente.")
    @PutMapping("/actividades/{idActividad}")
    @PreAuthorize("hasRole('MAESTRO')")
    public ResponseEntity<ActividadDto> updateActividad(@PathVariable Integer idActividad,
                                                        @RequestBody UpdateActividadRequest request,
                                                        Principal principal) {
        var actividadActualizada = profesorService.updateActividad(idActividad, request, principal.getName());
        return ResponseEntity.ok(actividadActualizada);
    }

    @Operation(summary = "Borrar una actividad",
            description = "Elimina una actividad. Importante: no se puede borrar si ya tiene calificaciones asignadas a alumnos.")
    @DeleteMapping("/actividades/{idActividad}")
    @PreAuthorize("hasRole('MAESTRO')")
    public ResponseEntity<Void> deleteActividad(@PathVariable Integer idActividad, Principal principal) {
        profesorService.deleteActividad(idActividad, principal.getName());
        return ResponseEntity.noContent().build();
    }
}