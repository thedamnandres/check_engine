package com.checkengine.checkengine.modelo;

import javax.persistence.*;
import javax.validation.constraints.*;
import org.openxava.annotations.*;
import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "tipo_servicio")
public class TipoServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Hidden
    private Long id;

    @Required
    @Column(length = 100)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Required
    @DecimalMin(value = "0.01", message = "La tarifa base debe ser mayor a 0")
    @Stereotype("MONEY")
    private BigDecimal tarifaBase;

    @OneToMany(mappedBy = "tipoServicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<DetalleTrabajoOrden> detallesTrabajo = new ArrayList<>();

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getTarifaBase() {
        return tarifaBase;
    }

    public void setTarifaBase(BigDecimal tarifaBase) {
        this.tarifaBase = tarifaBase;
    }

    public Collection<DetalleTrabajoOrden> getDetallesTrabajo() {
        return detallesTrabajo;
    }

    public void setDetallesTrabajo(Collection<DetalleTrabajoOrden> detallesTrabajo) {
        this.detallesTrabajo = detallesTrabajo;
    }

    // Métodos de negocio

    public BigDecimal calcularCostoHoras(BigDecimal horas) {
        return tarifaBase.multiply(horas);
    }
}
