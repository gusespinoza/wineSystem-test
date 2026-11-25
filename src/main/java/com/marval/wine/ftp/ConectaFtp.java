package com.marval.wine.ftp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import com.marval.wine.util.Constantes;

@PropertySource(Constantes.RUTA_PROPERTIES)
public class ConectaFtp {

	@Value("${db.ftp.server}")
	String SERVER_WINE;
	@Value("${db.ftp.user}")
	String USER_WINE;
	@Value("${db.ftp.pass}")
	String PASS_WINE;
	
	@Value("${db.ftp.holanda.server}")
	String SERVER_WINE_HOLANDA;
	@Value("${db.ftp.holanda.user}")
	String USER_WINE_HOLANDA;
	@Value("${db.ftp.holanda.pass}")
	String PASS_WINE_HOLANDA;
	
	@Value("${db.ftp.in}")
	String CARPETA_IN;
	@Value("${db.ftp.out}")
	String CARPETA_OUT;
	@Value("${db.ftp.backup}")
	String CARPETA_BACKUP;
	
	
	
	private String server = SERVER_WINE;
	private String user = USER_WINE;
	private String pass = PASS_WINE;
	private String carpeta ;
	private String img;


	public ConectaFtp() {

	}

	public ConectaFtp(String server, String user, String pass, String carpeta, String img) {
		super();
		this.server = server;
		this.user = user;
		this.pass = pass;
		this.carpeta = carpeta;
		this.img = img;
	}

	
	public void borrar(String archivo, String ruta) throws IOException {
		FTPClient client = new FTPClient();
		client.connect(SERVER_WINE_HOLANDA);
		client.login(USER_WINE_HOLANDA, PASS_WINE_HOLANDA);
		client.changeWorkingDirectory(ruta);
		client.dele(archivo);
		client.logout();
		client.disconnect();
	}

	
	public void subeArchivo(InputStream file, String fileName, String ruta) throws SocketException, IOException {
		FTPClient client = new FTPClient();
		client.connect(server);
		client.login(user, pass);
		client.changeWorkingDirectory(ruta);
		client.setFileType(FTP.BINARY_FILE_TYPE, FTP.BINARY_FILE_TYPE);
		client.setFileTransferMode(FTP.BINARY_FILE_TYPE);
		client.enterLocalPassiveMode();
		if (!listaArchivosPorDirectorio(ruta).contains(fileName)) {
			client.storeFile(fileName, file);
		}
		client.logout();
		client.disconnect();
	}
	
	public void subeBackUp(InputStream file, String fileName) throws SocketException, IOException {
		String rutaNueva = "";
		if(fileName.contains("POSP") || fileName.contains("posp")) {
			rutaNueva =    new SimpleDateFormat("dd-MM-yyyy").format(new Date()) + "/PO";
		}else {
			rutaNueva =    new SimpleDateFormat("dd-MM-yyyy").format(new Date()) + "/PT";
		}
		FTPClient client = new FTPClient();
		client.connect(this.server);
		client.login(USER_WINE_HOLANDA, PASS_WINE_HOLANDA);
		client.changeWorkingDirectory(CARPETA_BACKUP);
		client.setFileType(FTP.BINARY_FILE_TYPE, FTP.BINARY_FILE_TYPE);
		client.setFileTransferMode(FTP.BINARY_FILE_TYPE);
		client.enterLocalPassiveMode();
		client.makeDirectory(rutaNueva);
		client.changeWorkingDirectory(CARPETA_BACKUP+rutaNueva);
		if (!listaArchivosPorDirectorio(CARPETA_BACKUP+rutaNueva).contains(fileName)) {
			client.storeFile(fileName, file);
		}
		client.logout();
		client.disconnect();
	}
	
	public void subeArchivoOut(InputStream file, String fileName) throws SocketException, IOException {
		FTPClient client = new FTPClient();
		
		System.out.println("CARPETA_OUT + ruta " + CARPETA_OUT );

		client.connect(SERVER_WINE_HOLANDA);
		client.login(USER_WINE_HOLANDA, PASS_WINE_HOLANDA);
		client.changeWorkingDirectory(CARPETA_OUT);
		client.setFileType(FTP.BINARY_FILE_TYPE, FTP.BINARY_FILE_TYPE);
		client.setFileTransferMode(FTP.BINARY_FILE_TYPE);
		client.enterLocalPassiveMode();
		if (!listaArchivosPorDirectorio(CARPETA_OUT).contains(fileName)) {
			client.storeFile(fileName, file);
		}
		client.logout();
		client.disconnect();
	}


