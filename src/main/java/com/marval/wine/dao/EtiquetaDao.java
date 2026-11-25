package com.marval.wine.dao;

import java.util.List;

import com.marval.wine.entity.Etiqueta;

public interface EtiquetaDao {

	String obtenerEtiqueta();

	void cancelarEtiqueta(Integer idDetalle);

	void crearEtiqueta(Etiqueta etiqueta);

	
	Etiqueta buscarEtiqueta(String etiqueta);

	void actualizarEtiquetaRepaletizada(Etiqueta prima);

	void actualizarEtiquetaAsignacion(Integer id, String contendor);

	void despacharEtiqueta(Integer idDetalle);

	List<Etiqueta> resultadoBusquedaEtiquetas(String lote, String po, String fert);

}
