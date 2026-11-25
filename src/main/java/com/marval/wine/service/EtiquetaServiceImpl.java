package com.marval.wine.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marval.wine.dao.EtiquetaDao;
import com.marval.wine.entity.Etiqueta;

@Service
public class EtiquetaServiceImpl implements EtiquetaService {

	@Autowired
	private EtiquetaDao etiquetaDao;
	
	@Transactional
	@Override
	public void crearEtiqueta(Etiqueta etiqueta) {
		etiquetaDao.crearEtiqueta(etiqueta);
	}
	
	@Transactional
	@Override
	public String obtenerEtiqueta() {
		return etiquetaDao.obtenerEtiqueta();
	}
	
	
	@Transactional
	@Override	
	public void cancelarEtiqueta(Integer idDetalle) {
		etiquetaDao.cancelarEtiqueta(idDetalle);
	}
	@Transactional
	@Override	
	public void despacharEtiqueta(Integer idDetalle) {
		etiquetaDao.despacharEtiqueta(idDetalle);
	}
	@Transactional
	@Override	
	public void actualizarEtiquetaAsignacion(Integer id, String contenedor) {
		etiquetaDao.actualizarEtiquetaAsignacion(id, contenedor);
	}
	
	@Transactional
	@Override
	public List<Etiqueta> resultadoBusquedaEtiquetas(String lote, String po, String fert){
		return etiquetaDao.resultadoBusquedaEtiquetas(lote, po, fert);
	}

	@Transactional
	@Override
	public Etiqueta buscarEtiqueta(String etiqueta) {
		return etiquetaDao.buscarEtiqueta(etiqueta);
	}

	@Transactional
	@Override
	public void actualizarEtiquetaRepaletizada(Etiqueta prima) {
		etiquetaDao.actualizarEtiquetaRepaletizada(prima);
	}	
	
}
