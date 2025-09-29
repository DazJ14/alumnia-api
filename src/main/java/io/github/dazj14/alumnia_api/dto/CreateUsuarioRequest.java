package io.github.dazj14.alumnia_api.dto;

import lombok.Data;

@Data
public class CreateUsuarioRequest {
    // Campos comunes
    private String nombre;
    private String apellido;
    private Integer idRol; // 1=Admin, 2=Maestro, 3=Alumno

    // Campos específicos para Alumno
    private Integer idCarrera;
    private Integer idPlanEstudio;

    // Campos específicos para Profesor
    private String especialidad;
}