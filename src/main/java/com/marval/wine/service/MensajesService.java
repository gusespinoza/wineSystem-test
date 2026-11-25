package com.marval.wine.service;

import java.util.Date;
import java.util.List;

import com.marval.wine.entity.MensajeProcesado;

public interface MensajesService {

	List<MensajeProcesado> buscarMensajesProcesados(String tipoMensaje, Date fechaDesde, Date fechaHasta);

	
}
