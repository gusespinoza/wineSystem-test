# Estructura completa del proyecto

## 1. Informacion general

| Campo | Valor |
|---|---|
| Proyecto | `marvalWine` / `wineSystem` |
| Artefacto Maven | `org.marval:wineSystem:0.0.1-SNAPSHOT` |
| Empaquetado | WAR |
| Java | 1.8 |
| Framework web | JavaServer Faces 2.2, Mojarra 2.2.20, PrimeFaces 7.0 |
| Framework backend | Spring 4.3.18, Spring ORM, Spring MVC |
| Persistencia | Hibernate 5.4, SQL Server |
| Servidor objetivo | JBoss/WildFly |
| Vistas | XHTML |
| Tema | AdminFaces 1.1.0 |
| URL amigables | OCPsoft Rewrite/PrettyFaces 3.4.2 |
| Reportes | JasperReports 6.0.0 |

## 2. Arbol principal

```text
marvalWine/
|-- pom.xml
|-- hibernate.properties
|-- PROJECT_STRUCTURE.md
|-- .classpath
|-- .factorypath
|-- .project
|-- .tern-project
|-- .settings/
|-- .svn/
|-- src/
|   |-- main/
|   |   |-- java/
|   |   |-- resources/
|   |   `-- webapp/
|   |-- sql/
|   `-- test/
|       |-- java/
|       `-- resources/
|-- WebContent/
|   |-- index.xhtml
|   |-- login.xhtml
|   |-- META-INF/
|   |-- WEB-INF/
|   |-- includes/
|   |-- pages/
|   `-- resources/
`-- target/
```

## 3. Archivos de raiz

```text
marvalWine/
|-- pom.xml
|-- hibernate.properties
|-- PROJECT_STRUCTURE.md
|-- .classpath
|-- .factorypath
|-- .project
|-- .tern-project
|-- .settings/
|   |-- org.eclipse.core.resources.prefs
|   |-- org.eclipse.jdt.apt.core.prefs
|   |-- org.eclipse.jdt.core.prefs
|   |-- org.eclipse.jst.j2ee.ejb.annotations.xdoclet.prefs
|   |-- org.eclipse.ltk.core.refactoring.prefs
|   |-- org.eclipse.m2e.core.prefs
|   |-- org.eclipse.wst.common.component
|   |-- org.eclipse.wst.common.project.facet.core.prefs.xml
|   |-- org.eclipse.wst.common.project.facet.core.xml
|   |-- org.eclipse.wst.jsdt.ui.prefs
|   |-- org.eclipse.wst.jsdt.ui.superType.container
|   |-- org.eclipse.wst.jsdt.ui.superType.name
|   |-- org.eclipse.wst.validation.prefs
|   |-- org.eclipse.wst.ws.service.policy.prefs
|   |-- org.hibernate.eclipse.console.prefs
|   |-- org.springframework.ide.eclipse.core.prefs
|   `-- hibernateMapping/
|       `-- hibernate (1)
`-- .svn/
```

## 4. Codigo fuente Java

Ubicacion base: `src/main/java/com/marval/wine/`.

```text
src/main/java/com/marval/wine/
|-- bean/
|-- config/
|-- dao/
|-- entity/
|-- ftp/
|-- report/
|-- service/
`-- util/
```

### 4.1 Managed beans

Ubicacion: `src/main/java/com/marval/wine/bean/`

```text
bean/
|-- AjusteInventarioBean.java
|-- AsignacionBean.java
|-- BuscarEtiquetaBean.java
|-- CargaWineBean.java
|-- ConsolidadoBean.java
|-- ConstantesBean.java
|-- ContenedorBean.java
|-- DespachoBean.java
|-- ExistenciaCargaBean.java
|-- LoginBean.java
|-- MensajeBodegaBean.java
|-- MensajesProcesadosBean.java
|-- PackingListBean.java
|-- PtDisponiblesBean.java
|-- RecepcionBean.java
|-- RecepcionManualBean.java
|-- RepaletizadoBean.java
|-- ResumenFacturacionBean.java
|-- ScheduledTaskBean.java
|-- TipoRecepcionBean.java
`-- UbicacionBean.java
```

### 4.2 Configuracion

Ubicacion: `src/main/java/com/marval/wine/config/`

