package io.github.dazj14.alumnia_api.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "alumnos")
@PrimaryKeyJoinColumn(name = "id_usuario")
public class Alumno extends Usuario {

    @Column(nullable = false, unique = true, length = 50)
    private String matricula;

    @ManyToOne
    @JoinColumn(name = "id_carrera", nullable = false)
    private Carrera carrera;

    @ManyToOne
    @JoinColumn(name = "id_plan_estudio", nullable = false)
    private PlanEstudio planEstudio;
}