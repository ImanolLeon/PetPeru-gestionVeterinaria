package com.example.GestionVeterinaria.controller;

import com.example.GestionVeterinaria.entity.Cita;
import com.example.GestionVeterinaria.entity.HistorialClinico;
import com.example.GestionVeterinaria.repository.CitaRepository;
import com.example.GestionVeterinaria.repository.HistorialClinicoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/historial")
public class HistorialController {
    private final CitaRepository citaRepository;
    private  final HistorialClinicoRepository historialRepository;
    public HistorialController(CitaRepository citaRepository, HistorialClinicoRepository historialRepository) {
        this.citaRepository = citaRepository;
        this.historialRepository = historialRepository;
    }

    @GetMapping("/crear/{id}")
    public String mostrarFormulario(@PathVariable Long id, Model model) {

        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        HistorialClinico historial = new HistorialClinico();
        historial.setMascota(cita.getMascota());

        model.addAttribute("historial", historial);
        model.addAttribute("citaId", id);

        return "historial/formulario";
    }


    @PostMapping("/guardar")
    public String guardarHistorial(HistorialClinico historial,
                                   @RequestParam Long citaId) {

        Cita cita = citaRepository.findById(citaId)
                .orElseThrow();

        historial.setMascota(cita.getMascota());
        historial.setCita(cita);
        historial.setFechaConsulta(LocalDate.now());

        cita.setEstado("COMPLETADA");

        historialRepository.save(historial);
        citaRepository.save(cita);

        return "redirect:/veterinarios/agenda";
    }
}
