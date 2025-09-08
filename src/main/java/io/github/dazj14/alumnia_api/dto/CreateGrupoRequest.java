package io.github.dazj14.alumnia_api.dto;

import lombok.Data;

@Data
public class CreateGrupoRequest {
    // Group-specific data
    private String codigoGrupo;
    private Integer cupo;

    // IDs of related entities
    private Integer idMateria;
    private Integer idProfesor;
    private Integer idPeriodo;
    private Integer idSalon;
    private Integer idHorario;
    private Integer idModalidad;
}