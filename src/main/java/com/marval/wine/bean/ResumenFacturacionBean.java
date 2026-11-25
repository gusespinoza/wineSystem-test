package com.marval.wine.bean;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.servlet.ServletContext;

import com.marval.wine.entity.ConsultarEtiqueta;
import com.marval.wine.entity.ResumenContenedor;
import com.marval.wine.entity.ResumenFacturacion;
import com.marval.wine.entity.ResumenNaveViaje;
import com.marval.wine.ftp.ConectaFtp;
import com.marval.wine.report.ReporteExcel;
import com.marval.wine.service.ContainerService;
import com.marval.wine.service.ResumenFacturacionService;
import com.marval.wine.util.Constantes;

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

@ManagedBean
@SessionScope
@Component
@PropertySource(Constantes.RUTA_PROPERTIES)
public class ResumenFacturacionBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(ResumenFacturacionBean.class);

	@Value("${db.ftp.excel.contenedor}")
	String URL_EXCEL_CONT;

	@Autowired
	public ContainerService contService;
	
	@Autowired
	public ResumenFacturacionService facService;

	@Autowired
	private LoginBean loginBean;

	@Autowired
	ConectaFtp conectaFtp;

	private String etiqueta;
	private String naveSelec;
	private String viajeSelec;
	private String vinaSelec;

	private List<ResumenFacturacion> listaResumen = new ArrayList<ResumenFacturacion>();
	private ConsultarEtiqueta etiquetaData = new ConsultarEtiqueta();

	private List<String> listaVinas = new ArrayList<String>();
	private List<String> listaNaves = new ArrayList<String>();
	private List<String> listaViajesNave = new ArrayList<String>();
	
	private List<ResumenContenedor> resumenCont = new ArrayList<ResumenContenedor>();
	private List<ResumenNaveViaje> resumenNav = new ArrayList<ResumenNaveViaje>();
	
	private ResumenFacturacion resumenFac = new ResumenFacturacion();

	/**
	 * @throws IOException 
	 * 
	 */
	public void onLoad() throws IOException {
		if (loginBean.isMenuFacturacion()) {
			if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) {
				return;
			}
			logger.info("[ResumenFacturacionBean - onLoad] inicio");
			this.listaResumen.clear();
			this.etiqueta = "";
			this.naveSelec = "";
			this.viajeSelec = "";
			this.vinaSelec = "";
			
			this.resumenFac = new ResumenFacturacion();
			this.resumenCont.clear();
			this.listaResumen.clear();
			
		} else {
			ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			String path = ctx.getContextPath();
			FacesContext.getCurrentInstance().getExternalContext().redirect(path + "/index.xhtml");
		}

	}

	public List<String> autoCompletaEtiqueta() {

		List<String> listaEtiquetas = new ArrayList<String>();

		try {

			listaEtiquetas.addAll(this.contService.autoCompletaEtiquetaAlm(this.etiqueta));
			listaEtiquetas.addAll(this.contService.autoCompletaEtiquetaRecep(this.etiqueta));

			return listaEtiquetas;

		} catch (Exception e) {
			logger.info("[autoCompletaEtiquetaAlm - error] error " + e);
			return null;
		}

	}

	public List<String> autoCompletaEtiquetaAlm() {

		try {

			return this.contService.autoCompletaEtiquetaAlm(this.etiqueta);

		} catch (Exception e) {
			logger.info("[autoCompletaEtiquetaAlm - error] error " + e);
			return null;
		}

	}

	public List<String> autoCompletaEtiquetaRecep() {

		try {

			return this.contService.autoCompletaEtiquetaRecep(this.etiqueta);

		} catch (Exception e) {
			logger.info("[autoCompletaEtiquetaRecep - error] error " + e);
			return null;
		}
	}
	
	public List<String> autoCompletarViajesNave(String valor) {

		try {

			return this.contService.autoCompletarViajesNave(valor, this.naveSelec);

		} catch (Exception e) {
			logger.info("[autoCompletarViajesNave - error] error " + e);
			return null;
		}

	}
	
	public List<String> autoCompletarNaves(String valor) {

		try {

			return this.contService.autoCompletarNaves(valor);

		} catch (Exception e) {
			logger.info("[autoCompletarNaves - error] error " + e);
			return null;
		}

	}


	public void buscarResumen() {
		try {
			logger.info("[buscarResumen] buscar nave " + this.naveSelec);

			if ((this.naveSelec == null || this.naveSelec.equals("")) ||
					(this.viajeSelec == null || this.viajeSelec.equals(""))) {
				PrimeFaces.current().executeScript(
						"Swal.fire(\"Filtros Mandatorios\", \"Debe ingresar nave y viaje.\", \"info\");");
				return;
			}

			this.listaResumen.clear();
			this.listaResumen.addAll(this.contService.buscarResumenFacturacion(this.naveSelec, this.viajeSelec));

			if (this.listaResumen == null || this.listaResumen.isEmpty()) {
				PrimeFaces.current().executeScript("Swal.fire(\"Sin resultados\", \"Sin resultados.\", \"info\");");
				return;
			}

		} catch (Exception e) {
			PrimeFaces.current().executeScript(
					"Swal.fire(\"Problemas al buscar nave\", \"Existieron problemas al cursar búsqueda de la nave, "
							+ "favor vuelva a intentar.\", \"error\");");
			logger.info("[buscarResumen - error] error " + e);
		}
	}
	
	
	public void exportarResumen(ResumenFacturacion res) {
		
		try {
			
			this.resumenFac = res;
			this.resumenCont.clear();
			this.resumenCont.addAll(this.facService.resumenFacturacionContainer(res, this.naveSelec, this.viajeSelec));
			
			PrimeFaces.current().executeScript("$('#formResumen\\\\:descExcel').click()");
			
			
		} catch (Exception e) {
			PrimeFaces.current().executeScript(
					"Swal.fire(\"Problemas al exportar nave\", \"Existieron problemas al cursar búsqueda de la nave, "
							+ "favor vuelva a intentar.\", \"error\");");
			logger.info("[exportarResumen - error] error " + e);
		}
	}
	

	public void exportarResumenNaveViaje() {
		
		try {
			
			
			PrimeFaces.current().executeScript("$('#formResumen\\\\:descExcel').click()");
			
			
		} catch (Exception e) {
			PrimeFaces.current().executeScript(
					"Swal.fire(\"Problemas al exportar nave\", \"Existieron problemas al cursar búsqueda de la nave, "
							+ "favor vuelva a intentar.\", \"error\");");
			logger.info("[exportarResumen - error] error " + e);
		}
	}
	
	/**
	 * Método encargado de generar excel.
	 * @return StreamedContent
	 * @throws ParseException
	 */
	public StreamedContent generarReporteContenedor() throws ParseException {

		ReporteExcel g = new ReporteExcel();
		FacesContext context = FacesContext.getCurrentInstance();
		
		if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
			return new DefaultStreamedContent();
		} else {
			return new DefaultStreamedContent(new ByteArrayInputStream(g.crearExcelResumenFacturacion(this.resumenCont, this.resumenFac, this.naveSelec, this.viajeSelec)),
					"text/xls", "RES_" + this.resumenFac.getContainer() + "_" + this.vinaSelec + "_" + new SimpleDateFormat("dd-MM-yyyy").format(new Date()) + ".xls");
		}
	}
	
	/**
	 * Método encargado de generar excel.
	 * @return StreamedContent
	 * @throws ParseException
	 */
	public StreamedContent generarReporteNaveViaje() throws ParseException {

		ReporteExcel g = new ReporteExcel();
		FacesContext context = FacesContext.getCurrentInstance();
		
		if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
			return new DefaultStreamedContent();
		} else {
			return new DefaultStreamedContent(new ByteArrayInputStream(
					g.crearExcelResumenFacturacionNaveViaje(this.resumenCont, this.resumenNav, this.naveSelec, this.viajeSelec)),
					"text/xls", "RES_" + this.naveSelec + "_" + this.viajeSelec + "_" + new SimpleDateFormat("dd-MM-yyyy").format(new Date()) + ".xls");
		}
	}
	
	
	public void calcularResumenNaveViaje() {
		
		try {
			
			logger.info("[ResumenFacturacionBean - calcularResumenNaveViaje] inicio");
			
			this.resumenNav.clear();
			
			for (ResumenFacturacion res : this.listaResumen) {
				
				this.resumenCont.clear();
				this.resumenCont.addAll(this.facService.resumenFacturacionContainer(res, this.naveSelec, this.viajeSelec));
				
				logger.info("[ResumenFacturacionBean - calcularResumenNaveViaje] cont " + res.getContainer());
				
				this.resumenNav.add(sumaContenedorNave(res.getContainer(), this.resumenCont));
			}
			
			logger.info("[ResumenFacturacionBean - calcularResumenNaveViaje] fin");
			
			
			
			PrimeFaces.current().executeScript("$('#formResumen\\\\:descExcelNaveViaje').click()");
			
			
			
		} catch (Exception e) {
			PrimeFaces.current().executeScript(
					"Swal.fire(\"Problemas al exportar nave\", \"Existieron problemas al cursar búsqueda de la nave, "
							+ "favor vuelva a intentar.\", \"error\");");
			logger.info("[exportarResumen - error] error " + e);
		}
	}

	private ResumenNaveViaje sumaContenedorNave(String cont, List<ResumenContenedor> resCont) {
		
		try {

			logger.info("[ResumenFacturacionBean - sumaContenedorNave] ini " + cont);

			BigDecimal alm = BigDecimal.ZERO;
			BigDecimal air = BigDecimal.ZERO;
			BigDecimal port = BigDecimal.ZERO;
			BigDecimal rec = BigDecimal.ZERO;
			BigDecimal rep = BigDecimal.ZERO;
			BigDecimal slip = BigDecimal.ZERO;
			BigDecimal tran = BigDecimal.ZERO;

			for (ResumenContenedor resu : resCont) {
				

				alm = alm.add((resu.getAlmacenamientoJFH() == null) ? BigDecimal.ZERO : resu.getAlmacenamientoJFH());
				alm = alm.add((resu.getAlmacenamientoVina() == null) ? BigDecimal.ZERO : resu.getAlmacenamientoVina());
				air = air.add((resu.getAirbags() == null) ? BigDecimal.ZERO : resu.getAirbags());
				port = port.add((resu.getPorteo() == null) ? BigDecimal.ZERO : resu.getPorteo());
				rec = rec.add((resu.getRecepcion() == null) ? BigDecimal.ZERO : resu.getRecepcion());
				rep = rep.add((resu.getRepaletizacion() == null) ? BigDecimal.ZERO : resu.getRepaletizacion());
				slip = slip.add((resu.getSlipsheet() == null) ? BigDecimal.ZERO : resu.getSlipsheet());
				tran = tran.add((resu.getTransporte() == null) ? BigDecimal.ZERO : resu.getTransporte());
			}

			ResumenNaveViaje res = new ResumenNaveViaje();
			res.setShip(this.naveSelec);
			res.setVoyage(this.viajeSelec);
			res.setContainer(cont);

			res.setAlmacenamiento(alm);
			res.setAirbags(air);
			res.setGeteout(BigDecimal.ZERO);
			res.setPorteo(port);
			res.setRecepcion(rec);
			res.setRepaletizacion(rep);
			res.setSlipsheet(slip);
			res.setTransporte(tran);

			return res;

		} catch (Exception e) {
			// TODO: handle exception
			return null;
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

	public ConsultarEtiqueta getEtiquetaData() {
		return etiquetaData;
	}

	public void setEtiquetaData(ConsultarEtiqueta etiquetaData) {
		this.etiquetaData = etiquetaData;
	}

	public List<ResumenFacturacion> getListaResumen() {
		return listaResumen;
	}

	public void setListaResumen(List<ResumenFacturacion> listaOrdenes) {
		this.listaResumen = listaOrdenes;
	}

	public List<String> getListaVinas() {
		return listaVinas;
	}

	public void setListaVinas(List<String> listaVinas) {
		this.listaVinas = listaVinas;
	}

	public List<String> getListaNaves() {
		return listaNaves;
	}

	public void setListaNaves(List<String> listaNaves) {
		this.listaNaves = listaNaves;
	}

	public List<String> getListaViajesNave() {
		return listaViajesNave;
	}

	public void setListaViajesNave(List<String> listaViajesNave) {
		this.listaViajesNave = listaViajesNave;
	}

	public String getNaveSelec() {
		return naveSelec;
	}

	public void setNaveSelec(String naveSelec) {
		this.naveSelec = naveSelec;
	}

	public String getViajeSelec() {
		return viajeSelec;
	}

	public void setViajeSelec(String viajeSelec) {
		this.viajeSelec = viajeSelec;
	}

	public String getVinaSelec() {
		return vinaSelec;
	}

	public void setVinaSelec(String vinaSelec) {
		this.vinaSelec = vinaSelec;
	}

	public List<ResumenContenedor> getResumenCont() {
		return resumenCont;
	}

	public void setResumenCont(List<ResumenContenedor> resumenCont) {
		this.resumenCont = resumenCont;
	}

	public ResumenFacturacion getResumenFac() {
		return resumenFac;
	}

	public void setResumenFac(ResumenFacturacion resumenFac) {
		this.resumenFac = resumenFac;
	}

	public List<ResumenNaveViaje> getResumenNav() {
		return resumenNav;
		
	}

	public void setResumenNav(List<ResumenNaveViaje> resumenNav) {
		this.resumenNav = resumenNav;
		
	}

	

	
}
