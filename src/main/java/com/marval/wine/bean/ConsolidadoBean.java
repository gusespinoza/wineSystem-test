package com.marval.wine.bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.primefaces.PrimeFaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.marval.wine.util.Util;
import com.marval.wine.entity.Asignacion;
import com.marval.wine.entity.Container;
import com.marval.wine.entity.Etiqueta;
import com.marval.wine.service.AsignacionService;
import com.marval.wine.service.ContainerService;
import com.marval.wine.service.EtiquetaService;
import com.marval.wine.util.Constantes;

@ManagedBean
@SessionScope
@Component
@PropertySource(Constantes.RUTA_PROPERTIES)
public class ConsolidadoBean {

	private static final Logger logger = LoggerFactory.getLogger(ConsolidadoBean.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
	AsignacionService asignacionService;
	@Autowired
	LoginBean loginBean;
	@Autowired
	ContainerService contService;
	@Autowired
	EtiquetaService etiquetaService;

	private String seleccion;
	private String nuevoFolio;
	private Integer totalCajas;
	private Integer totalPallets;
	private Integer contenedorSelected;
	private Double totalpesoNeto;
	private Double totalPesoBruto;
	private Integer tara;
	private Integer airbag;
	private String observaciones;
	private String patenteCamion;
	private String observacionesOtra;
	private String selloCia;
	private String guiasDespacho;
	private String puertaDestino;
	private boolean cerrado;
	private boolean muestraObservacion;
	private boolean selloDiferente = false;
	private boolean muestraPuerta;
	private boolean contConsolidado;
	private Date fechaIncio;

	private List<String> guiaDespacho = new ArrayList<String>();
	private List<String> listaTermografo = new ArrayList<String>();
	private List<String> listaPalletsGuardado = new ArrayList<String>();
	private List<Etiqueta> etiquetasDialogo = new ArrayList<Etiqueta>();

	private Container contenedor;
	private Asignacion asignacion;
	private Asignacion asigancionAux;

	private List<Asignacion> listAsignacionesDisponibles = new ArrayList<Asignacion>();
	private List<Asignacion> listAsignacionesAsignados = new ArrayList<Asignacion>();
	private List<Asignacion> listAsignacionesAceptadas = new ArrayList<Asignacion>();
	private List<Asignacion> listAsignaciones = new ArrayList<Asignacion>();
	private List<Asignacion> listAsignacionesBd = new ArrayList<Asignacion>();
	private List<Asignacion> listaRespaldo = new ArrayList<Asignacion>();
	private Map<Integer, String> listContenedor = new HashMap<Integer, String>();
//	private List<Posiciones> listaPosiciones = new ArrayList<Posiciones>();

	public void onload() throws IOException {
		logger.info("[ConsolidadoBean - onload] Inicio");
		if (loginBean.isMenuDesconsolidado()) {

			if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) {
				return;
			}
			this.selloCia = "";
			this.listAsignacionesDisponibles.clear();
			this.listAsignacionesAsignados.clear();
			this.listAsignaciones.clear();
			this.listContenedor.clear();
			this.seleccion = "";
			this.muestraObservacion = false;
			this.observacionesOtra = "";
			this.observaciones = "";
			this.guiasDespacho = "";
			this.setSelloDiferente(false);
			this.muestraPuerta = false;
			this.listContenedor.putAll(contService.listaContenedoresTarjado());
			this.puertaDestino = "";
			this.contenedorSelected = null;

			if (this.contConsolidado) {
				PrimeFaces.current().executeScript("Swal.fire('Consolidado Finalizado','El consolidado del contenedor "
						+ contenedor.getContainer() + " ha finalizado correctamente.', 'info');");
				this.contConsolidado = false;
			}

			this.contConsolidado = false;
			this.contenedor = new Container();
		} else {
			ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			String path = ctx.getContextPath();
			FacesContext.getCurrentInstance().getExternalContext().redirect(path + "/index.xhtml");
		}

		logger.info("[ConsolidadoBean - onload] Fin");

	}

