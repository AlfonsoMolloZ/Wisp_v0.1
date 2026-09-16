package com.wisp.app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    /**
     * Muestra la página de login personalizada.
     * Spring Security procesa el POST /login automáticamente.
     * Los parámetros ?error y ?logout los maneja Thymeleaf con th:if="${param.error}".
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}