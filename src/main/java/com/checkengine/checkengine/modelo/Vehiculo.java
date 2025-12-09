package com.checkengine.checkengine.modelo;

import javax.persistence.*;
import org.openxava.annotations.*;
import java.util.*;

@Entity
@Table(name = "vehiculo")
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Hidden
    private Long id;

    @Required
    @Column(length = 20)
    private String placa;

    @Required
    @Column(length = 50)
    private String vin;

    @Required
    @Column(length = 50)
    private String marca;

    @Required
    @Column(length = 50)
    private String modelo;

    @Required
    private int anio;

    @Required
    @Column(length = 50)
    private String tipoCombustible;

    @Required
    private int kilometraje;

    @Required
    private boolean activo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    @DescriptionsList(descriptionProperties = "cedula, nombres, apellidos")
    @Required
    private Cliente cliente;

    @OneToMany(mappedBy = "vehiculo", cascade = CascadeType.ALL)
    private Collection<Cita> citas;

    @OneToMany(mappedBy = "vehiculo", cascade = CascadeType.ALL)
    private Collection<OrdenServicio> ordenesServicio;

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

    public String getTipoCombustible() {
        return tipoCombustible;
    }

    public void setTipoCombustible(String tipoCombustible) {
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
