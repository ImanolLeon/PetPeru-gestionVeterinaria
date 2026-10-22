package com.example.GestionVeterinaria.controller;

import com.example.GestionVeterinaria.entity.Cliente;
import com.example.GestionVeterinaria.exception.DniInvalidoException;
import com.example.GestionVeterinaria.service.ClienteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clientes")
public class ClienteController {
    private  final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    //listarClientes
    @GetMapping()
    public String listar(Model model,
                         @RequestParam(name = "page", defaultValue = "0") int page){
        model.addAttribute("clientes",clienteService.listarPaginado(page, 10));
        // Necesario para el modal "Registrar cliente" (fragmento clientes/formulario).
        // Si venimos de un DNI repetido, el cliente ya llega en el flash y se respeta.
        if (!model.containsAttribute("cliente")){
            model.addAttribute("cliente", new Cliente());
        }
        model.addAttribute("contenido", "clientes/listar");
        return "layout/base";
    }

    @GetMapping("/nuevo")
    public  String nuevo (Model model){
        model.addAttribute("cliente",new Cliente());
        model.addAttribute("contenido","clientes/formulario");
        return "layout/base";
    }

    //Aviso en vivo del formulario: responde "true" si el DNI ya es de otro cliente
    @GetMapping("/disponible")
    @ResponseBody
    public String dniDisponible(@RequestParam(name = "dni", required = false) String dni,
                                @RequestParam(name = "id", required = false) Long id){
        return clienteService.existeDni(dni, id) ? "true" : "false";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Cliente cliente, RedirectAttributes redirect){

        try {
            clienteService.registrar(cliente);
        } catch (DniInvalidoException e){
            // Volvemos a la lista conservando lo escrito y reabriendo el modal
            redirect.addFlashAttribute("cliente", cliente);
            redirect.addFlashAttribute("errorDni", e.getMessage());
            redirect.addFlashAttribute("abrirModalRegistrar", true);
            return "redirect:/clientes";
        }

        redirect.addFlashAttribute("exito", "Cliente registrado correctamente.");
        return "redirect:/clientes";
    }

    //Editar un cliente desde el modal del dashboard
    @PostMapping("/editar")
    public String editar(@ModelAttribute Cliente cliente, RedirectAttributes redirect){

        try {
            clienteService.actualizar(cliente);
        } catch (DniInvalidoException e){
            // Volvemos al inicio conservando lo escrito y reabriendo el modal
            redirect.addFlashAttribute("clienteEditado", cliente);
            redirect.addFlashAttribute("errorDniEdit", e.getMessage());
            redirect.addFlashAttribute("abrirModalEditar", true);
            return "redirect:/panel";
        }

        redirect.addFlashAttribute("exito", "Cliente actualizado correctamente.");
        return "redirect:/panel";
    }

}
