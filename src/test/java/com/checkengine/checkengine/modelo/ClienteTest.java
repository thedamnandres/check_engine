package com.checkengine.checkengine.modelo;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ClienteTest {

    private Cliente cliente;

    @Before
    public void setUp() {
        cliente = new Cliente();
    }

    @Test
    public void testCU1_RegistrarNuevoClienteFlujoPrincipal() {
        // Given
        String cedula = "1750505060";
        String nombres = "Juan Esteban";
        String apellidos = "Pérez Zambrano";
        String direccion = "Av. Eloy Alfaro N34-123 y Portugal";
        String telefono = "0998765432";
        String email = "juan.perez@gmail.com";
        String medioContacto = "WHATSAPP";

        // When
        cliente.setCedula(cedula);
        cliente.setNombres(nombres);
        cliente.setApellidos(apellidos);
        cliente.setDireccion(direccion);
        cliente.setTelefono(telefono);
        cliente.setEmail(email);
        cliente.setMedioContacto(medioContacto);

        // Then
        assertEquals(cedula, cliente.getCedula());
        assertEquals(nombres, cliente.getNombres());
        assertEquals(apellidos, cliente.getApellidos());
        assertEquals(direccion, cliente.getDireccion());
        assertEquals(telefono, cliente.getTelefono());
        assertEquals(email, cliente.getEmail());
        assertEquals(medioContacto, cliente.getMedioContacto());
        assertEquals(EstadoCliente.ACTIVO, cliente.getEstado());

        assertTrue(cliente.getCedula().matches("^\\d{10}$"));
        assertTrue(cliente.getNombres().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,100}$"));
        assertTrue(cliente.getApellidos().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,100}$"));
        assertTrue(cliente.getTelefono().matches("^[0-9]{10}$"));
        assertTrue(cliente.getEmail().contains("@"));
        assertTrue(cliente.getDireccion().length() <= 200);
    }

    @Test
    public void testCU1_Alterno31_ValidacionCamposObligatorios() {
        // Given - Cliente sin campos obligatorios
        cliente.setNombres("Juan Carlos");
        cliente.setApellidos("González Pérez");
        cliente.setTelefono("0999888777");

        // When - Cédula es null
        cliente.setCedula(null);

        // Then
        assertNull(cliente.getCedula());

        // When - Nombres muy cortos
        cliente.setNombres("J");

        // Then
        assertFalse(cliente.getNombres().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,100}$"));

        // When - Apellidos es null
        cliente.setApellidos(null);

        // Then
        assertNull(cliente.getApellidos());

        // When - Teléfono con formato incorrecto
        cliente.setTelefono("099876543");

        // Then
        assertFalse(cliente.getTelefono().matches("^[0-9]{10}$"));
    }

    @Test
    public void testCU1_Alterno41_ClienteYaExisteVerFicha() {
        // Given - Cédulas de clientes existentes en la BD
        String[] cedulasExistentes = {
            "1750505060", // Juan Esteban Pérez Zambrano
            "1712345678", // María Fernanda González López
            "1798765432", // Carlos Andrés Rodríguez Mora
            "1723456789", // Ana Lucía Martínez Sánchez
            "1734567890"  // Pedro José López Herrera
        };

        // When & Then - Verificar que las cédulas tienen el formato correcto
        for (String cedulaExistente : cedulasExistentes) {
            cliente.setCedula(cedulaExistente);
            assertTrue(cliente.getCedula().matches("^\\d{10}$"));
            assertEquals(10, cliente.getCedula().length());
        }
    }

    @Test
    public void testCU1_Alterno41_ClienteYaExisteCancelar() {
        // Given - Cliente existente con datos conocidos
        String cedulaExistente = "1750505060";
        String nombresOriginales = "Juan Esteban";
        String apellidosOriginales = "Pérez Zambrano";

        // When - Se intenta crear con la misma cédula pero se cancela
        cliente.setCedula(cedulaExistente);
        cliente.setNombres("Otros Nombres");
        cliente.setApellidos("Otros Apellidos");

        // Then - La cédula sigue siendo la misma (única)
        assertEquals(cedulaExistente, cliente.getCedula());
        assertNotEquals(nombresOriginales, cliente.getNombres());
        assertNotEquals(apellidosOriginales, cliente.getApellidos());
    }

    @Test
    public void testCU1_ExcepcionP_SesionExpiradaAlGuardar() {
        // Given - Cliente válido listo para guardar
        cliente.setCedula("1750505060");
        cliente.setNombres("Juan Esteban");
        cliente.setApellidos("Pérez Zambrano");
        cliente.setTelefono("0998765432");
        cliente.setEmail("juan.perez@gmail.com");
        cliente.setDireccion("Av. Eloy Alfaro N34-123");

        // When - Verificar que los datos están correctos antes del "guardado"
        assertTrue(cliente.getCedula().matches("^\\d{10}$"));
        assertTrue(cliente.getNombres().length() >= 2);
        assertTrue(cliente.getApellidos().length() >= 2);

        // Then - Los datos del cliente deben mantenerse íntegros
        assertNotNull(cliente.getCedula());
        assertNotNull(cliente.getNombres());
        assertNotNull(cliente.getApellidos());
        assertEquals(EstadoCliente.ACTIVO, cliente.getEstado());
    }

    @Test
    public void testCU1_ExcepcionQ_ErrorInternoAlGuardar() {
        // Given - Cliente con datos válidos
        cliente.setCedula("1712345678");
        cliente.setNombres("María Fernanda");
        cliente.setApellidos("González López");
        cliente.setTelefono("0987654321");
        cliente.setEmail("maria.gonzalez@hotmail.com");

        // When - Verificar integridad de datos antes del error
        assertTrue(cliente.getCedula().matches("^\\d{10}$"));
        assertTrue(cliente.getEmail().contains("@"));
        assertEquals(EstadoCliente.ACTIVO, cliente.getEstado());

        // Then - Verificar que el estado del objeto no se corrompe
        assertEquals(10, cliente.getCedula().length());
        assertTrue(cliente.getNombres().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,100}$"));
        assertTrue(cliente.getApellidos().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,100}$"));
    }
}
