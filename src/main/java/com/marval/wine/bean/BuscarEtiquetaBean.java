package com.marval.wine.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import com.marval.wine.entity.ConsultarEtiqueta;
import com.marval.wine.entity.Ubicacion;
import com.marval.wine.ftp.ConectaFtp;
import com.marval.wine.service.ContainerService;
import com.marval.wine.util.Constantes;
import com.marval.wine.util.Util;

import org.primefaces.PrimeFaces;
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
public class BuscarEtiquetaBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(BuscarEtiquetaBean.class);

	
	@Value("${db.ftp.excel.contenedor}")
	String URL_EXCEL_CONT;
	
	@Autowired
	public ContainerService contService;
	
	@Autowired
	private LoginBean loginBean;
	
	@Autowired
	ConectaFtp conectaFtp;

	private String etiqueta;
	private String pasillo;
	private String altura;
	private String posicion;
	
	private List<Ubicacion> listaEtiquetas = new ArrayList<Ubicacion>();
	private ConsultarEtiqueta etiquetaData = new ConsultarEtiqueta();
	private List<ConsultarEtiqueta> etiquetas = new ArrayList<ConsultarEtiqueta>();
	private List<ConsultarEtiqueta> etiquetasAux = new ArrayList<ConsultarEtiqueta>();
	private List<ConsultarEtiqueta> etiquetasCheck = new ArrayList<ConsultarEtiqueta>();
	private List<String> listaVinas = new ArrayList<String>();
	
	private String obs;
	private String vina;
	private String numeroDoc;
	private String tipoDoc;
	
	private String motivoAnulacion;
	
	
	/**
	 * @throws IOException 
	 * 
	 */
	public void onLoad() throws IOException {
		if (loginBean.isMenuBuscaEtiqueta()) {
			if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) {
				return;
			}
			logger.info("[BuscarEtiquetaBean - onLoad] inicio");
			this.listaEtiquetas.clear();
			this.etiqueta = "";
			this.pasillo = "";
			this.altura = "";
			this.posicion = "";
			this.obs = "";
			
			this.etiquetaData = new ConsultarEtiqueta();
			this.etiquetas.clear();
			this.etiquetasCheck.clear();
			this.etiquetasAux.clear();
			
			this.vina = "";
			this.tipoDoc = "";
			this.numeroDoc  = null;
			
		} else {
			ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			String path = ctx.getContextPath();
			FacesContext.getCurrentInstance().getExternalContext().redirect(path + "/index.xhtml");
		}

	}
	
	public List<String> autoCompletaEtiqueta(String valor){
		
		List<String> listaEtiquetas = new ArrayList<String>();
		
		try {
			
			listaEtiquetas.addAll(this.contService.autoCompletaEtiquetaAlm(valor));
			listaEtiquetas.addAll(this.contService.autoCompletaEtiquetaRecep(valor));
			
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
	
	public List<String> autoCompletaEtiquetaRecep(){
		
		try {
			
			return this.contService.autoCompletaEtiquetaRecep(this.etiqueta);
			
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
			this.listaEtiquetas.addAll(this.contService.buscarEtiquetas(this.etiqueta));
			
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
	
	public void cancelarEtiqueta() {
		
		try {
			
			this.contService.cancelarEtiqueta(this.etiquetaData);
			
			
			PrimeFaces.current().executeScript(
					"Swal.fire(\"Etiqueta cancelada\", \"La etiqueta "+this.etiquetaData.getEtiqueta()+" ha sido ANULADA.\", \"info\");");
			
			this.etiquetaData = new ConsultarEtiqueta();
			this.etiqueta = "";
			
			return;
			
		} catch (Exception e) {
			PrimeFaces.current().executeScript(
					"Swal.fire(\"Problemas al cancelar etiqueta\", \"Existieron problemas al cancelar la etiqueta, "
					+ "favor vuelva a intentar.\", \"error\");");
			logger.info("[cancelarEtiqueta - error] error " + e);
		}
	}
	
	public void confimarCancelacion() {
		
		try {
			
			logger.info("[cancelarEtiqueta] cancelar etiqueta " + this.etiquetaData.getEtiqueta());
			
			if(this.obs == null || this.obs.equals("")) {
				PrimeFaces.current().executeScript(
						"Swal.fire(\"Ingresar Etiqueta\", \"Debe ingresar una observación.\", \"warning\");");
				return;
			}
			
			
			String dialog = "Swal.fire({";
			dialog += "title: \"Confirmación\",";
			dialog += " text: \"La etiqueta " + this.etiqueta + " quedará ANULADA ¿Desea continuar?\",";
			dialog += " type: 'warning',";
			dialog += " showCancelButton: true,";
			dialog += " confirmButtonColor: '#3085d6',";
			dialog += " cancelButtonColor: '#d33',";
			dialog += " confirmButtonText: 'Confirmar',";
			dialog += " cancelButtonText: 'No'";
			dialog += " }).then((result) => {";
			dialog += " if (result.value) {";
			dialog += "  confirmar();PF('dlgAnular').hide();";
			dialog += " } else { habilitarScroll(); }})";

			PrimeFaces.current().executeScript(dialog);
			
		} catch (Exception e) {
			PrimeFaces.current().executeScript(
					"Swal.fire(\"Problemas al cancelar etiqueta\", \"Existieron problemas al cancelar la etiqueta, "
					+ "favor vuelva a intentar.\", \"error\");");
			logger.info("[cancelarEtiqueta - error] error " + e);
		}
		
		
	}
	
	
	
	//CONSULTAR ETIQUETAS impresiones
	
	public void buscarEtiqueta() {
		try {
			logger.info("[buscar] buscar etiqueta " + this.etiqueta);
			
			if(this.etiqueta == null || this.etiqueta.equals("")) {
				
				if((this.vina == null || this.vina.equals("")) && 
						(this.tipoDoc == null || this.tipoDoc.equals("")) &&
						(this.numeroDoc.equals(""))) {
					PrimeFaces.current().executeScript(
							"Swal.fire(\"Filtros Mandatorios\", \"Debe ingresar un filtro de búsqueda.\", \"warning\");");
					return;
				}
			} 
			this.etiquetas.clear();
			this.etiquetas.addAll(contService.consultarEtiqueta(this.etiqueta, this.vina, this.tipoDoc, this.numeroDoc));
			this.etiquetasAux.clear();
			this.etiquetasAux.addAll(generarQr(this.etiquetas));
			
			if(this.etiquetas.size() < 1) {
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
	
	
	public List<ConsultarEtiqueta> generarQr(List<ConsultarEtiqueta> etiquetas){
		List<ConsultarEtiqueta> nuevasEtiquetas = new ArrayList<ConsultarEtiqueta>();
		for(ConsultarEtiqueta et : etiquetas) {
			String qr = "";
			qr =  "Etiqueta: " + et.getEtiqueta() + "\n" + "Cog. Fert: " + et.getFert() + "\n"+ "Cant. Cajas: " + et.getCajasPorPalet() + "\n" + "Lote: " + et.getLote();
			et.setQr(qr);
			nuevasEtiquetas.add(et);
		}
		return nuevasEtiquetas;
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
	
	public void confirmarAnulacion() {
		
		String nota  = ((this.etiquetasCheck.size() > 1) ? "quedarán ANULADAS" : "quedará ANULADA");
		
		try {	
			String dialog = "Swal.fire({";
			dialog += "title: \"Confirmación\",";
			dialog += " text: \"La etiquetas " + this.listarEtiquetasPorAnular() + " " + nota + " ¿Desea continuar?\",";
			dialog += " type: 'warning',";
			dialog += " showCancelButton: true,";
			dialog += " confirmButtonColor: '#3085d6',";
			dialog += " cancelButtonColor: '#d33',";
			dialog += " confirmButtonText: 'Confirmar',";
			dialog += " cancelButtonText: 'Cancelar'";
			dialog += " }).then((result) => {";
			dialog += " if (result.value) {";
			dialog += "  confirmarCheck();";
			dialog += " } else { return; }})";

			PrimeFaces.current().executeScript(dialog);
			
		} catch (Exception e) {
			logger.info("[autoCompletaEtiquetaAlm - error] error " + e);
		}
	}
	
	private String listarEtiquetasPorAnular() {
		
		StringBuffer buf = new StringBuffer();
		buf.append(" ");
		
		for (ConsultarEtiqueta et : this.etiquetasCheck) {
			
			logger.info("[listarEtiquetasPorAnular - et] et " + et.getEtiqueta());
			buf.append("[ " + et.getEtiqueta() + " ]");
		}
		buf.append(" ");
		
		return buf.toString();
	}

	public String generaCod() {
		return Util.rellenarConCeros(12, this.etiquetaData.getPodId());
	}
	
	
	public void cancelarEtiquetasCheck() {

		try {
			
			if(this.etiquetasCheck.size() < 1) {
				PrimeFaces.current().executeScript("Swal.fire(\"Seleccione una etiqueta\", \"Debe seleccionar al menos una "
						+ "etiqueta.\", \"info\");");
				return;
			}
			
			if(this.motivoAnulacion == null || this.motivoAnulacion.equals("")) {
				PrimeFaces.current().executeScript("Swal.fire(\"Seleccione un motivo\", \"Debe seleccionar un motivo de anulación.\", \"info\");");
				return;
			}

			for (ConsultarEtiqueta et : this.etiquetasCheck) {
				
				et.setMotivoAnulacion(this.motivoAnulacion);
				this.contService.cancelarEtiqueta(et);
				logger.info("[cancelarEtiquetasCheck] etiqueta anulada " + et.getEtiqueta());
			}
			
			PrimeFaces.current().executeScript(
					"Swal.fire(\"Etiquetas anuladas.\", \"La etiquetas han sido ANULADAS correctamente.\", \"success\");");
			
			

			this.etiquetaData = new ConsultarEtiqueta();
			this.etiqueta = "";
			this.etiquetasCheck.clear();
			this.etiquetas.clear();

			return;

		} catch (Exception e) {
			PrimeFaces.current().executeScript(
					"Swal.fire(\"Problemas al cancelar etiqueta\", \"Existieron problemas al cancelar la etiqueta, "
							+ "favor vuelva a intentar.\", \"error\");");
			logger.info("[cancelarEtiqueta - error] error " + e);
		}
	}
	
	public void imprimir() {
		
		
		
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

	public ConsultarEtiqueta getEtiquetaData() {
		return etiquetaData;
	}

	public void setEtiquetaData(ConsultarEtiqueta etiquetaData) {
		this.etiquetaData = etiquetaData;
	}

	public String getObs() {
		return obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}

	public String getVina() {
		return vina;
	}

	public void setVina(String vina) {
		this.vina = vina;
	}

	public String getNumeroDoc() {
		return numeroDoc;
	}

	public void setNumeroDoc(String numeroDoc) {
		this.numeroDoc = numeroDoc;
	}

	public String getTipoDoc() {
		return tipoDoc;
	}

	public void setTipoDoc(String tipoDoc) {
		this.tipoDoc = tipoDoc;
	}

	public List<ConsultarEtiqueta> getEtiquetas() {
		return etiquetas;
		
	}

	public void setEtiquetas(List<ConsultarEtiqueta> etiquetas) {
		this.etiquetas = etiquetas;
		
	}

	public List<String> getListaVinas() {
		return listaVinas;
	}

	public void setListaVinas(List<String> listaVinas) {
		this.listaVinas = listaVinas;
	}

	public List<ConsultarEtiqueta> getEtiquetasCheck() {
		return etiquetasCheck;
		
	}

	public void setEtiquetasCheck(List<ConsultarEtiqueta> etiquetasCheck) {
		this.etiquetasCheck = etiquetasCheck;
		
	}

	public List<ConsultarEtiqueta> getEtiquetasAux() {
		return etiquetasAux;
	}

	public void setEtiquetasAux(List<ConsultarEtiqueta> etiquetasAux) {
		this.etiquetasAux = etiquetasAux;
	}

	public String getMotivoAnulacion() {
		return motivoAnulacion;
	}

	public void setMotivoAnulacion(String motivoAnulacion) {
		this.motivoAnulacion = motivoAnulacion;
	}
	
	
	
}
