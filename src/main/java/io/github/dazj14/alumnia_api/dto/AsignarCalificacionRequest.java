package io.github.dazj14.alumnia_api.dto;

import lombok.Data;

@Data
public class AsignarCalificacionRequest {
    private Integer idGrupo;
    private Integer idAlumno;
    private Float calificacionObtenida;
    private String comentario;
}