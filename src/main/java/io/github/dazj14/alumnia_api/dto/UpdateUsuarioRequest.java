package io.github.dazj14.alumnia_api.dto;

import lombok.Data;

@Data
public class UpdateUsuarioRequest {
    private String nombre;
    private String apellido;
    private String correo;
    // Campos específicos
    private String matricula;
    private String numeroEmpleado;
}