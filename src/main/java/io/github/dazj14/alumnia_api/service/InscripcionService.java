package io.github.dazj14.alumnia_api.service;

import io.github.dazj14.alumnia_api.dto.GrupoDisponibleDto;
import io.github.dazj14.alumnia_api.repository.GrupoRepository;
import io.github.dazj14.alumnia_api.repository.MateriaInscritaRepository;
import io.github.dazj14.alumnia_api.repository.PeriodoRepository;
import io.github.dazj14.alumnia_api.dto.InscribirGrupoRequest;
import io.github.dazj14.alumnia_api.model.MateriaInscrita;
import io.github.dazj14.alumnia_api.repository.AlumnoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InscripcionService {

    private final GrupoRepository grupoRepository;
    private final PeriodoRepository periodoRepository;
    private final MateriaInscritaRepository materiaInscritaRepository;
    private final AlumnoRepository alumnoRepository;

    @Transactional(readOnly = true) // Transacción de solo lectura, es más eficiente
    public List<GrupoDisponibleDto> findGruposDisponibles() {
        // Lógica para encontrar el periodo activo (aquí, el más reciente por fecha de inicio)
        var periodoActivo = periodoRepository.findTopByOrderByFechaInicioDesc()
                .orElseThrow(() -> new RuntimeException("No hay un periodo activo configurado."));

        var grupos = grupoRepository.findByPeriodo(periodoActivo);

        return grupos.stream().map(grupo -> {
            long inscritos = materiaInscritaRepository.countByGrupo(grupo);
            long lugaresDisponibles = grupo.getCupo() - inscritos;

            return GrupoDisponibleDto.builder()
                    .idGrupo(grupo.getId())
                    .codigoGrupo(grupo.getCodigoGrupo())
                    .nombreMateria(grupo.getMateria().getNombreMateria())
                    .creditos(grupo.getMateria().getCreditos())
                    .nombreProfesor(grupo.getProfesor().getNombre() + " " + grupo.getProfesor().getApellido())
                    .horario(grupo.getHorario().getNombreHorario())
                    .cupo(grupo.getCupo())
                    .lugaresDisponibles(Math.max(0, lugaresDisponibles)) // Asegura que no sea negativo
                    .build();
        }).collect(Collectors.toList());
    }

    @Transactional
    public MateriaInscrita inscribirGrupo(InscribirGrupoRequest request, String alumnoCorreo) {
        // 1. Obtener el alumno y el grupo
        var alumno = alumnoRepository.findByCorreo(alumnoCorreo)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado."));
        var grupo = grupoRepository.findById(request.getIdGrupo())
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado."));

        // 2. Realizar validaciones de negocio
        // Validación 2.1: Verificar si ya está inscrito
        if (materiaInscritaRepository.existsByAlumnoAndGrupo(alumno, grupo)) {
            throw new IllegalStateException("El alumno ya está inscrito en este grupo.");
        }

        // Validación 2.2: Verificar cupo disponible
        long inscritos = materiaInscritaRepository.countByGrupo(grupo);
        if (inscritos >= grupo.getCupo()) {
            throw new IllegalStateException("El grupo ya no tiene cupo disponible.");
        }

        // Validación 2.3: Verificar choque de horario
        var inscripcionesPrevias = materiaInscritaRepository.findByAlumnoAndGrupo_Periodo(alumno, grupo.getPeriodo());
        boolean hayChoque = inscripcionesPrevias.stream()
                .anyMatch(inscripcion -> inscripcion.getGrupo().getHorario().equals(grupo.getHorario()));
        if (hayChoque) {
            throw new IllegalStateException("Existe un choque de horario con otra materia inscrita.");
        }

        // (Aquí irían otras validaciones, como prerrequisitos)

        MateriaInscrita nuevaInscripcion = new MateriaInscrita();
        nuevaInscripcion.setAlumno(alumno);
        nuevaInscripcion.setGrupo(grupo);
        nuevaInscripcion.setStatus("Cursando");
        nuevaInscripcion.setOportunidad(1);
        nuevaInscripcion.setTipoOportunidad("Ordinario");

        return materiaInscritaRepository.save(nuevaInscripcion);
    }
}