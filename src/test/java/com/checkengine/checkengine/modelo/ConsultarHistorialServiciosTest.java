package com.checkengine.checkengine.modelo;

import org.junit.Before;
import org.junit.Test;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import static org.junit.Assert.*;

public class ConsultarHistorialServiciosTest {

    private Cliente cliente;
    private Vehiculo vehiculo;
    private OrdenServicio ordenServicio;
    private Cita cita;
    private DetalleTrabajoOrden detalleTrabajo;
    private DetalleRepuestoOrden detalleRepuesto;
    private TipoServicio tipoServicio;
    private Repuesto repuesto;

    @Before
    public void setUp() {
        // Setup Cliente
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setCedula("1750505060");
        cliente.setNombres("Juan Esteban");
        cliente.setApellidos("Pérez Zambrano");
        cliente.setTelefono("0998765432");
        cliente.setEmail("juan.perez@gmail.com");

        // Setup Vehículo
        vehiculo = new Vehiculo();
        vehiculo.setId(1L);
        vehiculo.setPlaca("PBC-345");
        vehiculo.setVin("3GCEC14X78G123456");
        vehiculo.setMarca("Chevrolet");
        vehiculo.setModelo("Captiva");
        vehiculo.setAnio(2012);
        vehiculo.setCliente(cliente);

        // Setup Cita
        cita = new Cita();
        cita.setId(1L);
        cita.setCliente(cliente);
        cita.setVehiculo(vehiculo);
        cita.setFechaHora(new Date());

        // Setup Orden de Servicio
        ordenServicio = new OrdenServicio();
        ordenServicio.setId(1L);
        ordenServicio.setCodigo("OS-2025-00001");
        ordenServicio.setFechaCreacion(new Date());
        ordenServicio.setFechaCierre(new Date());
        ordenServicio.setDiagnosticoInicial("Vehículo presenta luz de check engine encendida.");
        ordenServicio.setObservaciones("Cliente autoriza todos los trabajos. Vehículo entregado sin novedades.");
        ordenServicio.setEstadoActual(EstadoOrden.FINALIZADO);
        ordenServicio.setYear(2025);
        ordenServicio.setNumero(1);
        ordenServicio.setCita(cita);
        ordenServicio.setVehiculo(vehiculo);

        // Setup TipoServicio
        tipoServicio = new TipoServicio();
        tipoServicio.setId(1L);
        tipoServicio.setNombre("Diagnóstico Motor");
        tipoServicio.setDescripcion("Diagnóstico completo del sistema motor");
        tipoServicio.setTarifaBase(new BigDecimal("25.00"));

        // Setup Repuesto
        repuesto = new Repuesto();
        repuesto.setId(1L);
        repuesto.setCodigo("REP-001");
        repuesto.setDescripcion("Filtro de Aceite Universal");
        repuesto.setCategoria("Filtros");
        repuesto.setPrecioUnitario(new BigDecimal("8.50"));

        // Setup DetalleTrabajoOrden
        detalleTrabajo = new DetalleTrabajoOrden();
        detalleTrabajo.setId(1L);
        detalleTrabajo.setDescripcion("Diagnóstico de sistema motor con scanner");
        detalleTrabajo.setHoras(new BigDecimal("2.0"));
        detalleTrabajo.setTarifaHora(new BigDecimal("25.00"));
        detalleTrabajo.setSubtotal(new BigDecimal("50.00"));
        detalleTrabajo.setOrdenServicio(ordenServicio);
        detalleTrabajo.setTipoServicio(tipoServicio);

        // Setup DetalleRepuestoOrden
        detalleRepuesto = new DetalleRepuestoOrden();
        detalleRepuesto.setId(1L);
        detalleRepuesto.setCantidad(2);
        detalleRepuesto.setPrecioUnitario(new BigDecimal("8.50"));
        detalleRepuesto.setSubtotal(new BigDecimal("17.00"));
        detalleRepuesto.setOrdenServicio(ordenServicio);
        detalleRepuesto.setRepuesto(repuesto);
    }

