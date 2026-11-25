package com.marval.wine.bean;

import java.io.IOException;
import java.io.Serializable;
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
import com.marval.wine.entity.PurchaseOrder;
import com.marval.wine.entity.PurchaseOrderDetalle;
import com.marval.wine.service.ContainerService;
import com.marval.wine.service.DetalleRecepcionService;
import com.marval.wine.service.EtiquetaService;
import com.marval.wine.service.PurchaseOrderService;
import com.marval.wine.util.Constantes;
import com.marval.wine.util.Util;

@ManagedBean
@SessionScope
@Component
public class RecepcionManualBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(RecepcionManualBean.class);

	@Autowired
	PurchaseOrderService poService;

	@Autowired
	DetalleRecepcionService detalleService;

	@Autowired
	EtiquetaService etiquetaService;

	@Autowired
	ContainerService contServ;
	
	@Autowired
	LoginBean loginBean;

	private List<String> documentos = Constantes.TIPO_DOC;
	private List<String> estados = Constantes.ESTADO_DETALLE;
	private List<String> slipSheet = Constantes.SLIPSHEET;
	private List<String> vinas = new ArrayList<String>();
	private List<PurchaseOrder> listaPo = new ArrayList<PurchaseOrder>();
	private List<PurchaseOrderDetalle> listaDetalle = new ArrayList<PurchaseOrderDetalle>();
	
	private boolean botonAgregar;
	private String totalParcial;
	private Integer unidadesRecep;
	private Integer totalDetalles =  0;
	
	private String selectedSs = "";

	private PurchaseOrder selectedPo;
	private PurchaseOrderDetalle selectedDetalle;

	private StringBuffer htmlEtiquetas;

	private Etiqueta etSelec;
	private List<Etiqueta> listEtiquetasSel = new ArrayList<Etiqueta>();

	public void onLoad() throws IOException {
		if (loginBean.isMenuRecepcionManual()) {
			if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) {
				return;
			}
			
			logger.info("[RecepcionBean - onLoad] inicio");
			
			this.listaPo.clear();
			this.listaPo = poService.listPoManual();
			limpiarFiltros();
			this.htmlEtiquetas = new StringBuffer();
			this.etSelec = new Etiqueta();
			this.listEtiquetasSel.clear();
			this.botonAgregar = false;
		} else {
			ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			String path = ctx.getContextPath();
			FacesContext.getCurrentInstance().getExternalContext().redirect(path + "/index.xhtml");
		}

	}

	public void limpiarFiltros() {
		this.selectedPo = new PurchaseOrder();
		this.selectedDetalle = new PurchaseOrderDetalle();
		this.vinas.clear();
		this.vinas.addAll(contServ.consultarListaVinas());
	}

	public void agregarPo() {
		if (this.selectedPo != null) {
			if (this.selectedSs.equals("SI")) {
				this.selectedPo.setSlipsheet(true);
			}
			this.selectedPo.setFechaCr(new Date());
			this.selectedPo.setEstado(Constantes.ESTADO_PO_DISPONIBLE);
			this.selectedPo.setTipoRecepcion("MANUAL");
			this.selectedPo.setWarehouseId("SEAPORT");
			this.selectedPo.setRecivedQuantity(this.selectedPo.getPurchaseOrderQuantity());
			this.selectedPo.setSupplierId(this.selectedPo.getOwnerId());
			this.selectedPo.setInterfaceId("PURCHASEORDER");

			poService.guardarPo(this.selectedPo);
			this.listaPo.clear();
			this.listaPo = poService.listPoManual();
			limpiarFiltros();
			this.htmlEtiquetas = new StringBuffer();
			this.etSelec = new Etiqueta();
			this.selectedSs = "";
			this.listEtiquetasSel.clear();
			PrimeFaces.current().ajax().update("poFormtbl:panelPo");
			PrimeFaces.current().executeScript(
					Util.alerta("Recepción Exitosa", "El P.O. manual se ha recepcionado correctamente.", "success"));
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

	public void crearEtiqueta() {

		int pallets = this.selectedDetalle.getPallet();

		for (int i = 0; i < pallets; i++) {
			Etiqueta etiqueta = new Etiqueta();
			etiqueta.setDetallePo(this.selectedDetalle);
			etiqueta.setEstado(Constantes.ESTADO_ETIQUETA_RECEPCIONADA);
			etiqueta.setFechaCr(new Date());
			etiqueta.setRepaletizado(false);
			etiqueta.setAsignado(false);
			etiqueta.setSlipsheet(this.selectedPo.getSlipsheet());
			etiqueta.setNroEtiqueta(etiquetaService.obtenerEtiqueta());
			etiqueta.setCajasPallet(this.selectedDetalle.getCajasPorPallet());
			etiqueta.setHistorialRepaletizado(etiqueta.getNroEtiqueta());
			this.selectedDetalle.getEtiquetas().add(etiqueta);
		}
	}

	public List<String> autoCompletarVinas(String valor) {

		try {
			this.vinas.clear();
			this.vinas.addAll(this.contServ.consultarListaVinas(valor));

			return this.vinas;

		} catch (Exception e) {
			logger.info("[autoCompletaEtiquetaAlm - error] error " + e);
			return null;
		}

	}
	
	public void agregarRecepcion() {

		this.selectedDetalle.setDescripcionProducto(this.selectedPo.getProductDescription());
		this.selectedDetalle.setCodProducto(this.selectedPo.getProductNumber());
		this.selectedDetalle.setCosecha(this.selectedPo.getVintage());

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
		}
		
		calcularTotal();

	}
	
	
	public void quitarRecepcion(PurchaseOrderDetalle detalle) {
		this.botonAgregar = false;
		PrimeFaces.current().ajax().update("recepcionForm:rfPanel");
		this.selectedPo.getDetalle().remove(detalle);
		etiquetaService.cancelarEtiqueta(detalle.getId());
		detalleService.eliminarDetalle(detalle.getId());
		calcularTotal();
	}
	
	

	public void cargaRecepcion(PurchaseOrder po) {
		calcularTotal();
		this.selectedPo = po;
		this.botonAgregar = false;
		this.listaDetalle.clear();
		this.listaDetalle.addAll(detalleService.listarDetalle(selectedPo.getId()));
		this.selectedPo.setDetalle(this.listaDetalle);
		this.selectedDetalle = new PurchaseOrderDetalle();
		if (!this.listaDetalle.isEmpty()) {
			for (PurchaseOrderDetalle detalle : this.listaDetalle) {
				if (detalle.getTotalParcial().equals("SI")) {
					this.botonAgregar = true;
				}
			}
		}
		PrimeFaces.current().ajax().update("recepcionForm:rfPanel");
		PrimeFaces.current().executeScript("PF('rf').show();");
		PrimeFaces.current().executeScript("bloquearScroll()");
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
			this.selectedPo.setEstado(Constantes.ESTADO_PO_RECEPCIONADO);
			this.selectedPo.setRecivedQuantity(this.unidadesRecep);
			poService.updatePo(this.selectedPo);
			PrimeFaces.current().ajax().update("recepcionForm:rfPanel");
			PrimeFaces.current().executeScript("PF('rf').hide();");
			PrimeFaces.current().executeScript("habilitarScroll()");
			PrimeFaces.current().executeScript(Util.alerta("Recepción Exitosa",
					"Se ha recepcionado el Purchase Order " + this.selectedPo.getPurchaseOrderId(), "success"));
		}

	}

	public List<String> autoCompletarCodProd() {
		logger.info("[RecepcionManualBean - autoCompletarCodProducto] inicio, Codigo: "
				+ this.selectedPo.getProductNumber());
		return poService.autoCompletarCodProd(this.selectedPo.getProductNumber());
	}

	public List<String> autoCompletarProd() {
		logger.info("[RecepcionManualBean - autoCompletarProducto] inicio, Nombre: "
				+ this.selectedPo.getProductDescription());
		return poService.autoCompletarNombreProd(this.selectedPo.getProductDescription());
	}
	
	public void completarCodigoDesc() {
		
		String [] valores = this.selectedPo.getProductNumber().split(" / ");
		
		this.selectedPo.setProductNumber(valores[0]);
		this.selectedPo.setProductDescription(valores[1]);
	}
	
	public void completarDescCodigo() {
		
		String [] valores = this.selectedPo.getProductDescription().split(" / ");
		
		this.selectedPo.setProductDescription(valores[0]);
		this.selectedPo.setProductNumber(valores[1]);
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
	
	
	
	

	public List<String> getDocumentos() {
		return documentos;
	}

	public void setDocumentos(List<String> documentos) {
		this.documentos = documentos;
	}

	public List<String> getSlipSheet() {
		return slipSheet;
	}

	public void setSlipSheet(List<String> slipSheet) {
		this.slipSheet = slipSheet;
	}

	public List<String> getVinas() {
		return vinas;
	}

	public void setVinas(List<String> vinas) {
		this.vinas = vinas;
	}

	public PurchaseOrder getSelectedPo() {
		return selectedPo;
	}

	public void setSelectedPo(PurchaseOrder selectedPo) {
		this.selectedPo = selectedPo;
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

	public PurchaseOrderDetalle getSelectedDetalle() {
		return selectedDetalle;
	}

	public void setSelectedDetalle(PurchaseOrderDetalle selectedDetalle) {
		this.selectedDetalle = selectedDetalle;
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

	public List<PurchaseOrder> getListaPo() {
		return listaPo;
	}

	public void setListaPo(List<PurchaseOrder> listaPo) {
		this.listaPo = listaPo;
	}

	public List<PurchaseOrderDetalle> getListaDetalle() {
		return listaDetalle;
	}

	public void setListaDetalle(List<PurchaseOrderDetalle> listaDetalle) {
		this.listaDetalle = listaDetalle;
	}

	public boolean isBotonAgregar() {
		return botonAgregar;
	}

	public void setBotonAgregar(boolean botonAgregar) {
		this.botonAgregar = botonAgregar;
	}

	public String getTotalParcial() {
		return totalParcial;
	}

	public void setTotalParcial(String totalParcial) {
		this.totalParcial = totalParcial;
	}

	public Integer getTotalDetalles() {
		return totalDetalles;
	}

	public void setTotalDetalles(Integer totalDetalles) {
		this.totalDetalles = totalDetalles;
	}
	
	

}