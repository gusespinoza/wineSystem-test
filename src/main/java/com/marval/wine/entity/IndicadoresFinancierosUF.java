package com.marval.wine.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

@JsonIgnoreProperties({ "SchemaVersion", "schemaLocation",
					"Dolares", "UTMs", "TABs", 
					"IPCs", "Euros", "TMCs","TIPs", "Perfiles"})
public class IndicadoresFinancierosUF {

	@JacksonXmlElementWrapper(localName="UFs")
	private List<UF> ufs = new ArrayList<UF>();

	public List<UF> getUfs() {
		return ufs;
	}

	public void setUfs(List<UF> ufs) {
		this.ufs = ufs;
	}

	
	
	
}
