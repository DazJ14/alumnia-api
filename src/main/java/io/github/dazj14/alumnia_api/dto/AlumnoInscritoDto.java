package io.github.dazj14.alumnia_api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlumnoInscritoDto {
    private Integer idUsuario;
    private String nombreCompleto;
    private String matricula;
    private String correo;
}