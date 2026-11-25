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
@SqlResultSetMapping(name = "PackingListResult", entities = { @EntityResult(
		entityClass = MensajeBodega.class, fields = { @FieldResult(name = "id", column = "id"),
				@FieldResult(name = "pos", column = "pos"),
				@FieldResult(name = "label", column = "label"),
				@FieldResult(name = "owner_id", column = "owner_id"),
				@FieldResult(name = "product_number", column = "product_number"),
				@FieldResult(name = "product_description", column = "product_description"),
				@FieldResult(name = "vintage", column = "vintage"),
				@FieldResult(name = "cantidad", column = "cantidad")})})
public class PackingList implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer pos;
	private String label;
	private String ownerId;
	private String productNumber;
	private String productDescription;
	private String vintage;
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
	
	
	
	@Column(name = "pos")
	public Integer getPos() {
		return pos;
	}
	public void setPos(Integer pos) {
		this.pos = pos;
	}
	
	@Column(name = "label")
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
	@Column(name = "owner_id")
	public String getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	
	@Column(name = "product_number")
	public String getProductNumber() {
		return productNumber;
	}
	public void setProductNumber(String productNumber) {
		this.productNumber = productNumber;
	}
	
	@Column(name = "product_description")
	public String getProductDescription() {
		return productDescription;
	}
	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}
	
	@Column(name = "vintage")
	public String getVintage() {
		return vintage;
	}
	public void setVintage(String vintage) {
		this.vintage = vintage;
	}
	
	@Column(name = "cantidad")
	public Integer getCantidad() {
		return cantidad;
	}
	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}
	
}
