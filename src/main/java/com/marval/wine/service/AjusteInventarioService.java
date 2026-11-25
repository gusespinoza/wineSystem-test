package com.marval.wine.service;

import java.util.List;

import com.marval.wine.entity.AjusteInventario;
import com.marval.wine.entity.DetalleInventario;
import com.marval.wine.entity.PoAjuste;

public interface AjusteInventarioService {

	List<PoAjuste> buscarPos(String producto, String cosecha);

	List<String> consultarProductNumbers(String valor);

	DetalleInventario consultarDetalleInventario(String producto, String cosecha);

	void guardarAjusteInventario(AjusteInventario ajuste);

	String secuenciaSPCR();

	String secuenciaSPRC();

	String secuenciaSPSP();

	String secuenciaSPST();

	

}
