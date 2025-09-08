package io.github.dazj14.alumnia_api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MateriaInscritaDto {
    private Integer idMateriaInscrita;
    private String nombreMateria;
    private String codigoGrupo;
    private String nombreProfesor;
    private Float calificacionParcial; // Calcularemos un promedio simple de las actividades
}