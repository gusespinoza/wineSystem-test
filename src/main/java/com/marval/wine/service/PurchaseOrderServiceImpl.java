package com.marval.wine.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marval.wine.dao.PurchaseOrderDao;
import com.marval.wine.entity.AjusteInventario;
import com.marval.wine.entity.Mensaje;
import com.marval.wine.entity.MensajeBodega;
import com.marval.wine.entity.PoAxis;
import com.marval.wine.entity.PurchaseOrder;
import com.marval.wine.entity.PurchaseOrderDetalle;
import com.marval.wine.util.Constantes;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService{
	
	@Autowired
	private PurchaseOrderDao purchaseOrderDao;

	@Transactional
	@Override
	public void savePurchaseOrder(List<PurchaseOrder> purchaseOrders) {
		purchaseOrderDao.savePurchaseOrder(purchaseOrders);
	}
	@Transactional
	@Override
	public List<PoAxis>  listPo(String vina) {
		List<PoAxis> listaPo = purchaseOrderDao.listPo(vina);
		return listaPo;
	}
	
	@Transactional
	@Override
	public List<PurchaseOrder> listPoManual(){
		return purchaseOrderDao.listPoManual();
	}
	
	@Transactional
	@Override
	public void guardarPo(PurchaseOrder po) {
		purchaseOrderDao.guardarPo(po);
	}
	
	@Transactional
	@Override
	public void updatePo(PurchaseOrder po) {
		purchaseOrderDao.updatePo(po);
	}
	
	@Transactional
	@Override
	public List<String>  autoCompletarPo(String po) {
		return purchaseOrderDao.autoCompletarPo(po);
	}
	@Transactional
	@Override
	public List<String> autoCompletarLote(String lote) {
		return purchaseOrderDao.autoCompletarLote(lote);
	}
	
	@Transactional
	@Override
	public List<String> autoCompletarCodProd(String codigo){
		return purchaseOrderDao.autoCompletarCodProd(codigo);
	}

	@Transactional
	@Override
	public List<String> autoCompletarNombreProd(String prod){
		return purchaseOrderDao.autoCompletarNombreProd(prod);
	}
	
	@Transactional
	@Override
	public List<String> listPoAsignacion(){
		return purchaseOrderDao.listPoAsignacion();
	}
	
	@Transactional
	@Override
	public boolean consultarArchivoProcesado(String nombreArchivo) {
		return purchaseOrderDao.consultarArchivoProcesado(nombreArchivo);
	}
	
	@Transactional
	@Override
	public void guardarMensaje(Mensaje mensaje) {
		purchaseOrderDao.guardarMensaje(mensaje);
	}
	
	@Transactional
	@Override
	public List<MensajeBodega> mensajeInventario(){
		return purchaseOrderDao.mensajeInventario();
	}
	
	@Transactional
	@Override
	public boolean validarInventoryRecon() {
		return purchaseOrderDao.validarInventoryRecon();
	}
	@Transactional
	@Override
	public boolean validarInventoryAjust() {
		return purchaseOrderDao.validarInventoryAjust();
	}
	
	@Transactional
	@Override
	public PurchaseOrder buscarPo(Integer id) {
		return purchaseOrderDao.buscarPo(id);
	}
	
	@Transactional
	@Override
	public List<AjusteInventario> listaAjustes(){
		return purchaseOrderDao.listaAjustes();
	}
	
	@Transactional
	@Override
	public void nombreAjustes(String nombreArchivo) {
		purchaseOrderDao.nombreAjustes(nombreArchivo);
	}
	
}
