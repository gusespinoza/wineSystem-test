package com.marval.wine.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marval.wine.dao.RepaletizadoDao;
import com.marval.wine.entity.Repaletizado;

@Service
public class RepaletizadoServiceImpl implements RepaletizadoService {
	
	@Autowired
	RepaletizadoDao repaletizadoDao;
	

	@Transactional
	@Override
	public List<Repaletizado> buscarEtiquetasRepaletizado(String etiqueta) {
		return repaletizadoDao.buscarEtiquetasRepaletizado(etiqueta);
	}

	@Transactional
	@Override
	public Repaletizado buscarEtiquetaRepaletizado(String etiquetaSecun) {
		return repaletizadoDao.buscarEtiquetaRepaletizado(etiquetaSecun);
	}
}
