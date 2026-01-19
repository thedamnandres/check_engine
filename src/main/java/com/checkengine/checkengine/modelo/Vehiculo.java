package com.checkengine.checkengine.modelo;

import javax.persistence.*;
import javax.validation.constraints.*;
import org.openxava.annotations.*;
import java.util.*;

@Entity
@Table(name = "vehiculo", uniqueConstraints = {
    @UniqueConstraint(columnNames = "placa", name = "uk_vehiculo_placa"),
    @UniqueConstraint(columnNames = "vin", name = "uk_vehiculo_vin")
})
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Hidden
    private Long id;

    @Required
    @Column(length = 20, unique = true, nullable = false)
    @Pattern(regexp = "^[A-Z]{3}-\\d{3,4}$", message = "La placa debe tener el formato ABC-123 o ABC-1234")
    private String placa;

    @Required
    @Column(length = 17, unique = true, nullable = false)
    @Pattern(regexp = "^[A-Z0-9]{17}$", message = "El VIN debe tener exactamente 17 caracteres alfanuméricos (solo letras y números)")
    private String vin;

    @Required
    @Column(length = 50, nullable = false)
    private String marca;

    @Required
    @Column(length = 50, nullable = false)
    private String modelo;

    @Required
    @Min(value = 1985, message = "El año debe ser mayor o igual a 1985")
    @Max(value = 2026, message = "El año no puede ser mayor a 2026")
    @Column(nullable = false)
    private int anio;

    @Required
    @Column(length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoCombustible tipoCombustible;

    @Required
    @Min(value = 0, message = "El kilometraje no puede ser negativo")
    @Max(value = 500000, message = "El kilometraje no puede superar 500,000 km")
    @Column(nullable = false)
    private int kilometraje;

    @Required
    @Column(nullable = false)
    private boolean activo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @DescriptionsList(descriptionProperties = "cedula, nombres, apellidos")
    @Required
    private Cliente cliente;

    @OneToMany(mappedBy = "vehiculo", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<Cita> citas = new ArrayList<>();

    @OneToMany(mappedBy = "vehiculo", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<OrdenServicio> ordenesServicio = new ArrayList<>();

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
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

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public TipoCombustible getTipoCombustible() {
        return tipoCombustible;
    }

    public void setTipoCombustible(TipoCombustible tipoCombustible) {
        this.tipoCombustible = tipoCombustible;
    }

    public int getKilometraje() {
        return kilometraje;
    }

    public void setKilometraje(int kilometraje) {
        this.kilometraje = kilometraje;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Collection<Cita> getCitas() {
        return citas;
    }

    public void setCitas(Collection<Cita> citas) {
        this.citas = citas;
    }

    public Collection<OrdenServicio> getOrdenesServicio() {
        return ordenesServicio;
    }

    public void setOrdenesServicio(Collection<OrdenServicio> ordenesServicio) {
        this.ordenesServicio = ordenesServicio;
    }

    // Métodos de negocio

    public void actualizarKilometraje(int nuevoKilometraje) {
        this.kilometraje = nuevoKilometraje;
    }

    public void desactivar() {
        this.activo = false;
    }
}
