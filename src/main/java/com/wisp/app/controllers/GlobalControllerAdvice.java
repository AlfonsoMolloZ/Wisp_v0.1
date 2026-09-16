package com.wisp.app.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
// controladpor para las excepciones globales
//para manejra los roles

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("rol")
    public String obtenerRol(Authentication authentication) {

        if (authentication == null) {
            return "";
        }

        return authentication.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.replace("ROLE_", ""))
                .findFirst()
                .orElse("");
    }
}