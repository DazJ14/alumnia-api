package io.github.dazj14.alumnia_api.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class DesgloseCalificacionesDto {
    private String nombreMateria;
    private Float calificacionFinalCalculada;
    private List<ActividadCalificadaDto> actividades;
}