package com.marval.wine.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "mensaje", schema = "dbo")
public class Mensaje implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id; 
	private Date fechaCr;
	private String usuarioCr;
	private String tipo;
	private String nombreArchivo;
	private String mensaje;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	
	@Column(name = "tipo", nullable = true)
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
	@Column(name = "nombre_archivo", nullable = true)
	public String getNombreArchivo() {
		return nombreArchivo;
	}
	public void setNombreArchivo(String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
	}
	
	@Column(name = "mensaje", nullable = true)
	public String getMensaje() {
		return mensaje;
	}
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	
	
}
