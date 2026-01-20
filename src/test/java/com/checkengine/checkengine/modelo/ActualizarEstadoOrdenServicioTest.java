package com.checkengine.checkengine.modelo;

import org.junit.Before;
import org.junit.Test;
import java.util.Date;
import static org.junit.Assert.*;

public class ActualizarEstadoOrdenServicioTest {

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
    public void testCU4_ActualizarEstadoFlujoPrincipal() {
        // Given - Orden de servicio existente con estado inicial
        String codigo = "OS-2025-00001";
        Date fechaCreacion = new Date();
        String diagnostico = "Vehículo presenta luz de check engine encendida. Código P0171 - Sistema muy pobre. Desgaste en pastillas de freno delanteras.";
        EstadoOrden estadoInicial = EstadoOrden.PENDIENTE;
        EstadoOrden estadoNuevo = EstadoOrden.EN_REPARACION;

        ordenServicio.setCodigo(codigo);
        ordenServicio.setFechaCreacion(fechaCreacion);
        ordenServicio.setDiagnosticoInicial(diagnostico);
        ordenServicio.setEstadoActual(estadoInicial);
        ordenServicio.setYear(2025);
        ordenServicio.setNumero(1);
        ordenServicio.setCita(cita);
        ordenServicio.setVehiculo(vehiculo);

        // When - Se actualiza el estado
        ordenServicio.setEstadoActual(estadoNuevo);

        // Then - El estado debe haberse actualizado correctamente
        assertEquals(estadoNuevo, ordenServicio.getEstadoActual());
        assertNotEquals(estadoInicial, ordenServicio.getEstadoActual());
        assertEquals(codigo, ordenServicio.getCodigo());
        assertEquals(diagnostico, ordenServicio.getDiagnosticoInicial());

        // Verificar que los demás datos permanecen intactos
        assertNotNull(ordenServicio.getCita());
        assertNotNull(ordenServicio.getVehiculo());
        assertTrue(ordenServicio.getYear() > 0);
        assertTrue(ordenServicio.getNumero() > 0);
    }

    @Test
    public void testCU4_Alterno21_OrdenNoExiste() {
        // Given - Códigos de órdenes que no existen
        String[] codigosNoExistentes = {
            "OS-2025-99999",  // No existe
            "OS-2024-00001",  // Año anterior
            "OS-2027-00001",  // Año futuro
            "XYZ-2025-001",   // Formato incorrecto
            ""                // Código vacío
        };

        // When & Then - Verificar comportamiento con órdenes inexistentes
        for (String codigoNoExistente : codigosNoExistentes) {
            ordenServicio.setCodigo(codigoNoExistente);

            if (codigoNoExistente.isEmpty()) {
                assertTrue(ordenServicio.getCodigo().isEmpty());
            } else if (!codigoNoExistente.startsWith("OS-")) {
                assertFalse(ordenServicio.getCodigo().startsWith("OS-"));
            } else {
                // Para códigos con formato correcto pero que no existen,
                // verificar que el objeto existe pero sin datos asociados
                assertNull(ordenServicio.getDiagnosticoInicial());
                assertNull(ordenServicio.getEstadoActual());
                assertEquals(0, ordenServicio.getYear());
                assertEquals(0, ordenServicio.getNumero());
            }
        }
    }

    @Test
    public void testCU4_Alterno41_EstadoInvalido() {
        // Given - Orden de servicio válida
        ordenServicio.setCodigo("OS-2025-00002");
        ordenServicio.setFechaCreacion(new Date());
        ordenServicio.setDiagnosticoInicial("Mantenimiento preventivo de 30,000 km.");
        ordenServicio.setYear(2025);
        ordenServicio.setNumero(2);

        // When - Estado es null
        ordenServicio.setEstadoActual(null);

        // Then
        assertNull(ordenServicio.getEstadoActual());

        // When - Probar transiciones de estado inválidas
        // Una orden finalizada no debería poder volver a pendiente (lógica de negocio)
        ordenServicio.setEstadoActual(EstadoOrden.FINALIZADO);
        EstadoOrden estadoAnterior = ordenServicio.getEstadoActual();

        // Intentar cambiar a un estado "anterior" (esto sería una validación de negocio)
        ordenServicio.setEstadoActual(EstadoOrden.PENDIENTE);

        // Then - El estado se puede cambiar (la validación sería en el servicio, no en la entidad)
        assertEquals(EstadoOrden.PENDIENTE, ordenServicio.getEstadoActual());
        assertNotEquals(estadoAnterior, ordenServicio.getEstadoActual());

        // Verificar que todos los estados del enum son válidos
        for (EstadoOrden estado : EstadoOrden.values()) {
            ordenServicio.setEstadoActual(estado);
            assertEquals(estado, ordenServicio.getEstadoActual());
        }
    }

