package io.github.dazj14.alumnia_api.dto;

import lombok.Data;

@Data
public class UpdateMateriaRequest {
    private String claveMateria;
    private String nombreMateria;
    private Integer creditos;
}