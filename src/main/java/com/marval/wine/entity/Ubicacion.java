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
import javax.persistence.Transient;

@Entity
@SqlResultSetMapping(name = "UbicacionResult", entities = { @EntityResult(

		entityClass = Ubicacion.class, fields = { 
				@FieldResult(name = "id", column = "id"),
				@FieldResult(name = "id_detalle_po", column = "id_detalle_po"),
				@FieldResult(name = "etiqueta", column = "etiqueta"),
				@FieldResult(name = "vina", column = "vina"),
				@FieldResult(name = "producto", column = "producto"),
				@FieldResult(name = "pasillo", column = "pasillo"),
				@FieldResult(name = "posicion", column = "posicion"),
				@FieldResult(name = "altura", column = "altura"),
				@FieldResult(name = "lote", column = "lote"),
				@FieldResult(name = "estado_et", column = "estado_et"),
				@FieldResult(name = "estado_pud", column = "estado_pud"),
				@FieldResult(name = "cantidad", column = "cantidad")}) })

public class Ubicacion implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer id_detalle_po;
	
	private String etiqueta;
	private String vina;
	private String producto;
	private String pasillo;
	private String altura;
	private String posicion;
	private String lote;
	
	//estado etiqeuta
	private String estadoEt;
	//estado po
	private String estadoPud;
	//cantida po
	private String cantidad;
	
	private String usuarioUp;
	private Date fechaUp;
	
	private Integer movimiento;
	private String fert;
	private String tipoDoc;
	private Integer nroDoc;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "etiqueta", nullable = true)
	public String getEtiqueta() {
		return etiqueta;
	}

	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}

	@Column(name = "vina", nullable = true)
	public String getVina() {
		return vina;
	}

	public void setVina(String vina) {
		this.vina = vina;
	}

	@Column(name = "producto", nullable = true)
	public String getProducto() {
		return producto;
	}

	public void setProducto(String producto) {
		this.producto = producto;
	}

	@Column(name = "pasillo", nullable = true)
	public String getPasillo() {
		return pasillo;
	}

	public void setPasillo(String pasillo) {
		this.pasillo = pasillo;
	}

	@Column(name = "posicion", nullable = true)
	public String getPosicion() {
		return posicion;
	}

	public void setPosicion(String posicion) {
		this.posicion = posicion;
	}

	@Column(name = "altura", nullable = true)
	public String getAltura() {
		return altura;
	}

	public void setAltura(String altura) {
		this.altura = altura;
	}

	@Column(name = "id_detalle_po", nullable = true)
	public Integer getId_detalle_po() {
		return id_detalle_po;
	}

	public void setId_detalle_po(Integer id_detalle_po) {
		this.id_detalle_po = id_detalle_po;
	}

	@Column(name = "cantidad", nullable = true)
	public String getCantidad() {
		return cantidad;
	}

	public void setCantidad(String cantidad) {
		this.cantidad = cantidad;
	}
	
	@Column(name = "estado_pud", nullable = true)
	public String getEstadoPud() {
		return estadoPud;
	}

	public void setEstadoPud(String estadoPud) {
		this.estadoPud = estadoPud;
	}

	@Column(name = "estado_et", nullable = true)
	public String getEstadoEt() {
		return estadoEt;
	}

	public void setEstadoEt(String estadoEt) {
		this.estadoEt = estadoEt;
	}

	@Transient
	public String getUsuarioUp() {
		return usuarioUp;
	}

	public void setUsuarioUp(String usuarioUp) {
		this.usuarioUp = usuarioUp;
	}

	@Transient
	public Date getFechaUp() {
		return fechaUp;
	}

	public void setFechaUp(Date fechaUp) {
		this.fechaUp = fechaUp;
	}

	@Column(name = "lote", nullable = true)
	public String getLote() {
		return lote;
	}

	public void setLote(String lote) {
		this.lote = lote;
	}

	@Transient
	public Integer getMovimiento() {
		return movimiento;
	}

	public void setMovimiento(Integer movimiento) {
		this.movimiento = movimiento;
	}

	

	
	
}
