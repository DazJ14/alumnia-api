package io.github.dazj14.alumnia_api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioDto {
    private Integer id;
    private String nombre;
    private String apellido;
    private String correo;
    private String nombreRol;

    // Campos específicos que pueden o no estar presentes
    private String matricula;
    private String numeroEmpleado;
}