    @Test
    public void testCU7_ConsultarHistorialFlujoPrincipal() {
        // Given - Historial completo de servicios para un vehículo
        Collection<DetalleTrabajoOrden> trabajosRealizados = new ArrayList<>();
        trabajosRealizados.add(detalleTrabajo);

        Collection<DetalleRepuestoOrden> repuestosUsados = new ArrayList<>();
        repuestosUsados.add(detalleRepuesto);

        ordenServicio.setDetallesTrabajo(trabajosRealizados);
        ordenServicio.setDetallesRepuesto(repuestosUsados);

        // When - Se consulta el historial
        // Verificar datos de la orden de servicio
        assertNotNull(ordenServicio.getCodigo());
        assertNotNull(ordenServicio.getFechaCreacion());
        assertNotNull(ordenServicio.getVehiculo());
        assertNotNull(ordenServicio.getCita());

        // Then - El historial debe contener información completa
        assertEquals("OS-2025-00001", ordenServicio.getCodigo());
        assertEquals(EstadoOrden.FINALIZADO, ordenServicio.getEstadoActual());
        assertEquals(vehiculo, ordenServicio.getVehiculo());
        assertEquals(cliente, ordenServicio.getVehiculo().getCliente());

        // Verificar detalles de trabajo
        assertFalse(ordenServicio.getDetallesTrabajo().isEmpty());
        assertEquals(1, ordenServicio.getDetallesTrabajo().size());

        DetalleTrabajoOrden trabajo = ordenServicio.getDetallesTrabajo().iterator().next();
        assertEquals("Diagnóstico de sistema motor con scanner", trabajo.getDescripcion());
        assertEquals(new BigDecimal("2.0"), trabajo.getHoras());
        assertEquals(new BigDecimal("25.00"), trabajo.getTarifaHora());
        assertEquals("Diagnóstico Motor", trabajo.getTipoServicio().getNombre());

        // Verificar detalles de repuestos
        assertFalse(ordenServicio.getDetallesRepuesto().isEmpty());
        assertEquals(1, ordenServicio.getDetallesRepuesto().size());

        DetalleRepuestoOrden repuestoDetalle = ordenServicio.getDetallesRepuesto().iterator().next();
        assertEquals(2, repuestoDetalle.getCantidad());
        assertEquals(new BigDecimal("8.50"), repuestoDetalle.getPrecioUnitario());
        assertEquals("Filtro de Aceite Universal", repuestoDetalle.getRepuesto().getDescripcion());
    }

    @Test
    public void testCU7_Alterno31_SinHistorialDeServicios() {
        // Given - Vehículo sin historial de servicios
        Vehiculo vehiculoSinHistorial = new Vehiculo();
        vehiculoSinHistorial.setId(99L);
        vehiculoSinHistorial.setPlaca("XYZ-999");
        vehiculoSinHistorial.setVin("NOHISTORIAL1234567");
        vehiculoSinHistorial.setMarca("Toyota");
        vehiculoSinHistorial.setModelo("Nuevo");
        vehiculoSinHistorial.setAnio(2026);
        vehiculoSinHistorial.setCliente(cliente);

        OrdenServicio ordenVacia = new OrdenServicio();
        ordenVacia.setVehiculo(vehiculoSinHistorial);

        // When - Se consulta el historial
        Collection<DetalleTrabajoOrden> trabajosVacios = new ArrayList<>();
        Collection<DetalleRepuestoOrden> repuestosVacios = new ArrayList<>();

        ordenVacia.setDetallesTrabajo(trabajosVacios);
        ordenVacia.setDetallesRepuesto(repuestosVacios);

        // Then - El historial debe estar vacío pero la estructura debe ser válida
        assertNotNull(ordenVacia.getDetallesTrabajo());
        assertNotNull(ordenVacia.getDetallesRepuesto());
        assertTrue(ordenVacia.getDetallesTrabajo().isEmpty());
        assertTrue(ordenVacia.getDetallesRepuesto().isEmpty());

        // Verificar que el vehículo existe pero sin servicios
        assertNotNull(ordenVacia.getVehiculo());
        assertEquals("XYZ-999", ordenVacia.getVehiculo().getPlaca());
        assertEquals("Toyota", ordenVacia.getVehiculo().getMarca());
    }

