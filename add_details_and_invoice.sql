-- Add details and invoice to existing orden_servicio
-- This script assumes orden_servicio with id=1 already exists

USE check_engine_db;

-- Set the orden_id variable (using existing orden_servicio)
SET @orden_id = 1;

-- 1. Insert DetalleTrabajoOrden (Work Details)
INSERT INTO detalle_trabajo_orden (horas, tarifaHora, subtotal, tipo_servicio_id, orden_servicio_id) VALUES
-- Diagnóstico computarizado: 1 hora a $30/hora
(1.0, 30.00, 30.00,
    (SELECT id FROM tipo_servicio WHERE nombre = 'Diagnóstico computarizado'),
    @orden_id),
-- Cambio de aceite y filtro: 0.5 horas a $35/hora
(0.5, 35.00, 17.50,
    (SELECT id FROM tipo_servicio WHERE nombre = 'Cambio de aceite y filtro'),
    @orden_id),
-- Cambio de pastillas de freno: 1.5 horas a $45/hora
(1.5, 45.00, 67.50,
    (SELECT id FROM tipo_servicio WHERE nombre = 'Cambio de pastillas de freno'),
    @orden_id),
-- Revisión de frenos: 0.5 horas a $25/hora
(0.5, 25.00, 12.50,
    (SELECT id FROM tipo_servicio WHERE nombre = 'Revisión de frenos'),
    @orden_id);

-- 2. Insert DetalleRepuestoOrden (Parts Used)
INSERT INTO detalle_repuesto_orden (cantidad, precioUnitario, subtotal, repuesto_id, orden_servicio_id) VALUES
-- Filtro de aceite: 1 unidad a $8.50
(1, 8.50, 8.50,
    (SELECT id FROM repuesto WHERE codigo = 'REP-000001'),
    @orden_id),
-- Aceite sintético: 1 galón a $28.00
(1, 28.00, 28.00,
    (SELECT id FROM repuesto WHERE codigo = 'REP-000002'),
    @orden_id),
-- Pastillas de freno delanteras: 1 juego a $65.00
(1, 65.00, 65.00,
    (SELECT id FROM repuesto WHERE codigo = 'REP-000003'),
    @orden_id),
-- Líquido de frenos: 1 unidad a $12.00
(1, 12.00, 12.00,
    (SELECT id FROM repuesto WHERE codigo = 'REP-000005'),
    @orden_id),
-- Filtro de aire: 1 unidad a $15.00
(1, 15.00, 15.00,
    (SELECT id FROM repuesto WHERE codigo = 'REP-000008'),
    @orden_id);

-- 3. Update stock in repuesto table (decrease stock for used parts)
UPDATE repuesto SET stockActual = stockActual - 1 WHERE codigo = 'REP-000001';
UPDATE repuesto SET stockActual = stockActual - 1 WHERE codigo = 'REP-000002';
UPDATE repuesto SET stockActual = stockActual - 1 WHERE codigo = 'REP-000003';
UPDATE repuesto SET stockActual = stockActual - 1 WHERE codigo = 'REP-000005';
UPDATE repuesto SET stockActual = stockActual - 1 WHERE codigo = 'REP-000008';

-- 4. Insert FacturaInterna (Internal Invoice)
-- Subtotal Mano de Obra: 30.00 + 17.50 + 67.50 + 12.50 = $127.50
-- Subtotal Repuestos: 8.50 + 28.00 + 65.00 + 12.00 + 15.00 = $128.50
-- Subtotal General: $256.00
-- IVA (15%): $38.40
-- Total: $294.40
INSERT INTO factura_interna (year, numeroSecuencial, numero, fechaEmision, subtotalManoObra, subtotalRepuestos, porcentajeIVA, iva, total, orden_servicio_id) VALUES
(2025, 1, 'FACT-2025-00001', '2025-12-09', 127.50, 128.50, 15.00, 38.40, 294.40, @orden_id);

-- Display summary
SELECT '=== RESUMEN COMPLETO ===' as '';
SELECT CONCAT('Cliente: ', nombres, ' ', apellidos, ' (Cédula: ', cedula, ')') as 'CLIENTE' FROM cliente WHERE id = 1;
SELECT CONCAT('Vehículo: ', marca, ' ', modelo, ' ', anio, ' (Placa: ', placa, ')') as 'VEHICULO' FROM vehiculo WHERE id = 1;
SELECT CONCAT('Tipos de Servicio: ', COUNT(*)) as 'SERVICIOS_CATALOGO' FROM tipo_servicio;
SELECT CONCAT('Repuestos en catálogo: ', COUNT(*)) as 'REPUESTOS_CATALOGO' FROM repuesto;
SELECT CONCAT('Citas: ', COUNT(*)) as 'CITAS' FROM cita;
SELECT CONCAT('Orden de Servicio: ', codigo, ' - Estado: ', estadoActual) as 'ORDEN' FROM orden_servicio WHERE id = @orden_id;
SELECT CONCAT('Detalles de Trabajo: ', COUNT(*), ' servicios - Total: $', FORMAT(SUM(subtotal), 2)) as 'MANO_DE_OBRA' FROM detalle_trabajo_orden WHERE orden_servicio_id = @orden_id;
SELECT CONCAT('Detalles de Repuestos: ', COUNT(*), ' items - Total: $', FORMAT(SUM(subtotal), 2)) as 'REPUESTOS_USADOS' FROM detalle_repuesto_orden WHERE orden_servicio_id = @orden_id;
SELECT CONCAT('Factura: ', numero, ' - Total: $', FORMAT(total, 2), ' (IVA 15%: $', FORMAT(iva, 2), ')') as 'FACTURA' FROM factura_interna WHERE orden_servicio_id = @orden_id;

-- Show detailed breakdown
SELECT '=== DETALLE DE TRABAJOS REALIZADOS ===' as '';
SELECT
    ts.nombre as 'Servicio',
    dt.horas as 'Horas',
    CONCAT('$', FORMAT(dt.tarifaHora, 2)) as 'Tarifa/Hora',
    CONCAT('$', FORMAT(dt.subtotal, 2)) as 'Subtotal'
FROM detalle_trabajo_orden dt
JOIN tipo_servicio ts ON dt.tipo_servicio_id = ts.id
WHERE dt.orden_servicio_id = @orden_id;

SELECT '=== DETALLE DE REPUESTOS UTILIZADOS ===' as '';
SELECT
    r.codigo as 'Código',
    r.descripcion as 'Descripción',
    dr.cantidad as 'Cant.',
    CONCAT('$', FORMAT(dr.precioUnitario, 2)) as 'Precio Unit.',
    CONCAT('$', FORMAT(dr.subtotal, 2)) as 'Subtotal'
FROM detalle_repuesto_orden dr
JOIN repuesto r ON dr.repuesto_id = r.id
WHERE dr.orden_servicio_id = @orden_id;
