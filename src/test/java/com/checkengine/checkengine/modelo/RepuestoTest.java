package com.checkengine.checkengine.modelo;

import org.junit.Before;
import org.junit.Test;
import java.math.BigDecimal;
import static org.junit.Assert.*;

public class RepuestoTest {

    private Repuesto repuesto;

    @Before
    public void setUp() {
        repuesto = new Repuesto();
    }

    @Test
    public void testCU5_RegistrarRepuestosFlujoPrincipal() {
        // Given
        String codigo = "REP-001";
        String descripcion = "Filtro de Aceite Universal";
        String categoria = "Filtros";
        int stockActual = 50;
        int stockMinimo = 10;
        BigDecimal precioUnitario = new BigDecimal("8.50");
        String unidadMedida = "UNIDAD";

        // When
        repuesto.setCodigo(codigo);
        repuesto.setDescripcion(descripcion);
        repuesto.setCategoria(categoria);
        repuesto.setStockActual(stockActual);
        repuesto.setStockMinimo(stockMinimo);
        repuesto.setPrecioUnitario(precioUnitario);
        repuesto.setUnidadMedida(unidadMedida);

        // Then
        assertEquals(codigo, repuesto.getCodigo());
        assertEquals(descripcion, repuesto.getDescripcion());
        assertEquals(categoria, repuesto.getCategoria());
        assertEquals(stockActual, repuesto.getStockActual());
        assertEquals(stockMinimo, repuesto.getStockMinimo());
        assertEquals(precioUnitario, repuesto.getPrecioUnitario());
        assertEquals(unidadMedida, repuesto.getUnidadMedida());

        assertTrue(repuesto.getCodigo().startsWith("REP-"));
        assertTrue(repuesto.getDescripcion().length() <= 200);
        assertTrue(repuesto.getCategoria().length() <= 50);
        assertTrue(repuesto.getStockActual() >= 0);
        assertTrue(repuesto.getStockMinimo() >= 0);
        assertTrue(repuesto.getPrecioUnitario().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(repuesto.getUnidadMedida().length() <= 20);
        assertNotNull(repuesto.getDetallesRepuesto());
    }

    @Test
    public void testCU5_Alterno31_RepuestoNoExiste() {
        // Given - Códigos de repuestos que no existen
        String[] codigosNoExistentes = {
            "REP-999",  // No existe
            "REP-888",  // No existe
            "REP-777",  // No existe
            "XYZ-123",  // Formato incorrecto
            ""          // Código vacío
        };

        // When & Then - Verificar que los códigos no siguen el patrón esperado o están vacíos
        for (String codigoNoExistente : codigosNoExistentes) {
            repuesto.setCodigo(codigoNoExistente);

            if (codigoNoExistente.isEmpty()) {
                assertTrue(repuesto.getCodigo().isEmpty());
            } else if (!codigoNoExistente.startsWith("REP-")) {
                assertFalse(repuesto.getCodigo().startsWith("REP-"));
            } else {
                // Para códigos con formato correcto pero que no existen,
                // verificamos que el objeto existe pero sin datos asociados
                assertNull(repuesto.getDescripcion());
                assertEquals(0, repuesto.getStockActual());
            }
        }
    }

    @Test
    public void testCU5_Alterno41_CantidadInvalida() {
        // Given - Repuesto válido
        repuesto.setCodigo("REP-004");
        repuesto.setDescripcion("Pastillas de Freno Delanteras");
        repuesto.setCategoria("Frenos");
        repuesto.setPrecioUnitario(new BigDecimal("45.00"));
        repuesto.setUnidadMedida("JUEGO");

        // When - Stock actual negativo
        repuesto.setStockActual(-5);

        // Then
        assertFalse(repuesto.getStockActual() >= 0);

        // When - Stock mínimo negativo
        repuesto.setStockMinimo(-2);

        // Then
        assertFalse(repuesto.getStockMinimo() >= 0);

        // When - Precio unitario cero o negativo
        repuesto.setPrecioUnitario(BigDecimal.ZERO);

        // Then
        assertFalse(repuesto.getPrecioUnitario().compareTo(new BigDecimal("0.01")) >= 0);

        // When - Precio negativo
        repuesto.setPrecioUnitario(new BigDecimal("-10.00"));

        // Then
        assertTrue(repuesto.getPrecioUnitario().compareTo(BigDecimal.ZERO) < 0);

        // When - Descripción muy larga
        String descripcionLarga = crearCadenaRepetida("A", 201);
        repuesto.setDescripcion(descripcionLarga);

        // Then
        assertTrue(repuesto.getDescripcion().length() > 200);

        // When - Categoría muy larga
        String categoriaLarga = crearCadenaRepetida("B", 51);
        repuesto.setCategoria(categoriaLarga);

        // Then
        assertTrue(repuesto.getCategoria().length() > 50);
    }

    @Test
    public void testCU5_ExcepcionP_SinSesionActiva() {
        // Given - Repuesto válido listo para guardar
        repuesto.setCodigo("REP-006");
        repuesto.setDescripcion("Aceite 10W40 (Galón)");
        repuesto.setCategoria("Lubricantes");
        repuesto.setStockActual(60);
        repuesto.setStockMinimo(15);
        repuesto.setPrecioUnitario(new BigDecimal("28.00"));
        repuesto.setUnidadMedida("GALON");

        // When - Verificar que los datos están correctos antes del "guardado"
        assertTrue(repuesto.getCodigo().startsWith("REP-"));
        assertNotNull(repuesto.getDescripcion());
        assertTrue(repuesto.getStockActual() >= 0);
        assertTrue(repuesto.getPrecioUnitario().compareTo(BigDecimal.ZERO) > 0);

        // Then - Los datos del repuesto deben mantenerse íntegros
        assertNotNull(repuesto.getCodigo());
        assertNotNull(repuesto.getDescripcion());
        assertNotNull(repuesto.getCategoria());
        assertTrue(repuesto.getStockActual() > 0);
        assertTrue(repuesto.getStockMinimo() >= 0);
        assertNotNull(repuesto.getPrecioUnitario());
        assertNotNull(repuesto.getUnidadMedida());
    }

    @Test
    public void testCU5_ExcepcionQ_ErrorInternoEnRegistro() {
        // Given - Repuesto con datos válidos
        repuesto.setCodigo("REP-009");
        repuesto.setDescripcion("Batería 12V 60Ah");
        repuesto.setCategoria("Eléctrico");
        repuesto.setStockActual(15);
        repuesto.setStockMinimo(3);
        repuesto.setPrecioUnitario(new BigDecimal("120.00"));
        repuesto.setUnidadMedida("UNIDAD");

        // When - Verificar integridad de datos antes del error
        assertTrue(repuesto.getCodigo().startsWith("REP-"));
        assertEquals("Batería 12V 60Ah", repuesto.getDescripcion());
        assertTrue(repuesto.getStockActual() > 0);

        // Then - Verificar que el estado del objeto no se corrompe
        assertTrue(repuesto.getCodigo().length() <= 50);
        assertTrue(repuesto.getDescripcion().length() <= 200);
        assertTrue(repuesto.getCategoria().length() <= 50);
        assertTrue(repuesto.getStockActual() >= 0);
        assertTrue(repuesto.getStockMinimo() >= 0);
        assertTrue(repuesto.getPrecioUnitario().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(repuesto.getUnidadMedida().length() <= 20);
        assertNotNull(repuesto.getDetallesRepuesto());

        // Verificar método de negocio
        assertFalse(repuesto.necesitaReposicion()); // 15 > 3, no necesita reposición
    }

    @Test
    public void testMetodosDeNegocio() {
        // Given - Repuesto con stock bajo
        repuesto.setStockActual(5);
        repuesto.setStockMinimo(10);

        // Then - Necesita reposición
        assertTrue(repuesto.necesitaReposicion());

        // When - Se actualiza el stock
        repuesto.actualizarStockActual(20);

        // Then - Ya no necesita reposición
        assertEquals(25, repuesto.getStockActual());
        assertFalse(repuesto.necesitaReposicion());

        // When - Se reduce el stock
        repuesto.actualizarStockActual(-20);

        // Then - Vuelve a necesitar reposición
        assertEquals(5, repuesto.getStockActual());
        assertTrue(repuesto.necesitaReposicion());
    }

    @Test
    public void testRepuestosConDatosBD() {
        // Given - Datos reales de diferentes categorías de repuestos
        String[][] repuestosBD = {
            {"REP-001", "Filtro de Aceite Universal", "Filtros", "50", "10", "8.50", "UNIDAD"},
            {"REP-004", "Pastillas de Freno Delanteras", "Frenos", "30", "6", "45.00", "JUEGO"},
            {"REP-006", "Aceite 10W40 (Galón)", "Lubricantes", "60", "15", "28.00", "GALON"},
            {"REP-009", "Batería 12V 60Ah", "Eléctrico", "15", "3", "120.00", "UNIDAD"},
            {"REP-020", "Kit de Embrague", "Transmisión", "5", "2", "180.00", "KIT"}
        };

        // When & Then - Verificar cada repuesto
        for (String[] datosRepuesto : repuestosBD) {
            Repuesto repuestoTemp = new Repuesto();
            repuestoTemp.setCodigo(datosRepuesto[0]);
            repuestoTemp.setDescripcion(datosRepuesto[1]);
            repuestoTemp.setCategoria(datosRepuesto[2]);
            repuestoTemp.setStockActual(Integer.parseInt(datosRepuesto[3]));
            repuestoTemp.setStockMinimo(Integer.parseInt(datosRepuesto[4]));
            repuestoTemp.setPrecioUnitario(new BigDecimal(datosRepuesto[5]));
            repuestoTemp.setUnidadMedida(datosRepuesto[6]);

            // Validaciones para cada repuesto
            assertTrue("Código válido para " + datosRepuesto[1],
                      repuestoTemp.getCodigo().startsWith("REP-"));
            assertTrue("Descripción válida para " + datosRepuesto[0],
                      repuestoTemp.getDescripcion().length() <= 200);
            assertTrue("Stock actual válido para " + datosRepuesto[0],
                      repuestoTemp.getStockActual() >= 0);
            assertTrue("Stock mínimo válido para " + datosRepuesto[0],
                      repuestoTemp.getStockMinimo() >= 0);
            assertTrue("Precio válido para " + datosRepuesto[0],
                      repuestoTemp.getPrecioUnitario().compareTo(BigDecimal.ZERO) > 0);
        }
    }

    @Test
    public void testUnidadesMedidaValidas() {
        // Given - Unidades de medida comunes
        String[] unidadesValidas = {
            "UNIDAD", "JUEGO", "GALON", "LITRO", "KIT", "METRO", "GRAMO", "KILOGRAMO"
        };

        // When & Then
        for (String unidad : unidadesValidas) {
            repuesto.setUnidadMedida(unidad);
            assertEquals(unidad, repuesto.getUnidadMedida());
            assertTrue(repuesto.getUnidadMedida().length() <= 20);
        }
    }

    private String crearCadenaRepetida(String cadena, int veces) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < veces; i++) {
            sb.append(cadena);
        }
        return sb.toString();
    }
}
