# Inventario de desacople

Este documento registra las relaciones entre pantallas, managed beans, servicios, DAO, entidades y dependencias externas antes de migrar `wineSystem` a Angular + Go.

Para actualizarlo, analice un módulo a la vez y complete sus relaciones, inventario y preguntas abiertas. Marque la fila del índice con su estado correspondiente y registre responsable y fecha.

## Status legend

- 🟢 not started
- 🟡 in progress
- ✅ migrated
- 🔴 blocked

## Module index

| Module | Status | Owner | Last updated |
|---|---|---|---|
| asignacion | 🟡 in progress | covered by Ubicacion section | 2026-11-25 |
| recepcion | 🟡 in progress | TBD | 2026-11-25 |
| consolidado | 🟡 in progress | TBD | 2026-11-25 |
| despacho | 🟡 in progress | TBD | 2026-11-25 |
| ubicacion | 🟡 in progress | TBD | 2026-11-25 |
| manifiesto | 🟡 in progress | TBD | 2026-11-25 |
| mensajes | 🟡 in progress | TBD | 2026-11-25 |
| existencia | 🟡 in progress | TBD | 2026-11-25 |
| repaletizado | 🟡 in progress | TBD | 2026-11-25 |
| facturacion | 🟡 in progress | TBD | 2026-11-25 |
| cargaWine | 🟡 in progress | TBD | 2026-11-25 |
| login | 🟡 in progress | TBD | 2026-11-25 |
| contenedor | 🟡 in progress | TBD | 2026-11-25 |
| manifiesto | 🔴 blocked | TBD | 2026-11-25 | beans not in source tree |

## Mensajes Procesados

### Relations

- **Bean:** `MensajesProcesadosBean`
- **XHTML:** `mensajesprocesados.xhtml`; `mensajeBodega.xhtml` pertenece a `MensajeBodegaBean`
- **Route:** `/mensajesProcesados`; `/mensajeBodega`
- **Notes:** El módulo contiene consulta y descarga de mensajes procesados. `mensajeBodega.xhtml` no usa `MensajesProcesadosBean`.

### Inventory

| XHTML Screen | Managed Bean | Action Method | Service Used | DAO/SQL | Entity | Session State | Validations | Reports/FTP | Cross-Module Dependency |
|---|---|---|---|---|---|---|---|---|---|
| `mensajesprocesados.xhtml` | `MensajesProcesadosBean` | `onLoad()` | N/A | N/A | N/A | `@SessionScope`; limpia filtros y resultados | Verifica `loginBean.isMenuMensajes()`; redirige si no tiene permiso | N/A | `LoginBean` |
| `mensajesprocesados.xhtml` | `MensajesProcesadosBean` | `autoCompletarVinas(valor)` | `ContainerService.consultarListaVinas(valor)` | SQL sobre `purchase_order.owner_id` | Datos escalares `String` | Limpia `listaVinas` | Captura excepciones y devuelve `null` | N/A | `ContainerService` |
| `mensajesprocesados.xhtml` | `MensajesProcesadosBean` | `autocompletarPO(valor)` | `ContainerService.consultarListaPOenAjustes(valor)` | SQL sobre `ajuste_inventario.registro_po` | Datos escalares `String` | Usa `listaVinas` como lista temporal | Captura excepciones y devuelve `null` | N/A | Ajuste Inventario |
| `mensajesprocesados.xhtml` | `MensajesProcesadosBean` | `buscarMensajes()` | `MensajesService.buscarMensajesProcesados(...)` | SQL nativo sobre `mensaje` | `MensajeProcesado` | Reemplaza `listaMensajes` | Requiere PO, viña o fecha desde | N/A | `MensajesService` → `MensajesDao` |
| `mensajesprocesados.xhtml` | `MensajesProcesadosBean` | `descargarTXT(MensajeProcesado mens)` | N/A | N/A | `MensajeProcesado` | Genera `streamedContent` | Captura excepciones y muestra SweetAlert | Descarga TXT con UTF-8 | `PrimeFaces.fileDownload` |

### Open questions

- Revisar `MensajesDaoImpl.java`: el filtro de viña no forma parte de la consulta SQL actual.
- Revisar `MensajesDaoImpl.java`: el filtro de PO no forma parte de la consulta SQL actual.
- Revisar `MensajesProcesadosBean.java`: `ConectaFtp` está inyectado, pero `descargarTXT()` descarga directamente desde `MensajeProcesado.texto`.
- Revisar `MensajesProcesadosBean.java`: confirmar si el acceso desde PO debe reutilizarse o eliminarse como dependencia.

## Asignacion

| XHTML Screen | Managed Bean | Action Method | Service Used | DAO/SQL | Entity | Session State | Validations | Reports/FTP | Cross-Module Dependency |
|---|---|---|---|---|---|---|---|---|---|

## Recepcion

### Relations

- **Beans:** `RecepcionBean`; `RecepcionManualBean`; `TipoRecepcionBean`
- **XHTML:** `pages/recepcion/tipoRecepcion.xhtml`; `pages/recepcion/recepcionAxis.xhtml`; `pages/recepcion/recepcionManual.xhtml`
- **Route:** `/recepcion`; `/recepcionAxis`; `/recepcionManual`
- **Notes:** Axis reception loads pre-created POs and creates GOOD RECEIPT output; manual reception creates POs, details and labels interactively.

### Inventory

