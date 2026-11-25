package com.marval.wine.bean;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@ManagedBean
@SessionScope
@Component
public class TipoRecepcionBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer tipoRecepcion = 0;
	
	@Autowired
	private LoginBean loginBean;
	
	/**
	 * @throws IOException 
	 * 
	 */
	public void onLoad() throws IOException  {
		if(loginBean.isMenuRecepcion()) {
			if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) {
				return;
			}
			
			this.tipoRecepcion = 0;
		}else {
			ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			String path = ctx.getContextPath();
			FacesContext.getCurrentInstance().getExternalContext().redirect(path + "/index.xhtml");
		}
	}
	
	public Integer getTipoRecepcion() {
		return tipoRecepcion;
	}


	public void setTipoRecepcion(Integer tipoRecepcion) {
		this.tipoRecepcion = tipoRecepcion;
	}


	public void cambairValor() {
		FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("AutoUpdatePanelRecepcion");
	}
	
}