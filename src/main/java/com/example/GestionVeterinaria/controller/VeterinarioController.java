package com.example.GestionVeterinaria.controller;

import com.example.GestionVeterinaria.entity.Cita;
import com.example.GestionVeterinaria.entity.Usuarios;
import com.example.GestionVeterinaria.entity.Veterinario;
import com.example.GestionVeterinaria.repository.CitaRepository;
import com.example.GestionVeterinaria.repository.UsuarioRepository;
import com.example.GestionVeterinaria.service.VeterinarioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("veterinarios")
@PreAuthorize("hasRole('VETERINARIO')")

public class VeterinarioController
{

    private final VeterinarioService veterinarioService;
    private final CitaRepository citaRepository;
    private final UsuarioRepository usuarioRepository;

    public VeterinarioController(VeterinarioService veterinarioService,
                                 CitaRepository citaRepository,
                                 UsuarioRepository usuarioRepository) {
        this.veterinarioService = veterinarioService;
        this.citaRepository = citaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public String listarVeterinarios(Model model,
                                     @RequestParam(name = "page", defaultValue = "0") int page){
        model.addAttribute("veterinarios",veterinarioService.listarPaginado(page, 10));
        model.addAttribute("veterinarioService",veterinarioService);
        model.addAttribute("contenido","veterinarios/listar");
         return "layout/base";
    }
    @GetMapping("/nuevo")
    public  String nuevoVeterinario(Model model){
        model.addAttribute("veterinario",new Veterinario());
        model.addAttribute("contenido", "veterinarios/formulario");
        return "layout/base";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Veterinario veterinario){
        veterinarioService.registraVeterinario(veterinario);
        return "redirect:/veterinarios";
    }
    @GetMapping("/agenda")
    public String verAgenda(Model model, Authentication authentication,
                            @RequestParam(name = "page", defaultValue = "0") int page) {

        String username = authentication.getName();

        Usuarios usuario = usuarioRepository.findByUsername(username)
                .orElseThrow();

        Long veterinarioId = usuario.getVeterinario().getId();

        // 🔥 SOLO citas programadas (paginado)
        Page<Cita> citas = citaRepository
                .findByVeterinarioIdAndEstado(veterinarioId, "Programada",
                        PageRequest.of(page, 10, Sort.by("id").ascending()));

        model.addAttribute("citas", citas);
        model.addAttribute("contenido", "veterinario/agenda");

        return "layout/base";
    }
    @GetMapping("/atendidas")
    public String verAtendidas(Model model, Authentication authentication,
                               @RequestParam(name = "page", defaultValue = "0") int page) {

        String username = authentication.getName();

        Usuarios usuario = usuarioRepository.findByUsername(username)
                .orElseThrow();

        Long veterinarioId = usuario.getVeterinario().getId();

        Page<Cita> citas = citaRepository
                .findByVeterinarioIdAndEstado(veterinarioId, "COMPLETADA",
                        PageRequest.of(page, 10, Sort.by("id").ascending()));

        model.addAttribute("citas", citas);
        model.addAttribute("contenido", "veterinario/atendidas");

        return "layout/base";
    }



}