	/**
	 * 
	 * @param Asignacion
	 */
	public void asignaPalletDisponible(Asignacion asg) {
		if (this.asignacion.getContenedor() == null) {
			this.asignacion = asg;
			this.seleccion = "D";
		}
		PrimeFaces.current().ajax().update("formTarjadoPalet:palletAsig");
		PrimeFaces.current().ajax().update("formTarjadoPalet:palletDisp");
	}

	/**
	 * Método que permite cargar pallets disponibles.
	 * 
	 * @throws IOException
	 */
	public void cargaPalletDisponibles() throws IOException {

		logger.info("[ConsolidadoBean - cargaPalletDisponibles] inicio");
		this.asignacion = new Asignacion();
		this.listAsignacionesDisponibles.clear();
		this.listAsignaciones.clear();
		this.listAsignacionesAsignados.clear();
		this.observacionesOtra = "";
		this.observaciones = Constantes.LISTA_OBSERVACIONES_PALLET.get(0);
		this.muestraObservacion = false;
		this.guiaDespacho = new ArrayList<String>();
		List<String> lisString = new ArrayList<String>();
		Set<String> lisNroFolioOcupado = new HashSet<String>();
		Set<String> lisNroFolioDisponible = new HashSet<String>();
		Set<Asignacion> allAsignaciones = new HashSet<Asignacion>();
		this.setSelloDiferente(false);
		String folio = null;
		this.contenedor = contService.buscarContenedor(this.getContenedorSelected());
		if (this.contenedor == null) {
			return;
		} else {
			allAsignaciones = this.contenedor.getAsignaciones();
		}

		Etiqueta det;
		Object[] obj = null;

		for (Asignacion asg : allAsignaciones) {
			// asignacionService.actualizarAsignacion(asg);
			folio = asg.getEtiqueta().getNroEtiqueta();
			if (asg.getPosicion() != null) {
				lisNroFolioOcupado.add(folio);
			} else {
				lisNroFolioDisponible.add(folio);
			}
		}
		for (String flo : lisNroFolioOcupado) {
			if (lisNroFolioDisponible.contains(flo)) {
				lisNroFolioDisponible.remove(flo);
			}
		}

		if (!lisNroFolioOcupado.isEmpty() || !lisNroFolioDisponible.isEmpty()) {
			int ocupado = 0;
			int libre = 0;
			for (Asignacion asg : allAsignaciones) {

				folio = asg.getEtiqueta().getNroEtiqueta();
				det = new Etiqueta();

				if (lisNroFolioOcupado.contains(folio) && asg.getPosicion() != null) {
					det = asg.getEtiqueta();
					this.listAsignacionesBd.add(asg);
					ocupado++;
				}

				if (lisNroFolioDisponible.contains(folio) && !lisString.contains(folio)) {
					this.listAsignacionesDisponibles.add(asg);
					lisString.add(folio);
					libre++;
				}
			}
			logger.info("LIBRE : " + libre + " OCUPADO: " + ocupado);
		}

		Integer n = 1;
		lisString = new ArrayList<String>();

		Asignacion asig;
		Asignacion asigAux;
		Etiqueta guid;

		for (Integer i = 1; i <= 24; i++) {
			asig = new Asignacion();
			asigAux = new Asignacion();
			guid = new Etiqueta();
			if (!listAsignacionesBd.isEmpty()) {
				for (Asignacion as : listAsignacionesBd) {
					if (as.getPosicion() == i) {
						if (!asig.getEtiquetas().isEmpty()) {
							asig.getEtiquetas().add(as.getEtiqueta());
						} else {
							asig = as;
							asig.getEtiquetas().add(as.getEtiqueta());
						}
					}
				}

				if (!asig.getEtiquetas().isEmpty() && asig.getEtiquetas().size() > 1) {
					Integer totalEtiquetas = 0;
					for (Etiqueta et : asig.getEtiquetas()) {
						totalEtiquetas = totalEtiquetas + et.getCajasPallet();
					}
					Etiqueta etAux = new Etiqueta();
					etAux.setNroEtiqueta("VARIOS");
					etAux.setCajasPallet(totalEtiquetas);
					asig.setEtiqueta(etAux);
				} else {
					asig.getEtiquetas().clear();
				}

				if (asig.getId() != null) {
					this.listAsignaciones.add(asig);
				} else {
					guid.setNroEtiqueta(Constantes.DISPONIBLE);
					asig.setEtiqueta(guid);
					asig.setPosicion(i);
					this.listAsignaciones.add(asig);
				}

			} else {
				guid.setNroEtiqueta(Constantes.DISPONIBLE);
				guid.setCajasPallet(0);
				asig.setEtiqueta(guid);
				asig.setPosicion(i);
				this.listAsignaciones.add(asig);
			}
		}

		this.fechaIncio = new Date();
		this.selloCia = "";
		this.calculaTotales();
		this.cerrado = false;
		this.listAsignacionesBd.clear();
		ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		String path = ctx.getContextPath();
		FacesContext.getCurrentInstance().getExternalContext().redirect(path + "/consolidado");
		logger.info("[ConsolidadoBean - cargaPalletDisponibles] Fin");
	}

