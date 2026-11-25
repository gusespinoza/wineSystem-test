package com.marval.wine.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marval.wine.dao.DetalleRecepcionDao;
import com.marval.wine.entity.PurchaseOrderDetalle;

@Service
public class DetalleRecepcionServiceImpl implements DetalleRecepcionService{

	@Autowired
	private DetalleRecepcionDao detalleDao;
	
	@Transactional
	@Override
	public void guardarRecepcion(PurchaseOrderDetalle recepcion) {
		detalleDao.guardarRecepcion(recepcion);
	}

	@Transactional
	@Override
	public List<PurchaseOrderDetalle> listarDetalle(Integer poId){
		return detalleDao.listarDetalle(poId);
	}

	@Transactional
	@Override
	public void eliminarDetalle(Integer detalleId) {
		detalleDao.eliminarDetalle(detalleId);
	}
	
	
}
