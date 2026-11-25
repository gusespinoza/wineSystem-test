package com.marval.wine.entity;
import static javax.persistence.GenerationType.IDENTITY;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;



@Entity
@Table (name = "asignacion", schema="dbo")
public class Asignacion implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	private Integer id;
	private Etiqueta etiqueta;
	private Container contenedor;
	private String usuarioCr;
	private Date fechaCr;
	private String usuarioUp;
	private Date fechaUp;
	private Integer posicion;
	private String asignado;
	private List<Etiqueta> etiquetas = new ArrayList<Etiqueta>();
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "etiqueta_id")
	public Etiqueta getEtiqueta() {
		return etiqueta;
	}
	public void setEtiqueta(Etiqueta etiqueta) {
		this.etiqueta = etiqueta;
	}
	
	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "container_id")
	public Container getContenedor() {
		return contenedor;
	}
	public void setContenedor(Container contenedor) {
		this.contenedor = contenedor;
	}
	
	@Column(name = "usuario_cr", nullable = true, length=50)
	public String getUsuarioCr() {
		return usuarioCr;
	}
	public void setUsuarioCr(String usuarioCr) {
		this.usuarioCr = usuarioCr;
	}
	
	@Column(name = "fecha_cr", nullable = true)
	public Date getFechaCr() {
		return fechaCr;
	}
	public void setFechaCr(Date fechaCr) {
		this.fechaCr = fechaCr;
	}
	
	@Column(name = "usuario_up", nullable = true, length=50)
	public String getUsuarioUp() {
		return usuarioUp;
	}
	public void setUsuarioUp(String usuarioUp) {
		this.usuarioUp = usuarioUp;
	}
	
	@Column(name = "fecha_up", nullable = true)
	public Date getFechaUp() {
		return fechaUp;
	}
	public void setFechaUp(Date fechaUp) {
		this.fechaUp = fechaUp;
	}
	@Column(name = "posicion", nullable = true)
	public Integer getPosicion() {
		return posicion;
	}
	public void setPosicion(Integer posicion) {
		this.posicion = posicion;
	}
	@Column(name = "asignado")
	public String getAsignado() {
		return asignado;
	}
	public void setAsignado(String asignado) {
		this.asignado = asignado;
	}
	
	@javax.persistence.Transient
	public List<Etiqueta> getEtiquetas() {
		return etiquetas;
	}
	public void setEtiquetas(List<Etiqueta> etiquetas) {
		this.etiquetas = etiquetas;
	}
	
}
