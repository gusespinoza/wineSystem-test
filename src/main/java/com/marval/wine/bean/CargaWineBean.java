package com.marval.wine.bean;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
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
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.marval.wine.util.Util;
import com.marval.wine.entity.Etiqueta;
import com.marval.wine.entity.PurchaseOrder;
import com.marval.wine.entity.PurchaseOrderDetalle;
import com.marval.wine.service.EtiquetaService;
import com.marval.wine.service.PurchaseOrderService;
import com.marval.wine.util.Constantes;

@ManagedBean
@SessionScope
@Component
@PropertySource(Constantes.RUTA_PROPERTIES)
public class CargaWineBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(CargaWineBean.class);

	@Autowired
	private LoginBean loginBean;

	@Autowired
	EtiquetaService etiquetaService;

	@Autowired
	PurchaseOrderService poService;

	private StreamedContent plantillaExcel;
	private Boolean validadorSubida;
	private HashMap<String, String> erroresTipeo = new HashMap<String, String>();
	private PurchaseOrder po = new PurchaseOrder();
	private PurchaseOrderDetalle pod = new PurchaseOrderDetalle();
	
	private PurchaseOrder poAux = new PurchaseOrder();
	private PurchaseOrderDetalle podAux = new PurchaseOrderDetalle();
	
	private Etiqueta etiqueta = new Etiqueta();
	private List<Etiqueta> listEtiqueta = new ArrayList<Etiqueta>();
	private List<PurchaseOrderDetalle> listDetalles = new ArrayList<PurchaseOrderDetalle>();
	private String poAnterior = "";
	private Integer cantDetalle = 0;
	private String mensajeError;
	private Util util = new Util();
	private Boolean cantDist = false;

	public void onLoad() throws IOException {

		if (loginBean.isMenuProgramacion()) {
			if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) {
				return;
			}
			logger.info("[ContenedorBean - onLoad] inicio");

		} else {
			ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			String path = ctx.getContextPath();
			FacesContext.getCurrentInstance().getExternalContext().redirect(path + "/index.xhtml");
		}

	}

	public void leerExcel(FileUploadEvent event) throws IOException {

		logger.info("[CArgaWineBean - LeerExcel] inicio");
		this.validadorSubida = false;
		this.poAnterior = "";
		this.cantDetalle = 0;
		crearPO();
		crearPod();
		this.cantDist = false;
		
		this.poAux = new PurchaseOrder();
		this.podAux = new PurchaseOrderDetalle();
		
		// Lista que guardara los errores encontrados en el excel
		this.erroresTipeo = new HashMap<String, String>();
		this.listEtiqueta = new ArrayList<Etiqueta>();
		String nombreArchivo = event.getFile().getFileName().toUpperCase();
		InputStream inputStream = event.getFile().getInputstream();

		logger.info("[CArgaWineBean - LeerExcel] nombreArchivo: " + nombreArchivo);
		logger.info("[ContenedorBean - [CArgaWineBean - LeerExcel] inputStream: " + inputStream);

		// Objetos que contienen la informacion del excel
		Workbook workbook;
		Sheet firstSheet;

		// Lista que guarda los objetos obtenidos de la lectura del excel
		List<PurchaseOrder> listaPo = new ArrayList<PurchaseOrder>();

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

			// Objeto que almacena la informacion de una fila en particular
			crearPO();
			crearPod();

			// Ciclo que recorre las columas del excel
			for (int columna = 0; columna < columnas; columna++) {
				celda = filax.getCell(columna);

				// Valor de la fila que se esta leyendo
				numFila = filax.getRowNum() + 1;

				// Se revisa cada columna una por una.
				switch (columna) {

				case 0:

					nombreColumna = "etiqueta";
					valorCelda = validarCelda(celda);
					if (valorCelda.equals(nombreColumna) || valorCelda.equals("nulo")) {
						break;
					} else {
						try {
							crearEtiqueta();
							valorCelda = valorCelda.replace(".", "").replace("E8", "");
							this.etiqueta.setNroEtiquetaAnt(valorCelda);
							
							break;
						} catch (Exception e) {
							logger.error("[CArgaWineBean - LeerExcel] Error de lectura en la columna " + nombreColumna
									+ ", fila " + numFila);
							erroresExcelContendor(nombreColumna, numFila);

							break;
						}
					}
				case 1:
					nombreColumna = "productnum";
					valorCelda = validarCelda(celda);
					if (valorCelda.equals(nombreColumna) || valorCelda.equals("nulo")) {
						break;
					} else {
						try {
							po.setProductNumber(valorCelda);
							pod.setCodProducto(valorCelda);
							break;
						} catch (Exception e) {
							logger.error("[CArgaWineBean - LeerExcel] Error de lectura en la columna " + nombreColumna
									+ ", fila " + numFila);
							erroresExcelContendor(nombreColumna, numFila);

							break;
						}
					}

				case 2:
					nombreColumna = "vintage";
					valorCelda = validarCelda(celda);
					if (valorCelda.equals(nombreColumna) || valorCelda.equals("nulo")) {
						break;
					} else {
						try {
							po.setVintage(valorCelda);
							pod.setCosecha(valorCelda);
							break;
						} catch (Exception e) {
							logger.error("[CArgaWineBean - LeerExcel] Error de lectura en la columna " + nombreColumna
									+ ", fila " + numFila);
							erroresExcelContendor(nombreColumna, numFila);

							break;
						}
					}
				case 3:
					nombreColumna = "orderrefer";
					valorCelda = validarCelda(celda);

					if (valorCelda.equals(nombreColumna) || valorCelda.equals("nulo")) {
						break;
					} else {
						try {
							po.setOrderReference(valorCelda);
							break;
						} catch (Exception e) {
							logger.error("[CArgaWineBean - LeerExcel] Error de lectura en la columna " + nombreColumna
									+ ", fila " + numFila);
							erroresExcelContendor(nombreColumna, numFila);

							break;
						}
					}
				case 4:

					nombreColumna = "purchaseor";
					valorCelda = validarCelda(celda);
					if (valorCelda.equals(nombreColumna) || valorCelda.equals("nulo")) {
						break;
					} else {
						try {valorCelda =  valorCelda.replace(".0", "");
							po.setPurchaseOrderId(valorCelda);
							po.setPurchaseOrder(valorCelda);
							break;
						} catch (Exception e) {
							logger.error("[CArgaWineBean - LeerExcel] Error de lectura en la columna " + nombreColumna
									+ ", fila " + numFila);
							erroresExcelContendor(nombreColumna, numFila);

							break;
						}

					}

				case 5:
					nombreColumna = "ownerid";
					valorCelda = validarCelda(celda);

					if (valorCelda.equals(nombreColumna) || valorCelda.equals("nulo")) {
						break;
					} else {
						try {
							po.setOwnerId(valorCelda);
							po.setSupplierId(valorCelda);
							break;
						} catch (Exception e) {
							logger.error("[CArgaWineBean - LeerExcel] Error de lectura en la columna " + nombreColumna
									+ ", fila " + numFila);
							erroresExcelContendor(nombreColumna, numFila);

							break;
						}
					}

				case 6:
					nombreColumna = "numerocaja";
					valorCelda = validarCelda(celda);

					if (valorCelda.equals(nombreColumna) || valorCelda.equals("nulo")) {
						break;
					} else {
						try {
							valorCeldaInt = (Integer) getCellValue(celda);
							this.pod.setCajasPorPallet(valorCeldaInt);
							this.etiqueta.setCajasPallet(valorCeldaInt);
							break;
						} catch (Exception e) {
							logger.error("[CArgaWineBean - LeerExcel] Error de lectura en la columna " + nombreColumna
									+ ", fila " + numFila);
							erroresExcelContendor(nombreColumna, numFila);
							break;
						}
					}

				case 7:
					nombreColumna = "numbotunit";
					valorCelda = validarCelda(celda);

					if (valorCelda.equals(nombreColumna) || valorCelda.equals("nulo")) {
						break;
					} else {
						try {
							valorCeldaInt = (Integer) getCellValue(celda);
							this.po.setBottlesPerUnit(valorCeldaInt);
							break;
						} catch (Exception e) {
							logger.error("[CArgaWineBean - LeerExcel] Error de lectura en la columna " + nombreColumna
									+ ", fila " + numFila);
							erroresExcelContendor(nombreColumna, numFila);
							break;
						}
					}

				case 8:
					nombreColumna = "posbodega";
					valorCelda = validarCelda(celda);

					if (valorCelda.equals(nombreColumna) || valorCelda.equals("nulo")) {
						break;
					} else {
						try {

							asignarUbicacion(valorCelda);
							break;

						} catch (Exception e) {
							logger.error("[CArgaWineBean - LeerExcel] Error de lectura en la columna " + nombreColumna
									+ ", fila " + numFila);
							erroresExcelContendor(nombreColumna, numFila);

							break;
						}
					}

				case 9:
					nombreColumna = "fecharecep";
					valorCelda = validarCelda(celda);

					if (valorCelda.equals(nombreColumna) || valorCelda.equals("nulo")) {
						break;
					} else {
						try {
							if (!valorCelda.isEmpty()) {
								// Se guarda el valor tipo Date que viene en la celda
								this.pod.setFechaRecepcion(
										util.convertirFecha(String.valueOf(celda.getNumericCellValue())));
							}
							break;
						} catch (Exception e) {
							logger.error("[CArgaWineBean - LeerExcel] Error de lectura en la columna " + nombreColumna
									+ ", fila " + numFila);
							erroresExcelContendor(nombreColumna, numFila);

							break;
						}
					}

				case 10:
					nombreColumna = "numerodocu";
					valorCelda = validarCelda(celda);

					if (valorCelda.equals(nombreColumna) || valorCelda.equals("nulo")) {
						break;
					} else {
						try {
							valorCeldaInt = (Integer) getCellValue(celda);
							this.pod.setNumeroDoc(valorCeldaInt);
							break;
						} catch (Exception e) {
							logger.error("[CArgaWineBean - LeerExcel] Error de lectura en la columna " + nombreColumna
									+ ", fila " + numFila);
							erroresExcelContendor(nombreColumna, numFila);

							break;
						}
					}

				case 11:
					nombreColumna = "repaletiza";
					valorCelda = validarCelda(celda);

					if (valorCelda.equals(nombreColumna) || valorCelda.equals("nulo")) {
						break;
					} else {
						try {
							if (valorCelda.equals("1.0")) {
								this.etiqueta.setRepaletizado(true);
							} else {
								this.etiqueta.setRepaletizado(false);
							}
							break;
						} catch (Exception e) {
							logger.error("[CArgaWineBean - LeerExcel] Error de lectura en la columna " + nombreColumna
									+ ", fila " + numFila);
							erroresExcelContendor(nombreColumna, numFila);

							break;
						}
					}

				case 12:
					nombreColumna = "fecharepal";
					valorCelda = validarCelda(celda);

					if (valorCelda.equals(nombreColumna) || valorCelda.equals("nulo")) {
						break;
					} else {
						try {
							if (this.etiqueta.getRepaletizado()) {
								this.etiqueta.setFechaRepaletizado(
										util.convertirFecha(String.valueOf(celda.getNumericCellValue())));
							}
							break;
						} catch (Exception e) {
							logger.error("[CArgaWineBean - LeerExcel] Error de lectura en la columna " + nombreColumna
									+ ", fila " + numFila);
							erroresExcelContendor(nombreColumna, numFila);

							break;
						}
					}

				case 13:
					nombreColumna = "lote";
					valorCelda = validarCelda(celda);

					if (valorCelda.equals(nombreColumna) || valorCelda.equals("nulo")) {
						break;
					} else {
						try {
							pod.setLote(valorCelda);
							break;
						} catch (Exception e) {
							logger.error("[CArgaWineBean - LeerExcel] Error de lectura en la columna " + nombreColumna
									+ ", fila " + numFila);
							erroresExcelContendor(nombreColumna, numFila);

							break;
						}
					}

				case 14:
					nombreColumna = "codigofert";
					valorCelda = validarCelda(celda);

					if (valorCelda.equals(nombreColumna) || valorCelda.equals("nulo")) {
						break;
					} else {
						try {
							this.po.setCodFert(valorCelda);
							break;
						} catch (Exception e) {
							logger.error("[CArgaWineBean - LeerExcel] Error de lectura en la columna " + nombreColumna
									+ ", fila " + numFila);
							erroresExcelContendor(nombreColumna, numFila);

							break;
						}
					}
				}
			}

			if (this.po.getPurchaseOrderId() != null) {
				
				
				String poAnteriorAux = this.po.getPurchaseOrderId();
				Integer cantDetalleAux = this.etiqueta.getCajasPallet();
				
				if (this.listEtiqueta.isEmpty()) {
					this.listEtiqueta.add(this.etiqueta);
				}
				
				if(this.poAnterior.equals("")) {
					this.poAux = this.po;
					this.podAux = this.pod;
					
				} else if (this.poAnterior.equals(this.po.getPurchaseOrderId())) {

					if (this.cantDetalle.equals(this.etiqueta.getCajasPallet())) {
						this.listEtiqueta.add(this.etiqueta);
					} else {
						if (!this.cantDetalle.equals(this.etiqueta.getCajasPallet())) {
							this.cantDist = true;
						} else {
							this.cantDist = false;
						}
					}

				} else {
					
					
					if(this.cantDist) {
						listaPo.add(totalPoSinDetalle());
						cantDist = false;
					} else {
						listaPo.add(totalPo());
					}
					
//					listaPo.add(totalPo());
					
					this.listEtiqueta.clear();
					this.listEtiqueta.add(this.etiqueta);
				}
				
				
				
				if(this.cantDist) {
					totalDetalle();
					
//					if (!this.listEtiqueta.isEmpty()) {
//						Integer totalPallet = 0;
//						Integer totalCajas = 0;
//
//						for (Etiqueta et : this.listEtiqueta) {
//							et.setDetallePo(this.podAux);
//							totalCajas = totalCajas + et.getCajasPallet();
//							totalPallet = totalPallet + 1;
//						}
//						this.podAux.setPallet(totalPallet);
//						this.podAux.setCantidad(totalCajas);
//						this.podAux.getEtiquetas().addAll(this.listEtiqueta);
//						this.podAux.setPo(this.poAux);			
//						this.poAux.getDetalle().add(this.podAux);
//
//					}
					
					this.listEtiqueta.clear();
					this.listEtiqueta.add(this.etiqueta);
					this.pod.getEtiquetas().addAll(this.listEtiqueta);
					
					
					Integer totalPallet = 0;
					Integer totalCajas = 0;

					for (Etiqueta et : this.listEtiqueta) {
						et.setDetallePo(this.pod);
						totalCajas = totalCajas + et.getCajasPallet();
						totalPallet = totalPallet + 1;
					}
					this.pod.setPallet(totalPallet);
					this.pod.setCantidad(totalCajas);
					this.pod.setPo(this.poAux);
					this.poAux.getDetalle().add(this.pod);
					
					this.poAnterior = poAnteriorAux;
					this.cantDetalle = cantDetalleAux;
					
//					cantDist = false;
					
					continue;
				}				
				
				this.podAux = this.pod;
				this.poAux = this.po;	
				this.poAnterior = poAnteriorAux;
				this.cantDetalle = cantDetalleAux;
				System.out.println("DESPUES: Fila: " + numFila );
			}
		}
		listaPo.add(totalPo());
		workbook.close();
		inputStream.close();

		// Se limpia el primer registro
		// listaPo.remove(0);

		// Se valida si existen errores de lectura
		if (!erroresTipeo.isEmpty()) {

			this.mensajeError = "";

			if (!this.erroresTipeo.isEmpty()) {
				for (Entry<String, String> errorTipeo : this.erroresTipeo.entrySet()) {
					this.mensajeError = this.mensajeError + "<tr class=\"warning\"> <td>" + errorTipeo.getKey()
							+ "</td> <td> " + errorTipeo.getValue() + "</td> </tr> ";
				}
			}
			this.mensajeError = "<table class=\"table table-sm text-left\"> <thead>  <tr><th scope=\"col\">Columna</th><th scope=\"col\">Fila</th></tr><tbody></thead>"
					+ this.mensajeError + "</tbody></table>";
			logger.error(
					"[GuiaDespachoBean - leerContenedor] Error al cargar el archivo '" + event.getFile().getFileName());
			logger.error(this.mensajeError);

			listaPo.clear();
			this.validadorSubida = false;
		} else {
			if (!listaPo.isEmpty()) {

				logger.info("[CargaWineBean - LeerExcel] guardardando Purchase order nuevos");
				poService.savePurchaseOrder(listaPo);

				// Se limpia la lista
				listaPo.clear();
				this.validadorSubida = true;
				logger.info("[CArgaWineBean - LeerExcel] Fin ");
			} else {
				FacesContext.getCurrentInstance().addMessage("mensajes1", new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Error al cargar el archivo " + event.getFile().getFileName(), ""));
				logger.error("[GuiaDespachoBean - leerContenedor] Error al cargar el archivo '"
						+ event.getFile().getFileName());
				this.validadorSubida = false;
			}
		}

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

	public void erroresExcelContendor(String columna, Integer fila) {

		if (this.erroresTipeo.containsKey(columna)) {
			String antigua = this.erroresTipeo.get(columna);
			this.erroresTipeo.put(columna, antigua + " - " + fila);
		} else {
			this.erroresTipeo.put(columna, fila.toString());
		}

	}
	
	public void totalDetalle() {
		if (!this.listEtiqueta.isEmpty()) {
			Integer totalPallet = 0;
			Integer totalCajas = 0;

			for (Etiqueta et : this.listEtiqueta) {
				et.setDetallePo(this.podAux);
				totalCajas = totalCajas + et.getCajasPallet();
				totalPallet = totalPallet + 1;
			}
			this.podAux.setPallet(totalPallet);
			this.podAux.setCantidad(totalCajas);
			this.podAux.getEtiquetas().addAll(this.listEtiqueta);
			this.podAux.setPo(this.poAux);			
			this.poAux.getDetalle().add(this.podAux);

		}
	}
	
	public PurchaseOrder totalPoSinDetalle() {
		Integer totalCajasDetalle = 0;
//		Integer totalPallet = 0;
		Integer totalCajas = 0;
		PurchaseOrder nuevoPo = new PurchaseOrder();
		if (!this.poAux.getDetalle().isEmpty()) {
			for (PurchaseOrderDetalle pd : this.poAux.getDetalle()) {
				totalCajas = totalCajas + pd.getCantidad();
			}
		}
		
		this.poAux.setRecivedQuantity(totalCajas);
		this.poAux.setPurchaseOrderQuantity(totalCajas);
		nuevoPo = this.poAux;
		return nuevoPo;

	}


	public PurchaseOrder totalPo() {
		Integer totalCajas = 0;
		PurchaseOrder nuevoPo = new PurchaseOrder();
		totalDetalle();
		if (!this.poAux.getDetalle().isEmpty()) {
			for (PurchaseOrderDetalle pd : this.poAux.getDetalle()) {
				totalCajas = totalCajas + pd.getCantidad();
			}
		}
		this.poAux.setRecivedQuantity(totalCajas);
		this.poAux.setPurchaseOrderQuantity(totalCajas);
		nuevoPo = this.poAux;
		return nuevoPo;

	}
	


	public void crearEtiqueta() {
		this.etiqueta = new Etiqueta();
		this.etiqueta.setFechaCr(new Date());
		this.etiqueta.setAsignado(false);
		this.etiqueta.setNroEtiqueta(etiquetaService.obtenerEtiqueta());
		this.etiqueta.setSlipsheet(false);
		this.etiqueta.setEstado(Constantes.ESTADO_ETIQUETA_ALMACENADA);
		this.etiqueta.setHistorialRepaletizado(this.etiqueta.getNroEtiqueta());
	}

	public void crearPO() {
		this.po = new PurchaseOrder();
		this.po.setFechaCr(new Date());
		this.po.setSlipsheet(false);
		this.po.setInterfaceId("PURCHASEORDER");
		this.po.setSendingSystem("JFHILLEBRAND");
		this.po.setWarehouseId("SEAPORT");
		this.po.setStandardStorageUnit("CS");
		this.po.setEstado("RECEPCIONADO");
		this.po.setTipoRecepcion("AXIS");
	}

	public void crearPod() {
		this.pod = new PurchaseOrderDetalle();
		this.pod.setFechaCr(new Date());
		this.pod.setTipoDoc("GUIA DESPACHO");
		this.pod.setEstadoCarga("Buen estado");
		this.pod.setTotalParcial("SI");
		this.pod.setEstado("RECEPCIONADO");
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

	public void asignarUbicacion(String posAntigua) {
//		logger.info("[CargaWineBean - LeerExcel] Pos: " + posAntigua);
		if (!posAntigua.equals("0.0")) {
			if (this.etiqueta != null) {
				String numero = posAntigua.trim().replace(".", "").replace("E9", "");
//				logger.info("[CargaWineBean - LeerExcel] Pos: " + numero);
				this.etiqueta.setPasillo(numero.substring(4, 6));
				this.etiqueta.setPosicion(numero.substring(6, 8));
				this.etiqueta.setAltura(numero.substring(8, 10));
			}
		}else {
			this.etiqueta.setPasillo("0");
			this.etiqueta.setPosicion("0");
			this.etiqueta.setAltura("0");
		}
	}

	public StreamedContent getPlantillaExcel() {
		return plantillaExcel;
	}

	public void setPlantillaExcel(StreamedContent plantillaExcel) {
		this.plantillaExcel = plantillaExcel;
	}

	public Boolean getValidadorSubida() {
		return validadorSubida;
	}

	public void setValidadorSubida(Boolean validadorSubida) {
		this.validadorSubida = validadorSubida;
	}

	public HashMap<String, String> getErroresTipeo() {
		return erroresTipeo;
	}

	public void setErroresTipeo(HashMap<String, String> erroresTipeo) {
		this.erroresTipeo = erroresTipeo;
	}

	public PurchaseOrder getPo() {
		return po;
	}

	public void setPo(PurchaseOrder po) {
		this.po = po;
	}

	public PurchaseOrderDetalle getPod() {
		return pod;
	}

	public void setPod(PurchaseOrderDetalle pod) {
		this.pod = pod;
	}

	public Etiqueta getEtiqueta() {
		return etiqueta;
	}

	public void setEtiqueta(Etiqueta etiqueta) {
		this.etiqueta = etiqueta;
	}

	public List<Etiqueta> getListEtiqueta() {
		return listEtiqueta;
	}

	public void setListEtiqueta(List<Etiqueta> listEtiqueta) {
		this.listEtiqueta = listEtiqueta;
	}

	public String getPoAnterior() {
		return poAnterior;
	}

	public void setPoAnterior(String poAnterior) {
		this.poAnterior = poAnterior;
	}

	public String getMensajeError() {
		return mensajeError;
	}

	public void setMensajeError(String mensajeError) {
		this.mensajeError = mensajeError;
	}

	public List<PurchaseOrderDetalle> getListDetalles() {
		return listDetalles;
	}

	public void setListDetalles(List<PurchaseOrderDetalle> listDetalles) {
		this.listDetalles = listDetalles;
	}

	public Integer getCantDetalle() {
		return cantDetalle;
	}

	public void setCantDetalle(Integer cantDetalle) {
		this.cantDetalle = cantDetalle;
	}

	public PurchaseOrder getPoAux() {
		return poAux;
	}

	public void setPoAux(PurchaseOrder poAux) {
		this.poAux = poAux;
	}

	public PurchaseOrderDetalle getPodAux() {
		return podAux;
	}

	public void setPodAux(PurchaseOrderDetalle podAux) {
		this.podAux = podAux;
	}

	public Boolean getCantDist() {
		return cantDist;
	}

	public void setCantDist(Boolean cantDist) {
		this.cantDist = cantDist;
	}
	
	

}
