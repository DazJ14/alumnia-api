package io.github.dazj14.alumnia_api.repository;
import io.github.dazj14.alumnia_api.model.Alumno;
import io.github.dazj14.alumnia_api.model.Grupo;
import io.github.dazj14.alumnia_api.model.Periodo;
import io.github.dazj14.alumnia_api.model.MateriaInscrita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MateriaInscritaRepository extends JpaRepository<MateriaInscrita, Integer> {
    long countByGrupo(Grupo grupo);

    boolean existsByAlumnoAndGrupo(Alumno alumno, Grupo grupo);

    List<MateriaInscrita> findByAlumnoAndGrupo_Periodo(Alumno alumno, Periodo periodo);
    List<MateriaInscrita> findByGrupo(Grupo grupo);
    List<MateriaInscrita> findByAlumnoAndGrupo_PeriodoNot(Alumno alumno, Periodo periodo);
}