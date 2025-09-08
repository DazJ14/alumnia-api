package io.github.dazj14.alumnia_api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "calificaciones_actividades")
public class CalificacionActividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_calificacion")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_materia_inscrita", nullable = false)
    private MateriaInscrita materiaInscrita;

    @ManyToOne
    @JoinColumn(name = "id_actividad", nullable = false)
    private Actividad actividad;

    @Column(name = "calificacion_obtenida")
    private Float calificacionObtenida;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @Column(columnDefinition = "TEXT")
    private String comentario;
}