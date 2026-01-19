package com.checkengine.checkengine.modelo;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import org.openxava.annotations.*;
import org.openxava.calculators.*;
import org.openxava.jpa.XPersistence;
import com.checkengine.checkengine.calculadores.*;
import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "factura_interna")
@View(members =
    "numero, fechaEmision;" +
    "ordenServicio;" +
    "Totales [subtotalManoObra, subtotalRepuestos; porcentajeIVA, iva; total]"
)
public class FacturaInterna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Hidden
    private Long id;

    @Hidden
    @Column(length = 4, nullable = false)
    private int year = 0;

    @Hidden
    @Column(length = 5, nullable = false)
    private int numeroSecuencial = 0;

    @ReadOnly
    @Column(length = 50, unique = true)
    @SearchKey
    private String numero;

    @Required
    @Stereotype("DATE")
    private Date fechaEmision;

    @ReadOnly
    @Stereotype("MONEY")
    private BigDecimal subtotalManoObra;

    @ReadOnly
    @Stereotype("MONEY")
    private BigDecimal subtotalRepuestos;

    @ReadOnly
    @Digits(integer = 2, fraction = 2)
    @Column(precision = 5, scale = 2)
    private BigDecimal porcentajeIVA = new BigDecimal("15.00");

    @ReadOnly
    @Stereotype("MONEY")
    private BigDecimal iva;

    @ReadOnly
    @Stereotype("MONEY")
    private BigDecimal total;

    @OneToOne
    @JoinColumn(name = "orden_servicio_id")
    @DescriptionsList(descriptionProperties = "codigo, vehiculo.placa, vehiculo.cliente.nombres, vehiculo.cliente.apellidos")
    @ReferenceView("EMBEDDED")
    @Required
    private OrdenServicio ordenServicio;

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getNumeroSecuencial() {
        return numeroSecuencial;
    }

    public void setNumeroSecuencial(int numeroSecuencial) {
        this.numeroSecuencial = numeroSecuencial;
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

    public BigDecimal getPorcentajeIVA() {
        return porcentajeIVA;
    }

    public void setPorcentajeIVA(BigDecimal porcentajeIVA) {
        this.porcentajeIVA = porcentajeIVA;
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
    private void antesDeGuardar() {
        generarNumero();
        calcularTotales();
    }

    @PreUpdate
    private void antesDeActualizar() {
        calcularTotales();
    }

    private void generarNumero() {
        if (year == 0) {
            this.year = java.time.LocalDate.now().getYear();
        }
        if (numeroSecuencial == 0) {
            try {
                Query query = XPersistence.getManager()
                    .createQuery("SELECT MAX(f.numeroSecuencial) FROM FacturaInterna f WHERE f.year = :year");
                query.setParameter("year", year);
                Integer lastNumber = (Integer) query.getSingleResult();
                this.numeroSecuencial = lastNumber == null ? 1 : lastNumber + 1;
            } catch (Exception e) {
                this.numeroSecuencial = 1;
            }
        }
        if (numero == null || numero.isEmpty()) {
            this.numero = "FACT-" + year + "-" + String.format("%05d", numeroSecuencial);
        }
    }

    private void calcularTotales() {
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

            // Calcular IVA según el porcentaje configurado
            BigDecimal subtotalGeneral = subtotalManoObra.add(subtotalRepuestos);
            if (porcentajeIVA != null) {
                iva = subtotalGeneral.multiply(porcentajeIVA).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
            } else {
                // Si no hay porcentaje, usar 15% por defecto
                porcentajeIVA = new BigDecimal("15.00");
                iva = subtotalGeneral.multiply(porcentajeIVA).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
            }

            // Calcular total
            total = subtotalGeneral.add(iva);
        }
    }
}
