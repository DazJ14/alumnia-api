package io.github.dazj14.alumnia_api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder // Usamos Builder para facilitar la construcción del objeto
public class GrupoDisponibleDto {
    private Integer idGrupo;
    private String codigoGrupo;
    private String nombreMateria;
    private Integer creditos;
    private String nombreProfesor;
    private String horario;
    private Integer cupo;
    private long lugaresDisponibles;
}