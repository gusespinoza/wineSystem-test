package com.marval.wine.bean;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.primefaces.PrimeFaces;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.marval.wine.entity.MensajeProcesado;
import com.marval.wine.ftp.ConectaFtp;
import com.marval.wine.service.ContainerService;
import com.marval.wine.service.MensajesService;
import com.marval.wine.util.Constantes;

@ManagedBean
@SessionScope
@Component
@PropertySource(Constantes.RUTA_PROPERTIES)
public class MensajesProcesadosBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(MensajesProcesadosBean.class);

	@Value("${db.ftp.excel.contenedor}")
	String URL_EXCEL_CONT;

	@Autowired
	public ContainerService contService;
	
	@Autowired
	public MensajesService mensajesService;

	@Autowired
	private LoginBean loginBean;

	@Autowired
	ConectaFtp conectaFtp;

	private String po;
	private String vinaSelec;
	private String tipoMensaje;

	private List<MensajeProcesado> listaMensajes = new ArrayList<MensajeProcesado>();

	
	private Date fechaDesde;
	private Date fechaHasta;
	private List<String> listaVinas = new ArrayList<String>();
	
	private StreamedContent streamedContent;

	/**
	 * @throws IOException 
	 * 
	 */
	public void onLoad() throws IOException {
		if (loginBean.isMenuMensajes()) {
			if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) {
				return;
			}
			logger.info("[MensajesProcesadosBean - onLoad] inicio");
			this.listaMensajes.clear();
			this.listaVinas.clear();
			this.vinaSelec = "";
			this.fechaDesde = null;
			this.fechaHasta = null;
			this.po = "";
			
		} else {
			ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			String path = ctx.getContextPath();
			FacesContext.getCurrentInstance().getExternalContext().redirect(path + "/index.xhtml");
		}

	}

	public List<String> autoCompletarVinas(String valor) {

		try {
			this.listaVinas .clear();
			this.listaVinas.addAll(this.contService.consultarListaVinas(valor));

			return this.listaVinas;

		} catch (Exception e) {
			logger.info("[autoCompletaEtiquetaAlm - error] error " + e);
			return null;
		}

	}
	
	public List<String> autocompletarPO(String valor) {

		try {
			this.listaVinas .clear();
			this.listaVinas.addAll(this.contService.consultarListaPOenAjustes(valor));

			return this.listaVinas;

		} catch (Exception e) {
			logger.info("[autoCompletaEtiquetaAlm - error] error " + e);
			return null;
		}

	}

	public void buscarMensajes() {
		try {
			logger.info("[buscarMensajes] buscar  ");

			if ((this.po == null || this.po.equals(""))
					&& (this.vinaSelec == null || this.vinaSelec.equals(""))
					&& (this.fechaDesde == null)) {
				PrimeFaces.current().executeScript(
						"Swal.fire(\"Filtros Mandatorios\", \"Debe ingresar un filtro de búsqueda.\", \"info\");");
				return;
			}

			this.listaMensajes.clear();
			this.listaMensajes.addAll(this.mensajesService.buscarMensajesProcesados(this.tipoMensaje, this.fechaDesde, this.fechaHasta));

			if (this.listaMensajes == null || this.listaMensajes.isEmpty()) {
				PrimeFaces.current().executeScript("Swal.fire(\"Sin resultados\", \"No existen resultados para los filtros ingresados.\", \"info\");");
				return;
			}

		} catch (Exception e) {
			PrimeFaces.current().executeScript(
					"Swal.fire(\"Problemas al buscar información\", \"Existieron problemas al cursar la búsqueda, "
							+ "favor vuelva a intentar.\", \"error\");");
			logger.info("[buscarResumen - error] error " + e);
		}
	}
	
	
	public void descargarTXT(MensajeProcesado mens) {

		try {

			this.streamedContent = new DefaultStreamedContent(
					new ByteArrayInputStream(mens.getTexto().getBytes(StandardCharsets.UTF_8)), "txt",
					mens.getArchivo());

			PrimeFaces.current().executeScript("document.getElementById('formDescarga:descargaArchivo').click();");

			return;

		} catch (Exception e) {
			PrimeFaces.current().executeScript(
					"Swal.fire(\"Problemas al buscar información\", \"Existieron problemas al buscar la información, "
							+ "favor vuelva a intentar.\", \"error\");");
			logger.info("[descargarTXT - error] error " + e);
		}

	}

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

	public String getVinaSelec() {
		return vinaSelec;
	}

	public void setVinaSelec(String vinaSelec) {
		this.vinaSelec = vinaSelec;
	}

	public List<MensajeProcesado> getListaMensajes() {
		return listaMensajes;
	}

	public void setListaMensajes(List<MensajeProcesado> listaMensajes) {
		this.listaMensajes = listaMensajes;
	}

	public Date getFechaDesde() {
		return fechaDesde;
	}

	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}

	public Date getFechaHasta() {
		return fechaHasta;
	}

	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}

	public String getPo() {
		return po;
	}

	public void setPo(String po) {
		this.po = po;
	}

	public StreamedContent getStreamedContent() {
		return streamedContent;
	}

	public void setStreamedContent(StreamedContent streamedContent) {
		this.streamedContent = streamedContent;
	}

	public String getTipoMensaje() {
		return tipoMensaje;
	}

	public void setTipoMensaje(String tipoMensaje) {
		this.tipoMensaje = tipoMensaje;
	}

}
