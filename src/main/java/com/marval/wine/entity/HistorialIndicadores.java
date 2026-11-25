package com.marval.wine.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.marval.wine.config.AppConfig;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "idHistorial",scope = HistorialIndicadores.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "historial_indicadores", schema = "dbo")
public class HistorialIndicadores implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4482503942437434634L;
	private Integer idHistorial;
	private Date fechCambio;
	private String moneda;
	private BigDecimal valorCambio;
	private Date fechaCr;
	private String usuarioCr;
	private Date fechaUp;
	private String usuarioUp;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_historial", unique = true, nullable = false)
	public Integer getIdHistorial() {
		return idHistorial;
	}

	public void setIdHistorial(Integer idHistorial) {
		this.idHistorial = idHistorial;
	}

	@Column(name = "fech_cambio", nullable = false)
	public Date getFechCambio() {
		return fechCambio;
	}

	public void setFechCambio(Date fechCambio) {
		this.fechCambio = fechCambio;
	}

	@Column(name = "moneda", nullable = false)
	public String getMoneda() {
		return moneda;
	}

	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}

	@CreationTimestamp
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss", timezone = "America/Santiago")
	@Column(name = "fecha_cr", nullable = false)
	public Date getFechaCr() {
		return fechaCr;
	}

	public void setFechaCr(Date fechaCr) {
		this.fechaCr = fechaCr;
	}

	@Column(name = "valor_cambio", nullable = false)
	public BigDecimal getValorCambio() {
		return valorCambio;
	}

	public void setValorCambio(BigDecimal valorCambio) {
		this.valorCambio = valorCambio;
	}

	@Column(name = "usuario_cr", nullable = false)
	public String getUsuarioCr() {
		return usuarioCr;
	}

	public void setUsuarioCr(String usuarioCr) {
		this.usuarioCr = usuarioCr;
	}

	@UpdateTimestamp
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss", timezone = "America/Santiago")
	@Column(name = "fecha_up")
	public Date getFechaUp() {
		return fechaUp;
	}

	public void setFechaUp(Date fechaUp) {
		this.fechaUp = fechaUp;
	}

	@Column(name = "usuario_up")
	public String getUsuarioUp() {
		return usuarioUp;
	}

	public void setUsuarioUp(String usuarioUp) {
		this.usuarioUp = usuarioUp;
	}

}
