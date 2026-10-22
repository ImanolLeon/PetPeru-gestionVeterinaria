package com.example.GestionVeterinaria.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Página de inicio de sesión.
 *
 * Solo atiende el GET: el POST de /login lo procesa Spring Security
 * (UsernamePasswordAuthenticationFilter, ver {@code HttpSecurityConfig}).
 */
@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
