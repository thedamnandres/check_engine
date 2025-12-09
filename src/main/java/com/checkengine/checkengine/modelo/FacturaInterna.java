package com.checkengine.checkengine.modelo;

import javax.persistence.*;
import org.openxava.annotations.*;
import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "factura_interna")
public class FacturaInterna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Hidden
    private Long id;

    @Required
    @Column(length = 50)
    private String numero;

    @Required
    @Stereotype("DATE")
    private Date fechaEmision;

    @Required
    @Stereotype("MONEY")
    private BigDecimal subtotalManoObra;

    @Required
    @Stereotype("MONEY")
    private BigDecimal subtotalRepuestos;

    @Required
    @Stereotype("MONEY")
    private BigDecimal iva;

    @Required
    @Stereotype("MONEY")
    private BigDecimal total;

    @OneToOne
    @JoinColumn(name = "orden_servicio_id")
    @DescriptionsList(descriptionProperties = "codigo")
    @Required
    private OrdenServicio ordenServicio;

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public BigDecimal getSubtotalManoObra() {
        return subtotalManoObra;
    }

    public void setSubtotalManoObra(BigDecimal subtotalManoObra) {
        this.subtotalManoObra = subtotalManoObra;
    }

    public BigDecimal getSubtotalRepuestos() {
        return subtotalRepuestos;
    }

    public void setSubtotalRepuestos(BigDecimal subtotalRepuestos) {
        this.subtotalRepuestos = subtotalRepuestos;
    }

    public BigDecimal getIva() {
        return iva;
    }

    public void setIva(BigDecimal iva) {
        this.iva = iva;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public OrdenServicio getOrdenServicio() {
        return ordenServicio;
    }

    public void setOrdenServicio(OrdenServicio ordenServicio) {
        this.ordenServicio = ordenServicio;
    }

    // Métodos de negocio

    @PrePersist
    @PreUpdate
    public void calcularTotales() {
        if (ordenServicio != null) {
            // Calcular subtotal de mano de obra
            subtotalManoObra = BigDecimal.ZERO;
            if (ordenServicio.getDetallesTrabajo() != null) {
                for (DetalleTrabajoOrden detalle : ordenServicio.getDetallesTrabajo()) {
                    subtotalManoObra = subtotalManoObra.add(detalle.getSubtotal());
                }
            }

            // Calcular subtotal de repuestos
            subtotalRepuestos = BigDecimal.ZERO;
            if (ordenServicio.getDetallesRepuesto() != null) {
                for (DetalleRepuestoOrden detalle : ordenServicio.getDetallesRepuesto()) {
                    subtotalRepuestos = subtotalRepuestos.add(detalle.getSubtotal());
                }
            }

            // Calcular IVA (15%)
            BigDecimal subtotalGeneral = subtotalManoObra.add(subtotalRepuestos);
            iva = subtotalGeneral.multiply(BigDecimal.valueOf(0.15));

            // Calcular total
            total = subtotalGeneral.add(iva);
        }
    }
}
