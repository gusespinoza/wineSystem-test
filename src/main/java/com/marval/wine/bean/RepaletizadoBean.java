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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.marval.wine.entity.ConsultarEtiqueta;
import com.marval.wine.entity.Etiqueta;
import com.marval.wine.entity.Repaletizado;
import com.marval.wine.ftp.ConectaFtp;
import com.marval.wine.service.ContainerService;
import com.marval.wine.service.EtiquetaService;
import com.marval.wine.service.RepaletizadoService;
import com.marval.wine.util.Constantes;

@ManagedBean
@SessionScope
@Component
@PropertySource(Constantes.RUTA_PROPERTIES)
public class RepaletizadoBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(RepaletizadoBean.class);

	
	@Value("${db.ftp.excel.contenedor}")
	String URL_EXCEL_CONT;
	
	@Autowired
	public ContainerService contService;
	
	@Autowired
	public RepaletizadoService repaService;
	
	@Autowired
	public EtiquetaService etService;
	
	@Autowired
	private LoginBean loginBean;
	
	@Autowired
	ConectaFtp conectaFtp;

	private String etiqueta;
	private String pasillo;
	private String altura;
	private String posicion;
	
	private List<Repaletizado> listaEtiquetas = new ArrayList<Repaletizado>();
	private ConsultarEtiqueta etiquetaData = new ConsultarEtiqueta();
	private Repaletizado repaletizadoSel = new Repaletizado();
	private Repaletizado repSecundario = new Repaletizado();
	
	private String etiquetaPrima;
	private String etiquetaSecun;
	private String accion;
	
	private Integer maxCajas;
	private Integer movimiento;
	
	private String mensajeCompleto;
	
	private Boolean etiSec;
	
	/**
	 * @throws IOException 
	 * 
	 */
	public void onLoad() throws IOException {
		if (loginBean.isMenuRepaletizado()) {
			if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) {
				return;
			}
			logger.info("[ContenedorBean - onLoad] inicio");
			this.listaEtiquetas.clear();
			this.etiqueta = "";
			this.pasillo = "";
			this.altura = "";
			this.posicion = "";
			
			this.etiquetaData = new ConsultarEtiqueta();
			this.maxCajas = 0;
			this.movimiento = 0;
			this.accion = "";
			this.etiquetaPrima = "";
			this.etiquetaSecun = "";
			this.repaletizadoSel = new Repaletizado();
			this.repSecundario = new Repaletizado();
			
			this.mensajeCompleto = "";
			this.etiSec = false;
			
		} else {
			ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			String path = ctx.getContextPath();
			FacesContext.getCurrentInstance().getExternalContext().redirect(path + "/index.xhtml");
		}

	}
	
	public List<String> autoCompletaEtiqueta(String valor){
		
		List<String> listaEtiquetas = new ArrayList<String>();
		
		try {
			
			listaEtiquetas.addAll(this.contService.autoCompletaEtiquetaAlmRepale(valor));
			listaEtiquetas.addAll(this.contService.autoCompletaEtiquetaRecepRepale(valor));
			
			return listaEtiquetas;
			
		} catch (Exception e) {
			logger.info("[autoCompletaEtiquetaAlm - error] error " + e);
			return null;
		}
		
	}
	
	public List<String> autoCompletaEtiquetaSecun(String valor){
		
		List<String> listaEtiquetas = new ArrayList<String>();
		
		try {
			
			listaEtiquetas.addAll(this.contService.autoCompletaEtiquetaSecunRep(valor, this.repaletizadoSel.getCodigoProducto()));
			
			return listaEtiquetas;
			
		} catch (Exception e) {
			logger.info("[autoCompletaEtiquetaAlm - error] error " + e);
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
			this.listaEtiquetas.addAll(this.repaService.buscarEtiquetasRepaletizado(this.etiqueta));
			
			
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
	
	
	public void siguiente() {
		
		try {
			
			if(this.movimiento <= 0 || this.movimiento > this.repaletizadoSel.getCajasPallet()) {
				PrimeFaces.current().executeScript(
						"Swal.fire(\"Campos Mandatorios\", \"Debe ingresar una cantidad valida para mover.\", \"warning\");");
				return;
			}
			
			this.etiSec = false;
			this.repSecundario = new Repaletizado();
			
			PrimeFaces.current().executeScript("PF('dlgRepaletizado').hide();");
			PrimeFaces.current().executeScript("PF('dlgRepaletizadoSec').show();");
			
		} catch (Exception e) {
			PrimeFaces.current().executeScript(
					"Swal.fire(\"Problemas al buscar etiqueta\", \"Existieron problemas al cursar búsqueda de etiqueta, "
							+ "favor vuelva a intentar.\", \"error\");");
			logger.info("[siguiente - error] error " + e);
		}
	} 
	
	
	public void repaletizarEtiqueta(Repaletizado rep) {
		try {

			if (rep != null && rep.getEtiqueta() != null) {
				logger.info("[repaletizarEtiqueta]  etiqueta " + rep.getEtiqueta());

				this.repaletizadoSel = rep;
				//this.setAccion(acc);

				this.repSecundario = new Repaletizado();
				this.movimiento = 0;

				/*if (this.accion.equals(Constantes.QUITAR)) {
					this.maxCajas = this.repaletizadoSel.getCajasPallet();;
				} else if (this.accion.equals(Constantes.AGREGAR)) {
					this.maxCajas = 0;
				}*/

			}

		} catch (Exception e) {
			PrimeFaces.current().executeScript(
					"Swal.fire(\"Problemas al buscar etiqueta\", \"Existieron problemas al cursar búsqueda de etiqueta, "
							+ "favor vuelva a intentar.\", \"error\");");
			logger.info("[repaletizarEtiqueta - error] error " + e);
		}
	}
	
	
	public void buscarEtiquetaSecundaria() {
		
		try {
			logger.info("[buscarEtiquetaSecundaria] buscar etiqueta secundaria" + this.etiqueta);
			
			
			this.repSecundario = this.repaService.buscarEtiquetaRepaletizado(this.etiquetaSecun);
			
			if(this.accion.equals(Constantes.AGREGAR)) {
				this.maxCajas = this.repSecundario.getCajasPallet();
			}
			
			
		} catch (Exception e) {
			PrimeFaces.current().executeScript(
					"Swal.fire(\"Problemas al buscar etiqueta\", \"Existieron problemas al cursar búsqueda de etiqueta, "
					+ "favor vuelva a intentar.\", \"warning\");");
			logger.info("[buscarEtiquetaSecundaria - error] error " + e);
		}
	}
	
	
	public void confirmar() {
		
		try {
			
			if(this.etiSec) {
				if(this.etiquetaSecun == null || this.etiquetaSecun.equals("")) {
					PrimeFaces.current().executeScript(
							"Swal.fire(\"Ingresar Etiqueta\", \"Debe ingresar una etiqueta para asignar el movimiento.\", \"warning\");");
					return;
				}
				
				if(this.repSecundario.getEtiqueta().equals(this.repaletizadoSel.getEtiqueta())) {
					PrimeFaces.current().executeScript(
							"Swal.fire(\"Ingresar Etiqueta\", \"Debe ingresar una etiqueta diferente a la seleccionada.\", \"warning\");");
					return;
				}
				
				String dialog = "Swal.fire({";
				dialog += "title: \"Confirmación\",";
				dialog += " text: \"Se quitarán " +this.movimiento+"/cajas de la etiqueta "+this.repaletizadoSel.getEtiqueta() +" y se agregarán a la etiqueta " + this.repSecundario.getEtiqueta() + " ¿Desea continuar?\",";
				dialog += " type: 'warning',";
				dialog += " showCancelButton: true,";
				dialog += " confirmButtonColor: '#3085d6',";
				dialog += " cancelButtonColor: '#d33',";
				dialog += " confirmButtonText: 'Sí',";
				dialog += " cancelButtonText: 'No'";
				dialog += " }).then((result) => {";
				dialog += " if (result.value) {";
				dialog += "  guardar();habilitarScroll();PF('dlgRepaletizadoSec').hide();";
				dialog += " } else { habilitarScroll(); }})";
				
				PrimeFaces.current().executeScript(dialog);
			} else {
				
				
				
				String dialog = "Swal.fire({";
				dialog += "title: \"Confirmación\",";
				dialog += " text: \"Se quitarán " +this.movimiento+"/cajas de la etiqueta "+this.repaletizadoSel.getEtiqueta() +". ¿Desea continuar?\",";
				dialog += " type: 'warning',";
				dialog += " showCancelButton: true,";
				dialog += " confirmButtonColor: '#3085d6',";
				dialog += " cancelButtonColor: '#d33',";
				dialog += " confirmButtonText: 'Sí',";
				dialog += " cancelButtonText: 'No'";
				dialog += " }).then((result) => {";
				dialog += " if (result.value) {";
				dialog += "  guardar();habilitarScroll();PF('dlgRepaletizadoSec').hide();";
				dialog += " } else { habilitarScroll(); }})";
				
				PrimeFaces.current().executeScript(dialog);
			}
			
			

			
			
		} catch (Exception e) {
			PrimeFaces.current().executeScript(
					"Swal.fire(\"Problemas al buscar etiqueta\", \"Existieron problemas al cursar búsqueda de etiqueta, "
					+ "favor vuelva a intentar.\", \"warning\");");
			logger.info("[confirmar - error] error " + e);
		}
		
		
	}
	
	public void guardar() {

		try {

			this.mensajeCompleto = "";

			if (this.etiSec) {

				if (this.etiquetaSecun == null || this.etiquetaSecun.equals("")) {
					PrimeFaces.current().executeScript(
							"Swal.fire(\"Ingresar Etiqueta\", \"Debe ingresar una etiqueta para asignar el movimiento.\", \"warning\");");
					return;
				}

				if (repaletizadoSel != null && repaletizadoSel.getEtiqueta() != null) {
					logger.info("[guardar]  etiqueta " + repaletizadoSel.getEtiqueta());
					logger.info("[guardar]  cajas por pallet " + repaletizadoSel.getCajasPallet());
					logger.info("[guardar]  movimiento " + this.movimiento);

					this.repaletizadoSel.setCajasPallet(this.repaletizadoSel.getCajasPallet() - movimiento);
					this.repSecundario.setCajasPallet(this.repSecundario.getCajasPallet() + movimiento);

					if (this.repaletizadoSel.getCajasPallet() > 0) {
						Etiqueta prima = this.etService.buscarEtiqueta(this.repaletizadoSel.getEtiqueta());

						prima.setRepaletizado(true);
						prima.setFechaUp(new Date());
						prima.setUsuarioUp(this.loginBean.getUsername());
						prima.setEstado(Constantes.ESTADO_ETIQUETA_REPALETIZADA); //TODO se debe cambiar por almacenada?

						this.etService.actualizarEtiquetaRepaletizada(prima);

						String nuevaEt = this.etService.obtenerEtiqueta();
						prima.setId(null);
						prima.setFechaCr(new Date());
						prima.setUsuarioCr(this.loginBean.getUsername());
						prima.setFechaUp(null);
						prima.setUsuarioUp(null);
						prima.setHistorialRepaletizado(prima.getHistorialRepaletizado() + " / " + nuevaEt);
						prima.setRepaletizado(true);
						prima.setNroEtiqueta(nuevaEt);
						prima.setCajasPallet(this.repaletizadoSel.getCajasPallet());
						prima.setSlipsheet(this.repaletizadoSel.getSlipsheet());
						this.etService.crearEtiqueta(prima);

						this.mensajeCompleto = "<tr class=\"info\"> <td>" + "La etiqueta "
								+ this.repaletizadoSel.getEtiqueta() + " pasó a ser la etiqueta "
								+ prima.getNroEtiqueta() + " </td> <td> " + " con la cantidad de "
								+ prima.getCajasPallet() + " cajas " + "</td> </tr> <br></br>";

					} else {

						Etiqueta prima = this.etService.buscarEtiqueta(this.repaletizadoSel.getEtiqueta());

						prima.setRepaletizado(true);
						prima.setFechaUp(new Date());
						prima.setUsuarioUp(this.loginBean.getUsername());
						prima.setEstado(Constantes.ESTADO_ETIQUETA_REPALETIZADA);

						this.etService.actualizarEtiquetaRepaletizada(prima);
					}

					if (this.repSecundario.getCajasPallet() > 0) {

						Etiqueta secun = this.etService.buscarEtiqueta(this.repSecundario.getEtiqueta());

						secun.setRepaletizado(true);
						secun.setFechaUp(new Date());
						secun.setUsuarioUp(this.loginBean.getUsername());
						secun.setEstado(Constantes.ESTADO_ETIQUETA_REPALETIZADA);

						this.etService.actualizarEtiquetaRepaletizada(secun);

						String nuevaEtsec = this.etService.obtenerEtiqueta();
						secun.setId(null);
						secun.setFechaCr(new Date());
						secun.setUsuarioCr(this.loginBean.getUsername());
						secun.setFechaUp(null);
						secun.setUsuarioUp(null);
						secun.setHistorialRepaletizado(secun.getHistorialRepaletizado() + " / " + nuevaEtsec);
						secun.setRepaletizado(true);
						secun.setNroEtiqueta(nuevaEtsec);
						secun.setCajasPallet(this.repSecundario.getCajasPallet());
						secun.setSlipsheet(this.repSecundario.getSlipsheet());

						this.etService.crearEtiqueta(secun);

						this.mensajeCompleto = this.mensajeCompleto + "<tr class=\"info\"> <td>" + "La etiqueta "
								+ this.repSecundario.getEtiqueta() + " pasó a ser la etiqueta " + secun.getNroEtiqueta()
								+ " </td> <td> " + " con la cantidad de " + secun.getCajasPallet() + " cajas "
								+ "</td> </tr> ";
					}

					String men = "Swal.fire({title: 'Información del Movimiento',html:'" + this.mensajeCompleto
							+ "', type:'info'});";
					PrimeFaces.current().executeScript(men);

					this.limpiar();
					return;

				}

			} else {

				if (this.movimiento <= 0) {
					PrimeFaces.current().executeScript(
							"Swal.fire(\"Ingresar valor a mover\", \"Debe ingresar un valor para el movimiento de carga.\", \"warning\");");
					return;
				}

				if (repaletizadoSel != null && repaletizadoSel.getEtiqueta() != null) {
					logger.info("[guardar]  etiqueta " + repaletizadoSel.getEtiqueta());
					logger.info("[guardar]  cajas por pallet " + repaletizadoSel.getCajasPallet());
					logger.info("[guardar]  movimiento " + this.movimiento);

					this.repaletizadoSel.setCajasPallet(this.repaletizadoSel.getCajasPallet() - movimiento);

					if (this.repaletizadoSel.getCajasPallet() > 0) {
						Etiqueta prima = this.etService.buscarEtiqueta(this.repaletizadoSel.getEtiqueta());
						String historialPrima = prima.getHistorialRepaletizado();
						String nuevaEt = this.etService.obtenerEtiqueta();

						prima.setRepaletizado(true);
						prima.setHistorialRepaletizado("");
						prima.setFechaUp(new Date());
						prima.setUsuarioUp(this.loginBean.getUsername());
						prima.setEstado(Constantes.ESTADO_ETIQUETA_REPALETIZADA);

						this.etService.actualizarEtiquetaRepaletizada(prima);

						Etiqueta et1 = new Etiqueta();
						et1.setDetallePo(prima.getDetallePo());
						et1.setFechaCr(new Date());
						et1.setUsuarioCr(this.loginBean.getUsername());
						et1.setFechaUp(null);
						et1.setUsuarioUp(null);
						et1.setHistorialRepaletizado(historialPrima + " / " + nuevaEt);
						et1.setRepaletizado(true);
						et1.setNroEtiqueta(nuevaEt);
						et1.setAsignado(false);
						et1.setCajasPallet(this.repaletizadoSel.getCajasPallet());
						et1.setSlipsheet(this.repaletizadoSel.getSlipsheet());
						et1.setEstado(Constantes.ESTADO_ETIQUETA_RECEPCIONADA);

						this.etService.crearEtiqueta(et1);

						this.mensajeCompleto = "<tr class=\"info\"> <td>" + "La etiqueta "
								+ this.repaletizadoSel.getEtiqueta() + " pasó a ser la etiqueta "
								+ et1.getNroEtiqueta() + " </td> <td> " + " con la cantidad de "
								+ et1.getCajasPallet() + " cajas " + "</td> </tr> <br></br>";
						
						//etiqueta del movimiento
						
						nuevaEt = this.etService.obtenerEtiqueta();
						
						Etiqueta et2 = new Etiqueta();
						et2.setDetallePo(prima.getDetallePo());
						et2.setFechaCr(new Date());
						et2.setUsuarioCr(this.loginBean.getUsername());
						et2.setFechaUp(null);
						et2.setUsuarioUp(null);
						et2.setHistorialRepaletizado(historialPrima + " / " + nuevaEt);
						et2.setRepaletizado(true);
						et2.setAsignado(false);
						et2.setNroEtiqueta(nuevaEt);
						et2.setCajasPallet(this.movimiento);
						et2.setSlipsheet(this.repaletizadoSel.getSlipsheet());
						et2.setEstado(Constantes.ESTADO_ETIQUETA_RECEPCIONADA);

						this.etService.crearEtiqueta(et2);

						this.mensajeCompleto = this.mensajeCompleto + "<tr class=\"info\"> <td>" + "Las " + this.movimiento +
								" cajas del movimiento, pasaron a ser la etiqueta "
								+ et2.getNroEtiqueta() + " </td> "
								+ "</tr> <br></br>";

						String men = "Swal.fire({title: 'Información del Movimiento',html:'" + this.mensajeCompleto
								+ "', type:'info'});";
						PrimeFaces.current().executeScript(men);

						this.limpiar();
						return;

					}

				}
			}

		} catch (Exception e) {
			PrimeFaces.current().executeScript(
					"Swal.fire(\"Problemas al buscar etiqueta\", \"Existieron problemas al guardar cambios de la etiqueta, "
							+ "favor vuelva a intentar.\", \"error\");");
			logger.info("[guardar - error] error " + e);
		}
	}
	
	
	


	private void limpiar() {
		
		this.listaEtiquetas.clear();
		this.etiqueta = "";
		this.pasillo = "";
		this.altura = "";
		this.posicion = "";
		
		this.etiquetaData = new ConsultarEtiqueta();
		this.maxCajas = 0;
		this.movimiento = 0;
		this.accion = "";
		this.etiquetaPrima = "";
		this.etiquetaSecun = "";
		this.repaletizadoSel = new Repaletizado();
		this.repSecundario = new Repaletizado();
		
		this.mensajeCompleto = "";
		
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

	public List<Repaletizado> getListaEtiquetas() {
		return listaEtiquetas;
	}

	public void setListaEtiquetas(List<Repaletizado> listaEtiquetas) {
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

	public String getEtiquetaPrima() {
		return etiquetaPrima;
	}

	public void setEtiquetaPrima(String etiquetaPrima) {
		this.etiquetaPrima = etiquetaPrima;
	}

	public String getEtiquetaSecun() {
		return etiquetaSecun;
	}

	public void setEtiquetaSecun(String etiquetaSecun) {
		this.etiquetaSecun = etiquetaSecun;
	}

	public Repaletizado getRepaletizadoSel() {
		return repaletizadoSel;
	}

	public void setRepaletizadoSel(Repaletizado repaletizadoSel) {
		this.repaletizadoSel = repaletizadoSel;
	}

	public String getAccion() {
		return accion;
	}

	public void setAccion(String accion) {
		this.accion = accion;
	}

	public Integer getMovimiento() {
		return movimiento;
	}

	public void setMovimiento(Integer movimiento) {
		this.movimiento = movimiento;
	}

	public Integer getMaxCajas() {
		return maxCajas;
	}

	public void setMaxCajas(Integer maxCajas) {
		this.maxCajas = maxCajas;
	}

	public Repaletizado getRepSecundario() {
		return repSecundario;
	}

	public void setRepSecundario(Repaletizado repSecundario) {
		this.repSecundario = repSecundario;
	}

	public Boolean getEtiSec() {
		return etiSec;
	}

	public void setEtiSec(Boolean etiSec) {
		this.etiSec = etiSec;
	}
	
	
}
