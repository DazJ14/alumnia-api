package io.github.dazj14.alumnia_api.repository;
import io.github.dazj14.alumnia_api.model.Profesor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfesorRepository extends JpaRepository<Profesor, Integer> {
    Optional<Profesor> findByCorreo(String correo);
    Optional<Profesor> findByNumeroEmpleado(String numeroEmpleado);

    @Override
    Optional<Profesor> findById(Integer integer);
}