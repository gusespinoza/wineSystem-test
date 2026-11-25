package com.marval.wine.dao;

import java.math.BigInteger;
import java.util.List;

import com.marval.wine.entity.AjusteInventario;
import com.marval.wine.entity.PoAjuste;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AjusteInventarioDaoImpl implements AjusteInventarioDao {
	

	@Autowired
	private SessionFactory sessionFactory;


	@Override
	public List<PoAjuste> buscarPos(String prod, String cosecha) {

		
		String query = "exec dbo.buscar_pos :prod , :cosecha ;";
		

		return (List<PoAjuste>) sessionFactory.getCurrentSession().createNativeQuery(query, PoAjuste.class)
				.setParameter("prod", prod)
				.setParameter("cosecha", cosecha).list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<String> consultarProductNumbers(String valor) {

		String hql = "SELECT DISTINCT product_number FROM purchase_order WHERE estado in ('RECEPCIONADO', 'ALMACENADO') "
				+ " AND product_number LIKE :valor";
		return (List<String>) sessionFactory.getCurrentSession()
				.createNativeQuery(hql)
				.setParameter("valor", valor + "%")
				.list();

	}
	
	@Override
	public void guardarAjusteInventario(AjusteInventario ajuste) {
		sessionFactory.getCurrentSession().saveOrUpdate(ajuste);
	}

	
	@Override
	public Integer consultarCantidadAjustesEtiqueta(String producto, String cosecha) {
		String query = "SELECT COUNT(id) FROM ajuste_inventario as aj " + "	WHERE aj.product_number = :producto "
				+ "   AND aj.vintage = :cosecha ";

		return (Integer) sessionFactory.getCurrentSession().createNativeQuery(query).setParameter("producto", producto)
				.setParameter("cosecha", cosecha).uniqueResult();
	}
	

	@Override
	public BigInteger consultarUltimaCantidadIngresada(String producto, String cosecha) {
		String query = "SELECT TOP 1 cantidad_nueva FROM ajuste_inventario as aj "
				+ "	WHERE aj.product_number = :producto " + "   AND aj.vintage = :cosecha "
				+ "	ORDER BY aj.fecha_cr DESC";

		return (BigInteger) sessionFactory.getCurrentSession().createNativeQuery(query)
				.setParameter("producto", producto).setParameter("cosecha", cosecha).uniqueResult();
	}
	
	@Override
	public String secuenciaSPCR() {
		BigInteger correlativo = (BigInteger) sessionFactory.getCurrentSession()
				.createNativeQuery("SELECT next value for dbo.seq_ajuste_inventario_spcr").uniqueResult();
		return String.valueOf(correlativo);
	}
	
	@Override
	public String secuenciaSPRC() {
		BigInteger correlativo = (BigInteger) sessionFactory.getCurrentSession()
				.createNativeQuery("SELECT next value for dbo.seq_receip_sprc").uniqueResult();
		return String.valueOf(correlativo);
	}
	
	@Override
	public String secuenciaSPSP() {
		BigInteger correlativo = (BigInteger) sessionFactory.getCurrentSession()
				.createNativeQuery("SELECT next value for dbo.seq_shipment_spsp").uniqueResult();
		return String.valueOf(correlativo);
	}
	
	@Override
	public String secuenciaSPST() {
		BigInteger correlativo = (BigInteger) sessionFactory.getCurrentSession()
				.createNativeQuery("SELECT next value for dbo.seq_recon_spst").uniqueResult();
		return String.valueOf(correlativo);
	}
	
}
