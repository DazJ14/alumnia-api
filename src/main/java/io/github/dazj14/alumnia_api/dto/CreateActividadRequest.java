package io.github.dazj14.alumnia_api.dto;

import lombok.Data;

@Data
public class CreateActividadRequest {
    private String titulo;
    private String descripcion;
    private Float califMaxima;
}