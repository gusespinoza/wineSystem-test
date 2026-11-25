package com.marval.wine.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.marval.wine.entity.PurchaseOrderDetalle;

@Repository
public class DetalleRecepcionDaoImpl implements DetalleRecepcionDao {

	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public void guardarRecepcion(PurchaseOrderDetalle recepcion) {
			sessionFactory.getCurrentSession().persist(recepcion);
			sessionFactory.getCurrentSession().flush();
			sessionFactory.getCurrentSession().clear();
	}
	
	@Override
	public List<PurchaseOrderDetalle> listarDetalle(Integer poId){
		List<PurchaseOrderDetalle> listaDetalle  =  new ArrayList<PurchaseOrderDetalle>();
		String query = "Select  *  from purchase_order_detalle where estado = 'RECEPCIONADO' and po_id =:poId";
		listaDetalle = sessionFactory.getCurrentSession().createNativeQuery(query, PurchaseOrderDetalle.class).setParameter("poId", poId).list();
		
		return listaDetalle;
	}
	
	@Override
	public void eliminarDetalle(Integer detalleId) {
		String query = "update purchase_order_detalle set estado = 'CANCELADO' where id=:detalleId ";
		sessionFactory.getCurrentSession().createNativeQuery(query).setParameter("detalleId", detalleId).executeUpdate();
	}
}
