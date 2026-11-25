package com.marval.wine.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = AjusteInventario.class)
@Table(name = "ajuste_inventario", schema = "dbo")
public class AjusteInventario implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String product_number;
	private String vintage;
	private String lote;
	private String cantidadAnterior;
	private String cantidadNueva;
	private String registro_po;
	private String observaciones;
	private Date fechaCr;
	private String usuarioCr;
	private String texto;
	private String nombreTxt;
	private String motivo;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "cantidad_anterior", nullable = true)
	public String getCantidadAnterior() {
		return cantidadAnterior;
	}

	public void setCantidadAnterior(String cantidadAnterior) {
		this.cantidadAnterior = cantidadAnterior;
	}

	@Column(name = "cantidad_nueva", nullable = true)
	public String getCantidadNueva() {
		return cantidadNueva;
	}

	public void setCantidadNueva(String cantidadNueva) {
		this.cantidadNueva = cantidadNueva;
	}

	@Column(name = "observaciones", nullable = true)
	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	@Column(name = "fecha_cr", nullable = true)
	public Date getFechaCr() {
		return fechaCr;
	}

	public void setFechaCr(Date fechaCr) {
		this.fechaCr = fechaCr;
	}

	@Column(name = "usuario_cr", nullable = true)
	public String getUsuarioCr() {
		return usuarioCr;
	}

	public void setUsuarioCr(String usuarioCr) {
		this.usuarioCr = usuarioCr;
	}

	@Column(name = "product_number", nullable = true)
	public String getProduct_number() {
		return product_number;
	}

	public void setProduct_number(String product_number) {
		this.product_number = product_number;
	}

	@Column(name = "vintage", nullable = true)
	public String getVintage() {
		return vintage;
	}

	public void setVintage(String vintage) {
		this.vintage = vintage;
	}

	@Column(name = "registro_po", nullable = true)
	public String getRegistro_po() {
		return registro_po;
	}

	public void setRegistro_po(String registro_po) {
		this.registro_po = registro_po;
	}

	@Column(name = "lote", nullable = true)
	public String getLote() {
		return lote;
	}

	public void setLote(String lote) {
		this.lote = lote;
	}

	@Column(name = "texto", nullable = true)
	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	@Column(name = "nombre_txt", nullable = true)
	public String getNombreTxt() {
		return nombreTxt;
	}

	
	public void setNombreTxt(String nombreTxt) {
		this.nombreTxt = nombreTxt;
	}

	@Column(name = "motivo", nullable = true)
	public String getMotivo() {
		return motivo;
		
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
		
	}

}
