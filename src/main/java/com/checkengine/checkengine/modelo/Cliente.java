package com.checkengine.checkengine.modelo;

import javax.persistence.*;
import javax.validation.constraints.*;
import org.openxava.annotations.*;
import java.util.*;

@Entity
@Table(name = "cliente", uniqueConstraints = {
    @UniqueConstraint(columnNames = "cedula", name = "uk_cliente_cedula")
})
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Hidden
    private Long id;

    @Required
    @Column(length = 10, unique = true, nullable = false)
    @Pattern(regexp = "^\\d{10}$", message = "La cédula debe contener exactamente 10 dígitos numéricos")
    @PropertyValidator(value = CedulaEcuatorianaValidator.class,
                       message = "La cédula ingresada no es válida según el algoritmo ecuatoriano")
    private String cedula;

    @Required
    @Column(length = 100, nullable = false)
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,100}$", message = "Los nombres solo pueden contener letras y espacios (mínimo 2 caracteres)")
    private String nombres;

    @Required
    @Column(length = 100, nullable = false)
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,100}$", message = "Los apellidos solo pueden contener letras y espacios (mínimo 2 caracteres)")
    private String apellidos;

    @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
    @Column(length = 200)
    private String direccion;

    @Required
    @Column(length = 10, nullable = false)
    @Pattern(regexp = "^[0-9]{10}$", message = "El teléfono debe contener exactamente 10 dígitos numéricos")
    private String telefono;

    @Email(message = "Debe ingresar un email válido")
    @Column(length = 100)
    @Stereotype("EMAIL")
    private String email;

    @Column(length = 50)
    private String medioContacto;

    @Required
    @Column(length = 20, nullable = false)
    @Convert(converter = EstadoClienteConverter.class)
    private EstadoCliente estado = EstadoCliente.ACTIVO;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    @ListProperties("placa, vin, marca, modelo, anio")
    private Collection<Vehiculo> vehiculos = new ArrayList<>();

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<Cita> citas = new ArrayList<>();

    @PrePersist
    @PreUpdate
    private void validarEstado() {
        if (this.estado == null) {
            this.estado = EstadoCliente.ACTIVO;
        }
    }

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

    public EstadoCliente getEstado() {
        return estado;
    }

    public void setEstado(EstadoCliente estado) {
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

    public void activar() {
        this.estado = EstadoCliente.ACTIVO;
    }

    public void desactivar() {
        this.estado = EstadoCliente.INACTIVO;
    }
}
