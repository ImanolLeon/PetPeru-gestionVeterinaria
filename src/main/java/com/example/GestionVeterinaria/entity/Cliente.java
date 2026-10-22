package com.example.GestionVeterinaria.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Clientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 8 ,nullable = false, unique = true)
    private String dni;

    @Column(length = 100,nullable = false)
    private String nombre;

    @Column(length = 100,nullable = false)
    private  String apellido;

    @Column(length = 9)
    private String telefono;

    @Column(length = 100)
    private  String email;

    @Column(length = 200)
    private String direccion;

    @Column (name = "fecha_Registro")
    private LocalDate fechRegistro;

    @OneToMany(mappedBy = "cliente",cascade = CascadeType.ALL)
    private List<Mascota> mascotas;

}








