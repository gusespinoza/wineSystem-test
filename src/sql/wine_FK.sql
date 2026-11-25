ALTER TABLE etiqueta
ADD FOREIGN KEY (id_detalle_po) REFERENCES purchase_order_detalle(id);

ALTER TABLE purchase_order_detalle
ADD FOREIGN KEY (po_id) REFERENCES purchase_order(id);


ALTER TABLE asignacion
ADD FOREIGN KEY (container_id) REFERENCES container(id);
ALTER TABLE asignacion
ADD FOREIGN KEY (etiqueta_id) REFERENCES etiqueta(id);