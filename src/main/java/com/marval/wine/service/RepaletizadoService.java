package com.marval.wine.service;

import java.util.List;

import com.marval.wine.entity.Repaletizado;

public interface RepaletizadoService {

	List<Repaletizado> buscarEtiquetasRepaletizado(String etiqueta);

	Repaletizado buscarEtiquetaRepaletizado(String etiquetaSecun);


}
