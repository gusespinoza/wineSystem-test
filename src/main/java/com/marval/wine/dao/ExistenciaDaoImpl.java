package com.marval.wine.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.marval.wine.entity.Existencia;
import com.marval.wine.util.Constantes;

@Repository
public class ExistenciaDaoImpl implements ExistenciaDao {
	

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public List<Existencia> consultarCargasPorVina(String vinaSelec, String orden, Date fechaDesde, Date fechaHasta, String fert, String po) {


		String query = "exec dbo.existencia_carga :vina , :orden, :fert, :po, :fechaDesde, :fechaHasta ;";
		/*if(fert != null && !fert.equals("")) {
			hql = hql + " AND pu.codigo_fert = :fert ";
			parametros.put("fert", fert);
			
		}
		
		if(po != null && !po.equals("")) {
			hql = hql + " AND pu.purchase_order_id = :po ";
			parametros.put("po", po);
			
		}

		if (orden != null && !orden.equals("")) {

			if (orden.equals(Constantes.FECHA_REC)) {
				hql = hql + " AND et.estado != 'CANCELADA' ORDER BY pud.fecha_recepcion DESC ";
				
			} else if (orden.equals(Constantes.HISTORICO)) {
				hql = hql
						+ " AND (et.estado != 'ALMACENADA' OR et.estado != 'RECEPCIONADA') ";
				
				if (fechaDesde != null && fechaHasta != null) {
					
					hql = hql + " AND  BETWEEN :fechaDesde AND :fechaHasta";
					
					parametros.put("fechaDesde", fechaDesde);
					parametros.put("fechaHasta", fechaHasta);
				}
				
				hql = hql + " ORDER BY pud.fecha_recepcion DESC ";
				
				
			} else if (orden.equals(Constantes.DISPONIBLE)) {
				hql = hql + " AND et.estado = 'RECEPCIONADA' ORDER BY pud.fecha_recepcion DESC ";
				
			} else if (orden.equals(Constantes.UBICACION)) {
				hql = hql + " AND et.estado = 'ALMACENADA' ORDER BY pud.fecha_recepcion DESC ";
				
			} else if (orden.equals(Constantes.STOCK_COMPLETO)) {
				hql = hql + "  AND et.estado in ('RECEPCIONADA', 'ALMACENADA')  ORDER BY pud.fecha_recepcion DESC ";
			}
		}*/

		return (List<Existencia>) sessionFactory.getCurrentSession().createNativeQuery(query, Existencia.class)
				.setParameter("vina", vinaSelec)
				.setParameter("orden", orden)
				.setParameter("fechaDesde", fechaDesde)
				.setParameter("fechaHasta", fechaHasta)
				.setParameter("fert", fert)
				.setParameter("po", po).list();
	}

}
