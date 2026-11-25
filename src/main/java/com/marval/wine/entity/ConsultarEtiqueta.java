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
@SqlResultSetMapping(name = "ConsultarEtiquetaResult", entities = { @EntityResult(
		entityClass = ConsultarEtiqueta.class, fields = { 
				@FieldResult(name = "id", column = "id"),
				@FieldResult(name = "etiqueta", column = "etiqueta"),
				@FieldResult(name = "vina", column = "vina"),
				@FieldResult(name = "producto", column = "producto"),
				@FieldResult(name = "pasillo", column = "pasillo"),
				@FieldResult(name = "posicion", column = "posicion"),
				@FieldResult(name = "altura", column = "altura"),
				@FieldResult(name = "lote", column = "lote"),
				@FieldResult(name = "estado_et", column = "estado_et"),
				@FieldResult(name = "estado_pud", column = "estado_pud"),
				@FieldResult(name = "cantidad", column = "cantidad"),
				@FieldResult(name = "cod_producto", column = "cod_producto"),
				@FieldResult(name = "cajas_pallet", column = "cajas_pallet"),
				@FieldResult(name = "bot_caja", column = "bot_caja"),
				@FieldResult(name = "cap_en_cc", column = "cap_en_cc"),
				@FieldResult(name = "recepcion", column = "recepcion"),
				@FieldResult(name = "repaletizado", column = "repaletizado"),
				@FieldResult(name = "purchase_order_id", column = "purchase_order_id"),
				@FieldResult(name = "id_pud", column = "id_pud"),
				@FieldResult(name = "fert", column = "fert"),
				@FieldResult(name = "fecha_recepcion", column = "fecha_recepcion"),
				@FieldResult(name = "owner_id", column = "owner_id"),
				@FieldResult(name = "tipo_doc", column = "tipo_doc"),
				@FieldResult(name = "nro_doc", column = "nro_doc"),
				@FieldResult(name = "etiqueta_ant", column = "etiqueta_ant")}) })

public class ConsultarEtiqueta implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	
	private String etiqueta;
	private String vina;
	private String producto;
	private String pasillo;
	private String altura;
	private String posicion;
	private String lote;
	
	//TODO
	private String repaletizado;
	private Boolean slipsheet;
	private String cosecha;
	private String cajasPorPalet;
	private String botellasPorCaja;
	private Date recepcion;
	private String capacidadEnCc;
	private String qr; 
	
	//estado etiqeuta
	private String estadoEt;
	//estado po
	private String estadoPud;
	//cantida po
	private String cantidad;
	
	private String usuarioUp;
	private Date fechaUp;
	
	private String codProducto;
	private String poId;
	private String podId;
	
	private String fert;
	private Date fechaRecepcion;
	private String ownerId;
	
	private String nroDoc;
	private String tipoDoc;
	
	private String etiquetaAnterior;
	private String motivoAnulacion;
	
	
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

	@Column(name = "repaletizado", nullable = true)
	public String getRepaletizado() {
		return repaletizado;
	}

	public void setRepaletizado(String repaletizado) {
		this.repaletizado = repaletizado;
	}

	@Column(name = "slipsheet", nullable = true)
	public Boolean getSlipsheet() {
		return slipsheet;
	}

	public void setSlipsheet(Boolean slipsheet) {
		this.slipsheet = slipsheet;
	}

	@Column(name = "cosecha", nullable = true)
	public String getCosecha() {
		return cosecha;
	}

	public void setCosecha(String cosecha) {
		this.cosecha = cosecha;
	}

	@Column(name = "cajas_pallet", nullable = true)
	public String getCajasPorPalet() {
		return cajasPorPalet;
	}

	public void setCajasPorPalet(String cajasPorPalet) {
		this.cajasPorPalet = cajasPorPalet;
	}

	@Column(name = "bot_caja", nullable = true)
	public String getBotellasPorCaja() {
		return botellasPorCaja;
	}

	public void setBotellasPorCaja(String botellasPorCaja) {
		this.botellasPorCaja = botellasPorCaja;
	}

	@Column(name = "recepcion", nullable = true)
	public Date getRecepcion() {
		return recepcion;
	}

	public void setRecepcion(Date recepcion) {
		this.recepcion = recepcion;
	}

	@Column(name = "cap_en_cc", nullable = true)
	public String getCapacidadEnCc() {
		return capacidadEnCc;
	}

	public void setCapacidadEnCc(String capacidadEnCc) {
		this.capacidadEnCc = capacidadEnCc;
	}

	@Column(name = "cod_producto", nullable = true)
	public String getCodProducto() {
		return codProducto;
	}

	public void setCodProducto(String codProducto) {
		this.codProducto = codProducto;
	}

	@Column(name = "purchase_order_id", nullable = true)
	public String getPoId() {
		return poId;
	}

	public void setPoId(String poId) {
		this.poId = poId;
	}

	@Column(name = "id_pud", nullable = true)
	public String getPodId() {
		return podId;
	}

	public void setPodId(String podId) {
		this.podId = podId;
	}

	@Column(name = "fert", nullable = true)
	public String getFert() {
		return fert;
		
	}

	public void setFert(String fert) {
		this.fert = fert;
		
	}

	@Column(name = "fecha_recepcion", nullable = true)
	public Date getFechaRecepcion() {
		return fechaRecepcion;
	}

	public void setFechaRecepcion(Date fechaRecepcion) {
		this.fechaRecepcion = fechaRecepcion;
	}

	
	@Column(name = "owner_id", nullable = true)
	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	@Column(name = "nro_doc", nullable = true)
	public String getNroDoc() {
		return nroDoc;
		
	}

	public void setNroDoc(String nroDoc) {
		this.nroDoc = nroDoc;
		
	}

	@Column(name = "tipo_doc", nullable = true)
	public String getTipoDoc() {
		return tipoDoc;
		
	}

	public void setTipoDoc(String tipoDoc) {
		this.tipoDoc = tipoDoc;
		
	}

	@Column(name = "etiqueta_ant")
	public String getEtiquetaAnterior() {
		return etiquetaAnterior;
	}

	public void setEtiquetaAnterior(String etiquetaAnterior) {
		this.etiquetaAnterior = etiquetaAnterior;
	}

	@Transient
	public String getMotivoAnulacion() {
		return motivoAnulacion;
	}

	public void setMotivoAnulacion(String motivoAnulacion) {
		this.motivoAnulacion = motivoAnulacion;
	}

	@Transient
	public String getQr() {
		return qr;
	}

	public void setQr(String qr) {
		this.qr = qr;
	}

}
