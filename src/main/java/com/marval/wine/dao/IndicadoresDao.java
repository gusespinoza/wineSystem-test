package com.marval.wine.dao;

import java.util.Date;

import com.marval.wine.entity.HistorialIndicadores;

public interface IndicadoresDao {

	HistorialIndicadores indicadorMonedaFecha(String string, Date date);

	void guardaHistorialIndicador(HistorialIndicadores histGuardar);

	void actualizaIndicador(HistorialIndicadores ind);
	
	

}
