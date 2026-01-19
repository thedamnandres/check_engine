package com.checkengine.checkengine.modelo;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Conversor personalizado para el enum EstadoCliente.
 * Maneja valores inválidos o vacíos convirtiéndolos automáticamente a ACTIVO.
 */
@Converter(autoApply = false)
public class EstadoClienteConverter implements AttributeConverter<EstadoCliente, String> {

    @Override
    public String convertToDatabaseColumn(EstadoCliente estado) {
        if (estado == null) {
            return EstadoCliente.ACTIVO.name();
        }
        return estado.name();
    }

    @Override
    public EstadoCliente convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return EstadoCliente.ACTIVO;
        }

        try {
            // Convertir a mayúsculas para manejar casos como "Activo", "activo", "ACTIVO"
            String normalized = dbData.trim().toUpperCase();
            return EstadoCliente.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            // Si el valor en BD no es válido, devolver ACTIVO por defecto
            return EstadoCliente.ACTIVO;
        }
    }
}

