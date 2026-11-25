package com.marval.wine.service;

import java.io.IOException;
import java.util.Date;

import com.marval.wine.entity.HistorialIndicadores;
import com.marval.wine.entity.IndicadoresFinancieros;
import com.marval.wine.entity.IndicadoresFinancierosUF;

public interface IndicadoresService {

	HistorialIndicadores indicadorMonedaFecha(String string, Date date);

	IndicadoresFinancieros indicadorEconomicoSBIF(String tipo) throws IOException;

	void guardaHistorialIndicador(HistorialIndicadores histGuardar);

	void actualizaIndicador(HistorialIndicadores historial);

	IndicadoresFinancierosUF indicadorEconomicoUF(String string);

}
