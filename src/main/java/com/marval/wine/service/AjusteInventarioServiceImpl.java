package com.marval.wine.service;

import java.util.List;

import javax.transaction.Transactional;

import com.marval.wine.dao.AjusteInventarioDao;
import com.marval.wine.entity.AjusteInventario;
import com.marval.wine.entity.DetalleInventario;
import com.marval.wine.entity.PoAjuste;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AjusteInventarioServiceImpl  implements AjusteInventarioService{
	
	
	@Autowired
	private AjusteInventarioDao ajusteDao;

	@Transactional
	@Override
	public List<PoAjuste> buscarPos(String producto, String cosecha) {
		return ajusteDao.buscarPos(producto, cosecha);
	}

	@Transactional
	@Override
	public List<String> consultarProductNumbers(String valor) {
		return ajusteDao.consultarProductNumbers(valor);
	}
	
	@Transactional
	@Override
	public DetalleInventario consultarDetalleInventario(String producto, String cosecha) {

		DetalleInventario det = new DetalleInventario();

		det.setCantidadAjustes(this.ajusteDao.consultarCantidadAjustesEtiqueta(producto, cosecha));
		det.setNuevaCantidad(this.ajusteDao.consultarUltimaCantidadIngresada(producto, cosecha));

		return det;
	}

	@Transactional
	@Override
	public void guardarAjusteInventario(AjusteInventario ajuste) {
		ajusteDao.guardarAjusteInventario(ajuste);
	}

	@Transactional
	@Override
	public String secuenciaSPCR() {
		return ajusteDao.secuenciaSPCR();
	}
	@Transactional
	@Override
	public String secuenciaSPRC() {
		return ajusteDao.secuenciaSPRC();
	}
	@Transactional
	@Override
	public String secuenciaSPSP() {
		return ajusteDao.secuenciaSPSP();
	}
	@Transactional
	@Override
	public String secuenciaSPST() {
		return ajusteDao.secuenciaSPST();
	}
	
}
