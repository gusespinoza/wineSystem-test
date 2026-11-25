package com.marval.wine.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;

@Entity
@SqlResultSetMapping(name = "ExistenciaResult", entities = { @EntityResult(

		entityClass = Existencia.class, fields = { @FieldResult(name = "id", column = "id"),
				@FieldResult(name = "vina", column = "vina"),
				@FieldResult(name = "codigo_producto", column = "codigo_producto"),
				@FieldResult(name = "producto", column = "producto"),
				@FieldResult(name = "lote", column = "lote"),
				@FieldResult(name = "cosecha", column = "cosecha"),
				@FieldResult(name = "botellas_caja", column = "botellas_caja"),
				@FieldResult(name = "etiqueta", column = "etiqueta"),
				@FieldResult(name = "etiqueta_ant", column = "etiqueta_ant"),
				@FieldResult(name = "guia_cliente", column = "guia_cliente"),
				@FieldResult(name = "cajas_pallet", column = "cajas_pallet"),
				@FieldResult(name = "fecha_recepcion", column = "fecha_recepcion"),
				@FieldResult(name = "ubicacion", column = "ubicacion"), 
				@FieldResult(name = "po_id", column = "po_id"),
				@FieldResult(name = "codigo_fert", column = "codigo_fert"),
				@FieldResult(name = "contenedor", column = "contenedor"),
				@FieldResult(name = "fecha_despacho", column = "fecha_despacho"),
				@FieldResult(name = "estado_et", column = "estado_et")}) })

public class Existencia implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	private String vina;
	private String codigoProducto;
	private String producto;
	private String cosecha;
	private String botellasPorCaja;
	private String etiqueta;
	private String etiquetaAnterior;
	private String guiaCliente;
	private String cajasPallet;
	private Date fechaRecepcion;
	private String ubicacion;
	private String poId;
	private String codigo_fert;
	private String lote;

	private String contenedor;
	private Date fechaDespacho;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "vina")
	public String getVina() {
		return vina;
	}

	public void setVina(String vina) {
		this.vina = vina;
	}

	@Column(name = "codigo_producto")
	public String getCodigoProducto() {
		return codigoProducto;
	}

	public void setCodigoProducto(String codigoProducto) {
		this.codigoProducto = codigoProducto;
	}

	@Column(name = "producto")
	public String getProducto() {
		return producto;
	}

	public void setProducto(String producto) {
		this.producto = producto;
	}

	@Column(name = "cosecha")
	public String getCosecha() {
		return cosecha;
	}

	public void setCosecha(String cosecha) {
		this.cosecha = cosecha;
	}

	@Column(name = "botellas_caja")
	public String getBotellasPorCaja() {
		return botellasPorCaja;
	}

	public void setBotellasPorCaja(String botellasPorCaja) {
		this.botellasPorCaja = botellasPorCaja;
	}

	@Column(name = "etiqueta")
	public String getEtiqueta() {
		return etiqueta;
	}

	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}

	@Column(name = "guia_cliente")
	public String getGuiaCliente() {
		return guiaCliente;
	}

	public void setGuiaCliente(String guiaCliente) {
		this.guiaCliente = guiaCliente;
	}

	@Column(name = "cajas_pallet")
	public String getCajasPallet() {
		return cajasPallet;
	}

	public void setCajasPallet(String cajasPallet) {
		this.cajasPallet = cajasPallet;
	}

	@Column(name = "fecha_recepcion")
	public Date getFechaRecepcion() {
		return fechaRecepcion;
	}

	public void setFechaRecepcion(Date fechaRecepcion) {
		this.fechaRecepcion = fechaRecepcion;
	}

	@Column(name = "ubicacion")
	public String getUbicacion() {
		return ubicacion;
	}

	public void setUbicacion(String ubicacion) {
		this.ubicacion = ubicacion;
	}

	@Column(name = "po_id")
	public String getPoId() {
		return poId;
	}

	public void setPoId(String poId) {
		this.poId = poId;
	}

	@Column(name = "codigo_fert")
	public String getCodigo_fert() {
		return codigo_fert;
	}

	public void setCodigo_fert(String codigo_fert) {
		this.codigo_fert = codigo_fert;
	}

	@Column(name = "contenedor")
	public String getContenedor() {
		return contenedor;
	}

	public void setContenedor(String contenedor) {
		this.contenedor = contenedor;
	}

	@Column(name = "fecha_despacho")
	public Date getFechaDespacho() {
		return fechaDespacho;
	}

	public void setFechaDespacho(Date fechaDespacho) {
		this.fechaDespacho = fechaDespacho;
	}

	@Column(name = "etiqueta_ant")
	public String getEtiquetaAnterior() {
		return etiquetaAnterior;
	}

	public void setEtiquetaAnterior(String etiquetaAnterior) {
		this.etiquetaAnterior = etiquetaAnterior;
	}

	public String getLote() {
		return lote;
	}

	@Column(name = "lote")
	public void setLote(String lote) {
		this.lote = lote;
	}
	
	 

}
