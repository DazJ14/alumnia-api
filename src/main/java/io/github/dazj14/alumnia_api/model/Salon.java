package io.github.dazj14.alumnia_api.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "salones")
public class Salon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_salon")
    private Integer id;

    @Column(name = "nombre_salon", nullable = false)
    private String nombreSalon;

    private Integer capacidad;

    @ManyToOne
    @JoinColumn(name = "id_campus", nullable = false)
    private Campus campus;
}