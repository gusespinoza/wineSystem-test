package com.marval.wine.dao;

import java.util.List;

import com.marval.wine.entity.ResumenContenedor;
import com.marval.wine.entity.ResumenFacturacion;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ResumenFacturacionDaoImpl implements ResumenFacturacionDao {
	

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public List<ResumenContenedor> resumenFacturacionContainer(ResumenFacturacion res, String nave, String viaje) {
		
		String query = "exec dbo.resumen_facturacion_contenedor :cont , :nave, :viaje ;";
		return (List<ResumenContenedor>) sessionFactory.getCurrentSession().createNativeQuery(query, ResumenContenedor.class)
				.setParameter("cont", res.getId())
				.setParameter("nave", nave)
				.setParameter("viaje", viaje).list();
		}

}
