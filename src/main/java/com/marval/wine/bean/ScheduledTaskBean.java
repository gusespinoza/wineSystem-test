package com.marval.wine.bean;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.marval.wine.entity.HistorialIndicadores;
import com.marval.wine.entity.IndicadoresFinancieros;
import com.marval.wine.entity.IndicadoresFinancierosUF;
import com.marval.wine.entity.Mensaje;
import com.marval.wine.entity.PickingTicket;
import com.marval.wine.entity.PurchaseOrder;
import com.marval.wine.ftp.ConectaSFTP;
import com.marval.wine.service.IndicadoresService;
import com.marval.wine.service.PickingTicketService;
import com.marval.wine.service.PurchaseOrderService;
import com.marval.wine.util.Constantes;

/**
 * @author abelbrigna
 *
 */
@ManagedBean
@Component
@EnableScheduling
public class ScheduledTaskBean {

	private static final Logger logger = LoggerFactory.getLogger(ScheduledTaskBean.class);
	
	@Autowired
	ConectaSFTP conectaSFtp;

	@Autowired
	PurchaseOrderService poService;

	@Autowired
	PickingTicketService ptService;
	
	@Autowired
	IndicadoresService indicadoresService;
	
	private IndicadoresFinancieros indicadores = new IndicadoresFinancieros();
	private IndicadoresFinancierosUF indicadoresUF = new IndicadoresFinancierosUF();
	
	InputStream archivo;
	InputStream archivoAux;
	

