package com.marval.wine.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;


@Entity
@Table(name = "picking_ticket", schema = "dbo")
public class PickingTicket implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private Date fechaCr;
	private Date fechaUp;
	private String interfaceId; //Fijo ‘PICKTICKET’ 
	private String sendingSystem; // Fijo ‘JFHILLEBRAND’
	private String  interfaceCreationDate; // formato YYYYMMDD
	private String salesOrderId;
	private String salesOrderline;
	private String ordenReference;
	private String shipmentId;
	private String warehouseId;
	private String ownerId;
	private String productNumber;
	private Integer orderQuantity;
	private Integer shipmentQuantity;
	private String requestShipDate;
	private String supplierId;
	private String purchaseOrder;
	private String lotNumber;
	private String shipToCustomerId;
	private String shipToName;
	private String shipToAddress1;
	private String shipToAddress2;
	private String shipToAddress3;
	private String shipToAddress4;
	private String estado;
	private boolean seleccionado; 
	
	
	
	
	
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
	
	@Column(name = "sales_order_id", nullable = true)
	public String getSalesOrderId() {
		return salesOrderId;
	}
	public void setSalesOrderId(String salesOrderId) {
		this.salesOrderId = salesOrderId;
	}
	
	@Column(name = "sales_orderline", nullable = true)
	public String getSalesOrderline() {
		return salesOrderline;
	}
	public void setSalesOrderline(String salesOrderline) {
		this.salesOrderline = salesOrderline;
	}
	
	@Column(name = "orden_reference", nullable = true)
	public String getOrdenReference() {
		return ordenReference;
	}
	public void setOrdenReference(String ordenReference) {
		this.ordenReference = ordenReference;
	}
	
	@Column(name = "shipment_id", nullable = true)
	public String getShipmentId() {
		return shipmentId;
	}
	public void setShipmentId(String shipmentId) {
		this.shipmentId = shipmentId;
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
	
	@Column(name = "product_number", nullable = true)
	public String getProductNumber() {
		return productNumber;
	}
	public void setProductNumber(String productNumber) {
		this.productNumber = productNumber;
	}
	
	@Column(name = "order_quantity", nullable = true)
	public Integer getOrderQuantity() {
		return orderQuantity;
	}
	public void setOrderQuantity(Integer orderQuantity) {
		this.orderQuantity = orderQuantity;
	}
	
	@Column(name = "shipment_quantity", nullable = true)
	public Integer getShipmentQuantity() {
		return shipmentQuantity;
	}
	public void setShipmentQuantity(Integer shipmentQuantity) {
		this.shipmentQuantity = shipmentQuantity;
	}
	
	@Column(name = "request_ship_date", nullable = true)
	public String getRequestShipDate() {
		return requestShipDate;
	}
	public void setRequestShipDate(String requestShipDate) {
		this.requestShipDate = requestShipDate;
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
	
	@Column(name = "lot_number", nullable = true)
	public String getLotNumber() {
		return lotNumber;
	}
	public void setLotNumber(String lotNumber) {
		this.lotNumber = lotNumber;
	}
	
	@Column(name = "ship_to_customerid", nullable = true)
	public String getShipToCustomerId() {
		return shipToCustomerId;
	}
	public void setShipToCustomerId(String shipToCustomerId) {
		this.shipToCustomerId = shipToCustomerId;
	}
	
	@Column(name = "ship_to_name", nullable = true)
	public String getShipToName() {
		return shipToName;
	}
	public void setShipToName(String shipToName) {
		this.shipToName = shipToName;
	}
	
	@Column(name = "ship_to_address1", nullable = true)
	public String getShipToAddress1() {
		return shipToAddress1;
	}
	public void setShipToAddress1(String shipToAddress1) {
		this.shipToAddress1 = shipToAddress1;
	}
	
	@Column(name = "ship_to_address2", nullable = true)
	public String getShipToAddress2() {
		return shipToAddress2;
	}
	public void setShipToAddress2(String shipToAddress2) {
		this.shipToAddress2 = shipToAddress2;
	}
	
	@Column(name = "ship_to_address3", nullable = true)
	public String getShipToAddress3() {
		return shipToAddress3;
	}
	public void setShipToAddress3(String shipToAddress3) {
		this.shipToAddress3 = shipToAddress3;
	}
	
	@Column(name = "ship_to_address4", nullable = true)
	public String getShipToAddress4() {
		return shipToAddress4;
	}
	public void setShipToAddress4(String shipToAddress4) {
		this.shipToAddress4 = shipToAddress4;
	}
	@Column(name = "estado" , nullable = true)
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	@Column(name = "seleccionado", nullable = true)
	public boolean isSeleccionado() {
		return seleccionado;
	}
	public void setSeleccionado(boolean seleccionado) {
		this.seleccionado = seleccionado;
	}
	
	
	
}
