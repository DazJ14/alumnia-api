package io.github.dazj14.alumnia_api.repository;
import io.github.dazj14.alumnia_api.model.Alumno;
import io.github.dazj14.alumnia_api.model.Grupo;
import io.github.dazj14.alumnia_api.model.Periodo;
import io.github.dazj14.alumnia_api.model.MateriaInscrita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface MateriaInscritaRepository extends JpaRepository<MateriaInscrita, Integer> {
    long countByGrupo(Grupo grupo);

    boolean existsByAlumnoAndGrupo(Alumno alumno, Grupo grupo);

    List<MateriaInscrita> findByAlumnoAndGrupo_Periodo(Alumno alumno, Periodo periodo);
    List<MateriaInscrita> findByGrupo(Grupo grupo);
    List<MateriaInscrita> findByAlumnoAndGrupo_PeriodoNot(Alumno alumno, Periodo periodo);
    /**
     * Busca las materias inscritas de un alumno por su ID y un estatus específico,
     * ignorando mayúsculas y minúsculas en el estatus.
     * @param alumnoId El ID del alumno (que corresponde al id_usuario).
     * @param status El estatus de la inscripción (ej. "Cursando").
     * @return Una lista de materias inscritas que coinciden con los criterios.
     */
    @Query("SELECT m FROM MateriaInscrita m WHERE m.alumno.id = :alumnoId AND lower(CAST(m.status AS string)) = lower(:status)")
    List<MateriaInscrita> findInscripcionesActivasPorAlumnoId(@Param("alumnoId") Integer alumnoId, @Param("status") String status);
}