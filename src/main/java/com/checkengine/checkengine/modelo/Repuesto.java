package com.checkengine.checkengine.modelo;

import javax.persistence.*;
import javax.validation.constraints.*;
import org.openxava.annotations.*;
import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "repuesto")
public class Repuesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Hidden
    private Long id;

    @Column(length = 50, unique = true)
    @ReadOnly
    @SearchKey
    private String codigo;

    @Required
    @Column(length = 200)
    private String descripcion;

    @Required
    @Column(length = 50)
    private String categoria;

    @Required
    @Min(value = 0, message = "El stock actual no puede ser negativo")
    private int stockActual;

    @Required
    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private int stockMinimo;

    @Required
    @DecimalMin(value = "0.01", message = "El precio unitario debe ser mayor a 0")
    @Stereotype("MONEY")
    private BigDecimal precioUnitario;

    @Required
    @Column(length = 20)
    private String unidadMedida;

    @OneToMany(mappedBy = "repuesto", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<DetalleRepuestoOrden> detallesRepuesto = new ArrayList<>();

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getStockActual() {
        return stockActual;
    }

    public void setStockActual(int stockActual) {
        this.stockActual = stockActual;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public Collection<DetalleRepuestoOrden> getDetallesRepuesto() {
        return detallesRepuesto;
    }

    public void setDetallesRepuesto(Collection<DetalleRepuestoOrden> detallesRepuesto) {
        this.detallesRepuesto = detallesRepuesto;
    }

    // Métodos de negocio

    @PrePersist
    private void generarCodigo() {
        if (codigo == null || codigo.isEmpty()) {
            this.codigo = "REP-" + String.format("%06d", System.currentTimeMillis() % 1000000);
        }
    }

    public void actualizarStockActual(int cantidad) {
        this.stockActual += cantidad;
    }

    public boolean necesitaReposicion() {
        return stockActual <= stockMinimo;
    }
}
