package com.marval.wine.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name = "container", schema = "dbo")
public class Container implements java.io.Serializable {

private static final long serialVersionUID = 1L;
private Integer id;
private String container;
private String ship;
private String voyage;
private Date fechaConsolidado;
private Date fechaEmbarque;
private Integer palletCont;
private Integer tara;
private String sello;
private Date iniConsolidado;
private Date finConsolidado;
private String puerto;
private String estado; 
private Date fechaCr;
private boolean consolidado;
private String tipoDocu;
private String numeroDoc;
private Date fechaDoc;
private String importador;
private String deposito;
private Integer vgm;
private String tipoCont;
private String fille; 
private String booking;
private String embarque;
private Integer airbag;

private Set<Asignacion> asignaciones = new HashSet<Asignacion>(0);

@Id
@GeneratedValue(strategy = IDENTITY)
@Column(name = "id", unique = true, nullable = false)
public Integer getId() {
	return id;
}
public void setId(Integer id) {
	this.id = id;
}

@Column(name = "container", nullable = true)
public String getContainer() {
	return container;
}
public void setContainer(String container) {
	this.container = container;
}

@Column(name = "ship", nullable = true)
public String getShip() {
	return ship;
}
public void setShip(String ship) {
	this.ship = ship;
}

@Column(name = "voyage", nullable = true)
public String getVoyage() {
	return voyage;
}
public void setVoyage(String voyage) {
	this.voyage = voyage;
}

@Column(name = "fecha_consolidado", nullable = true)
public Date getFechaConsolidado() {
	return fechaConsolidado;
}
public void setFechaConsolidado(Date fechaConsolidado) {
	this.fechaConsolidado = fechaConsolidado;
}

@Column(name = "fecha_embarque", nullable = true)
public Date getFechaEmbarque() {
	return fechaEmbarque;
}
public void setFechaEmbarque(Date fechaEmbarque) {
	this.fechaEmbarque = fechaEmbarque;
}

@Column(name = "tara", nullable = true)
public Integer getTara() {
	return tara;
}
public void setTara(Integer tara) {
	this.tara = tara;
}

@Column(name = "sello", nullable = true)
public String getSello() {
	return sello;
}
public void setSello(String sello) {
	this.sello = sello;
}

@Column(name = "ini_consolidado", nullable = true)
public Date getIniConsolidado() {
	return iniConsolidado;
}
public void setIniConsolidado(Date iniConsolidado) {
	this.iniConsolidado = iniConsolidado;
}

@Column(name = "fin_consolidado", nullable = true)
public Date getFinConsolidado() {
	return finConsolidado;
}
public void setFinConsolidado(Date finConsolidado) {
	this.finConsolidado = finConsolidado;
}

@Column(name = "puerto", nullable = true)
public String getPuerto() {
	return puerto;
}
public void setPuerto(String puerto) {
	this.puerto = puerto;
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

@Column(name = "pallet_contenedor", nullable =  true)
public Integer getPalletCont() {
	return palletCont;
}
public void setPalletCont(Integer palletCont) {
	this.palletCont = palletCont;
}

@Column(name = "consolidado", nullable = true)
public boolean isConsolidado() {
	return consolidado;
}
public void setConsolidado(boolean consolidado) {
	this.consolidado = consolidado;
}

@Column(name = "tipo_doc", nullable = true)
public String getTipoDocu() {
	return tipoDocu;
}
public void setTipoDocu(String tipoDocu) {
	this.tipoDocu = tipoDocu;
}

@Column(name = "numero_doc", nullable = true)
public String getNumeroDoc() {
	return numeroDoc;
}
public void setNumeroDoc(String numeroDoc) {
	this.numeroDoc = numeroDoc;
}

@Column(name = "fecha_doc", nullable = true)
public Date getFechaDoc() {
	return fechaDoc;
}
public void setFechaDoc(Date fechaDoc) {
	this.fechaDoc = fechaDoc;
}

@Column(name = "importador", nullable = true)
public String getImportador() {
	return importador;
}
public void setImportador(String importador) {
	this.importador = importador;
}

@Column(name= "deposito", nullable = true)
public String getDeposito() {
	return deposito;
}
public void setDeposito(String deposito) {
	this.deposito = deposito;
}

@Column(name = "vgm", nullable = true)
public Integer getVgm() {
	return vgm;
}
public void setVgm(Integer vgm) {
	this.vgm = vgm;
}

@Column(name = "tipo_cont", nullable = true)
public String getTipoCont() {
	return tipoCont;
}
public void setTipoCont(String tipoCont) {
	this.tipoCont = tipoCont;
}

@Column(name = "fille", nullable = true)
public String getFille() {
	return fille;
}
public void setFille(String fille) {
	this.fille = fille;
}

@Column(name = "booking", nullable = true)
public String getBooking() {
	return booking;
}
public void setBooking(String booking) {
	this.booking = booking;
}

@Column(name = "embarque", nullable = true)
public String getEmbarque() {
	return embarque;
}
public void setEmbarque(String embarque) {
	this.embarque = embarque;
}

@Column(name = "airbag", nullable = true)
public Integer getAirbag() {
	return airbag;
}
public void setAirbag(Integer airbag) {
	this.airbag = airbag;
}
@OneToMany(fetch = FetchType.EAGER, mappedBy = "contenedor", cascade = CascadeType.ALL)
public Set<Asignacion> getAsignaciones() {
	return asignaciones;
}

public void setAsignaciones(Set<Asignacion> asignaciones) {
	this.asignaciones = asignaciones;
}
}
