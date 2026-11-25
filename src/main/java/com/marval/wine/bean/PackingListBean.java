package com.marval.wine.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.primefaces.PrimeFaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.marval.wine.entity.Container;
import com.marval.wine.entity.PackingList;
import com.marval.wine.entity.PickingTicket;
import com.marval.wine.service.ContainerService;

@ManagedBean
@SessionScope
@Component
public class PackingListBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(PackingListBean.class);

	@Autowired
	private LoginBean loginBean;

	@Autowired
	ContainerService contServ;

	private List<PackingList> listaPl = new ArrayList<PackingList>();
	private List<Container> listaContainer = new ArrayList<Container>();

	private Container contenedor = new Container();

	private String nroCont = "";
	private Integer totalCAntidad =  0;

	public void onLoad() throws IOException {
		if (loginBean.isMenuDespacho()) {
			if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) {
				return;
			}
			logger.info("[PackingListBean - onLoad] inicio");
			this.nroCont = "";
			this.listaContainer.clear();
			this.listaPl.clear();
			this.contenedor = new Container();
		} else {
			ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			String path = ctx.getContextPath();
			FacesContext.getCurrentInstance().getExternalContext().redirect(path + "/index.xhtml");
		}

	}

	public List<String> autoCompletarContenedor() {
		return contServ.autoCompletaContPacking(this.nroCont);
	}

	public void buscarContenedores() {
		this.listaContainer.clear();
		this.listaContainer.addAll(contServ.listarContenedoresPacking(this.nroCont));
	}

	public void cargarPackingList(Container cont) {
		try {
			this.contenedor =  cont;
			this.totalCAntidad =  0;
			if (this.contenedor != null) {
				this.listaPl = new ArrayList<PackingList>();
				this.listaPl.addAll(contServ.listarPacking(this.contenedor.getId()));
				
				for(PackingList pt : listaPl) {
					this.totalCAntidad =  this.totalCAntidad + pt.getCantidad();
				}
				
				PrimeFaces.current().ajax().update("formPt");
			}

		} catch (Exception e) {
			PrimeFaces.current().executeScript(
					"Swal.fire(\"Problemas al buscar PAcking List\", \"Existieron problemas al consultar el Packing List, "
							+ "favor vuelva a intentar.\", \"error\");");
			logger.info("[imprimir - error] error " + e);
		}

	}

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

	public List<PackingList> getListaPl() {
		return listaPl;
	}

	public void setListaPl(List<PackingList> listaPl) {
		this.listaPl = listaPl;
	}

	public List<Container> getListaContainer() {
		return listaContainer;
	}

	public void setListaContainer(List<Container> listaContainer) {
		this.listaContainer = listaContainer;
	}

	public Container getContenedor() {
		return contenedor;
	}

	public void setContenedor(Container contenedor) {
		this.contenedor = contenedor;
	}

	public String getNroCont() {
		return nroCont;
	}

	public void setNroCont(String nroCont) {
		this.nroCont = nroCont;
	}

	public Integer getTotalCAntidad() {
		return totalCAntidad;
	}

	public void setTotalCAntidad(Integer totalCAntidad) {
		this.totalCAntidad = totalCAntidad;
	}
	
	

}
