package io.github.dazj14.alumnia_api.dto;

import lombok.Data;

@Data
public class AsignarCalificacionRequest {
    private Integer idMateriaInscrita;
    private Float calificacionObtenida;
    private String comentario;
}