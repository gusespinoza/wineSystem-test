package com.marval.wine.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.marval.wine.bean.LoginBean;
import com.marval.wine.dao.IndicadoresDao;
import com.marval.wine.entity.HistorialIndicadores;
import com.marval.wine.entity.IndicadoresFinancieros;
import com.marval.wine.entity.IndicadoresFinancierosUF;

@Service
public class IndicadoresServiceImpl implements IndicadoresService {
	
	@Autowired
	IndicadoresDao indicadoresDao;
	
	@Autowired
	LoginBean login;

	@Transactional
	@Override
	public HistorialIndicadores indicadorMonedaFecha(String string, Date date) {
		return indicadoresDao.indicadorMonedaFecha(string, date);
	}
	
	@Transactional
	@Override
	public IndicadoresFinancieros indicadorEconomicoSBIF(String tipo) throws IOException {
		
		Date fechaHoy = new Date();
		
		String anio = new SimpleDateFormat("yyyy").format(fechaHoy);
		String mes = new SimpleDateFormat("MM").format(fechaHoy);
		String dia = new SimpleDateFormat("dd").format(fechaHoy);
		
		String ftpUrl = "https://api.sbif.cl/api-sbifv3/recursos_api/"+tipo+"/"
				+anio+"/"+mes+"/dias/"+dia+"?apikey=28a09ea299d773960aaf34bf2998e08f38918ad8&formato=xml";
		
		
		BigDecimal valor = BigDecimal.ZERO;

		try {
			URL url = new URL(ftpUrl);
			URLConnection conn = url.openConnection();
			String json = "";
			InputStream is = conn.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
				System.out.println(line);
			}
			is.close();
			json = sb.toString();
			
			
			XmlMapper xmlMapper = new XmlMapper();
			return xmlMapper.readValue(json, IndicadoresFinancieros.class);


		} catch (Exception e) {
//			if (!indicadoresDao.listaInd(tipo).equals("no")) {
//				return ultimoValor(tipo).getValorCambio();
//			}
			return null;
			
		}
	}

	@Transactional
	@Override
	public void guardaHistorialIndicador(HistorialIndicadores histGuardar) {
		indicadoresDao.guardaHistorialIndicador(histGuardar);
	}

	@Transactional
	@Override
	public void actualizaIndicador(HistorialIndicadores historial) {
		indicadoresDao.actualizaIndicador(historial);
	}

	@Transactional
	@Override
	public IndicadoresFinancierosUF indicadorEconomicoUF(String tipo) {
		
		String ftpUrl = "https://api.sbif.cl/api-sbifv3/recursos_api/"+tipo+"?apikey=28a09ea299d773960aaf34bf2998e08f38918ad8&formato=xml";
		

		try {
			URL url = new URL(ftpUrl);
			URLConnection conn = url.openConnection();
			String json = "";
			InputStream is = conn.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
				System.out.println(line);
			}
			is.close();
			json = sb.toString();
			
			
			XmlMapper xmlMapper = new XmlMapper();
			return xmlMapper.readValue(json, IndicadoresFinancierosUF.class);


		} catch (Exception e) {
//			if (!indicadoresDao.listaInd(tipo).equals("no")) {
//				return ultimoValor(tipo).getValorCambio();
//			}
			return null;
			
		}
	}

}
