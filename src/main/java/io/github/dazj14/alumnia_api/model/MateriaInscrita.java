package io.github.dazj14.alumnia_api.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "materias_inscritas")
public class MateriaInscrita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_materia_inscrita")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_alumno", nullable = false) // 'id_alumno' en la tabla es el FK a usuarios.id_usuario
    private Alumno alumno;

    @ManyToOne
    @JoinColumn(name = "id_grupo", nullable = false)
    private Grupo grupo;

    @Column(name = "calificacion_final")
    private Float calificacionFinal;

    @Column(length = 50)
    private String status; // Ej: 'Cursando', 'Aprobado', 'Reprobado'

    private Integer oportunidad;

    @Column(name = "tipo_oportunidad", length = 50)
    private String tipoOportunidad; // Ej: 'Ordinario', 'Extraordinario'
}