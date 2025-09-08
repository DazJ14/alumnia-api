// Modalidad.java
package io.github.dazj14.alumnia_api.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "modalidades")
public class Modalidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_modalidad")
    private Integer id;

    @Column(name = "nombre_modalidad", nullable = false)
    private String nombreModalidad;
}