package com.marval.wine.dao;

import java.util.List;

import com.marval.wine.entity.Repaletizado;

public interface RepaletizadoDao {

	List<Repaletizado> buscarEtiquetasRepaletizado(String etiqueta);

	Repaletizado buscarEtiquetaRepaletizado(String etiquetaSecun);


}
