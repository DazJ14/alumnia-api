package io.github.dazj14.alumnia_api.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class KardexPeriodoDto {
    private String nombrePeriodo;
    private List<KardexMateriaDto> materias;
}