package io.github.dazj14.alumnia_api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // No incluirá campos nulos en el JSON de respuesta
public class CreateUsuarioResponse {
    private String nombre;
    private String apellido;
    private String correo;
    private String passwordTemporal;
    private String matricula;       // Solo para alumnos
    private String numeroEmpleado;  // Solo para profesores
}