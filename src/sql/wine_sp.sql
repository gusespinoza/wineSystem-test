ALTER PROCEDURE resumen_facturacion_contenedor(
	@contenedor VARCHAR(30),
	@nave VARCHAR(50),
	@viaje VARCHAR(20)
)
AS
DECLARE @returnList TABLE (
[id] [bigint] IDENTITY (1,1),
[eti] [varchar](10),
[owner_id] [varchar](50),
[cod_producto] [varchar](50),
[producto] [varchar](100),
[cajas_pallet] [varchar](10),
[bot_caja] [varchar](10),
[cosecha] [varchar](10),
[fecha_recepcion] [DATETIME],
[fecha_conso] [DATETIME],
[dias] [varchar](10),
[semanas] [varchar](10),

[importadora] [varchar](100),
[pallet_contenedor] [NUMERIC](19,2),

[airbags] [NUMERIC](19,2),
[slipsheet] [NUMERIC](19,2),
[repaletizacion] [NUMERIC](19,2),
[porteo] [NUMERIC](19,2),
[transp] [NUMERIC](19,2),
[recepcion] [NUMERIC](19,2),
[almacenamiento_jfh] [NUMERIC](19,2),
[almacenamiento_vina] [NUMERIC](19,2)
)

DECLARE @fecha_embarque DATETIME

SELECT @fecha_embarque = (SELECT c.fecha_embarque FROM container as c 
WHERE c.container  = @contenedor 
AND c.ship = @nave
AND c.voyage = @viaje)


DECLARE @dolar INT

SELECT @dolar = (SELECT h.valor_cambio 
	FROM historial_indicadores AS h WHERE 
	h.moneda = 'dolar'
	--AND CONCAT (YEAR(@fecha_embarque),'-',MONTH(@fecha_embarque),'-',DAY(@fecha_embarque)) = h.fech_cambio)
	AND CONVERT(DATE, @fecha_embarque) = CONVERT(DATE, h.fech_cambio))

INSERT INTO @returnList

SELECT
et.nro_etiqueta as eti,   
pu.owner_id as owner_id,   
pd.cod_producto as cod_producto,   
pd.descripcion_producto as producto,   
et.cajas_pallet as cajas_pallet,   
pu.bottles_per_unit as bot_caja,   
pu.vintage as cosecha,   
pd.fecha_recepcion as fecha_recepcion,   
co.fecha_embarque as fecha_conso,   
DATEDIFF(day, pd.fecha_recepcion, co.fecha_embarque) + 1 as dias,   
DATEDIFF(week, pd.fecha_recepcion, co.fecha_embarque) + 1 as semanas,

co.importador as importadora,
co.pallet_contenedor AS pallet_contenedor,
airbags=CASE
WHEN (co.pallet_contenedor = 21) 
	THEN (SELECT (cf.valor*co.airbag)/co.pallet_contenedor FROM cobro_factura AS cf 
				WHERE cf.importadora = co.importador
				AND cf.tipo_cobro = 'AIRBAGS'
				AND cf.unidades = 21)
WHEN (co.pallet_contenedor = 11) 
	THEN (SELECT (cf.valor*co.airbag)/co.pallet_contenedor FROM cobro_factura AS cf 
				WHERE cf.importadora = co.importador
				AND cf.tipo_cobro = 'AIRBAGS'
				AND cf.unidades = 11)
WHEN (co.pallet_contenedor = 10) 
	THEN (SELECT (cf.valor*co.airbag)/co.pallet_contenedor FROM cobro_factura AS cf 
				WHERE cf.importadora = co.importador
				AND cf.tipo_cobro = 'AIRBAGS'
				AND cf.unidades = 10)
ELSE (SELECT (cf.valor*co.airbag)/co.pallet_contenedor FROM cobro_factura AS cf 
				WHERE cf.importadora = co.importador
				AND cf.tipo_cobro = 'AIRBAGS'
				AND cf.unidades = 99)
END,
slipsheet=CASE
WHEN (co.pallet_contenedor = 21 AND pu.slipsheet = 0) 
	THEN (SELECT cf.valor FROM cobro_factura AS cf 
				WHERE cf.importadora = co.importador
				AND cf.tipo_cobro = 'SLIPSHEET'
				AND cf.unidades = 21)
WHEN (co.pallet_contenedor > 10 AND co.pallet_contenedor <17 AND pu.slipsheet = 0) 
	THEN (SELECT cf.valor FROM cobro_factura AS cf 
				WHERE cf.importadora = co.importador
				AND cf.tipo_cobro = 'SLIPSHEET'
				AND cf.unidades = 11)
WHEN (co.pallet_contenedor = 10 AND pu.slipsheet = 0) 
	THEN (SELECT cf.valor FROM cobro_factura AS cf 
				WHERE cf.importadora = co.importador
				AND cf.tipo_cobro = 'SLIPSHEET'
				AND cf.unidades = 10)
WHEN (pu.slipsheet = 0) 
	THEN (SELECT cf.valor FROM cobro_factura AS cf 
				WHERE cf.importadora = co.importador
				AND cf.tipo_cobro = 'SLIPSHEET'
				AND cf.unidades = 99)
ELSE 0
END,
repaletizado=CASE
WHEN (co.pallet_contenedor = 21 AND et.repaletizado = 1) 
	THEN (SELECT (cf.valor*et.cajas_pallet) FROM cobro_factura AS cf 
				WHERE cf.importadora = co.importador
				AND cf.tipo_cobro = 'REPALETIZADO'
				AND cf.unidades = 21)
WHEN (co.pallet_contenedor > 10 AND co.pallet_contenedor <17 AND et.repaletizado = 1) 
	THEN (SELECT (cf.valor*et.cajas_pallet) FROM cobro_factura AS cf 
				WHERE cf.importadora = co.importador
				AND cf.tipo_cobro = 'REPALETIZADO'
				AND cf.unidades = 11)