| XHTML Screen | Managed Bean | Action Method | Service Used | DAO/SQL | Entity | Session State | Validations | Reports/FTP | Cross-Module Dependency |
|---|---|---|---|---|---|---|---|---|---|
| `tipoRecepcion.xhtml` | `TipoRecepcionBean` | `onLoad()` preRenderView | N/A | N/A | N/A | `@SessionScope`; resets reception mode | Verifies `loginBean.isMenuRecepcion()` | N/A | Axis/Manual UI |
| `tipoRecepcion.xhtml` | `TipoRecepcionBean` | `cambairValor()` valueChangeListener | N/A | N/A | N/A | Updates selected reception mode | N/A | N/A | RecepcionBean/RecepcionManualBean |
| `recepcionAxis.xhtml` | `RecepcionBean` | `onLoad()` preRenderView | N/A | N/A | N/A | Clears PO/detail/filter state | Verifies `loginBean.isMenuRecepcion()` | N/A | CargaWine preloaded PO |
| `recepcionAxis.xhtml` | `RecepcionBean` | `limpiarFiltros()` public helper | N/A | N/A | `PurchaseOrder`; `PoAxis` | Clears current reception state | N/A | N/A | CargaWine/Contenedor |
| `recepcionAxis.xhtml` | `RecepcionBean` | `buscarPo()` actionListener | `PurchaseOrderService.listPo(vina)` | Native/DAO PO SELECT for state and winery | `PoAxis`; `PurchaseOrder` | Replaces available AXIS PO lists | Winery mandatory | N/A | CargaWine preloaded PO |
| `recepcionAxis.xhtml` | `RecepcionBean` | `cargaRecepcion(Integer id)` actionListener | `PurchaseOrderService.buscarPo(...)`; `DetalleRecepcionService.listarDetalle(...)` | SELECT PO and details | `PurchaseOrder`; `PurchaseOrderDetalle` | Loads selected PO/details | N/A | N/A | CargaWine/Contenedor |
| `recepcionAxis.xhtml` | `RecepcionBean` | `agregarRecepcion()` action | `PurchaseOrderService.updatePo(...)` | UPDATE PO and cascade PO/details/labels | `PurchaseOrder`; `PurchaseOrderDetalle`; `Etiqueta` | Adds partial/total detail and labels | Total quantity/details and selected state | Creates generated labels/QR; no SOAP call found | WRITES RECEPCIONADA; Ubicacion/ConsultaEtiqueta/Consolidado |
| `recepcionAxis.xhtml` | `RecepcionBean` | `crearEtiqueta()` public helper | `EtiquetaService.obtenerEtiqueta()` | Sequence `seq_num_etiqueta`; persisting via PO cascade | `Etiqueta` | Creates one label per pallet | Uses pallet count/cases per pallet | QR data generated in memory | WRITES etiqueta state RECEPCIONADA |
| `recepcionAxis.xhtml` | `RecepcionBean` | `quitarRecepcion(PurchaseOrderDetalle)` actionListener | `EtiquetaService.cancelarEtiqueta(...)`; `DetalleRecepcionService.eliminarDetalle(...)` | UPDATE PO detail CANCELADO; update/cancel labels | `PurchaseOrderDetalle`; `Etiqueta` | Removes detail and recalculates | N/A | N/A | Ubicacion/ConsultaEtiqueta |
| `recepcionAxis.xhtml` | `RecepcionBean` | `guardarRecepcion()` action | `PurchaseOrderService.updatePo(...)`; `crearGoodReceipt(...)` | UPDATE PO state/received quantity | `PurchaseOrder`; `PurchaseOrderDetalle` | Finalizes reception and reloads PO | Requires at least one detail | SFTP receipt file for total partial flag; SOAP/Axis direct call not found | CargaWine/Contenedor/Ubicacion |
| `recepcionAxis.xhtml` | `RecepcionBean` | `crearGoodReceipt(PurchaseOrder)` public helper | `AjusteInventarioService.secuenciaSPRC()`; `ConectaSFTP`; `PurchaseOrderService.guardarMensaje(...)` | Sequence `seq_receip_sprc`; save receipt message | `Mensaje`; `PurchaseOrder` | Builds SPCR receipt text | Executes only from total reception branch | SFTP upload `SPCR*.txt`; file/message | Dependencies: CargaWine PO and wine_sp.sql sequence |
| `recepcionAxis.xhtml` | `RecepcionBean` | `impEtiquetas(List<Etiqueta>)` / `generarQr(...)` action/helper | N/A | N/A | `Etiqueta` | Builds QR label selection | N/A | PrimeFaces QR/barcode | ConsultaEtiqueta/Ubicacion |
| `recepcionManual.xhtml` | `RecepcionManualBean` | `onLoad()` preRenderView | `PurchaseOrderService.listPoManual()` | SELECT manual POs | `PurchaseOrder` | Clears/init manual reception state | Verifies `loginBean.isMenuRecepcionManual()` | N/A | CargaWine/Recepcion |
| `recepcionManual.xhtml` | `RecepcionManualBean` | `limpiarFiltros()` public helper | `ContainerService.consultarListaVinas()` | SELECT/list winery values | `PurchaseOrder`; `PurchaseOrderDetalle` | Clears selected PO/detail | N/A | N/A | Contenedor |
| `recepcionManual.xhtml` | `RecepcionManualBean` | `agregarPo()` action | `PurchaseOrderService.guardarPo(...)` | INSERT manual PO | `PurchaseOrder` | Creates MANUAL PO and reloads list | Sets supplier/warehouse/interface fields | N/A | CargaWine equivalent/Contenedor |
| `recepcionManual.xhtml` | `RecepcionManualBean` | `agregarRecepcion()` action | `PurchaseOrderService.updatePo(...)` | UPDATE PO + cascade detail/labels | `PurchaseOrder`; `PurchaseOrderDetalle`; `Etiqueta` | Adds detail/labels and recalculates | Sets partial/complete and receipt fields | Creates labels/QR | WRITES RECEPCIONADA; Ubicacion/ConsultaEtiqueta |
| `recepcionManual.xhtml` | `RecepcionManualBean` | `crearEtiqueta()` public helper | `EtiquetaService.obtenerEtiqueta()` | Sequence `seq_num_etiqueta`; PO cascade | `Etiqueta` | Creates one label per pallet | Uses pallet and boxes-per-pallet | QR data generated in memory | WRITES etiqueta state RECEPCIONADA |
| `recepcionManual.xhtml` | `RecepcionManualBean` | `quitarRecepcion(PurchaseOrderDetalle)` actionListener | `EtiquetaService.cancelarEtiqueta(...)`; `DetalleRecepcionService.eliminarDetalle(...)` | UPDATE/cancel detail and label state | `PurchaseOrderDetalle`; `Etiqueta` | Removes/cancels detail | N/A | N/A | Ubicacion/ConsultaEtiqueta |
| `recepcionManual.xhtml` | `RecepcionManualBean` | `cargaRecepcion(PurchaseOrder)` actionListener | `DetalleRecepcionService.listarDetalle(...)` | SELECT PO details | `PurchaseOrder`; `PurchaseOrderDetalle` | Loads manual PO/detail dialog | N/A | N/A | CargaWine/Contenedor |
| `recepcionManual.xhtml` | `RecepcionManualBean` | `guardarRecepcion()` action | `PurchaseOrderService.updatePo(...)` | UPDATE PO received quantity/state | `PurchaseOrder`; `PurchaseOrderDetalle` | Finalizes manual reception | Requires at least one detail | No explicit SFTP/SOAP call found | WRITES RECEPCIONADO/RECEPCIONADA; Ubicacion |
| `recepcionManual.xhtml` | `RecepcionManualBean` | `autoCompletarVinas/CodProd/Prod()` autocomplete | `ContainerService`; `PurchaseOrderService` | Native autocomplete queries | `PurchaseOrder`/strings | Updates autocomplete lists | N/A | N/A | CargaWine/Contenedor |
| `recepcionManual.xhtml` | `RecepcionManualBean` | `completarCodigoDesc()` / `completarDescCodigo()` listeners | N/A | N/A | `PurchaseOrder` | Splits code/description strings | N/A | N/A | CargaWine product data |
| `recepcionManual.xhtml` | `RecepcionManualBean` | `impEtiquetas(List<Etiqueta>)` / `generarQr(...)` helper | N/A | N/A | `Etiqueta` | Builds QR label selection | N/A | PrimeFaces QR/barcode | ConsultaEtiqueta/Ubicacion |

### Open questions

- No direct Axis SOAP/WSDL call is present in `RecepcionBean`; identify whether the external process is scheduled/imported from CargaWine or delegated to another JAR/service.
- If SOAP/Axis is still required, locate WSDL endpoint, operation, credentials, timeout and retry behavior outside this bean.
- Axis and Manual both create `Etiqueta.estado=RECEPCIONADA` and PO/detail records, but Axis uses preloaded CargaWine POs and manual creates/updates PO state differently; confirm if final state must diverge.
- Receipt of details is not visibly enclosed by one bean-level transaction: PO update and detail/label cascade are separate operations.
- Confirm per-label transaction behavior and rollback when one label sequence, detail persist or `guardarRecepcion()` update fails.
- No `dbo.recepcionar` or direct stored-procedure call was found in reception Java; only sequence DAO calls and native detail/PO queries are visible.
- `wine_sp.sql` has no reception procedure match; it mainly contains billing aggregation/other procedures, so confirm deployed procedure names outside the file.
- Cross-module dependencies: CargaWine supplies PO data; Contenedor supplies winery and location context; Ubicacion/ConsultaEtiqueta read `etiqueta.estado`, position and repaletizado fields.

## Consolidado

### Relations

- **Beans:** `ConsolidadoBean`; `PackingListBean`
- **XHTML:** `pages/consolidado/consolidado.xhtml`; `pages/consolidado/tarja.xhtml`; `pages/consolidado/packingList.xhtml`
- **Route:** `/consolidado`; `/tarja`; `/packingList`
- **Notes:** Consolidado/Tarja rearranges assigned pallets and closes the container; PackingList reads the resulting packing data and prints it in XHTML.

### Inventory

| XHTML Screen | Managed Bean | Action Method | Service Used | DAO/SQL | Entity | Session State | Validations | Reports/FTP | Cross-Module Dependency |
|---|---|---|---|---|---|---|---|---|---|
| `tarja.xhtml` | `ConsolidadoBean` | `onload()` preRenderView | `ContainerService.listaContenedoresTarjado()` | SELECT containers consolidated/assigned | `Container` | `@SessionScope`; clears lists and loads container map | Permission: `isMenuDesconsolidado()` | N/A | Asignacion/Ubicacion container state |
| `consolidado.xhtml` | `ConsolidadoBean` | `asignaPalletDisponible(Asignacion)` actionListener | N/A | N/A | `Asignacion` | Sets selected available pallet | Requires no existing selected container | N/A | Asignacion |
| `tarja.xhtml` | `ConsolidadoBean` | `cargaPalletDisponibles()` actionListener | `ContainerService.buscarContenedor(...)` | Container and assignment/native loading | `Container`; `Asignacion`; `Etiqueta` | Builds 24 positions, occupied/free lists and totals | Requires selected container | N/A | WRITES only in memory; later writes Asignacion/ConsultaEtiqueta |
| `consolidado.xhtml` | `ConsolidadoBean` | `asignaPallet(Asignacion)` actionListener | `AsignacionService.actualizarAsignacion(...)` | UPDATE `asignacion.posicion` | `Asignacion`; `Etiqueta` | Moves, swaps, removes or groups pallets | Browser-state rules; no explicit pallet limit check | N/A | WRITES pallet position; shared by ConsultaEtiqueta and Despacho |
| `consolidado.xhtml` | `ConsolidadoBean` | `observacionesSelect()` public helper | N/A | N/A | N/A | Provides observation options | N/A | N/A | N/A |
| `consolidado.xhtml` | `ConsolidadoBean` | `validaObservacion()` AJAX listener | N/A | N/A | N/A | Toggles free-text observation | Shows field when `OTRO` | N/A | N/A |
| `consolidado.xhtml` | `ConsolidadoBean` | `volver()` action | N/A | N/A | N/A | Redirect state | N/A | N/A | Redirects to `/tarja` |
| `consolidado.xhtml` | `ConsolidadoBean` | `guardarTarja()` action | `ContainerService.actualizarContainer(...)`; `AsignacionService.actualizarAsignacion(...)` | UPDATE container state/consolidado fields; UPDATE assignment positions | `Container`; `Asignacion`; `Etiqueta` | Sets container consolidated and positions | All pallets assigned, at least one, observation and seal | Tarja/Jasper assets exist; no direct call found in bean | WRITES stock container/position/label state; ConsultaEtiqueta |
| `consolidado.xhtml` | `ConsolidadoBean` | `validarSello()` action | `ContainerService.actualizarContainer(...)` | UPDATE `container.sello/airbag` | `Container` | Stores seal and airbag | Seal/airbag validation basic | N/A | Contenedor/Despacho |
| `consolidado.xhtml` | `ConsolidadoBean` | `validaTarjado()` public helper | `AsignacionService.actualizarAsignacion(...)` | Revalidates assignment state | `Asignacion`; `Etiqueta` | Builds reserved pallet state | Rejects already tarjado assignments | N/A | WRITES via assignment updates |
| `consolidado.xhtml` | `ConsolidadoBean` | `mostrarEtiquetas(List<Etiqueta>)` menu actionListener | N/A | N/A | `Etiqueta` | Populates dialog list | Only opens for non-empty list | N/A | ConsultaEtiqueta label data |
| `packingList.xhtml` | `PackingListBean` | `onLoad()` preRenderView | N/A | N/A | N/A | `@SessionScope`; clears container and packing list | Permission: `isMenuDespacho()` | N/A | Despacho |
| `packingList.xhtml` | `PackingListBean` | `autoCompletarContenedor()` completeMethod | `ContainerService.autoCompletaContPacking(...)` | SQL in container state `DESPACHADO/CONSOLIDADO` | `Container`/strings | Temporary autocomplete list | N/A | N/A | Contenedor/Despacho |
| `packingList.xhtml` | `PackingListBean` | `buscarContenedores()` action | `ContainerService.listarContenedoresPacking(...)` | SELECT container state `DESPACHADO/CONSOLIDADO` | `Container` | Replaces `listaContainer` | N/A | N/A | Contenedor/Despacho |
| `packingList.xhtml` | `PackingListBean` | `cargarPackingList(Container)` actionListener | `ContainerService.listarPacking(...)` | JOIN asignacion/etiqueta/PO detail/PO | `PackingList`; `Container` | Loads list and totals units | Catches query exceptions | PrimeFaces `<p:printer>` prints XHTML; Jasper/TBD | ConsultaEtiqueta/Contenedor |

