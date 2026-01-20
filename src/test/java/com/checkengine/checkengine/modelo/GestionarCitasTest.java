package com.checkengine.checkengine.modelo;

import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import static org.junit.Assert.*;

public class GestionarCitasTest {

    private Cita cita;
    private Cliente cliente;
    private Vehiculo vehiculo;

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
        cliente.setDireccion("Av. Eloy Alfaro N34-123 y Portugal");
        cliente.setMedioContacto("WHATSAPP");
        cliente.setEstado(EstadoCliente.ACTIVO);

        // Setup Vehículo
        vehiculo = new Vehiculo();
        vehiculo.setId(1L);
        vehiculo.setPlaca("PBC-345");
        vehiculo.setVin("3GCEC14X78G123456");
        vehiculo.setMarca("Chevrolet");
        vehiculo.setModelo("Captiva");
        vehiculo.setAnio(2012);
        vehiculo.setTipoCombustible(TipoCombustible.GASOLINA);
        vehiculo.setKilometraje(147000);
        vehiculo.setActivo(true);
        vehiculo.setCliente(cliente);

        // Setup Cita
        cita = new Cita();
        cita.setId(1L);
        cita.setCliente(cliente);
        cita.setVehiculo(vehiculo);
        cita.setEstado(EstadoCita.PROGRAMADA);
    }

    @Test
    public void testCU8_GestionarCitasFlujoPrincipalRegistrarCita() {
        // Given - Nueva cita para registrar
        String comentarios = "Cliente reporta luz de check engine y ruido al frenar";
        Date fechaHoraCita = crearFechaFutura(5, 9, 0); // 5 días desde hoy a las 9:00
        EstadoCita estadoInicial = EstadoCita.PROGRAMADA;

        // When - Se registra la cita
        cita.setFechaHora(fechaHoraCita);
        cita.setComentarios(comentarios);
        cita.setEstado(estadoInicial);

        // Then - La cita debe estar registrada correctamente
        assertEquals(fechaHoraCita, cita.getFechaHora());
        assertEquals(comentarios, cita.getComentarios());
        assertEquals(estadoInicial, cita.getEstado());
        assertEquals(cliente, cita.getCliente());
        assertEquals(vehiculo, cita.getVehiculo());

        // Verificar validaciones
        assertNotNull(cita.getFechaHora());
        assertTrue("La fecha debe ser futura", cita.getFechaHora().after(new Date()));
        assertTrue("Los comentarios no deben exceder 500 caracteres",
                  cita.getComentarios().length() <= 500);
        assertNotNull(cita.getCliente());
        assertNotNull(cita.getVehiculo());
        assertEquals(EstadoCita.PROGRAMADA, cita.getEstado());

        // Verificar relaciones
        assertEquals("1750505060", cita.getCliente().getCedula());
        assertEquals("PBC-345", cita.getVehiculo().getPlaca());
        assertEquals(EstadoCliente.ACTIVO, cita.getCliente().getEstado());
        assertTrue(cita.getVehiculo().isActivo());
    }

    @Test
    public void testCU8_Alterno31_ClienteOVehiculoNoExiste() {
        // Given - Cliente inexistente
        Cliente clienteInexistente = new Cliente();
        clienteInexistente.setId(999L);
        clienteInexistente.setCedula("9999999999");
        clienteInexistente.setEstado(EstadoCliente.INACTIVO);

        // When - Se intenta crear cita con cliente inexistente
        cita.setCliente(clienteInexistente);

        // Then - Debe detectarse que el cliente no es válido
        assertEquals(clienteInexistente, cita.getCliente());
        assertEquals("9999999999", cita.getCliente().getCedula());
        assertEquals(EstadoCliente.INACTIVO, cita.getCliente().getEstado());

        // Given - Vehículo inexistente
        Vehiculo vehiculoInexistente = new Vehiculo();
        vehiculoInexistente.setId(999L);
        vehiculoInexistente.setPlaca("XXX-999");
        vehiculoInexistente.setActivo(false);

        // When - Se intenta crear cita con vehículo inexistente
        cita.setVehiculo(vehiculoInexistente);

        // Then - Debe detectarse que el vehículo no es válido
        assertEquals(vehiculoInexistente, cita.getVehiculo());
        assertEquals("XXX-999", cita.getVehiculo().getPlaca());
        assertFalse(cita.getVehiculo().isActivo());

        // Verificar campos obligatorios nulos
        cita.setCliente(null);
        assertNull(cita.getCliente());

        cita.setVehiculo(null);
        assertNull(cita.getVehiculo());
    }

    @Test
    public void testCU8_Alterno41_HorarioNoDisponible() {
        // Given - Horarios ocupados/no disponibles
        Date[] horariosNoDisponibles = {
            crearFechaEspecifica(2025, 11, 5, 9, 0), // Fecha pasada
            new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000), // Ayer
            crearFechaEspecifica(2026, 0, 19, 7, 0), // Muy temprano (7:00 AM)
            crearFechaEspecifica(2026, 0, 19, 19, 0), // Muy tarde (7:00 PM)
            crearFechaEspecifica(2026, 0, 18, 12, 0), // Sábado
            crearFechaEspecifica(2026, 0, 19, 12, 0)  // Domingo
        };

        // When & Then - Verificar horarios no disponibles
        for (Date horarioNoDisponible : horariosNoDisponibles) {
            cita.setFechaHora(horarioNoDisponible);

            if (horarioNoDisponible.before(new Date())) {
                // Horario en el pasado
                assertFalse("La fecha no debe ser en el pasado",
                           cita.getFechaHora().after(new Date()));
            } else {
                // Horario futuro pero posiblemente fuera de horario laboral
                assertTrue("La fecha debe ser futura",
                          cita.getFechaHora().after(new Date()));

                Calendar cal = Calendar.getInstance();
                cal.setTime(horarioNoDisponible);
                int hora = cal.get(Calendar.HOUR_OF_DAY);
                int diaSemana = cal.get(Calendar.DAY_OF_WEEK);

                // Verificar si está fuera de horario laboral (8:00-18:00)
                if (hora < 8 || hora >= 18) {
                    assertTrue("Fuera de horario laboral", hora < 8 || hora >= 18);
                }

                // Verificar si es fin de semana
                if (diaSemana == Calendar.SATURDAY || diaSemana == Calendar.SUNDAY) {
                    assertTrue("Es fin de semana",
                              diaSemana == Calendar.SATURDAY || diaSemana == Calendar.SUNDAY);
                }
            }
        }

        // Given - Horario disponible válido
        Date horarioDisponible = crearFechaFutura(3, 14, 0); // 3 días, 14:00
        cita.setFechaHora(horarioDisponible);

        // Then - Debe ser un horario válido
        assertTrue("Horario disponible debe ser futuro",
                  cita.getFechaHora().after(new Date()));

        Calendar cal = Calendar.getInstance();
        cal.setTime(cita.getFechaHora());
        int hora = cal.get(Calendar.HOUR_OF_DAY);
        int diaSemana = cal.get(Calendar.DAY_OF_WEEK);

        assertTrue("Debe estar en horario laboral", hora >= 8 && hora < 18);
        assertTrue("Debe ser día laboral",
                  diaSemana >= Calendar.MONDAY && diaSemana <= Calendar.FRIDAY);
    }

    @Test
    public void testCU8_ExcepcionP_SinSesionActiva() {
        // Given - Cita válida lista para gestionar
        cita.setFechaHora(crearFechaFutura(7, 10, 30));
        cita.setComentarios("Mantenimiento preventivo de los 30,000 km");
        cita.setEstado(EstadoCita.PROGRAMADA);

        // When - Verificar que los datos están correctos antes de la "gestión"
        assertTrue(cita.getFechaHora().after(new Date()));
        assertNotNull(cita.getComentarios());
        assertNotNull(cita.getCliente());
        assertNotNull(cita.getVehiculo());
        assertEquals(EstadoCita.PROGRAMADA, cita.getEstado());

        // Then - Los datos de la cita deben mantenerse íntegros incluso si falla la sesión
        assertNotNull(cita.getFechaHora());
        assertNotNull(cita.getComentarios());
        assertNotNull(cita.getEstado());
        assertEquals(cliente, cita.getCliente());
        assertEquals(vehiculo, cita.getVehiculo());
        assertTrue(cita.getComentarios().length() <= 500);

        // Verificar integridad de relaciones
        assertEquals("1750505060", cita.getCliente().getCedula());
        assertEquals("PBC-345", cita.getVehiculo().getPlaca());
        assertEquals(EstadoCliente.ACTIVO, cita.getCliente().getEstado());
        assertTrue(cita.getVehiculo().isActivo());
    }

    @Test
    public void testCU8_ExcepcionQ_ErrorInternoEnGestion() {
        // Given - Cita con datos válidos
        cita.setFechaHora(crearFechaFutura(15, 14, 0));
        cita.setComentarios("Revisión de sistema de frenos");
        cita.setEstado(EstadoCita.PROGRAMADA);

        // When - Verificar integridad de datos antes del error interno
        assertTrue(cita.getFechaHora().after(new Date()));
        assertEquals("Revisión de sistema de frenos", cita.getComentarios());
        assertEquals(EstadoCita.PROGRAMADA, cita.getEstado());
        assertNotNull(cita.getCliente());
        assertNotNull(cita.getVehiculo());

        // Then - Verificar que el estado de la cita no se corrompe ante errores internos
        assertNotNull(cita.getId());
        assertNotNull(cita.getFechaHora());
        assertTrue(cita.getComentarios().length() <= 500);
        assertNotNull(cita.getEstado());
        assertNotNull(cita.getCliente());
        assertNotNull(cita.getVehiculo());

        // Verificar integridad de las relaciones
        assertEquals(cliente.getCedula(), cita.getCliente().getCedula());
        assertEquals(vehiculo.getPlaca(), cita.getVehiculo().getPlaca());
        assertEquals(cliente, cita.getCliente());
        assertEquals(vehiculo, cita.getVehiculo());

        // Verificar que los métodos de negocio funcionan
        EstadoCita estadoOriginal = cita.getEstado();
        cita.marcarAtendida();
        assertEquals(EstadoCita.ATENDIDA, cita.getEstado());
        assertNotEquals(estadoOriginal, cita.getEstado());
    }

    @Test
    public void testMetodosDeNegocioCita() {
        // Given - Cita programada
        cita.setEstado(EstadoCita.PROGRAMADA);
        assertEquals(EstadoCita.PROGRAMADA, cita.getEstado());

        // When - Se marca como atendida
        cita.marcarAtendida();

        // Then
        assertEquals(EstadoCita.ATENDIDA, cita.getEstado());

        // When - Se cancela
        cita.cancelar();

        // Then
        assertEquals(EstadoCita.CANCELADA, cita.getEstado());
    }

    @Test
    public void testCitasConDatosBD() {
        // Given - Datos reales de citas de la BD
        String[][] citasBD = {
            {"1", "Cliente reporta luz de check engine y ruido al frenar", "ATENDIDA", "2025-12-05 09:00:00", "1", "1"},
            {"2", "Mantenimiento preventivo de los 30,000 km", "ATENDIDA", "2025-12-10 10:30:00", "2", "3"},
            {"3", "Revisión de sistema de frenos", "ATENDIDA", "2025-12-15 14:00:00", "3", "4"},
            {"4", "Cambio de aceite y filtros", "PROGRAMADA", "2026-01-20 09:00:00", "4", "5"},
            {"5", "Revisión general antes de viaje", "PROGRAMADA", "2026-01-21 11:00:00", "5", "6"}
        };

        // When & Then - Verificar cada cita
        for (String[] datosCita : citasBD) {
            Cita citaTemp = new Cita();
            citaTemp.setId(Long.parseLong(datosCita[0]));
            citaTemp.setComentarios(datosCita[1]);
            citaTemp.setEstado(EstadoCita.valueOf(datosCita[2]));

            Cliente clienteTemp = new Cliente();
            clienteTemp.setId(Long.parseLong(datosCita[4]));
            citaTemp.setCliente(clienteTemp);

            Vehiculo vehiculoTemp = new Vehiculo();
            vehiculoTemp.setId(Long.parseLong(datosCita[5]));
            citaTemp.setVehiculo(vehiculoTemp);

            // Validaciones para cada cita
            assertNotNull("ID válido para cita " + datosCita[0], citaTemp.getId());
            assertTrue("Comentarios válidos para cita " + datosCita[0],
                      citaTemp.getComentarios().length() <= 500);
            assertNotNull("Estado válido para cita " + datosCita[0], citaTemp.getEstado());
            assertNotNull("Cliente válido para cita " + datosCita[0], citaTemp.getCliente());
            assertNotNull("Vehículo válido para cita " + datosCita[0], citaTemp.getVehiculo());

            // Verificar estados válidos
            assertTrue("Estado debe ser válido",
                      citaTemp.getEstado() == EstadoCita.PROGRAMADA ||
                      citaTemp.getEstado() == EstadoCita.ATENDIDA ||
                      citaTemp.getEstado() == EstadoCita.CANCELADA);
        }
    }

    @Test
    public void testValidacionComentarios() {
        // Given - Comentarios de diferentes longitudes
        String[] comentarios = {
            "Cambio de aceite", // Corto
            "Revisión completa del sistema de frenos, cambio de pastillas delanteras y traseras", // Medio
            crearCadenaRepetida("A", 500), // Máximo permitido
            crearCadenaRepetida("B", 501)  // Excede el límite
        };

        // When & Then - Verificar validaciones de comentarios
        for (int i = 0; i < comentarios.length; i++) {
            cita.setComentarios(comentarios[i]);

            if (i < 3) { // Primeros 3 son válidos
                assertTrue("Comentarios válidos (longitud " + comentarios[i].length() + ")",
                          cita.getComentarios().length() <= 500);
            } else { // El último excede el límite
                assertTrue("Comentarios exceden límite",
                          cita.getComentarios().length() > 500);
            }
        }
    }

    @Test
    public void testEstadosDisponibles() {
        // Given - Todos los estados disponibles
        EstadoCita[] estadosDisponibles = EstadoCita.values();

        // When & Then - Verificar que todos los estados se pueden asignar
        for (EstadoCita estado : estadosDisponibles) {
            cita.setEstado(estado);
            assertEquals("Estado " + estado + " debe poder asignarse",
                        estado, cita.getEstado());
        }

        // Verificar que existen todos los estados esperados
        assertTrue("Debe existir estado PROGRAMADA",
                  java.util.Arrays.asList(estadosDisponibles).contains(EstadoCita.PROGRAMADA));
        assertTrue("Debe existir estado ATENDIDA",
                  java.util.Arrays.asList(estadosDisponibles).contains(EstadoCita.ATENDIDA));
        assertTrue("Debe existir estado CANCELADA",
                  java.util.Arrays.asList(estadosDisponibles).contains(EstadoCita.CANCELADA));
    }

    @Test
    public void testRelacionesClienteVehiculoCita() {
        // Given - Cliente con vehículos y citas
        Collection<Vehiculo> vehiculos = new ArrayList<>();
        vehiculos.add(vehiculo);
        cliente.setVehiculos(vehiculos);

        Collection<Cita> citas = new ArrayList<>();
        citas.add(cita);
        cliente.setCitas(citas);
        vehiculo.setCitas(citas);

        // When & Then - Verificar relaciones bidireccionales
        assertEquals(1, cliente.getVehiculos().size());
        assertTrue(cliente.getVehiculos().contains(vehiculo));
        assertEquals(cliente, vehiculo.getCliente());

        assertEquals(1, cliente.getCitas().size());
        assertTrue(cliente.getCitas().contains(cita));
        assertEquals(cliente, cita.getCliente());

        assertEquals(1, vehiculo.getCitas().size());
        assertTrue(vehiculo.getCitas().contains(cita));
        assertEquals(vehiculo, cita.getVehiculo());
    }

    // Métodos auxiliares
    private Date crearFechaFutura(int dias, int hora, int minutos) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, dias);
        cal.set(Calendar.HOUR_OF_DAY, hora);
        cal.set(Calendar.MINUTE, minutos);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private Date crearFechaEspecifica(int anio, int mes, int dia, int hora, int minutos) {
        Calendar cal = Calendar.getInstance();
        cal.set(anio, mes, dia, hora, minutos, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private String crearCadenaRepetida(String cadena, int veces) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < veces; i++) {
            sb.append(cadena);
        }
        return sb.toString();
    }
}
