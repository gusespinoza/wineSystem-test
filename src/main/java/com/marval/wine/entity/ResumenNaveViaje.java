package com.marval.wine.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ResumenNaveViaje implements Serializable {

	/**
	 * RESUMEN POR NAVE Y VIAJE
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	
	private String container;
	private String ship;
	private String voyage;
	
	private String puerto;
	private String estado;
	private String sello;
	private String tara;
	private Date fechaCr;
	
	//cobros por contenedor
	
	private BigDecimal almacenamiento;
	private BigDecimal recepcion;
	private BigDecimal airbags;
	private BigDecimal slipsheet;
	private BigDecimal repaletizacion;
	private BigDecimal porteo;
	private BigDecimal transporte;
	private BigDecimal geteout;
	private BigDecimal total;
	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getContainer() {
		return container;
	}

	public void setContainer(String container) {
		this.container = container;
	}

	public String getShip() {
		return ship;
	}

	public void setShip(String ship) {
		this.ship = ship;
	}

	public String getVoyage() {
		return voyage;
	}

	public void setVoyage(String voyage) {
		this.voyage = voyage;
	}
	
	public String getPuerto() {
		return puerto;
	}

	public void setPuerto(String puerto) {
		this.puerto = puerto;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Date getFechaCr() {
		return fechaCr;
	}

	public void setFechaCr(Date fechaCr) {
		this.fechaCr = fechaCr;
	}

	public String getSello() {
		return sello;
	}

	public void setSello(String sello) {
		this.sello = sello;
	}

	public String getTara() {
		return tara;
	}

	public void setTara(String tara) {
		this.tara = tara;
	}

	public BigDecimal getAlmacenamiento() {
		return almacenamiento;
	}

	public void setAlmacenamiento(BigDecimal almacenamiento) {
		this.almacenamiento = almacenamiento;
	}

	public BigDecimal getRecepcion() {
		return recepcion;
	}

	public void setRecepcion(BigDecimal recepcion) {
		this.recepcion = recepcion;
	}

	public BigDecimal getAirbags() {
		return airbags;
	}

	public void setAirbags(BigDecimal airbags) {
		this.airbags = airbags;
	}

	public BigDecimal getSlipsheet() {
		return slipsheet;
	}

	public void setSlipsheet(BigDecimal slipsheet) {
		this.slipsheet = slipsheet;
	}

	public BigDecimal getRepaletizacion() {
		return repaletizacion;
	}

	public void setRepaletizacion(BigDecimal repaletizacion) {
		this.repaletizacion = repaletizacion;
	}

	public BigDecimal getPorteo() {
		return porteo;
	}

	public void setPorteo(BigDecimal porteo) {
		this.porteo = porteo;
	}

	public BigDecimal getTransporte() {
		return transporte;
	}

	public void setTransporte(BigDecimal transporte) {
		this.transporte = transporte;
	}

	public BigDecimal getGeteout() {
		return geteout;
	}

	public void setGeteout(BigDecimal geteout) {
		this.geteout = geteout;
	}

	public BigDecimal getTotal() {
		
		
		
		return total;
	}

	public void setTotal(BigDecimal total) {
		
		this.total = this.total.add((airbags == null) ? BigDecimal.ZERO : airbags);
		this.total = this.total.add((almacenamiento == null) ? BigDecimal.ZERO : almacenamiento);
		this.total = this.total.add((geteout == null) ? BigDecimal.ZERO : geteout);
		this.total = this.total.add((porteo == null) ? BigDecimal.ZERO : porteo);
		this.total = this.total.add((recepcion == null) ? BigDecimal.ZERO : recepcion);
		this.total = this.total.add((repaletizacion == null) ? BigDecimal.ZERO : repaletizacion);
		this.total = this.total.add((slipsheet == null) ? BigDecimal.ZERO : slipsheet);
		this.total = this.total.add((transporte == null) ? BigDecimal.ZERO : transporte);
		
		this.total = total;
	}


	
}
