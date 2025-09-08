package io.github.dazj14.alumnia_api.repository;

import io.github.dazj14.alumnia_api.model.Semestre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SemestreRepository extends JpaRepository<Semestre, Integer> {}
