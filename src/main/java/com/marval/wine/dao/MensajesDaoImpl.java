package com.marval.wine.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.marval.wine.entity.MensajeProcesado;
import com.marval.wine.entity.Ubicacion;

@Repository
public class MensajesDaoImpl implements MensajesDao {
	

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public List<MensajeProcesado> buscarMensajesProcesados(String tipoMensaje, Date fechaDesde,
			Date fechaHasta) {

		Map<String, Object> parametros = new HashedMap();

		String hql = "SELECT " + 
				"aj.id as id, " + 
				"aj.fecha_cr as fecha_cr, " +
				"aj.usuario_cr as usuario_cr, " +
				"aj.tipo as tipo, " +
				"aj.nombre_archivo as archivo, " + 
				"convert(varchar(max), aj.mensaje ) as texto " + 
				"FROM mensaje AS aj ";
				

		if (tipoMensaje != null && !tipoMensaje.equals("")) {

			hql = hql + " WHERE aj.tipo = :tipoMensaje";
			parametros.put("tipoMensaje", tipoMensaje);

			if (fechaDesde != null) {
				hql = hql + " AND aj.fecha_cr BETWEEN :fechaDesde AND :fechaHasta order by aj.fecha_cr desc ";
				parametros.put("fechaDesde", fechaDesde);
				parametros.put("fechaHasta", fechaHasta);
			}

		}

		return (List<MensajeProcesado>) sessionFactory.getCurrentSession()
				.createNativeQuery(hql, MensajeProcesado.class)
				.setProperties(parametros)
				.list();
	}

}
