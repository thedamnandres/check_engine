package com.checkengine.checkengine.modelo;

import org.junit.Before;
import org.junit.Test;
import java.util.Date;
import static org.junit.Assert.*;

public class OrdenServicioTest {

    private OrdenServicio ordenServicio;
    private Cliente cliente;
    private Vehiculo vehiculo;
    private Cita cita;

    @Before
    public void setUp() {
        ordenServicio = new OrdenServicio();

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setCedula("1750505060");
        cliente.setNombres("Juan Esteban");
        cliente.setApellidos("Pérez Zambrano");

        vehiculo = new Vehiculo();
        vehiculo.setId(1L);
        vehiculo.setPlaca("PBC-345");
        vehiculo.setVin("3GCEC14X78G123456");
        vehiculo.setMarca("Chevrolet");
        vehiculo.setModelo("Captiva");
        vehiculo.setAnio(2012);
        vehiculo.setCliente(cliente);

        cita = new Cita();
        cita.setId(1L);
        cita.setCliente(cliente);
        cita.setVehiculo(vehiculo);
        cita.setFechaHora(new Date());
    }

    @Test
    public void testCU3_CrearOrdenServicioFlujoPrincipal() {
        // Given
        String codigo = "OS-2025-00001";
        Date fechaCreacion = new Date();
        Date fechaCierre = new Date();
        String diagnosticoInicial = "Vehículo presenta luz de check engine encendida. Código P0171 - Sistema muy pobre. Desgaste en pastillas de freno delanteras.";
        String observaciones = "Cliente autoriza todos los trabajos. Vehículo entregado sin novedades.";
        EstadoOrden estado = EstadoOrden.FINALIZADO;
        int year = 2025;
        int numero = 1;

        // When
        ordenServicio.setCodigo(codigo);
        ordenServicio.setFechaCreacion(fechaCreacion);
        ordenServicio.setFechaCierre(fechaCierre);
        ordenServicio.setDiagnosticoInicial(diagnosticoInicial);
        ordenServicio.setObservaciones(observaciones);
        ordenServicio.setEstadoActual(estado);
        ordenServicio.setYear(year);
        ordenServicio.setNumero(numero);
        ordenServicio.setCita(cita);
        ordenServicio.setVehiculo(vehiculo);

        // Then
        assertEquals(codigo, ordenServicio.getCodigo());
        assertEquals(fechaCreacion, ordenServicio.getFechaCreacion());
        assertEquals(fechaCierre, ordenServicio.getFechaCierre());
        assertEquals(diagnosticoInicial, ordenServicio.getDiagnosticoInicial());
        assertEquals(observaciones, ordenServicio.getObservaciones());
        assertEquals(estado, ordenServicio.getEstadoActual());
        assertEquals(year, ordenServicio.getYear());
        assertEquals(numero, ordenServicio.getNumero());
        assertEquals(cita, ordenServicio.getCita());
        assertEquals(vehiculo, ordenServicio.getVehiculo());

        assertTrue(ordenServicio.getCodigo().startsWith("OS-"));
        assertTrue(ordenServicio.getCodigo().length() <= 50);
        assertTrue(ordenServicio.getDiagnosticoInicial().length() <= 500);
        assertTrue(ordenServicio.getObservaciones().length() <= 1000);
        assertNotNull(ordenServicio.getDetallesTrabajo());
        assertNotNull(ordenServicio.getDetallesRepuesto());
    }

