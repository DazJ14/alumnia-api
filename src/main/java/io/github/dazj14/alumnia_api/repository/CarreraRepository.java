package io.github.dazj14.alumnia_api.repository;
import io.github.dazj14.alumnia_api.model.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface CarreraRepository extends JpaRepository<Carrera, Integer> {}