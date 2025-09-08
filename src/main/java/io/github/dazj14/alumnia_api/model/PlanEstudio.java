package io.github.dazj14.alumnia_api.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List; // Importar List

@Data
@Entity
@Table(name = "planes_estudio")
public class PlanEstudio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plan_estudio")
    private Integer id;

    @Column(name = "nombre_plan", nullable = false, length = 255)
    private String nombrePlan;

    @ManyToOne
    @JoinColumn(name = "id_modelo", nullable = false)
    private ModeloEducativo modeloEducativo;

    // --- NUEVA LÍNEA AÑADIDA ---
    @OneToMany(mappedBy = "planEstudio")
    private List<PlanMateria> materias;
}