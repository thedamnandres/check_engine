package com.checkengine.checkengine.modelo;

import java.math.*;

public class CalculadorTotalOrdenAvanzado {

    public static final BigDecimal IVA = new BigDecimal("0.15");

    public static ResumenCalculo calcular(
            BigDecimal subtotalTrabajos,
            BigDecimal subtotalRepuestos,
            boolean existeTarifaAlta,
            boolean existeRepuestoCantidadAlta
    ) {
        BigDecimal subtotal = n(subtotalTrabajos).add(n(subtotalRepuestos));

        BigDecimal descuentoPct = descuentoPorMonto(subtotal);
        BigDecimal descuento = subtotal.multiply(descuentoPct);

        BigDecimal recargo = BigDecimal.ZERO;
        if (existeTarifaAlta) recargo = recargo.add(subtotal.multiply(new BigDecimal("0.08")));
        if (existeRepuestoCantidadAlta) recargo = recargo.add(subtotal.multiply(new BigDecimal("0.03")));

        BigDecimal neto = subtotal.subtract(descuento).add(recargo);
        BigDecimal iva = neto.multiply(IVA);
        BigDecimal total = neto.add(iva);

        return new ResumenCalculo(r2(subtotal), r2(descuentoPct), r2(descuento), r2(recargo), r2(neto), r2(iva), r2(total));
    }

    static BigDecimal descuentoPorMonto(BigDecimal subtotal) {
        if (subtotal.compareTo(new BigDecimal("300")) >= 0) return new BigDecimal("0.10");
        if (subtotal.compareTo(new BigDecimal("150")) >= 0) return new BigDecimal("0.05");
        if (subtotal.compareTo(new BigDecimal("80")) >= 0)  return new BigDecimal("0.02");
        return BigDecimal.ZERO;
    }

    private static BigDecimal n(BigDecimal v) { return v == null ? BigDecimal.ZERO : v; }

    private static BigDecimal r2(BigDecimal v) { return n(v).setScale(2, RoundingMode.HALF_UP); }

    public static class ResumenCalculo {
        public final BigDecimal subtotal;
        public final BigDecimal descuentoPct;
        public final BigDecimal descuento;
        public final BigDecimal recargo;
        public final BigDecimal neto;
        public final BigDecimal iva;
        public final BigDecimal total;

        public ResumenCalculo(
                BigDecimal subtotal,
                BigDecimal descuentoPct,
                BigDecimal descuento,
                BigDecimal recargo,
                BigDecimal neto,
                BigDecimal iva,
                BigDecimal total
        ) {
            this.subtotal = subtotal;
            this.descuentoPct = descuentoPct;
            this.descuento = descuento;
            this.recargo = recargo;
            this.neto = neto;
            this.iva = iva;
            this.total = total;
        }
    }

}