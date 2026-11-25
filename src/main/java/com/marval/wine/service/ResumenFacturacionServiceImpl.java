package com.marval.wine.service;

import java.util.List;

import javax.transaction.Transactional;

import com.marval.wine.dao.ResumenFacturacionDao;
import com.marval.wine.entity.ResumenContenedor;
import com.marval.wine.entity.ResumenFacturacion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResumenFacturacionServiceImpl implements ResumenFacturacionService {
	
	@Autowired
	private ResumenFacturacionDao resumenFacturacionDao;

	
	@Transactional
	@Override
	public List<ResumenContenedor> resumenFacturacionContainer(ResumenFacturacion res, String nave, String viaje) {
		return resumenFacturacionDao.resumenFacturacionContainer(res, nave, viaje);
	}
	
}
