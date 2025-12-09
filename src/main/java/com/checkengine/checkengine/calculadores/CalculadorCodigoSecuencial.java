package com.checkengine.checkengine.calculadores;

import org.openxava.calculators.ICalculator;
import org.openxava.jpa.XPersistence;

import javax.persistence.Query;

public class CalculadorCodigoSecuencial implements ICalculator {

    private String entidad;
    private String prefijo;

    @Override
    public Object calculate() throws Exception {
        String queryStr = "SELECT MAX(CAST(SUBSTRING(o.codigo, " +
            (prefijo.length() + 1) + ") AS int)) FROM " + entidad + " o WHERE o.codigo LIKE :prefijo";

        Query query = XPersistence.getManager().createQuery(queryStr);
        query.setParameter("prefijo", prefijo + "%");

        Integer lastNumber = (Integer) query.getSingleResult();
        int nextNumber = lastNumber == null ? 1 : lastNumber + 1;

        return prefijo + String.format("%05d", nextNumber);
    }

    public String getEntidad() {
        return entidad;
    }

    public void setEntidad(String entidad) {
        this.entidad = entidad;
    }

    public String getPrefijo() {
        return prefijo;
    }

    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo;
    }
}
