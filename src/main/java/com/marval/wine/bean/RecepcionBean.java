package com.marval.wine.bean;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.primefaces.PrimeFaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.marval.wine.entity.Etiqueta;
import com.marval.wine.entity.Mensaje;
import com.marval.wine.entity.PoAxis;
import com.marval.wine.entity.PurchaseOrder;
import com.marval.wine.entity.PurchaseOrderDetalle;
import com.marval.wine.ftp.ConectaSFTP;
import com.marval.wine.service.AjusteInventarioService;
import com.marval.wine.service.ContainerService;
import com.marval.wine.service.DetalleRecepcionService;
import com.marval.wine.service.EtiquetaService;
import com.marval.wine.service.PurchaseOrderService;
import com.marval.wine.util.Constantes;
import com.marval.wine.util.Util;

@ManagedBean
@SessionScope
@Component
public class RecepcionBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(RecepcionBean.class);

	@Autowired
	PurchaseOrderService poService;

	@Autowired
	DetalleRecepcionService detalleService;

	@Autowired
	EtiquetaService etiquetaService;

	@Autowired
	ContainerService contServ;

	@Autowired
	AjusteInventarioService ajusteService;

	@Autowired
	LoginBean loginBean;
	
	@Autowired
	ConectaSFTP conectaSFtp;

	private List<PoAxis> listaPo = new ArrayList<PoAxis>();
	private List<PoAxis> listaPoAux = new ArrayList<PoAxis>();
	private List<PurchaseOrderDetalle> listaDetalle = new ArrayList<PurchaseOrderDetalle>();
	private List<String> documentos = Constantes.TIPO_DOC;
	private List<String> vinas = new ArrayList<String>();
	private List<String> slipSheet = Constantes.SLIPSHEET;
	private List<String> estados = Constantes.ESTADO_DETALLE;

	private String selectedVina;
	private String selectedDoc;
	private PurchaseOrder selectedPo;
	private PurchaseOrderDetalle selectedDetalle;
	private String selectedSs = "";
	private Boolean selectedRepa = false;
	private String totalParcial = "";
	private Integer unidadesRecep;
	private Integer totalDetalles = 0;
	private boolean botonAgregar;

	private StringBuffer htmlEtiquetas;

	private Etiqueta etSelec;
	private List<Etiqueta> listEtiquetasSel = new ArrayList<Etiqueta>();

	public void onLoad() throws IOException {
		if (loginBean.isMenuRecepcion()) {
			if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) {
				return;
			}
			logger.info("[RecepcionBean - onLoad] inicio");

			limpiarFiltros();

		} else {
			ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			String path = ctx.getContextPath();
			FacesContext.getCurrentInstance().getExternalContext().redirect(path + "/index.xhtml");
		}
	}

	public void limpiarFiltros() {
		this.selectedPo = new PurchaseOrder();
		this.selectedDetalle = new PurchaseOrderDetalle();
		this.listaPo.clear();
		this.listaPoAux.clear();
		this.vinas.clear();
		this.selectedVina = "";
		this.listaPo = new ArrayList<PoAxis>();
		this.listaPoAux = new ArrayList<PoAxis>();
		this.totalDetalles = 0;
		this.htmlEtiquetas = new StringBuffer();
		this.etSelec = new Etiqueta();
		this.listEtiquetasSel.clear();
		this.botonAgregar = false;
		this.selectedRepa = false;
		// this.vinas.addAll(contServ.consultarListaVinas());
	}

	public void buscarPo() {
		if (this.selectedVina == null || this.selectedVina.isEmpty()) {

			PrimeFaces.current().executeScript(Util.alerta("Filtro Mandatorio", "Debe ingresar una viña", "warning"));
			return;
		} else {
			this.listaPo.clear();
			this.listaPoAux.clear();
			this.listaPo = poService.listPo(this.selectedVina);
			this.listaPoAux.addAll(listaPo);
			PrimeFaces.current().ajax().update("poForm");
			PrimeFaces.current().ajax().update("vinaForm");
		}
	}

	public void agregarRecepcion() {
		String loteOriginal =  this.selectedPo.getLote();
		this.selectedDetalle.setDescripcionProducto(this.selectedPo.getProductDescription());
		this.selectedDetalle.setCodProducto(this.selectedPo.getProductNumber());
		this.selectedDetalle.setCosecha(this.selectedPo.getVintage());
		this.selectedDetalle.setLote(loteOriginal);
		if (this.selectedSs.equals("SI")) {
			this.selectedPo.setSlipsheet(true);
		}
		if (this.totalParcial.equals("SI")) {
			this.botonAgregar = true;
			this.selectedDetalle.setTotalParcial("SI");
			PrimeFaces.current().ajax().update("recepcionForm:rfPanel");
		} else {
			this.selectedDetalle.setTotalParcial("NO");
		}

		this.selectedDetalle.setPo(this.selectedPo);
		this.selectedDetalle.setFechaCr(new Date());
		this.selectedDetalle.setEstado("RECEPCIONADO");
		crearEtiqueta();
		this.selectedPo.getDetalle().add(this.selectedDetalle);
		poService.updatePo(this.selectedPo);
		PurchaseOrderDetalle detalleAux = new PurchaseOrderDetalle();
		detalleAux = this.selectedDetalle;
		this.selectedDetalle = new PurchaseOrderDetalle();
		if (this.totalParcial.equals("NO")) {
			this.selectedDetalle.setTipoDoc(detalleAux.getTipoDoc());
			this.selectedDetalle.setNumeroDoc(detalleAux.getNumeroDoc());
			this.selectedDetalle.setFechaDoc(detalleAux.getFechaDoc());
			this.selectedDetalle.setFechaRecepcion(detalleAux.getFechaRecepcion());
			this.selectedDetalle.setLote(detalleAux.getLote());
			this.selectedDetalle.setTotalParcial("NO");
			this.selectedDetalle.setEstadoCarga(detalleAux.getEstadoCarga());
		}
		calcularTotal();

	}

	public void guardarRecepcion() {
		if (this.selectedPo.getDetalle().isEmpty()) {
			PrimeFaces.current()
					.executeScript(Util.alerta("Detalle vacio", "Debe ingresar al menos un detalle", "warning"));
			return;
		} else {

			this.unidadesRecep = 0;
			for (PurchaseOrderDetalle pd : this.selectedPo.getDetalle()) {
				this.unidadesRecep = this.unidadesRecep + pd.getCantidad();
			}
			this.selectedPo.setRecivedQuantity(this.unidadesRecep);
			if (this.totalParcial.equals("SI")) {
				this.selectedPo.setEstado(Constantes.ESTADO_PO_RECEPCIONADO);
				this.crearGoodReceipt(this.selectedPo);
			}
			poService.updatePo(this.selectedPo);
			PrimeFaces.current().ajax().update("recepcionForm:rfPanel");
			PrimeFaces.current().executeScript("PF('rf').hide();");
			PrimeFaces.current().executeScript("habilitarScroll()");
			buscarPo();
			this.selectedVina = "";
			PrimeFaces.current().executeScript(Util.alerta("Recepción Exitosa",
					"Se ha recepcionado el Purchase Order " + this.selectedPo.getPurchaseOrderId(), "success"));
		}

	}

	public void quitarRecepcion(PurchaseOrderDetalle detalle) {
		this.botonAgregar = false;
		PrimeFaces.current().ajax().update("recepcionForm:rfPanel");
		this.selectedPo.getDetalle().remove(detalle);
		etiquetaService.cancelarEtiqueta(detalle.getId());
		detalleService.eliminarDetalle(detalle.getId());
		calcularTotal();
	}

	public void calcularTotal() {
		this.totalDetalles = 0;
		if (!this.selectedPo.getDetalle().isEmpty()) {
			for (PurchaseOrderDetalle pd : this.selectedPo.getDetalle()) {
				this.totalDetalles = this.totalDetalles + pd.getCantidad();
			}
		}
		PrimeFaces.current().ajax().update("recepcionForm:rfPanel");
	}

	public void cargaRecepcion(Integer id) {
		this.selectedPo = poService.buscarPo(id);
		this.selectedPo.getDetalle().clear();
		this.selectedPo.setDetalle(detalleService.listarDetalle(id));
		this.botonAgregar = false;
		this.listaDetalle.clear();
		this.listaDetalle.addAll(this.selectedPo.getDetalle());

		this.selectedDetalle = new PurchaseOrderDetalle();
		if (!this.listaDetalle.isEmpty()) {
			for (PurchaseOrderDetalle detalle : this.listaDetalle) {
				if (detalle.getTotalParcial().equals("SI")) {
					this.botonAgregar = true;
				}
			}
		}
		calcularTotal();
		PrimeFaces.current().ajax().update("recepcionForm:rfPanel");
		PrimeFaces.current().executeScript("PF('rf').show();");
		PrimeFaces.current().executeScript("bloquearScroll()");
	}

	public void crearEtiqueta() {

		int pallets = this.selectedDetalle.getPallet();

		for (int i = 0; i < pallets; i++) {
			Etiqueta etiqueta = new Etiqueta();
			etiqueta.setRepaletizado(this.selectedRepa);
			etiqueta.setDetallePo(this.selectedDetalle);
			etiqueta.setEstado(Constantes.ESTADO_ETIQUETA_RECEPCIONADA);
			etiqueta.setFechaCr(new Date());
			etiqueta.setAsignado(false);
			etiqueta.setSlipsheet(this.selectedPo.getSlipsheet());
			etiqueta.setNroEtiqueta(etiquetaService.obtenerEtiqueta());
			etiqueta.setCajasPallet(this.selectedDetalle.getCajasPorPallet());
			if (this.selectedRepa) {
				etiqueta.setHistorialRepaletizado("AX / " + etiqueta.getNroEtiqueta());
			} else {
				etiqueta.setHistorialRepaletizado(etiqueta.getNroEtiqueta());
			}
			this.selectedDetalle.getEtiquetas().add(etiqueta);
			this.selectedRepa = false;
		}

	}

	private void imprimirEtiqueta(Etiqueta et) {
		try {
			this.htmlEtiquetas.append("<p:outputPanel style=\"text-align: center;\">"
					+ "								<p:barcode value=\"0123456789\" type=\"qr\" width=\"200\" height=\"200\"/>"
					+ "							</p:outputPanel>" + "							"
					+ "							<p:spacer height=\"5\"></p:spacer>" + ""
					+ "							<table style=\"width: 100%\">" + "								<tr>"
					+ "									<th style=\"width: 18%;\"><p:outputLabel value=\"Etiqueta:\" /></th>"
					+ "									<th style=\"width: 32%;\"><p:outputLabel value=\"Posición actual:\" /></th>"
					+ "									<th style=\"width: 25%;\"><p:outputLabel value=\"Estado del bulto:\" /></th>"
					+ "									<th style=\"width: 25%;\"><p:outputLabel value=\"Repaletizado:\" /></th>"
					+ "								</tr>" + "								<tr>"
					+ "									<td><p:outputLabel"
					+ "									style=\"font-weight:bold; font-size: 16px; \""
					+ "									value=\"" + et.getNroEtiqueta() + "\" /></td>"
					+ "									<td><p:outputLabel"
					+ "									style=\"font-weight:bold; font-size: 15px; \""
					+ "									value=\"Pasillo " + et.getPasillo() + " Altura "
					+ et.getAltura() + " Posición " + et.getPosicion() + "\" /></td>"
					+ "									<td><p:outputLabel"
					+ "									style=\"font-weight:bold; font-size: 16px; \""
					+ "									value=\"" + et.getEstado() + "\" /></td>"
					+ "									<td><p:outputLabel"
					+ "									style=\"font-weight:bold; font-size: 16px; \""
					+ "									value=\""
					+ ((et.getHistorialRepaletizado() != null) ? et.getHistorialRepaletizado() : "NO") + "\" /></td>"
					+ "								</tr>" + "							</table>" + ""
					+ "							<hr />" + "							<p:spacer height=\"5\"></p:spacer>"
					+ "" + "							<p:panelGrid columns=\"4\" layout=\"grid\" id=\"panelManifDet\""
					+ "								columnClasses=\"ui-grid-col-3,ui-grid-col-6, ui-grid-col-3, ui-grid-col-6\">"
					+ "								<p:outputLabel value=\"P.O.:\" />"
					+ "								<p:outputLabel"
					+ "									style=\"font-size: 15px; line-height: 1;width: 100%;\""
					+ "									value=\"" + et.getDetallePo().getPo().getId() + "\" />" + ""
					+ "								<p:outputLabel value=\"Owner Id:\" />"
					+ "								<p:outputLabel"
					+ "									style=\" font-size: 15px; line-height: 1;width: 100%;\""
					+ "									value=\"" + et.getDetallePo().getPo().getOwnerId() + "\" />"
					+ "									"
					+ "								<p:outputLabel value=\"Producto:\" />"
					+ "								<p:outputLabel"
					+ "									style=\" font-size: 15px; line-height: 1;width: 100%;\""
					+ "									value=\"" + et.getDetallePo().getDescripcionProducto() + "\" />"
					+ "" + "								<p:outputLabel value=\"Código:\" />"
					+ "								<p:outputLabel"
					+ "									style=\" font-size: 15px; line-height: 1;width: 100%;\""
					+ "									value=\"" + et.getDetallePo().getCodProducto() + "\" />" + ""
					+ "								" + "									"
					+ "								<p:outputLabel value=\"Botellas / Caja:\" />"
					+ "								<p:outputLabel"
					+ "									style=\" font-size: 15px; line-height: 1;width: 100%;\""
					+ "									value=\"" + et.getDetallePo().getPo().getBottlesPerUnit()
					+ "\" />" + "									"
					+ "								<p:outputLabel value=\"Recepción\" />"
					+ "								<p:outputLabel"
					+ "									style=\" font-size: 15px; line-height: 1;width: 100%;\""
					+ "									value=\""
					+ new SimpleDateFormat("dd-MM-yyyy").format(et.getDetallePo().getFechaRecepcion()) + "\" />" + ""
					+ "								<p:outputLabel value=\"Cajas por Pallet:\" />"
					+ "								<p:outputLabel"
					+ "									style=\" font-size: 15px; line-height: 1;width: 100%;\""
					+ "									value=\"" + et.getDetallePo().getCajasPorPallet() + "\" />" + ""
					+ "								<p:outputLabel value=\"Cosecha:\" />"
					+ "								<p:outputLabel"
					+ "									style=\" font-size: 15px; line-height: 1;width: 100%;\""
					+ "									value=\"" + et.getDetallePo().getCosecha() + "\" />" + ""
					+ "								<p:outputLabel value=\"Cc:\" />"
					+ "								<p:outputLabel"
					+ "									style=\" font-size: 15px; line-height: 1;width: 100%;\""
					+ "									value=\"" + et.getDetallePo().getPo().getMlPerBottle() + "\" />"
					+ "" + "								<p:outputLabel value=\"Lote:\" />"
					+ "								<p:outputLabel"
					+ "									style=\" font-size: 15px; line-height: 1;width: 100%;\""
					+ "									value=\"" + et.getDetallePo().getLote() + "\" />" + ""
					+ "							</p:panelGrid>");

		} catch (Exception e) {
			PrimeFaces.current().executeScript(
					"Swal.fire(\"Problemas al buscar etiqueta\", \"Existieron problemas al consultar etiqueta, "
							+ "favor vuelva a intentar.\", \"error\");");
			logger.info("[imprimir - error] error " + e);
		}
	}

	public void imprimirEtiquetas() {
		try {
			this.htmlEtiquetas = new StringBuffer();
			this.htmlEtiquetas.append("<p:panel id=\"etiqueta\" styleClass=\"card no-border\">"); // TODO panel etiqueta

			for (Etiqueta et : this.selectedDetalle.getEtiquetas()) {
				this.imprimirEtiqueta(et);
			}

			this.htmlEtiquetas.append("<p:panel id=\"etiqueta\" styleClass=\"card no-border\">"); // TODO fin panel
																									// etiqueta
			PrimeFaces.current().ajax().update("formEtiquetas");
			PrimeFaces.current().executeScript("$('#recepcionForm\\\\:imprimirEtiquetas').click();");
		} catch (Exception e) {
			PrimeFaces.current().executeScript(
					"Swal.fire(\"Problemas al buscar etiqueta\", \"Existieron problemas al consultar etiqueta, "
							+ "favor vuelva a intentar.\", \"error\");");
			logger.info("[imprimir - error] error " + e);
		}
	}

	public void impEtiquetas(List<Etiqueta> et) {
		try {

			this.listEtiquetasSel.clear();
			this.listEtiquetasSel.addAll(generarQr(et));
			PrimeFaces.current().ajax().update("formEtiquetas");

		} catch (Exception e) {
			PrimeFaces.current().executeScript(
					"Swal.fire(\"Problemas al buscar etiqueta\", \"Existieron problemas al consultar etiqueta, "
							+ "favor vuelva a intentar.\", \"error\");");
			logger.info("[imprimir - error] error " + e);
		}
	}
	
	public List<Etiqueta> generarQr(List<Etiqueta> etiquetas){
		List<Etiqueta> nuevasEtiquetas = new ArrayList<Etiqueta>();
		for(Etiqueta et : etiquetas) {
			String qr = "";
			qr =  "Etiqueta: " + et.getNroEtiqueta() + "\n" + "Cog. Fert: " + et.getDetallePo().getPo().getCodFert() + "\n"+ "Cant. Cajas: " + et.getCajasPallet() + "\n" + "Lote: " + et.getDetallePo().getLote();
			et.setQr(qr);
			nuevasEtiquetas.add(et);
		}
		return nuevasEtiquetas;
	}
	

	public void crearGoodReceipt(PurchaseOrder po) {
		logger.info("[crearGoodReceipt] Creando archivo");
		// TODO datos recepcion
		StringBuffer archivo = new StringBuffer();

		for (PurchaseOrderDetalle pod : po.getDetalle()) {
			String estado = "";
			String total = "";
			if (pod.getEstadoCarga().equals("Buen estado")) {
				estado = "GOOD";
			} else {
				estado = "DAMAGED";
			}

			if (pod.getTotalParcial().equals("SI")) {
				total = "Y";
			} else {
				total = "N";
			}
			archivo.append(Constantes.SPCR_RECEIPT);
			archivo.append(Constantes.SPCR_SEAPORT);
			archivo.append(new SimpleDateFormat("yyyyMMdd").format(new Date()) + "*"); // fecha
			archivo.append(Util.rellenarEspacios(Constantes.SPRC_ORDER_REF, po.getOrderReference()));// PO
			archivo.append(Util.rellenarEspacios(Constantes.SPRC_ORDER_REF, ""));// Container number
			archivo.append(Util.rellenarEspacios(Constantes.SPRC_PO, po.getPurchaseOrderId()));// Purchase order id
			archivo.append(Util.rellenarEspacios(Constantes.SPCR_CANT_ESP_PRODUCTO, po.getWarehouseId()));// Warehouse
			archivo.append(Util.rellenarEspacios(Constantes.SPCR_CANT_ESP_VINA, po.getOwnerId()));// Owner id
			archivo.append(Util.rellenarEspacios(Constantes.SPCR_CANT_ESP_PRODUCTO, po.getProductNumber()));// producto
																											// esperado
			archivo.append(Util.rellenarEspacios(Constantes.SPCR_CANT_ESP_PRODUCTO, pod.getCodProducto()));// Producto
																											// recibido
			archivo.append(Util.rellenarEspacios(Constantes.SPCR_CANT_ESP_LOTE, pod.getCosecha()));// Lote
			archivo.append(Util.rellenarEspacios(Constantes.SPCR_CANT_ESP_ESTADO, estado));// estado
			archivo.append(Util.rellenarEspacios(Constantes.SPCR_CANT_ESP_CANTAJUSTES, pod.getCantidad().toString()));// cantidad
																														// recibida
			archivo.append(new SimpleDateFormat("yyyyMMdd").format(pod.getFechaRecepcion()) + "*"); // fecha recepcion
			archivo.append(Util.rellenarEspacios(Constantes.SPRC_FLAG_RECP, total));// flag recibidas total
			archivo.append(Util.rellenarEspacios(Constantes.SPCR_CANT_ESP_LOTE, pod.getCosecha()));// Lote
			archivo.append(Util.rellenarEspacios(20, ""));// Dato sin uso
			archivo.append(Util.rellenarEspacios(15, ""));// Dato sin uso
			archivo.append(Util.rellenarEspacios(5, ""));// Dato sin uso
			archivo.append(Util.rellenarEspacios(8, ""));// Dato sin uso
			archivo.append(Util.rellenarEspacios(4, po.getVintage()));// Vintage
			if (total.equals("N")) {
				archivo.append("\n");// salto de linea
			}
		}
		logger.info("[crearGoodReceipt] CONTENIDO ARCHIVO: ");
		logger.info(archivo.toString());
		String nombreTxt = Constantes.PREFIJO_ARCHIVO_GOOD_RECEIPT + this.obtenerSecuenciaSPCR() + ".txt";
		try {
			conectaSFtp.subeArchivoOut(new ByteArrayInputStream(archivo.toString().getBytes()), nombreTxt);

			Mensaje mensaje = new Mensaje();
			mensaje.setUsuarioCr(loginBean.getUsername());
			mensaje.setFechaCr(new Date());
			mensaje.setTipo(Constantes.ARCHIVO_RECEIPT);
			mensaje.setNombreArchivo(nombreTxt);
			mensaje.setMensaje(archivo.toString());
			poService.guardarMensaje(mensaje);

		} catch (SocketException e1) {
			logger.info("[crearGoodReceipt - SocketException] error " + e1);
			e1.printStackTrace();
		} catch (IOException e2) {
			logger.info("[crearGoodReceipt - IOException] error " + e2);
			e2.printStackTrace();
		}

	}

	private String obtenerSecuenciaSPCR() {

		String sec = this.ajusteService.secuenciaSPRC();

		if (sec.length() == 1) {
			sec = Constantes.CINCO_ZEROS + sec;
		} else if (sec.length() == 2) {
			sec = Constantes.CUATRO_ZEROS + sec;
		} else if (sec.length() == 3) {
			sec = Constantes.TRES_ZEROS + sec;
		} else if (sec.length() == 4) {
			sec = Constantes.DOS_ZEROS + sec;
		} else if (sec.length() == 5) {
			sec = Constantes.UN_ZEROS + sec;
		}

		return sec;
	}

	public List<PoAxis> getListaPo() {
		return listaPo;
	}

	public void setListaPo(List<PoAxis> listaPo) {
		this.listaPo = listaPo;
	}

	public List<String> getVinas() {
		return vinas;
	}

	public void setVinas(List<String> vinas) {
		this.vinas = vinas;

	}

	public String getSelectedVina() {
		return selectedVina;
	}

	public void setSelectedVina(String selectedVina) {
		this.selectedVina = selectedVina;

	}

	public List<String> getDocumentos() {
		return documentos;
	}

	public void setDocumentos(List<String> documentos) {
		this.documentos = documentos;
	}

	public String getSelectedDoc() {
		return selectedDoc;
	}

	public void setSelectedDoc(String selectedDoc) {
		this.selectedDoc = selectedDoc;
	}

	public PurchaseOrder getSelectedPo() {
		return selectedPo;
	}

	public void setSelectedPo(PurchaseOrder selectedPo) {
		this.selectedPo = selectedPo;
	}

	public PurchaseOrderDetalle getSelectedDetalle() {
		return selectedDetalle;
	}

	public void setSelectedDetalle(PurchaseOrderDetalle selectedDetalle) {
		this.selectedDetalle = selectedDetalle;
	}

	public List<PurchaseOrderDetalle> getListaDetalle() {
		return listaDetalle;
	}

	public void setListaDetalle(List<PurchaseOrderDetalle> listaDetalle) {
		this.listaDetalle = listaDetalle;
	}

	public List<String> getSlipSheet() {
		return slipSheet;
	}

	public void setSlipSheet(List<String> slipSheet) {
		this.slipSheet = slipSheet;
	}

	public List<String> getEstados() {
		return estados;
	}

	public void setEstados(List<String> estados) {
		this.estados = estados;
	}

	public String getSelectedSs() {
		return selectedSs;
	}

	public void setSelectedSs(String selectedSs) {
		this.selectedSs = selectedSs;
	}

	public String getTotalParcial() {
		return totalParcial;
	}

	public void setTotalParcial(String totalParcial) {
		this.totalParcial = totalParcial;
	}

	public Integer getUnidadesRecep() {
		return unidadesRecep;
	}

	public void setUnidadesRecep(Integer unidadesRecep) {
		this.unidadesRecep = unidadesRecep;
	}

	public StringBuffer getHtmlEtiquetas() {
		return htmlEtiquetas;
	}

	public void setHtmlEtiquetas(StringBuffer htmlEtiquetas) {
		this.htmlEtiquetas = htmlEtiquetas;
	}

	public Etiqueta getEtSelec() {
		return etSelec;
	}

	public void setEtSelec(Etiqueta etSelec) {
		this.etSelec = etSelec;
	}

	public List<Etiqueta> getListEtiquetasSel() {
		return listEtiquetasSel;
	}

	public void setListEtiquetasSel(List<Etiqueta> listEtiquetasSel) {
		this.listEtiquetasSel = listEtiquetasSel;
	}

	public boolean isBotonAgregar() {
		return botonAgregar;
	}

	public void setBotonAgregar(boolean botonAgregar) {
		this.botonAgregar = botonAgregar;
	}

	public List<PoAxis> getListaPoAux() {
		return listaPoAux;
	}

	public void setListaPoAux(List<PoAxis> listaPoAux) {
		this.listaPoAux = listaPoAux;
	}

	public Integer getTotalDetalles() {
		return totalDetalles;
	}

	public void setTotalDetalles(Integer totalDetalles) {
		this.totalDetalles = totalDetalles;
	}

	public Boolean getSelectedRepa() {
		return selectedRepa;
	}

	public void setSelectedRepa(Boolean selectedRepa) {
		this.selectedRepa = selectedRepa;
	}

}
