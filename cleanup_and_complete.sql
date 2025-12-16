-- Cleanup duplicates and complete the orden_servicio with details and invoice

USE check_engine_db;

-- 1. Delete duplicate tipo_servicio (keep only the first of each)
DELETE FROM tipo_servicio WHERE id IN (6, 7, 8, 9, 10);

-- 2. Delete duplicate repuestos (keep only the first of each)
DELETE FROM repuesto WHERE id > 8;

-- 3. Now insert work details using specific IDs
SET @orden_id = 1;

INSERT INTO detalle_trabajo_orden (horas, tarifaHora, subtotal, tipo_servicio_id, orden_servicio_id) VALUES
-- Diagnóstico computarizado (id=4): 1 hora a $30/hora
(1.0, 30.00, 30.00, 4, @orden_id),
-- Cambio de aceite y filtro (id=1): 0.5 horas a $35/hora
(0.5, 35.00, 17.50, 1, @orden_id),
-- Cambio de pastillas de freno (id=5): 1.5 horas a $45/hora
(1.5, 45.00, 67.50, 5, @orden_id),
-- Revisión de frenos (id=2): 0.5 horas a $25/hora
(0.5, 25.00, 12.50, 2, @orden_id);

-- 4. Insert parts used details using specific IDs
INSERT INTO detalle_repuesto_orden (cantidad, precioUnitario, subtotal, repuesto_id, orden_servicio_id) VALUES
-- Filtro de aceite (id=1): 1 unidad a $8.50
(1, 8.50, 8.50, 1, @orden_id),
-- Aceite sintético (id=2): 1 galón a $28.00
(1, 28.00, 28.00, 2, @orden_id),
-- Pastillas de freno delanteras (id=3): 1 juego a $65.00
(1, 65.00, 65.00, 3, @orden_id),
-- Líquido de frenos (id=5): 1 unidad a $12.00
(1, 12.00, 12.00, 5, @orden_id),
-- Filtro de aire (id=8): 1 unidad a $15.00
(1, 15.00, 15.00, 8, @orden_id);

-- 5. Update stock in repuesto table (decrease stock for used parts)
UPDATE repuesto SET stockActual = stockActual - 1 WHERE id IN (1, 2, 3, 5, 8);

-- 6. Insert FacturaInterna (Internal Invoice)
-- Subtotal Mano de Obra: 30.00 + 17.50 + 67.50 + 12.50 = $127.50
-- Subtotal Repuestos: 8.50 + 28.00 + 65.00 + 12.00 + 15.00 = $128.50
-- Subtotal General: $256.00
-- IVA (15%): $38.40
-- Total: $294.40
INSERT INTO factura_interna (year, numeroSecuencial, numero, fechaEmision, subtotalManoObra, subtotalRepuestos, porcentajeIVA, iva, total, orden_servicio_id) VALUES
(2025, 1, 'FACT-2025-00001', '2025-12-09', 127.50, 128.50, 15.00, 38.40, 294.40, @orden_id);

-- Display complete summary
SELECT '========================================' as '';
SELECT '       RESUMEN COMPLETO DEL SISTEMA     ' as '';
SELECT '========================================' as '';

SELECT '' as '';
SELECT CONCAT('Cliente: ', nombres, ' ', apellidos) as 'DATOS DEL CLIENTE' FROM cliente WHERE id = 1;
SELECT CONCAT('Cédula: ', cedula) as '' FROM cliente WHERE id = 1;
SELECT CONCAT('Teléfono: ', telefono) as '' FROM cliente WHERE id = 1;
SELECT CONCAT('Email: ', email) as '' FROM cliente WHERE id = 1;

SELECT '' as '';
SELECT CONCAT('Vehículo: ', marca, ' ', modelo, ' ', anio) as 'DATOS DEL VEHICULO' FROM vehiculo WHERE id = 1;
SELECT CONCAT('Placa: ', placa) as '' FROM vehiculo WHERE id = 1;
SELECT CONCAT('VIN: ', vin) as '' FROM vehiculo WHERE id = 1;

SELECT '' as '';
SELECT codigo as 'ORDEN DE SERVICIO', estadoActual as 'Estado', DATE_FORMAT(fechaCreacion, '%Y-%m-%d') as 'Fecha' FROM orden_servicio WHERE id = @orden_id;
SELECT diagnosticoInicial as 'Diagnóstico' FROM orden_servicio WHERE id = @orden_id;

SELECT '' as '';
SELECT '--- TRABAJOS REALIZADOS ---' as '';
SELECT
    ts.nombre as 'Servicio',
    dt.horas as 'Horas',
    CONCAT('$', FORMAT(dt.tarifaHora, 2)) as 'Tarifa/Hora',
    CONCAT('$', FORMAT(dt.subtotal, 2)) as 'Subtotal'
FROM detalle_trabajo_orden dt
JOIN tipo_servicio ts ON dt.tipo_servicio_id = ts.id
WHERE dt.orden_servicio_id = @orden_id;

SELECT '' as '';
SELECT CONCAT('SUBTOTAL MANO DE OBRA: $', FORMAT(SUM(subtotal), 2)) as ''
FROM detalle_trabajo_orden WHERE orden_servicio_id = @orden_id;

SELECT '' as '';
SELECT '--- REPUESTOS UTILIZADOS ---' as '';
SELECT
    r.codigo as 'Código',
    r.descripcion as 'Descripción',
    dr.cantidad as 'Cant.',
    CONCAT('$', FORMAT(dr.precioUnitario, 2)) as 'Precio Unit.',
    CONCAT('$', FORMAT(dr.subtotal, 2)) as 'Subtotal'
FROM detalle_repuesto_orden dr
JOIN repuesto r ON dr.repuesto_id = r.id
WHERE dr.orden_servicio_id = @orden_id;

SELECT '' as '';
SELECT CONCAT('SUBTOTAL REPUESTOS: $', FORMAT(SUM(subtotal), 2)) as ''
FROM detalle_repuesto_orden WHERE orden_servicio_id = @orden_id;

SELECT '' as '';
SELECT '--- FACTURA ---' as '';
SELECT
    numero as 'Número',
    DATE_FORMAT(fechaEmision, '%Y-%m-%d') as 'Fecha Emisión',
    CONCAT('$', FORMAT(subtotalManoObra, 2)) as 'Subtotal M.O.',
    CONCAT('$', FORMAT(subtotalRepuestos, 2)) as 'Subtotal Repuestos',
    CONCAT(FORMAT(porcentajeIVA, 2), '%') as 'IVA %',
    CONCAT('$', FORMAT(iva, 2)) as 'IVA',
    CONCAT('$', FORMAT(total, 2)) as 'TOTAL'
FROM factura_interna WHERE orden_servicio_id = @orden_id;

SELECT '' as '';
SELECT '========================================' as '';
SELECT 'Datos de ejemplo creados exitosamente!' as '';
SELECT '========================================' as '';
