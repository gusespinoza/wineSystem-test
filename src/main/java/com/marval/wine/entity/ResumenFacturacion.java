package com.marval.wine.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.beans.Transient;
import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;

@Entity
@SqlResultSetMapping(name = "ResumenFacturacionResult", entities = { @EntityResult(

		entityClass = ResumenFacturacion.class, fields = { @FieldResult(name = "id", column = "id"),
				@FieldResult(name = "container", column = "container"),
				@FieldResult(name = "ship", column = "ship"),
				@FieldResult(name = "voyage", column = "voyage"),
				@FieldResult(name = "puerto", column = "puerto"),
				@FieldResult(name = "estado", column = "estado"),
				@FieldResult(name = "sello", column = "sello"),
				@FieldResult(name = "tara", column = "tara"),
				@FieldResult(name = "fecha_cr", column = "fecha_cr")}) })

public class ResumenFacturacion implements Serializable {

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
	
	/*private String almacenamiento;
	private String recepcion;
	private String airbags;
	private String slipsheet;
	private String repaletizacion;
	private String porteo;
	private String transporte;
	private String geteout;
	private String total;*/
	
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "container")
	public String getContainer() {
		return container;
	}

	public void setContainer(String container) {
		this.container = container;
	}

	@Column(name = "ship")
	public String getShip() {
		return ship;
	}

	public void setShip(String ship) {
		this.ship = ship;
	}

	@Column(name = "voyage")
	public String getVoyage() {
		return voyage;
	}

	public void setVoyage(String voyage) {
		this.voyage = voyage;
	}

	@Column(name = "puerto")
	public String getPuerto() {
		return puerto;
	}

	public void setPuerto(String puerto) {
		this.puerto = puerto;
	}

	@Column(name = "estado")
	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	@Column(name = "fecha_cr")
	public Date getFechaCr() {
		return fechaCr;
	}

	public void setFechaCr(Date fechaCr) {
		this.fechaCr = fechaCr;
	}

	@Column(name = "sello")
	public String getSello() {
		return sello;
	}

	public void setSello(String sello) {
		this.sello = sello;
	}

	@Column(name = "tara")
	public String getTara() {
		return tara;
	}

	public void setTara(String tara) {
		this.tara = tara;
	}


	/*@Transient
	public String getAirbags() {
		return airbags;
	}

	public void setAirbags(String airbags) {
		this.airbags = airbags;
	}

	@Transient
	public String getSlipsheet() {
		return slipsheet;
	}

	public void setSlipsheet(String slipsheet) {
		this.slipsheet = slipsheet;
	}

	@Transient
	public String getRepaletizacion() {
		return repaletizacion;
	}

	public void setRepaletizacion(String repaletizacion) {
		this.repaletizacion = repaletizacion;
	}

	@Transient
	public String getPorteo() {
		return porteo;
	}

	public void setPorteo(String porteo) {
		this.porteo = porteo;
	}

	@Transient
	public String getTransporte() {
		return transporte;
	}

	public void setTransporte(String transporte) {
		this.transporte = transporte;
	}

	@Transient
	public String getGeteout() {
		return geteout;
	}

	public void setGeteout(String geteout) {
		this.geteout = geteout;
	}

	@Transient
	public String getAlmacenamiento() {
		return almacenamiento;
		
	}

	public void setAlmacenamiento(String almacenamiento) {
		this.almacenamiento = almacenamiento;
		
	}

	@Transient
	public String getTotal() {
		return total;
		
	}

	public void setTotal(String total) {
		this.total = total;
		
	}

	@Transient
	public String getRecepcion() {
		return recepcion;
		
	}

	public void setRecepcion(String recepcion) {
		this.recepcion = recepcion;
		
	}*/
	
	
	
}
