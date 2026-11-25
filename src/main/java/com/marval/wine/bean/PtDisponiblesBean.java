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

import com.marval.wine.entity.PickingTicket;
import com.marval.wine.entity.PtDisponible;
import com.marval.wine.service.PickingTicketService;

@ManagedBean
@SessionScope
@Component
public class PtDisponiblesBean implements Serializable {
	
	@Autowired
	private LoginBean loginBean;

	@Autowired
	private PickingTicketService ptService;
	
	
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(PtDisponiblesBean.class);
	
	private List<PtDisponible> ptDisponibles =  new ArrayList<PtDisponible>(); 
	private List<PtDisponible> ptDisponiblesAux =  new ArrayList<PtDisponible>();
	private PtDisponible ptAux = new PtDisponible();
	;
	public void onLoad() throws IOException {
		if (loginBean.isMenuExistencia()) {
			if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) {
				return;
			}
			logger.info("[ptDisponiblesBean - onLoad] inicio");
		this.ptDisponibles.clear();
		this.ptDisponiblesAux.clear();
		this.ptDisponibles.addAll(ptService.ptDisponibles());
		this.ptDisponiblesAux.addAll(this.ptDisponibles);
		}else {
			ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			String path = ctx.getContextPath();
			FacesContext.getCurrentInstance().getExternalContext().redirect(path + "/index.xhtml");
		}
	}
	
	public void cargarPt(PtDisponible pt) {
		this.ptAux = pt;
		String dialog = "Swal.fire({";
		dialog += "title: \"Confirmación\",";
		dialog += " text: \"Se anulará el Pt: " + this.ptAux.getOrRef() + " con "+ this.ptAux.getCantidad() +" cajas. Producto; " + this.ptAux.getCodProd() + ". Cosecha;" + this.ptAux.getCosecha()
				+ " . ¿Desea continuar? \",";
		dialog += " type: 'warning',";
		dialog += " showCancelButton: true,";
		dialog += " confirmButtonColor: '#3085d6',";
		dialog += " cancelButtonColor: '#d33',";
		dialog += " confirmButtonText: 'Confirmar',";
		dialog += " cancelButtonText: 'Cancelar'";
		dialog += " }).then((result) => {";
		dialog += " if (result.value) {";
		dialog += "  mod();";
		dialog += " } else { habilitarScroll(); }})";

		PrimeFaces.current().executeScript(dialog);
	}
	
	public void cancelarPt() {
		ptService.anularPt(this.ptAux.getId());
		this.ptDisponibles.clear();
		this.ptDisponiblesAux.clear();
		this.ptDisponibles.addAll(ptService.ptDisponibles());
		this.ptDisponiblesAux.addAll(this.ptDisponibles);
		PrimeFaces.current().ajax().update("ptForm:panelPt");
		PrimeFaces.current().executeScript("Swal.fire('Anulación exitosa', 'El Picking Ticket se ha anulado.',  'info');");
	}
	
	public List<PtDisponible> getPtDisponibles() {
		return ptDisponibles;
	}
	public void setPtDisponibles(List<PtDisponible> ptDisponibles) {
		this.ptDisponibles = ptDisponibles;
	}


	public List<PtDisponible> getPtDisponiblesAux() {
		return ptDisponiblesAux;
	}


	public void setPtDisponiblesAux(List<PtDisponible> ptDisponiblesAux) {
		this.ptDisponiblesAux = ptDisponiblesAux;
	}

	public PtDisponible getPtAux() {
		return ptAux;
	}

	public void setPtAux(PtDisponible ptAux) {
		this.ptAux = ptAux;
	}
	
	

}
