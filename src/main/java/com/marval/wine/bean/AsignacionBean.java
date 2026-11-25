package com.marval.wine.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;

import org.primefaces.PrimeFaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.marval.wine.entity.Asignacion;
import com.marval.wine.entity.Container;
import com.marval.wine.entity.Etiqueta;
import com.marval.wine.service.AsignacionService;
import com.marval.wine.service.ContainerService;
import com.marval.wine.service.EtiquetaService;
import com.marval.wine.service.PurchaseOrderService;
import com.marval.wine.util.Util;

@ManagedBean
@SessionScope
@Component
public class AsignacionBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(AsignacionBean.class);

	@Autowired
	private LoginBean loginBean;

	@Autowired
	AsignacionService asigService;

	@Autowired
	ContainerService contenedorService;

	@Autowired
	PurchaseOrderService poService;

	@Autowired
	EtiquetaService etiquetaService;

	// Lisa que almacena los numeros de guas de despacho disponibles.

	private List<SelectItem> contenedores = new ArrayList<SelectItem>();
	private List<Asignacion> asignaciones = new ArrayList<Asignacion>();
	// Lista de detalles de guias que se asignaran

	// Lista de numero contenedor junto a su ID
	private List<String> numContenedorId = new ArrayList<String>();
	private List<String> listaPo = new ArrayList<String>();

	// Listas resultado de la busqueda
	private List<Etiqueta> busquedaEtiqueta = new ArrayList<Etiqueta>();
	private List<Container> listaContenedores = new ArrayList<Container>();
	private List<Etiqueta> etiquetasSelect = new ArrayList<Etiqueta>();

	// Valores a buscar
	private String nroPo;
	private String codFert;
	private String nroLote;

	// Valor que contiene los numeros de contenedores.
	private Integer selectedIdContenedor = 0;
	private String nroContenedor = "";

	/**
	 * onLoad.
	 * 
	 * @throws IOException
	 */
	public void onLoad() throws IOException {
		if (loginBean.isMenuAsignacion()) {
			if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) {
				return;
			}
			logger.info("[AsignacionBean - onLoad] inicio");
			this.limpiarVista();
		} else {
			ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			String path = ctx.getContextPath();
			FacesContext.getCurrentInstance().getExternalContext().redirect(path + "/index.xhtml");
		}
	}

	/**
	 * Limpia la vista al cargar.
	 */
	public void limpiarVista() {

		logger.info("[AsignacionBean - limpiarVista] inicio");
		this.listaPo.clear();
		this.listaPo.addAll(poService.listPoAsignacion());
		this.busquedaEtiqueta = new ArrayList<Etiqueta>();
		this.listaContenedores = new ArrayList<Container>();
		this.nroPo = "";
		this.nroLote = "";
		this.codFert = "";
		this.numContenedorId = new ArrayList<String>();
		this.contenedores.clear();
		this.asignaciones.clear();

	}

	/**
	 * Metodo que carga la lista de guias disponibles
	 * 
	 * @return List<String>
	 **/
	public List<String> autoCompletarFert() {

		logger.info("[AsignacionBean - autoCompletarGuiaAsignacion] inicio");
		return poService.autoCompletarPo(this.codFert);
	}

	/**
	 * Metodo que carga la lista de ruts disponibles
	 * 
	 * @return List<String>
	 */
	public List<String> autoCompletarLote() {
		logger.info("[AsignacionBean - autoCompletarRutAsignacion] inicio, rutExportador: " + this.nroLote);
		return poService.autoCompletarLote(this.nroLote);
	}

	/**
	 * Metodo que carga la tabla con los resultados de la busqueda
	 */
	public void listarEtiquetas() {
		try {
			logger.info("[AsignacionBean - listarEtiquetas] inicio");
			this.busquedaEtiqueta.clear();
			this.busquedaEtiqueta.addAll(etiquetaService.resultadoBusquedaEtiquetas(this.nroLote, this.nroPo, this.codFert));
		} catch (Exception e) {
			logger.error("[AsignacionBean - listarGuiaDespacho]  Error al listar etiquetas.");
		}
	}

	
	/**
	 * Metodo que actualiza la tabla con las listas y sublistas
	 **/
	public void actualizarTabla() {
		this.listarEtiquetas();
		// PrimeFaces.current().ajax().update("panel-busqueda");
	}

	/*
	 * Metodo que asigna y valida que los pallets no superen la cantidad permitidas
	 * por el contendor Y que vaya tanto el pallet y el contenedor vayan al mismo
	 * lugar
	 */
	public void asignarContenedor() {
		try {
			this.asignaciones = new ArrayList<Asignacion>();

			Container contenedorSeleccionado = contenedorService.buscarContenedor(selectedIdContenedor);
			this.nroContenedor = contenedorSeleccionado.getContainer();

			logger.info("[AsignacionBean - asignarContenedor] cont seleccionado " + contenedorSeleccionado.getId());
			logger.info("[AsignacionBean - asignarContenedor] etiquetas " + this.etiquetasSelect.size());

			int asignacionesContenedor = contenedorService.buscarAsignacionesContenedor(contenedorSeleccionado.getId());

			logger.info("[AsignacionBean - asignarContenedor] asignacionesContenedor " + asignacionesContenedor);
			logger.info("Etiquetas seleccionadas:_ " + this.etiquetasSelect.size());

			int palletsSel = this.etiquetasSelect.size();
			int maximoPallet = contenedorSeleccionado.getPalletCont() - asignacionesContenedor;

			logger.info("[AsignacionBean - asignarContenedor] palletsUsados " + palletsSel);
			logger.info("[AsignacionBean - asignarContenedor] maximoPallet " + maximoPallet);

			int errorNumPallet = 0;
			int asignacionExitosa = 0;
			Asignacion asignacion;

			if (!etiquetasSelect.isEmpty()) {
				for (Etiqueta etiqueta : etiquetasSelect) {
					etiqueta.setAsignado(true);
					etiqueta.setNroContenedor(contenedorSeleccionado.getContainer());
					etiquetaService.actualizarEtiquetaAsignacion(etiqueta.getId(),
							contenedorSeleccionado.getContainer());
					asignacion = new Asignacion();
					asignacion.setUsuarioCr(loginBean.getUsername());
					asignacion.setContenedor(contenedorSeleccionado);
					asignacion.setEtiqueta(etiqueta);
					this.asignaciones.add(asignacion);
					asignacionExitosa = asignacionExitosa + 1;
				}

				if (selectedIdContenedor != 0) {
					for (Asignacion asig : this.asignaciones) {
						asig.setId(null);
						asig.setContenedor(contenedorSeleccionado);
						asig.setFechaCr(new Date());
						asigService.guardarAsignacion(asig);
					}
				}


			} else {
				PrimeFaces.current().executeScript(
						Util.alerta("Advertencia", "No se han seleccionado pallets para asignar.", "warning"));
				logger.error("[AsignacionBean - asignarContenedor] no hay pallets seleccionados");
				asignaciones.clear();
			}

			// exitoso
			if (asignacionExitosa > 0 && errorNumPallet == 0) {
				PrimeFaces.current().ajax().update("formBusqueda:panel2");
				logger.info("[AsignacionBean - asignarContenedor] Asignacion de pallets realizada");
				PrimeFaces.current().executeScript("PF('dlgInfo').hide();");
				PrimeFaces.current()
						.executeScript(Util.alerta("Etiquetas Asignadas", "Se han asignado " + etiquetasSelect.size()
								+ " etiquetas al contenedor " + contenedorSeleccionado.getContainer(), "info"));

			}


		} catch (Exception e) {
			PrimeFaces.current()
					.executeScript(Util.alerta("Error", "Error al asignar la lista de pallets al contenedor", "error"));
			logger.error("[AsignacionBean - asignarContenedor] Error al guardar lista de asignaciones ");
			asignaciones.clear();
		}

	}

	/**
	 * Metodo que guarda en base de datos las asignaciones creadas
	 */
	public void guardarAsignaciones() {

		logger.info("[AsignacionBean - guardarAsignaciones] inicio");

		if (!this.asignaciones.isEmpty()) {

			if (this.selectedIdContenedor != 0) {
				Container contenedor = contenedorService.buscarContenedor(selectedIdContenedor);
				contenedor.setEstado("ASIGNADO");
				contenedorService.actualizarContainer(contenedor);
			}

			logger.info("[AsignacionBean - guardarAsignaciones] asignaciones size: " + etiquetasSelect.size());

			logger.info("[AsignacionBean - guardarAsignaciones] Lista de asignaciones guardadas correctamente ");
			this.asignaciones.clear();
			this.listarEtiquetas();
			PrimeFaces.current().executeScript("PF('dlgInfo').hide();habilitarScroll();");
			PrimeFaces.current().ajax().update("formBusqueda:mensajes");
			PrimeFaces.current().ajax().update("formBusqueda:panel2");
			PrimeFaces.current().scrollTo("formBusqueda:mensajes");
			PrimeFaces.current().executeScript(
					"Swal.fire('Asignación Exitosa','Las etiquetas se han asignado correctamente', 'info');");
		} else {
			if (this.selectedIdContenedor != 0) {
				Container contenedor = contenedorService.buscarContenedor(selectedIdContenedor);
				contenedor.setEstado("ASIGNADO");
				contenedorService.actualizarContainer(contenedor);
				logger.info("[AsignacionBean - guardarAsignaciones] asignaciones size: " + etiquetasSelect.size());

				logger.info("[AsignacionBean - guardarAsignaciones] Lista de asignaciones guardadas correctamente ");
				this.asignaciones.clear();
				this.listarEtiquetas();
				PrimeFaces.current().executeScript("PF('dlgInfo').hide();habilitarScroll();");
				PrimeFaces.current().ajax().update("formBusqueda:mensajes");
				PrimeFaces.current().ajax().update("formBusqueda:panel2");
				PrimeFaces.current().scrollTo("formBusqueda:mensajes");
				PrimeFaces.current().executeScript(
						"Swal.fire('Asignación Exitosa','Las etiquetas se han asignado correctamente', 'info');");
			} else {
				FacesContext.getCurrentInstance().addMessage("mensajesDialogo",
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al guardar la lista de asignaciones", ""));
				logger.error("[AsignacionBean - guardarAsignaciones] Error al guardar lista de asignaciones '");

				PrimeFaces.current().executeScript("PF('dlgInfo').hide();habilitarScroll();");
				PrimeFaces.current().ajax().update("formBusqueda:mensajesDialogo");
				PrimeFaces.current().scrollTo("formBusqueda:mensajesDialogo");
			}
		}

	}

	public void confirmar() {
		String dialog = "";
		if (!this.asignaciones.isEmpty()) {
			 dialog = "Swal.fire({";
			dialog += "title: \"Confirmación\",";
			dialog += " text: \"Se completará la asignación del contenedor " + this.nroContenedor
					+ " y ya no se podran asignar mas etiquetas. ¿Desea continuar?\",";
			dialog += " type: 'warning',";
			dialog += " showCancelButton: true,";
			dialog += " confirmButtonColor: '#3085d6',";
			dialog += " cancelButtonColor: '#d33',";
			dialog += " confirmButtonText: 'Sí',";
			dialog += " cancelButtonText: 'No'";
			dialog += " }).then((result) => {";
			dialog += " if (result.value) {";
			dialog += "  guardar();habilitarScroll();PF('formDlg').hide();";
			dialog += " } else { habilitarScroll(); }})";

			PrimeFaces.current().executeScript(dialog);
		} else {
			if (this.asignaciones.isEmpty() && this.nroContenedor != "") {
				 dialog = "Swal.fire({";
				dialog += "title: \"Confirmación\",";
				dialog += " text: \"Se completará la asignación del contenedor " + this.nroContenedor
						+ " y ya no se podran asignar mas etiquetas. ¿Desea continuar?\",";
				dialog += " type: 'warning',";
				dialog += " showCancelButton: true,";
				dialog += " confirmButtonColor: '#3085d6',";
				dialog += " cancelButtonColor: '#d33',";
				dialog += " confirmButtonText: 'Sí',";
				dialog += " cancelButtonText: 'No'";
				dialog += " }).then((result) => {";
				dialog += " if (result.value) {";
				dialog += "  guardar();habilitarScroll();PF('formDlg').hide();";
				dialog += " } else { habilitarScroll(); }})";
			}

			return;
		}
	}

	/**
	 * Metodo que determina si el pallet esta o no apto para asignarse
	 * 
	 * @param guia
	 */
	public void cargarContenedores() {
		logger.info("[AsignacionBean - cargarContenedores] inicio");

		contenedores = new ArrayList<SelectItem>();
		contenedores.addAll(contenedorService.listarContenedoresDisponibles());
		PrimeFaces.current().ajax().update("formDlg:panelDlg");
		PrimeFaces.current().executeScript("PF('dlgInfo').show();");
		PrimeFaces.current().executeScript("bloquearScroll()");
		logger.info("[AsignacionBean - cargarContenedores] contenedores size: " + contenedores.size());
		logger.info("Etiquetas seleccionadas: " + this.etiquetasSelect.size());
	}

	/*
	 * Accesadores y Mutadores
	 */
	public String getNroPo() {
		return nroPo;
	}

	public void setNroPo(String nroPo) {
		this.nroPo = nroPo;
	}

	public String getNroLote() {
		return nroLote;
	}

	public void setNroLote(String nroLote) {
		this.nroLote = nroLote;
	}

	public Integer getSelectedIdContenedor() {
		return selectedIdContenedor;
	}

	public void setSelectedIdContenedor(Integer selectedIdContenedor) {
		this.selectedIdContenedor = selectedIdContenedor;
	}

	public List<String> getNumContenedorId() {
		return numContenedorId;
	}

	public void setNumContenedorId(List<String> numContenedorId) {
		this.numContenedorId = numContenedorId;
	}

	public List<SelectItem> getContenedores() {
		return contenedores;
	}

	public void setContenedores(List<SelectItem> contenedores) {
		this.contenedores = contenedores;
	}

	public List<Container> getListaContenedores() {
		return listaContenedores;
	}

	public void setListaContenedores(List<Container> listaContenedores) {
		this.listaContenedores = listaContenedores;
	}

	public List<Etiqueta> getBusquedaEtiqueta() {
		return busquedaEtiqueta;
	}

	public void setBusquedaEtiqueta(List<Etiqueta> busquedaEtiqueta) {
		this.busquedaEtiqueta = busquedaEtiqueta;
	}

	public List<Etiqueta> getEtiquetasSelect() {
		return etiquetasSelect;
	}

	public void setEtiquetasSelect(List<Etiqueta> etiquetasSelect) {
		this.etiquetasSelect = etiquetasSelect;
	}

	public String getNroContenedor() {
		return nroContenedor;
	}

	public void setNroContenedor(String nroContenedor) {
		this.nroContenedor = nroContenedor;
	}

	public List<String> getListaPo() {
		return listaPo;
	}

	public void setListaPo(List<String> listaPo) {
		this.listaPo = listaPo;
	}

	public String getCodFert() {
		return codFert;
	}

	public void setCodFert(String codFert) {
		this.codFert = codFert;
	}
	
	

}