### Open questions

- PackingList current XHTML is printed with PrimeFaces `<p:printer>`; no Java call to Jasper was found in `PackingListBean`.
- Confirm whether `Plantilla_tarja_desconsolidado.jrxml` and `sub_reporte_tarja_desconsolidado.jrxml` belong to this module or to a legacy tarja flow.
- `.jrxml` parameters include the main report parameter `sub_reporte_desc`; confirm remaining fields/parameters and data sources before migration.
- `ConsolidadoBean` primarily changes operational state and assignments, not PO stock quantities; confirm whether physical stock is reduced only at Despacho.
- `guardarTarja()` updates container state, assignment positions and in one loop sets an etiqueta state to `CONSOLIDADA`; verify transaction scope across these writes.
- `tarja.xhtml` is a container selector handled by `ConsolidadoBean.onload/cargaPalletDisponibles`; it belongs to ConsolidadoBean.
- The final etiqueta-state update occurs in `guardarTarja()` while iterating `listAsignacionesAsignados`; confirm whether this state change is persisted by `Etiqueta` or only through `Asignacion`.
- Confirm whether grouping/moving pallets updates one assignment or many assignments and whether it can cross multiple labels.

## Despacho

### Relations

- **Beans:** `DespachoBean`; `PtDisponiblesBean`
- **XHTML:** `despacho.xhtml`; `listaContenedorDespacho.xhtml`; `ptDisponibles.xhtml`
- **Route:** `/despacho`; `/contenedor`; `/ptDisponibles`
- **Notes:** Dispatches consolidated container labels and optional AXIS picking tickets, marks label/container/PT states and generates an SPSP SHIPMENT file.

### Inventory

| XHTML Screen | Managed Bean | Action Method | Service Used | DAO/SQL | Entity | Session State | Validations | Reports/FTP | Cross-Module Dependency |
|---|---|---|---|---|---|---|---|---|---|
| `listaContenedorDespacho.xhtml` | `DespachoBean` | `onLoad()` preRenderView | `ContainerService.listarContenedoresConsolidados()` | SELECT consolidated containers | `Container` | `@SessionScope`; clears filters and loads container map | Verifies `loginBean.isMenuDespacho()` | N/A | Consolidado/Contenedor |
| `listaContenedorDespacho.xhtml` | `DespachoBean` | `validarContenedor()` actionListener | `ContainerService.buscarContenedor(...)`; `cargarListas()` | SELECT container; shipment data | `Container`; `DespachoContenedor` | Sets selected container and loads PT/data | Requires selected container and document fields | N/A | Consolidado/Contenedor/Existencia |
| `listaContenedorDespacho.xhtml` | `DespachoBean` | `cargarListas()` action | `ContainerService.actualizarContainer(...)`; `buscarDatosDespacho(...)` | UPDATE container dates; native consolidated-label query | `Container`; `DespachoContenedor` | Stores document/dates and loads dispatch load | Requires document, type and dates | N/A | Consolidado/Ubicacion/Existencia |
| `despacho.xhtml` | `DespachoBean` | `autoAgregarEtiquetas()` radio listener | N/A | N/A | `PickingTicket`; `DespachoContenedor` | Selects matching consolidated labels | Matches product, winery and harvest | N/A | Existencia/Ubicacion/ConsultaEtiqueta |
| `despacho.xhtml` | `DespachoBean` | `comprobarPt()` actionListener | `despachar()` when valid | N/A | `PickingTicket`; `DespachoContenedor` | Validates selected PT/labels | Quantity/type/product/vinyard/cosecha match | N/A | Existencia/Ubicacion/ConsultaEtiqueta |
| `despacho.xhtml` | `DespachoBean` | `despachar()` remote action | `PickingTicketService.updatePt(...)`; `ContainerService.actualizarContainer(...)`; `EtiquetaService.despacharEtiqueta(...)` | UPDATE PT state; UPDATE container state; UPDATE label state | `PickingTicket`; `Container`; `DespachoContenedor` | Finalizes dispatch and redirects | Requires valid PT/labels | Calls `crearShippingNotification()` / SFTP for AXIS | WRITES DESPACHADA; ConsultaEtiqueta/Existencia |
| `despacho.xhtml` | `DespachoBean` | `crearShippingNotification(PickingTicket, List<DespachoContenedor>)` public helper | `AjusteInventarioService.secuenciaSPSP()`; `PurchaseOrderService.guardarMensaje(...)` | Sequence `seq_shipment_spsp`; insert message | `Mensaje`; `PickingTicket` | Builds SPSP shipment text | Creates file only for AXIS shipment | SFTP upload `SPSP*.txt`; SHIPMENT message; no Jasper/Excel | Recepcion AXIS/Existencia/Ubicacion |
| `despacho.xhtml` | `DespachoBean` | `cargarListaPt(List<DespachoContenedor>)` public helper | `ContainerService.buscarPt(...)` | PT native SELECT by winery/product | `PickingTicket`; `DespachoContenedor` | Sets available PT and totals | Requires non-empty consolidated load | N/A | AXIS/Consolidado/Ubicacion |
| `despacho.xhtml` | `DespachoBean` | `cargaPt(RowEditEvent)` rowEdit | N/A | N/A | `PickingTicket` | Sets editable PT selection | N/A | N/A | PickingTicket |
| `despacho.xhtml` | `DespachoBean` | `actualizarPt()` actionListener | `PickingTicketService.updatePt(...)` | UPDATE picking ticket | `PickingTicket` | Updates selected PT | N/A | N/A | PickingTicket |
| `despacho.xhtml` | `DespachoBean` | `contarUnidades()` public helper | N/A | N/A | `DespachoContenedor` | Sums selected consolidated boxes | N/A | N/A | Ubicacion/Existencia |
| `despacho.xhtml` | `DespachoBean` | `limpiarFiltros()` public helper | N/A | N/A | N/A | Clears PT/container selection state | N/A | N/A | Session/Consolidado |
| `ptDisponibles.xhtml` | `PtDisponiblesBean` | `onLoad()` preRenderView | `PickingTicketService.ptDisponibles()` | SELECT PT estado `DISPONIBLE` | `PtDisponible`; `PickingTicket` | Replaces available PT lists | Verifies `loginBean.isMenuExistencia()` | N/A | Existencia/Ubicacion |
| `ptDisponibles.xhtml` | `PtDisponiblesBean` | `cargarPt(PtDisponible)` actionListener | N/A | N/A | `PtDisponible` | Sets PT selected for cancellation | N/A | SweetAlert confirmation | PickingTicket |
| `ptDisponibles.xhtml` | `PtDisponiblesBean` | `cancelarPt()` remote action | `PickingTicketService.anularPt(...)`; `ptDisponibles()` | UPDATE PT estado=`ANULADO`; reload available | `PtDisponible`; `PickingTicket` | Clears and reloads PT lists | N/A | N/A | WRITES PT state; Existencia/Ubicacion |

### Open questions

