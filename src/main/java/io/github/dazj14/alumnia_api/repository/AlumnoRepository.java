package io.github.dazj14.alumnia_api.repository;
import io.github.dazj14.alumnia_api.model.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Integer> {
    Optional<Alumno> findByCorreo(String correo);
}