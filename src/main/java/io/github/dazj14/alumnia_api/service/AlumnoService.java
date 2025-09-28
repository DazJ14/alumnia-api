package io.github.dazj14.alumnia_api.service;

import io.github.dazj14.alumnia_api.dto.*;
import io.github.dazj14.alumnia_api.exception.ResourceNotFoundException;
import io.github.dazj14.alumnia_api.model.Actividad;
import io.github.dazj14.alumnia_api.model.CalificacionActividad;
import io.github.dazj14.alumnia_api.model.MateriaInscrita;
import io.github.dazj14.alumnia_api.model.Periodo;
import io.github.dazj14.alumnia_api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlumnoService {

    private final AlumnoRepository alumnoRepository;
    private final PeriodoRepository periodoRepository;
    private final MateriaInscritaRepository materiaInscritaRepository;
    private final CalificacionActividadRepository calificacionActividadRepository;
    private final ActividadRepository actividadRepository;

    @Transactional(readOnly = true)
    public List<MateriaInscritaDto> findMateriasInscritasActuales(Integer alumnoId) {
        if (!alumnoRepository.existsById(alumnoId)) {
            throw new ResourceNotFoundException("Alumno no encontrado con ID: " + alumnoId);
        }
        var inscripciones = materiaInscritaRepository.findInscripcionesActivasPorAlumnoId(alumnoId, "Cursando");

        return inscripciones.stream().map(inscripcion -> {
            var grupo = inscripcion.getGrupo();
            var profesor = grupo.getProfesor();
            Float promedio = calcularPromedioPonderado(inscripcion);

            return MateriaInscritaDto.builder()
                    .idMateriaInscrita(inscripcion.getId())
                    .nombreMateria(grupo.getMateria().getNombreMateria())
                    .codigoGrupo(grupo.getCodigoGrupo())
                    .nombreProfesor(profesor.getNombre() + " " + profesor.getApellido())
                    .calificacionParcial(promedio)
                    .build();
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DesgloseCalificacionesDto findDesgloseCalificaciones(Integer idMateriaInscrita, Integer alumnoId) {
        var inscripcion = materiaInscritaRepository.findById(idMateriaInscrita)
                .orElseThrow(() -> new ResourceNotFoundException("Inscripción no encontrada."));
        if (!inscripcion.getAlumno().getId().equals(alumnoId)) {
            throw new SecurityException("Acceso denegado. Esta inscripción no le pertenece.");
        }

        var grupo = inscripcion.getGrupo();
        var actividadesDelGrupo = actividadRepository.findByGrupo(grupo);

        List<ActividadCalificadaDto> actividadesCalificadas = actividadesDelGrupo.stream().map(actividad -> {
            Optional<CalificacionActividad> calificacionOpt = calificacionActividadRepository
                    .findByMateriaInscritaAndActividad(inscripcion, actividad);

            return ActividadCalificadaDto.builder()
                    .tituloActividad(actividad.getTitulo())
                    .ponderacion(actividad.getPonderacion())
                    .calificacionObtenida(calificacionOpt.map(CalificacionActividad::getCalificacionObtenida).orElse(null))
                    .build();
        }).collect(Collectors.toList());

        Float promedioPonderado = calcularPromedioPonderado(inscripcion);

        return DesgloseCalificacionesDto.builder()
                .nombreMateria(grupo.getMateria().getNombreMateria())
                .calificacionFinalCalculada(promedioPonderado)
                .actividades(actividadesCalificadas)
                .build();
    }

    @Transactional(readOnly = true)
    public KardexDto findKardex(Integer alumnoId) {
        var alumno = alumnoRepository.findById(alumnoId)
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado con ID: " + alumnoId));
        var periodoActivo = periodoRepository.findTopByOrderByFechaInicioDesc()
                .orElse(null);

        List<MateriaInscrita> historialInscripciones = materiaInscritaRepository
                .findByAlumnoAndGrupo_PeriodoNot(alumno, periodoActivo);

        Map<Periodo, List<MateriaInscrita>> historialPorPeriodo = historialInscripciones.stream()
                .collect(Collectors.groupingBy(inscripcion -> inscripcion.getGrupo().getPeriodo()));

        List<KardexPeriodoDto> historialDto = historialPorPeriodo.entrySet().stream()
                .map(entry -> {
                    List<KardexMateriaDto> materiasDto = entry.getValue().stream()
                            .map(insc -> KardexMateriaDto.builder()
                                    .idMateriaInscrita(insc.getId())
                                    .nombreMateria(insc.getGrupo().getMateria().getNombreMateria())
                                    .calificacionFinal(insc.getCalificacionFinal())
                                    .creditos(insc.getGrupo().getMateria().getCreditos())
                                    .status(insc.getStatus())
                                    .build())
                            .collect(Collectors.toList());
                    return KardexPeriodoDto.builder()
                            .nombrePeriodo(entry.getKey().getNombrePeriodo())
                            .materias(materiasDto)
                            .build();
                })
                .collect(Collectors.toList());

        float sumaPonderada = 0;
        int totalCreditos = 0;
        for (MateriaInscrita insc : historialInscripciones) {
            if (insc.getCalificacionFinal() != null && insc.getGrupo().getMateria().getCreditos() != null) {
                sumaPonderada += insc.getCalificacionFinal() * insc.getGrupo().getMateria().getCreditos();
                totalCreditos += insc.getGrupo().getMateria().getCreditos();
            }
        }
        Float promedioGeneral = (totalCreditos > 0) ? sumaPonderada / totalCreditos : null;

        return KardexDto.builder()
                .promedioGeneral(promedioGeneral)
                .historial(historialDto)
                .build();
    }

    private Float calcularPromedioPonderado(MateriaInscrita inscripcion) {
        // 1. Obtener todas las actividades del grupo al que pertenece la inscripción.
        List<Actividad> actividadesDelGrupo = actividadRepository.findByGrupo(inscripcion.getGrupo());

        // 2. Obtener todas las calificaciones que tiene el alumno para esta materia inscrita.
        List<CalificacionActividad> calificacionesDelAlumno = calificacionActividadRepository.findByMateriaInscrita(inscripcion);

        // 3. Crear un mapa para buscar calificaciones por ID de actividad fácilmente.
        Map<Integer, Float> mapaCalificaciones = calificacionesDelAlumno.stream()
                .collect(Collectors.toMap(cal -> cal.getActividad().getId(), CalificacionActividad::getCalificacionObtenida));

        double sumaPonderada = 0.0;

        // 4. Iterar sobre TODAS las actividades que tiene el grupo.
        for (Actividad actividad : actividadesDelGrupo) {
            Float calificacionObtenida = mapaCalificaciones.get(actividad.getId());
            Float ponderacion = actividad.getPonderacion();

            if (calificacionObtenida != null && ponderacion != null) {
                // Si hay calificación para esta actividad, se calcula su contribución.
                double contribucion = (calificacionObtenida / 100.0) * ponderacion;
                sumaPonderada += contribucion;
            }
        }

        return (float) sumaPonderada;
    }
}