- `crearShippingNotification()` updates selected labels to `DESPACHADA` before building/uploading the SPSP file; confirm whether the label state commit and SFTP upload share an atomic boundary.
- Confirm whether `DESPACHADA` should be set for every selected consolidated label and whether it affects ConsultaEtiqueta immediately.
- `despachar()` updates PT, creates SHIPMENT, and can update container to `DESPACHADO`; verify rollback if SFTP or message persistence fails.
- `crearShippingNotification()` only generates SPSP text for `tipoRecepcion == AXIS`; confirm MANUAL/non-AXIS shipment behavior.
- SFTP filename uses `SPSP` + sequence `seq_shipment_spsp`, e.g. `SPSP000001.txt`; confirm path conventions and retry/idempotency.
- No Excel/Jasper report generation was found in these two beans; `Reports/FTP` is limited to SFTP SHIPMENT and any external view exports/TBD.
- `wine_sp.sql` contains shipment/dispatch related procedures or queries; map exact procedure names and parameters used at runtime to confirm whether these beans call none directly.
- Confirm whether container is marked `DESPACHADO` only after no consolidated labels remain and whether partial dispatch is allowed.

## Contenedor

### Relations

- **Beans:** `ContenedorBean`
- **XHTML:** `pages/contenedor/cargaContenedores.xhtml`; `pages/despacho/listaContenedorDespacho.xhtml`
- **Route:** `/cargaContenedor`; `/contenedor`
- **Notes:** `ContenedorBean` manages Excel container programming and editable container records. `/contenedor` is backed by `DespachoBean` and uses the container state from consolidado/despacho.

### Inventory

| XHTML Screen | Managed Bean | Action Method | Service Used | DAO/SQL | Entity | Session State | Validations | Reports/FTP | Cross-Module Dependency |
|---|---|---|---|---|---|---|---|---|---|
| `cargaContenedores.xhtml` | `ContenedorBean` | `onLoad()` preRenderView | `ContainerService.listarContenedoresCargados()` | SELECT available/not consolidated containers | `Container` | `@SessionScope`; clears and reloads containers | Verifies `loginBean.isMenuProgramacion()`; redirects otherwise | N/A | Recepcion/Consolidado container state |
| `cargaContenedores.xhtml` | `ContenedorBean` | `descargarPlanillaContenedor()` actionListener | N/A | N/A | N/A | Creates `plantillaExcel` stream | Downloads only if URL responds | HTTP/FTP-template URL `db.ftp.excel.contenedor`; file `solicitud_contenedor_dd-MM-yyyy.xlsx` | Recepcion/Consolidado template |
| `cargaContenedores.xhtml` | `ContenedorBean` | `leerExcel(FileUploadEvent)` fileUploadListener | Calls `leerContenedor(...)` | N/A | N/A | Sets upload validator/result message | Accepts XLS/XLSX; max 10 MB | Imports Excel; no upload or FTP write found | Recepcion/Consolidado program feed |
| `cargaContenedores.xhtml` | `ContenedorBean` | `leerContenedor(FileUploadEvent)` public helper | `ContainerService.saveContainer(...)`; `validarExistenciaContenedor(...)` | INSERT containers; duplicate-state validation | `Container` | Builds in-memory list, persists new rows | Required fields, type checks and duplicate container | Reads XLS/XLSX; no FTP/SFTP upload found | Recepcion/Consolidado container setup |
| `cargaContenedores.xhtml` | `ContenedorBean` | `erroresExcelContendor(columna, fila)` public helper | N/A | N/A | N/A | Builds error maps | Categorizes required/type errors | N/A | Consolidado import validation |
| `cargaContenedores.xhtml` | `ContenedorBean` | `cargaContenedor(RowEditEvent)` rowEdit | N/A | N/A | `Container` | Sets selected container for confirmation | N/A | N/A | Consolidado/Despacho shared container |
| `cargaContenedores.xhtml` | `ContenedorBean` | `actulizaContenedor()` actionListener | `ContainerService.actualizarContainer(...)` | UPDATE container fields | `Container` | Updates selected container | Container number format/length validation | N/A | WRITES container used by Consolidado/Despacho |
| `N/A - public helper` | `ContenedorBean` | `insertarGuion(contenedorNuevo)` public helper | N/A | N/A | `Container` | N/A | Formats 11-digit value with hyphen | N/A | Container identifier |
| `N/A - public helper` | `ContenedorBean` | `validarNroContenedor(nroContenedor)` public helper | N/A | N/A | `Container` | N/A | Length, hyphen and ISO container check | N/A | Container identifier |
| `N/A - public helper` | `ContenedorBean` | `validarCelda(Cell)` public helper | N/A | N/A | N/A | N/A | Converts null/unsupported Excel cells to `"nulo"` | Excel XLS/XLSX input | Consolidado import |
| `cargaContenedores.xhtml` | `ContenedorBean` | `listarContenedores()` public helper | `ContainerService.listarContenedoresCargados()` | SELECT estado `DISPONIBLE`, consolidado=0 | `Container` | Replaces session container list | Displays Faces error on DAO failure | N/A | Recepcion/Consolidado state |

### Open questions

- Confirm whether `URL_EXCEL_CONT` is an FTP URL, an HTTP endpoint or a mounted server URL; document the resolved path and naming convention.
- Confirm the meaning of `fille`, `booking`, `tipoCont`, `tarifa` and the Excel column header mapping before migrating.
- The upload parses `xls/xlsx` locally and calls `saveContainer`; no manifest FTP/SFTP transfer was found in `ContenedorBean`.
- The list view `listaContenedorDespacho.xhtml` is backed by `DespachoBean`, not `ContenedorBean`; confirm whether it should be grouped under Contenedor or Despacho.
- `cargaContenedores.xhtml` mutates container records through inline row editing and `actulizaContenedor`.
- `/contenedor` appears read-oriented in this view, loading selected container data, but the actual write operations are in `DespachoBean` and `ContainerService.actualizarContainer`.
- Confirm transaction boundaries when reading, validating, inserting and updating containers in `leerContenedor`/`actulizaContenedor`.

## Ubicacion y Asignacion

Includes AsignacionBean (pages/asignacion/asignacion.xhtml, route /asignacion).

### Relations

- **Beans:** `UbicacionBean`; `AjusteInventarioBean`; `AsignacionBean`
- **XHTML:** `pages/ubicacion/reubicacion.xhtml`; `pages/ubicacion/asignarubicacion.xhtml`; `pages/ubicacion/ajustarinventario.xhtml`; `pages/asignacion/asignacion.xhtml`
- **Route:** `/reubicacion`; `/asigubicacion`; `/ajusteInventario`; `/asignacion`
- **Notes:** This area changes label location, state, container assignment and inventory-adjustment records. ConsultaEtiqueta and Repaletizado read the resulting `etiqueta` state.

### Inventory

