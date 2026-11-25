package com.marval.wine.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;
import javax.transaction.Transactional;

import com.marval.wine.dao.ContainerDao;
import com.marval.wine.entity.AjusteInventario;
import com.marval.wine.entity.ConsultarEtiqueta;
import com.marval.wine.entity.Container;
import com.marval.wine.entity.DespachoContenedor;
import com.marval.wine.entity.Existencia;
import com.marval.wine.entity.MensajeProcesado;
import com.marval.wine.entity.PackingList;
import com.marval.wine.entity.PickingTicket;
import com.marval.wine.entity.Repaletizado;
import com.marval.wine.entity.ResumenFacturacion;
import com.marval.wine.entity.Ubicacion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContainerServiceImpl implements ContainerService {
	
	
	@Autowired
	private ContainerDao containerDao;

	@Transactional
	@Override
	public void saveContainer(List<Container> contenedores) {
		containerDao.saveContainer(contenedores);
	}

	@Transactional
	@Override
	public List<Container> listarContenedoresCargados() {
		return containerDao.listarContenedoresCargados();
	}

	@Transactional
	@Override
	public Map<Integer, String> listarContenedoresConsolidados() {
		return containerDao.listarContenedoresConsolidados();
	}

	@Transactional
	@Override
	public List<String> autoCompletaEtiquetaAlm(String etiqueta) {
		return containerDao.autoCompletaEtiquetaAlm(etiqueta);
	}

	@Transactional
	@Override
	public List<Ubicacion> buscarEtiquetas(String etiqueta) {
		return containerDao.buscarEtiquetas(etiqueta);
	}

	@Transactional
	@Override
	public List<String> autoCompletaEtiquetaRecep(String etiqueta) {
		return containerDao.autoCompletaEtiquetaRecep(etiqueta);
	}

	@Transactional
	@Override
	public void cancelarEtiqueta(ConsultarEtiqueta ubi) {
		containerDao.cancelarEtiqueta(ubi);
	}

	@Transactional
	@Override
	public void actualizarPOAlmacenada(Ubicacion ubicacion) {
		containerDao.actualizarPOAlmacenada(ubicacion);
	}

	@Transactional
	@Override
	public Ubicacion buscarEtiqueta(String etiqueta) {
		return containerDao.buscarEtiqueta(etiqueta);
	}

	@Transactional
	@Override
	public List<String> consultarListaVinas() {
		return containerDao.consultarListaVinas();
	}

	

	@Transactional
	@Override
	public List<ConsultarEtiqueta> consultarEtiqueta(String etiqueta, String vina, String tipoDoc, String nroDoc) {
		return containerDao.consultarEtiqueta(etiqueta, vina, tipoDoc, nroDoc);
	}

	@Transactional
	@Override
	public void regenerarEtiqueta(Repaletizado repaletizadoSel) {
		// containerDao.buscarEtiquetasRepaletizado(repaletizadoSel);
	}

	@Transactional
	@Override
	public Container buscarContenedor(Integer id) {
		return containerDao.buscarContenedor(id);
	}

	@Transactional
	@Override
	public int buscarAsignacionesContenedor(Integer id) {
		return containerDao.buscarAsignacionesContenedor(id);
	}

	@Transactional
	@Override
	public List<SelectItem> listarContenedoresDisponibles() {
		List<SelectItem> lista = new ArrayList<SelectItem>();
		for (Container cont : containerDao.listarContenedoresDisponibles()) {
			SelectItem select = new SelectItem();
			select.setValue(cont.getId());
			select.setLabel(cont.getContainer());
			lista.add(select);
		}
		return lista;
	}

	@Transactional
	@Override
	public void actualizarCantidadPODetalle(ConsultarEtiqueta etiqueta, String cantNueva) {
		containerDao.actualizarCantidadPODetalle(etiqueta, cantNueva);
	}

	@Transactional
	@Override
	public List<Ubicacion> buscarEtiquetasAlmacenadas(String etiqueta) {
		return containerDao.buscarEtiquetasAlmacenadas(etiqueta);
	}

	@Transactional
	@Override
	public List<String> consultarListaVinas(String valor) {
		return containerDao.consultarListaVinas(valor);
	}

	@Transactional
	@Override
	public List<ResumenFacturacion> buscarResumenFacturacion(String nave, String viaje) {
		return containerDao.buscarResumenFacturacion(nave, viaje);
	}

	@Transactional
	@Override
	public List<String> autoCompletarNaves(String valor) {
		return containerDao.autoCompletarNaves(valor);
	}

	@Transactional
	@Override
	public List<String> autoCompletarViajesNave(String valor, String nave) {
		return containerDao.autoCompletarViajesNave(valor, nave);
	}

	@Transactional
	@Override
	public void actualizarContainer(Container cont) {
		containerDao.actualizarContainer(cont);
	}

	@Transactional
	@Override
	public Map<Integer, String> listaContenedoresTarjado() {
		return containerDao.listaContenedoresTarjado();
	}

	@Transactional
	@Override
	public List<String> autoCompletaEtiquetaAlmRepale(String valor) {
		return containerDao.autoCompletaEtiquetaAlmRepale(valor);
	}

	@Transactional
	@Override
	public List<String> autoCompletaEtiquetaRecepRepale(String valor) {
		return containerDao.autoCompletaEtiquetaRecepRepale(valor);
	}

	@Transactional
	@Override
	public List<String> etiquetasRecepcionadas() {
		return containerDao.etiquetasRecepcionadas();
	}

	@Transactional
	@Override
	public List<DespachoContenedor> buscarDatosDespacho(Integer idContenedor) {
		return containerDao.buscarDatosDespacho(idContenedor);
	}

	@Transactional
	@Override
	public List<PickingTicket> buscarPt(String vinas, String productos) {
		return containerDao.buscarPt(vinas, productos);
	}

	@Transactional
	@Override
	public List<String> autoCompletaEtiquetaSecunRep(String valor, String prod) {
		return containerDao.autoCompletaEtiquetaSecunRep(valor, prod);
	}

	@Transactional
	@Override
	public List<String> consultarListaPOenAjustes(String valor) {
		return containerDao.consultarListaPOenAjustes(valor);
	}

	@Transactional
	@Override
	public List<String> consultarListaPOS(String valor) {
		return containerDao.consultarListaPOS(valor);
	}
	
	@Transactional
	@Override
	public List<String> autoCompletaContPacking(String contenedor){
		return containerDao.autoCompletaContPacking(contenedor);
	}

	@Transactional
	@Override
	public List<Container> listarContenedoresPacking(String contenedor){
		return containerDao.listarContenedoresPacking(contenedor);
	}
	
	@Transactional
	@Override
	public List<PackingList> listarPacking(Integer idCont){
		return containerDao.listarPacking(idCont);
	}

	@Transactional
	@Override
	public boolean validarExistenciaContenedor(String numeroContenedor) {
		return containerDao.validarExistenciaContenedor(numeroContenedor);
	}

}
