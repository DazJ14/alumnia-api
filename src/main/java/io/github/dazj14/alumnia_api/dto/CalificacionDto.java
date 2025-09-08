package io.github.dazj14.alumnia_api.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class CalificacionDto {
    // Info de la Inscripción del Alumno
    private Integer idMateriaInscrita;
    private String nombreAlumno;
    private String matricula;

    // Info de la Calificación (puede ser nula si no se ha calificado)
    private Integer idCalificacion;
    private Float calificacionObtenida;
    private LocalDateTime fechaRegistro;
    private String comentario;
}