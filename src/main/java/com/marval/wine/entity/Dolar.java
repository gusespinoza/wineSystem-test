package com.marval.wine.entity;

import java.util.Date;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Dolar {

	@JacksonXmlProperty(localName="Fecha")
	private Date fecha;
	
	@JacksonXmlProperty(localName="Valor")
	private String valor;
	
	
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	
	
}
