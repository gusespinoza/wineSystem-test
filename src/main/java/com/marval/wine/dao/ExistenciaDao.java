package com.marval.wine.dao;

import java.util.Date;
import java.util.List;

import com.marval.wine.entity.Existencia;

public interface ExistenciaDao {

	List<Existencia> consultarCargasPorVina(String vinaSelec, String orden, Date fechaDesde, Date fechaHasta,
			String fert, String po);

}