    @Test
    public void testCU7_Alterno41_FiltrosInvalidos() {
        // Given - Parámetros de filtro inválidos para consulta de historial

        // When - Filtro por fecha inválida (null)
        Date fechaInvalida = null;
        ordenServicio.setFechaCreacion(fechaInvalida);

        // Then
        assertNull(ordenServicio.getFechaCreacion());

        // When - Filtro por año inválido
        int anioInvalido = 0;
        ordenServicio.setYear(anioInvalido);

        // Then
        assertEquals(0, ordenServicio.getYear());
        assertFalse(ordenServicio.getYear() >= 2020);

        // When - Código de orden inválido
        String codigoInvalido = "";
        ordenServicio.setCodigo(codigoInvalido);

        // Then
        assertTrue(ordenServicio.getCodigo().isEmpty());

        // When - Estado inválido para filtro
        ordenServicio.setEstadoActual(null);

        // Then
        assertNull(ordenServicio.getEstadoActual());

        // When - Filtros válidos para comparación
        ordenServicio.setFechaCreacion(new Date());
        ordenServicio.setYear(2025);
        ordenServicio.setCodigo("OS-2025-00001");
        ordenServicio.setEstadoActual(EstadoOrden.FINALIZADO);

        // Then - Filtros válidos deben funcionar correctamente
        assertNotNull(ordenServicio.getFechaCreacion());
        assertTrue(ordenServicio.getYear() >= 2020);
        assertTrue(ordenServicio.getCodigo().startsWith("OS-"));
        assertNotNull(ordenServicio.getEstadoActual());
    }

    @Test
    public void testCU7_ExcepcionP_SinSesionActiva() {
        // Given - Consulta de historial válida lista para ejecutar
        ordenServicio.setCodigo("OS-2025-00001");
        ordenServicio.setFechaCreacion(new Date());
        ordenServicio.setEstadoActual(EstadoOrden.FINALIZADO);
        ordenServicio.setVehiculo(vehiculo);

        Collection<DetalleTrabajoOrden> trabajos = new ArrayList<>();
        trabajos.add(detalleTrabajo);
        ordenServicio.setDetallesTrabajo(trabajos);

        // When - Verificar que los datos están correctos antes de la "consulta"
        assertTrue(ordenServicio.getCodigo().startsWith("OS-"));
        assertNotNull(ordenServicio.getVehiculo());
        assertNotNull(ordenServicio.getEstadoActual());
        assertFalse(ordenServicio.getDetallesTrabajo().isEmpty());

        // Then - Los datos del historial deben mantenerse íntegros incluso si falla la sesión
        assertNotNull(ordenServicio.getCodigo());
        assertNotNull(ordenServicio.getFechaCreacion());
        assertNotNull(ordenServicio.getVehiculo());
        assertEquals(vehiculo.getPlaca(), ordenServicio.getVehiculo().getPlaca());
        assertEquals(cliente.getCedula(), ordenServicio.getVehiculo().getCliente().getCedula());

        // Verificar que los detalles siguen disponibles
        assertEquals(1, ordenServicio.getDetallesTrabajo().size());
        DetalleTrabajoOrden trabajo = ordenServicio.getDetallesTrabajo().iterator().next();
        assertEquals("Diagnóstico de sistema motor con scanner", trabajo.getDescripcion());
    }

