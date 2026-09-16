package com.wisp.app.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class TestUser {

    public static void main(String[] args) {

        String Contraseña = "123";

        String hashed = hashPassword(Contraseña);

        System.out.println("Contraseña hasheada: " + hashed);
    }

    public static String hashPassword(String Contraseña) {

        PasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(Contraseña);
    }
}