| XHTML Screen | Managed Bean | Action Method | Service Used | DAO/SQL | Entity | Session State | Validations | Reports/FTP | Cross-Module Dependency |
|---|---|---|---|---|---|---|---|---|---|
| `reubicacion.xhtml` | `UbicacionBean` | `onLoad()` preRenderView | N/A | N/A | N/A | `@SessionScope`; clears labels and location fields | Verifies `loginBean.isMenuUbicacion()`; redirects otherwise | N/A | `LoginBean` |
| `asignarubicacion.xhtml` | `UbicacionBean` | `onLoadAsignar()` preRenderView | N/A | N/A | N/A | Clears labels and location fields | Skips AJAX requests | N/A | `LoginBean` |
| `reubicacion.xhtml` | `UbicacionBean` | `buscar()` actionListener | `ContainerService.buscarEtiquetasAlmacenadas(...)` | Native SQL joining label/PO/detail | `Ubicacion` | Replaces `listaEtiquetas` | Requires label | N/A | ConsultaEtiqueta/Repaletizado read label state |
| `reubicacion.xhtml` | `UbicacionBean` | `autoCompletaEtiquetaAlm(valor)` completeMethod | `ContainerService.autoCompletaEtiquetaAlm(...)` | SQL over stored `etiqueta.nro_etiqueta` | `Etiqueta` | Temporary autocomplete result | Returns `null` on exception | N/A | ConsultaEtiqueta/Repaletizado |
| `reubicacion.xhtml` | `UbicacionBean` | `modificarUbicacion(RowEditEvent)` rowEdit | `UbicacionService.guardarAsignacion(...)` | UPDATE `etiqueta` pasillo/altura/posicion | `Ubicacion` | Updates selected row | All location fields required | N/A | WRITES stock location; shared by ConsultaEtiqueta/Repaletizado |
| `asignarubicacion.xhtml` | `UbicacionBean` | `autoCompletaEtiquetaRecep(valor)` completeMethod | `ContainerService.autoCompletaEtiquetaRecep(...)` | SQL over received `etiqueta.nro_etiqueta` | `Etiqueta` | Temporary autocomplete result | Returns `null` on exception | N/A | ConsultaEtiqueta/Repaletizado |
| `asignarubicacion.xhtml` | `UbicacionBean` | `guardarAsignacion()` actionListener | `UbicacionService.guardarAsignacion(...)`; `ContainerService.actualizarPOAlmacenada(...)` | UPDATE location; UPDATE label estado=`ALMACENADA` | `Ubicacion`; `Etiqueta` | Stores location then clears form | All label/location fields required | N/A | WRITES label location/state; impacts ConsultaEtiqueta/Repaletizado |
| `ajustarinventario.xhtml` | `AjusteInventarioBean` | `onLoad()` preRenderView | N/A | N/A | N/A | `@SessionScope`; clears PO/adjustment state | Verifies `loginBean.isMenuAjusteInventario()`; redirects otherwise | N/A | `LoginBean` |
| `N/A - no XHTML binding found` | `AjusteInventarioBean` | `autoCompletaEtiquetaAlm(valor)` public helper | `ContainerService.autoCompletaEtiquetaAlm(...)` | SQL over stored `etiqueta.nro_etiqueta` | `Etiqueta` | Temporary autocomplete result | Returns `null` on exception | N/A | Dead/reference TBD |
| `N/A - no XHTML binding found` | `AjusteInventarioBean` | `autoCompletaEtiquetaRecep(valor)` public helper | `ContainerService.autoCompletaEtiquetaRecep(...)` | SQL over received `etiqueta.nro_etiqueta` | `Etiqueta` | Temporary autocomplete result | Returns `null` on exception | N/A | Dead/reference TBD |
| `ajustarinventario.xhtml` | `AjusteInventarioBean` | `buscar()` actionListener | `AjusteInventarioService.buscarPos(...)` | Stored procedure `dbo.buscar_pos` | `PoAjuste` | Replaces `listaPos` | Requires product number | N/A | Purchase-order stock data |
| `ajustarinventario.xhtml` | `AjusteInventarioBean` | `autoCompletarProductNumbers(valor)` completeMethod | `AjusteInventarioService.consultarProductNumbers(...)` | SQL over `purchase_order.product_number` | Data `String` | Replaces autocomplete list | Returns `null` on exception | N/A | Purchase order |
| `ajustarinventario.xhtml` | `AjusteInventarioBean` | `editaCantidad(CellEditEvent)` cellEdit | N/A | N/A | `PoAjuste` | Keeps old/new quantity | Converts values to strings | N/A | Local adjustment state |
| `ajustarinventario.xhtml` | `AjusteInventarioBean` | `ajustar(PoAjuste)` actionListener | N/A | N/A | `PoAjuste` | Sets `poSel` | Requires selected PO | N/A | Local adjustment state |
| `ajustarinventario.xhtml` | `AjusteInventarioBean` | `confirmarAjuste()` actionListener | `AjusteInventarioService.consultarDetalleInventario(...)` | COUNT and latest `ajuste_inventario` quantity | `DetalleInventario`; `PoAjuste` | Reads current adjustment detail | Requires quantity, sign and reason | SweetAlert confirmation | WRITES inventory adjustment; Repaletizado/Existencia consume stock state |
| `ajustarinventario.xhtml` | `AjusteInventarioBean` | `modificarCantidad()` remote action | `AjusteInventarioService.guardarAjusteInventario(...)` | INSERT/UPDATE `ajuste_inventario` | `AjusteInventario`; `PoAjuste` | Clears PO list and selected adjustment | Calculates old/new stock locally | N/A; FTP/SFTP and save-message code commented | WRITES adjustment; stock PO update is commented/TBD |
| `asignacion.xhtml` | `AsignacionBean` | `onLoad()` preRenderView | N/A | N/A | N/A | `@SessionScope`; clears all assignment state | Verifies `loginBean.isMenuAsignacion()`; redirects otherwise | N/A | `LoginBean` |
| `asignacion.xhtml` | `AsignacionBean` | `limpiarVista()` public helper | `PurchaseOrderService.listPoAsignacion()` | Native/list DAO for assignment POs | N/A | Clears PO and container lists | N/A | N/A | `PurchaseOrderService` |
| `asignacion.xhtml` | `AsignacionBean` | `autoCompletarFert()` completeMethod | `PurchaseOrderService.autoCompletarPo(...)` | PO/FERT SQL lookup | Data `String` | Temporary FERT list | N/A | N/A | Purchase order |
| `asignacion.xhtml` | `AsignacionBean` | `autoCompletarLote()` completeMethod | `PurchaseOrderService.autoCompletarLote(...)` | Lot SQL lookup | Data `String` | Temporary lot list | N/A | N/A | Purchase order |
| `asignacion.xhtml` | `AsignacionBean` | `listarEtiquetas()` / `actualizarTabla()` action | `EtiquetaService.resultadoBusquedaEtiquetas(...)` | JOIN label/detail/PO by lot, PO or FERT | `Etiqueta` | Replaces `busquedaEtiqueta` | N/A | N/A | Shared label state; Repaletizado/ConsultaEtiqueta |
| `asignacion.xhtml` | `AsignacionBean` | `cargarContenedores()` actionListener | `ContainerService.listarContenedoresDisponibles()` | SELECT available containers | `Container` | Opens container dialog | N/A | N/A | Container module |
| `asignacion.xhtml` | `AsignacionBean` | `asignarContenedor()` action | `EtiquetaService.actualizarEtiquetaAsignacion(...)`; `AsignacionService.guardarAsignacion(...)` | UPDATE label assigned/container; INSERT `asignacion` | `Etiqueta`; `Asignacion`; `Container` | Adds assignment records in memory | Requires selected labels/container | N/A | WRITES label/container assignment; shared label state |
| `asignacion.xhtml` | `AsignacionBean` | `confirmar()` actionListener | N/A | N/A | N/A | Builds confirmation; invokes remote `guardar()` | Requires assignments/container | SweetAlert confirmation | Container assignment |
| `asignacion.xhtml` | `AsignacionBean` | `guardarAsignaciones()` remote action | `ContainerService.actualizarContainer(...)` | UPDATE container estado=`ASIGNADO` | `Container`; `Asignacion` | Clears assignments and refreshes labels | Requires selected container in one branches | N/A | WRITES container state; downstream despacho |

### Open questions

- `guardarAsignacion()` in `UbicacionBean` performs location UPDATE and label-state UPDATE through separate service calls with no visible outer transaction.
- Position collision validation is disabled in `UbicacionServiceImpl`; confirm whether `guardarAsignacion()` must validate occupied locations before migration.
- Reubicacion UPDATE touches `etiqueta.pasillo/altura/posicion`, the same row state read by ConsultaEtiqueta and Repaletizado.
- Asignacion updates etiqueta.asignado/nro_contenedor and persists asignacion rows; confirm whether a single transaction spans both writes or whether partial assignment is possible.
- Container assignment sets label state before saving assignment rows; confirm rollback between etiqueta update and asignacion insert.
- The pallet-capacity variables in `asignarContenedor()` are calculated but `errorNumPallet` is never set; confirm whether capacity validation is missing.
- Adjustments save `AjusteInventario` but the PO quantity update and FTP/SFTP message upload are commented; confirm the actual source of stock changes.
- `AjusteInventarioBean.consultarDetalleInventario()` combines two DAO reads in one service method; confirm intended atomicity and whether reads are sufficient for stock safety.
- Confirm whether `AjusteInventarioBean.modificarCantidad()` should update `purchase_order_detalle` or only `ajuste_inventario`.

## Manifiesto

### Relations

- **Beans:** TBD — `manifiestoBean`; `blBean` referenced by XHTML
- **XHTML:** `pages/manifiesto/manifiesto.xhtml`; `pages/manifiesto/manual.xhtml`; `pages/manifiesto/manual/encabezado.xhtml`; `pages/manifiesto/manual/participantes.xhtml`
- **Route:** TBD — no PrettyFaces mapping found
- **Notes:** The codebase has manifest views, but the referenced `manifiestoBean` and `blBean` Java classes are absent from `src/main/java/com/marval/wine/bean`.

### Inventory

| XHTML Screen | Managed Bean | Action Method | Service Used | DAO/SQL | Entity | Session State | Validations | Reports/FTP | Cross-Module Dependency |
|---|---|---|---|---|---|---|---|---|---|
| `manifiesto.xhtml` | `manifiestoBean` (TBD) | `leerExcel(FileUploadEvent)` fileUploadListener | TBD | TBD | TBD | TBD | XLS/XLSX only; max 10 MB | Excel import; FTP/SFTP/SOAP TBD | Contenedor/Recepcion TBD |
| `manifiesto.xhtml` | `contenedorBean` | `listarContenedores()` public helper | `ContainerService.listarContenedoresCargados()` | SELECT available/not consolidated containers | `Container` | TBD | N/A | N/A | Contenedor |
| `manual.xhtml` | `blBean` (TBD) | `buscarManifiestoPorNroIn()` AJAX listener | TBD | TBD | TBD | Stores selected `nroInternoMan` | TBD | TBD | Contenedor/Recepcion TBD |
| `manual.xhtml` | `manifiestoBean` (TBD) | `comboActivos()` selectItems | TBD | TBD | TBD | N/A | N/A | N/A | TBD |
| `manual/encabezado.xhtml` | `blBean` (TBD) | `comboTipoServicio()` selectItems | TBD | TBD | TBD | N/A | N/A | N/A | TBD |
| `manual/encabezado.xhtml` | `blBean` (TBD) | `comboCondTransporte()` selectItems | TBD | TBD | TBD | N/A | N/A | N/A | TBD |
| `manual/encabezado.xhtml` | `blBean` (TBD) | `comboSentidoOper()` selectItems | TBD | TBD | TBD | N/A | N/A | N/A | TBD |
| `manual/encabezado.xhtml` | `blBean` (TBD) | `comboServices()` selectItems | TBD | TBD | TBD | N/A | N/A | N/A | TBD |
| `manual/participantes.xhtml` | `blBean` (TBD) | `agregarBlParticipante()` actionListener | TBD | TBD | TBD participant | Adds row to `listaBlParticipanteAux` | TBD | N/A | TBD |

