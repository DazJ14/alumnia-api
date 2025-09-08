package io.github.dazj14.alumnia_api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GrupoDto {
    private Integer idGrupo;
    private String codigoGrupo;
    private Integer cupo;
    private String nombreMateria;
    private String nombreProfesor;
    private String nombrePeriodo;
    private String nombreSalon;
    private String nombreHorario;
    private String nombreModalidad;
}