package com.checkengine.checkengine.modelo;

import org.openxava.validators.*;
import org.openxava.util.*;

/**
 * Validador para cédulas ecuatorianas.
 * Valida que la cédula tenga 10 dígitos y cumpla con el algoritmo del dígito verificador.
 */
public class CedulaEcuatorianaValidator implements IPropertyValidator {

    @Override
    public void validate(Messages errors, Object value, String propertyName, String modelName) throws Exception {
        if (value == null || value.toString().trim().isEmpty()) {
            return; // Si está vacío, lo maneja @Required
        }

        String cedula = value.toString().trim();

        // Verificar que tenga exactamente 10 dígitos
        if (!cedula.matches("^\\d{10}$")) {
            errors.add("cedula_invalida", propertyName);
            return;
        }

        // Validar que los dos primeros dígitos correspondan a una provincia válida (01-24)
        int provincia = Integer.parseInt(cedula.substring(0, 2));
        if (provincia < 1 || provincia > 24) {
            errors.add("cedula_provincia_invalida", propertyName);
            return;
        }

        // Validar el tercer dígito (debe ser menor a 6 para cédulas de personas naturales)
        int tercerDigito = Integer.parseInt(cedula.substring(2, 3));
        if (tercerDigito >= 6) {
            errors.add("cedula_tercer_digito_invalido", propertyName);
            return;
        }

        // Validar el dígito verificador usando el algoritmo módulo 10
        int[] coeficientes = {2, 1, 2, 1, 2, 1, 2, 1, 2};
        int suma = 0;

        for (int i = 0; i < 9; i++) {
            int digito = Integer.parseInt(cedula.substring(i, i + 1));
            int producto = digito * coeficientes[i];

            // Si el producto es mayor a 9, se suman sus dígitos
            if (producto >= 10) {
                producto = producto - 9;
            }

            suma += producto;
        }

        int digitoVerificador = Integer.parseInt(cedula.substring(9, 10));
        int residuo = suma % 10;
        int resultado = residuo == 0 ? 0 : 10 - residuo;

        if (resultado != digitoVerificador) {
            errors.add("cedula_digito_verificador_invalido", propertyName);
        }
    }
}

