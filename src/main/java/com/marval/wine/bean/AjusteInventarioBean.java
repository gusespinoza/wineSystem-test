package com.marval.wine.bean;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.primefaces.PrimeFaces;
import org.primefaces.event.CellEditEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.marval.wine.entity.AjusteInventario;
import com.marval.wine.entity.DetalleInventario;
import com.marval.wine.entity.PoAjuste;
import com.marval.wine.service.AjusteInventarioService;
import com.marval.wine.service.ContainerService;
import com.marval.wine.service.PurchaseOrderService;
import com.marval.wine.util.Constantes;
import com.marval.wine.util.Util;

@ManagedBean
@SessionScope
@Component
@PropertySource(Constantes.RUTA_PROPERTIES)
public class AjusteInventarioBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(AjusteInventarioBean.class);

	@Value("${db.ftp.excel.contenedor}")
	String URL_EXCEL_CONT;

	@Autowired
	public ContainerService contService;

	@Autowired
	public AjusteInventarioService ajusteService;

	@Autowired
	private LoginBean loginBean;

	@Autowired
	PurchaseOrderService poService;

	private String etiqueta;
	private String pasillo;
	private String altura;
	private String posicion;

	private String cantAnt;
	private String cantNew;

	private List<PoAjuste> listaPos = new ArrayList<PoAjuste>();
	private PoAjuste poSel = new PoAjuste();
	private DetalleInventario detalle = new DetalleInventario();

	private String producto;
	private String cosecha;

	private List<String> listaVinas = new ArrayList<String>();

	/**
	 * 
	 */
	public void onLoad() {

		if (loginBean.isMenuAjusteInventario()) {
			if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) {
				return;
			}
			logger.info("[ContenedorBean - onLoad] inicio");
			this.listaPos.clear();

			this.etiqueta = "";
			this.pasillo = "";
			this.altura = "";
			this.posicion = "";

			this.cantAnt = "";
			this.cantNew = "";

			this.producto = "";
			this.setCosecha("");
			this.poSel = new PoAjuste();

		} else {
			ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			String path = ctx.getContextPath();
			try {
				FacesContext.getCurrentInstance().getExternalContext().redirect(path + "/index.xhtml");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * 
	 * @return
	 */
	public List<String> autoCompletaEtiquetaAlm(String valor) {

		try {

			return this.contService.autoCompletaEtiquetaAlm(valor);

		} catch (Exception e) {
			logger.info("[autoCompletaEtiquetaAlm - error] error " + e);
			return null;
		}

	}

	public List<String> autoCompletaEtiquetaRecep(String valor) {

		try {

			return this.contService.autoCompletaEtiquetaRecep(valor);

		} catch (Exception e) {
			logger.info("[autoCompletaEtiquetaRecep - error] error " + e);
			return null;
		}
	}

	public void buscar() {
		try {
			logger.info("[buscar] buscar etiqueta " + this.etiqueta);

			if (this.producto == null || this.producto.equals("")) {
				PrimeFaces.current().executeScript(
						"Swal.fire(\"Filtros Mandatorios\", \"Debe seleccionar una viña.\", \"warning\");");
				return;
			}
			this.poSel = new PoAjuste();
			this.listaPos.clear();
			this.listaPos.addAll(this.ajusteService.buscarPos(this.producto, this.cosecha));

			if (this.listaPos == null || this.listaPos.isEmpty()) {
				PrimeFaces.current().executeScript("Swal.fire(\"Sin resultados\", \"Sin resultados.\", \"info\");");
				return;
			}

		} catch (Exception e) {
			PrimeFaces.current().executeScript(
					"Swal.fire(\"Problemas al buscar etiqueta\", \"Existieron problemas al cursar búsqueda de etiqueta, "
							+ "favor vuelva a intentar.\", \"error\");");
			logger.info("[buscar - error] error " + e);
		}
	}

	public List<String> autoCompletarProductNumbers(String valor) {

		try {
			this.listaVinas.clear();
			this.listaVinas.addAll(this.ajusteService.consultarProductNumbers(valor));

			return this.listaVinas;

		} catch (Exception e) {
			logger.info("[autoCompletaEtiquetaAlm - error] error " + e);
			return null;
		}

	}

	public void editaCantidad(CellEditEvent event) {
		try {
			this.poSel = (PoAjuste) event.getSource();
			this.cantAnt = event.getOldValue().toString();
			this.cantNew = event.getNewValue().toString();
		} catch (Exception e) {

		}
	}

	public void confirmarAjuste() {

		if (this.poSel == null) {
			PrimeFaces.current().executeScript("Swal.fire(\"Error al cargar datos\", \"error.\", \"warning\");");
			return;
		}

		if (this.poSel.getCantidad() == null
				|| (this.poSel.getTipoAjuste() == null || this.poSel.getTipoAjuste().equals(""))
				|| (this.poSel.getMotivo() == null || this.poSel.getMotivo().equals(""))) {
			PrimeFaces.current().executeScript(
					"Swal.fire(\"Campos Mandatorios\", \"Debe ingresar todos los campos.\", \"warning\");");
			return;
		}
		
//		if ((this.poSel.getCantidad() > this.poSel.getStock()) && this.poSel.getTipoAjuste().equals("-")) {
//			PrimeFaces.current().executeScript(
//					"Swal.fire(\"Advertencia\", \"La cantidad a rebajar no puede ser mayor al stock actual.\", \"warning\");");
//			return;
//		}
		
//		if(this.poSel.getStock() < (this.poSel.getStock() + ((this.poSel.getTotalAjustes() != null) ? this.poSel.getTotalAjustes().intValue() : 0))) {
//			
//			PrimeFaces.current().executeScript(
//					"Swal.fire(\"Advertencia\", \"Se requiere generar rebajas en las etiquetas.\", \"warning\");");
//			return;
//		}

		// TODO validar cantidad máxima de inventario

		logger.info("[confirmarAjuste] consultar detalle inventario etiqueta");
		this.detalle = new DetalleInventario();
		this.detalle = this.ajusteService.consultarDetalleInventario(this.producto, this.cosecha);

		logger.info("[confirmarAjuste] cantidad ajustes -> " + detalle.getCantidadAjustes());
		logger.info("[confirmarAjuste] nueva cantidad -> " + detalle.getNuevaCantidad());

		String dialog = "Swal.fire({";
		dialog += "title: \"Confirmación\",";
		dialog += " text: \"Se realizará un movimiento de " + this.poSel.getTipoAjuste() + this.poSel.getCantidad()
				+ " cajas. Producto; " + this.poSel.getProducto_id() + ". Cosecha;" + this.poSel.getCosecha()
				+ " . ¿Desea continuar? \",";
		dialog += " type: 'warning',";
		dialog += " showCancelButton: true,";
		dialog += " confirmButtonColor: '#3085d6',";
		dialog += " cancelButtonColor: '#d33',";
		dialog += " confirmButtonText: 'Confirmar movimiento',";
		dialog += " cancelButtonText: 'Cancelar'";
		dialog += " }).then((result) => {";
		dialog += " if (result.value) {";
		dialog += "  mod();";
		dialog += " } else { habilitarScroll(); }})";

		PrimeFaces.current().executeScript(dialog);

	}

	public void ajustar(PoAjuste ajuste) {

		try {

			this.poSel = ajuste;

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void modificarCantidad() {

		try {

			AjusteInventario ajuste = new AjusteInventario();
			ajuste.setUsuarioCr(loginBean.getUsername());
			ajuste.setFechaCr(new Date());

			ajuste.setProduct_number(this.producto);
			ajuste.setVintage(this.poSel.getCosecha());
			ajuste.setLote("");

			ajuste.setCantidadAnterior(this.poSel.getStock().toString());
			ajuste.setRegistro_po(null);
			ajuste.setMotivo(this.poSel.getMotivo());

			if (this.poSel.getTipoAjuste().equals("+")) {
				ajuste.setCantidadNueva(String.valueOf((this.poSel.getStock() + this.poSel.getCantidad())));
			} else {
				ajuste.setCantidadNueva(String.valueOf((this.poSel.getStock() - this.poSel.getCantidad())));
			}

			// TODO datos ajuste
			StringBuffer archivo = new StringBuffer();
			archivo.append(Constantes.SPCR_INVENTORY_ADJUST);
			archivo.append(Constantes.SPCR_SEAPORT);
			archivo.append(new SimpleDateFormat("yyyyMMdd").format(new Date()) + "*"); // fecha
			archivo.append(Constantes.SPCR_SEAPORT);// sistema
			archivo.append(Util.rellenarEspacios(Constantes.SPCR_CANT_ESP_VINA, this.poSel.getVina()));// viña
			archivo.append(Util.rellenarEspacios(Constantes.SPCR_CANT_ESP_PRODUCTO, this.poSel.getProducto_id()));// producto
			archivo.append(Util.rellenarEspacios(Constantes.SPCR_CANT_ESP_LOTE, this.poSel.getCosecha()));// lote
			archivo.append(Util.rellenarEspacios(Constantes.SPCR_CANT_ESP_ESTADO,
					(this.poSel.getEstadoRecep().equals("Buen estado")) ? "GOOD" : "BAD"));// estado
			
			//recepcion
//			if (detalle.getCantidadAjustes() != null) {
//				// cantidad de veces q se ha ajustado el inventario
//				archivo.append(Util.rellenarEspaciosIzq(Constantes.SPCR_CANT_ESP_CANTAJUSTES,
//						detalle.getCantidadAjustes().toString()));
//			} else {
//				archivo.append(Util.rellenarEspaciosIzq(Constantes.SPCR_CANT_ESP_CANTAJUSTES, ""));
//			}
			
			
			archivo.append(Util.rellenarEspaciosIzq(Constantes.SPCR_CANT_ESP_CANTAJUSTES,
					this.poSel.getCantidad().toString()));

			// cantidad obtenida después del movimiento (+ o -)
			/*
			 * Integer cantidadajuste = Integer.parseInt(ajuste.getCantidadNueva());
			 * 
			 * if (detalle.getNuevaCantidad() == null) { if (Integer.parseInt(this.cantAnt)
			 * > Integer.parseInt(this.cantNew)) { archivo.append("*-*"); } else {
			 * archivo.append("*+*"); } } else if ((cantidadajuste >
			 * detalle.getNuevaCantidad().intValue())) { archivo.append("*+*"); } else {
			 * archivo.append("*-*"); }
			 */

			archivo.append(this.poSel.getTipoAjuste() + "*");
			archivo.append(new SimpleDateFormat("yyyyMMdd").format(new Date()) + "*"); // fecha
			archivo.append(Util.rellenarEspacios(Constantes.SPCR_CANT_ESP_OBS, this.poSel.getMotivo()));

			// this.contService.actualizarCantidadPODetalle(this.eti,
			// this.poSel.getCantidad());
			// TODO consultar donde se actualiza la cantidad

			logger.info("[modificarCantidad] ajuste guardado");
			logger.info("[modificarCantidad] purchase order cantidad detalle actualizado");
			logger.info("[modificarCantidad] sube archivo txt al ftp");
			logger.info("[modificarCantidad] TXT");
			logger.info("[modificarCantidad] ***");
			logger.info("[modificarCantidad] ***");
			logger.info(archivo.toString());
			logger.info("[modificarCantidad] ***");
			logger.info("[modificarCantidad] ***");

//			String nombreTxt = Constantes.PREFIJO_ARCHIVO_AJUSTE_INVENTARIO + this.obtenerSecuenciaSPCR() + ".txt";
//			ajuste.setNombreTxt(nombreTxt);
			ajuste.setTexto(archivo.toString());

//			Mensaje men = new Mensaje();
//			men.setFechaCr(new Date());
//			men.setMensaje(archivo.toString());
//			men.setNombreArchivo(nombreTxt);
//			men.setUsuarioCr(loginBean.getUsername());
//			men.setTipo(Constantes.ARCHIVO_AJUSTE_INVENTARIO);

//			logger.info("[modificarCantidad] *** SUBE ARCHIVO");
//			try {
//				conectaFtp.creaDirectorio("/AEP/WINE/AXIS/out/AJUSTEINVENTARIO/",
//						new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
//				conectaFtp.subeArchivoOut(new ByteArrayInputStream(archivo.toString().getBytes()), nombreTxt);
//			} catch (SocketException e1) {
//				logger.info("[modificarCantidad - SocketException] error " + e1);
//				e1.printStackTrace();
//			} catch (IOException e2) {
//				logger.info("[modificarCantidad - IOException] error " + e2);
//				e2.printStackTrace();
//			}

			logger.info("[modificarCantidad] *** ARCHIVO ARRIBA");
			logger.info("[modificarCantidad] *** " + archivo.toString());

//			this.poService.guardarMensaje(men);
			this.ajusteService.guardarAjusteInventario(ajuste);
			
			
			this.listaPos.clear();

			PrimeFaces.current().executeScript("PF('rf').hide();");

			PrimeFaces.current().executeScript(
					"Swal.fire(\"Ajuste realizado\", \"El ajuste de inventario ha sido realizado exitosamente.\", \"success\");");

			this.poSel = new PoAjuste();

			return;

		} catch (Exception e) {
			PrimeFaces.current().executeScript(
					"Swal.fire(\"Problemas al buscar etiqueta\", \"Existieron problemas al guardar etiqueta, "
							+ "favor vuelva a intentar.\", \"error\");");
			logger.info("[modificarCantidad - error] error " + e);
		}
	}


	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

	public String getEtiqueta() {
		return etiqueta;
	}

	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}

	public List<PoAjuste> getListaPos() {
		return listaPos;
	}

	public void setListaPos(List<PoAjuste> listaPos) {
		this.listaPos = listaPos;
	}

	public String getPasillo() {
		return pasillo;
	}

	public void setPasillo(String pasillo) {
		this.pasillo = pasillo;
	}

	public String getAltura() {
		return altura;
	}

	public void setAltura(String altura) {
		this.altura = altura;
	}

	public String getPosicion() {
		return posicion;
	}

	public void setPosicion(String posicion) {
		this.posicion = posicion;
	}

	public String getCantAnt() {
		return cantAnt;
	}

	public void setCantAnt(String cantAnt) {
		this.cantAnt = cantAnt;
	}

	public String getCantNew() {
		return cantNew;
	}

	public void setCantNew(String cantNew) {
		this.cantNew = cantNew;
	}

	public DetalleInventario getDetalle() {
		return detalle;
	}

	public void setDetalle(DetalleInventario detalle) {
		this.detalle = detalle;
	}

	public String getCosecha() {
		return cosecha;
	}

	public void setCosecha(String cosecha) {
		this.cosecha = cosecha;
	}

	public String getProducto() {
		return producto;
	}

	public void setProducto(String producto) {
		this.producto = producto;
	}

	public PoAjuste getPoSel() {
		return poSel;
	}

	public void setPoSel(PoAjuste poSel) {
		this.poSel = poSel;
	}

	public List<String> getListaVinas() {
		return listaVinas;
	}

	public void setListaVinas(List<String> listaVinas) {
		this.listaVinas = listaVinas;
	}
}
