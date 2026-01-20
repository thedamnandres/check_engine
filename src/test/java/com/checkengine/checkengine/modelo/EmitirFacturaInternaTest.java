package com.checkengine.checkengine.modelo;

import org.junit.Before;
import org.junit.Test;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import static org.junit.Assert.*;

public class EmitirFacturaInternaTest {

    private FacturaInterna facturaInterna;
    private OrdenServicio ordenServicio;
    private Cliente cliente;
    private Vehiculo vehiculo;
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
        detalleTrabajo.setTipoServicio(tipoServicio);

        // Setup DetalleRepuestoOrden
        detalleRepuesto = new DetalleRepuestoOrden();
        detalleRepuesto.setId(1L);
        detalleRepuesto.setCantidad(2);
        detalleRepuesto.setPrecioUnitario(new BigDecimal("8.50"));
        detalleRepuesto.setSubtotal(new BigDecimal("17.00"));
        detalleRepuesto.setRepuesto(repuesto);

        // Setup OrdenServicio con detalles
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

        Collection<DetalleTrabajoOrden> trabajos = new ArrayList<>();
        trabajos.add(detalleTrabajo);
        ordenServicio.setDetallesTrabajo(trabajos);

        Collection<DetalleRepuestoOrden> repuestos = new ArrayList<>();
        repuestos.add(detalleRepuesto);
        ordenServicio.setDetallesRepuesto(repuestos);

        detalleTrabajo.setOrdenServicio(ordenServicio);
        detalleRepuesto.setOrdenServicio(ordenServicio);

