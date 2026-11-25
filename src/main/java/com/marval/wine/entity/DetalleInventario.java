package com.marval.wine.entity;

import java.math.BigInteger;

import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Transient;

@Entity
@SqlResultSetMapping(name = "DetalleInventarioResult", entities = { @EntityResult(

		entityClass = DetalleInventario.class, fields = {}) })

public class DetalleInventario implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer cantidadAjustes;
	private BigInteger nuevaCantidad;
	
	

	@Transient
	public Integer getCantidadAjustes() {
		return cantidadAjustes;
	}

	public void setCantidadAjustes(Integer cantidadAjustes) {
		this.cantidadAjustes = cantidadAjustes;
	}

	@Transient
	public BigInteger getNuevaCantidad() {
		return nuevaCantidad;
	}

	public void setNuevaCantidad(BigInteger nuevaCantidad) {
		this.nuevaCantidad = nuevaCantidad;
	}

}