```text
config/
|-- AppConfig.java
`-- WebAppInitializer.java
```

`AppConfig` configura el datasource SQL Server, Hibernate, transacciones, componentes Spring, correo SMTP, FTP/SFTP y el executor de tareas. `WebAppInitializer` registra el contexto web de Spring.

### 4.3 DAO

Ubicacion: `src/main/java/com/marval/wine/dao/`

```text
dao/
|-- AjusteInventarioDao.java
|-- AjusteInventarioDaoImpl.java
|-- AsignacionDao.java
|-- AsignacionDaoImpl.java
|-- ContainerDao.java
|-- ContainerDaoImpl.java
|-- DetalleRecepcionDao.java
|-- DetalleRecepcionDaoImpl.java
|-- EtiquetaDao.java
|-- EtiquetaDaoImpl.java
|-- ExistenciaDao.java
|-- ExistenciaDaoImpl.java
|-- IndicadoresDao.java
|-- IndicadoresDaoImpl.java
|-- MensajesDao.java
|-- MensajesDaoImpl.java
|-- PickingTicketDao.java
|-- PickingTicketDaoImpl.java
|-- PurchaseOrderDao.java
|-- PurchaseOrderDaoImpl.java
|-- RepaletizadoDao.java
|-- RepaletizadoDaoImpl.java
|-- ResumenFacturacionDao.java
|-- ResumenFacturacionDaoImpl.java
|-- UbicacionDao.java
`-- UbicacionDaoImpl.java
```

Cada implementacion realiza el acceso a datos asociado al contrato DAO de su modulo.

### 4.4 Entidades

Ubicacion: `src/main/java/com/marval/wine/entity/`

```text
entity/
|-- AjusteInventario.java
|-- Asignacion.java
|-- ConsultarEtiqueta.java
|-- Container.java
|-- DespachoContenedor.java
|-- DetalleInventario.java
|-- Dolar.java
|-- Etiqueta.java
|-- Existencia.java
|-- HistorialIndicadores.java
|-- IndicadoresFinancieros.java
|-- IndicadoresFinancierosUF.java
|-- Mensaje.java
|-- MensajeBodega.java
|-- MensajeProcesado.java
|-- OrdenFacturada.java
|-- PackingList.java
|-- ParametrosDetalle.java
|-- PickingTicket.java
|-- PoAjuste.java
|-- PoAxis.java
|-- PtDisponible.java
|-- PurchaseOrder.java
|-- PurchaseOrderDetalle.java
|-- RecepcionManual.java
|-- Repaletizado.java
|-- ResumenContenedor.java
|-- ResumenFacturacion.java
|-- ResumenNaveViaje.java
|-- UF.java
`-- Ubicacion.java
```

### 4.5 FTP/SFTP

Ubicacion: `src/main/java/com/marval/wine/ftp/`

```text
ftp/
|-- ConectaFtp.java
`-- ConectaSFTP.java
```

### 4.6 Reportes

Ubicacion: `src/main/java/com/marval/wine/report/`

```text
report/
`-- ReporteExcel.java
```

### 4.7 Servicios

Ubicacion: `src/main/java/com/marval/wine/service/`

```text
service/
|-- AjusteInventarioService.java
|-- AjusteInventarioServiceImpl.java
|-- AsignacionService.java
|-- AsignacionServiceImpl.java
|-- ContainerService.java
|-- ContainerServiceImpl.java
|-- DetalleRecepcionService.java
|-- DetalleRecepcionServiceImpl.java
|-- EtiquetaService.java
|-- EtiquetaServiceImpl.java
|-- ExistenciaService.java
|-- ExistenciaServiceImpl.java
|-- IndicadoresService.java
|-- IndicadoresServiceImpl.java
|-- MensajesService.java
|-- MensajesServiceImpl.java
|-- PickingTicketService.java
|-- PickingTicketServiceImpl.java
|-- PurchaseOrderService.java
|-- PurchaseOrderServiceImpl.java
|-- RepaletizadoService.java
|-- RepaletizadoServiceImpl.java
|-- ResumenFacturacionService.java
|-- ResumenFacturacionServiceImpl.java
|-- UbicacionService.java
`-- UbicacionServiceImpl.java
```

### 4.8 Utilidades

Ubicacion: `src/main/java/com/marval/wine/util/`

```text
util/
|-- Constantes.java
`-- Util.java
```

## 5. Recursos principales

Ubicacion: `src/main/resources/`

```text
src/main/resources/
|-- SEG_USUMARVAL-20180904.jar
|-- admin-config.properties
|-- log4j.properties
|-- wine-config.properties
`-- jasper/
    |-- Plantilla_tarja_desconsolidado.jrxml
    |-- sub_reporte_tarja_desconsolidado.jasper
    `-- sub_reporte_tarja_desconsolidado.jrxml
```

La aplicacion web principal de Maven contiene ademas:

```text
src/main/webapp/
`-- monitor.xhtml
```

## 6. Estructura WebContent

### 6.1 Raiz web

