package com.example.GestionVeterinaria.controller;

import com.example.GestionVeterinaria.entity.Usuarios;
import com.example.GestionVeterinaria.entity.Veterinario;
import com.example.GestionVeterinaria.repository.UsuarioRepository;
import com.example.GestionVeterinaria.service.CitaService;
import com.example.GestionVeterinaria.service.ClienteService;
import com.example.GestionVeterinaria.service.VeterinarioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final ClienteService clienteService;
    private  final VeterinarioService veterinarioService;
    private  final CitaService citaService;
    private final PasswordEncoder passwordEncoder;
    private  final UsuarioRepository usuarioRepository;


    public AdminController(ClienteService clienteService, VeterinarioService veterinarioService, CitaService citaService, PasswordEncoder passwordEncoder, UsuarioRepository usuarioRepository) {
        this.clienteService = clienteService;
        this.veterinarioService = veterinarioService;
        this.citaService = citaService;
        this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = usuarioRepository;
    }
    @GetMapping
    public String panelAdmin(Model model,
                             @RequestParam(name = "pageClientes", defaultValue = "0") int pageClientes,
                             @RequestParam(name = "pageVets", defaultValue = "0") int pageVets,
                             @RequestParam(name = "pageCitas", defaultValue = "0") int pageCitas){
        model.addAttribute("clientes", clienteService.listarPaginado(pageClientes, 10));
        model.addAttribute("veterinarios", veterinarioService.listarPaginado(pageVets, 10));
        model.addAttribute("citas", citaService.listarPaginado(pageCitas, 10));
        model.addAttribute("contenido", "admin/panel");
        return "layout/base";
    }

    @GetMapping("/eliminarCliente/{id}")
    public String eliminarCliente(@PathVariable Long id){
       clienteService.eliminar(id);
        return "redirect:/admin";
    }
    @GetMapping("/eliminarCita/{id}")
    public  String eliminarCita(@PathVariable Long id){
        citaService.eliminarCita(id);
        return "redirect:/admin";
    }
    @GetMapping("/eliminarVeterinario/{id}")
    public  String eliminarVeterinario(@PathVariable Long id){
    veterinarioService.eliminarVeterinario(id);
        return "redirect:/admin";
    }


    // Mostrar formulario
    @GetMapping("/veterinarios/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("veterinario", new Veterinario());
        model.addAttribute("contenido","admin/crear_veterinario");
        return "layout/base";
    }

    @PostMapping("/veterinarios/guardar")
    public String guardarVeterinario(@ModelAttribute Veterinario veterinario,
                                     @RequestParam String username,
                                     @RequestParam String password) {

        Veterinario veterinarioGuardado = veterinarioService.registraVeterinario(veterinario);

        Usuarios usuarios = new Usuarios();
        usuarios.setUsername(username);
        usuarios.setPassword(passwordEncoder.encode(password));
        usuarios.setRol("VETERINARIO");
        usuarios.setVeterinario(veterinarioGuardado);
        usuarioRepository.save(usuarios);
        return "redirect:/admin";
    }



}
