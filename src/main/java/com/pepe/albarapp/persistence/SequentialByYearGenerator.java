package com.pepe.albarapp.persistence;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import com.pepe.albarapp.persistence.domain.Invoice;
import com.pepe.albarapp.persistence.domain.DeliveryNote;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.Instant;

public class SequentialByYearGenerator implements IdentifierGenerator {

	@Override
	public Serializable generate(SharedSessionContractImplementor session, Object obj) throws HibernateException {

		String idColumnName = session.getEntityPersister(obj.getClass().getName(), obj).getIdentifierPropertyName();
		String tableName = obj.getClass().getSimpleName();
		LocalDate localDate;

		if (obj instanceof Invoice) {
			long issuedTimestamp = ((Invoice) obj).getIssuedTimestamp();
			localDate = Instant.ofEpochMilli(issuedTimestamp).atZone(ZoneId.systemDefault()).toLocalDate();
		} else {
			long issuedTimestamp = ((DeliveryNote) obj).getIssuedTimestamp();
			localDate = Instant.ofEpochMilli(issuedTimestamp).atZone(ZoneId.systemDefault()).toLocalDate();
		}
		//Get first Id of current year
		long firstId = localDate.getYear() * 100000L + 1;

		//Get first Id of next year
		long firstIdNextYear = (localDate.getYear() + 1) * 100000L + 1;

		// Get latest Id of current year
		String query = String.format("select max(%s) from %s where id < %s", idColumnName, tableName, Long.toString(firstIdNextYear));
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