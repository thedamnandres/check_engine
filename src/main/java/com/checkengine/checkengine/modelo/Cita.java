package com.checkengine.checkengine.modelo;

import javax.persistence.*;
import javax.validation.constraints.*;
import org.openxava.annotations.*;
import java.util.*;

@Entity
@Table(name = "cita")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Hidden
    private Long id;

    @Required
    @Stereotype("DATETIME")
    @Future(message = "La fecha y hora de la cita debe ser futura")
    @Column(nullable = false)
    private Date fechaHora;

    @Enumerated(EnumType.STRING)
    @Required
    @Column(length = 20, nullable = false)
    private EstadoCita estado;

    @Size(max = 500, message = "Los comentarios no pueden exceder 500 caracteres")
    @Column(length = 500)
    private String comentarios;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @DescriptionsList(descriptionProperties = "cedula, nombres, apellidos")
    @Required
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    @DescriptionsList(descriptionProperties = "placa, marca, modelo")
    @Required
    private Vehiculo vehiculo;

    @OneToOne(mappedBy = "cita")
    private OrdenServicio ordenServicio;

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(Date fechaHora) {
        this.fechaHora = fechaHora;
    }

    public EstadoCita getEstado() {
        return estado;
    }

    public void setEstado(EstadoCita estado) {
        this.estado = estado;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public OrdenServicio getOrdenServicio() {
        return ordenServicio;
    }

    public void setOrdenServicio(OrdenServicio ordenServicio) {
        this.ordenServicio = ordenServicio;
    }

    // Métodos de negocio

    public void cancelar() {
        this.estado = EstadoCita.CANCELADA;
    }

    public void marcarAtendida() {
        this.estado = EstadoCita.ATENDIDA;
    }
}