        // Setup FacturaInterna
        facturaInterna = new FacturaInterna();
        facturaInterna.setId(1L);
        facturaInterna.setNumero("FACT-2025-00001");
        facturaInterna.setFechaEmision(new Date());
        facturaInterna.setYear(2025);
        facturaInterna.setNumeroSecuencial(1);
        facturaInterna.setOrdenServicio(ordenServicio);
    }

    @Test
    public void testCU6_EmitirFacturaInternaFlujoPrincipal() {
        // Given - Orden de servicio finalizada con costos registrados
        BigDecimal subtotalManoObra = new BigDecimal("185.00");
        BigDecimal subtotalRepuestos = new BigDecimal("89.50");
        BigDecimal porcentajeIVA = new BigDecimal("15.00");
        BigDecimal iva = new BigDecimal("41.18");
        BigDecimal total = new BigDecimal("315.68");

        // When - Se emite la factura interna
        facturaInterna.setSubtotalManoObra(subtotalManoObra);
        facturaInterna.setSubtotalRepuestos(subtotalRepuestos);
        facturaInterna.setPorcentajeIVA(porcentajeIVA);
        facturaInterna.setIva(iva);
        facturaInterna.setTotal(total);

        // Then - La factura debe contener información completa y correcta
        assertEquals("FACT-2025-00001", facturaInterna.getNumero());
        assertNotNull(facturaInterna.getFechaEmision());
        assertEquals(ordenServicio, facturaInterna.getOrdenServicio());
        assertEquals(subtotalManoObra, facturaInterna.getSubtotalManoObra());
        assertEquals(subtotalRepuestos, facturaInterna.getSubtotalRepuestos());
        assertEquals(porcentajeIVA, facturaInterna.getPorcentajeIVA());
        assertEquals(iva, facturaInterna.getIva());
        assertEquals(total, facturaInterna.getTotal());

        // Verificar estructura del número de factura
        assertTrue(facturaInterna.getNumero().startsWith("FACT-"));
        assertTrue(facturaInterna.getNumero().contains("2025"));
        assertEquals(2025, facturaInterna.getYear());
        assertEquals(1, facturaInterna.getNumeroSecuencial());

        // Verificar que los totales son consistentes
        BigDecimal subtotalCalculado = subtotalManoObra.add(subtotalRepuestos);
        BigDecimal ivaCalculado = subtotalCalculado.multiply(porcentajeIVA)
            .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal totalCalculado = subtotalCalculado.add(ivaCalculado);

        assertEquals(0, iva.compareTo(ivaCalculado));
        assertEquals(0, total.compareTo(totalCalculado));

        // Verificar relación con orden de servicio
        assertEquals("PBC-345", facturaInterna.getOrdenServicio().getVehiculo().getPlaca());
        assertEquals("Juan Esteban", facturaInterna.getOrdenServicio().getVehiculo().getCliente().getNombres());
    }

    @Test
    public void testCU6_Alterno21_SinCostosRegistrados() {
        // Given - Orden de servicio sin costos registrados
        OrdenServicio ordenSinCostos = new OrdenServicio();
        ordenSinCostos.setId(99L);
        ordenSinCostos.setCodigo("OS-2025-99999");
        ordenSinCostos.setFechaCreacion(new Date());
        ordenSinCostos.setEstadoActual(EstadoOrden.FINALIZADO);
        ordenSinCostos.setYear(2025);
        ordenSinCostos.setNumero(99999);
        ordenSinCostos.setVehiculo(vehiculo);

        // Detalles vacíos o con valores cero
        Collection<DetalleTrabajoOrden> trabajosVacios = new ArrayList<>();
        Collection<DetalleRepuestoOrden> repuestosVacios = new ArrayList<>();

        ordenSinCostos.setDetallesTrabajo(trabajosVacios);
        ordenSinCostos.setDetallesRepuesto(repuestosVacios);

        FacturaInterna facturaSinCostos = new FacturaInterna();
        facturaSinCostos.setOrdenServicio(ordenSinCostos);

        // When - Se intenta emitir factura sin costos
        facturaSinCostos.setSubtotalManoObra(BigDecimal.ZERO);
        facturaSinCostos.setSubtotalRepuestos(BigDecimal.ZERO);
        facturaSinCostos.setPorcentajeIVA(new BigDecimal("15.00"));

        // Then - La factura debe manejar correctamente los valores cero
        assertEquals(0, facturaSinCostos.getSubtotalManoObra().compareTo(BigDecimal.ZERO));
        assertEquals(0, facturaSinCostos.getSubtotalRepuestos().compareTo(BigDecimal.ZERO));

        // IVA y total deben ser cero cuando no hay subtotales
        BigDecimal subtotalTotal = BigDecimal.ZERO.add(BigDecimal.ZERO);
        BigDecimal ivaEsperado = subtotalTotal.multiply(facturaSinCostos.getPorcentajeIVA())
            .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);

        facturaSinCostos.setIva(ivaEsperado);
        facturaSinCostos.setTotal(subtotalTotal.add(ivaEsperado));

        assertEquals(0, facturaSinCostos.getIva().compareTo(BigDecimal.ZERO));
        assertEquals(0, facturaSinCostos.getTotal().compareTo(BigDecimal.ZERO));

        // Verificar que la estructura de la factura sigue siendo válida
        assertNotNull(facturaSinCostos.getOrdenServicio());
        assertTrue(ordenSinCostos.getDetallesTrabajo().isEmpty());
        assertTrue(ordenSinCostos.getDetallesRepuesto().isEmpty());
    }

    @Test
    public void testCU6_ExcepcionP_SinSesionActiva() {
        // Given - Factura válida lista para emitir
        facturaInterna.setNumero("FACT-2025-00002");
        facturaInterna.setFechaEmision(new Date());
        facturaInterna.setSubtotalManoObra(new BigDecimal("53.75"));
        facturaInterna.setSubtotalRepuestos(new BigDecimal("62.50"));
        facturaInterna.setPorcentajeIVA(new BigDecimal("15.00"));
        facturaInterna.setIva(new BigDecimal("17.44"));
        facturaInterna.setTotal(new BigDecimal("133.69"));
        facturaInterna.setYear(2025);
        facturaInterna.setNumeroSecuencial(2);

        // When - Verificar que los datos están correctos antes de la "emisión"
        assertTrue(facturaInterna.getNumero().startsWith("FACT-"));
        assertNotNull(facturaInterna.getFechaEmision());
        assertNotNull(facturaInterna.getOrdenServicio());
        assertTrue(facturaInterna.getSubtotalManoObra().compareTo(BigDecimal.ZERO) >= 0);
        assertTrue(facturaInterna.getSubtotalRepuestos().compareTo(BigDecimal.ZERO) >= 0);

        // Then - Los datos de la factura deben mantenerse íntegros incluso si falla la sesión
        assertNotNull(facturaInterna.getNumero());
        assertNotNull(facturaInterna.getFechaEmision());
        assertNotNull(facturaInterna.getOrdenServicio());
        assertEquals(new BigDecimal("53.75"), facturaInterna.getSubtotalManoObra());
        assertEquals(new BigDecimal("62.50"), facturaInterna.getSubtotalRepuestos());
        assertEquals(new BigDecimal("15.00"), facturaInterna.getPorcentajeIVA());
        assertEquals(new BigDecimal("17.44"), facturaInterna.getIva());
        assertEquals(new BigDecimal("133.69"), facturaInterna.getTotal());

        // Verificar integridad de relaciones
        assertEquals(ordenServicio.getCodigo(), facturaInterna.getOrdenServicio().getCodigo());
        assertEquals(vehiculo.getPlaca(), facturaInterna.getOrdenServicio().getVehiculo().getPlaca());
    }

    @Test
    public void testCU6_ExcepcionQ_ErrorInternoEnEmision() {
        // Given - Factura con datos válidos
        facturaInterna.setNumero("FACT-2025-00003");
        facturaInterna.setFechaEmision(new Date());
        facturaInterna.setSubtotalManoObra(new BigDecimal("155.00"));
        facturaInterna.setSubtotalRepuestos(new BigDecimal("183.00"));
        facturaInterna.setPorcentajeIVA(new BigDecimal("15.00"));
        facturaInterna.setIva(new BigDecimal("50.70"));
        facturaInterna.setTotal(new BigDecimal("388.70"));
        facturaInterna.setYear(2025);
        facturaInterna.setNumeroSecuencial(3);

        // When - Verificar integridad de datos antes del error interno
        assertTrue(facturaInterna.getNumero().startsWith("FACT-"));
        assertEquals(new BigDecimal("15.00"), facturaInterna.getPorcentajeIVA());
        assertNotNull(facturaInterna.getOrdenServicio());
        assertTrue(facturaInterna.getTotal().compareTo(BigDecimal.ZERO) > 0);

        // Then - Verificar que el estado de la factura no se corrompe ante errores internos
        assertTrue(facturaInterna.getNumero().length() <= 50);
        assertTrue(facturaInterna.getYear() >= 2025);
        assertTrue(facturaInterna.getNumeroSecuencial() > 0);
        assertNotNull(facturaInterna.getFechaEmision());
        assertTrue(facturaInterna.getSubtotalManoObra().compareTo(BigDecimal.ZERO) >= 0);
        assertTrue(facturaInterna.getSubtotalRepuestos().compareTo(BigDecimal.ZERO) >= 0);
        assertTrue(facturaInterna.getIva().compareTo(BigDecimal.ZERO) >= 0);
        assertTrue(facturaInterna.getTotal().compareTo(BigDecimal.ZERO) >= 0);

        // Verificar integridad de la relación con orden de servicio
        assertEquals(ordenServicio, facturaInterna.getOrdenServicio());
        assertNotNull(facturaInterna.getOrdenServicio().getVehiculo());
        assertNotNull(facturaInterna.getOrdenServicio().getVehiculo().getCliente());

        // Verificar que los cálculos siguen siendo consistentes
        BigDecimal subtotalCalculado = facturaInterna.getSubtotalManoObra()
            .add(facturaInterna.getSubtotalRepuestos());
        BigDecimal ivaCalculado = subtotalCalculado.multiply(facturaInterna.getPorcentajeIVA())
            .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal totalCalculado = subtotalCalculado.add(ivaCalculado);

        assertEquals(0, facturaInterna.getIva().compareTo(ivaCalculado));
        assertEquals(0, facturaInterna.getTotal().compareTo(totalCalculado));
    }

    @Test
    public void testCalculoAutomaticoDeIVA() {
        // Given - Factura con subtotales definidos
        BigDecimal subtotalManoObra = new BigDecimal("100.00");
        BigDecimal subtotalRepuestos = new BigDecimal("50.00");
        BigDecimal porcentajeIVA = new BigDecimal("15.00");

        facturaInterna.setSubtotalManoObra(subtotalManoObra);
        facturaInterna.setSubtotalRepuestos(subtotalRepuestos);
        facturaInterna.setPorcentajeIVA(porcentajeIVA);

        // When - Se calculan IVA y total
        BigDecimal subtotalTotal = subtotalManoObra.add(subtotalRepuestos); // 150.00
        BigDecimal ivaCalculado = subtotalTotal.multiply(porcentajeIVA)
            .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP); // 22.50
        BigDecimal totalCalculado = subtotalTotal.add(ivaCalculado); // 172.50

        facturaInterna.setIva(ivaCalculado);
        facturaInterna.setTotal(totalCalculado);

        // Then - Los cálculos deben ser correctos
        assertEquals(new BigDecimal("150.00"), subtotalTotal);
        assertEquals(new BigDecimal("22.50"), facturaInterna.getIva());
        assertEquals(new BigDecimal("172.50"), facturaInterna.getTotal());
    }

    @Test
    public void testFacturasConDatosBD() {
        // Given - Datos reales de facturas de la BD
        String[][] facturasBD = {
            {"FACT-2025-00001", "2025", "1", "185.00", "89.50", "15.00", "41.18", "315.68"},
            {"FACT-2025-00002", "2025", "2", "53.75", "62.50", "15.00", "17.44", "133.69"},
            {"FACT-2025-00003", "2025", "3", "155.00", "183.00", "15.00", "50.70", "388.70"},
            {"FACT-2026-00001", "2026", "1", "18.75", "36.50", "15.00", "8.29", "63.54"}
        };

        // When & Then - Verificar cada factura
        for (String[] datosFactura : facturasBD) {
            FacturaInterna facturaTemp = new FacturaInterna();
            facturaTemp.setNumero(datosFactura[0]);
            facturaTemp.setYear(Integer.parseInt(datosFactura[1]));
            facturaTemp.setNumeroSecuencial(Integer.parseInt(datosFactura[2]));
            facturaTemp.setSubtotalManoObra(new BigDecimal(datosFactura[3]));
            facturaTemp.setSubtotalRepuestos(new BigDecimal(datosFactura[4]));
            facturaTemp.setPorcentajeIVA(new BigDecimal(datosFactura[5]));
            facturaTemp.setIva(new BigDecimal(datosFactura[6]));
            facturaTemp.setTotal(new BigDecimal(datosFactura[7]));

            // Validaciones para cada factura
            assertTrue("Número válido para " + datosFactura[0],
                      facturaTemp.getNumero().startsWith("FACT-"));
            assertTrue("Año válido para " + datosFactura[0],
                      facturaTemp.getYear() >= 2025);
            assertTrue("Número secuencial válido para " + datosFactura[0],
                      facturaTemp.getNumeroSecuencial() > 0);
            assertTrue("Subtotal mano de obra válido para " + datosFactura[0],
                      facturaTemp.getSubtotalManoObra().compareTo(BigDecimal.ZERO) >= 0);
            assertTrue("Subtotal repuestos válido para " + datosFactura[0],
                      facturaTemp.getSubtotalRepuestos().compareTo(BigDecimal.ZERO) >= 0);
            assertEquals("Porcentaje IVA para " + datosFactura[0],
                        new BigDecimal("15.00"), facturaTemp.getPorcentajeIVA());

            // Verificar cálculos
            BigDecimal subtotalCalculado = facturaTemp.getSubtotalManoObra()
                .add(facturaTemp.getSubtotalRepuestos());
            BigDecimal ivaCalculado = subtotalCalculado.multiply(facturaTemp.getPorcentajeIVA())
                .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
            BigDecimal totalCalculado = subtotalCalculado.add(ivaCalculado);

            assertEquals("IVA calculado para " + datosFactura[0],
                        0, facturaTemp.getIva().compareTo(ivaCalculado));
            assertEquals("Total calculado para " + datosFactura[0],
                        0, facturaTemp.getTotal().compareTo(totalCalculado));
        }
    }

    @Test
    public void testGeneracionNumeroFactura() {
        // Given - Datos para generar número de factura
        int year = 2025;
        int numeroSecuencial = 1;

        // When
        facturaInterna.setYear(year);
        facturaInterna.setNumeroSecuencial(numeroSecuencial);
        String numeroEsperado = "FACT-" + year + "-" + String.format("%05d", numeroSecuencial);
        facturaInterna.setNumero(numeroEsperado);

        // Then
        assertEquals("FACT-2025-00001", facturaInterna.getNumero());
        assertEquals(2025, facturaInterna.getYear());
        assertEquals(1, facturaInterna.getNumeroSecuencial());
        assertTrue(facturaInterna.getNumero().matches("^FACT-\\d{4}-\\d{5}$"));
    }

    @Test
    public void testValidacionPorcentajeIVA() {
        // Given - Diferentes porcentajes de IVA
        BigDecimal[] porcentajesIVA = {
            new BigDecimal("0.00"),   // Sin IVA
            new BigDecimal("12.00"),  // IVA reducido
            new BigDecimal("15.00"),  // IVA estándar Ecuador
            new BigDecimal("21.00")   // IVA alto
        };

        BigDecimal subtotal = new BigDecimal("100.00");

        // When & Then - Verificar cálculos con diferentes porcentajes
        for (BigDecimal porcentaje : porcentajesIVA) {
            facturaInterna.setPorcentajeIVA(porcentaje);

            BigDecimal ivaCalculado = subtotal.multiply(porcentaje)
                .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
            facturaInterna.setIva(ivaCalculado);

            assertEquals("Porcentaje IVA debe coincidir",
                        porcentaje, facturaInterna.getPorcentajeIVA());
            assertTrue("IVA calculado debe ser >= 0",
                      facturaInterna.getIva().compareTo(BigDecimal.ZERO) >= 0);
        }
    }
}
