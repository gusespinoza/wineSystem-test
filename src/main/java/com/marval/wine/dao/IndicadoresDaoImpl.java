package com.marval.wine.dao;

import java.util.Date;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.marval.wine.entity.HistorialIndicadores;

@Repository
public class IndicadoresDaoImpl implements IndicadoresDao {
	
	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public HistorialIndicadores indicadorMonedaFecha(String string, Date date) {

		String query = "From HistorialIndicadores as i where i.moneda=:moneda AND i.fechaCr=convert(date ,:fecha)";
		
		return sessionFactory.getCurrentSession().createQuery(query, HistorialIndicadores.class)
				.setParameter("moneda", string).setParameter("fecha", date).uniqueResult();

	}

	
	@Override
	public void actualizaIndicador(HistorialIndicadores ind) {
		sessionFactory.getCurrentSession().saveOrUpdate(ind);
	}

	@Override
	public void guardaHistorialIndicador(HistorialIndicadores histGuardar) {
		sessionFactory.getCurrentSession().persist(histGuardar);
	}

}
