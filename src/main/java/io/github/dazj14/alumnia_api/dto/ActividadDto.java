package io.github.dazj14.alumnia_api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActividadDto {
    private Integer id;
    private String titulo;
    private String descripcion;
    private Float califMaxima;
    private Integer idGrupo;
}