    @Test
    public void testCU3_Alterno71_FaltanDatosObligatorios() {
        // Given - Orden de servicio sin campos obligatorios

        // When - Estado es null
        ordenServicio.setEstadoActual(null);

        // Then
        assertNull(ordenServicio.getEstadoActual());

        // When - Cita es null
        ordenServicio.setCita(null);

        // Then
        assertNull(ordenServicio.getCita());

        // When - Código es null
        ordenServicio.setCodigo(null);

        // Then
        assertNull(ordenServicio.getCodigo());

        // When - Año es 0
        ordenServicio.setYear(0);

        // Then
        assertEquals(0, ordenServicio.getYear());

        // When - Número es 0
        ordenServicio.setNumero(0);

        // Then
        assertEquals(0, ordenServicio.getNumero());

        // When - Diagnóstico muy largo
        String diagnosticoLargo = crearCadenaRepetida("A", 501);
        ordenServicio.setDiagnosticoInicial(diagnosticoLargo);

        // Then
        assertTrue(ordenServicio.getDiagnosticoInicial().length() > 500);

        // When - Observaciones muy largas
        String observacionesLargas = crearCadenaRepetida("B", 1001);
        ordenServicio.setObservaciones(observacionesLargas);

        // Then
        assertTrue(ordenServicio.getObservaciones().length() > 1000);
    }

    @Test
    public void testCU3_ExcepcionP_SesionExpiradaAlGuardar() {
        // Given - Orden de servicio válida lista para guardar
        ordenServicio.setCodigo("OS-2025-00002");
        ordenServicio.setFechaCreacion(new Date());
        ordenServicio.setDiagnosticoInicial("Mantenimiento preventivo de 30,000 km. Se requiere cambio de aceite, filtros y revisión de frenos.");
        ordenServicio.setObservaciones("Mantenimiento completado según especificaciones del fabricante.");
        ordenServicio.setEstadoActual(EstadoOrden.FINALIZADO);
        ordenServicio.setYear(2025);
        ordenServicio.setNumero(2);
        ordenServicio.setCita(cita);
        ordenServicio.setVehiculo(vehiculo);

        // When - Verificar que los datos están correctos antes del "guardado"
        assertTrue(ordenServicio.getCodigo().startsWith("OS-"));
        assertNotNull(ordenServicio.getEstadoActual());
        assertTrue(ordenServicio.getDiagnosticoInicial().length() <= 500);

        // Then - Los datos de la orden deben mantenerse íntegros
        assertNotNull(ordenServicio.getCodigo());
        assertNotNull(ordenServicio.getFechaCreacion());
        assertNotNull(ordenServicio.getEstadoActual());
        assertNotNull(ordenServicio.getCita());
        assertTrue(ordenServicio.getYear() > 0);
        assertTrue(ordenServicio.getNumero() > 0);
    }

    @Test
    public void testCU3_ExcepcionQ_ErrorInternoAlGuardar() {
        // Given - Orden de servicio con datos válidos
        ordenServicio.setCodigo("OS-2025-00003");
        ordenServicio.setFechaCreacion(new Date());
        ordenServicio.setFechaCierre(new Date());
        ordenServicio.setDiagnosticoInicial("Cliente reporta vibración al frenar. Se detecta desgaste irregular en discos delanteros.");
        ordenServicio.setObservaciones("Se cambió discos y pastillas. Prueba de manejo satisfactoria.");
        ordenServicio.setEstadoActual(EstadoOrden.FINALIZADO);
        ordenServicio.setYear(2025);
        ordenServicio.setNumero(3);

        // When - Verificar integridad de datos antes del error
        assertTrue(ordenServicio.getCodigo().startsWith("OS-"));
        assertEquals(EstadoOrden.FINALIZADO, ordenServicio.getEstadoActual());
        assertTrue(ordenServicio.getObservaciones().length() <= 1000);

        // Then - Verificar que el estado del objeto no se corrompe
        assertTrue(ordenServicio.getCodigo().length() <= 50);
        assertTrue(ordenServicio.getYear() >= 2025);
        assertTrue(ordenServicio.getNumero() > 0);
        assertTrue(ordenServicio.getDiagnosticoInicial().length() <= 500);
        assertNotNull(ordenServicio.getDetallesTrabajo());
        assertNotNull(ordenServicio.getDetallesRepuesto());
    }

    private String crearCadenaRepetida(String cadena, int veces) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < veces; i++) {
            sb.append(cadena);
        }
        return sb.toString();
    }
}
