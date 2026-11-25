package com.marval.wine.service;

import java.util.List;

import com.marval.wine.entity.PurchaseOrderDetalle;

public interface DetalleRecepcionService {

	void guardarRecepcion(PurchaseOrderDetalle recepcion);

	List<PurchaseOrderDetalle> listarDetalle(Integer poId);

	void eliminarDetalle(Integer detalleId);

}
