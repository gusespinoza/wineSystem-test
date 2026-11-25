package com.marval.wine.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Transient;

@Entity
@SqlResultSetMapping(name = "PoAjusteResult", entities = { @EntityResult(

		entityClass = PoAjuste.class, fields = { 
				@FieldResult(name = "id", column = "id"),
				@FieldResult(name = "vina", column = "vina"),
				@FieldResult(name = "producto", column = "producto"),
				@FieldResult(name = "producto_id", column = "producto_id"),
				@FieldResult(name = "vintage", column = "vintage"),
				@FieldResult(name = "stock", column = "stock"),
				@FieldResult(name = "estado_rep", column = "estado_rep"),
				@FieldResult(name = "total_ajustes", column = "total_ajustes"),
				@FieldResult(name = "cantidad_anterior", column = "cantidad_anterior")}) })

public class PoAjuste implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	
	private String vina;
	private String producto;
	private String producto_id;
	private String cosecha;
	private Integer stock;
	
	private Integer cantidad;
	private String tipoAjuste;
	private String estadoRecep;
	private BigDecimal totalAjustes;
	private BigDecimal cantidadAnterior;
	
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

	@Column(name = "producto", nullable = true)
	public String getProducto() {
		return producto;
	}

	public void setProducto(String producto) {
		this.producto = producto;
	}

	@Column(name = "producto_id", nullable = true)
	public String getProducto_id() {
		return producto_id;
	}

	public void setProducto_id(String producto_id) {
		this.producto_id = producto_id;
	}

	@Column(name = "stock", nullable = true)
	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	@Transient
	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

	@Transient
	public String getTipoAjuste() {
		return tipoAjuste;
	}

	public void setTipoAjuste(String tipoAjuste) {
		this.tipoAjuste = tipoAjuste;
	}

	@Column(name = "vintage", nullable = true)
	public String getCosecha() {
		return cosecha;
	}

	public void setCosecha(String cosecha) {
		this.cosecha = cosecha;
	}

	@Column(name = "estado_rep", nullable = true)
	public String getEstadoRecep() {
		return estadoRecep;
	}

	public void setEstadoRecep(String estadoRecep) {
		this.estadoRecep = estadoRecep;
	}


	@Column(name = "total_ajustes")
	public BigDecimal getTotalAjustes() {
		return totalAjustes;
	}

	public void setTotalAjustes(BigDecimal totalAjustes) {
		this.totalAjustes = totalAjustes;
	}

	
	@Transient
	public String getMotivo() {
		return motivo;
		
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
		
	}


	@Column(name = "vina")
	public String getVina() {
		return vina;
	}

	public void setVina(String vina) {
		this.vina = vina;
	}

	@Column(name = "cantidad_anterior")
	public BigDecimal getCantidadAnterior() {
		return cantidadAnterior;
	}

	public void setCantidadAnterior(BigDecimal cantidadAnterior) {
		this.cantidadAnterior = cantidadAnterior;
	}
	
}