	private void calculaTotales() {
		this.totalCajas = 0;
		this.totalPallets = 0;
		this.totalpesoNeto = 0.0;
		this.totalPesoBruto = 0.0;
		for (Asignacion asg : this.listAsignaciones) {
			if (asg.getContenedor() != null) {
				this.totalCajas += asg.getEtiqueta().getCajasPallet();
				this.totalPallets++;
			}
		}
	}

	/**
	 * Metodo que asigna un pallet
	 * 
	 * @param Asignacion
	 */
	public void asignaPallet(Asignacion asgDestino) {
		List<Asignacion> list = new ArrayList<Asignacion>();
		Integer p = 0;
		/**
		 * Borra con dos click
		 */
		if (this.asignacion.equals(asgDestino) && this.asignacion.getContenedor() != null
				&& asgDestino.getContenedor() != null && this.seleccion.equals("S")) {
			this.listAsignaciones.remove(asgDestino);
			this.asignacion = new Asignacion();
			Etiqueta guid = new Etiqueta();
			guid.setNroEtiqueta(Constantes.DISPONIBLE);
			guid.setCajasPallet(0);
			this.asignacion.setEtiqueta(guid);
			this.asignacion.setPosicion(asgDestino.getPosicion());
			asgDestino.setPosicion(null);
			asignacionService.actualizarAsignacion(asgDestino, 0);
			this.listAsignaciones.add(this.asignacion);
			if (asgDestino.getEtiquetas().isEmpty()) {
				this.listAsignacionesDisponibles.add(asgDestino);
			} else {
				for (Asignacion asignac : this.contenedor.getAsignaciones()) {
					if (asgDestino.getEtiqueta().getId() == asignac.getEtiqueta().getId()) {
						this.listAsignacionesDisponibles.add(asignac);
					}
				}

				for (Etiqueta et : asgDestino.getEtiquetas()) {
					for (Asignacion asigna : this.contenedor.getAsignaciones()) {
						if (et.getId() == asigna.getEtiqueta().getId()) {
							this.listAsignacionesDisponibles.add(asigna);
						}
					}
				}
			}
			list.addAll(this.listAsignaciones);
			this.listAsignaciones.clear();
			this.listAsignaciones.addAll(reordenaPallet(list));
			this.asignacion = new Asignacion();
			this.calculaTotales();
			PrimeFaces.current().ajax().update("formTarjadoPalet:gridTotales");
			PrimeFaces.current().ajax().update("formTarjadoPalet:palletAsig");
			PrimeFaces.current().ajax().update("formTarjadoPalet:palletDisp");
			return;
		}

		if (!asgDestino.getEtiqueta().getNroEtiqueta().equals("DISPONIBLE") && !this.seleccion.equals("")
				&& this.asignacion.getEtiqueta() != null) {
			int totalCajas = 0;
			Etiqueta et = new Etiqueta();
			if (asgDestino.getEtiquetas().isEmpty()) {
				asgDestino.getEtiquetas().add(asgDestino.getEtiqueta());
				asgDestino.getEtiquetas().add(this.asignacion.getEtiqueta());
			} else {
				asgDestino.getEtiquetas().add(this.asignacion.getEtiqueta());
			}

			for (Etiqueta eti : asgDestino.getEtiquetas()) {
				totalCajas = totalCajas + eti.getCajasPallet();
			}
			et.setNroEtiqueta("VARIOS");
			et.setCajasPallet(totalCajas);
			asgDestino.setEtiqueta(et);
			this.asignacion.setPosicion(asgDestino.getPosicion());
			asignacionService.actualizarAsignacion(this.asignacion, 0);
			this.listAsignacionesDisponibles.remove(this.asignacion);
			this.asignacion = new Asignacion();
			this.calculaTotales();
			PrimeFaces.current().ajax().update("formTarjadoPalet:gridTotales");
			PrimeFaces.current().ajax().update("formTarjadoPalet:palletAsig");
			PrimeFaces.current().ajax().update("formTarjadoPalet:palletDisp");
			return;
		}

		/**
		 * valida dos click en disponible
		 */
		if (this.asignacion.getContenedor() == null && asgDestino.getContenedor() == null) {
			return;
		}

		/*
		 * mover etiqueta
		 */
		if (this.asignacion.getContenedor() != null && !this.asignacion.equals(asgDestino)
				&& this.seleccion.equals("S")) {
			p = this.asignacion.getPosicion();
			this.listAsignaciones.remove(this.asignacion);
			if (this.asignacion.getEtiquetas().isEmpty()) {
				this.asignacion.setPosicion(asgDestino.getPosicion());
				asignacionService.actualizarAsignacion(this.asignacion, 0);
				this.listAsignaciones.add(this.asignacion);
				asgDestino.setPosicion(p);
			} else {
				this.asignacion.setPosicion(asgDestino.getPosicion());
				this.listAsignaciones.add(this.asignacion);
				asgDestino.setPosicion(p);
				asignacionService.actualizarAsignacion(this.asignacion, this.asignacion.getPosicion());
			}
			this.listAsignaciones.remove(asgDestino);
			this.listAsignaciones.add(asgDestino);
			list.addAll(this.listAsignaciones);
			this.listAsignaciones.clear();
			this.listAsignaciones.addAll(reordenaPallet(list));
			this.asignacion = new Asignacion();
			this.calculaTotales();
			PrimeFaces.current().ajax().update("formTarjadoPalet:gridTotales");
			PrimeFaces.current().ajax().update("formTarjadoPalet:palletAsig");
			PrimeFaces.current().ajax().update("formTarjadoPalet:palletDisp");
			return;
		}

		/**
		 * toma pallet ya asignado en memoria
		 */
		if (this.asignacion.getContenedor() == null && asgDestino.getContenedor() != null) {
			this.asignacion = asgDestino;
			this.seleccion = "S";
			PrimeFaces.current().ajax().update("formTarjadoPalet:palletAsig");
			PrimeFaces.current().ajax().update("formTarjadoPalet:palletDisp");
			return;
		}

		/**
		 * asigna nuevo pallet
		 */
		if (this.asignacion.getContenedor() != null && asgDestino.getContenedor() == null
				&& this.seleccion.equals("D")) {
			this.asignacion.setPosicion(asgDestino.getPosicion());
			asignacionService.actualizarAsignacion(this.asignacion, 0);
			list.clear();
			this.listAsignaciones.remove(asgDestino);
			this.listAsignaciones.add(this.asignacion);
			this.listAsignacionesDisponibles.remove(this.asignacion);
			list.addAll(this.listAsignaciones);
			this.listAsignaciones.clear();
			this.listAsignaciones.addAll(reordenaPallet(list));
			this.asignacion = new Asignacion();
			this.calculaTotales();
			PrimeFaces.current().ajax().update("formTarjadoPalet:gridTotales");
			PrimeFaces.current().ajax().update("formTarjadoPalet:palletAsig");
			PrimeFaces.current().ajax().update("formTarjadoPalet:palletDisp");
		}

		/*
		 * Agrupar etiquetas
		 */
		if (this.asignacion.getContenedor() != null && asgDestino.getContenedor() == null
				&& this.seleccion.equals("A")) {
			p = this.asignacion.getPosicion();
			this.asignacion.setPosicion(asgDestino.getPosicion());
			list.clear();
			this.listAsignaciones.remove(asgDestino);
			this.listAsignaciones.add(this.asignacion);
			this.listAsignacionesAsignados.remove(this.asignacion);
			list.addAll(this.listAsignaciones);
			this.listAsignaciones.clear();
			this.listAsignaciones.addAll(reordenaPallet(list));
			list.clear();

			this.asignacion = new Asignacion();
			Etiqueta guid = new Etiqueta();
			guid.setNroEtiqueta(Constantes.ALMACEN);
			guid.setCajasPallet(p);
			this.asignacion.setEtiqueta(guid);
			this.asignacion.setPosicion(p);
			this.listAsignacionesAsignados.add(this.asignacion);
			list.addAll(this.listAsignacionesAsignados);
			this.listAsignacionesAsignados.clear();
			this.listAsignacionesAsignados.addAll(reordenaPallet(list));
			this.asignacion = new Asignacion();
			this.calculaTotales();
			PrimeFaces.current().ajax().update("formTarjadoPalet:gridTotales");
			PrimeFaces.current().ajax().update("formTarjadoPalet:palletAsig");
			PrimeFaces.current().ajax().update("formTarjadoPalet:palletDisp");
		}
	}

