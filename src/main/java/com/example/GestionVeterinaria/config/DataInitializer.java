package com.example.GestionVeterinaria.config;

import com.example.GestionVeterinaria.entity.Usuarios;
import com.example.GestionVeterinaria.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initUsers(UsuarioRepository usuarioRepository,
                                PasswordEncoder passwordEncoder) {

        return args -> {

            if (usuarioRepository.count() == 0) {

                // ADMIN
                Usuarios admin = new Usuarios();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRol("ADMIN");

                // RECEPCION (antes USER)
                Usuarios recepcion = new Usuarios();
                recepcion.setUsername("recepcion");
                recepcion.setPassword(passwordEncoder.encode("recepcion123"));
                recepcion.setRol("RECEPCION");

                usuarioRepository.save(admin);
                usuarioRepository.save(recepcion);
            }
        };
    }
}
