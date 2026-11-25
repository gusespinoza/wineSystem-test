package com.marval.wine.bean;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.primefaces.PrimeFaces;
import org.primefaces.event.RowEditEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.marval.wine.entity.Container;
import com.marval.wine.entity.DespachoContenedor;
import com.marval.wine.entity.Mensaje;
import com.marval.wine.entity.PickingTicket;
import com.marval.wine.ftp.ConectaSFTP;
import com.marval.wine.service.AjusteInventarioService;
import com.marval.wine.service.ContainerService;
import com.marval.wine.service.EtiquetaService;
import com.marval.wine.service.PickingTicketService;
import com.marval.wine.service.PurchaseOrderService;
import com.marval.wine.util.Constantes;
import com.marval.wine.util.Util;

@ManagedBean
@SessionScope
@Component
public class DespachoBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(DespachoBean.class);

	@Autowired
	PurchaseOrderService poService;

	@Autowired
	EtiquetaService etiquetaService;

	@Autowired
	ContainerService contServ;

	@Autowired
	PickingTicketService ptService;

	@Autowired
	public AjusteInventarioService ajusteService;

	@Autowired
	private LoginBean loginBean;
	
	@Autowired
	ConectaSFTP conectaSFtp;

	private int selectedCont;

	private List<PickingTicket> listaPt = new ArrayList<PickingTicket>();
	private PickingTicket PtSelected = new PickingTicket();
	private List<DespachoContenedor> cDSelected = new ArrayList<DespachoContenedor>();
	private Map<Integer, String> listaCont = new HashMap<Integer, String>();
	private List<DespachoContenedor> listContDespacho = new ArrayList<DespachoContenedor>();
	private List<String> documentos = Constantes.TIPO_DOC;
	private Container contenedor;
	private Integer cajas;
	private Integer pallets;
	private Integer cajasSelected = 0;
	private boolean contDespachado;

	public void onLoad() throws IOException {
		if (loginBean.isMenuDespacho()) {
			if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) {
				return;
			}
			logger.info("[DespachoBean - onLoad] inicio");
			if (this.contDespachado) {
				PrimeFaces.current().executeScript("Swal.fire('Despacho Finalizado', 'El despacho del contenedor "
						+ contenedor.getContainer() + " ha finalizado correctamente.',  'info');");
				this.contDespachado = false;
			}
			limpiarFiltros();
			this.listaCont = contServ.listarContenedoresConsolidados();
		} else {
			ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			String path = ctx.getContextPath();
			FacesContext.getCurrentInstance().getExternalContext().redirect(path + "/index.xhtml");
		}

	}

	public void cargarListas() throws IOException {
		if (this.contenedor != null && (!this.contenedor.getNumeroDoc().isEmpty()
				&& !this.contenedor.getTipoDocu().isEmpty() && this.contenedor.getFechaConsolidado() != null
				&& this.contenedor.getFechaEmbarque() != null && this.contenedor.getIniConsolidado() != null
				&& this.contenedor.getFinConsolidado() != null)) {
			this.contenedor.setFechaConsolidado(this.contenedor.getFinConsolidado());
			contServ.actualizarContainer(this.contenedor);
		} else {
			this.contenedor = contServ.buscarContenedor(this.selectedCont);
		}
		this.listContDespacho = contServ.buscarDatosDespacho(this.selectedCont);
		this.cargarListaPt(this.listContDespacho);
		ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		String path = ctx.getContextPath();
		FacesContext.getCurrentInstance().getExternalContext().redirect(path + "/despacho");
		logger.info("[DespachoBean - cargaPalletDisponibles] Fin");
	}

	public void limpiarFiltros() {
		this.PtSelected = new PickingTicket();
		this.PtSelected.setSeleccionado(false);
		this.selectedCont = 0;
		this.cajasSelected = 0;
		this.contenedor = new Container();
		this.contDespachado = false;
		this.listaPt.clear();
		this.listaCont.clear();
		this.listaCont.clear();
		contarUnidades();
	}

	public void contarUnidades() {
		this.cajasSelected = 0;
		if (!this.cDSelected.isEmpty()) {
			for (DespachoContenedor dc : this.cDSelected) {
				cajasSelected = cajasSelected + dc.getCajasConsolidas();
			}
		}
	}

	public void validarContenedor() throws IOException {
		if (this.selectedCont != 0) {
			this.contenedor = contServ.buscarContenedor(this.selectedCont);
			if (this.contenedor.getNumeroDoc() == null || this.contenedor.getTipoDocu() == null
					|| this.contenedor.getFechaConsolidado() == null || this.contenedor.getFechaEmbarque() == null
					|| this.contenedor.getIniConsolidado() == null || this.contenedor.getFinConsolidado() == null) {
				PrimeFaces.current().ajax().update("contForm:rfPanel");
				PrimeFaces.current().executeScript("PF('rf').show();");
				PrimeFaces.current().executeScript("bloquearScroll()");
			} else {
				this.cargarListas();
			}

		} else {
			this.selectedCont = 0;
			PrimeFaces.current()
					.executeScript(Util.alerta("Alerta", "Debe seleccionar un contendor para despachar.", "warning"));
		}
	}

	public void autoAgregarEtiquetas() {
		this.PtSelected.setSeleccionado(true);
		String numeroProd = this.PtSelected.getProductNumber();
		String vina = this.PtSelected.getOwnerId();
		String cosecha = this.PtSelected.getLotNumber();
		this.cDSelected.clear();
		if (!this.listContDespacho.isEmpty()) {
			for (DespachoContenedor dc : this.listContDespacho) {
				if (dc.getCosecha().equals(cosecha) && dc.getVina().equals(vina)
						&& dc.getNumeroProducto().equals(numeroProd)) {
					this.cDSelected.add(dc);
				}
			}
			contarUnidades();
			PrimeFaces.current().ajax().update("ptForm:panelPo");
		}

	}

	public void comprobarPt() throws IOException {

		int totalCajas = 0;
		String numeroProd = this.PtSelected.getProductNumber();
		String vina = this.PtSelected.getOwnerId();
		String cosecha = this.PtSelected.getLotNumber();

		if (this.PtSelected.isSeleccionado() != false) {
			if (this.cDSelected.isEmpty() || this.cDSelected == null) {
				String dialog = "Swal.fire({";
				dialog += "title: \"Confirmación\",";
				dialog += " text: \"Se realizará el envio de Pt: " + this.PtSelected.getOrdenReference() + " sin etiquetas asociadas por "+ this.PtSelected.getOrderQuantity() +" cajas. Producto; " + this.PtSelected.getProductNumber() + ". Cosecha;" + this.PtSelected.getLotNumber()
						+ " . ¿Desea continuar? \",";
				dialog += " type: 'warning',";
				dialog += " showCancelButton: true,";
				dialog += " confirmButtonColor: '#3085d6',";
				dialog += " cancelButtonColor: '#d33',";
				dialog += " confirmButtonText: 'Confirmar despacho',";
				dialog += " cancelButtonText: 'Cancelar'";
				dialog += " }).then((result) => {";
				dialog += " if (result.value) {";
				dialog += "  mod();";
				dialog += " } else { habilitarScroll(); }})";

				PrimeFaces.current().executeScript(dialog);
				
			} else {

				for (DespachoContenedor dc : this.cDSelected) {

					totalCajas = totalCajas + dc.getCajasConsolidas();

					if (!numeroProd.equals(dc.getNumeroProducto())) {
						PrimeFaces.current().executeScript(Util.alerta("Advertencia",
								"Numero de producto " + dc.getNumeroProducto() + " no coincide con " + numeroProd,
								"warning"));
						return;

					}
					if (!vina.equals(dc.getVina())) {
						PrimeFaces.current().executeScript(Util.alerta("Advertencia",
								"La viña " + dc.getVina() + " no coincide con " + vina, "warning"));
						return;

					}
					if (!cosecha.equals(dc.getCosecha())) {
						PrimeFaces.current().executeScript(Util.alerta("Advertencia",
								"La cosecha " + dc.getCosecha() + " no coincide con " + cosecha, "warning"));
						return;

					}

				}

				if (totalCajas == PtSelected.getOrderQuantity()) {
					this.despachar();
				} else {
					PrimeFaces.current()
							.executeScript(Util.alerta("Advertencia", "La cantidad de cajas no cionciden", "warning"));
				}

			}
		} else {
			if (this.cDSelected.isEmpty() || this.cDSelected == null) {
				PrimeFaces.current().executeScript(Util.alerta("Advertencia",
						"Se debe seleccionar al menos una etiqueta para despachar.", "warning"));

			} else {
				String tipo = cDSelected.get(0).getTipoRecepcion();
				boolean distinto = false;
				if (tipo.equals("AXIS")) {
					PrimeFaces.current().executeScript(
							Util.alerta("Advertencia", "Se debe seleccionar al menos un Picking Ticket.", "warning"));
					return;
				}
				for (DespachoContenedor dc : this.cDSelected) {
					if (!tipo.equals(dc.getTipoRecepcion())) {
						PrimeFaces.current()
								.executeScript(Util.alerta("Advertencia",
										"No se pueden despachar carga con tipos distintos de recepción al mismo tiempo",
										"warning"));
						distinto = true;
						break;
					}
				}
				if (distinto) {
					return;
				} else {
					despachar();
				}

			}
		}

	}
	
	
	public void despachar() throws IOException {
		if (this.PtSelected.isSeleccionado() != false) {
			this.PtSelected.setEstado("DESPACHADO");
			ptService.updatePt(this.PtSelected);
		}
		crearShippingNotification(this.PtSelected, this.cDSelected);

		List<DespachoContenedor> conteoTotal = contServ.buscarDatosDespacho(this.contenedor.getId());
		if (conteoTotal.isEmpty()) {
			this.contenedor.setEstado("DESPACHADO");
			contServ.actualizarContainer(this.contenedor);
		}
		ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		String path = ctx.getContextPath();
		this.contDespachado = true;
		FacesContext.getCurrentInstance().getExternalContext().redirect(path + "/contenedor");
	}

	public void crearShippingNotification(PickingTicket pt, List<DespachoContenedor> despCon) {
		logger.info("[crearShippingNotification] Creando archivo");
		StringBuffer archivo = new StringBuffer();
		Integer total = 0;
		DespachoContenedor dcAux =  new DespachoContenedor();
		if(despCon.isEmpty()) {
			dcAux.setTipoRecepcion("AXIS");
			dcAux.setCosecha(pt.getLotNumber());
			total = pt.getOrderQuantity();
		}else {
			for (DespachoContenedor dc : despCon) {

				etiquetaService.despacharEtiqueta(dc.getId());
				total = total +  dc.getCajasConsolidas();
				dcAux = dc;
			}
		}

		if (dcAux.getTipoRecepcion().equals("AXIS")) {
			archivo.append(Constantes.SPSP_SHIPMENT);
			archivo.append(Constantes.SPCR_SEAPORT);
			archivo.append(new SimpleDateFormat("yyyyMMdd").format(new Date()) + "*"); // fecha
			archivo.append(Util.rellenarEspacios(Constantes.SPRC_ORDER_REF, pt.getOrdenReference()));
			archivo.append(Util.rellenarEspacios(Constantes.SPRC_ORDER_REF, pt.getShipmentId()));
			archivo.append(Util.rellenarEspacios(Constantes.SPRC_PO, pt.getSalesOrderId()));
			archivo.append(Util.rellenarEspacios(5, pt.getSalesOrderline()));
			archivo.append(Util.rellenarEspacios(Constantes.SPCR_CANT_ESP_PRODUCTO, pt.getWarehouseId()));
			archivo.append(Util.rellenarEspacios(35, dcAux.getVina()));
			archivo.append(Util.rellenarEspacios(Constantes.SPCR_CANT_ESP_PRODUCTO, pt.getShipToCustomerId()));// producto
																												// esperado
			archivo.append(Util.rellenarEspacios(Constantes.SPCR_CANT_ESP_PRODUCTO, pt.getProductNumber()));// Producto
																											// recibido
			archivo.append(Util.rellenarEspacios(Constantes.SPCR_CANT_ESP_ESTADO, "GOOD"));// estado
			archivo.append(
					Util.rellenarEspaciosIzq(Constantes.SPCR_CANT_ESP_CANTAJUSTES, total.toString()));// cantidad
																														// recibida
			archivo.append(new SimpleDateFormat("yyyyMMdd").format(this.contenedor.getFechaEmbarque()) + "*"); // fecha
																												// recepcion
			archivo.append(Util.rellenarEspacios(Constantes.SPCR_CANT_ESP_LOTE, dcAux.getCosecha()));// Lote
			archivo.append(Util.rellenarEspacios(15, ""));// Dato sin uso
			archivo.append(Util.rellenarEspacios(5, ""));// Dato sin uso
			archivo.append("\n");// salto de linea
		}

		if (archivo.length() > 0) {
			logger.info("[crearShippingNotification] CONTENIDO ARCHIVO: ");
			logger.info(archivo.toString());
			String nombreTxt = Constantes.PREFIJO_ARCHIVO_SHIPMENT + this.obtenerSecuenciaSPCR() + ".txt";
			try {
				conectaSFtp.subeArchivoOut(new ByteArrayInputStream(archivo.toString().getBytes()), nombreTxt);

				Mensaje mensaje = new Mensaje();
				mensaje.setUsuarioCr(loginBean.getUsername());
				mensaje.setFechaCr(new Date());
				mensaje.setTipo(Constantes.ARCHIVO_SHIPMENT);
				mensaje.setNombreArchivo(nombreTxt);
				mensaje.setMensaje(archivo.toString());
				poService.guardarMensaje(mensaje);

			} catch (SocketException e1) {
				logger.info("[crearShippingNotification - SocketException] error " + e1);
				e1.printStackTrace();
			} catch (IOException e2) {
				logger.info("[crearShippingNotification - IOException] error " + e2);
				e2.printStackTrace();
			}
		}

	}

	private String obtenerSecuenciaSPCR() {

		String sec = this.ajusteService.secuenciaSPSP();

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

	public void cargarListaPt(List<DespachoContenedor> listaPo) {
		// string para clausula "in" en la query
		if (!listaPo.isEmpty()) {

			String vinas = "";
			String nrosProductos = "";
			Integer indiceV = 0;
			Integer indiceP = 0;

			StringBuilder inVina = new StringBuilder().append("'");
			StringBuilder inProducto = new StringBuilder().append("'");

			List<String> listaVinas = new ArrayList<String>();
			List<String> listaProd = new ArrayList<String>();

			this.cajas = 0;

			if (!listaPo.isEmpty()) {
				for (DespachoContenedor po : listaPo) {
					if (!listaVinas.contains(po.getVina())) {
						listaVinas.add(po.getVina());
					}

					if (!listaProd.contains(po.getProducto())) {
						listaProd.add(po.getNumeroProducto());
					}

					this.cajas = this.cajas + po.getCajasConsolidas();

				}

				this.pallets = listaPo.size();

			}

			if (!listaVinas.isEmpty()) {
				indiceV = 1;
				for (String vina : listaVinas) {
					if (vina != null) {
						if (indiceV == listaVinas.size()) {
							inVina.append(vina).append("'");
						} else {
							inVina.append(vina).append("' , '");
						}
					}
					indiceV++;
				}
			}

			if (!listaProd.isEmpty()) {
				indiceP = 1;
				for (String prod : listaProd) {
					if (prod != null) {
						if (indiceP == listaProd.size()) {
							inProducto.append(prod).append("'");
						} else {
							inProducto.append(prod).append("' , '");
						}
					}
					indiceP++;
				}
			}

			vinas = inVina.toString();
			nrosProductos = inProducto.toString();

			this.listaPt = contServ.buscarPt(vinas, nrosProductos);

		}
	}

	/**
	 * Método encargado de cargar un contenedor.
	 * 
	 * @return RowEditEvent
	 */
	public void cargaPt(RowEditEvent event) {
		this.PtSelected = new PickingTicket();
		this.PtSelected = (PickingTicket) event.getObject();
		// PrimeFaces.current().ajax().update("ptForm");
		PrimeFaces.current().executeScript("bloquearScroll()");
		PrimeFaces.current().executeScript("PF('confimaeditPt').show()");

	}

	public void actualizarPt() {
		ptService.updatePt(this.PtSelected);
		PrimeFaces.current().executeScript("PF('confimaeditPt').hide()");
		PrimeFaces.current().executeScript(Util.alerta("Actualizacion Realizada",
				"El Picking Ticket N°" + this.PtSelected.getSalesOrderId() + " ha sido Actualizado", "success"));
		logger.info("[DespachoBean - actualizarPt ] El Picking Ticket N° " + this.PtSelected.getSalesOrderId()
				+ " ha sido Actualizado");
	}

	public List<PickingTicket> getListaPt() {
		return listaPt;
	}

	public void setListaPt(List<PickingTicket> listaPt) {
		this.listaPt = listaPt;
	}

	public Map<Integer, String> getListaCont() {
		return listaCont;
	}

	public void setListaCont(Map<Integer, String> listaCont) {
		this.listaCont = listaCont;
	}

	public int getSelectedCont() {
		return selectedCont;
	}

	public void setSelectedCont(int selectedCont) {
		this.selectedCont = selectedCont;
	}

	public Container getContenedor() {
		return contenedor;
	}

	public void setContenedor(Container contenedor) {
		this.contenedor = contenedor;
	}

	public List<DespachoContenedor> getListContDespacho() {
		return listContDespacho;
	}

	public void setListContDespacho(List<DespachoContenedor> listContDespacho) {
		this.listContDespacho = listContDespacho;
	}

	public PickingTicket getPtSelected() {
		return PtSelected;
	}

	public void setPtSelected(PickingTicket ptSelected) {
		PtSelected = ptSelected;
	}

	public Integer getCajas() {
		return cajas;
	}

	public void setCajas(Integer cajas) {
		this.cajas = cajas;
	}

	public Integer getPallets() {
		return pallets;
	}

	public void setPallets(Integer pallets) {
		this.pallets = pallets;
	}

	public List<DespachoContenedor> getcDSelected() {
		return cDSelected;
	}

	public void setcDSelected(List<DespachoContenedor> cDSelected) {
		this.cDSelected = cDSelected;
	}

	public List<String> getDocumentos() {
		return documentos;
	}

	public void setDocumentos(List<String> documentos) {
		this.documentos = documentos;
	}

	public boolean isContDespachado() {
		return contDespachado;
	}

	public void setContDespachado(boolean contDespachado) {
		this.contDespachado = contDespachado;
	}

	public Integer getCajasSelected() {
		return cajasSelected;
	}

	public void setCajasSelected(Integer cajasSelected) {
		this.cajasSelected = cajasSelected;
	}

}