	private List<Asignacion> reordenaPallet(List<Asignacion> asign) {
		List<Asignacion> list = new ArrayList<Asignacion>();
		for (Integer i = 1; i <= asign.size(); i++) {
			for (Asignacion s : asign) {
				if (s.getPosicion() == i) {
					list.add(s);
				}
			}
		}
		return list;
	}

	public List<SelectItem> observacionesSelect() {
		return new Util().listaStringASelect(Constantes.LISTA_OBSERVACIONES_PALLET);
	}

	public void validaObservacion() {
		this.muestraObservacion = (this.observaciones.equals("OTRO")) ? true : false;
		PrimeFaces.current().ajax().update("formTarjadoPalet:gridInput");
	}

	public String cambiaColor(Asignacion asg) {

		if (this.asignacion.equals(asg)) {
			return "background-color: red!important";
		}

		return "";
	}

	public void volver() throws IOException {
		ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		String path = ctx.getContextPath();
		FacesContext.getCurrentInstance().getExternalContext().redirect(path + "/tarja");
	}

	public String cambiaClaseBoton(Asignacion asg) {
		if (asg.getContenedor() == null) {
			return "btn-teal";
		}

		return "btn-info";

	}

	public void guardarTarja() throws IOException {
		this.listAsignacionesAceptadas = new ArrayList<Asignacion>();
		logger.info("[PalletBean - guardarTarja] inicio");
		this.contenedor = contService.buscarContenedor(this.contenedor.getId());

		if (this.cerrado) {
			mensajeWarnig("El Contenedor " + this.contenedor.getContainer() + " ya se encuentra cerrado");
			return;
		}

		if (!this.listAsignacionesDisponibles.isEmpty()) {
			mensajeWarnig("Todos los Pallet deben quedar asignados");
			return;
		}
		if (this.listAsignaciones.isEmpty()) {
			mensajeWarnig("Debe asignar al menos un Pallet");
			return;
		}

		if ((!this.listAsignacionesAsignados.isEmpty() || !this.listAsignaciones.isEmpty())
				&& !this.observaciones.equals("")) {
			this.contenedor.setConsolidado(true);

			this.selloCia = (this.selloCia != null) ? this.selloCia : "";
			if ((this.selloCia.equals(""))) {
				PrimeFaces.current().ajax().update("formTarjadoPalet:selloModalId");
				PrimeFaces.current().executeScript("bloquearScroll()");
				PrimeFaces.current().executeScript("PF('selloModal').show()");
				return;
			}

			String validar = validaTarjado();
			if (!validar.equals("")) {
				mensajeWarnig(validar);
				return;
			}

			this.contenedor.setEstado(Constantes.ESTADO_CONT_CONSOLIDADO);
			this.contenedor.setFechaConsolidado(new Date());
			contService.actualizarContainer(this.contenedor);
			this.cerrado = true;

			for (Asignacion asg : this.listAsignaciones) {
				for (Asignacion ag : this.contenedor.getAsignaciones()) {
					if (asg.getContenedor() != null
							&& ag.getEtiqueta().getNroEtiqueta().equals(asg.getEtiqueta().getNroEtiqueta())) {
						ag.setAsignado(Constantes.CONTENEDOR);
						ag.setPosicion(asg.getPosicion());
						try {
							asignacionService.actualizarAsignacion(ag, 0);
						} catch (Exception e) {
							logger.error(
									"[PalletBean - guardarTarja] Error al actulizar asignacion numero: " + asg.getId());
						}

					}

				}
			}

			for (Asignacion asg : this.listAsignacionesAsignados) {
				for (Asignacion ag : this.contenedor.getAsignaciones()) {
					if (asg.getContenedor() != null
							&& ag.getEtiqueta().getNroEtiqueta().equals(asg.getEtiqueta().getNroEtiqueta())) {
						ag.setAsignado(Constantes.ALMACEN);
						ag.setPosicion(asg.getPosicion());
						ag.getEtiqueta().setEstado(Constantes.ESTADO_ETIQUETA_CONSOLIDADA);
						asignacionService.actualizarAsignacion(ag, 0);
						this.listAsignacionesAceptadas.add(ag);
					}
				}
			}

			ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			String path = ctx.getContextPath();

			this.contConsolidado = true;

			FacesContext.getCurrentInstance().getExternalContext().redirect(path + "/tarja");

		}

	}

