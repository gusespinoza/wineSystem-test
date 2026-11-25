package com.marval.wine.entity;


import static javax.persistence.GenerationType.IDENTITY;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;



@Entity
@Table(name="purchase_order_detalle", schema = "dbo")
public class PurchaseOrderDetalle implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	private Integer id;
	private PurchaseOrder po;
	private Date fechaCr;
	private Date fechaUp;
	private String tipoDoc;
	private Integer numeroDoc;
	private Date fechaDoc; 
	private Date fechaRecepcion;
	private Integer cantidad;
	private String descripcionProducto;
	private String codProducto;
	private String lote;
	private String estadoCarga;
	private String cosecha;
	private String totalParcial;
	private Integer pallet; 
	private Integer cajasPorPallet;
	private Boolean total;
	private String estado; //RECEPCIONADA-ALMACENADA-CONSOLIDADA- DESPACHADA
	private List<Etiqueta> etiquetas = new ArrayList<Etiqueta>(0);
	
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "po_id")
	public PurchaseOrder getPo() {
		return po;
	}
	public void setPo(PurchaseOrder po) {
		this.po = po;
	}
	
	@Column(name = "tipo_doc", nullable = true)
	public String getTipoDoc() {
		return tipoDoc;
	}
	public void setTipoDoc(String tipoDoc) {
		this.tipoDoc = tipoDoc;
	}
	
	@Column(name = "numero_doc", nullable = true)
	public Integer getNumeroDoc() {
		return numeroDoc;
	}
	public void setNumeroDoc(Integer numeroDoc) {
		this.numeroDoc = numeroDoc;
	}
	
	@Column(name = "fecha_doc", nullable = true)
	public Date getFechaDoc() {
		return fechaDoc;
	}
	public void setFechaDoc(Date fechaDoc) {
		this.fechaDoc = fechaDoc;
	}
	
	@Column(name = "fecha_recepcion", nullable = true)
	public Date getFechaRecepcion() {
		return fechaRecepcion;
	}
	public void setFechaRecepcion(Date fechaRecepcion) {
		this.fechaRecepcion = fechaRecepcion;
	}
	
	@Column(name = "cantidad", nullable = true)
	public Integer getCantidad() {
		return cantidad;
	}
	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}
	
	@Column(name = "descripcion_producto", nullable = true)
	public String getDescripcionProducto() {
		return descripcionProducto;
	}
	public void setDescripcionProducto(String descripcionProducto) {
		this.descripcionProducto = descripcionProducto;
	}
	
	@Column(name = "cod_producto", nullable = true)
	public String getCodProducto() {
		return codProducto;
	}
	public void setCodProducto(String codProducto) {
		this.codProducto = codProducto;
	}
	
	@Column(name = "lote", nullable = true)
	public String getLote() {
		return lote;
	}
	public void setLote(String lote) {
		this.lote = lote;
	}
	
	@Column(name = "estado_carga", nullable = true)
	public String getEstadoCarga() {
		return estadoCarga;
	}
	public void setEstadoCarga(String estadoCarga) {
		this.estadoCarga = estadoCarga;
	}
	
	@Column(name = "cosecha", nullable = true)
	public String getCosecha() {
		return cosecha;
	}
	public void setCosecha(String cosecha) {
		this.cosecha = cosecha;
	}
	
	@Column(name = "total_parcial", nullable = true)
	public String getTotalParcial() {
		return totalParcial;
	}
	public void setTotalParcial(String totalParcial) {
		this.totalParcial = totalParcial;
	}
	
	@Column(name = "pallet", nullable = true)
	public Integer getPallet() {
		return pallet;
	}
	public void setPallet(Integer pallet) {
		this.pallet = pallet;
	}
	
	@Column(name = "caja_por_pallet", nullable = true)
	public Integer getCajasPorPallet() {
		return cajasPorPallet;
	}
	public void setCajasPorPallet(Integer cajasPorPallet) {
		this.cajasPorPallet = cajasPorPallet;
	}
	
	@Column(name = "estado", nullable = true)
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	@Column(name = "fecha_cr", nullable = true)
	public Date getFechaCr() {
		return fechaCr;
	}
	public void setFechaCr(Date fechaCr) {
		this.fechaCr = fechaCr;
	}
	
	@Column(name = "fecha_up", nullable = true)
	public Date getFechaUp() {
		return fechaUp;
	}
	public void setFechaUp(Date fechaUp) {
		this.fechaUp = fechaUp;
	}
	
	@Column(name  = "total" ,nullable = true)
	public Boolean getTotal() {
		return total;
	}
	public void setTotal(Boolean total) {
		this.total = total;
	}
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "detallePo", cascade = CascadeType.ALL)
	public List<Etiqueta> getEtiquetas() {
		return etiquetas;
	}
	public void setEtiquetas(List<Etiqueta> etiquetas) {
		this.etiquetas = etiquetas;
	}
	
	
	
	
	
	 
}
