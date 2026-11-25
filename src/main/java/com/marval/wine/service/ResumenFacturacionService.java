package com.marval.wine.service;

import java.util.List;

import com.marval.wine.entity.ResumenContenedor;
import com.marval.wine.entity.ResumenFacturacion;

public interface ResumenFacturacionService {

	
	List<ResumenContenedor> resumenFacturacionContainer(ResumenFacturacion res, String nave, String viaje);
	
}
