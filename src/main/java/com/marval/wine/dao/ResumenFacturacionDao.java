package com.marval.wine.dao;

import java.util.List;

import com.marval.wine.entity.ResumenContenedor;
import com.marval.wine.entity.ResumenFacturacion;

public interface ResumenFacturacionDao {


	List<ResumenContenedor> resumenFacturacionContainer(ResumenFacturacion res, String nave, String viaje);

}
