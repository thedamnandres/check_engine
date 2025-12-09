package com.checkengine.checkengine.calculadores;

public class CalculadorSiguienteNumeroFactura extends CalculadorSiguienteNumeroParaYear {

    @Override
    protected String getEntityName() {
        return "FacturaInterna";
    }
}
