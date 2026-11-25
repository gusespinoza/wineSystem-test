package com.marval.wine.service;

import java.util.List;

import com.marval.wine.entity.Etiqueta;

public interface EtiquetaService {

	void crearEtiqueta(Etiqueta etiqueta);

	String obtenerEtiqueta();

	void cancelarEtiqueta(Integer idDetalle);

	Etiqueta buscarEtiqueta(String etiqueta);

	void actualizarEtiquetaRepaletizada(Etiqueta prima);

	void actualizarEtiquetaAsignacion(Integer id, String contenedor);

	void despacharEtiqueta(Integer idDetalle);

	List<Etiqueta> resultadoBusquedaEtiquetas(String lote, String po, String fert);

}
