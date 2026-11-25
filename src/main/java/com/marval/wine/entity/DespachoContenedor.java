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
@SqlResultSetMapping(name = "DespachoResult", entities = { @EntityResult(
		entityClass = DespachoContenedor.class, fields = { @FieldResult(name = "id", column = "id"),
				@FieldResult(name = "orden_referencia", column = "orden_referencia"),
				@FieldResult(name = "vina", column = "vina"),
				@FieldResult(name = "producto", column = "producto"),
				@FieldResult(name = "cosecha", column = "cosecha"),
				@FieldResult(name = "cajas_recepcionadas", column = "cajas_recepcionadas"),
				@FieldResult(name = "cajas_consolidadas", column = "cajas_consolidadas"),
				@FieldResult(name = "fecha_recepcion", column = "fecha_recepcion"),
				@FieldResult(name = "lote", column = "lote"),
				@FieldResult(name = "idPo", column = "idPo"),
				@FieldResult(name = "tipo_recep", column = "tipo_recep"),
				@FieldResult(name = "tipo_doc", column = "tipo_doc"),
				@FieldResult(name = "num_doc", column = "num_doc"),
				@FieldResult(name = "fecha_doc", column = "fecha_doc"),
				@FieldResult(name = "pallet", column = "pallet")})})

public class DespachoContenedor implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer idPo;
	private String ordenReferencia;
	private String vina; 
	private String producto; 
	private String cosecha;
	private String cajasRecepcionadas;
	private Integer cajasConsolidas;
	private Date fechaRecepcion;
	private String lote;
	private String pallet;
	private String numeroProducto;
	private String tipoRecepcion;
	private Integer numDoc;
	private String tipoDoc;
	private Date fechaDoc;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "orden_referencia")
	public String getOrdenReferencia() {
		return ordenReferencia;
	}
	public void setOrdenReferencia(String ordenReferencia) {
		this.ordenReferencia = ordenReferencia;
	}
	
	@Column(name = "vina")
	public String getVina() {
		return vina;
	}
	public void setVina(String vina) {
		this.vina = vina;
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
	
	@Column(name = "cajas_recepcionadas")
	public String getCajasRecepcionadas() {
		return cajasRecepcionadas;
	}
	public void setCajasRecepcionadas(String cajasRecepcionadas) {
		this.cajasRecepcionadas = cajasRecepcionadas;
	}
	
	@Column(name = "cajas_consolidadas")
	public Integer getCajasConsolidas() {
		return cajasConsolidas;
	}
	
	public void setCajasConsolidas(Integer cajasConsolidas) {
		this.cajasConsolidas = cajasConsolidas;
	}
	
	@Column(name = "fecha_recepcion")
	public Date getFechaRecepcion() {
		return fechaRecepcion;
	}
	
	public void setFechaRecepcion(Date fechaRecepcion) {
		this.fechaRecepcion = fechaRecepcion;
	}
	
	@Column(name = "lote")
	public String getLote() {
		return lote;
	}
	public void setLote(String lote) {
		this.lote = lote;
	}
	
	@Column(name = "pallet")
	public String getPallet() {
		return pallet;
	}
	public void setPallet(String pallet) {
		this.pallet = pallet;
	}
	
	@Column(name = "numero_producto") 
	public String getNumeroProducto() {
		return numeroProducto;
	}
	public void setNumeroProducto(String numeroProducto) {
		this.numeroProducto = numeroProducto;
	}
	
	@Column(name = "id_po") 
	public Integer getIdPo() {
		return idPo;
	}
	public void setIdPo(Integer idPo) {
		this.idPo = idPo;
	}
	
	@Column(name = "tipo_recep") 
	public String getTipoRecepcion() {
		return tipoRecepcion;
	}
	public void setTipoRecepcion(String tipoRecepcion) {
		this.tipoRecepcion = tipoRecepcion;
	}
	
	@Column(name = "num_doc") 
	public Integer getNumDoc() {
		return numDoc;
	}
	public void setNumDoc(Integer numDoc) {
		this.numDoc = numDoc;
	}
	
	@Column(name = "tipo_doc") 
	public String getTipoDoc() {
		return tipoDoc;
	}
	public void setTipoDoc(String tipoDoc) {
		this.tipoDoc = tipoDoc;
	}
	
	@Column(name = "fecha_doc") 
	public Date getFechaDoc() {
		return fechaDoc;
	}
	public void setFechaDoc(Date fechaDoc) {
		this.fechaDoc = fechaDoc;
	}
	
	
	
	
	
}
