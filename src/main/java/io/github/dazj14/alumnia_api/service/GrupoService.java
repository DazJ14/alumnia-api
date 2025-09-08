package io.github.dazj14.alumnia_api.service;

import io.github.dazj14.alumnia_api.dto.CreateGrupoRequest;
import io.github.dazj14.alumnia_api.model.Grupo;
import io.github.dazj14.alumnia_api.repository.*;
import io.github.dazj14.alumnia_api.dto.GrupoDto;
import io.github.dazj14.alumnia_api.dto.UpdateGrupoRequest;
import io.github.dazj14.alumnia_api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GrupoService {

    // Inject all the repositories we need to find the related entities
    private final GrupoRepository grupoRepository;
    private final MateriaRepository materiaRepository;
    private final ProfesorRepository profesorRepository;
    private final PeriodoRepository periodoRepository;
    private final SalonRepository salonRepository;
    private final HorarioRepository horarioRepository;
    private final ModalidadRepository modalidadRepository;
    private final MateriaInscritaRepository materiaInscritaRepository;

    public List<GrupoDto> findAll() {
        return grupoRepository.findAll().stream()
                .map(this::toGrupoDto)
                .collect(Collectors.toList());
    }

    public GrupoDto findById(Integer id) {
        return grupoRepository.findById(id)
                .map(this::toGrupoDto)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado con ID: " + id));
    }

    @Transactional
    public GrupoDto update(Integer id, UpdateGrupoRequest request) {
        Grupo grupoExistente = grupoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado con ID: " + id));

        // Fetch all related entities
        var materia = materiaRepository.findById(request.getIdMateria()).orElseThrow(() -> new RuntimeException("Materia no encontrada"));
        var profesor = profesorRepository.findById(request.getIdProfesor()).orElseThrow(() -> new RuntimeException("Profesor no encontrado"));
        var periodo = periodoRepository.findById(request.getIdPeriodo()).orElseThrow(() -> new RuntimeException("Periodo no encontrado"));
        var salon = salonRepository.findById(request.getIdSalon()).orElseThrow(() -> new RuntimeException("Salón no encontrado"));
        var horario = horarioRepository.findById(request.getIdHorario()).orElseThrow(() -> new RuntimeException("Horario no encontrado"));
        var modalidad = modalidadRepository.findById(request.getIdModalidad()).orElseThrow(() -> new RuntimeException("Modalidad no encontrada"));

        // Update fields
        grupoExistente.setCodigoGrupo(request.getCodigoGrupo());
        grupoExistente.setCupo(request.getCupo());
        grupoExistente.setMateria(materia);
        grupoExistente.setProfesor(profesor);
        grupoExistente.setPeriodo(periodo);
        grupoExistente.setSalon(salon);
        grupoExistente.setHorario(horario);
        grupoExistente.setModalidad(modalidad);

        var grupoActualizado = grupoRepository.save(grupoExistente);
        return toGrupoDto(grupoActualizado);
    }

    @Transactional
    public void deleteById(Integer id) {
        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado con ID: " + id));

        // Business Rule: Cannot delete a group with enrolled students
        if (materiaInscritaRepository.countByGrupo(grupo) > 0) {
            throw new IllegalStateException("No se puede eliminar el grupo porque ya tiene alumnos inscritos.");
        }

        grupoRepository.deleteById(id);
    }

    @Transactional
    public Grupo create(CreateGrupoRequest request) {
        // 1. Find all related entities by their IDs, throwing an error if any are not found.
        var materia = materiaRepository.findById(request.getIdMateria())
                .orElseThrow(() -> new RuntimeException("Materia no encontrada con ID: " + request.getIdMateria()));
        var profesor = profesorRepository.findById(request.getIdProfesor())
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado con ID: " + request.getIdProfesor()));
        var periodo = periodoRepository.findById(request.getIdPeriodo())
                .orElseThrow(() -> new RuntimeException("Periodo no encontrado con ID: " + request.getIdPeriodo()));
        var salon = salonRepository.findById(request.getIdSalon())
                .orElseThrow(() -> new RuntimeException("Salón no encontrado con ID: " + request.getIdSalon()));
        var horario = horarioRepository.findById(request.getIdHorario())
                .orElseThrow(() -> new RuntimeException("Horario no encontrado con ID: " + request.getIdHorario()));
        var modalidad = modalidadRepository.findById(request.getIdModalidad())
                .orElseThrow(() -> new RuntimeException("Modalidad no encontrada con ID: " + request.getIdModalidad()));

        // 2. Create and populate the new Grupo object
        Grupo nuevoGrupo = new Grupo();
        nuevoGrupo.setCodigoGrupo(request.getCodigoGrupo());
        nuevoGrupo.setCupo(request.getCupo());
        nuevoGrupo.setMateria(materia);
        nuevoGrupo.setProfesor(profesor);
        nuevoGrupo.setPeriodo(periodo);
        nuevoGrupo.setSalon(salon);
        nuevoGrupo.setHorario(horario);
        nuevoGrupo.setModalidad(modalidad);

        // 3. Save and return the new group
        return grupoRepository.save(nuevoGrupo);
    }

    private GrupoDto toGrupoDto(Grupo grupo) {
        return GrupoDto.builder()
                .idGrupo(grupo.getId())
                .codigoGrupo(grupo.getCodigoGrupo())
                .cupo(grupo.getCupo())
                .nombreMateria(grupo.getMateria().getNombreMateria())
                .nombreProfesor(grupo.getProfesor().getNombre() + " " + grupo.getProfesor().getApellido())
                .nombrePeriodo(grupo.getPeriodo().getNombrePeriodo())
                .nombreSalon(grupo.getSalon().getNombreSalon())
                .nombreHorario(grupo.getHorario().getNombreHorario())
                .nombreModalidad(grupo.getModalidad().getNombreModalidad())
                .build();
    }
}