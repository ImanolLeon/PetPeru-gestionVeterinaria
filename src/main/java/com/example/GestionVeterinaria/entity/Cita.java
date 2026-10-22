package com.example.GestionVeterinaria.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"veterinario_id", "fechaCita", "horaCita"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "FechaCita")
    private LocalDate fechaCita;

    @Column(name = "horaCita")
    private LocalTime horaCita;

    @Column (length = 100)
    private String motivo;


    @ManyToOne
    @JoinColumn(name = "mascota_id",nullable = false)
    private Mascota mascota;


    @Column(length = 100)
    private String estado;

    @ManyToOne
    @JoinColumn(name = "veterinario_id",nullable = false)
    private Veterinario veterinario;

    @OneToOne(mappedBy = "cita", cascade = CascadeType.ALL, orphanRemoval = true)
    private HistorialClinico historialClinico;
}
