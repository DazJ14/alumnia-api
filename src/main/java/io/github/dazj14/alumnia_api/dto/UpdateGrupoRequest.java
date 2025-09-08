package io.github.dazj14.alumnia_api.dto;

import lombok.Data;

@Data
public class UpdateGrupoRequest {
    private String codigoGrupo;
    private Integer cupo;
    private Integer idMateria;
    private Integer idProfesor;
    private Integer idPeriodo;
    private Integer idSalon;
    private Integer idHorario;
    private Integer idModalidad;
}