```text
WebContent/
|-- index.xhtml
|-- login.xhtml
|-- META-INF/
|-- WEB-INF/
|-- includes/
|-- pages/
`-- resources/
```

### 6.2 META-INF

```text
WebContent/META-INF/
|-- MANIFEST.MF
|-- admin.xhtml
`-- context.xml
```

### 6.3 WEB-INF

```text
WebContent/WEB-INF/
|-- .faces-config.xml.jsfdia
|-- faces-config.xml
|-- hibernate.cfg.xml
|-- jboss-web.xml
|-- pretty-config.xml
|-- web.xml
|-- lib/
|   `-- SEG_USUMARVAL-20240416.jar
|-- resources/
|   |-- css/
|   |   |-- starter.css
|   |   `-- styles.css
|   `-- js/
|       |-- 3dAep.js
|       |-- aepJS.js
|       |-- calendar_es.js
|       |-- push.min.js
|       |-- sweetalert2.all.min.js
|       `-- three.js
`-- templates/
    |-- template.xhtml
    `-- template-top.xhtml
```

### 6.4 Includes

```text
WebContent/includes/
|-- menu-search.xhtml
|-- menu.xhtml
`-- top-menu.xhtml
```

### 6.5 Paginas funcionales

```text
WebContent/pages/
|-- asignacion/
|   `-- asignacion.xhtml
|-- cargaWine/
|   `-- cargaWine.xhtml
|-- consolidado/
|   |-- consolidado.xhtml
|   |-- packingList.xhtml
|   `-- tarja.xhtml
|-- consultaretiqueta/
|   `-- consultaretiqueta.xhtml
|-- contenedor/
|   `-- cargaContenedores.xhtml
|-- despacho/
|   |-- despacho.xhtml
|   |-- listaContenedorDespacho.xhtml
|   `-- ptDisponibles.xhtml
|-- existenciacarga/
|   `-- existenciacarga.xhtml
|-- manifiesto/
|   |-- manifiesto.xhtml
|   |-- manual.xhtml
|   `-- manual/
|       |-- encabezado.xhtml
|       `-- participantes.xhtml
|-- mensajesprocesados/
|   |-- mensajeBodega.xhtml
|   `-- mensajesprocesados.xhtml
|-- recepcion/
|   |-- recepcionAxis.xhtml
|   |-- recepcionManual.xhtml
|   `-- tipoRecepcion.xhtml
|-- repaletizado/
|   `-- repaletizado.xhtml
|-- resumenfacturacion/
|   `-- resumenfacturacion.xhtml
`-- ubicacion/
    |-- ajustarinventario.xhtml
    |-- asignarubicacion.xhtml
    `-- reubicacion.xhtml
```

### 6.6 Recursos publicos

```text
WebContent/resources/
|-- css/
|   `-- styles.css
|-- img/
|   |-- DSC00905.JPG
|   |-- Seaport.jpg
|   |-- Texval.jpg
|   |-- marval.ico
|   |-- marval.png
|   `-- video-corp-01.mp4
`-- js/
    |-- aepJS.js
    |-- push.min.js
    `-- sweetalert2.all.min.js
```

## 7. Rutas PrettyFaces

Las rutas publicas se encuentran en `WebContent/WEB-INF/pretty-config.xml`.

| URL | Vista |
|---|---|
| `/` | `login.xhtml` |
| `/inicio` | `index.xhtml` |
| `/reubicacion` | `pages/ubicacion/reubicacion.xhtml` |
| `/asigubicacion` | `pages/ubicacion/asignarubicacion.xhtml` |
| `/recepcion` | `pages/recepcion/tipoRecepcion.xhtml` |
| `/recepcionAxis` | `pages/recepcion/recepcionAxis.xhtml` |
| `/recepcionManual` | `pages/recepcion/recepcionManual.xhtml` |
| `/cargaContenedor` | `pages/contenedor/cargaContenedores.xhtml` |
| `/asignacion` | `pages/asignacion/asignacion.xhtml` |
| `/tarja` | `pages/consolidado/tarja.xhtml` |
| `/consolidado` | `pages/consolidado/consolidado.xhtml` |
| `/buscarEtiqueta` | `pages/consultaretiqueta/consultaretiqueta.xhtml` |
| `/ajusteInventario` | `pages/ubicacion/ajustarinventario.xhtml` |
| `/existenciaCarga` | `pages/existenciacarga/existenciacarga.xhtml` |
| `/repaletizado` | `pages/repaletizado/repaletizado.xhtml` |
| `/resumenFacturacion` | `pages/resumenfacturacion/resumenfacturacion.xhtml` |
| `/mensajesProcesados` | `pages/mensajesprocesados/mensajesprocesados.xhtml` |
| `/mensajeBodega` | `pages/mensajesprocesados/mensajeBodega.xhtml` |
| `/despacho` | `pages/despacho/despacho.xhtml` |
| `/ptDisponibles` | `pages/despacho/ptDisponibles.xhtml` |
| `/contenedor` | `pages/despacho/listaContenedorDespacho.xhtml` |
| `/packingList` | `pages/consolidado/packingList.xhtml` |
| `/cargaWine` | `pages/cargaWine/cargaWine.xhtml` |

