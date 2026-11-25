package com.marval.wine.entity;

import static javax.persistence.GenerationType.IDENTITY;

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
@SqlResultSetMapping(name = "MensajeResult", entities = { @EntityResult(

		entityClass = Existencia.class, fields = { @FieldResult(name = "id", column = "id"),
				@FieldResult(name = "fecha_cr", column = "fecha_cr"),
				@FieldResult(name = "usuario_cr", column = "usuario_cr"),
				@FieldResult(name = "archivo", column = "archivo"),
				@FieldResult(name = "texto", column = "texto")}) })

public class MensajeProcesado implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Date fechaCr;
	private String usuarioCr;
	private String archivo;
	private String texto;
	
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "usuario_cr")
	public String getUsuarioCr() {
		return usuarioCr;
	}

	public void setUsuarioCr(String usuarioCr) {
		this.usuarioCr = usuarioCr;
	}

	@Column(name = "fecha_cr")
	public Date getFechaCr() {
		return fechaCr;
	}

	public void setFechaCr(Date fechaCr) {
		this.fechaCr = fechaCr;
	}

	@Column(name = "archivo")
	public String getArchivo() {
		return archivo;
	}

	public void setArchivo(String archivo) {
		this.archivo = archivo;
	}

	@Column(name = "texto")
	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	
}
