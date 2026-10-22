package com.example.GestionVeterinaria.controller;

import com.example.GestionVeterinaria.entity.Cliente;
import com.example.GestionVeterinaria.service.CitaService;
import com.example.GestionVeterinaria.service.ClienteService;
import com.example.GestionVeterinaria.service.GaleriaService;
import com.example.GestionVeterinaria.service.MascotaService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/panel")
@AllArgsConstructor
public class HomeController {

    // Tamaño de página de la tabla de clientes del inicio
    private static final int TAM_PAGINA = 8;

    private final CitaService citaService;
    private final ClienteService clienteService;
    private final MascotaService mascotaService;
    private final GaleriaService galeriaService;

    @GetMapping()
    public String home(Model model,
                       @RequestParam(name = "page", defaultValue = "0") int page) {
        model.addAttribute("contenido", "home/home");
        // Necesario para el modal "Registrar cliente" (fragmento clientes/formulario)
        model.addAttribute("cliente", new Cliente());
        // Imágenes de static/img para la galería del banner
        model.addAttribute("galeria", galeriaService.listar());
        // Datos del dashboard
        model.addAttribute("clientes", clienteService.listarPaginado(page, TAM_PAGINA));
        model.addAttribute("contarCliente", clienteService.mostrarCantidadClientes());
        model.addAttribute("contarMascota", mascotaService.contarMascotas());
        model.addAttribute("contarCitas", citaService.contarProgramadas());
        return "layout/base";
    }
}
