package com.marval.wine.dao;

import java.math.BigInteger;
import java.util.List;

import com.marval.wine.entity.AjusteInventario;
import com.marval.wine.entity.PoAjuste;

public interface AjusteInventarioDao {

	List<PoAjuste> buscarPos(String vinaSelec, String cosecha);

	List<String> consultarProductNumbers(String valor);

	void guardarAjusteInventario(AjusteInventario ajuste);

	Integer consultarCantidadAjustesEtiqueta(String producto, String cosecha);

	BigInteger consultarUltimaCantidadIngresada(String producto, String cosecha);

	String secuenciaSPCR();

	String secuenciaSPRC();

	String secuenciaSPSP();

	String secuenciaSPST();

    
}
