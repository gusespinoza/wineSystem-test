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
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "purchase_order", schema = "dbo")
public class PurchaseOrder implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private Date fechaCr;
	private Date fechaUp;
	private String interfaceId; //Fijo "PURCHASEORDER" 
	private String sendingSystem; // Fijo ‘JFHILLEBRAND’
	private String  interfaceCreationDate; // formato YYYYMMDD
	private String purchaseOrderId;
	private String orderReference;
	private String warehouseId;
	private String ownerId;
	private String supplierId;
	private String purchaseOrder;
	private Integer purchaseOrderQuantity;
	private Integer recivedQuantity;
	private String expectedArrivalDate;
	private String productNumber;
	private String productDescription;
	private String standardStorageUnit;
	private Integer bottlesPerUnit;
	private Integer mlPerBottle;
	private Double alcoholPercentage;
	private String productCategory;
	private Double weight;
	private Double volume;
	private String vintage;
	private Boolean slipsheet;
	private String estado;
	private String codFert;
	private String tipoRecepcion;
	private String lote;
	private List<PurchaseOrderDetalle> detalle =  new ArrayList<PurchaseOrderDetalle>(0);
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	
	@Column(name = "interface_id", nullable = true)
	public String getInterfaceId() {
		return interfaceId;
	}
	public void setInterfaceId(String interfaceId) {
		this.interfaceId = interfaceId;
	}
	
	@Column(name = "sending_system", nullable = true)
	public String getSendingSystem() {
		return sendingSystem;
	}
	public void setSendingSystem(String sendingSystem) {
		this.sendingSystem = sendingSystem;
	}
	
	@Column(name = "interface_creation_date", nullable = true)
	public String getInterfaceCreationDate() {
		return interfaceCreationDate;
	}
	public void setInterfaceCreationDate(String interfaceCreationDate) {
		this.interfaceCreationDate = interfaceCreationDate;
	}
	
	@Column(name = "purchase_order_id", nullable = true)
	public String getPurchaseOrderId() {
		return purchaseOrderId;
	}
	public void setPurchaseOrderId(String purchaseOrderId) {
		this.purchaseOrderId = purchaseOrderId;
	}
	
	@Column(name = "order_reference", nullable = true)
	public String getOrderReference() {
		return orderReference;
	}
	public void setOrderReference(String orderReference) {
		this.orderReference = orderReference;
	}
	
	@Column(name = "warehouse_id", nullable = true)
	public String getWarehouseId() {
		return warehouseId;
	}
	public void setWarehouseId(String warehouseId) {
		this.warehouseId = warehouseId;
	}
	
	@Column(name = "owner_id", nullable = true)
	public String getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	
	@Column(name = "supplier_id", nullable = true)
	public String getSupplierId() {
		return supplierId;
	}
	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}
	
	@Column(name = "purchase_order", nullable = true)
	public String getPurchaseOrder() {
		return purchaseOrder;
	}
	public void setPurchaseOrder(String purchaseOrder) {
		this.purchaseOrder = purchaseOrder;
	}
	
	@Column(name = "purchase_order_quantity", nullable = true)
	public Integer getPurchaseOrderQuantity() {
		return purchaseOrderQuantity;
	}
	public void setPurchaseOrderQuantity(Integer purchaseOrderQuantity) {
		this.purchaseOrderQuantity = purchaseOrderQuantity;
	}
	
	@Column(name = "recived_quantity", nullable = true)
	public Integer getRecivedQuantity() {
		return recivedQuantity;
	}
	public void setRecivedQuantity(Integer recivedQuantity) {
		this.recivedQuantity = recivedQuantity;
	}
	
	@Column(name = "expected_arrival_date", nullable = true)
	public String getExpectedArrivalDate() {
		return expectedArrivalDate;
	}
	public void setExpectedArrivalDate(String expectedArrivalDate) {
		this.expectedArrivalDate = expectedArrivalDate;
	}
	
	@Column(name = "product_number", nullable = true)
	public String getProductNumber() {
		return productNumber;
	}
	public void setProductNumber(String productNumber) {
		this.productNumber = productNumber;
	}
	
	@Column(name = "product_description", nullable = true)
	public String getProductDescription() {
		return productDescription;
	}
	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}
	
	@Column(name = "standard_storage_unit", nullable = true)
	public String getStandardStorageUnit() {
		return standardStorageUnit;
	}
	public void setStandardStorageUnit(String standardStorageUnit) {
		this.standardStorageUnit = standardStorageUnit;
	}
	
	@Column(name = "bottles_per_unit", nullable = true)
	public Integer getBottlesPerUnit() {
		return bottlesPerUnit;
	}
	public void setBottlesPerUnit(Integer bottlesPerUnit) {
		this.bottlesPerUnit = bottlesPerUnit;
	}
	
	@Column(name = "ml_per_bottle", nullable = true)
	public Integer getMlPerBottle() {
		return mlPerBottle;
	}
	public void setMlPerBottle(Integer mlPerBottle) {
		this.mlPerBottle = mlPerBottle;
	}
	
	@Column(name = "alcohol_percentage", nullable = true)
	public Double getAlcoholPercentage() {
		return alcoholPercentage;
	}
	public void setAlcoholPercentage(Double alcoholPercentage) {
		this.alcoholPercentage = alcoholPercentage;
	}
	
	@Column(name = "product_category", nullable = true)
	public String getProductCategory() {
		return productCategory;
	}
	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}
	
	@Column(name = "weight", nullable = true)
	public Double getWeight() {
		return weight;
	}
	public void setWeight(Double weight) {
		this.weight = weight;
	}
	
	@Column(name = "volume", nullable = true)
	public Double getVolume() {
		return volume;
	}
	public void setVolume(Double volume) {
		this.volume = volume;
	}
	
	@Column(name = "vintage", nullable = true)
	public String getVintage() {
		return vintage;
	}
	public void setVintage(String vintage) {
		this.vintage = vintage;
	}
	
	@Column(name = "slipsheet", nullable = true)
	public Boolean getSlipsheet() {
		return slipsheet;
	}
	public void setSlipsheet(Boolean slipsheet) {
		this.slipsheet = slipsheet;
	}
	
	@Column(name = "estado", nullable = true)
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	@Column(name = "codigo_fert", nullable = true)
	public String getCodFert() {
		return codFert;
	}
	public void setCodFert(String codFert) {
		this.codFert = codFert;
	}
	
	@Column(name = "tipo_recep", nullable = true)
	public String getTipoRecepcion() {
		return tipoRecepcion;
	}
	public void setTipoRecepcion(String tipoRecepcion) {
		this.tipoRecepcion = tipoRecepcion;
	}
	
	
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "po", cascade = CascadeType.ALL)
	public List<PurchaseOrderDetalle> getDetalle() {
		return detalle;
	}
	public String getLote() {
		return lote;
	}
	
	@Column(name = "lote", nullable = true)
	public void setLote(String lote) {
		this.lote = lote;
	}
	public void setDetalle(List<PurchaseOrderDetalle> detalle) {
		this.detalle = detalle;
	}
	
	
	
	
}