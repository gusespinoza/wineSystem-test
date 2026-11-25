package com.marval.wine.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.marval.wine.entity.Ubicacion;

@Repository
public class UbicacionDaoImpl implements UbicacionDao {
	

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public void guardarAsignacion(Ubicacion ubi) {

		String query = "UPDATE etiqueta SET pasillo=:pasillo, altura=:altura, posicion=:posicion where id=:id";
		sessionFactory.getCurrentSession().createNativeQuery(query).setParameter("pasillo", ubi.getPasillo())
				.setParameter("altura", ubi.getAltura()).setParameter("posicion", ubi.getPosicion())
				.setParameter("id", ubi.getId()).executeUpdate();
	}

}
