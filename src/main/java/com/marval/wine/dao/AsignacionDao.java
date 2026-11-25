package com.marval.wine.dao;

import org.hibernate.HibernateException;

import com.marval.wine.entity.Asignacion;

public interface AsignacionDao {

	void guardarAsignacion(Asignacion asign) throws HibernateException;

	void eliminarAsignacion(int idDetalle);

	Asignacion recuperarAsignaciones(Integer id);

	void actualizarAsignacion(Asignacion asg, int posicion);

}
