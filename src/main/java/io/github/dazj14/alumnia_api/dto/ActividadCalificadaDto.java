package io.github.dazj14.alumnia_api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActividadCalificadaDto {
    private String tituloActividad;
    private Float califMaxima;
    private Float calificacionObtenida; // Puede ser nulo si no está calificada
}