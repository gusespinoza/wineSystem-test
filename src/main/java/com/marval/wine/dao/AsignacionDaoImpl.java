package com.marval.wine.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.marval.wine.entity.Asignacion;
import com.marval.wine.entity.Etiqueta;

@Repository
public class AsignacionDaoImpl implements AsignacionDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public void guardarAsignacion(Asignacion asign) throws HibernateException {
		sessionFactory.getCurrentSession().persist(asign);
	}

	@Override
	public void eliminarAsignacion(int idDetalle) {
		try {
			String hql = "delete from asignacion where guia_despacho_detalle_id =:idDetalle";
			sessionFactory.getCurrentSession().createNativeQuery(hql).setParameter("idDetalle", idDetalle)
					.executeUpdate();
		} catch (HibernateException e) {
			System.out.println(e);
		}
	}

	@Override
	public void actualizarAsignacion(Asignacion asg, int posicion) {
		if (asg.getEtiquetas().isEmpty()) {
			sessionFactory.getCurrentSession().update(asg);
		} else {
			String query = "";
			
			for (Etiqueta et : asg.getEtiquetas()) {
				Integer id = et.getId();
				if(posicion == 0) {
					query = "update asignacion set posicion = null where etiqueta_id=:id";
					sessionFactory.getCurrentSession().createNativeQuery(query).setParameter("id", id).executeUpdate();
				}else {
					query = "update asignacion set posicion =:posicion where etiqueta_id=:id";
					sessionFactory.getCurrentSession().createNativeQuery(query).setParameter("posicion", posicion).setParameter("id", id).executeUpdate();
				}
			}
		}

	}

	@Override
	public Asignacion recuperarAsignaciones(Integer idEtiqueta) {
		System.out.println("ID ETIQUETA: " + idEtiqueta);
		String query = "select  *  from asignacion where etiqueta_id =:idEtiqueta";
		return sessionFactory.getCurrentSession().createNativeQuery(query, Asignacion.class).setParameter("idEtiqueta", idEtiqueta)
				.getSingleResult();
	}

}
