package com.marval.wine.bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Specializes;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletContext;

import com.github.adminfaces.template.session.AdminSession;
import com.marval.wine.service.IndicadoresService;

import org.omnifaces.util.Faces;
import org.primefaces.PrimeFaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.web.context.annotation.SessionScope;

import marval.seg_usumarval.to.Permiso_segusu;
import marval.seg_usumarval.to.Rol_segusu;
import marval.seg_usumarval.to.Usuario_segusu;
import marval.seg_usumarval.utils.Login_segusu;

/**
 *
 * Clase encargada del login del sistema.
 * 
 * @author Marval.
 * @version 1.0.
 */
@Named
@Specializes
@SessionScope
@PropertySource("file:C:\\\\wildfly\\standalone\\data\\bd_wine.properties")
public class LoginBean extends AdminSession {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(LoginBean.class);
	private Usuario_segusu to = new Usuario_segusu();
	int validarCredenciales = 0;
	boolean todoOkPermisos = false;

	private Usuario_segusu currentUser = new Usuario_segusu();
	private String rolUser = "";
	private String usuario;
	private String password;
	private boolean remember;
	private boolean login;
	private boolean menuRecepcion;
	private boolean menuRecepcionManual;
	private boolean menuUbicacion;
	private boolean menuProgramacion;
	private boolean menuAsignacion;
	private boolean menuDesconsolidado;
	private boolean menuDespacho;
	private boolean menuFacturacion;
	private boolean menuBuscaEtiqueta;
	private boolean menuAjusteInventario;
	private boolean menuExistencia;
	private boolean menuRepaletizado;
	private boolean menuMensajes;
	private boolean menuPtDisponible;
	private boolean menuMensajeBodega;
	private boolean menuPackingList;
	
	
	
	private boolean presentarBienv;
	private String username;

	private List<String> empresas = new ArrayList<String>();
	private String empresa = "";
	private String empresaCompleta = "";


	@Autowired
	private Environment env;

	@Autowired
	IndicadoresService indicadoresService;

	@Value("${db.wine.url}")
	String URL_TARJA;

	/**
	 * Método constructor de la clase.
	 */
	@PostConstruct
	public void init() {
		empresas.add("SEAPORT");
	}

	public void onLoad() {

		if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) {
			return;
		}