WHEN (co.pallet_contenedor = 10 AND et.repaletizado = 1) 
	THEN (SELECT (cf.valor*et.cajas_pallet) FROM cobro_factura AS cf 
				WHERE cf.importadora = co.importador
				AND cf.tipo_cobro = 'REPALETIZADO'
				AND cf.unidades = 10)
WHEN (et.repaletizado = 1) 
	THEN (SELECT (cf.valor*et.cajas_pallet) FROM cobro_factura AS cf 
				WHERE cf.importadora = co.importador
				AND cf.tipo_cobro = 'REPALETIZADO'
				AND cf.unidades = 99)
ELSE 0
END,
porteo=CASE
WHEN (co.pallet_contenedor > 16 ) 
	THEN (SELECT (cf.valor/co.pallet_contenedor) FROM cobro_factura AS cf 
				WHERE cf.importadora = co.importador
				AND cf.tipo_cobro = 'PORTEO'
				AND cf.unidades = 21)
WHEN (co.pallet_contenedor > 10 AND co.pallet_contenedor <17 ) 
	THEN (SELECT (cf.valor/co.pallet_contenedor) FROM cobro_factura AS cf 
				WHERE cf.importadora = co.importador
				AND cf.tipo_cobro = 'PORTEO'
				AND cf.unidades = 11)
WHEN (co.pallet_contenedor = 10 ) 
	THEN (SELECT (cf.valor/co.pallet_contenedor) FROM cobro_factura AS cf 
				WHERE cf.importadora = co.importador
				AND cf.tipo_cobro = 'PORTEO'
				AND cf.unidades = 10)
WHEN (co.pallet_contenedor = 10 ) 
	THEN (SELECT (cf.valor/co.pallet_contenedor) FROM cobro_factura AS cf 
				WHERE cf.importadora = co.importador
				AND cf.tipo_cobro = 'PORTEO'
				AND cf.unidades = 99)
ELSE 0
END, --porteo
transp = (SELECT (t.valor/@dolar) / co.pallet_contenedor FROM transporte AS t 
				WHERE t.puerto_embar = co.embarque ),
recepcion=CASE
WHEN (co.pallet_contenedor > 20 ) 
	THEN (SELECT cf.valor FROM cobro_factura AS cf 
				WHERE cf.importadora = co.importador
				AND cf.tipo_cobro = 'RECEPCION'
				AND cf.unidades = 21)
WHEN (co.pallet_contenedor > 10 AND co.pallet_contenedor <17) 
	THEN (SELECT cf.valor FROM cobro_factura AS cf 
				WHERE cf.importadora = co.importador
				AND cf.tipo_cobro = 'RECEPCION'
				AND cf.unidades = 11)
WHEN (co.pallet_contenedor = 10) 
	THEN (SELECT cf.valor FROM cobro_factura AS cf 
				WHERE cf.importadora = co.importador
				AND cf.tipo_cobro = 'RECEPCION'
				AND cf.unidades = 10)
ELSE (SELECT cf.valor FROM cobro_factura AS cf 
				WHERE cf.importadora = co.importador
				AND cf.tipo_cobro = 'RECEPCION'
				AND cf.unidades = 99)
END, 
almacenamiento_jfh=CASE
WHEN ((DATEDIFF(week, pd.fecha_recepcion, co.fecha_embarque) + 1) = 1 ) 
	THEN (SELECT cf.valor FROM cobro_factura AS cf 
				WHERE cf.importadora = co.importador
				AND cf.tipo_cobro = 'ALMACENAMIENTO')
WHEN ((DATEDIFF(week, pd.fecha_recepcion, co.fecha_embarque) + 1) > 1) 
	THEN (SELECT (cf.valor*2) FROM cobro_factura AS cf 
				WHERE cf.importadora = co.importador
				AND cf.tipo_cobro = 'ALMACENAMIENTO')
ELSE 0
END,
almacenamiento_vina=CASE
WHEN ((DATEDIFF(week, pd.fecha_recepcion, co.fecha_embarque) + 1) = 3 ) 
	THEN (SELECT cf.valor FROM cobro_factura AS cf 
				WHERE cf.importadora = co.importador
				AND cf.tipo_cobro = 'ALMACENAMIENTO')
WHEN ((DATEDIFF(week, pd.fecha_recepcion, co.fecha_embarque) + 1) = 4 OR
		(DATEDIFF(week, pd.fecha_recepcion, co.fecha_embarque) + 1) = 5 OR
		(DATEDIFF(week, pd.fecha_recepcion, co.fecha_embarque) + 1) = 6) 
	THEN (SELECT (cf.valor*2) FROM cobro_factura AS cf 
				WHERE cf.importadora = co.importador
				AND cf.tipo_cobro = 'ALMACENAMIENTO')
WHEN ((DATEDIFF(week, pd.fecha_recepcion, co.fecha_embarque) + 1) > 6) 
	THEN (SELECT (((DATEDIFF(week, pd.fecha_recepcion, co.fecha_embarque) + 1) - 4) * cf.valor) FROM cobro_factura AS cf 
				WHERE cf.importadora = co.importador
				AND cf.tipo_cobro = 'ALMACENAMIENTO')
ELSE 0
END
FROM etiqueta as et
INNER JOIN container as co ON co.container = et.nro_contenedor
INNER JOIN purchase_order_detalle as pd ON pd.id = et.id_detalle_po   
INNER JOIN purchase_order as pu ON pd.po_id = pu.id
WHERE co.container  = @contenedor 
AND co.ship = @nave
AND co.voyage = @viaje

SELECT * FROM @returnList
