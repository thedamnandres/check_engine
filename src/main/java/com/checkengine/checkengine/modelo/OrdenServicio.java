package com.checkengine.checkengine.modelo;

import javax.persistence.*;
import org.openxava.annotations.*;
import org.openxava.calculators.*;
import org.openxava.jpa.XPersistence;
import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "orden_servicio")
@View(members =
    "year, numero, codigo;" +
    "fechaCreacion, fechaCierre;" +
    "vehiculo;" +
    "cita;" +
    "estadoActual;" +
    "diagnosticoInicial;" +
    "detalles [" +
        "detallesTrabajo;" +
        "detallesRepuesto" +
    "];" +
    "observaciones"
)
public class OrdenServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Hidden
    private Long id;

    @Column(length = 4, nullable = false)
    @ReadOnly
    private int year = 0;

    @Column(length = 5, nullable = false)
    @ReadOnly
    private int numero = 0;

    @Column(length = 50, unique = true)
    @ReadOnly
    @SearchKey
    private String codigo;

    @Required
    @Stereotype("DATE")
    @DefaultValueCalculator(CurrentLocalDateCalculator.class)
    private Date fechaCreacion;

    @Stereotype("DATE")
    private Date fechaCierre;

    @Column(length = 500)
    private String diagnosticoInicial;

    @Column(length = 1000)
    private String observaciones;

    @Enumerated(EnumType.STRING)
    @Required
    @Column(length = 30)
    private EstadoOrden estadoActual;

    @OneToOne
    @JoinColumn(name = "cita_id")
    private Cita cita;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehiculo_id")
    @DescriptionsList(descriptionProperties = "placa, marca, modelo")
    @Required
    private Vehiculo vehiculo;

    @OneToMany(mappedBy = "ordenServicio", cascade = CascadeType.ALL, orphanRemoval = true)
    @ListProperties("tipoServicio.nombre, horas, tarifaHora, subtotal")
    private Collection<DetalleTrabajoOrden> detallesTrabajo = new ArrayList<>();

    @OneToMany(mappedBy = "ordenServicio", cascade = CascadeType.ALL, orphanRemoval = true)
    @ListProperties("repuesto.descripcion, cantidad, precioUnitario, subtotal")
    private Collection<DetalleRepuestoOrden> detallesRepuesto = new ArrayList<>();

    @OneToOne(mappedBy = "ordenServicio")
    private FacturaInterna facturaInterna;

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

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Date getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(Date fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public String getDiagnosticoInicial() {
        return diagnosticoInicial;
    }

    public void setDiagnosticoInicial(String diagnosticoInicial) {
        this.diagnosticoInicial = diagnosticoInicial;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public EstadoOrden getEstadoActual() {
        return estadoActual;
    }

    public void setEstadoActual(EstadoOrden estadoActual) {
        this.estadoActual = estadoActual;
    }

    public Cita getCita() {
        return cita;
    }

    public void setCita(Cita cita) {
        this.cita = cita;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public Collection<DetalleTrabajoOrden> getDetallesTrabajo() {
        return detallesTrabajo;
    }

    public void setDetallesTrabajo(Collection<DetalleTrabajoOrden> detallesTrabajo) {
        this.detallesTrabajo = detallesTrabajo;
    }

    public Collection<DetalleRepuestoOrden> getDetallesRepuesto() {
        return detallesRepuesto;
    }

    public void setDetallesRepuesto(Collection<DetalleRepuestoOrden> detallesRepuesto) {
        this.detallesRepuesto = detallesRepuesto;
    }

    public FacturaInterna getFacturaInterna() {
        return facturaInterna;
    }

    public void setFacturaInterna(FacturaInterna facturaInterna) {
        this.facturaInterna = facturaInterna;
    }

    // Métodos de negocio

    @PrePersist
    private void generarCodigo() {
        if (year == 0) {
            this.year = java.time.LocalDate.now().getYear();
        }
        if (numero == 0) {
            try {
                Query query = XPersistence.getManager()
                    .createQuery("SELECT MAX(o.numero) FROM OrdenServicio o WHERE o.year = :year");
                query.setParameter("year", year);
                Integer lastNumber = (Integer) query.getSingleResult();
                this.numero = lastNumber == null ? 1 : lastNumber + 1;
            } catch (Exception e) {
                this.numero = 1;
            }
        }
        if (codigo == null || codigo.isEmpty()) {
            this.codigo = "OS-" + year + "-" + String.format("%05d", numero);
        }
    }

    public void agregarDetalleTrabajo() {
        // Lógica para agregar detalle de trabajo
    }

    public void agregarDetalleRepuesto() {
        // Lógica para agregar detalle de repuesto
    }

    public void cerrarOrden() {
        this.estadoActual = EstadoOrden.FINALIZADO;
        this.fechaCierre = new Date();
    }
}