		if (this.presentarBienv) {

			String submensaje = "Usuario: " + this.username + " - Perfil: " + this.rolUser + " - Empresa: "
					+ this.empresaCompleta + ".";
			PrimeFaces.current().executeScript(
					"Swal.fire(\'Bienvenido " + this.usuario + ". \', \'" + submensaje + "\', \'success\');");
		}
		this.presentarBienv = false;
		logger.info("[INFORMACION LOGIN MARVAL WINE]");
		logger.info("[USUARIO]   " +this.username);
		logger.info("[ROL]   " +this.rolUser);
		
	}

	/**
	 * Método encargado de realizar login de usuario.
	 * 
	 * @return String.
	 * @throws IOException.
	 */
	public void login() throws IOException {

		if (this.empresa == "") {
			PrimeFaces.current().executeScript(
					"Swal.fire(\"Acceso denegado! \", \"Debe seleccionar una Empresa. \", \"warning\");");
			return;
		}

		if (empresa.equals("SEAPORT")) {
			this.setEmpresaCompleta(empresa);
			setEmpresa(env.getProperty("wine.empresa.seaport"));
		}
		
		String rutaArchivoBdPorperties = "C:/wildfly/standalone/data/";

		try {
			//to = marval.seg_usumarval.utils.Login_segusu.autentica2(usuario, password, empresa, 8);
			
			to = Login_segusu.autentica2_conArchivoPropertiesYgetRolSoloSistemaConn(usuario,
					password, empresa, 12, rutaArchivoBdPorperties);
			
		} catch (Exception exe) {
			to.setSalidaSP(0);
			validarCredenciales = 0;
		}

		

		if (to.getSalidaSP() == 1) {
			currentUser = to;
			setLogin(true);
			verificarPermisos(currentUser);
			setUsuario(currentUser.getNombreSoloUnNombre() + " " + currentUser.getApellido_p());
			setUsername(currentUser.getUsuario());
			Faces.getExternalContext().getFlash().setKeepMessages(true);
			logger.info("[login] Accede usuario " + getUsername());
			ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			String path = ctx.getContextPath();
			FacesContext.getCurrentInstance().getExternalContext().redirect(path + "/inicio");
			this.presentarBienv = true;
		} else {
			setLogin(false);
			PrimeFaces.current()
					.executeScript("Swal.fire(\"Acceso denegado! \", \" Credenciales incorrectas. \", \"warning\");");
			return;
		}
	}

	public void valida() throws IOException {
		ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		String path = ctx.getContextPath();
		if (!login) {
			FacesContext.getCurrentInstance().getExternalContext().redirect(path);
		}
	}

	public void cierraSession() throws IOException {
		login = false;
		setMenuRecepcion(false);
		setMenuRecepcionManual(false);
		setMenuUbicacion(false);
		setMenuUbicacion(false);
		setMenuProgramacion(false);
		setMenuAsignacion(false);
		setMenuDesconsolidado(false);
		setMenuDespacho(false);
		setMenuFacturacion(false);
		setMenuBuscaEtiqueta(false);
		setMenuAjusteInventario(false);
		setMenuExistencia(false);
		setMenuRepaletizado(false);
		setMenuMensajes(false);
		setMenuPtDisponible(false);
		setMenuMensajeBodega(false);
		setMenuPackingList(false);

		setLogin(false);
		setEmpresa("");
		setUsuario("");
		currentUser = new Usuario_segusu();
		to = new Usuario_segusu();
		ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		String path = ctx.getContextPath();
		FacesContext.getCurrentInstance().getExternalContext().redirect(path);
	}
	
	
	public void verificarPermisos(Usuario_segusu currentUser) {
		// Recorrer roles de usuario:

		List<String> idPermiso = new ArrayList<String>();

		for (Rol_segusu r : currentUser.getRoles()) {

			for (Permiso_segusu permiso : r.getPermisos()) {
				idPermiso.add(permiso.getId());// NOMBRE DEL CODIGO DEL PERMISO
			}
		}

		if (idPermiso.contains("WINE_ADMIN") || idPermiso.contains("WINE_JEFEX")) {
			this.rolUser = "Administrador";
			setMenuRecepcion(true);
			setMenuRecepcionManual(true);
			setMenuUbicacion(true);
			setMenuUbicacion(true);
			setMenuProgramacion(true);
			setMenuAsignacion(true);
			setMenuDesconsolidado(true);
			setMenuDespacho(true);
			setMenuFacturacion(true);
			setMenuBuscaEtiqueta(true);
			setMenuAjusteInventario(true);
			setMenuExistencia(true);
			setMenuRepaletizado(true);
			setMenuMensajes(true);
			setMenuPtDisponible(true);
			setMenuMensajeBodega(true);
			setMenuPackingList(true);
		} else {
			if (idPermiso.contains("WINE_TARJA")) {
				this.rolUser = "Tarjador";
				setMenuDesconsolidado(true);
			} else {
				if (idPermiso.contains("WINE_ALMA")) {
					setMenuAjusteInventario(true);
					setMenuUbicacion(true);
					this.rolUser = "BODEGA";
				}
			}
		}

	}

	@Override
	public boolean isLoggedIn() {
		return true;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isRemember() {
		return remember;
	}

	public void setRemember(boolean remember) {
		this.remember = remember;
	}

	public Usuario_segusu getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(Usuario_segusu currentUser) {
		this.currentUser = currentUser;
	}

	public List<String> getEmpresas() {
		return empresas;
	}

	public void setEmpresas(List<String> empresas) {
		this.empresas = empresas;
	}

	public String getEmpresa() {
		return empresa;
	}

	public void setEmpresa(String empresa) {
		this.empresa = empresa;
	}

	public boolean isLogin() {
		return login;
	}

	public void setLogin(boolean login) {
		this.login = login;
	}

	public boolean isMenuProgramacion() {
		return menuProgramacion;
	}

	public void setMenuProgramacion(boolean menuProgramacion) {
		this.menuProgramacion = menuProgramacion;
	}

	public boolean isMenuDesconsolidado() {
		return menuDesconsolidado;
	}

	public void setMenuDesconsolidado(boolean menuDesconsolidado) {
		this.menuDesconsolidado = menuDesconsolidado;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String nombreUsuario() {
		return username;
	}

	public boolean isPresentarBienv() {
		return presentarBienv;
	}

	public void setPresentarBienv(boolean presentarBienv) {
		this.presentarBienv = presentarBienv;
	}

	public boolean isMenuAsignacion() {
		return menuAsignacion;
	}

	public void setMenuAsignacion(boolean menuAsignacion) {
		this.menuAsignacion = menuAsignacion;
	}



	public String getEmpresaCompleta() {
		return empresaCompleta;
	}

	public void setEmpresaCompleta(String empresaCompleta) {
		this.empresaCompleta = empresaCompleta;
	}

	public String getRolUser() {
		return rolUser;
	}

	public void setRolUser(String rolUser) {
		this.rolUser = rolUser;
	}

	public boolean isMenuRecepcion() {
		return menuRecepcion;
	}

	public void setMenuRecepcion(boolean menuRecepcion) {
		this.menuRecepcion = menuRecepcion;
	}

	public boolean isMenuDespacho() {
		return menuDespacho;
	}

	public void setMenuDespacho(boolean menuDespacho) {
		this.menuDespacho = menuDespacho;
	}

	public boolean isMenuRecepcionManual() {
		return menuRecepcionManual;
	}

	public void setMenuRecepcionManual(boolean menuRecepcionManual) {
		this.menuRecepcionManual = menuRecepcionManual;
	}

	public boolean isMenuUbicacion() {
		return menuUbicacion;
	}

	public void setMenuUbicacion(boolean menuUbicacion) {
		this.menuUbicacion = menuUbicacion;
	}

	public boolean isMenuFacturacion() {
		return menuFacturacion;
	}

	public void setMenuFacturacion(boolean menuFacturacion) {
		this.menuFacturacion = menuFacturacion;
	}

	public boolean isMenuBuscaEtiqueta() {
		return menuBuscaEtiqueta;
	}

	public void setMenuBuscaEtiqueta(boolean menuBuscaEtiqueta) {
		this.menuBuscaEtiqueta = menuBuscaEtiqueta;
	}

	public boolean isMenuAjusteInventario() {
		return menuAjusteInventario;
	}

	public void setMenuAjusteInventario(boolean menuAjusteInventario) {
		this.menuAjusteInventario = menuAjusteInventario;
	}

	public boolean isMenuExistencia() {
		return menuExistencia;
	}

	public void setMenuExistencia(boolean menuExistencia) {
		this.menuExistencia = menuExistencia;
	}

	public boolean isMenuRepaletizado() {
		return menuRepaletizado;
	}

	public void setMenuRepaletizado(boolean menuRepaletizado) {
		this.menuRepaletizado = menuRepaletizado;
	}

	public boolean isMenuMensajes() {
		return menuMensajes;
	}

	public void setMenuMensajes(boolean menuMensajes) {
		this.menuMensajes = menuMensajes;
	}

	public boolean isMenuPtDisponible() {
		return menuPtDisponible;
	}

	public void setMenuPtDisponible(boolean menuPtDisponible) {
		this.menuPtDisponible = menuPtDisponible;
	}

	public boolean isMenuMensajeBodega() {
		return menuMensajeBodega;
	}

	public void setMenuMensajeBodega(boolean menuMensajeBodega) {
		this.menuMensajeBodega = menuMensajeBodega;
	}

	public boolean isMenuPackingList() {
		return menuPackingList;
	}

	public void setMenuPackingList(boolean menuPackingList) {
		this.menuPackingList = menuPackingList;
	}
		
}