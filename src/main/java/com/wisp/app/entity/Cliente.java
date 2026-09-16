package com.wisp.app.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombres;

    @Column(nullable = false)
    private String apellidos;

    @Column(nullable = false, unique = true)
    private String dni;

    @Column(nullable = false)
    private String telefono;

    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false, unique = true)
    private String correo;

    @Column(nullable = false)
    private String estado;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private Planes plan;

    @ManyToOne
    @JoinColumn(name = "red_wan_id", nullable = true)
    private RedWan redWan;

    @Column(nullable = false)
    private String ip;

    

    private LocalDateTime fechaSuspension;
    public Cliente() {
    }

    public LocalDateTime getFechaSuspension() {
        return fechaSuspension;
    }

    public void setFechaSuspension(LocalDateTime fechaSuspension) {
        this.fechaSuspension = fechaSuspension;
    }

    // Método que usan Averia e Instalacion para mostrar el nombre completo
    public String getNombre() {
        return nombres + " " + apellidos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Planes getPlan() {
        return plan;
    }

    public void setPlan(Planes plan) {
        this.plan = plan;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public RedWan getRedWan() {
        return redWan;
    }

    public void setRedWan(RedWan redWan) {
        this.redWan = redWan;
    }
}