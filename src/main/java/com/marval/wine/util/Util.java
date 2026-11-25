package com.marval.wine.util;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.model.SelectItem;
import javax.imageio.ImageIO;

import org.apache.poi.ss.usermodel.DateUtil;
import org.omnifaces.util.Messages;

/**
 * @author Marval.
 */
@ApplicationScoped
public class Util implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void addDetailMessage(String message) {
		addDetailMessage(message, null);
	}

	public static void addDetailMessage(String message, FacesMessage.Severity severity) {

		FacesMessage facesMessage = Messages.create("").detail(message).get();
		if (severity != null && severity != FacesMessage.SEVERITY_INFO) {
			facesMessage.setSeverity(severity);
		}
		Messages.add(null, facesMessage);
	}

	public Date stringADate(String fecha) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		return formatter.parse(fecha);
	}

	public static String minutoshora(Integer minutos) {
		Integer hora = (minutos >= 0) ? (minutos - (minutos % 60)) / 60 : 0;
		Integer min = minutos % 60;
		return hora + ":" + ((min < 10) ? "0" + min : min);
	}

	public static ByteArrayOutputStream resizeImagen(InputStream input, int width, int height) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			BufferedImage src = ImageIO.read(input);
			BufferedImage dest = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = dest.createGraphics();
			AffineTransform at = AffineTransform.getScaleInstance((double) width / src.getWidth(),
					(double) height / src.getHeight());
			g.drawRenderedImage(src, at);
			ImageIO.write(dest, "jpg", os);
			return os;
		} catch (Exception e) {
			os = new ByteArrayOutputStream();
			System.out.println(e);
			return os;
		}

	}

	public static int getRandomInt(int min, int max) {
		return (int) Math.floor(Math.random() * (max - min)) + min;
	}

	public List<SelectItem> listaStringASelect(List<String> lis) {
		List<SelectItem> lista = new ArrayList<SelectItem>();
		for (String t : lis) {
			SelectItem select = new SelectItem();
			select.setValue(t);
			select.setLabel(t);
			lista.add(select);
		}
		return lista;
	}

	public static String rellenarEspacios(int cantidad, String texto) {
		
		

		if (texto != null) {
			
			if(cantidad < texto.length()) {
				texto = texto.substring(0, cantidad);
				return texto +  "*";
			}
			
			int cantidadfinal = cantidad - texto.length();

			for (int i = 0; i < cantidadfinal; i++) {
				texto = texto + " ";

			}

			return texto + "*";
		} else {
			texto = "";
			int cantidadfinal = cantidad;

			for (int i = 0; i < cantidadfinal; i++) {
				texto = texto + " ";

			}

			return texto + "*";
		}
		
		

	}
	
	public static String rellenarEspaciosIzq(int cantidad, String texto) {
		
		

		if (texto != null) {
			
			if(cantidad < texto.length()) {
				texto = texto.substring(0, cantidad);
				return texto +  "*";
			}
			
			int cantidadfinal = cantidad - texto.length();

			for (int i = 0; i < cantidadfinal; i++) {
				texto = " " + texto;

			}

			return texto + "*";
		} else {
			texto = "";
			int cantidadfinal = cantidad;

			for (int i = 0; i < cantidadfinal; i++) {
				texto = " " + texto;

			}

			return texto + "*";
		}
		
		

	}

	public static String rellenarConCeros(int cantidad, String texto) {

		if (texto != null) {
			int cantidadfinal = cantidad - texto.length();

			for (int i = 0; i < cantidadfinal; i++) {
				texto = texto + "0";

			}

			return texto;
		} else {
			int cantidadfinal = cantidad;

			for (int i = 0; i < cantidadfinal; i++) {
				texto = texto + "0";

			}

			return texto;
		}

	}
	
	public static String alerta(String titulo, String mensaje, String tipo) {
		String script = "Swal.fire('"+titulo+"', '" + mensaje +"','"+tipo+"');";
		return script;
	}
	
	public Date convertirFecha(String celda) throws ParseException {
		celda = celda.replaceAll("-", "/");
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
		String celdaFinal;
		Date fecha;
		if (celda.equals("null")) {
			fecha = null;
		} else {
			if (celda.contains(".")) {
				double original = Double.parseDouble(celda);
				Date fechaAlternativa = DateUtil.getJavaDate(original);
				celdaFinal = formatter.format(fechaAlternativa);
				fecha = formatter.parse(celdaFinal);
			} else {
				celdaFinal = celda;
				fecha = formatter.parse(celdaFinal);
			}

		}

		return fecha;
	}

}