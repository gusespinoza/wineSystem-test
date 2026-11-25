package com.marval.wine.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marval.wine.dao.UbicacionDao;
import com.marval.wine.entity.Ubicacion;

@Service
public class UbicacionServiceImpl implements UbicacionService {
	
	@Autowired
	private UbicacionDao ubicacionDao;

	
	@Transactional
	@Override
	public String guardarAsignacion(Ubicacion ubi) {

		/*String etiv = containerDao.validarPosicion(ubi.getPasillo(), ubi.getAltura(), ubi.getPosicion());

		if (etiv == null || etiv.equals("")) {
			containerDao.guardarAsignacion(ubi);
			return "";
		}

		return etiv;*/
		
		ubicacionDao.guardarAsignacion(ubi);
		return "";
	}
	
}