### Open questions

- No `manifiestoBean` or `blBean` class exists under `src/main/java/com/marval/wine/bean`; check deployment JARs or a missing source branch for implementation.
- `SEG_USUMARVAL-20180904.jar` and `SEG_USUMARVAL-20240416.jar` may contain the missing beans/entities; confirm artifact and source location.
- No manifest-related procedure or text match was found in `src/sql/wine_sp.sql`; confirm whether manifest logic is in another SQL file or external system.
- The manifest upload method name is inferred from XHTML and cannot be traced to Java/DAO behavior.
- Confirm generated file names, formats and FTP/SFTP destination paths; no implementation was found in current source.
- Confirm whether any SOAP service is invoked for AXIS/customs manifest submission; no call is visible in current source.
- Cross-module impact with Contenedor and Recepcion is likely, but exact stored data and synchronization require the missing bean/service.
- Confirm whether `/manifiesto` and `/manual` routes exist outside `pretty-config.xml` or are incomplete screens.
- 🔴 BLOCKER: `manifiestoBean` y `blBean` NO existen en `src/main/java/com/marval/wine/bean`. El inventario no puede completarse por análisis estático. Próximo paso: descompilar `SEG_USUMARVAL-*.jar` o localizar la rama de código perdida.

## Existencia y Etiquetado

### Relations

- **Beans:** `ExistenciaCargaBean`; `BuscarEtiquetaBean`
- **XHTML:** `pages/existenciacarga/existenciacarga.xhtml`; `pages/consultaretiqueta/consultaretiqueta.xhtml`
- **Route:** `/existenciaCarga`; `/buscarEtiqueta`
- **Notes:** Ambos módulos comparten `LoginBean`, `ContainerService` y estado de sesión; `ExistenciaCargaBean` usa el procedimiento almacenado `dbo.existencia_carga` y `BuscarEtiquetaBean` consulta/actualiza etiquetas.

### Inventory

| XHTML Screen | Managed Bean | Action Method | Service Used | DAO/SQL | Entity | Session State | Validations | Reports/FTP | Cross-Module Dependency |
|---|---|---|---|---|---|---|---|---|---|
| `existenciacarga.xhtml` | `ExistenciaCargaBean` | `onLoad()` preRenderView | N/A | N/A | N/A | `@SessionScope`; limpia filtros y carga | Verifica `loginBean.isMenuExistencia()`; redirige si no tiene permiso | N/A | `LoginBean` |
| `existenciacarga.xhtml` | `ExistenciaCargaBean` | `autoCompletarVinas(valor)` completeMethod | `ContainerService.consultarListaVinas(valor)` | SQL sobre `purchase_order.owner_id` | Datos escalares `String` | Reemplaza `listaVinas` | Devuelve `null` ante error | N/A | `ContainerService` |
| `existenciacarga.xhtml` | `ExistenciaCargaBean` | `autocompletarPOS(valor)` completeMethod | `ContainerService.consultarListaPOS(valor)` | SQL sobre `purchase_order.purchase_order_id` | Datos escalares `String` | Reemplaza `listaPOS` | Devuelve `null` ante error | N/A | `ContainerService` |
| `existenciacarga.xhtml` | `ExistenciaCargaBean` | `buscarCargas()` actionListener | `ExistenciaService.consultarCargasPorVina(...)` | SP `dbo.existencia_carga` | `Existencia` | Reemplaza carga y copia auxiliar | Requiere `orden` | N/A | `ExistenciaService` → `ExistenciaDao` |
| `existenciacarga.xhtml` | `ExistenciaCargaBean` | `generarExcelExistencia()` fileDownload | N/A | N/A | `Existencia` | Usa `listaCargas` | Evita descarga fuera de render | Excel `.xls` vía `ReporteExcel` | Exportación Excel |
| `existenciacarga.xhtml` | `ExistenciaCargaBean` | `fechaMax()` listener | N/A | N/A | N/A | Calcula `fechaMaxima` | Fecha máxima = +365 días desde `fechaDesde` | N/A | N/A |
| `consultaretiqueta.xhtml` | `BuscarEtiquetaBean` | `onLoad()` preRenderView | N/A | N/A | N/A | `@SessionScope`; limpia etiquetas y filtros | Verifica `loginBean.isMenuBuscaEtiqueta()`; redirige si no tiene permiso | N/A | `LoginBean` |
| `consultaretiqueta.xhtml` | `BuscarEtiquetaBean` | `autoCompletaEtiqueta(valor)` completeMethod | `ContainerService.autoCompletaEtiquetaAlm/Recep(...)` | SQL en tablas `etiqueta` por estado | Datos escalares `String` | Resultado local temporal | Devuelve `null` ante error | N/A | `ContainerService` |
| `consultaretiqueta.xhtml` | `BuscarEtiquetaBean` | `autoCompletarVinas(valor)` completeMethod | `ContainerService.consultarListaVinas(valor)` | SQL sobre `purchase_order.owner_id` | Datos escalares `String` | Reemplaza `listaVinas` | Devuelve `null` ante error | N/A | `ContainerService` |
| `consultaretiqueta.xhtml` | `BuscarEtiquetaBean` | `buscarEtiqueta()` actionListener | `ContainerService.consultarEtiqueta(...)` | SQL nativo dinámico sobre etiquetas y PO | `ConsultarEtiqueta` | Reemplaza etiquetas y genera QR auxiliar | Requiere etiqueta o viña/tipo/número | QR generado en memoria | `ContainerService` |
| `consultaretiqueta.xhtml` | `BuscarEtiquetaBean` | `generarQr(List<ConsultarEtiqueta>)` helper | N/A | N/A | `ConsultarEtiqueta` | Agrega texto QR a cada entidad | N/A | QR PrimeFaces en XHTML | Sin dependencia externa |
| `consultaretiqueta.xhtml` | `BuscarEtiquetaBean` | `confimarCancelacion()` actionListener | N/A | N/A | `ConsultarEtiqueta` | Lee `obs` y `etiqueta` | Requiere observación | SweetAlert de confirmación | Remoto JS `confirmar()` |
| `consultaretiqueta.xhtml` | `BuscarEtiquetaBean` | `confirmarAnulacion()` actionListener | N/A | N/A | `ConsultarEtiqueta` | Usa `etiquetasCheck` | Requiere al menos una selección | SweetAlert de confirmación | Remoto JS `confirmarCheck()` |
| `consultaretiqueta.xhtml` | `BuscarEtiquetaBean` | `cancelarEtiquetasCheck()` remote action | `ContainerService.cancelarEtiqueta(...)` | UPDATE `etiqueta` estado/observación/fecha | `ConsultarEtiqueta` | Limpia selección y listas | Requiere selección y motivo | N/A | Ajuste/Despacho potencial vía cancelación |
| `consultaretiqueta.xhtml` | `BuscarEtiquetaBean` | `cancelarEtiqueta()` remote action | `ContainerService.cancelarEtiqueta(...)` | UPDATE `etiqueta` estado/observación/fecha | `ConsultarEtiqueta` | Limpia etiqueta seleccionada | Depende de observación previa | N/A | Ubicación/repaletizado potencial |
| `consultaretiqueta.xhtml` | `BuscarEtiquetaBean` | `imprimir()` public helper | N/A | N/A | N/A | N/A | Método vacío | Impresión real usa `<p:printer>`; sin servicio Java | PrimeFaces Printer/QR |

### Open questions

- Confirmar el comportamiento interno de `dbo.existencia_carga`; el DAO delega todos los filtros al SP sin consultar tablas directamente.
- Los métodos públicos `autoCompletaEtiquetaAlm()` y `autoCompletaEtiquetaRecep()` de `BuscarEtiquetaBean` no tienen referencias XHTML directas detectadas.
- `buscar()` de `BuscarEtiquetaBean` no está referenciado desde las vistas inspeccionadas y podría ser dead code.
- `generaCod()` no está referenciado desde `consultaretiqueta.xhtml`; confirmar si se usa desde otro recurso.
- `confimarCancelacion()` contiene una ruta remota llamada `confirmar`; confirmar el flujo porque el nombre no coincide con `confirmarAnulacion()`.
- `imprimir()` está vacío; la impresión actual ocurre exclusivamente con `<p:printer>` y QR `<p:barcode>`.
- `generarQr()` construye texto local; confirmar si la migración Angular debe generar el QR o conservar la librería.
- Compartición entre ambos beans: ambos usan `ConectaFtp` inyectado, pero no se observa uso en los métodos inspeccionados.
- `BuscarEtiquetaBean.cancelarEtiqueta()` y `cancelarEtiquetasCheck()` escriben directamente estado `CANCELADA`; revisar reglas de auditoría y efectos sobre ubicación/repaletizado.

## Repaletizado

### Relations

- **Beans:** `RepaletizadoBean`
- **XHTML:** `pages/repaletizado/repaletizado.xhtml`
- **Route:** `/repaletizado`
- **Notes:** Splits or transfers pallet boxes by creating new `Etiqueta` records and marking source labels as repaletized.

### Inventory