	public List<String> listaArchivosPorDirectorio(String ruta) throws SocketException, IOException {
		FTPClient client = new FTPClient();
		client.connect(server);
		client.login(user, pass);
		client.changeWorkingDirectory(ruta);
		client.setFileType(FTP.BINARY_FILE_TYPE, FTP.BINARY_FILE_TYPE);
		client.setFileTransferMode(FTP.BINARY_FILE_TYPE);
		client.enterLocalPassiveMode();
		List<String> lista = new ArrayList<String>();
		for (String fi : client.listNames()) {
			lista.add(fi);
		}
		client.logout();
		client.disconnect();
		return lista;
	}

	public List<String> listaArchivosPorDirectorioHolanda(String ruta) throws SocketException, IOException {

		try {

			FTPClient client = new FTPClient();
			client.connect(SERVER_WINE_HOLANDA);
			client.login(USER_WINE_HOLANDA, PASS_WINE_HOLANDA);
			client.changeWorkingDirectory(ruta);
			client.setFileType(FTP.BINARY_FILE_TYPE, FTP.BINARY_FILE_TYPE);
			client.setFileTransferMode(FTP.BINARY_FILE_TYPE);
			client.enterLocalPassiveMode();
			List<String> lista = new ArrayList<String>();
			for (String fi : client.listNames()) {
				lista.add(fi);
			}
			client.logout();
			client.disconnect();
			return lista;
		} catch (Exception e) {
			System.out.println("SIN CONEXION DESDE HOLANDA");
			return null;
		}
	}


	public void creaDirectorio(String ruta, String directorio) throws SocketException, IOException {
		FTPClient client = new FTPClient();
		client.connect(server);
		client.login(user, pass);
		client.changeWorkingDirectory(ruta);
		client.setFileType(FTP.BINARY_FILE_TYPE, FTP.BINARY_FILE_TYPE);
		client.setFileTransferMode(FTP.BINARY_FILE_TYPE);
		client.enterLocalPassiveMode();
		client.makeDirectory(directorio);
	}

	public ByteArrayInputStream descargaFtp(String ruta) throws IOException {
		ruta = ruta.replaceAll("", "");
		FTPClient client = new FTPClient();
		client.connect(server);
		client.login(user, pass);
		client.changeWorkingDirectory(ruta);
		client.setFileType(FTP.BINARY_FILE_TYPE, FTP.BINARY_FILE_TYPE);
		client.setFileTransferMode(FTP.BINARY_FILE_TYPE);
		client.enterLocalPassiveMode();

		return new ByteArrayInputStream(IOUtils.toByteArray(client.retrieveFileStream(ruta)));

	}

	public InputStream descargaFtpInput(String ruta) throws IOException {
		ruta = ruta.replaceAll("ftp://tarja:admin@192.168.1.25", "");
		FTPClient client = new FTPClient();
		client.connect(server);
		client.login(user, pass);
		client.changeWorkingDirectory(ruta);
		client.setFileType(FTP.BINARY_FILE_TYPE, FTP.BINARY_FILE_TYPE);
		client.setFileTransferMode(FTP.BINARY_FILE_TYPE);
		client.enterLocalPassiveMode();

		return client.retrieveFileStream(ruta);

	}

	public InputStream descargaArchivo(String ruta) throws IOException {
		FTPClient client = new FTPClient();
		client.connect(server);
		client.login(user, pass);
		client.changeWorkingDirectory(ruta);
		client.setFileType(FTP.BINARY_FILE_TYPE, FTP.BINARY_FILE_TYPE);
		client.setFileTransferMode(FTP.BINARY_FILE_TYPE);
		client.enterLocalPassiveMode();

		return client.retrieveFileStream(ruta);

	}
	public InputStream descargaArchivoHolanda(String ruta) throws IOException {
		FTPClient client = new FTPClient();
		client.connect(SERVER_WINE_HOLANDA);
		client.login(USER_WINE_HOLANDA, PASS_WINE_HOLANDA);
		client.changeWorkingDirectory(ruta);
		client.setFileType(FTP.BINARY_FILE_TYPE, FTP.BINARY_FILE_TYPE);
		client.setFileTransferMode(FTP.BINARY_FILE_TYPE);
		client.enterLocalPassiveMode();
		
		return client.retrieveFileStream(ruta);
		
	}


	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getCarpeta() {
		return carpeta;
	}

	public void setCarpeta(String carpeta) {
		this.carpeta = carpeta;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

}
