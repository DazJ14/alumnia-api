package io.github.dazj14.alumnia_api.service;

import io.github.dazj14.alumnia_api.dto.MateriaInscritaDto;
import io.github.dazj14.alumnia_api.model.CalificacionActividad;
import io.github.dazj14.alumnia_api.model.MateriaInscrita;
import io.github.dazj14.alumnia_api.repository.AlumnoRepository;
import io.github.dazj14.alumnia_api.repository.CalificacionActividadRepository;
import io.github.dazj14.alumnia_api.repository.MateriaInscritaRepository;
import io.github.dazj14.alumnia_api.repository.PeriodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.github.dazj14.alumnia_api.dto.ActividadCalificadaDto;
import io.github.dazj14.alumnia_api.dto.DesgloseCalificacionesDto;
import io.github.dazj14.alumnia_api.repository.ActividadRepository;
import io.github.dazj14.alumnia_api.dto.KardexDto;
import io.github.dazj14.alumnia_api.dto.KardexMateriaDto;
import io.github.dazj14.alumnia_api.dto.KardexPeriodoDto;
import io.github.dazj14.alumnia_api.model.Periodo;

import java.util.stream.Collectors;
import java.util.Map;
import java.util.Optional;
import java.util.List;
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
    public List<MateriaInscritaDto> findMateriasInscritasActuales(String alumnoCorreo) {
        var alumno = alumnoRepository.findByCorreo(alumnoCorreo)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado."));
        var periodoActivo = periodoRepository.findTopByOrderByFechaInicioDesc()
                .orElseThrow(() -> new RuntimeException("No hay un periodo activo configurado."));

        var inscripciones = materiaInscritaRepository.findByAlumnoAndGrupo_Periodo(alumno, periodoActivo);

        return inscripciones.stream().map(inscripcion -> {
            var grupo = inscripcion.getGrupo();
            var profesor = grupo.getProfesor();

            // Lógica para calcular la calificación parcial
            var calificaciones = calificacionActividadRepository.findByMateriaInscrita(inscripcion);
            Float promedio = calcularPromedioSimple(calificaciones);

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
    public DesgloseCalificacionesDto findDesgloseCalificaciones(Integer idMateriaInscrita, String alumnoCorreo) {
        // 1. Validar que la inscripción pertenece al alumno
        var inscripcion = materiaInscritaRepository.findById(idMateriaInscrita)
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada."));
        if (!inscripcion.getAlumno().getCorreo().equals(alumnoCorreo)) {
            throw new SecurityException("Acceso denegado. Esta inscripción no le pertenece.");
        }

        var grupo = inscripcion.getGrupo();
        var actividadesDelGrupo = actividadRepository.findByGrupo(grupo);

        // 2. Recopilar las calificaciones para cada actividad
        List<ActividadCalificadaDto> actividadesCalificadas = actividadesDelGrupo.stream().map(actividad -> {
            Optional<CalificacionActividad> calificacionOpt = calificacionActividadRepository
                    .findByMateriaInscritaAndActividad(inscripcion, actividad);

            return ActividadCalificadaDto.builder()
                    .tituloActividad(actividad.getTitulo())
                    .califMaxima(actividad.getCalifMaxima())
                    .calificacionObtenida(calificacionOpt.map(CalificacionActividad::getCalificacionObtenida).orElse(null))
                    .build();
        }).collect(Collectors.toList());

        // 3. Calcular el promedio y construir el DTO de respuesta
        Float promedio = calcularPromedioSimple(
                calificacionActividadRepository.findByMateriaInscrita(inscripcion)
        );

        return DesgloseCalificacionesDto.builder()
                .nombreMateria(grupo.getMateria().getNombreMateria())
                .calificacionFinalCalculada(promedio)
                .actividades(actividadesCalificadas)
                .build();
    }

    private Float calcularPromedioSimple(List<CalificacionActividad> calificaciones) {
        if (calificaciones == null || calificaciones.isEmpty()) {
            return null; // O 0.0f si prefieres
        }
        double suma = calificaciones.stream()
                .mapToDouble(CalificacionActividad::getCalificacionObtenida)
                .sum();
        return (float) (suma / calificaciones.size());
    }

    @Transactional(readOnly = true)
    public KardexDto findKardex(String alumnoCorreo) {
        var alumno = alumnoRepository.findByCorreo(alumnoCorreo)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado."));
        var periodoActivo = periodoRepository.findTopByOrderByFechaInicioDesc()
                .orElse(null); // Usamos orElse(null) para no fallar si no hay periodos

        // 1. Obtener todas las inscripciones pasadas
        List<MateriaInscrita> historialInscripciones = materiaInscritaRepository
                .findByAlumnoAndGrupo_PeriodoNot(alumno, periodoActivo);

        // 2. Agrupar las inscripciones por periodo
        Map<Periodo, List<MateriaInscrita>> historialPorPeriodo = historialInscripciones.stream()
                .collect(Collectors.groupingBy(inscripcion -> inscripcion.getGrupo().getPeriodo()));

        // 3. Convertir cada grupo a su DTO
        List<KardexPeriodoDto> historialDto = historialPorPeriodo.entrySet().stream()
                .map(entry -> {
                    List<KardexMateriaDto> materiasDto = entry.getValue().stream()
                            .map(insc -> KardexMateriaDto.builder()
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

        // 4. Calcular el promedio general ponderado
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
}