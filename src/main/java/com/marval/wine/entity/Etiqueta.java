package com.marval.wine.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.transaction.Transactional;




@Entity
@Table(name = "etiqueta", schema = "dbo")
public class Etiqueta implements java.io.Serializable {

private static final long serialVersionUID = 1L;

	
	private	Integer id;
	private PurchaseOrderDetalle detallePo;
	private String pasillo;
	private String altura;
	private String posicion;
	private String usuarioCr;
	private String usuarioUp;
	private Date fechaCr;
	private Date fechaUp;
	private String estado; 
	private String historialRepaletizado;
	private Boolean repaletizado;
	private Boolean asignado;
	private String nroEtiqueta;
	private Integer cajasPallet;
	private String nroContenedor;
	private String nroEtiquetaAnt;
	private Date fechaRepaletizado;
	private String qr;
	
	private Boolean slipsheet;
	 
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
	@JoinColumn(name = "id_detalle_po")
	public PurchaseOrderDetalle getDetallePo() {
		return detallePo;
	}
	public void setDetallePo(PurchaseOrderDetalle detallePo) {
		this.detallePo = detallePo;
	}
	
	@Column(name = "pasillo", nullable = true)
	public String getPasillo() {
		return pasillo;
	}
	public void setPasillo(String pasillo) {
		this.pasillo = pasillo;
	}
	
	@Column(name = "altura", nullable = true)
	public String getAltura() {
		return altura;
	}
	public void setAltura(String altura) {
		this.altura = altura;
	}
	
	@Column(name = "posicion", nullable = true)
	public String getPosicion() {
		return posicion;
	}
	public void setPosicion(String posicion) {
		this.posicion = posicion;
	}
	
	@Column(name = "usuario_cr", nullable = true)
	public String getUsuarioCr() {
		return usuarioCr;
	}
	public void setUsuarioCr(String usuarioCr) {
		this.usuarioCr = usuarioCr;
	}
	
	@Column(name = "usuario_up", nullable = true)
	public String getUsuarioUp() {
		return usuarioUp;
	}
	public void setUsuarioUp(String usuarioUp) {
		this.usuarioUp = usuarioUp;
	}
	
	@Column(name = "fecha_cr", nullable = true)
	public Date getFechaCr() {
		return fechaCr;
	}
	public void setFechaCr(Date fechaCr) {
		this.fechaCr = fechaCr;
	}
	
	@Column(name = "fecha_up", nullable = true)
	public Date getFechaUp() {
		return fechaUp;
	}
	
	public void setFechaUp(Date fechaUp) {
		this.fechaUp = fechaUp;
	}
	
	@Column(name = "estado", nullable = true)
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	@Column(name = "historial_repaletizado", nullable = true)
	public String getHistorialRepaletizado() {
		return historialRepaletizado;
	}
	public void setHistorialRepaletizado(String historialRepaletizado) {
		this.historialRepaletizado = historialRepaletizado;
	}
		
	@Column(name = "nro_etiqueta", nullable = true)
	public String getNroEtiqueta() {
		return nroEtiqueta;
	}
	public void setNroEtiqueta(String nroEtiqueta) {
		this.nroEtiqueta = nroEtiqueta;
	}
	
	@Column(name = "asignado", nullable = true)
	public Boolean getAsignado() {
		return asignado;
	}
	public void setAsignado(Boolean asignado) {
		this.asignado = asignado;
	}
	
	@Column(name = "repaletizado", nullable = true)
	public Boolean getRepaletizado() {
		return repaletizado;
	}
	public void setRepaletizado(Boolean repaletizado) {
		this.repaletizado = repaletizado;
	}
	
	@Column(name = "cajas_pallet", nullable = true)
	public Integer getCajasPallet() {
		return cajasPallet;
	}
	public void setCajasPallet(Integer cajasPallet) {
		this.cajasPallet = cajasPallet;
	}
	
	@Column(name = "nro_contenedor", nullable = true)
	public String getNroContenedor() {
		return nroContenedor;
	}
	public void setNroContenedor(String nroContenedor) {
		this.nroContenedor = nroContenedor;
	}
	
	@Column(name = "slipsheet")
	public Boolean getSlipsheet() {
		return slipsheet;
	}
	public void setSlipsheet(Boolean slipsheet) {
		this.slipsheet = slipsheet;
	}
	
	@Column(name = "nro_etiqueta_ant")
	public String getNroEtiquetaAnt() {
		return nroEtiquetaAnt;
	}
	public void setNroEtiquetaAnt(String nroEtiquetaAnt) {
		this.nroEtiquetaAnt = nroEtiquetaAnt;
	}
	
	@Column(name = "fecha_repaletizado", nullable = true)
	public Date getFechaRepaletizado() {
		return fechaRepaletizado;
	}
	public void setFechaRepaletizado(Date fechaRepaletizado) {
		this.fechaRepaletizado = fechaRepaletizado;
	}
	
	@Transient
	public String getQr() {
		return qr;
	}
	public void setQr(String qr) {
		this.qr = qr;
	} 
	
	
	
	

}
