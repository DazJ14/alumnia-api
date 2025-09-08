package io.github.dazj14.alumnia_api.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "carreras")
public class Carrera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carrera")
    private Integer id;

    @Column(name = "nombre_carrera", nullable = false, length = 255)
    private String nombreCarrera;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "id_plan_estudio", nullable = false)
    private PlanEstudio planEstudio;
}