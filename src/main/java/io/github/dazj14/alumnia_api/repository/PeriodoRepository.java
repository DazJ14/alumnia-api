package io.github.dazj14.alumnia_api.repository;
import io.github.dazj14.alumnia_api.model.Periodo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PeriodoRepository extends JpaRepository<Periodo, Integer> {
    Optional<Periodo> findTopByOrderByFechaInicioDesc();
}