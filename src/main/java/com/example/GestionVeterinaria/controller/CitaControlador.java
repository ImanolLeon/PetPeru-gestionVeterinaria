package com.example.GestionVeterinaria.controller;

import com.example.GestionVeterinaria.entity.Cita;
import com.example.GestionVeterinaria.repository.VeterinarioRepository;
import com.example.GestionVeterinaria.service.CitaService;
import com.example.GestionVeterinaria.service.MascotaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/citas")
public class CitaControlador {

    private final CitaService citaService;
    private final MascotaService mascotaService;
    private  final VeterinarioRepository veterinarioRepository;

    public CitaControlador(CitaService citaService, MascotaService mascotaService, VeterinarioRepository veterinarioRepository) {
        this.citaService = citaService;
        this.mascotaService = mascotaService;
        this.veterinarioRepository = veterinarioRepository;

    }

    @GetMapping
    public String listarCitas(Model model,
                              @RequestParam(name = "page", defaultValue = "0") int page){
        model.addAttribute("contenido","citas/listar");
        model.addAttribute("citas",citaService.listarPaginado(page, 10));
        return "layout/base";

    }

    @GetMapping("/nueva")
    public String nuevaCita(Model model){
        model.addAttribute("contenido","citas/formulario");
        model.addAttribute("cita",new Cita());
        model.addAttribute("mascotas",mascotaService.listarTodas());
        model.addAttribute("veterinarios",veterinarioRepository.findAll());
        return "layout/base";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Cita cita,
                          @RequestParam Long mascotaId,
                          @RequestParam Long veterinarioId,
                          RedirectAttributes redirectAttributes){

        try {
            citaService.registrarCita(cita, mascotaId, veterinarioId);
            redirectAttributes.addFlashAttribute("exito", "Cita registrada correctamente");
            return "redirect:/citas";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/citas/nueva";
        }

    }

    @PostMapping("/eliminar/{id}")
    public String eliminarCita(@PathVariable Long id){
        citaService.eliminarCita(id);
        return "redirect:/citas";
    }


}
