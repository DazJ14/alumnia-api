package io.github.dazj14.alumnia_api.repository;
import io.github.dazj14.alumnia_api.model.Grupo;
import io.github.dazj14.alumnia_api.model.Periodo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.github.dazj14.alumnia_api.model.Profesor;

import java.util.List;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, Integer> {
    List<Grupo> findByPeriodo(Periodo periodo);

    List<Grupo> findByProfesorAndPeriodo(Profesor profesor, Periodo periodo);
}