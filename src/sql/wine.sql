USE [wine]
GO
/****** Object:  Table [dbo].[ajuste_inventario]    Script Date: 29-06-2022 23:14:33 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ajuste_inventario](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[product_number] [varchar](100) NULL,
	[vintage][varchar](10) NULL,
	[lote] [varchar](20) NULL,
	[cantidad_anterior] [bigint] NOT NULL,
	[cantidad_nueva] [bigint] NOT NULL,
	[observaciones] [varchar](500) NULL,
	[registro_po][varchar](100) NULL,
	[fecha_cr] [datetime] NULL,
	[usuario_cr] [varchar](50) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[asignacion]    Script Date: 29-06-2022 23:14:33 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[asignacion](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[etiqueta_id] [bigint] NOT NULL,
	[container_id] [bigint] NOT NULL,
	[usuario_cr] [varchar](50) NULL,
	[fecha_cr] [datetime] NULL,
	[usuario_up] [varchar](50) NULL,
	[fecha_up] [datetime] NULL,
	[posicion] [int] NULL,
	[asignado] [varchar](50) NULL,
	[observacion] [varchar](max) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[container]    Script Date: 29-06-2022 23:14:33 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[container](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[container] [varchar](50) NULL,
	[ship] [varchar](50) NULL,
	[voyage] [varchar](50) NULL,
	[fecha_consolidado] [datetime] NULL,
	[fecha_embarque] [datetime] NULL,
	[tara] [int] NULL,
	[sello] [varchar](50) NULL,
	[ini_consolidado] [datetime] NULL,
	[fin_consolidado] [datetime] NULL,
	[puerto] [varchar](50) NULL,
	[estado] [varchar](30) NULL,
	[fecha_cr] [datetime] NULL,
	[fecha_up] [datetime] NULL,
	[usuario_cr] [varchar](50) NULL,
	[usuario_up] [varchar](50) NULL,
	[pallet_contenedor] [int] NULL,
	[consolidado] [bit] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[etiqueta]    Script Date: 29-06-2022 23:14:33 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[etiqueta](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[id_detalle_po] [bigint] NOT NULL,
	[pasillo] [varchar](50) NULL,
	[altura] [varchar](50) NULL,
	[posicion] [varchar](50) NULL,
	[fecha_cr] [datetime] NULL,
	[fecha_up] [datetime] NULL,
	[usuario_cr] [varchar](50) NULL,
	[usuario_up] [varchar](50) NULL,
	[nro_etiqueta] [varchar](50) NULL,
	[estado] [varchar](50) NULL,
	[historial_repaletizado] [varchar](max) NULL,
	[repaletizado] [bit] NULL,
	[asignado] [bit] NULL,
	[cajas_pallet] [int] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[historial_indicadores]    Script Date: 29-06-2022 23:14:33 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[historial_indicadores](
	[id_historial] [bigint] IDENTITY(1,1) NOT NULL,
	[fech_cambio] [date] NOT NULL,
	[fecha_cr] [date] NOT NULL,
	[fecha_up] [datetime] NULL,
	[moneda] [varchar](255) NOT NULL,
	[usuario_cr] [varchar](255) NOT NULL,
	[usuario_up] [varchar](255) NULL,
	[valor_cambio] [numeric](19, 2) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id_historial] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UC_horario] UNIQUE NONCLUSTERED 
(
	[fecha_cr] ASC,
	[moneda] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[picking_ticket]    Script Date: 29-06-2022 23:14:33 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[picking_ticket](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[fecha_cr] [datetime] NULL,
	[fecha_up] [datetime] NULL,
	[interface_id] [varchar](50) NULL,
	[sending_system] [varchar](50) NULL,
	[interface_creation_date] [varchar](50) NULL,
	[sales_order_id] [varchar](50) NULL,
	[sales_orderline] [varchar](50) NULL,
	[orden_reference] [varchar](50) NULL,
	[shipment_id] [varchar](50) NULL,
	[warehouse_id] [varchar](50) NULL,
	[owner_id] [varchar](50) NULL,
	[product_number] [varchar](50) NULL,
	[order_quantity] [varchar](50) NULL,
	[shipment_quantity] [int] NULL,
	[request_ship_date] [int] NULL,
	[supplier_id] [varchar](50) NULL,
	[purchase_order] [varchar](50) NULL,
	[lot_number] [varchar](50) NULL,
	[ship_to_customerId] [varchar](50) NULL,
	[ship_to_name] [varchar](50) NULL,
	[ship_to_address1] [varchar](50) NULL,
	[ship_to_address2] [varchar](50) NULL,
	[ship_to_address3] [varchar](50) NULL,
	[ship_to_address4] [varchar](50) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[purchase_order]    Script Date: 29-06-2022 23:14:33 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[purchase_order](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[fecha_cr] [datetime] NULL,
	[fecha_up] [datetime] NULL,
	[interface_id] [varchar](80) NULL,
	[sending_system] [varchar](80) NULL,
	[interface_creation_date] [varchar](80) NULL,
	[purchase_order_id] [varchar](80) NULL,
	[order_reference] [varchar](80) NULL,
	[warehouse_id] [varchar](80) NULL,
	[owner_id] [varchar](80) NULL,
	[supplier_id] [varchar](80) NULL,
	[purchase_order] [varchar](80) NULL,
	[purchase_order_quantity] [int] NULL,
	[recived_quantity] [int] NULL,
	[expected_arrival_date] [varchar](80) NULL,
	[product_number] [varchar](80) NULL,
	[product_description] [varchar](80) NULL,
	[standard_storage_unit] [varchar](80) NULL,
	[bottles_per_unit] [int] NULL,
	[ml_per_bottle] [int] NULL,
	[alcohol_percentage] [numeric](19, 2) NULL,
	[product_category] [varchar](80) NULL,
	[weight] [numeric](19, 2) NULL,
	[volume] [numeric](19, 2) NULL,
	[vintage] [varchar](80) NULL,
	[slipsheet] [bit] NULL,
	[estado] [varchar](80) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[purchase_order_detalle]    Script Date: 29-06-2022 23:14:33 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[purchase_order_detalle](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[fecha_cr] [datetime] NULL,
	[fecha_up] [datetime] NULL,
	[tipo_doc] [varchar](80) NULL,
	[numero_doc] [int] NULL,
	[fecha_doc] [datetime] NULL,
	[purchase_order] [varchar](80) NULL,
	[po_id] [bigint] NULL,
	[cantidad] [int] NULL,
	[descripcion_producto] [varchar](max) NULL,
	[cod_producto] [varchar](80) NULL,
	[lote] [varchar](80) NULL,
	[estado_carga] [varchar](80) NULL,
	[cosecha] [varchar](80) NULL,
	[total_parcial] [varchar](80) NULL,
	[pallet] [int] NULL,
	[caja_por_pallet] [int] NULL,
	[estado] [varchar](80) NULL,
	[fecha_recepcion] [datetime] NULL,
	[total] [bit] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
ALTER TABLE [dbo].[container] ADD  CONSTRAINT [DF_container_consolidado]  DEFAULT ((0)) FOR [consolidado]
GO
ALTER TABLE [dbo].[purchase_order] ADD  CONSTRAINT [DF_purchase_order_slipsheet]  DEFAULT ((0)) FOR [slipsheet]
GO
