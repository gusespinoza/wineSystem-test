package com.marval.wine.report;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marval.wine.entity.Existencia;
import com.marval.wine.entity.ResumenContenedor;
import com.marval.wine.entity.ResumenFacturacion;
import com.marval.wine.entity.ResumenNaveViaje;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ReporteExcel {
	
	private static final Logger logger = LoggerFactory.getLogger(ReporteExcel.class);

	// DEFINIR FUENTE PARA EL CONTENIDO
	private WritableFont hv = new WritableFont(WritableFont.COURIER, 10, WritableFont.NO_BOLD);
	private WritableFont ht = new WritableFont(WritableFont.COURIER, 13, WritableFont.BOLD);
	private WritableFont hc = new WritableFont(WritableFont.COURIER, 11, WritableFont.BOLD);
	private WritableFont hd = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD);

	private WritableCellFormat hformatTitulos = new WritableCellFormat(ht);
	private WritableCellFormat hformatViaje = new WritableCellFormat(hv);
	private WritableCellFormat hformatColumnas = new WritableCellFormat(hc);
	private WritableCellFormat hformatData = new WritableCellFormat(hd);
	private WritableCellFormat hformatDataPos = new WritableCellFormat(hd);
	private WritableCellFormat hformatFecha = new jxl.write.WritableCellFormat(
			new jxl.write.DateFormat("dd/mm/yyyy"));
	private WritableCellFormat hformatDaAmarillo = new WritableCellFormat(hd);
	private WritableCellFormat formatFechaAmarillo = new jxl.write.WritableCellFormat(
			new jxl.write.DateFormat("dd/mm/yyyy h:mm"));
	private WritableCellFormat hformatHora = new jxl.write.WritableCellFormat(
			new jxl.write.DateFormat("hh:mm"));

	public byte[] crearExcelResumenFacturacion(List<ResumenContenedor> res, ResumenFacturacion fac, String nave,
			String viaje) throws ParseException {
		byte[] data = null;

		try {
			// CREAMOS EL LIBRO
			Date fecha = new Date();

			File fi = new File(String.valueOf(fecha.getTime()));
			WorkbookSettings conf = new WorkbookSettings();
			conf.setEncoding("ISO-8859-1");
			WritableWorkbook workbook = Workbook.createWorkbook(fi, conf);

			// CREAMOS LAS HOJAS DEL EXCEL
			WritableSheet sheet = workbook.createSheet("Resultado", 0);

			// DEFINIR FUENTE PARA EL CONTENIDO

			// DEFINIR CELDAS AUTOAJUSTABLES
			hformatFecha.setShrinkToFit(true);
			hformatData.setShrinkToFit(true);
			hformatData.setAlignment(Alignment.CENTRE);
			hformatDataPos.setShrinkToFit(true);
			hformatDataPos.setAlignment(Alignment.CENTRE);
			hformatColumnas.setShrinkToFit(true);
			formatFechaAmarillo.setShrinkToFit(true);
			formatFechaAmarillo.setAlignment(Alignment.CENTRE);
			hformatHora.setShrinkToFit(true);
			formatFechaAmarillo.setBackground(Colour.YELLOW);
			hformatHora.setBackground(Colour.YELLOW);
			hformatHora.setAlignment(Alignment.CENTRE);

			// DEFINIR COLORES A CELDAS
			ht.setColour(Colour.BLACK);
			hformatColumnas.setBackground(Colour.LIGHT_BLUE);
			hformatDaAmarillo.setBackground(Colour.YELLOW);

			// DEFINIR POSICION A CELDA Y BORDE
			hformatTitulos.setAlignment(Alignment.CENTRE);
			hformatViaje.setAlignment(Alignment.CENTRE);
			hformatColumnas.setAlignment(Alignment.CENTRE);
			hformatData.setAlignment(Alignment.CENTRE);
			hformatDataPos.setAlignment(Alignment.CENTRE);
			hformatColumnas.setBorder(Border.ALL, BorderLineStyle.MEDIUM, Colour.BLACK);
			hformatData.setBorder(Border.ALL, BorderLineStyle.DOTTED, Colour.BLACK);
			hformatDataPos.setBorder(Border.ALL, BorderLineStyle.DOTTED, Colour.BLACK);
			hformatDaAmarillo.setBorder(Border.ALL, BorderLineStyle.DOTTED, Colour.BLACK);

			// AÑADIR CONTENDIO POR CELDAS
			for (int numero = 0; numero <= 22; numero++) {
				CellView cell = sheet.getColumnView(numero);
				cell.setAutosize(true);
				sheet.setColumnView(numero, cell);
			}
			// PARA EL TITULO
			sheet.mergeCells(2, 2, 6, 2);
			sheet.addCell(
					new jxl.write.Label(2, 2, "RESUMEN FACTURACION CONTENEDOR " + fac.getContainer(), hformatTitulos));
			Calendar calendar = Calendar.getInstance();
			Calendar calendar2 = Calendar.getInstance();
			calendar2.set(Calendar.DAY_OF_MONTH, 1);
			calendar.add(Calendar.MONTH, -1);
			calendar.set(Calendar.DAY_OF_MONTH, 1);

			// TITULOS
			sheet.addCell(new jxl.write.Label(0, 8, "   ETIQUETA   ", hformatColumnas));
			sheet.addCell(new jxl.write.Label(1, 8, "   POSICION   ", hformatColumnas));
			sheet.addCell(new jxl.write.Label(2, 8, "        OWNER ID        ", hformatColumnas));
			sheet.addCell(new jxl.write.Label(3, 8, "   CODIGO  ", hformatColumnas));
			sheet.addCell(new jxl.write.Label(4, 8, "         PRODUCTO", hformatColumnas));
			sheet.addCell(new jxl.write.Label(5, 8, "     CAJAS / PALLET      ", hformatColumnas));
			sheet.addCell(new jxl.write.Label(6, 8, "       BOTELLAS / CAJA        ", hformatColumnas));
			sheet.addCell(new jxl.write.Label(7, 8, "   COSECHA   ", hformatColumnas));
			sheet.addCell(new jxl.write.Label(8, 8, "   FECHA RECEP   ", hformatColumnas));
			sheet.addCell(new jxl.write.Label(9, 8, "   FECHA CONSO  ", hformatColumnas));
			sheet.addCell(new jxl.write.Label(10, 8, "   DIAS   ", hformatColumnas));
			sheet.addCell(new jxl.write.Label(11, 8, "   SEMANAS   ", hformatColumnas));

			sheet.addCell(new jxl.write.Label(12, 8, "   ALMACENAMIENTO JFH   ", hformatColumnas));
			sheet.addCell(new jxl.write.Label(13, 8, "   ALMACENAMIENTO VIÑA   ", hformatColumnas));
			sheet.addCell(new jxl.write.Label(14, 8, "   RECEPCION   ", hformatColumnas));
			sheet.addCell(new jxl.write.Label(15, 8, "   AIRBAGS   ", hformatColumnas));
			sheet.addCell(new jxl.write.Label(16, 8, "   SLIPSHEET   ", hformatColumnas));
			sheet.addCell(new jxl.write.Label(17, 8, "   REPALETIZACION   ", hformatColumnas));
			sheet.addCell(new jxl.write.Label(18, 8, "   PORTEO   ", hformatColumnas));
			sheet.addCell(new jxl.write.Label(19, 8, "   TRANSPORTE   ", hformatColumnas));
			sheet.addCell(new jxl.write.Label(20, 8, "   GATE_OUT   ", hformatColumnas));

			sheet.addCell(new jxl.write.Label(21, 8, "   NAVE   ", hformatColumnas));
			sheet.addCell(new jxl.write.Label(22, 8, "   VIAJE   ", hformatColumnas));
			sheet.addCell(new jxl.write.Label(23, 8, "   CONTENEDOR   ", hformatColumnas));

			sheet.addCell(new jxl.write.Label(24, 8, "   SUBTOTAL   ", hformatColumnas));

			int i = 9;
			int k = 0;
			int posicionAux = 0;
			Boolean repetido = false;
			BigDecimal totalContenedor = BigDecimal.ZERO;
			BigDecimal cajasPallet = BigDecimal.ZERO;
			BigDecimal almaJfh = BigDecimal.ZERO;
			BigDecimal almaVina = BigDecimal.ZERO;
			BigDecimal recepcion = BigDecimal.ZERO;
			BigDecimal airbags = BigDecimal.ZERO;
			BigDecimal slipsheet = BigDecimal.ZERO;
			BigDecimal repale = BigDecimal.ZERO;
			BigDecimal porteo = BigDecimal.ZERO;
			BigDecimal trans = BigDecimal.ZERO;
			
			
			List<WritableCellFormat> writables = new ArrayList<WritableCellFormat>();
			
			
			WritableCellFormat hformatDataBlue = new WritableCellFormat(hd);
			hformatDataBlue.setShrinkToFit(true);
			hformatDataBlue.setAlignment(Alignment.CENTRE);
			hformatDataBlue.setAlignment(Alignment.CENTRE);
			hformatDataBlue.setBorder(Border.ALL, BorderLineStyle.DOTTED, Colour.BLACK);
			hformatDataBlue.setBackground(Colour.LIGHT_BLUE);
			writables.add(hformatDataBlue);
			
			WritableCellFormat hformatDataGreen = new WritableCellFormat(hd);
			hformatDataGreen.setShrinkToFit(true);
			hformatDataGreen.setAlignment(Alignment.CENTRE);
			hformatDataGreen.setAlignment(Alignment.CENTRE);
			hformatDataGreen.setBorder(Border.ALL, BorderLineStyle.DOTTED, Colour.BLACK);
			hformatDataGreen.setBackground(Colour.LIGHT_GREEN);
			writables.add(hformatDataGreen);
			
			WritableCellFormat hformatDataOrange = new WritableCellFormat(hd);
			hformatDataOrange.setShrinkToFit(true);
			hformatDataOrange.setAlignment(Alignment.CENTRE);
			hformatDataOrange.setAlignment(Alignment.CENTRE);
			hformatDataOrange.setBorder(Border.ALL, BorderLineStyle.DOTTED, Colour.BLACK);
			hformatDataOrange.setBackground(Colour.LIGHT_ORANGE);
			writables.add(hformatDataOrange);
			
			WritableCellFormat hformatDataTur = new WritableCellFormat(hd);
			hformatDataTur.setShrinkToFit(true);
			hformatDataTur.setAlignment(Alignment.CENTRE);
			hformatDataTur.setAlignment(Alignment.CENTRE);
			hformatDataTur.setBorder(Border.ALL, BorderLineStyle.DOTTED, Colour.BLACK);
			hformatDataTur.setBackground(Colour.LIGHT_TURQUOISE);
			writables.add(hformatDataTur);			
			
			WritableCellFormat hformatDataYel = new WritableCellFormat(hd);
			hformatDataYel.setShrinkToFit(true);
			hformatDataYel.setAlignment(Alignment.CENTRE);
			hformatDataYel.setAlignment(Alignment.CENTRE);
			hformatDataYel.setBorder(Border.ALL, BorderLineStyle.DOTTED, Colour.BLACK);
			hformatDataYel.setBackground(Colour.VERY_LIGHT_YELLOW);
			writables.add(hformatDataYel);

			for (ResumenContenedor dc : res) {
				
				logger.info("*********ETIQUETA--------------------> " + dc.getEtiqueta());
				
				
				
				sheet.addCell(new jxl.write.Label(0, i, dc.getEtiqueta(), hformatData));
				
				
				
				//sheet.addCell(new jxl.write.Number(1, i, dc.getPosicion().intValue(), hformatData));
				
				if(dc.getPosicion() != null && dc.getPosicion().intValue() == posicionAux) {
					
					
					if (repetido) {

						sheet.addCell(new jxl.write.Number(1, i, dc.getPosicion().intValue(), writables.get(k)));
						
						logger.info("*********K--------------------> " + k);

					} else {

						if (k > 0) {

							if (k > (writables.size()-1)) {
								k = 0;
							}
						}
						
						sheet.addCell(new jxl.write.Number(1, (i-1), posicionAux, writables.get(k)));
						sheet.addCell(new jxl.write.Number(1, i, dc.getPosicion().intValue(), writables.get(k)));
						
					}
					repetido = true;
				} else {
					
					if(repetido) {
						k++;
						repetido = false;
					}
					
									
					sheet.addCell(new jxl.write.Number(1, i, dc.getPosicion().intValue(), hformatData ));
				}
				
				
				sheet.addCell(new jxl.write.Label(2, i, dc.getOwner_id(), hformatData));
				sheet.addCell(new jxl.write.Label(3, i, dc.getCod_producto(), hformatData));
				sheet.addCell(new jxl.write.Label(4, i, dc.getProducto(), hformatData));

				sheet.addCell(new jxl.write.Number(5, i, dc.getCajas_pallet().intValue(),hformatData));
				sheet.addCell(new jxl.write.Label(6, i, dc.getBot_caja(), hformatData));
				sheet.addCell(new jxl.write.Label(7, i, dc.getCosecha(), hformatData));
				sheet.addCell(new jxl.write.DateTime(8, i, dc.getFecha_recepcion(), hformatFecha));
				sheet.addCell(new jxl.write.DateTime(9, i, dc.getFecha_conso(), hformatFecha));

				sheet.addCell(new jxl.write.Number(10, i, dc.getDias(), hformatData));
				sheet.addCell(new jxl.write.Number(11, i, dc.getSemanas(), hformatData));

				sheet.addCell(new jxl.write.Number(12, i, (dc.getAlmacenamientoJFH() == null) ? 0 : dc.getAlmacenamientoJFH().doubleValue(), hformatData));
				sheet.addCell(new jxl.write.Number(13, i, (dc.getAlmacenamientoVina() == null) ? 0 : dc.getAlmacenamientoVina().doubleValue(), hformatData));
				sheet.addCell(new jxl.write.Number(14, i, (dc.getRecepcion() == null) ? 0 : dc.getRecepcion().doubleValue(), hformatData));
				sheet.addCell(new jxl.write.Number(15, i, (dc.getAirbags() == null) ? 0 : dc.getAirbags().doubleValue(), hformatData));
				sheet.addCell(new jxl.write.Number(16, i, (dc.getSlipsheet() == null) ? 0 : dc.getSlipsheet().doubleValue(), hformatData));
				sheet.addCell(new jxl.write.Number(17, i, (dc.getRepaletizacion()==null) ? 0 : dc.getRepaletizacion().doubleValue(), hformatData));
				sheet.addCell(new jxl.write.Number(18, i, (dc.getPorteo() == null) ? 0 : dc.getPorteo().doubleValue(), hformatData));
				sheet.addCell(new jxl.write.Number(19, i, (dc.getTransporte() == null) ? 0 : dc.getTransporte().doubleValue(), hformatData));
				sheet.addCell(new jxl.write.Number(20, i, 0, hformatData)); // gate out
				sheet.addCell(new jxl.write.Label(21, i, nave, hformatData));
				sheet.addCell(new jxl.write.Label(22, i, viaje, hformatData));
				sheet.addCell(new jxl.write.Label(23, i, fac.getContainer(), hformatData));

				sheet.addCell(new jxl.write.Number(24, i, (this.sumaFila(dc)).doubleValue(), hformatData));
				
				totalContenedor = totalContenedor.add(this.sumaFila(dc));
				cajasPallet = cajasPallet.add((dc.getCajas_pallet() == null) ? BigDecimal.ZERO : dc.getCajas_pallet());
				almaJfh = almaJfh.add((dc.getAlmacenamientoJFH() == null) ? BigDecimal.ZERO : dc.getAlmacenamientoJFH());
				almaVina = almaVina.add((dc.getAlmacenamientoVina() == null) ? BigDecimal.ZERO : dc.getAlmacenamientoVina());
				recepcion = recepcion.add((dc.getRecepcion() == null) ? BigDecimal.ZERO : dc.getRecepcion());
				airbags = airbags.add((dc.getAirbags() == null) ? BigDecimal.ZERO : dc.getAirbags());
				slipsheet = slipsheet.add((dc.getSlipsheet() == null) ? BigDecimal.ZERO : dc.getSlipsheet());
				repale = repale.add((dc.getRepaletizacion() == null) ? BigDecimal.ZERO : dc.getRepaletizacion());
				porteo = porteo.add((dc.getPorteo() == null) ? BigDecimal.ZERO : dc.getPorteo());
				trans = trans.add((dc.getTransporte() == null) ? BigDecimal.ZERO : dc.getTransporte());

				i++;
				posicionAux = dc.getPosicion().intValue();
			}
			
			i++;
			i++;
			sheet.addCell(new jxl.write.Label(0, i, "TOTAL", hformatData));
			sheet.addCell(new jxl.write.Label(1, i, "", hformatData));
			sheet.addCell(new jxl.write.Label(2, i, "", hformatData));
			sheet.addCell(new jxl.write.Label(3, i, "", hformatData));
			sheet.addCell(new jxl.write.Label(4, i, "", hformatData));

			sheet.addCell(new jxl.write.Number(5, i, cajasPallet.intValue() , hformatData));
			sheet.addCell(new jxl.write.Label(6, i, "", hformatData));
			sheet.addCell(new jxl.write.Label(7, i, "", hformatData));
			sheet.addCell(new jxl.write.Label(8, i, "", hformatFecha));
			sheet.addCell(new jxl.write.Label(9, i, "", hformatFecha));

			sheet.addCell(new jxl.write.Label(10, i, "", hformatData));
			sheet.addCell(new jxl.write.Label(11, i, "", hformatData));

			sheet.addCell(new jxl.write.Number(12, i, almaJfh.doubleValue(), hformatData));
			sheet.addCell(new jxl.write.Number(13, i, almaVina.doubleValue(), hformatData));
			sheet.addCell(new jxl.write.Number(14, i, recepcion.doubleValue(), hformatData));
			sheet.addCell(new jxl.write.Number(15, i, airbags.doubleValue(), hformatData));
			sheet.addCell(new jxl.write.Number(16, i, slipsheet.doubleValue(), hformatData));
			sheet.addCell(new jxl.write.Number(17, i, repale.doubleValue() , hformatData));
			sheet.addCell(new jxl.write.Number(18, i, porteo.doubleValue() , hformatData));
			sheet.addCell(new jxl.write.Number(19, i, trans.doubleValue() , hformatData));
			sheet.addCell(new jxl.write.Number(20, i, 0, hformatData)); // gate out
			sheet.addCell(new jxl.write.Label(21, i, "", hformatData));
			sheet.addCell(new jxl.write.Label(22, i, "", hformatData));
			sheet.addCell(new jxl.write.Label(23, i, "", hformatData));


			sheet.addCell(new jxl.write.Number(24, i, totalContenedor.doubleValue(), hformatData));

			workbook.write();
			workbook.close();
			Path path = Paths.get(fi.getPath());
			data = Files.readAllBytes(path);

			fi.delete();
		} catch (IOException ex) {

		} catch (WriteException ex) {

		}
		return data;
	}
	
	
	private BigDecimal sumaFila(ResumenContenedor dc) {
		
		BigDecimal suma = BigDecimal.ZERO;
		
		suma = suma.add((dc.getAlmacenamientoJFH() == null) ? BigDecimal.ZERO : dc.getAlmacenamientoJFH());
		suma = suma.add((dc.getAlmacenamientoVina() == null) ? BigDecimal.ZERO : dc.getAlmacenamientoVina());
		suma = suma.add((dc.getRecepcion() == null) ? BigDecimal.ZERO : dc.getRecepcion());
		suma = suma.add((dc.getAirbags() == null) ? BigDecimal.ZERO : dc.getAirbags());
		suma = suma.add((dc.getSlipsheet() == null) ? BigDecimal.ZERO : dc.getSlipsheet());
		suma = suma.add((dc.getRepaletizacion() == null) ? BigDecimal.ZERO : dc.getRepaletizacion());
		suma = suma.add((dc.getTransporte() == null) ? BigDecimal.ZERO : dc.getTransporte());
		suma = suma.add((dc.getPorteo() == null) ? BigDecimal.ZERO : dc.getPorteo());
		
		return suma;
	}

	public byte[] crearExcelResumenFacturacionNaveViaje(List<ResumenContenedor> res, List<ResumenNaveViaje>  resNav, String nave, String viaje) throws ParseException {
		byte[] data = null;

		try {
			// CREAMOS EL LIBRO
			Date fecha = new Date();

			File fi = new File(String.valueOf(fecha.getTime()));
			WorkbookSettings conf = new WorkbookSettings();
			conf.setEncoding("ISO-8859-1");
			WritableWorkbook workbook = Workbook.createWorkbook(fi, conf);

			// CREAMOS LAS HOJAS DEL EXCEL
			WritableSheet sheet = workbook.createSheet("Resultado", 0);

			// DEFINIR FUENTE PARA EL CONTENIDO

			// DEFINIR CELDAS AUTOAJUSTABLES
			hformatFecha.setShrinkToFit(true);
			hformatData.setShrinkToFit(true);
			hformatData.setAlignment(Alignment.CENTRE);
			hformatColumnas.setShrinkToFit(true);
			formatFechaAmarillo.setShrinkToFit(true);
			formatFechaAmarillo.setAlignment(Alignment.CENTRE);
			hformatHora.setShrinkToFit(true);
			formatFechaAmarillo.setBackground(Colour.YELLOW);
			hformatHora.setBackground(Colour.YELLOW);
			hformatHora.setAlignment(Alignment.CENTRE);

			// DEFINIR COLORES A CELDAS
			ht.setColour(Colour.BLACK);
			hformatColumnas.setBackground(Colour.LIGHT_BLUE);
			hformatDaAmarillo.setBackground(Colour.YELLOW);

			// DEFINIR POSICION A CELDA Y BORDE
			hformatTitulos.setAlignment(Alignment.CENTRE);
			hformatViaje.setAlignment(Alignment.CENTRE);
			hformatColumnas.setAlignment(Alignment.CENTRE);
			hformatData.setAlignment(Alignment.CENTRE);
			hformatColumnas.setBorder(Border.ALL, BorderLineStyle.MEDIUM, Colour.BLACK);
			hformatData.setBorder(Border.ALL, BorderLineStyle.DOTTED, Colour.BLACK);
			hformatDaAmarillo.setBorder(Border.ALL, BorderLineStyle.DOTTED, Colour.BLACK);

			// AÑADIR CONTENDIO POR CELDAS
			for (int numero = 0; numero <= 22; numero++) {
				CellView cell = sheet.getColumnView(numero);
				cell.setAutosize(true);
				sheet.setColumnView(numero, cell);
			}
			// PARA EL TITULO
			sheet.mergeCells(2, 2, 6, 2);
			sheet.addCell(
					new jxl.write.Label(2, 2, "RESUMEN FACTURACION NAVE " + nave + " / VIAJE " + viaje , hformatTitulos));
			Calendar calendar = Calendar.getInstance();
			Calendar calendar2 = Calendar.getInstance();
			calendar2.set(Calendar.DAY_OF_MONTH, 1);
			calendar.add(Calendar.MONTH, -1);
			calendar.set(Calendar.DAY_OF_MONTH, 1);

			// TITULOS
			sheet.addCell(new jxl.write.Label(0, 8, "   NAVE   ", hformatColumnas));
			sheet.addCell(new jxl.write.Label(1, 8, "        VIAJE        ", hformatColumnas));
			sheet.addCell(new jxl.write.Label(2, 8, "   CONTENEDOR  ", hformatColumnas));
			
			sheet.addCell(new jxl.write.Label(3, 8, "         ALMACENAMIENTO", hformatColumnas));
			sheet.addCell(new jxl.write.Label(4, 8, "     RECEPCION      ", hformatColumnas));
			sheet.addCell(new jxl.write.Label(5, 8, "       AIRBAGS        ", hformatColumnas));
			sheet.addCell(new jxl.write.Label(6, 8, "   SLIPSHEET   ", hformatColumnas));
			sheet.addCell(new jxl.write.Label(7, 8, "   VALOR REPALETIZACION   ", hformatColumnas));
			sheet.addCell(new jxl.write.Label(8, 8, "   PORTEO  ", hformatColumnas));
			sheet.addCell(new jxl.write.Label(9, 8, "   TRANSPORTE   ", hformatColumnas));
			sheet.addCell(new jxl.write.Label(10, 8, "   GATE_OUT   ", hformatColumnas));
			sheet.addCell(new jxl.write.Label(11, 8, "   TOTAL   ", hformatColumnas));

			int i = 9;
			BigDecimal total = BigDecimal.ZERO;
			BigDecimal totalNave = BigDecimal.ZERO;

			for (ResumenNaveViaje dc : resNav) {
				
				
				
				total = total.add((dc.getAirbags() == null) ? BigDecimal.ZERO : dc.getAirbags());
				total = total.add((dc.getAlmacenamiento() == null) ? BigDecimal.ZERO : dc.getAlmacenamiento());
				total = total.add((dc.getGeteout()== null) ? BigDecimal.ZERO : dc.getGeteout());
				total = total.add((dc.getPorteo() == null) ? BigDecimal.ZERO : dc.getPorteo());
				total = total.add((dc.getRecepcion() == null) ? BigDecimal.ZERO : dc.getRecepcion());
				total = total.add((dc.getRepaletizacion() == null) ? BigDecimal.ZERO : dc.getRepaletizacion());
				total = total.add((dc.getSlipsheet() == null) ? BigDecimal.ZERO : dc.getSlipsheet());
				total = total.add((dc.getTransporte() == null) ? BigDecimal.ZERO : dc.getTransporte());
				
				sheet.addCell(new jxl.write.Label(0, i, nave, hformatData));
				sheet.addCell(new jxl.write.Label(1, i, viaje, hformatData));
				sheet.addCell(new jxl.write.Label(2, i, dc.getContainer(), hformatData));
				
				sheet.addCell(new jxl.write.Number(3, i, dc.getAlmacenamiento().doubleValue(), hformatData));
				sheet.addCell(new jxl.write.Number(4, i, dc.getRecepcion().doubleValue(), hformatData));
				sheet.addCell(new jxl.write.Number(5, i, dc.getAirbags().doubleValue(), hformatData));
				sheet.addCell(new jxl.write.Number(6, i, dc.getSlipsheet().doubleValue(), hformatData));
				sheet.addCell(new jxl.write.Number(7, i, dc.getRepaletizacion().doubleValue(), hformatData));
				sheet.addCell(new jxl.write.Number(8, i, dc.getPorteo().doubleValue(), hformatData));
				sheet.addCell(new jxl.write.Number(9, i, dc.getTransporte().doubleValue(), hformatData));
				sheet.addCell(new jxl.write.Number(10, i, dc.getGeteout().doubleValue(), hformatData));
				
				sheet.addCell(new jxl.write.Number(11, i, total.doubleValue(), hformatData));
				
				totalNave = totalNave.add(total);
				
				i++;

			}
			
			sheet.addCell(new jxl.write.Number(11, i, totalNave.doubleValue(), hformatData));

			workbook.write();
			workbook.close();
			Path path = Paths.get(fi.getPath());
			data = Files.readAllBytes(path);

			fi.delete();
		} catch (IOException ex) {

		} catch (WriteException ex) {

		}
		return data;
	}

	public byte[] crearExcelExistencia(List<Existencia> listaCargas) {

		byte[] data = null;

		try {
			// CREAMOS EL LIBRO
			Date fecha = new Date();

			File fi = new File(String.valueOf(fecha.getTime()));
			WorkbookSettings conf = new WorkbookSettings();
			conf.setEncoding("ISO-8859-1");
			WritableWorkbook workbook = Workbook.createWorkbook(fi, conf);

			// CREAMOS LAS HOJAS DEL EXCEL
			WritableSheet sheet = workbook.createSheet("Resultado", 0);

			// DEFINIR FUENTE PARA EL CONTENIDO

			// DEFINIR CELDAS AUTOAJUSTABLES
			hformatFecha.setShrinkToFit(true);
			hformatData.setShrinkToFit(true);
			hformatData.setAlignment(Alignment.CENTRE);
			hformatColumnas.setShrinkToFit(true);
			formatFechaAmarillo.setShrinkToFit(true);
			formatFechaAmarillo.setAlignment(Alignment.CENTRE);
			hformatHora.setShrinkToFit(true);
			formatFechaAmarillo.setBackground(Colour.YELLOW);
			hformatHora.setBackground(Colour.YELLOW);
			hformatHora.setAlignment(Alignment.CENTRE);

			// DEFINIR COLORES A CELDAS
			ht.setColour(Colour.BLACK);
			hformatColumnas.setBackground(Colour.LIGHT_GREEN);
			hformatDaAmarillo.setBackground(Colour.YELLOW);

			// DEFINIR POSICION A CELDA Y BORDE
			hformatTitulos.setAlignment(Alignment.CENTRE);
			hformatViaje.setAlignment(Alignment.CENTRE);
			hformatColumnas.setAlignment(Alignment.CENTRE);
			hformatData.setAlignment(Alignment.CENTRE);
			hformatColumnas.setBorder(Border.ALL, BorderLineStyle.MEDIUM, Colour.BLACK);
			hformatData.setBorder(Border.ALL, BorderLineStyle.DOTTED, Colour.BLACK);
			hformatDaAmarillo.setBorder(Border.ALL, BorderLineStyle.DOTTED, Colour.BLACK);

			// AÑADIR CONTENDIO POR CELDAS
			for (int numero = 0; numero <= 22; numero++) {
				CellView cell = sheet.getColumnView(numero);
				cell.setAutosize(true);
				sheet.setColumnView(numero, cell);
			}
			// PARA EL TITULO
			sheet.mergeCells(2, 2, 6, 2);
			sheet.addCell(
					new jxl.write.Label(2, 2, "REPORTE EXISTENCIA CARGA ", hformatTitulos));
			Calendar calendar = Calendar.getInstance();
			Calendar calendar2 = Calendar.getInstance();
			calendar2.set(Calendar.DAY_OF_MONTH, 1);
			calendar.add(Calendar.MONTH, -1);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			// sheet.addCell(new jxl.write.Label(3, 6, "FECHA DESDE ", hformatColumnas));
			// sheet.addCell(new jxl.write.Label(3, 7, "FECHA HASTA ", hformatColumnas));


			// TITULOS
			sheet.addCell(new jxl.write.Label(0, 8, "   ETIQUETA   ", hformatColumnas));
			//sheet.addCell(new jxl.write.Label(1, 8, "   ETIQUETA ANTIGUA   ", hformatColumnas));
			sheet.addCell(new jxl.write.Label(1, 8, "        VIÑA        ", hformatColumnas));
			sheet.addCell(new jxl.write.Label(2, 8, "   LOTE   ", hformatColumnas));
			sheet.addCell(new jxl.write.Label(3, 8, "   CODIGO  ", hformatColumnas));
			sheet.addCell(new jxl.write.Label(4, 8, "         PRODUCTO", hformatColumnas));
			sheet.addCell(new jxl.write.Label(5, 8, "     COSECHA      ", hformatColumnas));
			sheet.addCell(new jxl.write.Label(6, 8, "       BOTELLAS / CAJA        ", hformatColumnas));
			sheet.addCell(new jxl.write.Label(7, 8, "   CAJAS / PALLET   ", hformatColumnas));
			sheet.addCell(new jxl.write.Label(8, 8, "   GUÍA CLIENTE   ", hformatColumnas));
			sheet.addCell(new jxl.write.Label(9, 8, "   FECHA RECEPCION  ", hformatColumnas)); //--
			sheet.addCell(new jxl.write.Label(10, 8, "   FERT   ", hformatColumnas));
			sheet.addCell(new jxl.write.Label(11, 8, "   P.O.   ", hformatColumnas));
			sheet.addCell(new jxl.write.Label(12, 8, "   UBICACIÓN   ", hformatColumnas));
			sheet.addCell(new jxl.write.Label(13, 8, "   CONTENEDOR / MOVIMIENTO   ", hformatColumnas));
			sheet.addCell(new jxl.write.Label(14, 8, "   DESPACHO   ", hformatColumnas));


			int i = 9;

			for (Existencia dc : listaCargas) {
				sheet.addCell(new jxl.write.Label(0, i, dc.getEtiqueta(), hformatData));
				//sheet.addCell(new jxl.write.Label(1, i, dc.getEtiquetaAnterior(), hformatData));
				sheet.addCell(new jxl.write.Label(1, i, dc.getVina(), hformatData));
				sheet.addCell(new jxl.write.Label(2, i, dc.getLote(), hformatData));
				sheet.addCell(new jxl.write.Label(3, i, dc.getCodigoProducto(), hformatData));
				sheet.addCell(new jxl.write.Label(4, i, dc.getProducto(), hformatData));
				sheet.addCell(new jxl.write.Label(5, i, dc.getCosecha(), hformatData));
				sheet.addCell(new jxl.write.Label(6, i, dc.getBotellasPorCaja(), hformatData));
				sheet.addCell(new jxl.write.Label(7, i, dc.getCajasPallet(), hformatData));
				sheet.addCell(new jxl.write.Label(8, i, dc.getGuiaCliente(), hformatFecha));
				sheet.addCell(new jxl.write.DateTime(9, i, dc.getFechaRecepcion(), hformatFecha));
				sheet.addCell(new jxl.write.Label(10, i, dc.getCodigo_fert(), hformatData));
				sheet.addCell(new jxl.write.Label(11, i, dc.getPoId(), hformatData));
				sheet.addCell(new jxl.write.Label(12, i, dc.getUbicacion(), hformatData));
				sheet.addCell(new jxl.write.Label(13, i, dc.getContenedor(), hformatData));
				sheet.addCell(new jxl.write.Label(14, i, (dc.getFechaDespacho() != null) ? new SimpleDateFormat("dd-MM-yyyy").format(dc.getFechaDespacho()) : "", hformatData));
				i++;

			}

			workbook.write();
			workbook.close();
			Path path = Paths.get(fi.getPath());
			data = Files.readAllBytes(path);

			fi.delete();
		} catch (IOException ex) {

		} catch (WriteException ex) {

		}
		return data;
	
	}
}

