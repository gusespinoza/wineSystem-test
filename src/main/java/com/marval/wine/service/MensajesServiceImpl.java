package com.marval.wine.service;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marval.wine.dao.MensajesDao;
import com.marval.wine.entity.MensajeProcesado;
import com.marval.wine.entity.Ubicacion;

@Service
public class MensajesServiceImpl implements MensajesService {
	
	@Autowired
	private MensajesDao mensajesDao;

	
	@Transactional
	@Override
	public List<MensajeProcesado> buscarMensajesProcesados(String tipoMensaje, Date fechaDesde, Date fechaHasta) {
		return mensajesDao.buscarMensajesProcesados(tipoMensaje, fechaDesde, fechaHasta);
	}

}