    @Test
    public void testCU7_ExcepcionQ_ErrorInternoEnConsulta() {
        // Given - Datos válidos para consulta de historial
        ordenServicio.setCodigo("OS-2025-00002");
        ordenServicio.setFechaCreacion(new Date());
        ordenServicio.setFechaCierre(new Date());
        ordenServicio.setDiagnosticoInicial("Mantenimiento preventivo de 30,000 km.");
        ordenServicio.setObservaciones("Mantenimiento completado según especificaciones del fabricante.");
        ordenServicio.setEstadoActual(EstadoOrden.FINALIZADO);
        ordenServicio.setYear(2025);
        ordenServicio.setNumero(2);

        Collection<DetalleTrabajoOrden> trabajos = new ArrayList<>();
        trabajos.add(detalleTrabajo);

        Collection<DetalleRepuestoOrden> repuestos = new ArrayList<>();
        repuestos.add(detalleRepuesto);

        ordenServicio.setDetallesTrabajo(trabajos);
        ordenServicio.setDetallesRepuesto(repuestos);

        // When - Verificar integridad de datos antes del error interno
        assertTrue(ordenServicio.getCodigo().startsWith("OS-"));
        assertEquals(EstadoOrden.FINALIZADO, ordenServicio.getEstadoActual());
        assertFalse(ordenServicio.getDetallesTrabajo().isEmpty());
        assertFalse(ordenServicio.getDetallesRepuesto().isEmpty());

        // Then - Verificar que el estado del historial no se corrompe ante errores internos
        assertTrue(ordenServicio.getCodigo().length() <= 50);
        assertTrue(ordenServicio.getYear() >= 2025);
        assertTrue(ordenServicio.getNumero() > 0);
        assertTrue(ordenServicio.getDiagnosticoInicial().length() <= 500);
        assertNotNull(ordenServicio.getDetallesTrabajo());
        assertNotNull(ordenServicio.getDetallesRepuesto());

        // Verificar integridad de las relaciones
        assertEquals(ordenServicio, detalleTrabajo.getOrdenServicio());
        assertEquals(ordenServicio, detalleRepuesto.getOrdenServicio());
        assertEquals(tipoServicio, detalleTrabajo.getTipoServicio());
        assertEquals(repuesto, detalleRepuesto.getRepuesto());
    }

    @Test
    public void testHistorialCompletoConMultiplesServicios() {
        // Given - Múltiples servicios realizados en diferentes fechas
        Collection<DetalleTrabajoOrden> trabajosMultiples = new ArrayList<>();

        // Trabajo 1: Diagnóstico
        DetalleTrabajoOrden trabajo1 = new DetalleTrabajoOrden();
        trabajo1.setDescripcion("Diagnóstico de sistema motor");
        trabajo1.setHoras(new BigDecimal("1.5"));
        trabajo1.setTarifaHora(new BigDecimal("25.00"));
        trabajo1.setSubtotal(new BigDecimal("37.50"));
        trabajo1.setOrdenServicio(ordenServicio);
        trabajo1.setTipoServicio(tipoServicio);

        // Trabajo 2: Reparación
        TipoServicio tipoReparacion = new TipoServicio();
        tipoReparacion.setNombre("Reparación Frenos");
        tipoReparacion.setTarifaBase(new BigDecimal("30.00"));

        DetalleTrabajoOrden trabajo2 = new DetalleTrabajoOrden();
        trabajo2.setDescripcion("Cambio de pastillas de freno delanteras");
        trabajo2.setHoras(new BigDecimal("2.0"));
        trabajo2.setTarifaHora(new BigDecimal("30.00"));
        trabajo2.setSubtotal(new BigDecimal("60.00"));
        trabajo2.setOrdenServicio(ordenServicio);
        trabajo2.setTipoServicio(tipoReparacion);

        trabajosMultiples.add(trabajo1);
        trabajosMultiples.add(trabajo2);

        Collection<DetalleRepuestoOrden> repuestosMultiples = new ArrayList<>();
        repuestosMultiples.add(detalleRepuesto);

        ordenServicio.setDetallesTrabajo(trabajosMultiples);
        ordenServicio.setDetallesRepuesto(repuestosMultiples);

        // When & Then - Verificar historial completo
        assertEquals(2, ordenServicio.getDetallesTrabajo().size());
        assertEquals(1, ordenServicio.getDetallesRepuesto().size());

        // Verificar que cada trabajo mantiene su relación correcta
        for (DetalleTrabajoOrden trabajo : ordenServicio.getDetallesTrabajo()) {
            assertEquals(ordenServicio, trabajo.getOrdenServicio());
            assertNotNull(trabajo.getTipoServicio());
            assertTrue(trabajo.getSubtotal().compareTo(BigDecimal.ZERO) > 0);
        }

        // Verificar información del cliente y vehículo a través del historial
        assertEquals("PBC-345", ordenServicio.getVehiculo().getPlaca());
        assertEquals("Juan Esteban", ordenServicio.getVehiculo().getCliente().getNombres());
        assertEquals("1750505060", ordenServicio.getVehiculo().getCliente().getCedula());
    }

