package io.github.dazj14.alumnia_api.repository;
import io.github.dazj14.alumnia_api.model.PlanEstudio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface PlanEstudioRepository extends JpaRepository<PlanEstudio, Integer> {}