| XHTML Screen | Managed Bean | Action Method | Service Used | DAO/SQL | Entity | Session State | Validations | Reports/FTP | Cross-Module Dependency |
|---|---|---|---|---|---|---|---|---|---|
| `repaletizado.xhtml` | `RepaletizadoBean` | `onLoad()` preRenderView | N/A | N/A | N/A | `@SessionScope`; clears labels and movement fields | Verifies `loginBean.isMenuRepaletizado()`; redirects otherwise | N/A | `LoginBean` |
| `repaletizado.xhtml` | `RepaletizadoBean` | `autoCompletaEtiqueta(valor)` completeMethod | `ContainerService.autoCompletaEtiquetaAlmRepale/RecepRepale(...)` | SQL in `etiqueta`; stored/received, unassigned, not repaletized | `Etiqueta` / strings | Updates temporary result list | Returns `null` on exception | N/A | `ContainerService`; `Ubicacion` inventory |
| `repaletizado.xhtml` | `RepaletizadoBean` | `autoCompletaEtiquetaSecun(valor)` completeMethod | `ContainerService.autoCompletaEtiquetaSecunRep(valor, codProducto)` | SQL joins `etiqueta` and `purchase_order_detalle` | `Etiqueta` / strings | Uses selected primary code | Returns `null` on exception | N/A | `ContainerService`; same product |
| `repaletizado.xhtml` | `RepaletizadoBean` | `buscar()` actionListener | `RepaletizadoService.buscarEtiquetasRepaletizado(...)` | SELECT from PO/PO detail/label; excludes repaletized labels | `Repaletizado` | Replaces `listaEtiquetas` | Requires label | N/A | `RepaletizadoService` → `RepaletizadoDao` |
| `repaletizado.xhtml` | `RepaletizadoBean` | `repaletizarEtiqueta(Repaletizado)` actionListener | N/A | N/A | `Repaletizado` | Sets selected and movement state | Requires non-null label | N/A | `Ubicacion` inventory state |
| `repaletizado.xhtml` | `RepaletizadoBean` | `siguiente()` actionListener | N/A | N/A | N/A | Opens secondary movement dialog | Movement must be >0 and <= source boxes | N/A | `Ubicacion` stock movement |
| `repaletizado.xhtml` | `RepaletizadoBean` | `buscarEtiquetaSecundaria()` itemSelect listener | `RepaletizadoService.buscarEtiquetaRepaletizado(...)` | SELECT of target label joined to PO data; excludes repaletized | `Repaletizado` | Sets `repSecundario` | No explicit target-label null check | N/A | `ContainerService`; target `Etiqueta` |
| `repaletizado.xhtml` | `RepaletizadoBean` | `confirmar()` actionListener | N/A | N/A | N/A | Builds confirmation; invokes remote `guardar()` | Requires target label and different label | SweetAlert only | `Ubicacion` stock movement |
| `repaletizado.xhtml` | `RepaletizadoBean` | `guardar()` remote action | `EtiquetaService.buscar/obtener/actualizar/crearEtiqueta(...)` | UPDATE source state/repaletizado; persist new labels; sequence `seq_num_etiqueta` | `Etiqueta`; `Repaletizado` | Reduces/creates labels and clears state | Requires movement >0 | N/A | Ubicacion/AjusteInventario: stock movement |

### Open questions

- `guardar()` is not transactional as one unit; it calls several `EtiquetaService` methods, each annotated `@Transactional`, but no bean-level transaction spans the complete repaletizado.
- If a later label update or insert fails, earlier updates may remain committed; confirm rollback requirements.
- The first branch may duplicate location fields because it clones `Etiqueta` and sets `id` to null; confirm whether source and new positions should remain different.
- Marking the source as `REPALETIZADA` is followed by creating `RECEPCIONADA` labels; confirm whether this is the intended stock/Ubicacion state.
- No direct call to `AjusteInventarioService/Dao` was found; confirm whether repaletizado must create an adjustment record elsewhere.
- `maxCajas` and dead `accion`/`autoCompletaEtiquetaAlm/Recep` logic may be leftover code; no direct XHTML binding was found for those public helpers.
- Confirm whether secondary labels can be repaletized away from their original position and whether `slipsheet` and assigned/container fields are copied correctly.
- `ConectaFtp` and `URL_EXCEL_CONT` are injected but unused in this bean.

## Facturacion

### Relations

- **Beans:** `ResumenFacturacionBean`
- **XHTML:** `pages/resumenfacturacion/resumenfacturacion.xhtml`
- **Route:** `/resumenFacturacion`
- **Notes:** Reads vessel/voyage/container billing totals and exports container or vessel/voyage summaries as XLS files.

### Inventory

| XHTML Screen | Managed Bean | Action Method | Service Used | DAO/SQL | Entity | Session State | Validations | Reports/FTP | Cross-Module Dependency |
|---|---|---|---|---|---|---|---|---|---|
| `resumenfacturacion.xhtml` | `ResumenFacturacionBean` | `onLoad()` preRenderView | N/A | N/A | N/A | `@SessionScope`; clears filters and summaries | Verifies `loginBean.isMenuFacturacion()`; redirects otherwise | N/A | `LoginBean` |
| `N/A - no direct XHTML binding found` | `ResumenFacturacionBean` | `autoCompletaEtiqueta()` public helper | `ContainerService.autoCompletaEtiquetaAlm/Recep(...)` | SQL over stored/received labels | `ConsultarEtiqueta`/strings | Local autocomplete list | Returns `null` on exception | N/A | ConsultaEtiqueta/Ubicacion |
| `N/A - no direct XHTML binding found` | `ResumenFacturacionBean` | `autoCompletaEtiquetaAlm()` public helper | `ContainerService.autoCompletaEtiquetaAlm(...)` | SQL over stored labels | Data `String` | N/A | Returns `null` on exception | N/A | ConsultaEtiqueta/Ubicacion |
| `N/A - no direct XHTML binding found` | `ResumenFacturacionBean` | `autoCompletaEtiquetaRecep()` public helper | `ContainerService.autoCompletaEtiquetaRecep(...)` | SQL over received labels | Data `String` | N/A | Returns `null` on exception | N/A | ConsultaEtiqueta/Ubicacion |
| `resumenfacturacion.xhtml` | `ResumenFacturacionBean` | `autoCompletarViajesNave(valor)` completeMethod | `ContainerService.autoCompletarViajesNave(valor, naveSelec)` | SQL by selected vessel | Data `String` | Uses `naveSelec` | Returns `null` on exception | N/A | Contenedor/Despacho |
| `resumenfacturacion.xhtml` | `ResumenFacturacionBean` | `autoCompletarNaves(valor)` completeMethod | `ContainerService.autoCompletarNaves(valor)` | SQL for vessels | Data `String` | N/A | Returns `null` on exception | N/A | Contenedor/Despacho |
| `resumenfacturacion.xhtml` | `ResumenFacturacionBean` | `buscarResumen()` actionListener | `ContainerService.buscarResumenFacturacion(nave, viaje)` | SELECT containers by vessel/voyage and shipment date | `ResumenFacturacion`; `Container` | Replaces `listaResumen` | Both vessel and voyage are mandatory | N/A | Despacho/Consolidado/Contenedor |
| `resumenfacturacion.xhtml` | `ResumenFacturacionBean` | `exportarResumen(ResumenFacturacion)` actionListener | `ResumenFacturacionService.resumenFacturacionContainer(...)` | EXEC `dbo.resumen_facturacion_contenedor(cont, nave, viaje)` | `ResumenContenedor`; `ResumenFacturacion` | Sets selected container; loads detail; triggers hidden download | Catches export/query exceptions | JExcel `.xls` server-side; POI/JExcel | Despacho/Consolidado/Manifiesto/TBD |
| `N/A - no direct XHTML binding found` | `ResumenFacturacionBean` | `exportarResumenNaveViaje()` public helper | N/A | N/A | N/A | Triggers hidden vessel/voyage download | N/A | JExcel `.xls` server-side/TBD | Despacho/Consolidado |
| `resumenfacturacion.xhtml` | `ResumenFacturacionBean` | `generarReporteContenedor()` fileDownload | `ReporteExcel.crearExcelResumenFacturacion(...)` | Reads `resumenCont` already calculated | `ResumenContenedor`; `ResumenFacturacion` | Generates streamed XLS | Avoids response phase download | JExcel/Apache POI XLS; no Jasper/FTP found | Despacho/Consolidado |
| `resumenfacturacion.xhtml` | `ResumenFacturacionBean` | `generarReporteNaveViaje()` fileDownload | `ReporteExcel.crearExcelResumenFacturacionNaveViaje(...)` | Reads aggregated `resumenNav/resumenCont` | `ResumenNaveViaje`; `ResumenContenedor` | Generates streamed XLS | Avoids response phase download | JExcel/Apache POI XLS; no Jasper/FTP found | Despacho/Consolidado |
| `resumenfacturacion.xhtml` | `ResumenFacturacionBean` | `calcularResumenNaveViaje()` actionListener | `ResumenFacturacionService.resumenFacturacionContainer(...)` | Executes container SP for every container in `listaResumen`; sums Java-side | `ResumenNaveViaje`; `ResumenContenedor` | Replaces `resumenNav`, then triggers XLS | Catches export/query exceptions | JExcel `.xls` server-side; no Jasper | Despacho/Consolidado/Existencia |

