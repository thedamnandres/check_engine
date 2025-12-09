package com.checkengine.checkengine.modelo;

import javax.persistence.*;
import org.openxava.annotations.*;
import java.util.*;

@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Hidden
    private Long id;

    @Required
    @Column(length = 20)
    private String cedula;

    @Required
    @Column(length = 100)
    private String nombres;

    @Required
    @Column(length = 100)
    private String apellidos;

    @Column(length = 200)
    private String direccion;

    @Required
    @Column(length = 20)
    private String telefono;

    @Column(length = 100)
    @Stereotype("EMAIL")
    private String email;

    @Column(length = 50)
    private String medioContacto;

    @Column(length = 20)
    private String estado;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    @ListProperties("placa, vin, marca, modelo, anio")
    private Collection<Vehiculo> vehiculos;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private Collection<Cita> citas;

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMedioContacto() {
        return medioContacto;
    }

    public void setMedioContacto(String medioContacto) {
        this.medioContacto = medioContacto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Collection<Vehiculo> getVehiculos() {
        return vehiculos;
    }

    public void setVehiculos(Collection<Vehiculo> vehiculos) {
        this.vehiculos = vehiculos;
    }

    public Collection<Cita> getCitas() {
        return citas;
    }

    public void setCitas(Collection<Cita> citas) {
        this.citas = citas;
    }

    // Métodos de negocio

    public void actualizarDatos() {
        // Lógica para actualizar datos del cliente
    }

    public void registrarVehiculo() {
        // Lógica para registrar un nuevo vehículo
    }

    public void agendarCita() {
        // Lógica para agendar una cita
    }
}
