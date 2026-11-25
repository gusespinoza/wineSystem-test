package com.marval.wine.bean;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
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

import com.marval.wine.entity.Existencia;
import com.marval.wine.ftp.ConectaFtp;
import com.marval.wine.report.ReporteExcel;
import com.marval.wine.service.ContainerService;
import com.marval.wine.service.ExistenciaService;
import com.marval.wine.util.Constantes;

@ManagedBean
@SessionScope
@Component
@PropertySource(Constantes.RUTA_PROPERTIES)
public class ExistenciaCargaBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(ExistenciaCargaBean.class);

	@Value("${db.ftp.excel.contenedor}")
	String URL_EXCEL_CONT;

	@Autowired
	public ContainerService contService;
	
	@Autowired
	public ExistenciaService existenciaService;

	@Autowired
	private LoginBean loginBean;

	@Autowired
	ConectaFtp conectaFtp;

	private String etiqueta;
	private String pasillo;
	private String altura;
	private String posicion;

	private String cantAnt;
	private String cantNew;

	private String vinaSelec;
	private String orden;

	private List<String> listaVinas = new ArrayList<String>();
	private List<String> listaPOS = new ArrayList<String>();
	private List<Existencia> listaCargas = new ArrayList<Existencia>();
	private List<Existencia> listaCargasAux = new ArrayList<Existencia>();
	private Date fechaDesde;
	private Date fechaHasta;

	private String fert;
	private String po;
	
	Date fechaMaxima;
	

	/**
	 * @throws IOException 
	 * 
	 */
	public void onLoad() throws IOException {
		if (loginBean.isMenuExistencia()) {
			if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) {
				return;
			}
			logger.info("[ContenedorBean - onLoad] inicio");
			this.listaVinas.clear();
			this.listaCargas.clear();
			
			this.orden = "";
			this.vinaSelec = "";
			this.fechaDesde = null;
			this.fechaHasta = null;
			
			this.fert = "";
			this.po = "";
			this.fechaMaxima = null;
			
		} else {
			ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			String path = ctx.getContextPath();
			FacesContext.getCurrentInstance().getExternalContext().redirect(path + "/index.xhtml");
		}

		
	}

	public List<String> autoCompletarVinas(String valor) {

		try {
			this.listaVinas.clear();
			this.listaVinas.addAll(this.contService.consultarListaVinas(valor));

			return this.listaVinas;

		} catch (Exception e) {
			logger.info("[autoCompletaEtiquetaAlm - error] error " + e);
			return null;
		}

	}
	
	public List<String> autocompletarPOS(String valor) {

		try {
			this.listaPOS.clear();
			this.listaPOS.addAll(this.contService.consultarListaPOS(valor));

			return this.listaPOS;

		} catch (Exception e) {
			logger.info("[autocompletarPOS - error] error " + e);
			return null;
		}

	}


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

	public void buscarCargas() {

		try {

			logger.info("[buscarCargas] buscar existencia para vina " + this.vinaSelec);

			if (this.orden == null || this.orden.equals("")) {
				PrimeFaces.current().executeScript(
						"Swal.fire(\"Filtros Mandatorios\", \"Debe ingresar orden de la búsqueda.\", \"warning\");");
				return;
			}

			this.listaCargas.clear();
			this.listaCargasAux.clear();
			this.listaCargas.addAll(this.existenciaService.consultarCargasPorVina(this.vinaSelec, this.orden, this.fechaDesde,
					this.fechaHasta, this.fert, this.po));
			this.listaCargasAux.addAll(this.listaCargas);

		} catch (Exception e) {
			logger.info("[buscarCargas] error " + e);
		}
	}
	
	
	/**
	 * Método encargado de generar excel.
	 * @return StreamedContent
	 * @throws ParseException
	 */
	public StreamedContent generarExcelExistencia() throws ParseException {

		ReporteExcel g = new ReporteExcel();
		FacesContext context = FacesContext.getCurrentInstance();
		
		if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
			return new DefaultStreamedContent();
		} else {
			return new DefaultStreamedContent(new ByteArrayInputStream(g.crearExcelExistencia(this.listaCargas)),
					"text/xls", "EC_" + this.vinaSelec + "_" + new SimpleDateFormat("dd-MM-yyyy").format(new Date()) + ".xls");
		}
	}
	
	public void fechaMax() {
		
		if(this.fechaDesde != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(this.fechaDesde);
			calendar.add(Calendar.DAY_OF_YEAR, 365);
			this.fechaMaxima = calendar.getTime();
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

	public List<String> getListaVinas() {
		return listaVinas;
	}

	public void setListaVinas(List<String> listaVinas) {
		this.listaVinas = listaVinas;
	}

	public String getVinaSelec() {
		return vinaSelec;
	}

	public void setVinaSelec(String vinaSelec) {
		this.vinaSelec = vinaSelec;
	}

	public List<Existencia> getListaCargas() {
		return listaCargas;
	}

	public void setListaCargas(List<Existencia> listaCargas) {
		this.listaCargas = listaCargas;
	}

	public String getOrden() {
		return orden;
	}

	public void setOrden(String orden) {
		this.orden = orden;
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

	public String getFert() {
		return fert;

	}

	public void setFert(String fert) {
		this.fert = fert;

	}

	public String getPo() {
		return po;
		
	}

	public void setPo(String po) {
		this.po = po;
		
	}

	public List<String> getListaPOS() {
		return listaPOS;
	}

	public void setListaPOS(List<String> listaPOS) {
		this.listaPOS = listaPOS;
	}

	public Date getFechaMaxima() {
		return fechaMaxima;
	}

	public void setFechaMaxima(Date fechaMaxima) {
		this.fechaMaxima = fechaMaxima;
	}

	public List<Existencia> getListaCargasAux() {
		return listaCargasAux;
	}

	public void setListaCargasAux(List<Existencia> listaCargasAux) {
		this.listaCargasAux = listaCargasAux;
	}
	
	
	
}
