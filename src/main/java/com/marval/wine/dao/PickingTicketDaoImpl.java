package com.marval.wine.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.marval.wine.entity.PickingTicket;
import com.marval.wine.entity.PtDisponible;

@Repository
public class PickingTicketDaoImpl implements PickingTicketDao  {
	
	private Date fecha1;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public void savePickingTicket(List<PickingTicket> pickingTickets) {
		fecha1 = new Date();
		fecha1.setSeconds(00);
		for (PickingTicket pt : pickingTickets) {
			pt.setFechaCr(fecha1);
			sessionFactory.getCurrentSession().persist(pt);
			sessionFactory.getCurrentSession().flush();
			sessionFactory.getCurrentSession().clear();
		}
	}
	
	@Override
	public void updatePt(PickingTicket pt) {
		sessionFactory.getCurrentSession().update(pt);
	}
	
	
	@Override
	public List<PickingTicket> listPT(String vina) {
		List<PickingTicket> pot =  new ArrayList<PickingTicket>();
		if(!vina.isEmpty()) {
			String query = "select * from picking_ticket as pt WHERE pt.estado = 'DISPONIBLE'  and pt.owner_id=:vina";
			return sessionFactory.getCurrentSession().createNativeQuery(query, PickingTicket.class).setParameter("vina", vina).list();
		}else {
			return pot;
		}
	}
	
	
	@Override
	public List<PtDisponible> ptDisponibles(){
		List<PtDisponible> pot =  new ArrayList<PtDisponible>();
		String query = "select ptd.id, ptd.shipment_id as pt, ptd.owner_id as vina,  ptd.orden_reference as or_ref, ptd.product_number as cod_prod, ptd.order_quantity as cantidad, ptd.lot_number as cosecha from picking_ticket as ptd WHERE ptd.estado = 'DISPONIBLE'  order by ptd.fecha_cr desc";
		pot = sessionFactory.getCurrentSession().createNativeQuery(query, PtDisponible.class).list();		
		return pot; 
	}
	
	
	@Override
	public void anularPt(Integer id) {
		String query =  "update picking_ticket set estado = 'ANULADO' where id =:id";
		sessionFactory.getCurrentSession().createNativeQuery(query).setParameter("id", id).executeUpdate();
	}
	

}
