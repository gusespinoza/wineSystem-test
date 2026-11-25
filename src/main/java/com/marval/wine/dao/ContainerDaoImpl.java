package com.marval.wine.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.marval.wine.entity.ConsultarEtiqueta;
import com.marval.wine.entity.Container;
import com.marval.wine.entity.DespachoContenedor;
import com.marval.wine.entity.Existencia;
import com.marval.wine.entity.PackingList;
import com.marval.wine.entity.PickingTicket;
import com.marval.wine.entity.ResumenFacturacion;
import com.marval.wine.entity.Ubicacion;
import com.marval.wine.util.Constantes;

@Repository
public class ContainerDaoImpl implements ContainerDao {
	

	private Date fecha1;

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public void saveContainer(List<Container> containers) {
		fecha1 = new Date();
		fecha1.setSeconds(00);
		for (Container pt : containers) {
			pt.setFechaCr(fecha1);
			sessionFactory.getCurrentSession().persist(pt);
			sessionFactory.getCurrentSession().flush();
			sessionFactory.getCurrentSession().clear();
		}
	}

	@Override
	public List<Container> listarContenedoresCargados() {
		String hql = "select * from container  WITH (NOLOCK) where consolidado =  0 and "
				+ "estado = 'DISPONIBLE' ORDER BY fecha_cr desc";
		return sessionFactory.getCurrentSession().createNativeQuery(hql, Container.class).list();
	}

	@Override
	public Map<Integer, String> listarContenedoresConsolidados() {
		List<String> resultado = new ArrayList<String>();
		Map<Integer, String> tar = new HashMap<Integer, String>();
		String query = "select distinct(concat(c.id ,'/', c.container)) from asignacion as asg  "
				+ "inner join container as c on asg.container_id =  c.id  "
				+ "where c.estado = 'CONSOLIDADO' and consolidado = 1";
		resultado = sessionFactory.getCurrentSession().createNativeQuery(query).list();

		if (!resultado.isEmpty() || resultado != null) {
			String idContenedor = null;
			Integer ide = null;
			String contenedor = null;
			for (String r : resultado) {
				idContenedor = r;
				ide = Integer.valueOf(idContenedor.split("/")[0]);
				contenedor = idContenedor.split("/")[1];
				tar.put(ide, contenedor);
			}
		} else {
			tar = new HashMap<Integer, String>();
		}
		return tar;
	}

	@Override
	public List<Container> listarContenedoresDisponibles() {
		String hql = "select * from container  WITH (NOLOCK) where " + "estado = 'DISPONIBLE' ORDER BY fecha_cr desc";
		return sessionFactory.getCurrentSession().createNativeQuery(hql, Container.class).list();
	}