	//@Scheduled(cron = "*/10 * * * * *")
	@Scheduled(cron = "0 0/5 * * * ?")
	public void conexionHolanda() {
		String ayer =  new SimpleDateFormat("yyyy-MM-dd").format(ayer());
		String hoy =  new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		try {
			
			Mensaje mensaje;
			mensaje = new Mensaje();
			mensaje.setUsuarioCr("SYSTEM");
			mensaje.setFechaCr(new Date());
			mensaje.setNombreArchivo("");
			mensaje.setMensaje("TAREA TRABAJANDO - LEYENDO ARCHIVOS");
			mensaje.setTipo(Constantes.CHECK);
			this.poService.guardarMensaje(mensaje);
			
			//logger.info("[conexionHolanda] Se buscan archivos del dia de ayer: "+  ayer);
			//readFiles(ayer);
			logger.info("[conexionHolanda] Se buscan archivos del dia de hoy: " + hoy);
			readFiles(hoy);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readFiles(String date) throws SocketException, IOException {
		List<String> archivos = new ArrayList<String>();
		
		String raizFtp = "/out/work/";
		Mensaje mensaje;
		
		archivos.addAll(conectaSFtp.listaArchivosPorDirectorioHolanda(raizFtp));
		logger.info("[readFiles] Se trae lista de archivos de carpeta OUT");
		
		if (!archivos.isEmpty()) {
			logger.info("[readFiles] Nuevos archivos encontrados.");
			for (String nombreArchivo : archivos) {
				
				logger.info("[readFiles] archivo: " + nombreArchivo);

				if (this.poService.consultarArchivoProcesado(nombreArchivo)) {
					continue;
				}
				
				mensaje = new Mensaje();
				mensaje.setUsuarioCr("SYSTEM");
				mensaje.setFechaCr(new Date());
				mensaje.setNombreArchivo(nombreArchivo);

				String rutaArchivo = raizFtp + nombreArchivo;
				
				logger.info("[readFiles] descargaArchivoHolanda");
				
				this.archivo = conectaSFtp.descargaArchivoHolanda(rutaArchivo);
				this.archivoAux = conectaSFtp.descargaArchivoHolanda(rutaArchivo);
				
				if(this.archivo == null || this.archivoAux == null) {
					logger.info("[readFiles] archivo null");
					continue;
				}
				
				String result = new BufferedReader(new InputStreamReader(this.archivo))
						   .lines().collect(Collectors.joining("\n"));
				mensaje.setMensaje(result);

				
				if (nombreArchivo.contains("POSP") || nombreArchivo.contains("posp")) {
					
					mensaje.setTipo(Constantes.ARCHIVO_PURCHASEORDER);
					this.poService.guardarMensaje(mensaje);
					
					logger.info("[readFiles] Comienza lectura Archivo PO " + nombreArchivo);
					leerPO(this.archivoAux, nombreArchivo);
					logger.info("[readFiles] Termina lectura Archivo PO " + nombreArchivo);
					
				} else {
					if (nombreArchivo.contains("PTSP") || nombreArchivo.contains("ptsp")) {
						
						mensaje.setTipo(Constantes.ARCHIVO_PICKTICKET);
						this.poService.guardarMensaje(mensaje);
						
						logger.info("[readFiles] Comienza lectura Archivo PT " + nombreArchivo);
						leetPT(this.archivoAux, nombreArchivo);
						logger.info("[readFiles] Termina lectura Archivo PT " + nombreArchivo);
						
					}
				}

				logger.info("[readFiles] guarda mensaje");
				//mensaje.setMensaje(this.backupFile(archivo));
				
				
				 logger.info("[readFiles] Se elimina a|rchivo " + nombreArchivo + " de carpeta OUT");
				 conectaSFtp.borrar(nombreArchivo, raizFtp);
			}
		} else {
			logger.info("[readFiles] No se han encontrado nuevos archivos en la  carpeta OUT");
		}

	}



	public void leerPO(InputStream archivo, String nombre) throws IOException {
		List<String> resultado = new ArrayList<String>();
		resultado.addAll(lineasArchivo(archivo));
		logger.info("[leerPO] tamanho:" + resultado.size());
		List<PurchaseOrder> listaPo = new ArrayList<PurchaseOrder>();
		for (String linea : resultado) {
			if (!linea.isEmpty()) {
				String[] dato = linea.split("\\*");
				Integer numParam = dato.length - 1;
				PurchaseOrder po = new PurchaseOrder();

				po.setInterfaceId((dato[0] != null) ? dato[0].trim() : "");
				po.setSendingSystem((dato[1] != null) ? dato[1].trim() : "");
				po.setInterfaceCreationDate((dato[2] != null) ? dato[2].trim() : "");
				po.setPurchaseOrderId((dato[3] != null) ? dato[3].trim() : "");
				po.setOrderReference((dato[4] != null) ? dato[4].trim() : "");
				po.setWarehouseId((dato[5] != null) ? dato[5].trim() : "");
				po.setOwnerId((dato[6] != null) ? dato[6].trim() : "");
				po.setSupplierId((dato[7] != null) ? dato[7].trim() : "");

				Integer purchaseOrderQuantityValue = 0;
				String valorDato8 = (dato[8] != null) ? dato[8].trim() : "";
				if (!valorDato8.isEmpty()) {
					try {
						purchaseOrderQuantityValue = Integer.valueOf(valorDato8);
					} catch (NumberFormatException e) {
						logger.error("Error al convertir el dato de cantidad de orden de compra '" + valorDato8
								+ "' a Integer. Se asignará 0. Detalle: " + e.getMessage());
					}
				}
				po.setPurchaseOrderQuantity(purchaseOrderQuantityValue);

				Integer recivedQuantityValue = 0;
				String valorDato9 = (dato[9] != null) ? dato[9].trim() : "";
				if (!valorDato9.isEmpty()) {
					try {
						recivedQuantityValue = Integer.valueOf(valorDato9);
					} catch (NumberFormatException e) {
						logger.error("Error al convertir el dato de cantidad recibida '" + valorDato9
								+ "' a Integer. Se asignará 0. Detalle: " + e.getMessage());
					}
				}
				po.setRecivedQuantity(recivedQuantityValue);

				po.setExpectedArrivalDate((dato[10] != null) ? dato[10].trim() : "");
				po.setProductNumber((dato[11] != null) ? dato[11].trim() : "");
				po.setProductDescription((dato[12] != null) ? dato[12].trim() : "");
				po.setStandardStorageUnit((dato[13] != null) ? dato[13].trim() : "");

				Integer bottlesPerUnitValue = 0;
				String valorDato14 = (dato[14] != null) ? dato[14].trim() : "";
				if (!valorDato14.isEmpty()) {
					try {
						bottlesPerUnitValue = Integer.valueOf(valorDato14);
					} catch (NumberFormatException e) {
						logger.error("Error al convertir el dato de botellas por unidad '" + valorDato14
								+ "' a Integer. Se asignará 0. Detalle: " + e.getMessage());
					}
				}
				po.setBottlesPerUnit(bottlesPerUnitValue);

				Integer mlPerBottleValue = 0;
				String valorDato15 = (dato[15] != null) ? dato[15].trim() : "";
				if (!valorDato15.isEmpty()) {
					try {
						mlPerBottleValue = Integer.valueOf(valorDato15);
					} catch (NumberFormatException e) {
						logger.error("Error al convertir el dato de ML por botella '" + valorDato15
								+ "' a Integer. Se asignará 0. Detalle: " + e.getMessage());
					}
				}
				po.setMlPerBottle(mlPerBottleValue);

				logger.info("[leerPO] dato[16] " + dato[16]);

				Double alcoholPercentageValue = 0.0;
				String valorDato16 = (dato[16] != null) ? dato[16].trim() : "";
				if (valorDato16.isEmpty() || valorDato16.equalsIgnoreCase("NULL")) {
					alcoholPercentageValue = 0.0;
				} else {
					try {
						alcoholPercentageValue = Double.valueOf(valorDato16);
					} catch (NumberFormatException e) {
						logger.error("Error al convertir el dato de alcohol '" + valorDato16
								+ "' a Double. Se asignará 0.0. Detalle: " + e.getMessage());
					}
				}
				po.setAlcoholPercentage(alcoholPercentageValue);

				po.setProductCategory((dato[17] != null) ? dato[17].trim() : "");

				Double weightValue = 0.0;
				String valorDato18 = (dato[18] != null) ? dato[18].trim() : "";
				if (!valorDato18.isEmpty()) {
					try {
						weightValue = Double.valueOf(valorDato18);
					} catch (NumberFormatException e) {
						logger.error("Error al convertir el dato de peso '" + valorDato18
								+ "' a Double. Se asignará 0.0. Detalle: " + e.getMessage());
					}
				}
				po.setWeight(weightValue);

				Double volumeValue = 0.0;
				String valorDato19 = (dato[19] != null) ? dato[19].trim() : "";
				if (!valorDato19.isEmpty()) {
					try {
						volumeValue = Double.valueOf(valorDato19);
					} catch (NumberFormatException e) {
						logger.error("Error al convertir el dato de volumen '" + valorDato19
								+ "' a Double. Se asignará 0.0. Detalle: " + e.getMessage());
					}
				}
				po.setVolume(volumeValue);

				po.setVintage((dato[20] != null) ? dato[20].trim() : "");

				if (numParam > 20) {
					if (dato[21] != null) {
						logger.info("[leerPO] lote " + dato[21].trim());
						po.setLote(leerLote(dato[21].trim()));
					} else {
						logger.info("[leerPO] lote null");
						po.setLote(null);
					}

					if (numParam > 21) {
						if (dato[22] != null) {
							po.setCodFert(dato[22].trim());
						} else {
							po.setCodFert("");
						}
					}
				}

				po.setFechaCr(new Date());
				po.setEstado(Constantes.ESTADO_PO_DISPONIBLE);
				po.setTipoRecepcion("AXIS");
				
				logger.info("");

				if (po.getPurchaseOrderQuantity() > 0) {
					listaPo.add(po);
				}
			}

		}

		if (!listaPo.isEmpty()) {
			poService.savePurchaseOrder(listaPo);
			// backupFile(nombre, resultado);
		} else {
			logger.info("[archivo sin PO -  vacio - snuh]");
		}

	}
	
	public String leerLote(String lote) {
	    if (lote == null || lote.isEmpty()) {
	        return "";
	    }
	    if (lote.contains("/")) {
	        String[] partes = lote.split("/", 2);
	        if (partes.length > 1) {
	            return partes[1];
	        } else {
	            return "";
	        }
	    }
	    return lote;
	}

	public void leetPT(InputStream archivo,String nombre) throws IOException {
		List<String> resultado = new ArrayList<String>(); 
		resultado.addAll(lineasArchivo(archivo));
		List<PickingTicket> listaPt = new ArrayList<PickingTicket>();
		for (String linea : resultado) {
			if (!linea.isEmpty()) {
				String[] dato = linea.split("\\*");
				PickingTicket po = new PickingTicket();
				po.setInterfaceId(dato[0].trim());
				po.setSendingSystem(dato[1].trim());
				po.setInterfaceCreationDate(dato[2].trim());
				po.setSalesOrderId(dato[3].trim());
				po.setSalesOrderline(dato[4].trim());
				po.setOrdenReference(dato[5].trim());
				po.setShipmentId(dato[6].trim());
				po.setWarehouseId(dato[7].trim());
				po.setOwnerId(dato[8].trim());
				po.setProductNumber(dato[9].trim());
				po.setOrderQuantity(Integer.valueOf(dato[10].trim()));
				po.setShipmentQuantity(Integer.valueOf(dato[11].trim()));
				po.setRequestShipDate(dato[12].trim());
				po.setLotNumber(dato[13].trim());
				po.setShipToCustomerId(dato[14].trim());
				po.setShipToName(dato[15].trim());
				po.setShipToAddress1(dato[16].trim());
				po.setShipToAddress2(dato[17].trim());
				po.setShipToAddress3(dato[18].trim());
				po.setShipToAddress4(dato[19].trim());
				po.setFechaCr(new Date());
				po.setEstado(Constantes.ESTADO_PO_DISPONIBLE);
				
				if(po.getOrderQuantity() == 0) {
					if(po.getShipmentQuantity() > 0) {
						po.setOrderQuantity(po.getShipmentQuantity());
					}
				}

				listaPt.add(po);
			}

		}

		if (!listaPt.isEmpty()) {
			ptService.savePickingTicket(listaPt);
			//backupFile(nombre, resultado);
		}
	}

	public List<String> lineasArchivo(InputStream archivo) {
		BufferedReader resultado = new BufferedReader(new InputStreamReader(archivo));
		Object[] lineas = resultado.lines().toArray();
		List<String> lineasFinal = new ArrayList();
		for (int i = 0; i < lineas.length; i++) {
			lineasFinal.add(lineas[i].toString());
		}
		logger.info("[lineasArchivo] cantidad de lineas en archivo :" +  lineasFinal.size());
		return lineasFinal;
	}
	
	
	public void backupFile(String nombreArchivo, List<String> lineas) throws IOException {
		logger.info("[backupFile] Se comienza respaldo archivo " + nombreArchivo);
		if(!lineas.isEmpty()) {
			StringBuffer file = new StringBuffer();
			for(String linea :  lineas){
				file.append(linea + "\n");
			}
			logger.info(file.toString());
			conectaSFtp.subeBackUp(new ByteArrayInputStream(file.toString().getBytes()), nombreArchivo);
		}

		logger.info("[backupFile] Respaldo archivo  " + nombreArchivo + " Completado");
	}
	
	public String backupFile(InputStream input) throws IOException {
		List<String> lineas = lineasArchivo(input);
		StringBuffer file = new StringBuffer();

		if (!lineas.isEmpty()) {
			for (String linea : lineas) {
				file.append(linea + "\n");
			}
		}

		return file.toString();
	}
	
	
	
	@Scheduled(cron = "0 0/45 * * * ?")
	public void valoresDelDia() throws SocketException, IOException {
		
		logger.info("[valoresDelDia] va a buscar valores del dia");
		
		HistorialIndicadores historial = new HistorialIndicadores();
		
		
		historial = this.indicadoresService.indicadorMonedaFecha("dolar", new Date());

		if (historial == null || historial.getValorCambio() == null || historial.getValorCambio().intValue() == 0) {

			logger.info("[valoresDelDia] no hay indicador para hoy en BD, se actualiza desde servicio SBIF");

			try {
				indicadores = this.indicadoresService.indicadorEconomicoSBIF("dolar");
				indicadoresUF = this.indicadoresService.indicadorEconomicoUF("uf");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (indicadores == null || indicadores.getDolares().isEmpty()
					|| indicadores.getDolares().get(0).getValor() == null) {

				Calendar cal = Calendar.getInstance();

				while (historial == null) {
					cal.add(Calendar.DATE, -1);
					historial = this.indicadoresService.indicadorMonedaFecha("dolar", cal.getTime());
				}

				HistorialIndicadores histGuardar = new HistorialIndicadores();

				histGuardar.setFechaCr(new Date());
				histGuardar.setFechaUp(null);
				histGuardar.setFechCambio(new Date());
				histGuardar.setMoneda("dolar");
				histGuardar.setUsuarioCr(Constantes.USUARIO_SISTEMA);
				histGuardar.setValorCambio(historial.getValorCambio());


				indicadoresService.guardaHistorialIndicador(histGuardar);
				
				
				while (historial == null) {
					cal.add(Calendar.DATE, -1);
					historial = this.indicadoresService.indicadorMonedaFecha("uf", cal.getTime());
				}
				
				//guarda UF
				histGuardar = new HistorialIndicadores();
				histGuardar.setFechaCr(new Date());
				histGuardar.setFechaUp(null);
				histGuardar.setFechCambio(new Date());
				histGuardar.setMoneda("uf");
				histGuardar.setUsuarioCr(Constantes.USUARIO_SISTEMA);
				histGuardar.setValorCambio(historial.getValorCambio());
				indicadoresService.guardaHistorialIndicador(histGuardar);
				

				logger.info(
						"[valoresDelDia] indicador dolar no obtenido (feriado o error en el servicio), se guarda el valor del día anterior");
			} else {

				historial = new HistorialIndicadores();

				historial.setFechaCr(new Date());
				historial.setFechCambio(new Date());
				historial.setMoneda("dolar");
				historial.setUsuarioCr(Constantes.USUARIO_SISTEMA);
				historial.setValorCambio(new BigDecimal(indicadores.getDolares().get(0).getValor().replace(',', '.')));

				indicadoresService.guardaHistorialIndicador(historial);

				
				
				historial = new HistorialIndicadores();
				historial.setFechaCr(new Date());
				historial.setFechCambio(new Date());
				historial.setMoneda("uf");
				historial.setUsuarioCr(Constantes.USUARIO_SISTEMA);
				historial.setValorCambio(new BigDecimal(indicadoresUF.getUfs().get(0).getValor().replace(".", "").replace(',', '.')));

				indicadoresService.guardaHistorialIndicador(historial);
				
				logger.info("[valoresDelDia] indicador dolar y UF de hoy agregado");
			}

		} 

	}
	
	
	private Date ayer() {
	    final Calendar cal = Calendar.getInstance();
	    cal.add(Calendar.DATE, -1);
	    return cal.getTime();
	}



	public InputStream getArchivo() {
		return archivo;
	}



	public void setArchivo(InputStream archivo) {
		this.archivo = archivo;
	}



	public InputStream getArchivoAux() {
		return archivoAux;
	}



	public void setArchivoAux(InputStream archivoAux) {
		this.archivoAux = archivoAux;
	}
	
	
	
	

}