	public void validarSello() {
		this.selloCia = (this.selloCia != null) ? this.selloCia : "";
		this.airbag = (this.airbag != null) ? this.airbag : 0;
		if (this.selloCia != null) {
			PrimeFaces.current().executeScript("PF('selloModal').hide()");
			this.contenedor.setSello(this.selloCia);
			this.contenedor.setAirbag(this.airbag);
			contService.actualizarContainer(this.contenedor);
		}
	}

	public String validaTarjado() {
		logger.info("[PalletBean - validaTarjado] inicio");
		String pall = "";
		String tarjados = "";

		if (!pall.equals("")) {
			return "Los pallets " + pall + " deben ser asignados a este contenedor";
		}

		for (Asignacion asg : this.listAsignaciones) {
			if (asg.getEtiquetas().isEmpty()) {
				this.listaRespaldo.add(asg);
			} else {
				for (Etiqueta et : asg.getEtiquetas()) {
					for (Asignacion asigna : this.contenedor.getAsignaciones()) {
						if (et.getId() == asigna.getEtiqueta().getId()) {
							this.listaRespaldo.add(asigna);
						}
					}
				}
			}
			asg.getEtiquetas().clear();
		}
		
		if(!this.listaRespaldo.isEmpty()) {
			this.listAsignaciones.clear();
			this.listAsignaciones.addAll(listaRespaldo);
		}

		for (Asignacion asg : this.listAsignaciones) {
			for (Asignacion ag : this.contenedor.getAsignaciones()) {
				if (asg.getContenedor() != null
						&& ag.getEtiqueta().getNroEtiqueta().equals(asg.getEtiqueta().getNroEtiqueta())) {
					try {
						asignacionService.actualizarAsignacion(ag, 0);
					} catch (Exception e) {
						tarjados += asg.getEtiqueta().getNroEtiqueta() + ", ";
					}

				}

			}
		}

		if (!tarjados.equals("")) {
			return "Los Pallets " + tarjados + " ya fueron tarjados ";
		}

		return "";
	}
	
