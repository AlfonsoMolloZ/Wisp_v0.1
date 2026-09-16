package com.wisp.app.dto;

import java.time.LocalDate;

public class StockDto {
    private Long id;
    private String equipo;
    private String marca;
    private String modelo;
    private Integer cantidad;
    private String estado;
    private LocalDate ultimoIngreso;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEquipo() {
        return equipo;
    }

    public void setEquipo(String equipo) {
        this.equipo = equipo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDate getUltimoIngreso() {
        return ultimoIngreso;
    }

    public void setUltimoIngreso(LocalDate ultimoIngreso) {
        this.ultimoIngreso = ultimoIngreso;
    }
}
