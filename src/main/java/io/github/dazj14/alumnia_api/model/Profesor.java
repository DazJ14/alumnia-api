package io.github.dazj14.alumnia_api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "profesores")
@PrimaryKeyJoinColumn(name = "id_usuario")
public class Profesor extends Usuario {

    @Column(name = "numero_empleado", nullable = false, unique = true, length = 50)
    private String numeroEmpleado;

    @Column(length = 255)
    private String especialidad;
}