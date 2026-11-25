package com.marval.wine.entity;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "parametros_detalle", schema = "dbo")
public class ParametrosDetalle implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer ParametrosId;
	private String codigo;
	private String descrpcion;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "parametros_id", nullable = true)
	public Integer getParametrosId() {
		return ParametrosId;
	}
	public void setParametrosId(Integer parametrosId) {
		ParametrosId = parametrosId;
	}
	
	@Column(name = "codigo", nullable = true)
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	
	@Column(name = "descripcion", nullable = true)
	public String getDescrpcion() {
		return descrpcion;
	}
	public void setDescrpcion(String descrpcion) {
		this.descrpcion = descrpcion;
	} 
	

	
}