## 8. Scripts SQL

Ubicacion: `src/sql/`

```text
src/sql/
|-- wine.sql
|-- wine_FK.sql
|-- wine_seq.sql
`-- wine_sp.sql
```

| Archivo | Contenido esperado |
|---|---|
| `wine.sql` | Definicion y datos principales de la base |
| `wine_FK.sql` | Restricciones de llaves foraneas |
| `wine_seq.sql` | Secuencias de base de datos |
| `wine_sp.sql` | Procedimientos almacenados |

## 9. Pruebas

```text
src/test/
|-- java/
`-- resources/
```

Actualmente las carpetas existen, pero no contienen archivos Java o recursos detectados en el estado actual del proyecto.

## 10. Maven y dependencias relevantes

El empaquetado se controla desde `pom.xml` mediante `maven-compiler-plugin` y `maven-war-plugin`.

### Framework y plataforma

- Java EE Servlet API 3.1.0
- JSF/Mojarra 2.2.20
- PrimeFaces 7.0 y PrimeFaces Extensions 7.0.3
- AdminFaces 1.1.0
- Omnifaces 2.6.8
- Rewrite/PrettyFaces 3.4.2
- Spring 4.3.18
- Spring Integration SFTP 5.5.17
- JSch 2.27.2

### Persistencia

- Hibernate Core 5.4.0
- SQL Server JDBC 6.4.0.jre8
- Spring Data JPA 2.1.6
- Eclipse Persistence API 2.2.1

### Reportes y documentos

- JasperReports 6.0.0
- Apache POI 3.17
- JExcelAPI 2.6.12
- QRGen 1.4
- Barcode4J 2.0

### Integraciones y utilidades

- Apache Axis 1.4 y WSDL4J
- JavaMail 1.4.7
- Commons Net 3.6
- Gson 2.8.5
- Jackson
- Lombok 1.16.14
- Log4j 2.17.0
- SLF4J 1.7.32
- JUnit 4.12

## 11. Configuracion del despliegue

| Archivo | Proposito |
|---|---|
| `pom.xml` | Dependencias, Java 1.8 y construccion WAR |
| `WebContent/WEB-INF/web.xml` | Descriptor web, FacesServlet, tema PrimeFaces y sesion |
| `WebContent/WEB-INF/faces-config.xml` | Integracion JSF/Spring |
| `WebContent/WEB-INF/hibernate.cfg.xml` | Configuracion Hibernate del descriptor web |
| `WebContent/WEB-INF/pretty-config.xml` | Rutas amigables |
| `WebContent/WEB-INF/jboss-web.xml` | Configuracion de contexto JBoss/WildFly |
| `WebContent/META-INF/context.xml` | Contexto de la aplicacion |
| `src/main/java/com/marval/wine/config/AppConfig.java` | Configuracion programatica de Spring |
| `src/main/java/com/marval/wine/config/WebAppInitializer.java` | Inicializador programatico |
| `src/main/resources/wine-config.properties` | Parametros funcionales |
| `src/main/resources/admin-config.properties` | Parametros administrativos |
| `src/main/resources/log4j.properties` | Configuracion de logging |
| `hibernate.properties` | Propiedades locales de Hibernate |

Las credenciales de bases de datos, FTP y correo deben mantenerse fuera del control de versiones o sustituirse por variables/configuracion segura en el entorno de despliegue.

## 12. Arquitectura logica

```text
XHTML / JSF / PrimeFaces
            |
      Managed Beans
            |
         Servicios
            |
            DAO
            |
   Hibernate / SQL Server

Integraciones adicionales:
|-- JasperReports
|-- Apache POI / JExcelAPI
|-- FTP y SFTP
|-- Apache Axis
`-- JavaMail SMTP
```

## 13. Salida generada

La carpeta `target/` contiene artefactos generados por Maven/Eclipse. En el estado actual inspeccionado solo incluye recursos desplegados por `m2e-wtp`:

```text
target/
`-- m2e-wtp/
    `-- web-resources/
        `-- META-INF/
            |-- MANIFEST.MF
            `-- maven/
                `-- org.marval/
                    `-- wineSystem/
                        |-- pom.properties
                        `-- pom.xml
```
