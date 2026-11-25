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

import com.marval.wine.entity.AjusteInventario;
import com.marval.wine.entity.Mensaje;
import com.marval.wine.entity.MensajeBodega;
import com.marval.wine.ftp.ConectaSFTP;
import com.marval.wine.service.AjusteInventarioService;
import com.marval.wine.service.ContainerService;
import com.marval.wine.service.PurchaseOrderService;
import com.marval.wine.util.Constantes;
import com.marval.wine.util.Util;

@ManagedBean
@SessionScope
@Component
public class MensajeBodegaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(MensajeBodegaBean.class);

	@Autowired
	PurchaseOrderService poService;

	@Autowired
	AjusteInventarioService ajusteService;

	@Autowired
	private LoginBean loginBean;

	@Autowired
	ContainerService contServ;

	@Autowired
	ConectaSFTP conectaSFtp;

	private List<MensajeBodega> listaMensaje = new ArrayList<MensajeBodega>();
	private List<AjusteInventario> listaAjustes = new ArrayList<AjusteInventario>();
	private Integer cantidadConchaToro = 0;
	private Integer cantidadEmiliana = 0;
	private Integer cantidadConoSur = 0;
	private Integer cantidadTotal = 0;
	private boolean envioMensaje = false;
	private boolean envioAjuste = false;

	public void onLoad() throws IOException {
		if (loginBean.isMenuDespacho()) {
			if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) {
				return;
			}
			logger.info("[MensajeBodegaBean - onLoad] inicio");
			this.listaMensaje.clear();
			this.listaAjustes.clear();
			this.cantidadConchaToro = 0;
			this.cantidadConoSur = 0;
			this.cantidadEmiliana = 0;
			this.cantidadTotal = 0;
			this.envioMensaje = false;
			this.envioAjuste = false;
		} else {
			ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			String path = ctx.getContextPath();
			FacesContext.getCurrentInstance().getExternalContext().redirect(path + "/index.xhtml");
		}

	}

	public void crearAjuste() {
		// traer datos de ajustes del dia
		this.listaAjustes = poService.listaAjustes();
		// crear nombre de mensaje
		if (!this.listaAjustes.isEmpty()) {
			String nombreTxt = Constantes.PREFIJO_ARCHIVO_AJUSTE_INVENTARIO + this.obtenerSecuenciaSPCR() + ".txt";
			StringBuffer archivo = new StringBuffer();
			// iterar lineas para crear el menaje
			for (AjusteInventario ai : listaAjustes) {
				archivo.append(ai.getTexto());
				archivo.append("\n");// salto de linea
			}
			// asignar nombre de mensaje a todos los ajuste de inventario
			poService.nombreAjustes(nombreTxt);

			// se crea mensaje
			Mensaje men = new Mensaje();
			men.setFechaCr(new Date());
			men.setMensaje(archivo.toString());
			men.setNombreArchivo(nombreTxt);
			men.setUsuarioCr(loginBean.getUsername());
			men.setTipo(Constantes.ARCHIVO_AJUSTE_INVENTARIO);
			
			try {
				//conectaSFtp.creaDirectorio("/AEP/WINE/AXIS/out/AJUSTEINVENTARIO/", new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
				conectaSFtp.subeArchivoOut(new ByteArrayInputStream(archivo.toString().getBytes()), nombreTxt);
			} catch (SocketException e1) {
				logger.info("[modificarCantidad - SocketException] error " + e1);
				e1.printStackTrace();
			} catch (IOException e2) {
				logger.info("[modificarCantidad - IOException] error " + e2);
				e2.printStackTrace();
			}
			poService.guardarMensaje(men);
			cargarInfo();
			this.listaAjustes.clear();
			PrimeFaces.current().ajax().update("formBusqueda:tablaResult:panelTabla");
			PrimeFaces.current()
			.executeScript("Swal.fire('Ajustes enviados con exito', '',  'success');");
		}else {
			this.listaAjustes.clear();
		}

	}

	public void crearInventoryRecon() {

		logger.info("[crearInventoryRecon] Creando archivo");
		// TODO datos recepcion
		StringBuffer archivo = new StringBuffer();
		this.listaMensaje = new ArrayList<MensajeBodega>();
		this.listaMensaje.addAll(poService.mensajeInventario());
		if (!this.listaMensaje.isEmpty()) {
			for (MensajeBodega mb : this.listaMensaje) {
				archivo.append(Constantes.SPST_INVENTORYRECON);
				archivo.append(Constantes.SPCR_SEAPORT);
				archivo.append(new SimpleDateFormat("yyyyMMdd").format(new Date()) + "*"); // fecha
				archivo.append(Constantes.SPCR_SEAPORT);
				archivo.append(Util.rellenarEspacios(35, mb.getVina()));
				archivo.append(Util.rellenarEspacios(Constantes.SPCR_CANT_ESP_PRODUCTO, mb.getCodProducto()));
				archivo.append(Util.rellenarEspacios(40, mb.getCosecha()));
				archivo.append(Util.rellenarEspacios(10, mb.getEstado()));
				archivo.append(Util.rellenarEspaciosIzq(7, mb.getCantidad().toString()));
				archivo.append("\n");// salto de linea
			}

			logger.info("[crearInventoryRecon] CONTENIDO ARCHIVO: ");
			logger.info(archivo.toString());
			String nombreTxt = Constantes.PREFIJO_ARCHIVO_INVENTORYRECON + this.obtenerSecuenciaSPST() + ".txt";

			try {
				conectaSFtp.subeArchivoOut(new ByteArrayInputStream(archivo.toString().getBytes()), nombreTxt);

				Mensaje mensaje = new Mensaje();
				mensaje.setUsuarioCr(loginBean.getUsername());
				mensaje.setFechaCr(new Date());
				mensaje.setTipo(Constantes.ARCHIVO_INVENTORYRECON);
				mensaje.setNombreArchivo(nombreTxt);
				mensaje.setMensaje(archivo.toString());
				poService.guardarMensaje(mensaje);
				cargarInfo();
				PrimeFaces.current().ajax().update("formBusqueda:tablaResult:panelTabla");
				PrimeFaces.current().executeScript("Swal.fire('Archivo creado correctamente', '',  'success');");

			} catch (SocketException e1) {
				logger.info("[crearInventoryRecon - SocketException] error " + e1);
				e1.printStackTrace();
			} catch (IOException e2) {
				logger.info("[crearInventoryRecon - IOException] error " + e2);
				e2.printStackTrace();
			}
		} else {
			PrimeFaces.current()
					.executeScript("Swal.fire('ERROR', 'El sistema no encontro carga almacenada',  'error');");
		}

	}

	private String obtenerSecuenciaSPCR() {

		String sec = this.ajusteService.secuenciaSPCR();

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

	private String obtenerSecuenciaSPST() {

		String sec = this.ajusteService.secuenciaSPST();

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

	public void cargarInfo() {
		this.listaMensaje.clear();
		this.listaMensaje.addAll(poService.mensajeInventario());
		contadorCosecha();
		this.envioAjuste = validarEnvioAjuste();
		if (this.envioAjuste) {
			this.envioMensaje = validarEnvio();
		}
	}

	public boolean validarEnvio() {
		return poService.validarInventoryRecon();
	}

	public boolean validarEnvioAjuste() {
		return poService.validarInventoryAjust();
	}

	public void contadorCosecha() {
		this.cantidadConchaToro = 0;
		this.cantidadConoSur = 0;
		this.cantidadEmiliana = 0;
		this.cantidadTotal = 0;
		if (!this.listaMensaje.isEmpty()) {
			for (MensajeBodega mb : listaMensaje) {
				this.cantidadTotal = this.cantidadTotal + mb.getCantidad();

				if (mb.getVina().equals("CONCHA Y TORO")) {
					this.cantidadConchaToro = this.cantidadConchaToro + mb.getCantidad();
				}

				if (mb.getVina().equals("Vina Cono Sur")) {
					this.cantidadConoSur = this.cantidadConoSur + mb.getCantidad();
				}

				if (mb.getVina().equals("VINEDOS EMILIANA")) {
					this.cantidadEmiliana = this.cantidadEmiliana + mb.getCantidad();
				}
			}
		}
	}

	public List<MensajeBodega> getListaMensaje() {
		return listaMensaje;
	}

	public void setListaMensaje(List<MensajeBodega> listaMensaje) {
		this.listaMensaje = listaMensaje;
	}

	public List<AjusteInventario> getListaAjustes() {
		return listaAjustes;
	}

	public void setListaAjustes(List<AjusteInventario> listaAjustes) {
		this.listaAjustes = listaAjustes;
	}

	public Integer getCantidadConchaToro() {
		return cantidadConchaToro;
	}

	public void setCantidadConchaToro(Integer cantidadConchaToro) {
		this.cantidadConchaToro = cantidadConchaToro;
	}

	public Integer getCantidadEmiliana() {
		return cantidadEmiliana;
	}

	public void setCantidadEmiliana(Integer cantidadEmiliana) {
		this.cantidadEmiliana = cantidadEmiliana;
	}

	public Integer getCantidadConoSur() {
		return cantidadConoSur;
	}

	public void setCantidadConoSur(Integer cantidadConoSur) {
		this.cantidadConoSur = cantidadConoSur;
	}

	public Integer getCantidadTotal() {
		return cantidadTotal;
	}

	public void setCantidadTotal(Integer cantidadTotal) {
		this.cantidadTotal = cantidadTotal;
	}

	public boolean isEnvioMensaje() {
		return envioMensaje;
	}

	public void setEnvioMensaje(boolean envioMensaje) {
		this.envioMensaje = envioMensaje;
	}

	public boolean isEnvioAjuste() {
		return envioAjuste;
	}

	public void setEnvioAjuste(boolean envioAjuste) {
		this.envioAjuste = envioAjuste;
	}

}
