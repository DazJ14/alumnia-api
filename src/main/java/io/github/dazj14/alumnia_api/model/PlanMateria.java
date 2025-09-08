package io.github.dazj14.alumnia_api.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "plan_materias")
public class PlanMateria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plan_materia")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_plan_estudio", nullable = false)
    private PlanEstudio planEstudio;

    @ManyToOne
    @JoinColumn(name = "id_materia", nullable = false)
    private Materia materia;

    @Column(name = "semestre_sugerido")
    private Integer semestreSugerido;

    @Column(length = 50)
    private String tipo; // Ej: 'Obligatoria', 'Optativa'
}