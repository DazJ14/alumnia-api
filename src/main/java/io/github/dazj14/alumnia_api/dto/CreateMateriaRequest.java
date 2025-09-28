package io.github.dazj14.alumnia_api.dto;

import jakarta.validation.constraints.NotBlank; // Importar
import jakarta.validation.constraints.NotNull;  // Importar
import jakarta.validation.constraints.Positive; // Importar
import lombok.Data;

import java.util.List;

@Data
public class CreateMateriaRequest {
    @NotBlank(message = "La clave de la materia no puede estar vacía")
    private String claveMateria;

    @NotBlank(message = "El nombre de la materia no puede estar vacío")
    private String nombreMateria;

    @NotNull(message = "Los créditos no pueden ser nulos")
    @Positive(message = "Los créditos deben ser un número positivo")
    private Integer creditos;

    private List<Integer> prerrequisitoIds;
}