### Open questions

- Billing aggregation is split: container detail comes from `dbo.resumen_facturacion_contenedor`, while vessel/voyage aggregation is calculated in Java.
- Confirm the purpose and inputs of the container stored procedure, especially dollar exchange lookup, `cobro_factura` values, transport and storage formulas.
- No date-range fields exist in this screen; the current default/parameters are vessel `naveSelec`, voyage `viajeSelec` and container `container.id`.
- Confirm which vessel/voyage values are defaults or whether filters remain empty until user selection.
- `generarReporteContenedor()` and `generarReporteNaveViaje()` use JExcel/`ReporteExcel` to build `.xls` server-side; no Jasper report call was found.
- Jasper Reports is present in the project but not referenced by `ResumenFacturacionBean` or this XHTML.
- Confirm whether the SP result should include Manifiesto data or only container/label/reception/dispatch facts.
- `ConectaFtp` and `URL_EXCEL_CONT` are injected but unused in this bean.

## Carga Wine

### Relations

- **Beans:** `CargaWineBean`
- **XHTML:** `pages/cargaWine/cargaWine.xhtml`
- **Route:** `/cargaWine`
- **Notes:** Uploads a Wine purchase-order spreadsheet and builds `PurchaseOrder` → `PurchaseOrderDetalle` → `Etiqueta` records for the AXIS reception workflow.

### Inventory

| XHTML Screen | Managed Bean | Action Method | Service Used | DAO/SQL | Entity | Session State | Validations | Reports/FTP | Cross-Module Dependency |
|---|---|---|---|---|---|---|---|---|---|
| `cargaWine.xhtml` | `CargaWineBean` | `onLoad()` preRenderView | N/A | N/A | N/A | `@SessionScope`; no explicit reset found | Verifies `loginBean.isMenuProgramacion()`; redirects otherwise | N/A | Contenedor/Recepcion programacion |
| `cargaWine.xhtml` | `CargaWineBean` | `leerExcel(FileUploadEvent)` fileUploadListener | `PurchaseOrderService.savePurchaseOrder(...)`; `EtiquetaService.obtenerEtiqueta()` | JPA/Hibernate persist list of PO/details/labels | `PurchaseOrder`; `PurchaseOrderDetalle`; `Etiqueta` | Rebuilds PO/detail/label lists and error maps | XLS/XLSX only; max 10 MB; parses required/typed columns | Apache POI XLS/XLSX upload and bulk insert; no FTP/SFTP call found | WRITES Recepcion AXIS; feeds Ubicacion/Asignacion/Consolidado |
| `N/A - internal/public helper` | `CargaWineBean` | `validarCelda(Cell)` public helper | N/A | N/A | N/A | N/A | Null/non-readable cell -> `"nulo"` | Apache POI cell parsing | Recepcion validation |
| `N/A - internal/public helper` | `CargaWineBean` | `erroresExcelContendor(columna, fila)` public helper | N/A | N/A | N/A | Adds row numbers to `erroresTipeo` | Collects parsing/type errors | Excel validation only | Recepcion import |
| `N/A - internal/public helper` | `CargaWineBean` | `totalDetalle()` public helper | N/A | N/A | `PurchaseOrderDetalle`; `Etiqueta` | Totals pallets/cases and links labels to detail | Depends on non-empty label list | N/A | Ubicacion/Asignacion |
| `N/A - internal/public helper` | `CargaWineBean` | `totalPoSinDetalle()` public helper | N/A | N/A | `PurchaseOrder` | Totals PO quantity across details | Used when varying pallet/case sizes are detected | N/A | Recepcion AXIS |
| `N/A - internal/public helper` | `CargaWineBean` | `totalPo()` public helper | N/A | N/A | `PurchaseOrder`; `PurchaseOrderDetalle` | Builds final PO totals and detail | Totals all generated details | N/A | Recepcion AXIS |
| `N/A - internal/public helper` | `CargaWineBean` | `crearEtiqueta()` public helper | `EtiquetaService.obtenerEtiqueta()` | Sequence `seq_num_etiqueta` through DAO | `Etiqueta` | Creates label with reception state and location defaults | Sets assigned=false and repaletizado state | No FTP/SFTP | WRITES etiqueta; Ubicacion/ConsultaEtiqueta/Repaletizado |
| `N/A - internal/public helper` | `CargaWineBean` | `crearPO()` public helper | N/A | N/A | `PurchaseOrder` | Initializes AXIS purchase order defaults | Sets JFHILLEBRAND/SEAPORT and RECEPCIONADO | N/A | Recepcion AXIS |
| `N/A - internal/public helper` | `CargaWineBean` | `crearPod()` public helper | N/A | N/A | `PurchaseOrderDetalle` | Initializes detail defaults | Sets guide, receipt state and total-partial flags | N/A | Recepcion/Consolidado |
| `N/A - internal/public helper` | `CargaWineBean` | `asignarUbicacion(posAntigua)` public helper | N/A | N/A | `Etiqueta` | Decodes warehouse position into pasillo/posicion/altura | `0.0` sets zero values | N/A | WRITES Ubicacion fields on new etiqueta |

### Open questions

- Confirm the exact Excel template/header mapping and mandatory columns; the bean reads fixed columns 0-14 and mostly tracks type errors.
- Upload limit is 10 MB and accepts `.xls/.xlsx` in PrimeFaces; confirm whether server limits or workbook sheet/row limits exist.
- Validation is per-cell and only blocks persistence when `erroresTipeo` is non-empty; confirm duplicate PO/label/position rules are missing or handled elsewhere.
- `leerExcel()` persists only after the workbook is fully parsed and no type errors remain, but `savePurchaseOrder()` loops and flushes each PO, so confirm rollback on a later persistence failure.
- No explicit service/bean `@Transactional` surrounds the complete Excel parse; `savePurchaseOrder` is `@Transactional` but sequence/label creation occurs before that transaction.
- Confirm whether generated PO/detail/label writes are all-or-nothing or may partially persist if the final service call fails.
- `crearEtiqueta()` assigns the reception location and label state consumed by Ubicacion, ConsultaEtiqueta, Repaletizado and Consolidado.
- No FTP/SFTP upload or SOAP call was found in `CargaWineBean`; report/FTP column is limited to local Excel parsing.
- Cross-module dependencies include Recepcion AXIS, Ubicacion, Asignacion, Consolidado and Existencia after imported stock becomes available.

## Login

### Relations

- **Beans:** `LoginBean`; extends `AdminSession`
- **XHTML:** `login.xhtml`; `index.xhtml`; `META-INF/admin.xhtml`; `includes/top-menu.xhtml`
- **Route:** `/` → `login.xhtml`; `/inicio` → `index.xhtml`
- **Notes:** Session state and menu permissions are shared with all protected views. Authentication is delegated to `SEG_USUMARVAL-*.jar`.

### Inventory

| XHTML Screen | Managed Bean | Action Method | Service Used | DAO/SQL | Entity | Session State | Validations | Reports/FTP | Cross-Module Dependency |
|---|---|---|---|---|---|---|---|---|---|
| `login.xhtml` | `LoginBean` | `init()` @PostConstruct | N/A | N/A | N/A | Initializes `empresas` in the session bean | N/A | N/A | N/A |
| `index.xhtml` | `LoginBean` | `onLoad()` preRenderView | N/A | N/A | `Usuario_segusu` | Reads welcome state and resets it after display | Skips AJAX requests | N/A | Shared session authorization |
| `login.xhtml` | `LoginBean` | `login()` action | SEG `Login_segusu` via JAR | N/A | `Usuario_segusu`; `Rol_segusu`; `Permiso_segusu` | Sets login, user, role, company and menu flags | Requires company; verifies credentials and redirect | N/A | SEG authentication JAR; `/inicio` |
| `META-INF/admin.xhtml` | `LoginBean` | `valida()` preRenderView | N/A | N/A | N/A | Reads `login` | Redirects unauthenticated users to `/` | N/A | Shared protected views |
| `includes/top-menu.xhtml` | `LoginBean` | `cierraSession()` action | N/A | N/A | `Usuario_segusu` | Clears login, menus, user and company | N/A | N/A | Redirects to `/` |
| `N/A - invoked by login()` | `LoginBean` | `verificarPermisos(Usuario_segusu)` | N/A | N/A | `Usuario_segusu`; `Rol_segusu`; `Permiso_segusu` | Sets role and menu permissions | Maps permission IDs to menu flags | N/A | All modules controlled by menu flags |

### Open questions

- Trace authentication internals and native SQL inside `SEG_USUMARVAL-20180904.jar` / `SEG_USUMARVAL-20240416.jar`.
- Confirm the exact meaning and source of permission IDs `WINE_ADMIN`, `WINE_JEFEX`, `WINE_TARJA` and `WINE_ALMA`.
- `IndicadoresService` is injected in `LoginBean` but is not used in this bean.
- Confirm why the login method expects one company while `empresas` initializes only `SEAPORT`.
- XHTML marks `usuario`, `password` and `empresa` as required; `login()` also validates the company explicitly.
- The login JAR call properties, database configuration and password handling in `LoginBean` should be reviewed without exposing credentials.
- `nombreUsuario()` appears public but has no direct XHTML reference found.
