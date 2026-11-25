package com.marval.wine.ftp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.integration.sftp.session.SftpSession;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.marval.wine.util.Constantes;

@PropertySource(Constantes.RUTA_PROPERTIES)
public class ConectaSFTP {

	private static final Logger logger = LoggerFactory.getLogger(ConectaSFTP.class);

	@Value("${db.sftp.holanda.server}")
	String SERVER_WINE_SFTP;
	@Value("${db.sftp.holanda.user}")
	String USER_WINE_SFTP;
	@Value("${db.sftp.holanda.pass}")
	String PASS_WINE_SFTP;

	@Value("${db.sftp.in}")
	String CARPETA_IN;
	@Value("${db.sftp.out}")
	String CARPETA_OUT;
	@Value("${db.sftp.backup}")
	String CARPETA_BACKUP;

	private String server = SERVER_WINE_SFTP;
	private String user = USER_WINE_SFTP;
	private String pass = PASS_WINE_SFTP;
	
	public ConectaSFTP() {

	}

	public ConectaSFTP(String server, String user, String pass, String carpeta, String img) {
		super();
		this.server = server;
		this.user = user;
		this.pass = pass;
	}

	public InputStream descargaArchivoHolanda(String ruta) throws IOException {
	    DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory(true);

	    factory.setHost(SERVER_WINE_SFTP);
	    factory.setPort(4222);
	    factory.setUser(USER_WINE_SFTP);
	    factory.setPassword(PASS_WINE_SFTP);
	    factory.setAllowUnknownKeys(true);
	    factory.setTimeout(60000);

	    logger.info("[conectaSFTP] Iniciando descarga del archivo: " + ruta);

	    try (SftpSession session = factory.getSession()) {
	        logger.info("[conectaSFTP] Sesión SFTP establecida correctamente.");
	        ChannelSftp channelSftp = session.getClientInstance();
	        logger.info("[conectaSFTP] Instancia de ChannelSftp obtenida.");

	        SftpATTRS attrs = channelSftp.lstat(ruta);
	        if (attrs.isDir()) {
	            logger.info("[conectaSFTP] La ruta especificada es un directorio, no se puede descargar como archivo: " + ruta);
	            return null;
	        }

	        try (InputStream is = channelSftp.get(ruta);
	             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

	            byte[] buffer = new byte[4096];
	            int bytesRead;
	            while ((bytesRead = is.read(buffer)) != -1) {
	                baos.write(buffer, 0, bytesRead);
	            }

	            logger.info("[conectaSFTP] Archivo descargado correctamente: " + ruta);
	            return new ByteArrayInputStream(baos.toByteArray());
	        }

	    } catch (Exception e) {
	        logger.error("[conectaSFTP] Error descargando archivo por SFTP: " + e.getMessage(), e);
	        throw new IOException("Error descargando archivo por SFTP: " + e.getMessage(), e);
	    }
	}


	public List<String> listaArchivosPorDirectorioHolanda(String ruta) throws IOException {
	    DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory(true);

	    factory.setHost(SERVER_WINE_SFTP);
	    factory.setPort(4222);
	    factory.setUser(USER_WINE_SFTP);
	    factory.setPassword(PASS_WINE_SFTP);
	    factory.setAllowUnknownKeys(true);
	    factory.setTimeout(60000);

	    try (SftpSession session = factory.getSession()) { // Auto-close con try-with-resources
	        ChannelSftp channelSftp = session.getClientInstance();

	        Vector<ChannelSftp.LsEntry> archivos = channelSftp.ls(ruta);

	        List<String> lista = new ArrayList<>();
	        for (ChannelSftp.LsEntry entry : archivos) {
	            String nombre = entry.getFilename();
	            // Filtramos ".", ".." y solo incluimos archivos que terminen en .txt
	            if (!nombre.equals(".") && !nombre.equals("..") && nombre.toLowerCase().endsWith(".txt")) {
	                lista.add(nombre);
	            }
	        }

	        return lista;
	    } catch (Exception e) {
	        logger.error("SIN CONEXION DESDE HOLANDA: " + e.getMessage(), e);
	        return Collections.emptyList(); // Mejor devolver lista vacía en lugar de null
	    }
	}


	public void borrar(String archivo, String ruta) throws IOException {
	    DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory(true);

	    factory.setHost(SERVER_WINE_SFTP);
	    factory.setPort(4222);
	    factory.setUser(USER_WINE_SFTP);
	    factory.setPassword(PASS_WINE_SFTP);
	    factory.setAllowUnknownKeys(true); // equivale a StrictHostKeyChecking=no
	    factory.setTimeout(60000);

	    try (SftpSession session = factory.getSession()) {
	        ChannelSftp channelSftp = session.getClientInstance();
	        channelSftp.cd(ruta);
	        channelSftp.rm(archivo);
	        logger.info("[SFTP] Archivo '{}' eliminado correctamente en la ruta '{}'", archivo, ruta);

	    } catch (SftpException e) {
	        logger.error("[SFTP] Error al eliminar archivo: {}", e.getMessage(), e);
	        throw new IOException("Error al borrar archivo en SFTP", e);
	    } catch (Exception e) {
	        logger.error("[SFTP] Error general en la conexión o eliminación: {}", e.getMessage(), e);
	        throw new IOException("Error en la conexión SFTP", e);
	    }
	}


