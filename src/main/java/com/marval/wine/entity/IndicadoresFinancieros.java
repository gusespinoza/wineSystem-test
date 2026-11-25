package com.marval.wine.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

@JsonIgnoreProperties({ "SchemaVersion", "schemaLocation",
					"UFs", "UTMs", "TABs", 
					"IPCs", "Euros", "TMCs","TIPs", "Perfiles"})
public class IndicadoresFinancieros {

	@JacksonXmlElementWrapper(localName="Dolares")
	private List<Dolar> dolares = new ArrayList<Dolar>();

	public List<Dolar> getDolares() {
		return dolares;
	}

	public void setDolares(List<Dolar> dolares) {
		this.dolares = dolares;
	}
	
	
}
