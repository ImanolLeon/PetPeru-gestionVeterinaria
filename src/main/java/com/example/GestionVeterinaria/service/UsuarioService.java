package com.example.GestionVeterinaria.service;

import com.example.GestionVeterinaria.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UsuarioService {

    private UsuarioRepository usuarioRepository;

    public String buscarRolPorId(Long id){
        return usuarioRepository.buscarRolPorId(id);
    }
}
