package com.example.GestionVeterinaria.controller;
import com.example.GestionVeterinaria.entity.Mascota;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import com.example.GestionVeterinaria.entity.Cliente;
import com.example.GestionVeterinaria.service.ClienteService;
import com.example.GestionVeterinaria.service.MascotaService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Controller
@RequestMapping("/clientes/{clienteId}/mascotas")
public class MascotaController {

    private final MascotaService mascotaService;
    private final ClienteService clienteService;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public MascotaController(MascotaService mascotaService,
                             ClienteService clienteService) {
        this.mascotaService = mascotaService;
        this.clienteService = clienteService;
    }

    // Listar mascotas del cliente
    @GetMapping
    public String listarMascotas(@PathVariable Long clienteId, Model model,
                                 @RequestParam(name = "page", defaultValue = "0") int page) {

        Cliente cliente = clienteService.buscarPorId(clienteId);

        model.addAttribute("cliente", cliente);
        model.addAttribute("mascotas",
                mascotaService.listarPorCliente(clienteId, page, 8));
        model.addAttribute("contenido","mascotas/listar");

        return "layout/base";
    }

    // Mostrar formulario para registrar mascota
    @GetMapping("/nueva")
    public String mostrarFormulario(@PathVariable Long clienteId, Model model) {

        model.addAttribute("mascota", new Mascota());
        model.addAttribute("clienteId", clienteId);
        model.addAttribute("contenido","mascotas/formulario");
        return "layout/base";
    }

    // Guardar mascota
    @PostMapping("/guardar")
    public String guardarMascota(@PathVariable Long clienteId,
                                 @ModelAttribute Mascota mascota,
                                 BindingResult resultado,
                                 @RequestParam(value = "archivoImagen", required = false) MultipartFile imagen,
                                 Model model,
                                 RedirectAttributes redirect
    ) throws IOException {

        // Si algún campo no se pudo convertir (p.ej. edad/peso vacíos),
        // volvemos al formulario en lugar de devolver un error 400.
        if (resultado.hasErrors()) {
            model.addAttribute("clienteId", clienteId);
            model.addAttribute("contenido", "mascotas/formulario");
            return "layout/base";
        }

        // La imagen es opcional: solo se guarda si el usuario subió un archivo.
        if (imagen != null && !imagen.isEmpty()) {

            //Configuraciones para imagenes
            Path directorio = Paths.get(uploadDir, "mascotas");
            Files.createDirectories(directorio);

            //Obtener extensión
            String nombreOriginal = imagen.getOriginalFilename();
            String extension = "";
            if (nombreOriginal != null && nombreOriginal.contains(".")) {
                extension = nombreOriginal.substring(
                        //Lo que hay después de un punto
                        nombreOriginal.lastIndexOf(".")
                );
            }

            String nombreArchivo = UUID.randomUUID() + extension;
            Path rutaArchivo = directorio.resolve(nombreArchivo);

            Files.copy(
                    imagen.getInputStream(),
                    rutaArchivo, StandardCopyOption.REPLACE_EXISTING
            );

            mascota.setImagen("/uploads/mascotas/" + nombreArchivo);
        }

        // Si llega un id es una edición; si no, una mascota nueva.
        if (mascota.getId() == null) {
            mascotaService.registrar(mascota, clienteId);
            redirect.addFlashAttribute("exito", "Mascota guardada correctamente.");
        } else {
            mascotaService.actualizar(mascota);
            redirect.addFlashAttribute("exito", "Mascota actualizada correctamente.");
        }

        return "redirect:/clientes/" + clienteId + "/mascotas";

    }

    // Mostrar el formulario con los datos de una mascota existente
    @GetMapping("/{mascotaId}/editar")
    public String mostrarEdicion(@PathVariable Long clienteId,
                                 @PathVariable Long mascotaId,
                                 Model model) {

        model.addAttribute("mascota", mascotaService.buscarPorId(mascotaId));
        model.addAttribute("clienteId", clienteId);
        model.addAttribute("contenido", "mascotas/formulario");
        return "layout/base";
    }

    // Eliminar mascota
    @PostMapping("/{mascotaId}/eliminar")
    public String eliminarMascota(@PathVariable Long clienteId,
                                  @PathVariable Long mascotaId,
                                  RedirectAttributes redirect) {

        mascotaService.eliminarMascota(mascotaId);
        redirect.addFlashAttribute("exito", "Mascota eliminada correctamente.");

        return "redirect:/clientes/" + clienteId + "/mascotas";
    }

}
