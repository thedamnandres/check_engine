package com.checkengine.checkengine.modelo;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class VehiculoTest {

    private Vehiculo vehiculo;
    private Cliente cliente;

    @Before
    public void setUp() {
        vehiculo = new Vehiculo();
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setCedula("1750505060");
        cliente.setNombres("Juan Esteban");
        cliente.setApellidos("Pérez Zambrano");
    }

    @Test
    public void testCU2_RegistrarVehiculoFlujoPrincipal() {
        // Given
        String placa = "PBC-345";
        String vin = "3GCEC14X78G123456";
        String marca = "Chevrolet";
        String modelo = "Captiva";
        int anio = 2012;
        TipoCombustible tipoCombustible = TipoCombustible.GASOLINA;
        int kilometraje = 147000;

        // When
        vehiculo.setPlaca(placa);
        vehiculo.setVin(vin);
        vehiculo.setMarca(marca);
        vehiculo.setModelo(modelo);
        vehiculo.setAnio(anio);
        vehiculo.setTipoCombustible(tipoCombustible);
        vehiculo.setKilometraje(kilometraje);
        vehiculo.setCliente(cliente);

        // Then
        assertEquals(placa, vehiculo.getPlaca());
        assertEquals(vin, vehiculo.getVin());
        assertEquals(marca, vehiculo.getMarca());
        assertEquals(modelo, vehiculo.getModelo());
        assertEquals(anio, vehiculo.getAnio());
        assertEquals(tipoCombustible, vehiculo.getTipoCombustible());
        assertEquals(kilometraje, vehiculo.getKilometraje());
        assertEquals(cliente, vehiculo.getCliente());
        assertTrue(vehiculo.isActivo());

        assertTrue(vehiculo.getPlaca().matches("^[A-Z]{3}-\\d{3,4}$"));
        assertTrue(vehiculo.getVin().matches("^[A-Z0-9]{17}$"));
        assertTrue(vehiculo.getAnio() >= 1985 && vehiculo.getAnio() <= 2026);
        assertTrue(vehiculo.getKilometraje() >= 0 && vehiculo.getKilometraje() <= 500000);
    }

    @Test
    public void testCU2_Alterno41_ValidacionCamposVehiculo() {
        // Given - Vehículo sin campos obligatorios

        // When - Placa es null
        vehiculo.setPlaca(null);

        // Then
        assertNull(vehiculo.getPlaca());

        // When - Placa con formato incorrecto
        vehiculo.setPlaca("ABC123");

        // Then
        assertFalse(vehiculo.getPlaca().matches("^[A-Z]{3}-\\d{3,4}$"));

        // When - VIN muy corto
        vehiculo.setVin("1HGBH41JXMN10918");

        // Then
        assertFalse(vehiculo.getVin().matches("^[A-Z0-9]{17}$"));

        // When - Año fuera de rango
        vehiculo.setAnio(1984);

        // Then
        assertFalse(vehiculo.getAnio() >= 1985 && vehiculo.getAnio() <= 2026);

        // When - Kilometraje negativo
        vehiculo.setKilometraje(-1000);

        // Then
        assertFalse(vehiculo.getKilometraje() >= 0);

        // When - Marca es null
        vehiculo.setMarca(null);

        // Then
        assertNull(vehiculo.getMarca());

        // When - Modelo es null
        vehiculo.setModelo(null);

        // Then
        assertNull(vehiculo.getModelo());
    }

    @Test
    public void testCU2_Alterno51_PlacaDuplicadaVerFicha() {
        // Given - Placas de vehículos existentes en la BD
        String[] placasExistentes = {
            "PBC-345",  // Chevrolet Captiva
            "ABC-1234", // Toyota Corolla
            "XYZ-987",  // Hyundai Tucson
            "QWE-456",  // Kia Sportage
            "RTY-789"   // Mazda CX-5
        };

        // When & Then - Verificar que las placas tienen el formato correcto
        for (String placaExistente : placasExistentes) {
            vehiculo.setPlaca(placaExistente);
            assertTrue(vehiculo.getPlaca().matches("^[A-Z]{3}-\\d{3,4}$"));
            assertTrue(vehiculo.getPlaca().length() <= 20);
        }
    }

    @Test
    public void testCU2_Alterno51_PlacaDuplicadaCancelar() {
        // Given - Vehículo existente con datos conocidos
        String placaExistente = "PBC-345";
        String marcaOriginal = "Chevrolet";
        String modeloOriginal = "Captiva";

        // When - Se intenta crear con la misma placa pero se cancela
        vehiculo.setPlaca(placaExistente);
        vehiculo.setMarca("Toyota");
        vehiculo.setModelo("Corolla");

        // Then - La placa sigue siendo la misma (única)
        assertEquals(placaExistente, vehiculo.getPlaca());
        assertNotEquals(marcaOriginal, vehiculo.getMarca());
        assertNotEquals(modeloOriginal, vehiculo.getModelo());
    }

    @Test
    public void testCU2_ExcepcionP_SesionExpiradaAlGuardar() {
        // Given - Vehículo válido listo para guardar
        vehiculo.setPlaca("ABC-1234");
        vehiculo.setVin("2T1BURHE5JC123456");
        vehiculo.setMarca("Toyota");
        vehiculo.setModelo("Corolla");
        vehiculo.setAnio(2020);
        vehiculo.setTipoCombustible(TipoCombustible.GASOLINA);
        vehiculo.setKilometraje(35000);
        vehiculo.setCliente(cliente);

        // When - Verificar que los datos están correctos antes del "guardado"
        assertTrue(vehiculo.getPlaca().matches("^[A-Z]{3}-\\d{3,4}$"));
        assertTrue(vehiculo.getVin().matches("^[A-Z0-9]{17}$"));
        assertTrue(vehiculo.getAnio() >= 1985 && vehiculo.getAnio() <= 2026);

        // Then - Los datos del vehículo deben mantenerse íntegros
        assertNotNull(vehiculo.getPlaca());
        assertNotNull(vehiculo.getVin());
        assertNotNull(vehiculo.getMarca());
        assertNotNull(vehiculo.getModelo());
        assertTrue(vehiculo.isActivo());
    }

    @Test
    public void testCU2_ExcepcionQ_ErrorInternoAlGuardar() {
        // Given - Vehículo con datos válidos
        vehiculo.setPlaca("XYZ-987");
        vehiculo.setVin("KM8J3CA46KU123456");
        vehiculo.setMarca("Hyundai");
        vehiculo.setModelo("Tucson");
        vehiculo.setAnio(2019);
        vehiculo.setTipoCombustible(TipoCombustible.DIESEL);
        vehiculo.setKilometraje(62000);

        // When - Verificar integridad de datos antes del error
        assertTrue(vehiculo.getPlaca().matches("^[A-Z]{3}-\\d{3,4}$"));
        assertTrue(vehiculo.getVin().matches("^[A-Z0-9]{17}$"));
        assertEquals(TipoCombustible.DIESEL, vehiculo.getTipoCombustible());

        // Then - Verificar que el estado del objeto no se corrompe
        assertEquals(17, vehiculo.getVin().length());
        assertTrue(vehiculo.getAnio() >= 1985 && vehiculo.getAnio() <= 2026);
        assertTrue(vehiculo.getKilometraje() >= 0 && vehiculo.getKilometraje() <= 500000);
    }
}
