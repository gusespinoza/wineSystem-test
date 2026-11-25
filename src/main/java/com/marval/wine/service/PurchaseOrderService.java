package com.marval.wine.service;

import java.util.List;

import com.marval.wine.entity.AjusteInventario;
import com.marval.wine.entity.Mensaje;
import com.marval.wine.entity.MensajeBodega;
import com.marval.wine.entity.PoAxis;
import com.marval.wine.entity.PurchaseOrder;

public interface PurchaseOrderService {

	void savePurchaseOrder(List<PurchaseOrder> purchaseOrders);

	List<PoAxis> listPo(String vina);

	void updatePo(PurchaseOrder po);

	List<String> autoCompletarPo(String po);

	List<String> autoCompletarLote(String lote);

	void guardarPo(PurchaseOrder po);

	List<PurchaseOrder> listPoManual();

	List<String> autoCompletarCodProd(String codigo);

	List<String> autoCompletarNombreProd(String prod);

	List<String> listPoAsignacion();

	boolean consultarArchivoProcesado(String nombreArchivo);

	void guardarMensaje(Mensaje mensaje);

	List<MensajeBodega> mensajeInventario();

	boolean validarInventoryRecon();

	PurchaseOrder buscarPo(Integer id);

	boolean validarInventoryAjust();

	List<AjusteInventario> listaAjustes();

	void nombreAjustes(String nombreArchivo);


	
}
