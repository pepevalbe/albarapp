package com.pepe.albarapp.persistance;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.time.LocalDate;

public class SequentialByYearIdGenerator implements IdentifierGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object obj) throws HibernateException {

        String idColumnName = session.getEntityPersister(obj.getClass().getName(), obj).getIdentifierPropertyName();
        String tableName = obj.getClass().getSimpleName();
        String query = String.format("select max(%s) from %s", idColumnName, tableName);

        //Get first Id of current year
        long firstId = LocalDate.now().getYear() * 100000L;

        // Get latest Id
        Long max = (Long) session.createQuery(query).getSingleResult();

        // Empty table
        if (max == null) {
            return firstId;
        }

        // Begin new year
        if (firstId > max) {
            return firstId;
        }

        return max + 1;
    }
}