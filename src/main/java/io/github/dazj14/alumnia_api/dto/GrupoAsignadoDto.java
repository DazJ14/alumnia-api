package io.github.dazj14.alumnia_api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GrupoAsignadoDto {
    private Integer idGrupo;
    private String codigoGrupo;
    private String nombreMateria;
    private String horario;
    private String salon;
}