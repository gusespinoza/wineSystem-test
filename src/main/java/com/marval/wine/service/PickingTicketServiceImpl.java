package com.marval.wine.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marval.wine.dao.PickingTicketDao;
import com.marval.wine.entity.PickingTicket;
import com.marval.wine.entity.PtDisponible;

@Service
public class PickingTicketServiceImpl implements  PickingTicketService{

	
	@Autowired
	private PickingTicketDao pickingTicketDao;

	@Transactional
	@Override
	public void savePickingTicket(List<PickingTicket> pickingTickets) {
		pickingTicketDao.savePickingTicket(pickingTickets);
	}
	
	
	@Transactional
	@Override
	public void updatePt(PickingTicket pt) {
		pickingTicketDao.updatePt(pt);
	}

	@Transactional
	@Override
	public List<PickingTicket> listPT(String vina){
		return pickingTicketDao.listPT(vina);
	}
	
	@Transactional
	@Override
	public List<PtDisponible> ptDisponibles(){
		return pickingTicketDao.ptDisponibles();
	}
	
	@Transactional
	@Override
	public void anularPt(Integer id) {
		pickingTicketDao.anularPt(id);
	}

}
