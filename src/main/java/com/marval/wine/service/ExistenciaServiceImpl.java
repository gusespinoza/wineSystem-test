package com.marval.wine.service;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marval.wine.dao.ExistenciaDao;
import com.marval.wine.entity.Existencia;

@Service
public class ExistenciaServiceImpl implements ExistenciaService {
	
	@Autowired
	private ExistenciaDao existenciaDao;

	
	@Transactional
	@Override
	public List<Existencia> consultarCargasPorVina(String vinaSelec, String orden, Date fechaDesde, Date fechaHasta, String fert, String po) {
		return existenciaDao.consultarCargasPorVina(vinaSelec, orden, fechaDesde, fechaHasta, fert, po);
	}

	
}
