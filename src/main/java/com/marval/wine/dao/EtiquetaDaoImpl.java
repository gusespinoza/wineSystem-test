package com.marval.wine.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.marval.wine.entity.Etiqueta;

@Repository
public class EtiquetaDaoImpl implements EtiquetaDao {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public void crearEtiqueta(Etiqueta etiqueta) {
		sessionFactory.getCurrentSession().persist(etiqueta);
		sessionFactory.getCurrentSession().flush();
		sessionFactory.getCurrentSession().clear();
	}
	
	@Override
	public void cancelarEtiqueta(Integer idDetalle) {
		String sql = "update etiqueta set estado = 'CANCELADA' where id_detalle_po =:idDetalle";
		sessionFactory.getCurrentSession().createNativeQuery(sql).setParameter("idDetalle", idDetalle).executeUpdate();
	}
	
	@Override
	public void despacharEtiqueta(Integer idDetalle) {
		String sql = "update etiqueta set estado = 'DESPACHADA' where id =:idDetalle";
		sessionFactory.getCurrentSession().createNativeQuery(sql).setParameter("idDetalle", idDetalle).executeUpdate();
	}
	
	@Override
	public void actualizarEtiquetaAsignacion(Integer id, String contendor) {
		String sql = "update etiqueta set asignado = 1, nro_contenedor =:contenedor where id =:id";
		sessionFactory.getCurrentSession().createNativeQuery(sql).setParameter("id", id).setParameter("contenedor", contendor).executeUpdate();
	}
	
	@Override
	public String obtenerEtiqueta() {
		String sql = "select CONCAT(YEAR(SYSDATETIME ( )),'-',next value for dbo.seq_num_etiqueta)";
		String etiqueta = (String) sessionFactory.getCurrentSession().createNativeQuery(sql).uniqueResult();
		return etiqueta;
	}
	
	@Override
	public List<Etiqueta> resultadoBusquedaEtiquetas(String lote, String po, String fert){
		List<Etiqueta> resultado =  new ArrayList<Etiqueta>();
		String query =  "select *  from etiqueta as e "
				+ "inner join purchase_order_detalle as pod on pod.id =  e.id_detalle_po "
				+ "inner join purchase_order as po on po.id =  pod.po_id "
				+ "where (pod.lote =:lote or po.purchase_order_id =:po or po.codigo_fert =:fert) and e.estado in ('RECEPCIONADA', 'ALMACENADA') "
				+ "order by e.asignado ";
		resultado =  sessionFactory.getCurrentSession().createNativeQuery(query, Etiqueta.class).setParameter("lote", lote).setParameter("po", po).setParameter("fert", fert).list();
		return resultado;
	}

	@Override
	public Etiqueta buscarEtiqueta(String etiqueta) {
		
		String query =  "select TOP 1 *  from etiqueta as e "
				+ "where e.nro_etiqueta =:etiqueta ";
		return (Etiqueta) sessionFactory.getCurrentSession().createNativeQuery(query, Etiqueta.class)
				.setParameter("etiqueta", etiqueta).uniqueResult();
	}

	@Override
	public void actualizarEtiquetaRepaletizada(Etiqueta prima) {
		String sql = "update etiqueta set repaletizado = 1, usuario_up=:usuarioup, fecha_up =:fechaup, estado=:estado  where nro_etiqueta =:etiqueta";
		sessionFactory.getCurrentSession().createNativeQuery(sql)
			.setParameter("etiqueta", prima.getNroEtiqueta())
			.setParameter("usuarioup", prima.getUsuarioUp())
			.setParameter("fechaup", prima.getFechaUp())
			.setParameter("estado", prima.getEstado())
			.executeUpdate();
	}
	
}