	public void mostrarEtiquetas(List<Etiqueta> etiquetas) {
		this.etiquetasDialogo.clear();
		this.etiquetasDialogo.addAll(etiquetas);
		if(!this.etiquetasDialogo.isEmpty()) {
			PrimeFaces.current().ajax().update("formTarjadoPalet");
			PrimeFaces.current().executeScript("bloquearScroll()");
			PrimeFaces.current().executeScript("PF('etiquetasModal').show()");
		}
	}

	public void mensajeWarnig(String mensaje) {
		FacesContext.getCurrentInstance().addMessage("mensajes",
				new FacesMessage(FacesMessage.SEVERITY_WARN, mensaje, ""));

		PrimeFaces.current().ajax().update("formTarjadoPalet:mensajes");
		PrimeFaces.current().scrollTo("formTarjadoPalet:gridContenedor");
	}

	public void mensajeExito(String mensaje) {
		FacesContext.getCurrentInstance().addMessage("mensajes",
				new FacesMessage(FacesMessage.SEVERITY_INFO, mensaje, ""));

		PrimeFaces.current().ajax().update("formTarjadoPalet:mensajes");
		PrimeFaces.current().scrollTo("formTarjadoPalet:gridContenedor");
	}

	public Integer getAirbag() {
		return airbag;
	}