	public void subeBackUp(InputStream file, String fileName) throws IOException {
	    String rutaNueva;
	    String fecha = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

	    if (fileName.toLowerCase().contains("posp")) {
	        rutaNueva = fecha + "/PO";
	    } else {
	        rutaNueva = fecha + "/PT";
	    }

	    DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory(true);
	    factory.setHost(SERVER_WINE_SFTP);
	    factory.setPort(4222);
	    factory.setUser(USER_WINE_SFTP);
	    factory.setPassword(PASS_WINE_SFTP);
	    factory.setAllowUnknownKeys(true);
	    factory.setTimeout(60000);

	    try (SftpSession session = factory.getSession()) {
	        ChannelSftp sftp = session.getClientInstance();

	        // Ir a la carpeta base
	        sftp.cd(CARPETA_BACKUP);

	        // Crear estructura de carpetas si no existe
	        String[] folders = rutaNueva.split("/");
	        for (String folder : folders) {
	            if (!folder.isEmpty()) {
	                try {
	                    sftp.cd(folder);
	                } catch (SftpException e) {
	                    sftp.mkdir(folder);
	                    sftp.cd(folder);
	                }
	            }
	        }

	        // Verificar si ya existe el archivo
	        Vector<ChannelSftp.LsEntry> archivos = sftp.ls(".");
	        boolean existe = archivos.stream()
	            .anyMatch(entry -> entry.getFilename().equals(fileName));

	        if (!existe) {
	            sftp.put(file, fileName);
	        }

	    } catch (SftpException e) {
	        throw new IOException("Error al subir backup a SFTP", e);
	    } catch (Exception e) {
	        throw new IOException("Error en la conexión SFTP", e);
	    }
	}

	
	
	
	public void subeArchivoOut(InputStream file, String fileName) throws IOException {
	    DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory(true);

	    factory.setHost(SERVER_WINE_SFTP);
	    factory.setPort(4222);
	    factory.setUser(USER_WINE_SFTP);
	    factory.setPassword(PASS_WINE_SFTP);
	    factory.setAllowUnknownKeys(true);
	    factory.setTimeout(60000);

	    logger.info("[SFTP] Iniciando subida del archivo: {}", fileName);

	    try (SftpSession session = factory.getSession()) {
	        ChannelSftp channelSftp = session.getClientInstance();
	        logger.info("[SFTP] Conexión SFTP establecida con {}.", SERVER_WINE_SFTP);

	        // Cambiar al directorio de trabajo
	        channelSftp.cd(CARPETA_OUT);

	        // Verificar si ya existe el archivo
	        Vector<ChannelSftp.LsEntry> archivos = channelSftp.ls(".");
	        boolean existe = archivos.stream().anyMatch(entry -> entry.getFilename().equals(fileName));

	        if (existe) {
	            logger.warn("[SFTP] El archivo '{}' ya existe en la carpeta {}", fileName, CARPETA_OUT);
	            return;
	        }

	        // Subir el archivo
	        channelSftp.put(file, fileName);
	        logger.info("[SFTP] Archivo '{}' subido correctamente a {}", fileName, CARPETA_OUT);

	    } catch (SftpException e) {
	        logger.error("[SFTP] Error al subir el archivo '{}': {}", fileName, e.getMessage(), e);
	        throw new IOException("Error al subir el archivo por SFTP", e);
	    } catch (Exception e) {
	        logger.error("[SFTP] Error general durante la conexión o transferencia: {}", e.getMessage(), e);
	        throw new IOException("Error en la conexión o transferencia SFTP", e);
	    }
	}
	
	public void creaDirectorio(String ruta, String directorio) throws IOException {
	    DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory(true);

	    factory.setHost(SERVER_WINE_SFTP);
	    factory.setPort(4222);
	    factory.setUser(USER_WINE_SFTP);
	    factory.setPassword(PASS_WINE_SFTP);
	    factory.setAllowUnknownKeys(true);
	    factory.setTimeout(60000);

	    logger.info("[creaDirectorioSFTP] Iniciando creación de directorio: " + ruta + "/" + directorio);

	    try (SftpSession session = factory.getSession()) {
	        logger.info("[creaDirectorioSFTP] Sesión SFTP establecida correctamente.");
	        ChannelSftp channelSftp = session.getClientInstance();
	        logger.info("[creaDirectorioSFTP] Instancia de ChannelSftp obtenida.");

	        // Cambiar al directorio base (si existe)
	        if (ruta != null && !ruta.trim().isEmpty()) {
	            channelSftp.cd(ruta);
	        }

	        // Crear el directorio
	        channelSftp.mkdir(directorio);
	        logger.info("[creaDirectorioSFTP] Directorio creado correctamente: " + ruta + "/" + directorio);

	    } catch (SftpException e) {
	        // El código 4 en SftpException.SSH_FX_FAILURE suele indicar que ya existe
	        if (e.id == ChannelSftp.SSH_FX_FAILURE) {
	            logger.warn("[creaDirectorioSFTP] El directorio ya existe o no se pudo crear: " + ruta + "/" + directorio + " - " + e.getMessage());
	        } else {
	            logger.error("[creaDirectorioSFTP] Error SFTP creando directorio: " + e.getMessage(), e);
	            throw new IOException("Error creando directorio por SFTP: " + e.getMessage(), e);
	        }
	    } catch (Exception e) {
	        logger.error("[creaDirectorioSFTP] Error creando directorio por SFTP: " + e.getMessage(), e);
	        throw new IOException("Error creando directorio por SFTP: " + e.getMessage(), e);
	    }
	}


}