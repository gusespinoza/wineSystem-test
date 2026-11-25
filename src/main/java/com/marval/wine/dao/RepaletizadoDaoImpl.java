package com.marval.wine.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.marval.wine.entity.Repaletizado;

@Repository
public class RepaletizadoDaoImpl implements RepaletizadoDao {
	
	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public List<Repaletizado> buscarEtiquetasRepaletizado(String etiqueta) {

		String hql = "select   " 
				+ "pu.id as id," 
				+ "pu.supplier_id as vina," 
				+ "pu.product_description as producto,"
				+ "pud.cod_producto as codigo_producto," 
				+ "pud.cosecha as cosecha,"
				+ "pu.bottles_per_unit as botellas_caja," 
				+ "et.nro_etiqueta as etiqueta,"
				+ "et.cajas_pallet as cajas_pallet," 
				+ "pud.lote as lote, "
				+ "et.slipsheet as slipsheet "
				+ "FROM purchase_order as pu "
				+ "INNER JOIN purchase_order_detalle as pud ON pu.id = pud.po_id "
				+ "INNER JOIN etiqueta as et ON et.id_detalle_po = pud.id " 
				+ "WHERE et.nro_etiqueta = :etiqueta "
				+ " AND et.repaletizado = 0 ";

		return (List<Repaletizado>) sessionFactory.getCurrentSession().createNativeQuery(hql, Repaletizado.class)
				.setParameter("etiqueta", etiqueta).list();

	}

	
	@Override
	public Repaletizado buscarEtiquetaRepaletizado(String etiquetaSecun) {
		String hql = "select  " 
				+ "pu.id as id," 
				+ "pu.supplier_id as vina," 
				+ "pu.product_description as producto,"
				+ "pud.cod_producto as codigo_producto," 
				+ "pud.cosecha as cosecha,"
				+ "pu.bottles_per_unit as botellas_caja," 
				+ "et.nro_etiqueta as etiqueta,"
				+ "et.cajas_pallet as cajas_pallet," 
				+ "pud.lote as lote, "
				+ "et.slipsheet as slipsheet "
				+ "FROM purchase_order as pu "
				+ "INNER JOIN purchase_order_detalle as pud ON pu.id = pud.po_id "
				+ "INNER JOIN etiqueta as et ON et.id_detalle_po = pud.id " 
				+ "WHERE et.nro_etiqueta = :etiqueta "
				+ " AND et.repaletizado = 0 ";

		return (Repaletizado) sessionFactory.getCurrentSession().createNativeQuery(hql, Repaletizado.class)
				.setParameter("etiqueta", etiquetaSecun).uniqueResult();
	}
}
