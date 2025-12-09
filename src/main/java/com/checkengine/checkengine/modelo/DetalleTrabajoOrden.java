package com.checkengine.checkengine.modelo;

import javax.persistence.*;
import org.openxava.annotations.*;
import java.math.BigDecimal;

@Entity
@Table(name = "detalle_trabajo_orden")
public class DetalleTrabajoOrden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Hidden
    private Long id;

    @Column(length = 500)
    private String descripcion;

    @Required
    @Stereotype("MONEY")
    private BigDecimal horas;

    @Required
    @Stereotype("MONEY")
    private BigDecimal tarifaHora;

    @Required
    @Stereotype("MONEY")
    private BigDecimal subtotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_servicio_id")
    @Required
    private OrdenServicio ordenServicio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_servicio_id")
    @DescriptionsList(descriptionProperties = "nombre")
    @Required
    private TipoServicio tipoServicio;

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getHoras() {
        return horas;
    }

    public void setHoras(BigDecimal horas) {
        this.horas = horas;
    }

    public BigDecimal getTarifaHora() {
        return tarifaHora;
    }

    public void setTarifaHora(BigDecimal tarifaHora) {
        this.tarifaHora = tarifaHora;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public OrdenServicio getOrdenServicio() {
        return ordenServicio;
    }

    public void setOrdenServicio(OrdenServicio ordenServicio) {
        this.ordenServicio = ordenServicio;
    }

    public TipoServicio getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(TipoServicio tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    // Métodos de negocio

    @PrePersist
    @PreUpdate
    public void calcularSubtotal() {
        if (horas != null && tarifaHora != null) {
            this.subtotal = horas.multiply(tarifaHora);
        }
    }
}
