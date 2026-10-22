package com.example.GestionVeterinaria.repository;

import com.example.GestionVeterinaria.entity.Veterinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VeterinarioRepository extends JpaRepository<Veterinario,Long> {
    void deleteById(Long id);

    @Query("select count(v) from Veterinario v")
    long contarVeterinarios();
}
