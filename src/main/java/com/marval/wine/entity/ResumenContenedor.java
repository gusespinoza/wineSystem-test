package com.marval.wine.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;

@Entity
@SqlResultSetMapping(name = "ResumenContenedorResult", entities = { @EntityResult(

		entityClass = ResumenContenedor.class, fields = {
				@FieldResult(name = "id", column = "id"), 
				@FieldResult(name = "eti", column = "eti"),
				@FieldResult(name = "posicion", column = "posicion"),
				@FieldResult(name = "owner_id", column = "owner_id"),
				@FieldResult(name = "cod_producto", column = "cod_producto"),
				@FieldResult(name = "producto", column = "producto"),
				@FieldResult(name = "cajas_pallet", column = "cajas_pallet"),
				@FieldResult(name = "bot_caja", column = "bot_caja"),
				@FieldResult(name = "cosecha", column = "cosecha"),
				@FieldResult(name = "fecha_recepcion", column = "fecha_recepcion"),
				@FieldResult(name = "fecha_conso", column = "fecha_conso"),
				@FieldResult(name = "dias", column = "dias"),
				@FieldResult(name = "semanas", column = "semanas"),
				
				@FieldResult(name = "importadora", column = "importadora"),
				@FieldResult(name = "pallet_contenedor", column = "pallet_contenedor"),
				
				@FieldResult(name = "airbags", column = "airbags"),
				@FieldResult(name = "slipsheet", column = "slipsheet"),
				@FieldResult(name = "repaletizacion", column = "repaletizacion"),
				@FieldResult(name = "porteo", column = "porteo"),
				@FieldResult(name = "transp", column = "transp"),
				@FieldResult(name = "recepcion", column = "recepcion"),
				@FieldResult(name = "almacenamiento_jfh", column = "almacenamiento_jfh"),
				@FieldResult(name = "almacenamiento_vina", column = "almacenamiento_vina")
		}) })

public class ResumenContenedor implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String etiqueta;
	private BigDecimal posicion;
	private String owner_id;
	private String cod_producto;
	private String producto;
	private BigDecimal cajas_pallet;
	private String bot_caja;
	private String cosecha;
	private Date fecha_recepcion;
	private Date fecha_conso;
	private Integer dias;
	private Integer semanas;
	
	private String importadora;
	private BigDecimal palletContenedor;
	
	private BigDecimal airbags;
	private BigDecimal slipsheet;
	private BigDecimal repaletizacion;
	private BigDecimal porteo;
	private BigDecimal transporte;
	
	private BigDecimal almacenamientoJFH;
	private BigDecimal almacenamientoVina;
	private BigDecimal recepcion;
	
	

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "eti", unique = true)
	public String getEtiqueta() {
		return etiqueta;
	}

	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}

	@Column(name = "owner_id")
	public String getOwner_id() {
		return owner_id;
	}

	public void setOwner_id(String owner_id) {
		this.owner_id = owner_id;
	}

	@Column(name = "cod_producto")
	public String getCod_producto() {
		return cod_producto;
	}

	public void setCod_producto(String cod_producto) {
		this.cod_producto = cod_producto;
	}

	@Column(name = "producto")
	public String getProducto() {
		return producto;
	}

	public void setProducto(String producto) {
		this.producto = producto;
	}

	@Column(name = "bot_caja")
	public String getBot_caja() {
		return bot_caja;
	}

	public void setBot_caja(String bot_caja) {
		this.bot_caja = bot_caja;
	}

	@Column(name = "cosecha")
	public String getCosecha() {
		return cosecha;
	}

	public void setCosecha(String cosecha) {
		this.cosecha = cosecha;
	}

	@Column(name = "fecha_recepcion")
	public Date getFecha_recepcion() {
		return fecha_recepcion;
	}

	public void setFecha_recepcion(Date fecha_recepcion) {
		this.fecha_recepcion = fecha_recepcion;
	}

	@Column(name = "fecha_conso")
	public Date getFecha_conso() {
		return fecha_conso;
	}

	public void setFecha_conso(Date fecha_conso) {
		this.fecha_conso = fecha_conso;
	}

	@Column(name = "dias")
	public Integer getDias() {
		return dias;
	}

	public void setDias(Integer dias) {
		this.dias = dias;
	}

	@Column(name = "semanas")
	public Integer getSemanas() {
		return semanas;
	}

	public void setSemanas(Integer semanas) {
		this.semanas = semanas;
	}

	
	@Column(name = "airbags")
	public BigDecimal getAirbags() {
		return airbags;
	}

	public void setAirbags(BigDecimal airbags) {
		this.airbags = airbags;
	}

	@Column(name = "slipsheet")
	public BigDecimal getSlipsheet() {
		return slipsheet;
	}

	public void setSlipsheet(BigDecimal slipsheet) {
		this.slipsheet = slipsheet;
	}

	@Column(name = "repaletizacion")
	public BigDecimal getRepaletizacion() {
		return repaletizacion;
	}

	public void setRepaletizacion(BigDecimal repaletizacion) {
		this.repaletizacion = repaletizacion;
	}

	@Column(name = "porteo")
	public BigDecimal getPorteo() {
		return porteo;
	}

	public void setPorteo(BigDecimal porteo) {
		this.porteo = porteo;
	}

	@Column(name = "transp")
	public BigDecimal getTransporte() {
		return transporte;
	}

	public void setTransporte(BigDecimal transporte) {
		this.transporte = transporte;
	}

	@Column(name = "almacenamiento_jfh")
	public BigDecimal getAlmacenamientoJFH() {
		return almacenamientoJFH;
	}

	public void setAlmacenamientoJFH(BigDecimal almacenamientoJFH) {
		this.almacenamientoJFH = almacenamientoJFH;
	}

	@Column(name = "almacenamiento_vina")
	public BigDecimal getAlmacenamientoVina() {
		return almacenamientoVina;
	}

	public void setAlmacenamientoVina(BigDecimal almacenamientoVina) {
		this.almacenamientoVina = almacenamientoVina;
	}

	@Column(name = "recepcion")
	public BigDecimal getRecepcion() {
		return recepcion;
	}

	public void setRecepcion(BigDecimal recepcion) {
		this.recepcion = recepcion;
	}

	@Column(name = "importadora")
	public String getImportadora() {
		return importadora;
	}

	public void setImportadora(String importadora) {
		this.importadora = importadora;
	}

	@Column(name = "pallet_contenedor")
	public BigDecimal getPalletContenedor() {
		return palletContenedor;
	}

	public void setPalletContenedor(BigDecimal palletContenedor) {
		this.palletContenedor = palletContenedor;
	}

	@Column(name = "cajas_pallet")
	public BigDecimal getCajas_pallet() {
		return cajas_pallet;
	}

	public void setCajas_pallet(BigDecimal cajas_pallet) {
		this.cajas_pallet = cajas_pallet;
	}

	@Column(name = "posicion")
	public BigDecimal getPosicion() {
		return posicion;
	}

	public void setPosicion(BigDecimal posicion) {
		this.posicion = posicion;
	}

	
}
