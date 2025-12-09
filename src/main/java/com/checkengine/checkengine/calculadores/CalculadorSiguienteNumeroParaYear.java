package com.checkengine.checkengine.calculadores;

import org.openxava.calculators.ICalculator;
import org.openxava.jpa.XPersistence;

import javax.persistence.Query;

public class CalculadorSiguienteNumeroParaYear implements ICalculator {

    private int year;

    @Override
    public Object calculate() throws Exception {
        try {
            if (year == 0) {
                return 1;
            }

            Query query = XPersistence.getManager()
                .createQuery("SELECT MAX(o.numero) FROM " +
                    getEntityName() + " o WHERE o.year = :year");
            query.setParameter("year", year);

            Integer lastNumber = (Integer) query.getSingleResult();
            return lastNumber == null ? 1 : lastNumber + 1;
        } catch (Exception e) {
            return 1;
        }
    }

    protected String getEntityName() {
        return "OrdenServicio";
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
