package com.example.GestionVeterinaria.repository;

import com.example.GestionVeterinaria.entity.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuarios, Long> {
    Optional<Usuarios> findByUsername(String username);
    @Query("select u.rol from Usuarios u where u.id=:idUsuario")
    String buscarRolPorId(@Param("idUsuario") Long id);

}