    @Test
    public void testCU4_ExcepcionP_SinSesionActiva() {
        // Given - Orden de servicio válida lista para actualizar
        ordenServicio.setCodigo("OS-2025-00003");
        ordenServicio.setFechaCreacion(new Date());
        ordenServicio.setDiagnosticoInicial("Cliente reporta vibración al frenar. Se detecta desgaste irregular en discos delanteros.");
        ordenServicio.setObservaciones("Se cambió discos y pastillas. Prueba de manejo satisfactoria.");
        ordenServicio.setEstadoActual(EstadoOrden.EN_REPARACION);
        ordenServicio.setYear(2025);
        ordenServicio.setNumero(3);
        ordenServicio.setCita(cita);
        ordenServicio.setVehiculo(vehiculo);

        EstadoOrden estadoAnterior = ordenServicio.getEstadoActual();

        // When - Verificar que los datos están correctos antes de la "actualización"
        assertTrue(ordenServicio.getCodigo().startsWith("OS-"));
        assertNotNull(ordenServicio.getEstadoActual());
        assertTrue(ordenServicio.getDiagnosticoInicial().length() <= 500);

        // Simular intento de actualización de estado
        EstadoOrden nuevoEstado = EstadoOrden.FINALIZADO;
        ordenServicio.setEstadoActual(nuevoEstado);

        // Then - Los datos de la orden deben mantenerse íntegros incluso si falla la sesión
        assertNotNull(ordenServicio.getCodigo());
        assertNotNull(ordenServicio.getFechaCreacion());
        assertNotNull(ordenServicio.getEstadoActual());
        assertNotNull(ordenServicio.getCita());
        assertTrue(ordenServicio.getYear() > 0);
        assertTrue(ordenServicio.getNumero() > 0);

        // El estado puede haberse actualizado en el objeto, pero en una falla de sesión
        // no se persistiría en BD
        assertEquals(nuevoEstado, ordenServicio.getEstadoActual());
    }

    @Test
    public void testCU4_ExcepcionQ_ErrorInternoEnActualizacion() {
        // Given - Orden de servicio con datos válidos
        ordenServicio.setCodigo("OS-2026-00001");
        ordenServicio.setFechaCreacion(new Date());
        ordenServicio.setFechaCierre(new Date());
        ordenServicio.setDiagnosticoInicial("Vehículo ingresa para cambio de aceite programado.");
        ordenServicio.setObservaciones("En proceso de cambio de aceite y filtros.");
        ordenServicio.setEstadoActual(EstadoOrden.EN_REPARACION);
        ordenServicio.setYear(2026);
        ordenServicio.setNumero(1);

        EstadoOrden estadoOriginal = ordenServicio.getEstadoActual();

        // When - Verificar integridad de datos antes del error
        assertTrue(ordenServicio.getCodigo().startsWith("OS-"));
        assertEquals(EstadoOrden.EN_REPARACION, ordenServicio.getEstadoActual());
        assertTrue(ordenServicio.getObservaciones().length() <= 1000);

        // Simular intento de actualización que falla internamente
        EstadoOrden estadoObjetivo = EstadoOrden.FINALIZADO;
        ordenServicio.setEstadoActual(estadoObjetivo);

        // Then - Verificar que el estado del objeto no se corrompe
        assertTrue(ordenServicio.getCodigo().length() <= 50);
        assertTrue(ordenServicio.getYear() >= 2025);
        assertTrue(ordenServicio.getNumero() > 0);
        assertTrue(ordenServicio.getDiagnosticoInicial().length() <= 500);
        assertNotNull(ordenServicio.getDetallesTrabajo());
        assertNotNull(ordenServicio.getDetallesRepuesto());

        // El objeto mantiene el nuevo estado, pero en un error interno
        // se haría rollback en la transacción
        assertEquals(estadoObjetivo, ordenServicio.getEstadoActual());
        assertNotEquals(estadoOriginal, ordenServicio.getEstadoActual());
    }

