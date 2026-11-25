package com.marval.wine.entity;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;

@Entity
@SqlResultSetMapping(name = "RepaletizadoResult", entities = { @EntityResult(

		entityClass = Repaletizado.class, fields = { 
				@FieldResult(name = "id", column = "id"),
				@FieldResult(name = "vina", column = "vina"),
				@FieldResult(name = "codigo_producto", column = "codigo_producto"),
				@FieldResult(name = "producto", column = "producto"),
				@FieldResult(name = "cosecha", column = "cosecha"),
				@FieldResult(name = "botellas_caja", column = "botellas_caja"),
				@FieldResult(name = "etiqueta", column = "etiqueta"),
				@FieldResult(name = "cajas_pallet", column = "cajas_pallet"),
				@FieldResult(name = "lote", column = "lote"),
				@FieldResult(name = "slipsheet", column = "slipsheet")}) })

public class Repaletizado implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String vina;
	private String codigoProducto;
	private String producto;
	private String cosecha;
	private String etiqueta;
	private String lote;
	private Integer cajasPallet;
	private Integer botellasPorCaja;
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
	
	
	@Column(name = "etiqueta")
	public String getEtiqueta() {
		return etiqueta;
	}
	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}
	

	@Column(name = "lote")
	public String getLote() {
		return lote;
	}

	public void setLote(String lote) {
		this.lote = lote;
	}

	@Column(name = "cajas_pallet")
	public Integer getCajasPallet() {
		return cajasPallet;
	}

	public void setCajasPallet(Integer cajasPallet) {
		this.cajasPallet = cajasPallet;
	}

	@Column(name = "botellas_caja")
	public Integer getBotellasPorCaja() {
		return botellasPorCaja;
	}

	public void setBotellasPorCaja(Integer botellasPorCaja) {
		this.botellasPorCaja = botellasPorCaja;
	}

	@Column(name = "slipsheet")
	public Boolean getSlipsheet() {
		return slipsheet;
	}

	public void setSlipsheet(Boolean slipsheet) {
		this.slipsheet = slipsheet;
	}
		

}
