package com.marval.wine.config;

import java.sql.Connection;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.marval.wine.ftp.ConectaFtp;
import com.marval.wine.ftp.ConectaSFTP;
import com.marval.wine.entity.AjusteInventario;
import com.marval.wine.entity.Asignacion;
import com.marval.wine.entity.ConsultarEtiqueta;
import com.marval.wine.entity.Container;
import com.marval.wine.entity.DespachoContenedor;
import com.marval.wine.entity.Etiqueta;
import com.marval.wine.entity.Existencia;
import com.marval.wine.entity.HistorialIndicadores;
import com.marval.wine.entity.Mensaje;
import com.marval.wine.entity.MensajeBodega;
import com.marval.wine.entity.MensajeProcesado;
import com.marval.wine.entity.PackingList;
import com.marval.wine.entity.ParametrosDetalle;
import com.marval.wine.entity.PickingTicket;
import com.marval.wine.entity.PoAjuste;
import com.marval.wine.entity.PoAxis;
import com.marval.wine.entity.PtDisponible;
import com.marval.wine.entity.PurchaseOrder;
import com.marval.wine.entity.PurchaseOrderDetalle;
import com.marval.wine.entity.Repaletizado;
import com.marval.wine.entity.ResumenContenedor;
import com.marval.wine.entity.ResumenFacturacion;
import com.marval.wine.entity.Ubicacion;
import com.marval.wine.util.Util;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

@Configuration
@EnableTransactionManagement
@ComponentScans(value = { @ComponentScan("com.marval.wine.dao"), @ComponentScan("com.marval.wine.service"),
		@ComponentScan("com.marval.wine.entity"), @ComponentScan("com.marval.wine.config"),
		@ComponentScan("com.marval.wine.bean"), @ComponentScan("com.marval.wine.ftp"),
		@ComponentScan("com.github.adminfaces.template") })

/**
 * Clase encargada de configurar conexiones de base de datos.
 * 
 * @author marval.
 * @version 1.0.
 */
@PropertySource("file:C:\\\\wildfly\\standalone\\data\\bd_wine.properties")
public class AppConfig {
	/**
	 * Obtiene datasource para realizar conexión a BD.
	 * 
	 * 
	 * @return DataSource.
	 */
	public final static String CATALOGO_WINE = "wine";

	@Value("${db.wine.url}")
	String URL_WINE;
	@Value("${db.wine.user}")
	String USER_WINE;
	@Value("${db.wine.pass}")
	String PASS_WINE;

	@Value("${db.ftp.server}")
	String SERVER_FTP;
	@Value("${db.ftp.user}")
	String USER_FTP;
	@Value("${db.ftp.pass}")
	String PASS_FTP;
	@Value("${db.ftp.carpeta}")
	String FOLDER_FTP;
	@Value("${db.ftp.img}")
	String IMG_FTP;

	@Bean
	public DataSource getDataSource() {
		SQLServerDataSource dataSource = new SQLServerDataSource();
		String test = URL_WINE + CATALOGO_WINE;

		dataSource.setURL(test);

		dataSource.setUser(USER_WINE);
		dataSource.setPassword(PASS_WINE);

		return dataSource;
	}

	/**
	 * crea objeto de sesión para conectar a BD.
	 * 
	 * @return LocalSessionFactoryBean.
	 * @version 1.0.
	 * @since 1.0.
	 */
	@Bean
	@Primary
	public LocalSessionFactoryBean getSessionFactory() {
		LocalSessionFactoryBean factoryBean = new LocalSessionFactoryBean();
		factoryBean.setDataSource(getDataSource());

		Properties props = new Properties();
		props.put("hibernate.show_sql", "false");
		props.put("hibernate.dialect", "org.hibernate.dialect.SQLServer2012Dialect");
		props.put("hibernate.id.new_generator_mappings", "false");
		props.put("hibernate.connection.driver_class", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
		props.put("hibernate.connection.autocommit", "true");
		props.put("hibernate.connection.pool_size", "100");
		props.put("hibernate.connection.isolation", String.valueOf(Connection.TRANSACTION_READ_UNCOMMITTED));
		factoryBean.setHibernateProperties(props);

		/* Se agregan clases bean para trabajar con hibernate */
		factoryBean.setAnnotatedClasses(Util.class, PickingTicket.class, PurchaseOrder.class,  
				PurchaseOrderDetalle.class,Container.class, Etiqueta.class, ConsultarEtiqueta.class, 
				Ubicacion.class, AjusteInventario.class, Existencia.class, Repaletizado.class,
				HistorialIndicadores.class, ResumenFacturacion.class, Asignacion.class, DespachoContenedor.class,
				PoAjuste.class, ResumenContenedor.class, MensajeProcesado.class, Mensaje.class, ParametrosDetalle.class, 
				MensajeBodega.class, PackingList.class, PoAxis.class, PtDisponible.class);

		return factoryBean;
	}

	/**
	 * @return HibernateTransactionManager.
	 */
	@Bean
	public HibernateTransactionManager getTransactionManager() {
		HibernateTransactionManager transactionManager = new HibernateTransactionManager();
		transactionManager.setSessionFactory(getSessionFactory().getObject());
		return transactionManager;
	}

	/**
	 * Mail sender.
	 * 
	 * @return JavaMailSender.
	 */
	@Bean
	public JavaMailSender getMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

		mailSender.setHost("smtp.gmail.com");
		mailSender.setPort(587);
		mailSender.setUsername("marval.tarja@gmail.com");
		mailSender.setPassword("Marval49");
		// mailSender.setPassword("*****");
		Properties javaMailProperties = new Properties();
		javaMailProperties.put("mail.smtp.starttls.enable", "true");
		javaMailProperties.put("mail.smtp.auth", "true");
		javaMailProperties.put("mail.transport.protocol", "smtp");
		javaMailProperties.put("mail.debug", "false");

		mailSender.setJavaMailProperties(javaMailProperties);
		return mailSender;
	}

	@Bean
	public ConectaFtp conectaFtpLocal() {
		ConectaFtp ftp = new ConectaFtp(SERVER_FTP, USER_FTP, PASS_FTP, FOLDER_FTP, IMG_FTP);
		return ftp;
	}
	
	@Bean
	public ConectaSFTP conectaSFtpLocal() {
		ConectaSFTP ftp = new ConectaSFTP(SERVER_FTP, USER_FTP, PASS_FTP, FOLDER_FTP, IMG_FTP);
		return ftp;
	}
	
	@Bean
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5);
		executor.setMaxPoolSize(10);
		executor.setQueueCapacity(25);
		executor.setKeepAliveSeconds(60);
		return executor;
	}

}