	public void setAirbag(Integer airbag) {
		this.airbag = airbag;
	}

	public Container getContenedor() {
		return contenedor;
	}

	public void setContenedor(Container contenedor) {
		this.contenedor = contenedor;
	}

	public Map<Integer, String> getListContenedor() {
		return listContenedor;
	}

	public void setListContenedor(Map<Integer, String> listContenedor) {
		this.listContenedor = listContenedor;
	}

	public String getSeleccion() {
		return seleccion;
	}

	public void setSeleccion(String seleccion) {
		this.seleccion = seleccion;
	}

	public String getNuevoFolio() {
		return nuevoFolio;
	}

	public void setNuevoFolio(String nuevoFolio) {
		this.nuevoFolio = nuevoFolio;
	}

	public Integer getTotalCajas() {
		return totalCajas;
	}

	public void setTotalCajas(Integer totalCajas) {
		this.totalCajas = totalCajas;
	}

	public Integer getTotalPallets() {
		return totalPallets;
	}

	public void setTotalPallets(Integer totalPallets) {
		this.totalPallets = totalPallets;
	}

	public Integer getContenedorSelected() {
		return contenedorSelected;
	}

	public void setContenedorSelected(Integer contenedorSelected) {
		this.contenedorSelected = contenedorSelected;
	}

	public Double getTotalpesoNeto() {
		return totalpesoNeto;
	}

	public void setTotalpesoNeto(Double totalpesoNeto) {
		this.totalpesoNeto = totalpesoNeto;
	}

	public Double getTotalPesoBruto() {
		return totalPesoBruto;
	}

	public void setTotalPesoBruto(Double totalPesoBruto) {
		this.totalPesoBruto = totalPesoBruto;
	}

	public Integer getTara() {
		return tara;
	}

