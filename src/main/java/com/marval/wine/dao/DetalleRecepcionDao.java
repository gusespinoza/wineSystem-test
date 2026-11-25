package com.marval.wine.dao;

import java.util.List;

import com.marval.wine.entity.PurchaseOrderDetalle;

public interface DetalleRecepcionDao {

	void guardarRecepcion(PurchaseOrderDetalle recepcion);

	List<PurchaseOrderDetalle> listarDetalle(Integer poId);

	void eliminarDetalle(Integer detalleId);

}
