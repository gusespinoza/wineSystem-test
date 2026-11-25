package com.marval.wine.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

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

public interface ContainerDao {

	void saveContainer(List<Container> containers);

	List<Container> listarContenedoresCargados();

	List<Ubicacion> buscarEtiquetas(String etiqueta);

	List<String> autoCompletaEtiquetaAlm(String etiqueta);

	List<String> autoCompletaEtiquetaRecep(String etiqueta);

	String validarPosicion(String pasillo, String altura, String posicion);

	void cancelarEtiqueta(ConsultarEtiqueta ubi);

	void actualizarPOAlmacenada(Ubicacion ubicacion);

	Ubicacion buscarEtiqueta(String etiqueta);

	List<String> consultarListaVinas();

	List<ConsultarEtiqueta> consultarEtiqueta(String etiqueta, String vina, String tipoDoc, String nroDoc);

	Container buscarContenedor(Integer id);

    int buscarAsignacionesContenedor(Integer id);

	List<Container> listarContenedoresDisponibles();

	Map<Integer, String> listaContenedoresTarjado();      
	
	void actualizarCantidadPODetalle(ConsultarEtiqueta etiqueta, String cantNueva);

	List<Ubicacion> buscarEtiquetasAlmacenadas(String etiqueta);

	List<String> consultarListaVinas(String valor);

	List<ResumenFacturacion> buscarResumenFacturacion(String nave, String viaje);

	List<String> autoCompletarNaves(String valor);

	List<String> autoCompletarViajesNave(String valor, String nave);

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
