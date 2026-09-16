package com.wisp.app.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class InstalacionDto {
    private String nombreCliente;
    private Long id;
    private String tipo;
    private String direccion;
    private String estado;
    private LocalDate fecha;
    private Long clienteId;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaAtencion;

    private Boolean cobrarInstalacion;
    private Double montoInstalacion;

    private String metodoPago;
    private String observacion;

    public Long getId() {
        return id;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public LocalDateTime getFechaAtencion() {
        return fechaAtencion;
    }

    public void setFechaAtencion(LocalDateTime fechaAtencion) {
        this.fechaAtencion = fechaAtencion;
    }

    public Boolean getCobrarInstalacion() {
        return cobrarInstalacion;
    }

    public void setCobrarInstalacion(Boolean cobrarInstalacion) {
        this.cobrarInstalacion = cobrarInstalacion;
    }

    public Double getMontoInstalacion() {
        return montoInstalacion;
    }

    public void setMontoInstalacion(Double montoInstalacion) {
        this.montoInstalacion = montoInstalacion;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEstado() {
        return estado;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }
}
