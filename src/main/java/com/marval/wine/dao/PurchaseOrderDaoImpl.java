package com.marval.wine.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.marval.wine.entity.AjusteInventario;
import com.marval.wine.entity.Mensaje;
import com.marval.wine.entity.MensajeBodega;
import com.marval.wine.entity.PoAxis;
import com.marval.wine.entity.PurchaseOrder;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PurchaseOrderDaoImpl implements PurchaseOrderDao {
	
	private Date fecha1;

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public void savePurchaseOrder(List<PurchaseOrder> purchaseOrders) {
		fecha1 = new Date();
		fecha1.setSeconds(00);
		for (PurchaseOrder pt : purchaseOrders) {
			pt.setFechaCr(fecha1);
			System.out.println("PO: " + pt.getPurchaseOrderId());
			sessionFactory.getCurrentSession().persist(pt);
			sessionFactory.getCurrentSession().flush();
			sessionFactory.getCurrentSession().clear();
		}
	}

	@Override
	public void guardarPo(PurchaseOrder po) {
		sessionFactory.getCurrentSession().persist(po);
	}

	@Override
	public void updatePo(PurchaseOrder po) {
		sessionFactory.getCurrentSession().update(po);
	}

	@Override
	public List<PoAxis> listPo(String vina) {
		List<PoAxis> pos = new ArrayList<PoAxis>();
		if (!vina.isEmpty()) {
			String query = "select  "
					+ "p.id as id,  "
					+ "p.purchase_order_id as po,  "
					+ "case when p.order_reference = 'NULL' then '' else p.order_reference end as or_ref,  "
					+ "p.vintage as cosecha,  "
					+ "p.product_number as cod_prod,  "
					+ "p.product_description as prod, "
					+ "p.purchase_order_quantity as un_inf, "
					+ "p.bottles_per_unit as bot_caja,  "
					+ "p.ml_per_bottle as bot_ml, "
					+ "(select case when sum(pd.cantidad) > 0 then sum(pd.cantidad) else 0 end as cantidad from purchase_order_detalle as pd where pd.estado = 'RECEPCIONADO' and pd.po_id = p.id) as un_recep "
					+ "from purchase_order as p WHERE p.estado = 'DISPONIBLE'  and p.owner_id=:vina and p.tipo_recep='AXIS' ORDER BY p.fecha_cr DESC";
			return sessionFactory.getCurrentSession().createNativeQuery(query, PoAxis.class)
					.setParameter("vina", vina).list();
		} else {
			return pos;
		}
	}

	@Override
	public List<String> listPoAsignacion() {
		List<String> pos;
		String query = "select distinct(po.purchase_order_id) "
				+ "from purchase_order as po "
				+ "inner join purchase_order_detalle as pod on po.id = pod.po_id "
				+ "inner join etiqueta as et on et.id_detalle_po = pod.id "
				+ "where pod.estado in ('RECEPCIONADO', 'ALMACENADO') and et.asignado = 0 ";
		pos = sessionFactory.getCurrentSession().createNativeQuery(query).list();
		if (pos.isEmpty()) {
			pos = new ArrayList<String>();
		}
		return pos;
	}

	@Override
	public List<PurchaseOrder> listPoManual() {
		List<PurchaseOrder> pos = new ArrayList<PurchaseOrder>();
		String query = "select * from purchase_order as po WHERE po.estado = 'DISPONIBLE'  and po.tipo_recep='MANUAL' order by po.fecha_cr desc";
		pos = sessionFactory.getCurrentSession().createNativeQuery(query, PurchaseOrder.class).list();
		return pos;
	}
	
	@Override
	public PurchaseOrder buscarPo(Integer id) {
		PurchaseOrder po = new PurchaseOrder();
		String query = "select * from purchase_order as po where po.id=:id";
		po =  sessionFactory.getCurrentSession().createNativeQuery(query, PurchaseOrder.class).setParameter("id", id).uniqueResult();
		return po;
	}

	@Override
	public List<String> autoCompletarPo(String po) {
		String query = "select distinct(po.codigo_fert) from purchase_order as po "
				+ "inner join purchase_order_detalle as pod on po.id = pod.po_id  "
				+ "inner join etiqueta as et on et.id_detalle_po = pod.id "
				+ "where pod.estado in ('RECEPCIONADO', 'ALMACENADO') and et.asignado = 0 and po.codigo_fert like :po";
		return (List<String>) sessionFactory.getCurrentSession().createNativeQuery(query)
				.setParameter("po", "%" + po + "%").list();
	}

	@Override
	public List<String> autoCompletarLote(String lote) {
		String query = "select distinct top 10 pod.lote from purchase_order_detalle as pod "
				+ "inner join purchase_order as po on po.id = pod.po_id "
				+ "inner join etiqueta as et on et.id_detalle_po = pod.id "
				+ "where pod.estado in ('RECEPCIONADO', 'ALMACENADO') and et.asignado = 0 and pod.lote like :lote ";
		return (List<String>) sessionFactory.getCurrentSession().createNativeQuery(query)
				.setParameter("lote", "%" + lote + "%").list();
	}

	@Override
	public List<String> autoCompletarCodProd(String codigo) {
		String query = "select distinct(concat(po.product_number, ' / ', po.product_description)) "
				+ "from purchase_order as po "
				+ "where po.product_description <> '' "
				+ "and po.product_number like :codigo "
				+ "group by po.product_number, po.product_description "
				+ " order by concat(po.product_number, ' / ', po.product_description) "
				+ "OFFSET 0 ROWS FETCH FIRST 10 ROWS ONLY;";
		return (List<String>) sessionFactory.getCurrentSession().createNativeQuery(query)
				.setParameter("codigo", "%" + codigo + "%").list();
	}

	@Override
	public List<String> autoCompletarNombreProd(String prod) {
		String query = "select distinct(concat(po.product_description, ' / ', po.product_number)) "
				+ "from purchase_order as po "
				+ "where po.product_description <> '' "
				+ "and po.product_description like :prod "
				+ "group by po.product_number, po.product_description "
				+ "order by concat(po.product_description, ' / ', po.product_number) "
				+ "OFFSET 0 ROWS FETCH FIRST 10 ROWS ONLY;";
		return (List<String>) sessionFactory.getCurrentSession().createNativeQuery(query)
				.setParameter("prod", "%" + prod + "%").list();
	}

	@Override
	public boolean consultarArchivoProcesado(String nombreArchivo) {
		String query = "SELECT TOP 1 men.usuario_cr FROM mensaje AS men WITH (nolock) WHERE men.nombre_archivo=:archivo";
		String res = (String) sessionFactory.getCurrentSession().createNativeQuery(query)
				.setParameter("archivo", nombreArchivo).uniqueResult();
		return (res != null && !res.equals("")) ? true : false;
	}

	@Override
	public void guardarMensaje(Mensaje mensaje) {
		sessionFactory.getCurrentSession().persist(mensaje);
	}
	
	@Override
	public boolean validarInventoryRecon() {
		String query = "select count(id) as fecha  from mensaje where tipo = 'INVENTORYRECON' and Convert(DATE, fecha_cr) = Convert(DATE, GetDate())";
		Integer res = (Integer) sessionFactory.getCurrentSession().createNativeQuery(query).uniqueResult();
		return (res == 0 ) ? true : false;
	}
	@Override
	public boolean validarInventoryAjust() {
		String query = "select count(id) as fecha  from ajuste_inventario where nombre_txt is null and Convert(DATE, fecha_cr) = Convert(DATE, GetDate())";
		Integer res = (Integer) sessionFactory.getCurrentSession().createNativeQuery(query).uniqueResult();
		return (res == 0 ) ? true : false;
	}
	
	@Override
	public List<MensajeBodega> mensajeInventario(){
		List<MensajeBodega> mensaje = new ArrayList<MensajeBodega>();
		String query = "select ROW_NUMBER() over (order by po.owner_id) as id, "
				+ "po.owner_id as vina, "
				+ "pod.cod_producto as cod_producto, "
				+ "pod.cosecha as cosecha,'estado' = CASE pod.estado_carga WHEN 'Buen estado' THEN 'GOOD'  WHEN 'Mal estado' then 'DAMAGED' END, "
				+ "sum(e.cajas_pallet) as cantidad "
				+ "from etiqueta as e "
				+ "inner join purchase_order_detalle as pod on e.id_detalle_po =  pod.id "
				+ "inner join purchase_order as po on pod.po_id = po.id "
				+ "where e.estado in ( 'ALMACENADA', 'RECEPCIONADA')  and po.tipo_recep = 'AXIS'"
				+ "group by po.owner_id,pod.cod_producto, pod.cosecha, pod.estado_carga";
		mensaje = sessionFactory.getCurrentSession().createNativeQuery(query, MensajeBodega.class).list();
		return mensaje;
	}
	
	@Override
	public List<AjusteInventario> listaAjustes(){
		List<AjusteInventario> ajustes = new ArrayList<AjusteInventario>();
		String query =  "select * from ajuste_inventario where nombre_txt is null";
		ajustes = sessionFactory.getCurrentSession().createNativeQuery(query, AjusteInventario.class).list();
		return ajustes;
	}
	
	@Override
	public void nombreAjustes(String nombreArchivo) {
		String query =  "update ajuste_inventario set nombre_txt =:nombreArchivo where nombre_txt is null";
		sessionFactory.getCurrentSession().createNativeQuery(query).setParameter("nombreArchivo", nombreArchivo).executeUpdate();
	}

}