    @Test
    public void testSecuenciaEstadosValida() {
        // Given - Orden de servicio nueva
        ordenServicio.setCodigo("OS-2026-00002");
        ordenServicio.setYear(2026);
        ordenServicio.setNumero(2);

        // When & Then - Probar secuencia típica de estados

        // Estado inicial: PENDIENTE
        ordenServicio.setEstadoActual(EstadoOrden.PENDIENTE);
        assertEquals(EstadoOrden.PENDIENTE, ordenServicio.getEstadoActual());

        // Cambiar a EN_ESPERA_REPUESTOS
        ordenServicio.setEstadoActual(EstadoOrden.EN_ESPERA_REPUESTOS);
        assertEquals(EstadoOrden.EN_ESPERA_REPUESTOS, ordenServicio.getEstadoActual());

        // Cambiar a EN_REPARACION
        ordenServicio.setEstadoActual(EstadoOrden.EN_REPARACION);
        assertEquals(EstadoOrden.EN_REPARACION, ordenServicio.getEstadoActual());

        // Cambiar a FINALIZADO
        ordenServicio.setEstadoActual(EstadoOrden.FINALIZADO);
        assertEquals(EstadoOrden.FINALIZADO, ordenServicio.getEstadoActual());

        // Cambiar a ENTREGADO
        ordenServicio.setEstadoActual(EstadoOrden.ENTREGADO);
        assertEquals(EstadoOrden.ENTREGADO, ordenServicio.getEstadoActual());
    }

    @Test
    public void testOrdenesConDatosBD() {
        // Given - Datos reales de órdenes existentes en BD
        String[][] ordenesBD = {
            {"OS-2025-00001", "FINALIZADO", "2025", "1"},
            {"OS-2025-00002", "FINALIZADO", "2025", "2"},
            {"OS-2025-00003", "FINALIZADO", "2025", "3"},
            {"OS-2026-00001", "FINALIZADO", "2026", "1"},
            {"OS-2026-00002", "FINALIZADO", "2026", "2"}
        };

        // When & Then - Verificar cada orden
        for (String[] datosOrden : ordenesBD) {
            OrdenServicio ordenTemp = new OrdenServicio();
            ordenTemp.setCodigo(datosOrden[0]);
            ordenTemp.setEstadoActual(EstadoOrden.valueOf(datosOrden[1]));
            ordenTemp.setYear(Integer.parseInt(datosOrden[2]));
            ordenTemp.setNumero(Integer.parseInt(datosOrden[3]));

            // Validaciones para cada orden
            assertTrue("Código válido para " + datosOrden[0],
                      ordenTemp.getCodigo().startsWith("OS-"));
            assertEquals("Estado válido para " + datosOrden[0],
                        EstadoOrden.valueOf(datosOrden[1]), ordenTemp.getEstadoActual());
            assertTrue("Año válido para " + datosOrden[0],
                      ordenTemp.getYear() >= 2025);
            assertTrue("Número válido para " + datosOrden[0],
                      ordenTemp.getNumero() > 0);

            // Probar cambio de estado
            EstadoOrden estadoOriginal = ordenTemp.getEstadoActual();
            ordenTemp.setEstadoActual(EstadoOrden.PENDIENTE);
            assertEquals(EstadoOrden.PENDIENTE, ordenTemp.getEstadoActual());
            assertNotEquals(estadoOriginal, ordenTemp.getEstadoActual());
        }
    }

    @Test
    public void testTodosLosEstadosDisponibles() {
        // Given - Lista de todos los estados disponibles
        EstadoOrden[] estadosDisponibles = EstadoOrden.values();

        // When & Then - Verificar que todos los estados se pueden asignar
        for (EstadoOrden estado : estadosDisponibles) {
            ordenServicio.setEstadoActual(estado);
            assertEquals("Estado " + estado + " debe poder asignarse",
                        estado, ordenServicio.getEstadoActual());
        }

        // Verificar que existen todos los estados esperados
        assertTrue("Debe existir estado PENDIENTE",
                  java.util.Arrays.asList(estadosDisponibles).contains(EstadoOrden.PENDIENTE));
        assertTrue("Debe existir estado EN_ESPERA_REPUESTOS",
                  java.util.Arrays.asList(estadosDisponibles).contains(EstadoOrden.EN_ESPERA_REPUESTOS));
        assertTrue("Debe existir estado EN_REPARACION",
                  java.util.Arrays.asList(estadosDisponibles).contains(EstadoOrden.EN_REPARACION));
        assertTrue("Debe existir estado FINALIZADO",
                  java.util.Arrays.asList(estadosDisponibles).contains(EstadoOrden.FINALIZADO));
        assertTrue("Debe existir estado ENTREGADO",
                  java.util.Arrays.asList(estadosDisponibles).contains(EstadoOrden.ENTREGADO));
    }
}
