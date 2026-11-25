package com.marval.wine.dao;

import java.util.Date;
import java.util.List;

import com.marval.wine.entity.MensajeProcesado;

public interface MensajesDao {

	List<MensajeProcesado> buscarMensajesProcesados(String tipoMensaje, Date fechaDesde, Date fechaHasta);

}
