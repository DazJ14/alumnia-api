package io.github.dazj14.alumnia_api.service;

import io.github.dazj14.alumnia_api.dto.AlumnoInscritoDto;
import io.github.dazj14.alumnia_api.dto.CreateActividadRequest;
import io.github.dazj14.alumnia_api.dto.GrupoAsignadoDto;
import io.github.dazj14.alumnia_api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.github.dazj14.alumnia_api.dto.ActividadDto;
import io.github.dazj14.alumnia_api.model.Actividad;
import io.github.dazj14.alumnia_api.model.Grupo;
import io.github.dazj14.alumnia_api.model.Profesor;
import io.github.dazj14.alumnia_api.dto.AsignarCalificacionRequest;
import io.github.dazj14.alumnia_api.dto.CalificacionDto;
import io.github.dazj14.alumnia_api.model.CalificacionActividad;
import io.github.dazj14.alumnia_api.dto.UpdateActividadRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfesorService {

    private final ProfesorRepository profesorRepository;
    private final GrupoRepository grupoRepository;
    private final PeriodoRepository periodoRepository;
    private final MateriaInscritaRepository materiaInscritaRepository;
    private final ActividadRepository actividadRepository;
    private final CalificacionActividadRepository calificacionActividadRepository; // Añadir

    @Transactional(readOnly = true)
    public List<GrupoAsignadoDto> findGruposAsignados(String profesorCorreo) {
        System.out.println(profesorRepository.findByCorreo(profesorCorreo));
        var profesor = profesorRepository.findByCorreo(profesorCorreo)
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado."));
        var periodoActivo = periodoRepository.findTopByOrderByFechaInicioDesc()
                .orElseThrow(() -> new RuntimeException("No hay un periodo activo configurado."));

        return grupoRepository.findByProfesorAndPeriodo(profesor, periodoActivo).stream()
                .map(grupo -> GrupoAsignadoDto.builder()
                        .idGrupo(grupo.getId())
                        .codigoGrupo(grupo.getCodigoGrupo())
                        .nombreMateria(grupo.getMateria().getNombreMateria())
                        .horario(grupo.getHorario().getNombreHorario())
                        .salon(grupo.getSalon().getNombreSalon())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AlumnoInscritoDto> findAlumnosPorGrupo(Integer idGrupo) {
        var grupo = grupoRepository.findById(idGrupo)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado."));

        return materiaInscritaRepository.findByGrupo(grupo).stream()
                .map(inscripcion -> {
                    var alumno = inscripcion.getAlumno();
                    return AlumnoInscritoDto.builder()
                            .idUsuario(alumno.getId())
                            .nombreCompleto(alumno.getNombre() + " " + alumno.getApellido())
                            .matricula(alumno.getMatricula())
                            .correo(alumno.getCorreo())
                            .build();
                })
                .collect(Collectors.toList());
    }

    public ActividadDto createActividad(CreateActividadRequest request, Integer idGrupo, String profesorCorreo) {
        var profesor = getProfesorFromCorreo(profesorCorreo);
        var grupo = getGrupoFromId(idGrupo);

        validarProfesorDelGrupo(profesor, grupo);

        Actividad nuevaActividad = new Actividad();
        nuevaActividad.setGrupo(grupo);
        nuevaActividad.setTitulo(request.getTitulo());
        nuevaActividad.setDescripcion(request.getDescripcion());
        nuevaActividad.setPonderacion(request.getPonderacion());

        var actividadGuardada = actividadRepository.save(nuevaActividad);
        return toActividadDto(actividadGuardada);
    }

    public List<ActividadDto> findActividadesPorGrupo(Integer idGrupo, String profesorCorreo) {
        var profesor = getProfesorFromCorreo(profesorCorreo);
        var grupo = getGrupoFromId(idGrupo);

        validarProfesorDelGrupo(profesor, grupo);

        return actividadRepository.findByGrupo(grupo).stream()
                .map(this::toActividadDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ActividadDto updateActividad(Integer idActividad, UpdateActividadRequest request, String profesorCorreo) {
        var profesor = getProfesorFromCorreo(profesorCorreo);
        var actividad = actividadRepository.findById(idActividad)
                .orElseThrow(() -> new RuntimeException("Actividad no encontrada."));

        validarProfesorDelGrupo(profesor, actividad.getGrupo());

        actividad.setTitulo(request.getTitulo());
        actividad.setDescripcion(request.getDescripcion());
        actividad.setPonderacion(request.getPonderacion());

        var actividadActualizada = actividadRepository.save(actividad);
        return toActividadDto(actividadActualizada);
    }

    @Transactional
    public void deleteActividad(Integer idActividad, String profesorCorreo) {
        var profesor = getProfesorFromCorreo(profesorCorreo);
        var actividad = actividadRepository.findById(idActividad)
                .orElseThrow(() -> new RuntimeException("Actividad no encontrada."));

        validarProfesorDelGrupo(profesor, actividad.getGrupo());

        // Regla de negocio: No se puede borrar una actividad si ya tiene calificaciones.
        if (calificacionActividadRepository.existsByActividad(actividad)) {
            throw new IllegalStateException("No se puede eliminar la actividad porque ya tiene calificaciones asignadas.");
        }

        actividadRepository.deleteById(idActividad);
    }

    // --- MÉTODOS PRIVADOS DE AYUDA ---

    private Profesor getProfesorFromCorreo(String correo) {
        return profesorRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado."));
    }

    private Grupo getGrupoFromId(Integer idGrupo) {
        return grupoRepository.findById(idGrupo)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado."));
    }

    private void validarProfesorDelGrupo(Profesor profesor, Grupo grupo) {
        if (!grupo.getProfesor().getId().equals(profesor.getId())) {
            throw new SecurityException("Acceso denegado: Usted no es el profesor de este grupo.");
        }
    }

    private ActividadDto toActividadDto(Actividad actividad) {
        return ActividadDto.builder()
                .id(actividad.getId())
                .titulo(actividad.getTitulo())
                .descripcion(actividad.getDescripcion())
                .idGrupo(actividad.getGrupo().getId())
                .build();
    }

    @Transactional(readOnly = true)
    public List<CalificacionDto> findCalificacionesPorActividad(Integer idActividad, String profesorCorreo) {
        var profesor = getProfesorFromCorreo(profesorCorreo);
        var actividad = actividadRepository.findById(idActividad)
                .orElseThrow(() -> new RuntimeException("Actividad no encontrada."));

        validarProfesorDelGrupo(profesor, actividad.getGrupo());

        var inscripciones = materiaInscritaRepository.findByGrupo(actividad.getGrupo());
        List<CalificacionDto> calificaciones = new ArrayList<>();

        for (var inscripcion : inscripciones) {
            var calificacionExistente = calificacionActividadRepository
                    .findByMateriaInscritaAndActividad(inscripcion, actividad);

            calificaciones.add(CalificacionDto.builder()
                    .idMateriaInscrita(inscripcion.getId())
                    .nombreAlumno(inscripcion.getAlumno().getNombre() + " " + inscripcion.getAlumno().getApellido())
                    .matricula(inscripcion.getAlumno().getMatricula())
                    .idCalificacion(calificacionExistente.map(CalificacionActividad::getId).orElse(null))
                    .calificacionObtenida(calificacionExistente.map(CalificacionActividad::getCalificacionObtenida).orElse(null))
                    .fechaRegistro(calificacionExistente.map(CalificacionActividad::getFechaRegistro).orElse(null))
                    .comentario(calificacionExistente.map(CalificacionActividad::getComentario).orElse(null))
                    .build());
        }
        return calificaciones;
    }

    @Transactional
    public CalificacionActividad asignarCalificacion(AsignarCalificacionRequest request, Integer idActividad, String profesorCorreo) {
        var profesor = getProfesorFromCorreo(profesorCorreo);
        var actividad = actividadRepository.findById(idActividad)
                .orElseThrow(() -> new RuntimeException("Actividad no encontrada."));
        var inscripcion = materiaInscritaRepository.findById(request.getIdMateriaInscrita())
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada."));

        validarProfesorDelGrupo(profesor, actividad.getGrupo());
        if (!inscripcion.getGrupo().getId().equals(actividad.getGrupo().getId())) {
            throw new SecurityException("La inscripción del alumno no corresponde al grupo de esta actividad.");
        }

        CalificacionActividad calificacion = calificacionActividadRepository
                .findByMateriaInscritaAndActividad(inscripcion, actividad)
                .orElse(new CalificacionActividad()); // Crea una nueva si no existe

        calificacion.setMateriaInscrita(inscripcion);
        calificacion.setActividad(actividad);
        calificacion.setCalificacionObtenida(request.getCalificacionObtenida());
        calificacion.setComentario(request.getComentario());
        calificacion.setFechaRegistro(LocalDateTime.now());

        return calificacionActividadRepository.save(calificacion);
    }
}