	public void setTara(Integer tara) {
		this.tara = tara;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public String getPatenteCamion() {
		return patenteCamion;
	}

	public void setPatenteCamion(String patenteCamion) {
		this.patenteCamion = patenteCamion;
	}

	public String getObservacionesOtra() {
		return observacionesOtra;
	}

	public void setObservacionesOtra(String observacionesOtra) {
		this.observacionesOtra = observacionesOtra;
	}

	public String getSelloCia() {
		return selloCia;
	}

	public void setSelloCia(String selloCia) {
		this.selloCia = selloCia;
	}

	public String getGuiasDespacho() {
		return guiasDespacho;
	}

	public void setGuiasDespacho(String guiasDespacho) {
		this.guiasDespacho = guiasDespacho;
	}

	public String getPuertaDestino() {
		return puertaDestino;
	}

	public void setPuertaDestino(String puertaDestino) {
		this.puertaDestino = puertaDestino;
	}

	public boolean isCerrado() {
		return cerrado;
	}

	public void setCerrado(boolean cerrado) {
		this.cerrado = cerrado;
	}

	public boolean isMuestraObservacion() {
		return muestraObservacion;
	}

	public void setMuestraObservacion(boolean muestraObservacion) {
		this.muestraObservacion = muestraObservacion;
	}

	public boolean isSelloDiferente() {
		return selloDiferente;
	}

	public void setSelloDiferente(boolean selloDiferente) {
		this.selloDiferente = selloDiferente;
	}

	public boolean isMuestraPuerta() {
		return muestraPuerta;
	}

	public void setMuestraPuerta(boolean muestraPuerta) {
		this.muestraPuerta = muestraPuerta;
	}

	public boolean isContConsolidado() {
		return contConsolidado;
	}

	public void setContConsolidado(boolean contConsolidado) {
		this.contConsolidado = contConsolidado;
	}

	public Date getFechaIncio() {
		return fechaIncio;
	}

	public void setFechaIncio(Date fechaIncio) {
		this.fechaIncio = fechaIncio;
	}

	public List<String> getGuiaDespacho() {
		return guiaDespacho;
	}

	public void setGuiaDespacho(List<String> guiaDespacho) {
		this.guiaDespacho = guiaDespacho;
	}

	public List<String> getListaTermografo() {
		return listaTermografo;
	}

	public void setListaTermografo(List<String> listaTermografo) {
		this.listaTermografo = listaTermografo;
	}

	public List<String> getListaPalletsGuardado() {
		return listaPalletsGuardado;
	}

	public void setListaPalletsGuardado(List<String> listaPalletsGuardado) {
		this.listaPalletsGuardado = listaPalletsGuardado;
	}

	public Asignacion getAsignacion() {
		return asignacion;
	}

	public void setAsignacion(Asignacion asignacion) {
		this.asignacion = asignacion;
	}

	public Asignacion getAsigancionAux() {
		return asigancionAux;
	}

	public void setAsigancionAux(Asignacion asigancionAux) {
		this.asigancionAux = asigancionAux;
	}

	public List<Asignacion> getListAsignacionesDisponibles() {
		return listAsignacionesDisponibles;
	}

	public void setListAsignacionesDisponibles(List<Asignacion> listAsignacionesDisponibles) {
		this.listAsignacionesDisponibles = listAsignacionesDisponibles;
	}

	public List<Asignacion> getListAsignacionesAsignados() {
		return listAsignacionesAsignados;
	}

	public void setListAsignacionesAsignados(List<Asignacion> listAsignacionesAsignados) {
		this.listAsignacionesAsignados = listAsignacionesAsignados;
	}

	public List<Asignacion> getListAsignacionesAceptadas() {
		return listAsignacionesAceptadas;
	}

	public void setListAsignacionesAceptadas(List<Asignacion> listAsignacionesAceptadas) {
		this.listAsignacionesAceptadas = listAsignacionesAceptadas;
	}

	public List<Asignacion> getListAsignaciones() {
		return listAsignaciones;
	}

	public void setListAsignaciones(List<Asignacion> listAsignaciones) {
		this.listAsignaciones = listAsignaciones;
	}

	public List<Asignacion> getListAsignacionesBd() {
		return listAsignacionesBd;
	}

	public void setListAsignacionesBd(List<Asignacion> listAsignacionesBd) {
		this.listAsignacionesBd = listAsignacionesBd;
	}

	public List<Asignacion> getListaRespaldo() {
		return listaRespaldo;
	}

	public void setListaRespaldo(List<Asignacion> listaRespaldo) {
		this.listaRespaldo = listaRespaldo;
	}

	public List<Etiqueta> getEtiquetasDialogo() {
		return etiquetasDialogo;
	}

	public void setEtiquetasDialogo(List<Etiqueta> etiquetasDialogo) {
		this.etiquetasDialogo = etiquetasDialogo;
	}
	
	

}