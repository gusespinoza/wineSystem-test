package com.marval.wine.service;

import org.hibernate.HibernateException;

import com.marval.wine.entity.Asignacion;

public interface AsignacionService {

	void guardarAsignacion(Asignacion asign) throws HibernateException;

	void eliminarAsignacion(int idDetalle);

	Asignacion recuperarAsignaciones(Integer id);

	void actualizarAsignacion(Asignacion asg, int posicion);

}
