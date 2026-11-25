package com.marval.wine.entity;

import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.SqlResultSetMapping;

@Entity
@SqlResultSetMapping(name = "ConsultarEtiquetaResult", entities = { @EntityResult(

		entityClass = ConsultarEtiqueta.class, fields = { 
				@FieldResult(name = "id_po", column = "id")
				}) })

public class OrdenFacturada {

	
	private Integer id_po;

	public Integer getId_po() {
		return id_po;
	}

	public void setId_po(Integer id_po) {
		this.id_po = id_po;
	}
	
}
