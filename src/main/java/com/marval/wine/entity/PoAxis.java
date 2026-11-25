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
@SqlResultSetMapping(name = "PoAxisResult", entities = { @EntityResult(
		entityClass = PoAxis.class, fields = { @FieldResult(name = "id", column = "id"),
				@FieldResult(name = "po", column = "po"),
				@FieldResult(name = "or_ref", column = "or_ref"),
				@FieldResult(name = "cosecha", column = "cosecha"),
				@FieldResult(name = "cod_prod", column = "cod_prod"),
				@FieldResult(name = "prod", column = "prod"),
				@FieldResult(name = "un_inf", column = "un_inf"),
				@FieldResult(name = "bot_caja", column = "bot_caja"),
				@FieldResult(name = "bot_ml", column = "bot_ml"),
				@FieldResult(name = "un_recep", column = "un_recep")})})
public class PoAxis implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String po;
	private String orRef;
	private String cosecha;
	private String codProd;
	private String prod;
	private String unInf;
	private Integer botCaja;
	private Integer botMl;
	private Integer unRecep;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "po")
	public String getPo() {
		return po;
	}
	public void setPo(String po) {
		this.po = po;
	}
	
	@Column(name = "or_ref")
	public String getOrRef() {
		return orRef;
	}
	public void setOrRef(String orRef) {
		this.orRef = orRef;
	}
	
	@Column(name = "cosecha")
	public String getCosecha() {
		return cosecha;
	}
	public void setCosecha(String cosecha) {
		this.cosecha = cosecha;
	}
	
	@Column(name = "cod_prod")
	public String getCodProd() {
		return codProd;
	}
	public void setCodProd(String codProd) {
		this.codProd = codProd;
	}
	
	@Column(name = "prod")
	public String getProd() {
		return prod;
	}
	public void setProd(String prod) {
		this.prod = prod;
	}
	
	@Column(name = "un_inf")
	public String getUnInf() {
		return unInf;
	}
	public void setUnInf(String unInf) {
		this.unInf = unInf;
	}
	
	@Column(name = "bot_caja")
	public Integer getBotCaja() {
		return botCaja;
	}
	public void setBotCaja(Integer botCaja) {
		this.botCaja = botCaja;
	}
	
	@Column(name = "bot_ml")
	public Integer getBotMl() {
		return botMl;
	}
	public void setBotMl(Integer botMl) {
		this.botMl = botMl;
	}
	
	@Column(name = "un_recep")
	public Integer getUnRecep() {
		return unRecep;
	}
	public void setUnRecep(Integer unRecep) {
		this.unRecep = unRecep;
	}
	
}
