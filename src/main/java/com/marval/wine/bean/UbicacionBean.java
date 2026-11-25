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
import org.primefaces.event.RowEditEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.marval.wine.entity.Ubicacion;
import com.marval.wine.service.ContainerService;
import com.marval.wine.service.UbicacionService;
import com.marval.wine.util.Constantes;

@ManagedBean
@SessionScope
@Component
@PropertySource(Constantes.RUTA_PROPERTIES)
public class UbicacionBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(UbicacionBean.class);

	
	@Value("${db.ftp.excel.contenedor}")
	String URL_EXCEL_CONT;
	
	@Autowired
	public ContainerService contService;
	
	@Autowired
	public UbicacionService ubiService;
	
	@Autowired
	private LoginBean loginBean;

	private String etiqueta;
	private String pasillo;
	private String altura;
	private String posicion;
	
	private List<Ubicacion> listaEtiquetas = new ArrayList<Ubicacion>();
	private List<String> etiquetasUbi = new ArrayList<String>();
	private Ubicacion ubi = new Ubicacion();
	
	/**
	 * @throws IOException 
	 * 
	 */
	public void onLoad() throws IOException {
		if (loginBean.isMenuUbicacion()) {
			if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) {
				return;
			}
			logger.info("[UbicacionBean - onLoad] inicio");
			this.listaEtiquetas.clear();
			this.etiquetasUbi.clear();
			this.etiqueta = "";
			this.pasillo = "";
			this.altura = "";
			this.posicion = "";
			
		} else {
			ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			String path = ctx.getContextPath();
			FacesContext.getCurrentInstance().getExternalContext().redirect(path + "/index.xhtml");
		}
		
	
	}
	
	/**
	 * 
	 */
	public void onLoadAsignar() {
		if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) {
			return;
		}

		logger.info("[UbicacionBean - onLoadAsignar] inicio");
		this.listaEtiquetas.clear();
		this.etiquetasUbi.clear();
		
		//this.etiquetasUbi.addAll(this.contService.etiquetasRecepcionadas());
		
		this.etiqueta = "";
		this.pasillo = "";
		this.altura = "";
		this.posicion = "";
	}

	public List<String> autoCompletaEtiquetaAlm(String valor) {
		
		try {
			
			return this.contService.autoCompletaEtiquetaAlm(valor);
			
		} catch (Exception e) {
			logger.info("[autoCompletaEtiquetaAlm - error] error " + e);
			return null;
		}
		
		
	}
	
	public List<String> autoCompletaEtiquetaRecep(String valor){
		
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
			
			if(this.etiqueta == null || this.etiqueta.equals("")) {
				PrimeFaces.current().executeScript(
						"Swal.fire(\"Filtros Mandatorios\", \"Debe ingresar una etiqueta.\", \"warning\");");
				return;
			}
			this.listaEtiquetas.clear();
			this.listaEtiquetas.addAll(this.contService.buscarEtiquetasAlmacenadas(this.etiqueta));
			
			if(this.listaEtiquetas == null || this.listaEtiquetas.isEmpty()) {
				PrimeFaces.current().executeScript(
						"Swal.fire(\"Sin resultados\", \"Sin resultados.\", \"info\");");
				return;
			}
			
		} catch (Exception e) {
			PrimeFaces.current().executeScript(
					"Swal.fire(\"Problemas al buscar etiqueta\", \"Existieron problemas al cursar búsqueda de etiqueta, "
					+ "favor vuelva a intentar.\", \"error\");");
			logger.info("[buscar - error] error " + e);
		}
	}
	
	public void modificarUbicacion(RowEditEvent event) {
		
		Ubicacion ubicacion = (Ubicacion) event.getObject();
		
		try {
			logger.info("[modificarUbicacion] etiqueta " + ubicacion.getEtiqueta());
			
			if((ubicacion.getPasillo() == null || ubicacion.getPasillo().equals(""))
					|| (ubicacion.getAltura() == null || ubicacion.getAltura().equals(""))
					|| (ubicacion.getPosicion() == null || ubicacion.getPosicion().equals(""))) {
				PrimeFaces.current().executeScript(
						"Swal.fire(\"Campos Mandatorios\", \"Debe ingresar todos los campos.\", \"warning\");");
				return;
			}
			
			ubicacion.setUsuarioUp(loginBean.getUsername());
			ubicacion.setFechaUp(new Date());
			
			String etiqGuardada = this.ubiService.guardarAsignacion(ubicacion);
			if(etiqGuardada != null && !etiqGuardada.equals("")) {
				PrimeFaces.current().executeScript(
						"Swal.fire(\"Posición ocupada\", \"La posición ingresada actualmente está siendo ocupada por la etiqueta "+etiqGuardada+"\", \"info\");");
				return;
			}
			
			PrimeFaces.current().executeScript(
					"Swal.fire(\"Reubicación asignada\", \"La nueva ubicación ha sido guardada.\", \"success\");");
			return;
			
		} catch (Exception e) {
			PrimeFaces.current().executeScript(
					"Swal.fire(\"Problemas al buscar etiqueta\", \"Existieron problemas al guardar etiqueta, "
					+ "favor vuelva a intentar.\", \"error\");");
			logger.info("[guardarAsignacion - error] error " + e);
		}
	}
	
	public void guardarAsignacion() {
		
		try {
			logger.info("[guardarAsignacion] etiqueta " + this.etiqueta);
			
			if((this.etiqueta == null || this.etiqueta.equals(""))
					|| (this.pasillo == null || this.pasillo.equals(""))
					|| (this.altura == null || this.altura.equals(""))
					|| (this.posicion == null || this.posicion.equals(""))) {
				PrimeFaces.current().executeScript(
						"Swal.fire(\"Campos Mandatorios\", \"Todos los campos son mandatorios.\", \"info\");");
				return;
			}
			
			Ubicacion ubicacion = this.contService.buscarEtiqueta(this.etiqueta);
			ubicacion.setUsuarioUp(loginBean.getUsername());
			ubicacion.setFechaUp(new Date());
			ubicacion.setPasillo(this.pasillo);
			ubicacion.setAltura(this.altura);
			ubicacion.setPosicion(this.posicion);
			
			String etiqGuardada = this.ubiService.guardarAsignacion(ubicacion);
			
			if(etiqGuardada != null && !etiqGuardada.equals("")) {
				PrimeFaces.current().executeScript(
						"Swal.fire(\"Posición ocupada\", \"La posición ingresada actualmente está siendo ocupada por la etiqueta "+etiqGuardada+"\", \"info\");");
				return;
			}
			
			//TODO pasar estado de recepcionada a almacenada
			this.contService.actualizarPOAlmacenada(ubicacion);
			
			PrimeFaces.current().executeScript(
					"Swal.fire(\"Ubicación asignada\", \"La ubicación asignada ha sido guardada.\", \"success\");");
			
			
			this.limpiarAsignacion();
			
			return;
			
		} catch (Exception e) {
			PrimeFaces.current().executeScript(
					"Swal.fire(\"Problemas al buscar etiqueta\", \"Existieron problemas al guardar etiqueta, "
					+ "favor vuelva a intentar.\", \"error\");");
			logger.info("[guardarAsignacion - error] error " + e);
		}
	}
	
	
	
	private void limpiarAsignacion() {
		this.pasillo = "";
		this.altura = "";
		this.posicion = "";
		this.etiqueta = "";
		
		this.etiquetasUbi.clear();
		this.etiquetasUbi.addAll(this.contService.etiquetasRecepcionadas());
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

	public List<Ubicacion> getListaEtiquetas() {
		return listaEtiquetas;
	}

	public void setListaEtiquetas(List<Ubicacion> listaEtiquetas) {
		this.listaEtiquetas = listaEtiquetas;
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

	public Ubicacion getUbi() {
		return ubi;
	}

	public void setUbi(Ubicacion ubi) {
		this.ubi = ubi;
	}

	public List<String> getEtiquetasUbi() {
		return etiquetasUbi;
	}

	public void setEtiquetasUbi(List<String> etiquetasUbi) {
		this.etiquetasUbi = etiquetasUbi;
	}
	
}
