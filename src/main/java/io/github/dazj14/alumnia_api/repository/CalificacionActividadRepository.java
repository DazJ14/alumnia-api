package io.github.dazj14.alumnia_api.repository;

import io.github.dazj14.alumnia_api.model.Actividad;
import io.github.dazj14.alumnia_api.model.CalificacionActividad;
import io.github.dazj14.alumnia_api.model.MateriaInscrita; // Importar
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface CalificacionActividadRepository extends JpaRepository<CalificacionActividad, Integer> {
    Optional<CalificacionActividad> findByMateriaInscritaAndActividad(MateriaInscrita materiaInscrita, Actividad actividad);

    List<CalificacionActividad> findByMateriaInscrita(MateriaInscrita materiaInscrita);

    boolean existsByActividad(Actividad actividad);
}