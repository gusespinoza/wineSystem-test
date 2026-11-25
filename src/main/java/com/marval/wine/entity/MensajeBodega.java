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
@SqlResultSetMapping(name = "MensajeBodegaResult", entities = { @EntityResult(
		entityClass = MensajeBodega.class, fields = { @FieldResult(name = "id", column = "id"),
				@FieldResult(name = "vina", column = "vina"),
				@FieldResult(name = "cod_producto", column = "cod_producto"),
				@FieldResult(name = "cosecha", column = "cosecha"),
				@FieldResult(name = "estado", column = "estado"),
				@FieldResult(name = "cantidad", column = "cantidad")})})
public class MensajeBodega implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String vina; 
	private String codProducto;
	private String cosecha; 
	private String estado;
	private Integer cantidad;
	
	
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
	
	@Column(name = "cod_producto")
	public String getCodProducto() {
		return codProducto;
	}
	public void setCodProducto(String codProducto) {
		this.codProducto = codProducto;
	}
	
	@Column(name = "cosecha")
	public String getCosecha() {
		return cosecha;
	}
	public void setCosecha(String cosecha) {
		this.cosecha = cosecha;
	}
	
	@Column(name = "estado")
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	@Column(name = "cantidad")
	public Integer getCantidad() {
		return cantidad;
	}
	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}
		
}
