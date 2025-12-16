package com.checkengine.checkengine.modelo;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CalculadorTotalOrdenAvanzadoTest {

    @Test
    void calcula_correcto_caso_base_descuento_5_sin_recargos() {
        // subtotal = 120 + 30 = 150 => descuento 5% = 7.50
        // neto = 150 - 7.50 = 142.50
        // iva = 142.50 * 0.15 = 21.375 => 21.38
        // total = 163.88
        var r = CalculadorTotalOrdenAvanzado.calcular(
                bd("120.00"),
                bd("30.00"),
                false,
                false
        );

        assertBd("150.00", r.subtotal);
        assertBd("0.05", r.descuentoPct);
        assertBd("7.50", r.descuento);
        assertBd("0.00", r.recargo);
        assertBd("142.50", r.neto);
        assertBd("21.38", r.iva);
        assertBd("163.88", r.total);
    }

    @Test
    void descuento_2_por_ciento_cuando_subtotal_es_80_o_mas() {
        var r = CalculadorTotalOrdenAvanzado.calcular(
                bd("50.00"),
                bd("30.00"), // subtotal=80
                false,
                false
        );
        assertBd("80.00", r.subtotal);
        assertBd("0.02", r.descuentoPct);
        assertBd("1.60", r.descuento);
        assertBd("78.40", r.neto);
        assertBd("11.76", r.iva);
        assertBd("90.16", r.total);
    }

    @Test
    void descuento_10_por_ciento_cuando_subtotal_es_300_o_mas() {
        var r = CalculadorTotalOrdenAvanzado.calcular(
                bd("250.00"),
                bd("50.00"), // subtotal=300
                false,
                false
        );
        assertBd("300.00", r.subtotal);
        assertBd("0.10", r.descuentoPct);
        assertBd("30.00", r.descuento);
        assertBd("270.00", r.neto);
        assertBd("40.50", r.iva);
        assertBd("310.50", r.total);
    }

    @Test
    void aplica_recargo_tarifa_alta_8_por_ciento() {
        // subtotal 200 => descuento 5% => 10
        // recargo 8% de 200 => 16
        // neto = 200 - 10 + 16 = 206
        // iva = 30.90
        // total = 236.90
        var r = CalculadorTotalOrdenAvanzado.calcular(
                bd("200.00"),
                bd("0.00"),
                true,
                false
        );

        assertBd("200.00", r.subtotal);
        assertBd("0.05", r.descuentoPct);
        assertBd("10.00", r.descuento);
        assertBd("16.00", r.recargo);
        assertBd("206.00", r.neto);
        assertBd("30.90", r.iva);
        assertBd("236.90", r.total);
    }

    @Test
    void aplica_recargo_cantidad_alta_3_por_ciento() {
        // subtotal 200 => descuento 5% => 10
        // recargo 3% de 200 => 6
        // neto = 196
        // iva = 29.40
        // total = 225.40
        var r = CalculadorTotalOrdenAvanzado.calcular(
                bd("200.00"),
                bd("0.00"),
                false,
                true
        );

        assertBd("200.00", r.subtotal);
        assertBd("0.05", r.descuentoPct);
        assertBd("10.00", r.descuento);
        assertBd("6.00", r.recargo);
        assertBd("196.00", r.neto);
        assertBd("29.40", r.iva);
        assertBd("225.40", r.total);
    }

    @Test
    void aplica_recargo_doble_8_mas_3_por_ciento() {
        // recargo = 11% del subtotal
        var r = CalculadorTotalOrdenAvanzado.calcular(
                bd("100.00"),
                bd("50.00"), // subtotal=150 => desc 5% = 7.50
                true,
                true          // recargo 11% de 150 = 16.50
        );

        assertBd("150.00", r.subtotal);
        assertBd("0.05", r.descuentoPct);
        assertBd("7.50", r.descuento);
        assertBd("16.50", r.recargo);

        // neto = 150 - 7.50 + 16.50 = 159.00
        assertBd("159.00", r.neto);

        // iva = 159 * 0.15 = 23.85
        assertBd("23.85", r.iva);

        // total = 182.85
        assertBd("182.85", r.total);
    }

    @Test
    void soporta_nulls_sin_reventar() {
        var r = CalculadorTotalOrdenAvanzado.calcular(
                null,
                null,
                false,
                false
        );
        assertBd("0.00", r.subtotal);
        assertBd("0.00", r.descuentoPct);
        assertBd("0.00", r.descuento);
        assertBd("0.00", r.recargo);
        assertBd("0.00", r.neto);
        assertBd("0.00", r.iva);
        assertBd("0.00", r.total);
    }

    private static BigDecimal bd(String v) {
        return new BigDecimal(v);
    }

    private static void assertBd(String expected, BigDecimal actual) {
        assertEquals(new BigDecimal(expected), actual, "Mismatch BigDecimal value");
    }
}

