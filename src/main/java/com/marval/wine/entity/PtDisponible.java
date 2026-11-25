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
@SqlResultSetMapping(name = "PtDisponible", entities = { @EntityResult(
		entityClass = DespachoContenedor.class, fields = { @FieldResult(name = "id", column = "id"),
				@FieldResult(name = "pt", column = "pt"),
				@FieldResult(name = "vina", column = "vina"),
				@FieldResult(name = "or_ref", column = "or_ref"),
				@FieldResult(name = "cod_prod", column = "cod_prod"),
				@FieldResult(name = "cantidad", column = "cantidad"),
				@FieldResult(name = "cosecha", column = "cosecha")})})

public class PtDisponible implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String  pt;
	private String vina;
	private String orRef;
	private String codProd;
	private Integer cantidad;
	private String cosecha;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "pt")
	public String getPt() {
		return pt;
	}
	public void setPt(String pt) {
		this.pt = pt;
	}
	
	@Column(name = "vina")
	public String getVina() {
		return vina;
	}
	public void setVina(String vina) {
		this.vina = vina;
	}
	
	@Column(name = "or_ref")
	public String getOrRef() {
		return orRef;
	}
	public void setOrRef(String orRef) {
		this.orRef = orRef;
	}
	
	@Column(name = "cod_prod")
	public String getCodProd() {
		return codProd;
	}
	public void setCodProd(String codProd) {
		this.codProd = codProd;
	}
	
	@Column(name = "cantidad")
	public Integer getCantidad() {
		return cantidad;
	}
	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}
	
	@Column(name = "cosecha")
	public String getCosecha() {
		return cosecha;
	}
	public void setCosecha(String cosecha) {
		this.cosecha = cosecha;
	}

 	
	
}