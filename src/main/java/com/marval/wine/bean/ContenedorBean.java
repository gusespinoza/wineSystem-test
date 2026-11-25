package com.marval.wine.bean;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.marval.wine.entity.Container;
import com.marval.wine.service.ContainerService;
import com.marval.wine.util.Constantes;
import com.marval.wine.util.Util;

@ManagedBean
@SessionScope
@Component
@PropertySource(Constantes.RUTA_PROPERTIES)
public class ContenedorBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(ContenedorBean.class);

	@Value("${db.ftp.excel.contenedor}")
	String URL_EXCEL_CONT;

	@Autowired
	public ContainerService contService;

	@Autowired
	private LoginBean loginBean;

	private boolean validadorSubida;
	private String estado;
	private String nave;
	private String nroCont;
	private Integer nroGuia;
	private String numeroContenedor;
	private String mensajeError;
	private List<String> listaSellos = new ArrayList<String>();

	private Container contenedor = new Container();
	private StreamedContent plantillaExcel;
	private List<Container> contenedores = new ArrayList<Container>();
	private List<Container> listaContenedor = new ArrayList<Container>();

	private HashMap<String, String> erroresTipeo = new HashMap<String, String>();
	private HashMap<String, String> erroresObligatorios = new HashMap<String, String>();

	/**
	 * Llama al metodo {@link #listaContenedor}
	 * @throws IOException 
	 */
	public void onLoad() throws IOException {
		
		
		if (loginBean.isMenuProgramacion()) {
			if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) {
				return;
			}
			logger.info("[ContenedorBean - onLoad] inicio");
			this.listaContenedor.clear();
			contenedores = new ArrayList<Container>();
			listarContenedores();

		} else {
			ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			String path = ctx.getContextPath();
			FacesContext.getCurrentInstance().getExternalContext().redirect(path + "/index.xhtml");
		}
		
		
		
	

		
	}

	/**
	 * Método encargado de descargar la plantilla base en el servidor ftp
	 */
	public void descargarPlanillaContenedor() {

		logger.info("[ContenedorBean - descargarPlanillaContenedor] inicio ");

		InputStream outR = null;

		try {

			URL url = new URL(URL_EXCEL_CONT);
			URLConnection conn = url.openConnection();

			outR = new ByteArrayInputStream(IOUtils.toByteArray(conn.getInputStream()));

		} catch (IOException e) {
			logger.error("[ContenedorBean - descargarPlanillaContenedor] error " + e);
			FacesContext.getCurrentInstance().addMessage("mensajes", new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Problemas al obtener plantilla de guías, intente más tarde.", ""));
			PrimeFaces.current().ajax().update("mensajes");
			return;

		}

		plantillaExcel = new DefaultStreamedContent(outR, "text/xls",
				"solicitud_contenedor_" + new SimpleDateFormat("dd-MM-yyyy").format(new Date()) + ".xlsx");

		logger.info("[ContenedorBean - descargarPlanillaContenedor] fin, actualiza dlg");

		PrimeFaces.current().executeScript("$('#formTablaXls2\\\\:descExcelCont').click()");

	}

	/**
	 * Metodo que llama a la {@link #leerContenedor(FileUploadEvent)} y valida si la
	 * lectura fue correcta.
	 * 
	 * @param FileUploadEvent
	 * @throws IOException, ParseException
	 */
	public void leerExcel(FileUploadEvent event) throws IOException, ParseException {

		logger.info("[ContenedorBean - leerExcel] inicio");

		try {
			this.validadorSubida = false;
			this.leerContenedor(event);

			logger.info("[ContenedorBean - leerExcel] this.validadorSubida: " + this.validadorSubida);

			if (this.validadorSubida) {
				PrimeFaces.current()
						.executeScript("Swal.fire('Programacion Contenedores cargada correctamente','','success');");
				logger.info("[ContenedorBean - leerExcel] Archivo '" + event.getFile().getFileName()
						+ "' cargado con exito ");

				this.listarContenedores();
			} else {
				String men = "Swal.fire({title: 'Error de lectura',html:'" + this.mensajeError + "', type:'warning'});";
				PrimeFaces.current().executeScript(men);
				return;
			}
		} catch (Exception e) {
			PrimeFaces.current()
					.executeScript("Swal.fire('Error al cargar la programacion de contenedores','','error');");
			logger.error("[ContenedorBean - leerExcel] Error al cargar el archivo '" + event.getFile().getFileName());
		}
	}

	/**
	 * Metodo encargado de leer el Excel
	 * 
	 * @param FileUploadEvent
	 * @throws IOException
	 */
	public void leerContenedor(FileUploadEvent event) throws IOException {

		logger.info("[ContenedorBean - leerContenedor] inicio");
		this.validadorSubida = false;
		// Lista que guardara los errores encontrados en el excel
		this.erroresTipeo = new HashMap<String, String>();
		this.erroresObligatorios = new HashMap<String, String>();

		String nombreArchivo = event.getFile().getFileName().toUpperCase();
		InputStream inputStream = event.getFile().getInputstream();

		logger.info("[ContenedorBean - leerContenedor] nombreArchivo: " + nombreArchivo);
		logger.info("[ContenedorBean - [ContenedorBean - leerContenedor] inputStream: " + inputStream);

		// Objetos que contienen la informacion del excel
		Workbook workbook;
		Sheet firstSheet;

		// Lista que guarda los objetos obtenidos de la lectura del excel
		List<Container> listaContenedores = new ArrayList<Container>();

		// Lista de los sellos internos del terminal
		List<String> listaSellosInternos = new ArrayList<String>();

		// Valor que varia segun en que columna se encuentre la lectura
		String nombreColumna;

		// Diferencia el tipo de formato del excel.
		if (nombreArchivo.contains(".XLSX")) {
			workbook = new XSSFWorkbook(inputStream);
			firstSheet = workbook.getSheetAt(0);
		} else {
			workbook = new HSSFWorkbook(inputStream);
			firstSheet = workbook.getSheetAt(0);
		}

		// Numero que guarda la cantidad de filas
		Integer filas = firstSheet.getLastRowNum() + 1;

		int columnas = 0;
		int numFila = 0;

		Row filax = null;
		Container contenedor = null;
		Cell celda = null;
		String par;

		// Valor en estring del contenido de la celda
		// Este se almacena tal cual en el caso que el dato sea un String
		String valorCelda;
		Integer valorCeldaInt;

		// Ciclo que recorre las filas del excel
		for (int fila = 0; fila < filas; fila++) {

			// Objeto que guarda la informacion de la fila
			filax = firstSheet.getRow(fila);

			// Numero que almacena la cantidad de columnas
			if (fila == 0) {
				columnas = filax.getLastCellNum();
			}

			// Objeto que almacena la informacion de una fila en aprticular
			contenedor = new Container();
			// Ciclo que recorre las columas del excel
			for (int columna = 0; columna < columnas; columna++) {
				celda = filax.getCell(columna);

				// Valor de la fila que se esta leyendo
				numFila = filax.getRowNum() + 1;

				// Se revisa cada columna una por una.
				switch (columna) {

				case 0:

					nombreColumna = "nave";
					valorCelda = validarCelda(celda);
					if (valorCelda.equals(nombreColumna) || valorCelda.equals("nulo")) {
						break;
					} else {
						try {
							contenedor.setShip(valorCelda.toUpperCase().trim());
							break;
						} catch (Exception e) {
							logger.error("[ContenedorBean - leerContenedor] Error de lectura en la columna "
									+ nombreColumna + ", fila " + numFila);
							erroresExcelContendor(nombreColumna, numFila);

							break;
						}
					}
				case 1:
					nombreColumna = "viaje";
					valorCelda = validarCelda(celda);
					if (valorCelda.equals(nombreColumna) || valorCelda.equals("nulo")) {
						break;
					} else {
						try {
							contenedor.setVoyage(valorCelda.toUpperCase().trim());
							break;
						} catch (Exception e) {
							logger.error("[ContenedorBean - leerContenedor] Error de lectura en la columna "
									+ nombreColumna + ", fila " + numFila);
							erroresExcelContendor(nombreColumna, numFila);

							break;
						}
					}

				case 2:
					nombreColumna = "puerto_destino";
					valorCelda = validarCelda(celda);
					if (valorCelda.equals(nombreColumna) || valorCelda.equals("nulo")) {
						break;
					} else {
						try {
							contenedor.setPuerto(valorCelda);
							break;
						} catch (Exception e) {
							logger.error("[ContenedorBean - leerContenedor] Error de lectura en la columna "
									+ nombreColumna + ", fila " + numFila);
							erroresExcelContendor(nombreColumna, numFila);

							break;
						}
					}
				case 3:
					nombreColumna = "pallet_x_cont";
					valorCelda = validarCelda(celda);

					if (valorCelda.equals(nombreColumna) || valorCelda.equals("nulo")) {
						break;
					} else {
						try {

							valorCeldaInt = (Integer) getCellValue(celda);

							contenedor.setPalletCont(valorCeldaInt);
							break;
						} catch (Exception e) {
							logger.error("[ContenedorBean - leerContenedor] Error de lectura en la columna "
									+ nombreColumna + ", fila " + numFila);
							erroresExcelContendor(nombreColumna, numFila);

							break;
						}
					}
				case 4:

					nombreColumna = "nro_cntr";
					valorCelda = validarCelda(celda).replaceAll(".0", "");
					if (valorCelda.equals(nombreColumna) || valorCelda.equals("nulo")) {
						break;
					} else {

						String errorFormato = "Fallo en el formato (AAAA000000-0)";
						numeroContenedor = celda.getStringCellValue();
						try {
							if (numeroContenedor.length() == 11 && !numeroContenedor.contains("-")) {
								numeroContenedor = insertarGuion(numeroContenedor);
							}
							
							if (this.contService.validarExistenciaContenedor(numeroContenedor)) {
								logger.error("[ContenedorBean - leerContenedor] Error de lectura en la columna "
										+ nombreColumna + ", fila " + numFila + ". El contendor ya se encuentra habilitado en el sistema.");
								erroresExcelContendor(nombreColumna + " (El contendor ya se encuentra habilitado en el sistema.)", numFila);
								break;
							}

							if (validarNroContenedor(numeroContenedor)) {
								contenedor.setContainer(numeroContenedor.toUpperCase());
								errorFormato = "";
								break;
							} else {
								logger.error("[ContenedorBean - leerContenedor] Error de lectura en la columna "
										+ nombreColumna + ", fila " + numFila + ". " + errorFormato.toUpperCase());
								erroresExcelContendor(nombreColumna + " (" + errorFormato + ")", numFila);
								break;
							}
							
							
						} catch (Exception e) {
							logger.error("[ContenedorBean - leerContenedor] Error de lectura en la columna "
									+ nombreColumna + ", fila " + numFila + ". " + errorFormato.toUpperCase());
							erroresExcelContendor(nombreColumna + " (" + errorFormato + ")", numFila);
							break;
						}

					}

				case 5:
					nombreColumna = "tara";
					valorCelda = validarCelda(celda);

					if (valorCelda.equals(nombreColumna) || valorCelda.equals("nulo")) {
						break;
					} else {
						try {

							valorCeldaInt = (Integer) getCellValue(celda);

							contenedor.setTara(valorCeldaInt);
							break;
						} catch (Exception e) {
							logger.error("[ContenedorBean - leerContenedor] Error de lectura en la columna "
									+ nombreColumna + ", fila " + numFila);
							erroresExcelContendor(nombreColumna, numFila);

							break;
						}
					}

				case 6:
					nombreColumna = "importador";
					valorCelda = validarCelda(celda);

					if (valorCelda.equals(nombreColumna) || valorCelda.equals("nulo")) {
						break;
					} else {
						try {

							contenedor.setImportador(valorCelda);
							break;
						} catch (Exception e) {
							logger.error("[ContenedorBean - leerContenedor] Error de lectura en la columna "
									+ nombreColumna + ", fila " + numFila);
							erroresExcelContendor(nombreColumna, numFila);

							break;
						}
					}

				case 7:
					nombreColumna = "deposito";
					valorCelda = validarCelda(celda);

					if (valorCelda.equals(nombreColumna) || valorCelda.equals("nulo")) {
						break;
					} else {
						try {
							contenedor.setDeposito(valorCelda);
							break;
						} catch (Exception e) {
							logger.error("[ContenedorBean - leerContenedor] Error de lectura en la columna "
									+ nombreColumna + ", fila " + numFila);
							erroresExcelContendor(nombreColumna, numFila);

							break;
						}
					}
					
				case 8:
					nombreColumna = "tipo_cont";
					valorCelda = validarCelda(celda);
					
					if (valorCelda.equals(nombreColumna) || valorCelda.equals("nulo")) {
						break;
					} else {
						try {
							contenedor.setTipoCont(valorCelda);
							break;
						} catch (Exception e) {
							logger.error("[ContenedorBean - leerContenedor] Error de lectura en la columna "
									+ nombreColumna + ", fila " + numFila);
							erroresExcelContendor(nombreColumna, numFila);
							
							break;
						}
					}
					
				case 9:
					nombreColumna = "file";
					valorCelda = validarCelda(celda);
					
					if (valorCelda.equals(nombreColumna) || valorCelda.equals("nulo")) {
						break;
					} else {
						try {
							contenedor.setFille(valorCelda);
							break;
						} catch (Exception e) {
							logger.error("[ContenedorBean - leerContenedor] Error de lectura en la columna "
									+ nombreColumna + ", fila " + numFila);
							erroresExcelContendor(nombreColumna, numFila);
							
							break;
						}
					}
					
					
					
				case 10:
					nombreColumna = "booking";
					valorCelda = validarCelda(celda);
					
					if (valorCelda.equals(nombreColumna) || valorCelda.equals("nulo")) {
						break;
					} else {
						try {
							contenedor.setBooking(this.limpiarBooking(valorCelda));
							break;
						} catch (Exception e) {
							logger.error("[ContenedorBean - leerContenedor] Error de lectura en la columna "
									+ nombreColumna + ", fila " + numFila);
							erroresExcelContendor(nombreColumna, numFila);
							
							break;
						}
					}
					
					
				case 11:
					nombreColumna = "embarque";
					valorCelda = validarCelda(celda);
					
					if (valorCelda.equals(nombreColumna) || valorCelda.equals("nulo")) {
						break;
					} else {
						try {
							contenedor.setEmbarque(valorCelda);
							break;
						} catch (Exception e) {
							logger.error("[ContenedorBean - leerContenedor] Error de lectura en la columna "
									+ nombreColumna + ", fila " + numFila);
							erroresExcelContendor(nombreColumna, numFila);
							
							break;
						}
					}

				}

			}
			contenedor.setEstado(Constantes.ESTADO_CONT_DISPONIBLE);
			listaContenedores.add(contenedor);
		}
		workbook.close();
		inputStream.close();

		// Se limpia el primer registro
		listaContenedores.remove(0);

		// validador de que no existan estos registros en la base de datos
		if (erroresObligatorios.isEmpty() && erroresTipeo.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (String sello : listaSellosInternos) {
				sb.append(sello).append(",");
			}
			String sellos = sb.toString();
			List<String> repetidos = new ArrayList<String>();
			// repetidos.addAll(contService.listarSellosDespachados(sellos));
			if (!repetidos.isEmpty()) {
				for (String rep : repetidos) {
					erroresTipeo.put("sellos", rep);
					logger.error("[GuiaDespachoBean - leerContenedor] " + rep);
				}
			} else {
				logger.info("[GuiaDespachoBean - leerContenedor] El archivo " + event.getFile().getFileName()
						+ " no tiene sellos repetidos.");
			}

		}

		// Se valida si existen errores de lectura
		if ((!erroresObligatorios.isEmpty()) || (!erroresTipeo.isEmpty())) {

			this.mensajeError = "";

			if (!this.erroresTipeo.isEmpty()) {
				for (Entry<String, String> errorTipeo : this.erroresTipeo.entrySet()) {
					this.mensajeError = this.mensajeError + "<tr class=\"warning\"> <td>" + errorTipeo.getKey()
							+ "</td> <td> " + errorTipeo.getValue() + "</td> </tr> ";
				}
			}
			if (!this.erroresObligatorios.isEmpty()) {
				for (Entry<String, String> errorOblig : this.erroresObligatorios.entrySet()) {
					this.mensajeError = this.mensajeError + "<tr class=\"danger\"> <td>" + errorOblig.getKey()
							+ " (obligatorio) </td> <td> " + errorOblig.getValue() + "</td> </tr> ";
				}
			}

			this.mensajeError = "<table class=\"table table-sm text-left\"> <thead>  <tr><th scope=\"col\">Columna</th><th scope=\"col\">Fila</th></tr><tbody></thead>"
					+ this.mensajeError + "</tbody></table>";

			logger.error(
					"[GuiaDespachoBean - leerContenedor] Error al cargar el archivo '" + event.getFile().getFileName());

			listaContenedores.clear();
			this.validadorSubida = false;
		} else {
			if (!listaContenedores.isEmpty()) {

				List<Container> contenedoresUpdate = new ArrayList<Container>();

				if (!contenedoresUpdate.isEmpty()) {
					for (Container conte : contenedoresUpdate) {
						listaContenedores.remove(conte);
					}

				}

				if (!listaContenedores.isEmpty()) {
					logger.info("[ContenedorBean - leerContenedor] guardardando contenedores nuevos");
					contService.saveContainer(listaContenedores);
				}

				// Se limpia la lista
				listaContenedores.clear();
				listaSellosInternos.clear();
				contenedoresUpdate.clear();
				this.validadorSubida = true;
				logger.info("[ContenedorBean - leerContenedor] Fin ");
			} else {
				FacesContext.getCurrentInstance().addMessage("mensajes1", new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Error al cargar el archivo " + event.getFile().getFileName(), ""));
				logger.error("[GuiaDespachoBean - leerContenedor] Error al cargar el archivo '"
						+ event.getFile().getFileName());
				this.validadorSubida = false;
			}
		}

	}

	private String limpiarBooking(String valor) {
		String retorno = "";
		valor = valor.replace(".", "");
		valor = valor.replace("E7", "");
		retorno = valor;
		return retorno;
	}

	@SuppressWarnings("deprecation")
	private Object getCellValue(Cell cell) {
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_STRING:
			return cell.getStringCellValue();
		case Cell.CELL_TYPE_BOOLEAN:
			return cell.getBooleanCellValue();
		case Cell.CELL_TYPE_NUMERIC:
			return (int) cell.getNumericCellValue();
		}
		return null;
	}

	public void erroresExcelContendor(String columna, Integer fila) {
		if (columna.equals("sello_texval")) {
			if (this.erroresObligatorios.containsKey(columna)) {
				String antigua = this.erroresObligatorios.get(columna);
				this.erroresObligatorios.put(columna, antigua + " - " + fila);
			} else {
				this.erroresObligatorios.put(columna, fila.toString());
			}
		} else {
			if (this.erroresTipeo.containsKey(columna)) {
				String antigua = this.erroresTipeo.get(columna);
				this.erroresTipeo.put(columna, antigua + " - " + fila);
			} else {
				this.erroresTipeo.put(columna, fila.toString());
			}

		}
	}
	
	
	/**
	 * Método encargado de cargar un contenedor.
	 * 
	 * @return RowEditEvent
	 */
	public void cargaContenedor(RowEditEvent event) {

		this.contenedor = (Container) event.getObject();
		PrimeFaces.current().ajax().update("confimaEditCont");
		PrimeFaces.current().executeScript("bloquearScroll()");
		PrimeFaces.current().executeScript("PF('confimaeditC').show()");

	}
	
	
	/**
	 * Métgteconodo encargado de actualizar un contenedor.
	 */
	public void actulizaContenedor() {
		
		if(validarNroContenedor(this.contenedor.getContainer())) {
			contService.actualizarContainer(this.contenedor);
			PrimeFaces.current().executeScript("PF('confimaeditC').hide()");
			PrimeFaces.current()
			.executeScript(Util.alerta("Actualizacion Realizada", "El Contenedor N°" + this.contenedor.getContainer() 
					+ " ha sido Actualizado", "success"));
			logger.info("[EditarGuiaContenedorBean - actualizaContenedor ] El Contenedor N° " + this.contenedor.getContainer()
			+ " ha sido Actualizado");			
		}else {
			PrimeFaces.current().executeScript("PF('confimaeditC').hide()");
			PrimeFaces.current()
			.executeScript(Util.alerta("Advertencia", "Nro de conenedor invalido", "warning"));
			
		}
			
	}

	/**
	 * Valida y asigna un guion al numero del contenedor de ser requerido
	 * 
	 * @return String
	 * @param String
	 */
	public String insertarGuion(String contenedorNuevo) {
		String parte1 = contenedorNuevo.substring(0, 10);
		String parte2 = contenedorNuevo.substring(10, 11);
		String completo = parte1 + "-" + parte2;
		return completo;
	}

	/**
	 * Metodo que valida el formato del numero de contenedor
	 * 
	 * @return boolean
	 * @param String
	 */
	public boolean validarNroContenedor(String nroContenedor) {

		boolean validador = false;

		if (nroContenedor.length() == 12 && nroContenedor.substring(10, 11).equals("-")) {

			Integer nros;
			try {
				nros = Integer.parseInt(nroContenedor.substring(4, 10));
			} catch (Exception e) {
				nros = null;
			}
			if (nros != null && nros >= 000000 && nros <= 999999) {
				validador = true;
			}
		}

		return validador;
	}

	/**
	 * Método encargado de validar celda.
	 * 
	 * @param Cell
	 * @return String
	 */
	public String validarCelda(Cell celda) {
		try {
			if (celda != null) {
				return celda.toString();
			} else {
				return "nulo";
			}
		} catch (Exception e) {
			System.out.println(e);
			return "nulo";
		}
	}

	/**
	 * Método encargado de listar contenedores.
	 */
	public void listarContenedores() {

		logger.info("[ContenedorBean - listarContenedores] inicio");

		List<Container> contenedoresAux = new ArrayList<Container>();
		try {
			contenedoresAux.addAll(contService.listarContenedoresCargados());

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage("mensajes1",
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al listar contenedores.", ""));
		}
		contenedores.clear();
		contenedores.addAll(contenedoresAux);
	}

	public List<Container> getContenedores() {
		return contenedores;
	}

	public void setContenedores(List<Container> contenedores) {
		this.contenedores = contenedores;
	}

	public List<Container> getListaContenedor() {
		return listaContenedor;
	}

	public void setListaContenedor(List<Container> listaContenedor) {
		this.listaContenedor = listaContenedor;
	}

	public Container getContenedor() {
		return contenedor;
	}

	public void setContenedor(Container contenedor) {
		this.contenedor = contenedor;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getNave() {
		return nave;
	}

	public void setNave(String nave) {
		this.nave = nave;
	}

	public String getNroCont() {
		return nroCont;
	}

	public void setNroCont(String nroCont) {
		this.nroCont = nroCont;
	}

	public Integer getNroGuia() {
		return nroGuia;
	}

	public void setNroGuia(Integer nroGuia) {
		this.nroGuia = nroGuia;
	}

	public StreamedContent getPlantillaExcel() {
		return plantillaExcel;
	}

	public void setPlantillaExcel(StreamedContent plantillaExcel) {
		this.plantillaExcel = plantillaExcel;
	}

	public String getMensajeError() {
		return mensajeError;
	}

	public void setMensajeError(String mensajeError) {
		this.mensajeError = mensajeError;
	}

}