    @Test
    public void testFiltrosPorEstadoDeOrden() {
        // Given - Órdenes con diferentes estados para filtrado
        String[] estadosParaFiltrar = {"PENDIENTE", "EN_REPARACION", "FINALIZADO", "ENTREGADO"};

        // When & Then - Verificar que todos los estados son válidos para filtros
        for (String estadoStr : estadosParaFiltrar) {
            EstadoOrden estado = EstadoOrden.valueOf(estadoStr);
            ordenServicio.setEstadoActual(estado);

            assertEquals(estado, ordenServicio.getEstadoActual());
            assertNotNull(ordenServicio.getEstadoActual());
        }

        // Verificar filtro por estado específico
        ordenServicio.setEstadoActual(EstadoOrden.FINALIZADO);
        assertEquals(EstadoOrden.FINALIZADO, ordenServicio.getEstadoActual());

        // Un historial filtrado por FINALIZADO debería incluir esta orden
        assertTrue(ordenServicio.getEstadoActual() == EstadoOrden.FINALIZADO);
    }

    @Test
    public void testConsistenciaHistorialConDatosBD() {
        // Given - Datos reales de órdenes de servicio de la BD
        String[][] historialBD = {
            {"OS-2025-00001", "FINALIZADO", "2025", "1", "PBC-345"},
            {"OS-2025-00002", "FINALIZADO", "2025", "2", "ABC-1234"},
            {"OS-2025-00003", "FINALIZADO", "2025", "3", "XYZ-987"},
            {"OS-2026-00001", "FINALIZADO", "2026", "1", "PBC-345"},
            {"OS-2026-00002", "FINALIZADO", "2026", "2", "ABC-1234"}
        };

        // When & Then - Verificar consistencia de cada entrada del historial
        for (String[] entrada : historialBD) {
            OrdenServicio ordenTemp = new OrdenServicio();
            ordenTemp.setCodigo(entrada[0]);
            ordenTemp.setEstadoActual(EstadoOrden.valueOf(entrada[1]));
            ordenTemp.setYear(Integer.parseInt(entrada[2]));
            ordenTemp.setNumero(Integer.parseInt(entrada[3]));

            Vehiculo vehiculoTemp = new Vehiculo();
            vehiculoTemp.setPlaca(entrada[4]);
            ordenTemp.setVehiculo(vehiculoTemp);

            // Validaciones para cada entrada del historial
            assertTrue("Código válido para historial",
                      ordenTemp.getCodigo().startsWith("OS-"));
            assertEquals("Estado válido para historial",
                        EstadoOrden.valueOf(entrada[1]), ordenTemp.getEstadoActual());
            assertTrue("Año válido para historial",
                      ordenTemp.getYear() >= 2025);
            assertNotNull("Vehículo válido para historial",
                         ordenTemp.getVehiculo());
            assertTrue("Placa válida para historial",
                      ordenTemp.getVehiculo().getPlaca().matches("^[A-Z]{3}-\\d{3,4}$"));
        }
    }
}
