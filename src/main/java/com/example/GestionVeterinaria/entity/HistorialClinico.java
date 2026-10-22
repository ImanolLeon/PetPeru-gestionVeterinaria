package com.example.GestionVeterinaria.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "Historial Clinico")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HistorialClinico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_Consulta")
    private LocalDate fechaConsulta;

    @Column()
    private LocalTime horaConsulta;
    @Column (length = 200)
    private String diagnostico;


    @Column (length = 200)
    private String tratamiento;


    @Column (length = 200)
    private String observaciones;

    //Clave de 1 a uno
    @ManyToOne
    @JoinColumn(name = "mascota_id", nullable = false)
    private Mascota mascota;

    @OneToOne
    @JoinColumn(name = "cita_id", nullable = false, unique = true)
    private Cita cita;

}