	@Override
	public List<Ubicacion> buscarEtiquetas(String etiqueta) {
		String hql = "select  " 
				+ "			et.id as id," 
				+ "			et.id_detalle_po as id_detalle_po,"
				+ "			et.nro_etiqueta as etiqueta, " 
				+ "			pu.supplier_id as vina, "
				+ "			pu.product_description as producto, " 
				+ "			et.pasillo as pasillo, "
				+ "			et.posicion as posicion, " 
				+ "			et.altura as altura, "
				+ "			et.estado as estado_et, " 
				+ "			pud.estado as estado_pud, "
				+ "			et.cajas_pallet as cantidad," 
				+ "			pud.lote as lote"
				+ "		FROM etiqueta as et"
				+ " INNER JOIN purchase_order_detalle as pud ON pud.id = et.id_detalle_po "
				+ " INNER JOIN purchase_order as pu ON pu.id = pud.po_id " 
				+ "		WHERE et.nro_etiqueta =:etiqueta ";
		return sessionFactory.getCurrentSession().createNativeQuery(hql, Ubicacion.class)
				.setParameter("etiqueta", etiqueta).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> autoCompletaEtiquetaRecep(String etiqueta) {
		String hql = "SELECT TOP 5 nro_etiqueta FROM etiqueta as et " + " WHERE et.estado = 'RECEPCIONADA' "
				+ " AND et.nro_etiqueta LIKE :etiqueta";
		return sessionFactory.getCurrentSession().createNativeQuery(hql).setParameter("etiqueta", etiqueta + "%")
				.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> autoCompletaEtiquetaAlm(String etiqueta) {
		String hql = "SELECT TOP 5 nro_etiqueta FROM etiqueta as et " + " WHERE et.estado = 'ALMACENADA' "
				+ " AND et.nro_etiqueta LIKE :etiqueta";
		return (List<String>) sessionFactory.getCurrentSession().createNativeQuery(hql)
				.setParameter("etiqueta", etiqueta + "%").list();
	}

	

	@Override
	public String validarPosicion(String pasillo, String altura, String posicion) {
		String query = "SELECT TOP 1 nro_etiqueta FROM etiqueta as et " + " WHERE et.pasillo = :pasillo "
				+ " AND et.altura = :altura " + " AND et.posicion = :posicion " + " AND et.estado = 'ALMACENADA'";
		return (String) sessionFactory.getCurrentSession().createNativeQuery(query).setParameter("pasillo", pasillo)
				.setParameter("altura", altura).setParameter("posicion", posicion).uniqueResult();
	}

	

	@Override
	public void cancelarEtiqueta(ConsultarEtiqueta ubi) {
		String query = "UPDATE etiqueta SET estado=:estado, observacion =:motivo, fecha_up=:fechaUp where nro_etiqueta=:id";
		sessionFactory.getCurrentSession().createNativeQuery(query).setParameter("estado", Constantes.ESTADO_CANCELADA)
				.setParameter("id", ubi.getEtiqueta())
				.setParameter("motivo", ubi.getMotivoAnulacion())
				.setParameter("fechaUp", new Date()).executeUpdate();
	}

	@Override
	public Container buscarContenedor(Integer id) {
		String query = "select * from container where id =:id";
		Container contenedor = sessionFactory.getCurrentSession().createNativeQuery(query, Container.class)
				.setParameter("id", id).getSingleResult();
		return contenedor;
	}

	@Override
	public int buscarAsignacionesContenedor(Integer id) {
		String query = "select count(asg.etiqueta_id) from asignacion as asg WITH (NOLOCK) where asg.container_id=:id";

		return (int) sessionFactory.getCurrentSession().createNativeQuery(query).setParameter("id", id).uniqueResult();
	}

	@Override
	public void actualizarPOAlmacenada(Ubicacion ubicacion) {
		String query = "UPDATE etiqueta SET estado=:estado where id=:id";
		sessionFactory.getCurrentSession().createNativeQuery(query).setParameter("estado", Constantes.ESTADO_ALMACENADA)
				.setParameter("id", ubicacion.getId()).executeUpdate();
	}

	@Override
	public Ubicacion buscarEtiqueta(String etiqueta) {
		String hql = "select   " 	
				+ "et.id as id, " 
				+ "et.id_detalle_po as id_detalle_po, "
				+ "et.nro_etiqueta as etiqueta,   " 
				+ "pu.supplier_id as vina,   "
				+ "pu.product_description as producto,   " 
				+ "et.pasillo as pasillo,   " 
				+ "et.posicion as posicion,   "
				+ "et.altura as altura,   " 
				+ "et.estado as estado_et,   " 
				+ "pud.estado_carga as estado_pud, "
				+ "et.cajas_pallet as cantidad, " 
				+ "pud.lote as lote " 
				+ "FROM etiqueta as et "
				+ "INNER JOIN purchase_order_detalle as pud ON pud.id = et.id_detalle_po "
				+ "INNER JOIN purchase_order as pu ON pu.id = pud.po_id " + "WHERE et.nro_etiqueta =:etiqueta ";
		return sessionFactory.getCurrentSession().createNativeQuery(hql, Ubicacion.class)
				.setParameter("etiqueta", etiqueta).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> consultarListaVinas() {
		String hql = "select DISTINCT owner_id from purchase_order";
		return (List<String>) sessionFactory.getCurrentSession().createNativeQuery(hql).list();
	}


	@Override
	public List<ConsultarEtiqueta> consultarEtiqueta(String etiqueta, String vina, String tipoDoc, String nroDoc) {
		
		Map<String, Object> parametros = new HashedMap();
		
		String hql = "SELECT   "
				+ "et.id as id,    "
				+ "et.nro_etiqueta as etiqueta,   "
				+ "pu.supplier_id as vina,    "
				+ "pu.product_description as producto,   "
				+ "et.pasillo as pasillo,    "
				+ "et.posicion as posicion,   "
				+ "et.altura as altura,   "
				+ "et.estado as estado_et,    "
				+ "pud.estado_carga as estado_pud,   "
				+ "pud.cantidad as cantidad,   "
				+ "pud.lote as lote,   "
				+ "pud.cod_producto as cod_producto,   "
				+ "pu.slipsheet as slipsheet,  "
				+ "et.cajas_pallet as cajas_pallet,   "
				+ "pu.bottles_per_unit as bot_caja,  "
				+ "pu.ml_per_bottle as cap_en_cc,   "
				+ "pud.cosecha as cosecha,  "
				+ "  pud.fecha_recepcion as recepcion,    "
				+ "  et.historial_repaletizado as repaletizado,  "
				+ "  pu.purchase_order_id as purchase_order_id,   "
				+ "  pud.id as id_pud, "
				+ "  pu.codigo_fert AS fert, "
				+ "  pud.fecha_recepcion as fecha_recepcion,"
				+ "  pu.owner_id as owner_id, "
				+ "  pud.numero_doc as nro_doc, "
				+ " pud.tipo_doc as tipo_doc, "
				+ " et.nro_etiqueta_ant as etiqueta_ant "
				+ "FROM etiqueta as et  "
				+ "INNER JOIN purchase_order_detalle as pud ON pud.id = et.id_detalle_po  "
				+ "INNER JOIN purchase_order as pu ON pu.id = pud.po_id  "
				+ "WHERE et.estado <> 'CANCELADA' ";
		
		if (etiqueta != null && !etiqueta.equals("")) {

			parametros.put("etiqueta", etiqueta);
			hql = hql + " AND et.nro_etiqueta =:etiqueta";

		} else {
			
			hql = hql + "AND et.estado IN ('ALMACENADA', 'RECEPCIONADA') ";

			if (vina != null && !vina.equals("")) {
				parametros.put("vina", vina);
				hql = hql + " AND pu.supplier_id =:vina ";
			}

			if (tipoDoc != null && !tipoDoc.equals("")) {
				parametros.put("tipoDoc", tipoDoc);
				hql = hql + " AND pud.tipo_doc = :tipoDoc ";
			}

			if (nroDoc != null && !nroDoc.equals("")) {
				parametros.put("nroDoc", nroDoc);
				hql = hql + " AND pud.numero_doc =:nroDoc ";
			}
		}
		
		return (List<ConsultarEtiqueta>) sessionFactory.getCurrentSession().createNativeQuery(hql, ConsultarEtiqueta.class)
				.setProperties(parametros).list();
	}

	

	

	@Override
	public void actualizarCantidadPODetalle(ConsultarEtiqueta etiqueta, String cantNueva) {
		String query = "UPDATE etiqueta SET cajas_pallet=:cantNueva where id=:id";
		sessionFactory.getCurrentSession().createNativeQuery(query)
				.setParameter("cantNueva", Integer.parseInt(cantNueva)).setParameter("id", etiqueta.getId())
				.executeUpdate();
	}

	@Override
	public List<Ubicacion> buscarEtiquetasAlmacenadas(String etiqueta) {
		String hql = "select  " 
				+ "			et.id as id," 
				+ "			et.id_detalle_po as id_detalle_po,"
				+ "			et.nro_etiqueta as etiqueta, " 
				+ "			pu.supplier_id as vina, "
				+ "			pu.product_description as producto, " 
				+ "			et.pasillo as pasillo, "
				+ "			et.posicion as posicion, " 
				+ "			et.altura as altura, "
				+ "			et.estado as estado_et, " 
				+ "			pud.estado as estado_pud, "
				+ "			pud.cantidad as cantidad," 
				+ "			pud.lote as lote" 
				+ "		FROM etiqueta as et"
				+ " INNER JOIN purchase_order_detalle as pud ON pud.id = et.id_detalle_po "
				+ " INNER JOIN purchase_order as pu ON pu.id = pud.po_id "
				+ "		WHERE et.nro_etiqueta =:etiqueta AND et.estado = 'ALMACENADA' AND pu.tipo_recep = 'AXIS' ";
		return (List<Ubicacion>) sessionFactory.getCurrentSession().createNativeQuery(hql, Ubicacion.class)
				.setParameter("etiqueta", etiqueta).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> consultarListaVinas(String valor) {

		String hql = "select DISTINCT owner_id from purchase_order WHERE owner_id LIKE :vina";
		return (List<String>) sessionFactory.getCurrentSession().createNativeQuery(hql)
				.setParameter("vina", valor + "%").list();

	}

	@Override
	public List<DespachoContenedor> buscarDatosDespacho(Integer idContenedor) {
		String query = "select  "
				+ "e.id as id, "
				+ "po.id as id_po, "
				+ "po.purchase_order_id as orden_referencia, "
				+ "po.owner_id as vina, "
				+ "po.product_description as producto, "
				+ "po.vintage as cosecha,  "
				+ "po.purchase_order_quantity as cajas_recepcionadas, "
				+ "e.cajas_pallet as cajas_consolidadas, "
				+ "e.fecha_cr as fecha_recepcion, "
				+ "pd.lote as lote, "
				+ "po.product_number as numero_producto, "
				+ "po.tipo_recep as tipo_recep, "
				+ "pd.numero_doc as num_doc, " 
				+ "pd.tipo_doc as tipo_doc, "
				+ "pd.fecha_doc as fecha_doc," 
				+ "e.nro_etiqueta as pallet "
				+ "from asignacion as asg "
				+ "inner join container as c on c.id =  asg.container_id "
				+ "inner join etiqueta as e on e.id = asg.etiqueta_id "
				+ "inner join purchase_order_detalle as pd on pd.id = e.id_detalle_po "
				+ "inner join purchase_order as po on po.id = pd.po_id "
				+ "where c.consolidado = 1 and c.estado = 'CONSOLIDADO' and c.id =:idContenedor and e.estado not in ( 'DESPACHADA', 'CANCELADA')";

		return sessionFactory.getCurrentSession().createNativeQuery(query, DespachoContenedor.class)
				.setParameter("idContenedor", idContenedor).list();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ResumenFacturacion> buscarResumenFacturacion(String nave, String viaje) {

		String hql = "SELECT " 
				+ "co.id AS id, " 
				+ "co.container AS container, " 
				+ "co.ship AS ship, "
				+ "co.voyage AS voyage, " 
				+ "co.puerto AS puerto, " 
				+ "co.estado AS estado, " 
				+ "co.sello AS sello, "
				+ "co.tara AS tara, " 
				+ "co.fecha_cr AS fecha_cr " 
				+ "FROM container AS co " 
				+ "WHERE co.ship = :nave "
				+ "AND co.voyage = :viaje "
				+ "AND co.fecha_embarque IS NOT NULL "
				+ "AND co.estado = 'DESPACHADO' ";

		return (List<ResumenFacturacion>) sessionFactory.getCurrentSession()
				.createNativeQuery(hql, ResumenFacturacion.class)
				.setParameter("nave", nave)
				.setParameter("viaje", viaje).list();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> autoCompletarNaves(String valor) {

		String hql = "select DISTINCT ship from container WHERE ship LIKE :nave";
		return (List<String>) sessionFactory.getCurrentSession().createNativeQuery(hql)
				.setParameter("nave", valor + "%").list();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> autoCompletarViajesNave(String viaje, String nave) {

		String hql = "select DISTINCT voyage from container WHERE ship =:nave AND voyage LIKE :viaje ";
		return (List<String>) sessionFactory.getCurrentSession().createNativeQuery(hql).setParameter("nave", nave)
				.setParameter("viaje", viaje + "%").list();
	}

	@Override
	public Map<Integer, String> listaContenedoresTarjado() {
		List<String> resultado = new ArrayList<String>();
		Map<Integer, String> tar = new HashMap<Integer, String>();
		String query = "select distinct(concat(c.id ,'/', c.container)) " + "from asignacion as asg "
				+ "inner join container as c on asg.container_id =  c.id "
				+ "where c.estado in ('ASIGNADO')";
		resultado = sessionFactory.getCurrentSession().createNativeQuery(query).list();

		if (!resultado.isEmpty() || resultado != null) {
			String idContenedor = null;
			Integer ide = null;
			String contenedor = null;
			for (String r : resultado) {
				idContenedor = r;
				ide = Integer.valueOf(idContenedor.split("/")[0]);
				contenedor = idContenedor.split("/")[1];
				tar.put(ide, contenedor);
			}
		} else {
			tar = new HashMap<Integer, String>();
		}
		return tar;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> autoCompletaEtiquetaAlmRepale(String valor) {
		String hql = "SELECT TOP 5 nro_etiqueta FROM etiqueta as et " + " WHERE et.estado = 'ALMACENADA' "
				+ " AND et.asignado = 0 " + " AND et.repaletizado = 0 " + " AND et.nro_etiqueta LIKE :etiqueta";
		return (List<String>) sessionFactory.getCurrentSession().createNativeQuery(hql)
				.setParameter("etiqueta", valor + "%").list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> autoCompletaEtiquetaRecepRepale(String valor) {
		String hql = "SELECT TOP 5 nro_etiqueta FROM etiqueta as et " + " WHERE et.estado = 'RECEPCIONADA' "
				+ " AND et.asignado = 0 " + " AND et.repaletizado = 0 " + " AND et.nro_etiqueta LIKE :etiqueta";
		return sessionFactory.getCurrentSession().createNativeQuery(hql).setParameter("etiqueta", valor + "%").list();
	}

	@Override
	public void actualizarContainer(Container cont) {
		sessionFactory.getCurrentSession().update(cont);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> etiquetasRecepcionadas() {
		String hql = "SELECT DISTINCT nro_etiqueta FROM etiqueta as et " + " WHERE et.estado = 'RECEPCIONADA' ";
		return sessionFactory.getCurrentSession().createNativeQuery(hql).list();
	}
	
	
	@Override
	public List<PickingTicket> buscarPt(String vinas, String productos){
		List<PickingTicket> listaPt =  new ArrayList<PickingTicket>();
		String query =  "select * from picking_ticket where owner_id in ( " + vinas + ") and product_number in ( " + productos + " ) and estado = 'DISPONIBLE'";
		listaPt = (List<PickingTicket>) sessionFactory.getCurrentSession().createNativeQuery(query, PickingTicket.class).list();
		return listaPt;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> autoCompletaEtiquetaSecunRep(String valor, String prod) {
		String hql = "SELECT nro_etiqueta FROM etiqueta as et " + 
				"	INNER JOIN purchase_order_detalle as pd ON pd.id = et.id_detalle_po" + 
				"	WHERE et.estado IN ('RECEPCIONADA', 'ALMACENADA')" + 
				"	AND pd.cod_producto =:prod" + 
				"	AND et.nro_etiqueta LIKE :etiqueta";
		return (List<String>) sessionFactory.getCurrentSession().createNativeQuery(hql)
				.setParameter("etiqueta", valor + "%")
				.setParameter("prod", prod)
				.list();
	}

	

	

	@SuppressWarnings("unchecked")
	@Override
	public List<String> consultarListaPOenAjustes(String valor) {
		String hql = "SELECT registro_po FROM ajuste_inventario WHERE registro_po LIKE :po";
		return (List<String>) sessionFactory.getCurrentSession().createNativeQuery(hql)
				.setParameter("po", valor + "%").list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> consultarListaPOS(String valor) {

		String hql = "SELECT DISTINCT po.purchase_order_id  FROM purchase_order AS po WHERE po.purchase_order_id LIKE :valor";
		return (List<String>) sessionFactory.getCurrentSession().createNativeQuery(hql)
				.setParameter("valor", valor + "%").list();

	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<String> autoCompletaContPacking(String contenedor) {
		String hql = "select c.container from container as c where c.estado in ('DESPACHADO', 'CONSOLIDADO') "
				+ " AND c.container LIKE :contenedor";
		return sessionFactory.getCurrentSession().createNativeQuery(hql).setParameter("contenedor", contenedor + "%")
				.list();
	}
	
	@Override
	public List<Container> listarContenedoresPacking(String contenedor) {
		String hql = "select * from container as c  WITH (NOLOCK) where container=:contenedor and "
				+ "estado in ('DESPACHADO', 'CONSOLIDADO')  ORDER BY c.estado";
		return sessionFactory.getCurrentSession().createNativeQuery(hql, Container.class).setParameter("contenedor", contenedor).list();
	}
	
	@Override
	public List<PackingList> listarPacking(Integer idCont) {
		String hql = "select ROW_NUMBER() over (order by asig.id) as id, "
				+ "asig.posicion as pos,  "
				+ "et.nro_etiqueta as'label',  "
				+ "po.owner_id as owner_id, "
				+ "pod.cod_producto as product_number,  "
				+ "pod.descripcion_producto as product_description,  "
				+ "pod.cosecha as vintage, "
				+ "et.cajas_pallet as cantidad "
				+ "from asignacion as asig  "
				+ "inner join etiqueta as et on asig.etiqueta_id = et.id "
				+ "inner join purchase_order_detalle as pod on et.id_detalle_po = pod.id "
				+ "inner join purchase_order as po on pod.po_id = po.id "
				+ "where asig.container_id =:idCont "
				+ "order by po.owner_id, asig.posicion";
		return sessionFactory.getCurrentSession().createNativeQuery(hql, PackingList.class).setParameter("idCont", idCont).list();
	}

	@Override
	public boolean validarExistenciaContenedor(String numeroContenedor) {
		String sql = "IF EXISTS (SELECT TOP 1 * FROM container AS c " + 
				"WHERE c.container = :cont " + 
				"AND c.estado IN ('DISPONIBLE', 'ASIGNADO', 'CONSOLIDADO')) " + 
				"SELECT 1 " + 
				"ELSE SELECT 0 ";
		Integer estado = (Integer) sessionFactory.getCurrentSession().createNativeQuery(sql)
				.setParameter("cont", numeroContenedor).uniqueResult();
		
		return ((estado==1) ? true : false);
	}
}
