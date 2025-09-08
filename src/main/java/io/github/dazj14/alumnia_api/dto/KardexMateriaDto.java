package io.github.dazj14.alumnia_api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KardexMateriaDto {
    private String nombreMateria;
    private Float calificacionFinal;
    private Integer creditos;
    private String status; // Aprobado, Reprobado
}