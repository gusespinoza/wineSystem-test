package com.marval.wine.service;

import javax.transaction.Transactional;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marval.wine.dao.AsignacionDao;
import com.marval.wine.entity.Asignacion;

@Service
public class AsignacionServiceImpl  implements AsignacionService{

	
	@Autowired
	private AsignacionDao asignacionDao;
	
	
	@Transactional
	@Override
	public void guardarAsignacion(Asignacion asign) throws HibernateException{
		asignacionDao.guardarAsignacion(asign);
	}

	@Transactional
	@Override
	public void eliminarAsignacion(int idDetalle) {
		asignacionDao.eliminarAsignacion(idDetalle);
	}

	@Transactional
	@Override
	public void actualizarAsignacion(Asignacion asg, int posicion) {
		asignacionDao.actualizarAsignacion(asg, posicion);
	}
	
	
	@Transactional
	@Override
	public Asignacion recuperarAsignaciones(Integer id) {
		return asignacionDao.recuperarAsignaciones(id);
	}
}
