package com.marval.wine.service;

import java.util.List;

import com.marval.wine.entity.PickingTicket;
import com.marval.wine.entity.PtDisponible;

public interface PickingTicketService {

	void savePickingTicket(List<PickingTicket> pickingTickets);

	void updatePt(PickingTicket pt);

	List<PickingTicket> listPT(String vina);

	List<PtDisponible> ptDisponibles();

	void anularPt(Integer id);

}
