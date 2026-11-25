package com.marval.wine.service;

import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import com.marval.wine.entity.ConsultarEtiqueta;
import com.marval.wine.entity.Container;
import com.marval.wine.entity.DespachoContenedor;
import com.marval.wine.entity.PackingList;
import com.marval.wine.entity.PickingTicket;
import com.marval.wine.entity.Repaletizado;
import com.marval.wine.entity.ResumenFacturacion;
import com.marval.wine.entity.Ubicacion;

public interface ContainerService {

	void saveContainer(List<Container> contenedores);

	List<Container> listarContenedoresCargados();

	List<String> autoCompletaEtiquetaAlm(String etiqueta);

	List<Ubicacion> buscarEtiquetas(String etiqueta);

	List<String> autoCompletaEtiquetaRecep(String etiqueta);	

	void cancelarEtiqueta(ConsultarEtiqueta ubi);

	void actualizarPOAlmacenada(Ubicacion ubicacion);

	Ubicacion buscarEtiqueta(String etiqueta);

	List<String> consultarListaVinas(String valor);

	List<ConsultarEtiqueta> consultarEtiqueta(String etiqueta, String vina, String tipoDoc, String numeroDoc);

	void regenerarEtiqueta(Repaletizado repaletizadoSel);

	List<SelectItem> listarContenedoresDisponibles();

	Container buscarContenedor(Integer id);

	int buscarAsignacionesContenedor(Integer id);

	Map<Integer, String> listaContenedoresTarjado();

	void actualizarCantidadPODetalle(ConsultarEtiqueta etiqueta, String cantNew);

	List<Ubicacion> buscarEtiquetasAlmacenadas(String etiqueta);

	List<String> consultarListaVinas();

	List<ResumenFacturacion> buscarResumenFacturacion(String etiqueta, String viajeSelec);

	List<String> autoCompletarNaves(String valor);

	List<String> autoCompletarViajesNave(String valor, String naveSelec);

	void actualizarContainer(Container cont);

	List<String> autoCompletaEtiquetaAlmRepale(String valor);

	List<String> autoCompletaEtiquetaRecepRepale(String valor);

	List<String> etiquetasRecepcionadas();

	Map<Integer, String> listarContenedoresConsolidados();

	List<DespachoContenedor> buscarDatosDespacho(Integer idContenedor);

	List<PickingTicket> buscarPt(String vinas, String productos);

	List<String> autoCompletaEtiquetaSecunRep(String valor, String prod);

	List<String> consultarListaPOenAjustes(String valor);

	List<String> consultarListaPOS(String valor);

	List<String> autoCompletaContPacking(String contenedor);

	List<Container> listarContenedoresPacking(String contenedor);

	List<PackingList> listarPacking(Integer idCont);

	boolean validarExistenciaContenedor(String numeroContenedor);
}
