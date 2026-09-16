package com.wisp.app.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "instalaciones")
public class Instalacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipo;
    private String direccion;
    private String estado;
    
    private LocalDateTime fechaRegistro;      

private LocalDateTime fechaAtencion;     

private Boolean cobrarInstalacion;       

private Double montoInstalacion;          

private String metodoPago;                

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

private String observacion;               // Observaciones del técnico

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente preCliente;

    public String getPreClienteNombre() {
        return preCliente != null ? preCliente.getNombre() : "";
    }

    public Long getId() {
        return id;
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

    public void setEstado(String estado) {
        this.estado = estado;
    }


    public Cliente getPreCliente() {
        return preCliente;
    }

    public void setPreCliente(Cliente preCliente) {
        this.preCliente = preCliente;
    }
}