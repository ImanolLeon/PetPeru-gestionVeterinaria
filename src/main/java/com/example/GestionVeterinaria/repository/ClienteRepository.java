package com.example.GestionVeterinaria.repository;

import com.example.GestionVeterinaria.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente,Long> {
    //Verificar si existe el dni
    boolean existsByDni(String dni);

    //Verificar si el dni lo tiene OTRO cliente (ignora el que se está editando)
    boolean existsByDniAndIdNot(String dni, Long id);

    @Query("select count(c) from Cliente c")
    Long countTodosLosUsuarios();



}
