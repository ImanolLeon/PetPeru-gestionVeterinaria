package com.example.GestionVeterinaria.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Landing pública del sitio "Care Pet Clinic".
 *
 * <p>Atiende el GET de "/" y devuelve la plantilla {@code landing/landing},
 * que es autónoma (no usa {@code layout/base.html} del panel).
 *
 * <p>Cada sección tiene además su propia ruta pública ("/servicios",
 * "/contacto", "/ubicacion", ...): devuelven la misma plantilla y el JS de la
 * landing (static/JS/landing.js) se encarga de dejar el scroll en la sección
 * correspondiente y de actualizar la URL al navegar.
 *
 * <p>Spring Security las deja abiertas al público (ver
 * {@code HttpSecurityConfig}); una vez autenticado, la misma plantilla
 * deja de mostrar el botón de iniciar sesión.
 *
 * <p>La landing NO ofrece ningún enlace ni botón al panel: el acceso al
 * panel es solo por URL directa ("/panel").
 *
 * <p>El panel en sí vive en "/panel" (ver {@code HomeController}).
 */
@Controller
public class LandingController {

    @Value("${web3forms.access-key:}")
    private String web3formsAccessKey;

    /** Rutas públicas de la landing: la home y una por sección. */
    @GetMapping({"/", "/inicio", "/servicios", "/pacientes", "/contacto", "/ubicacion"})
    public String landing(Model model) {
        model.addAttribute("web3formsAccessKey", web3formsAccessKey);
        return "landing/landing";
    }
}
