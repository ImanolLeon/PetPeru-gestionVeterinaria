package com.example.GestionVeterinaria.repository;

import com.example.GestionVeterinaria.entity.Mascota;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota,Long> {
    List<Mascota> findByClienteId(Long clienteId);

    // Versión paginada (para el listado de mascotas de un cliente)
    Page<Mascota> findByClienteId(Long clienteId, Pageable pageable);

}
