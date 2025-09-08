package io.github.dazj14.alumnia_api.repository;
import io.github.dazj14.alumnia_api.model.Actividad;
import io.github.dazj14.alumnia_api.model.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActividadRepository extends JpaRepository<Actividad, Integer> {
    List<Actividad> findByGrupo(Grupo grupo);
}