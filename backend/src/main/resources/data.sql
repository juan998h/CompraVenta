INSERT INTO compras (id, comprador_id, repuesto_id, precio_monto, precio_moneda, estado, fecha_compra, fecha_descarga) VALUES
('C-001', 'ana',   'pastillas-freno',  250000, 'COP', 'COMPLETADA',  '2026-09-01 10:00:00', NULL),
('C-002', 'ana',   'filtro-aceite',     80000, 'COP', 'COMPLETADA',  '2026-09-03 11:30:00', NULL),
('C-003', 'bruno', 'escape-deportivo', 300000, 'COP', 'COMPLETADA',  '2026-09-05 09:15:00', NULL),
('C-004', 'bruno', 'kit-arrastre',      50000, 'COP', 'PENDIENTE',   '2026-09-20 16:40:00', NULL),
('C-005', 'carla', 'pastillas-freno',  250000, 'COP', 'COMPLETADA',  '2026-09-07 14:05:00', NULL),
('C-006', 'carla', 'espejos',          120000, 'COP', 'REEMBOLSADA', '2026-09-09 18:20:00', NULL),
('C-007', 'diego', 'escape-deportivo', 300000, 'COP', 'REEMBOLSADA', '2026-09-10 08:50:00', NULL),
('C-008', 'diego', 'pastillas-freno',  250000, 'COP', 'REEMBOLSADA', '2026-09-11 08:55:00', NULL),
('C-009', 'diego', 'espejos',          120000, 'COP', 'REEMBOLSADA', '2026-09-12 09:02:00', NULL),
('C-010', 'diego', 'kit-arrastre',      50000, 'COP', 'COMPLETADA',  '2026-09-15 21:10:00', NULL),
('C-011', 'elena', 'escape-deportivo', 300000, 'COP', 'COMPLETADA',  '2026-09-16 10:45:00', NULL),
('C-012', 'elena', 'pastillas-freno',  250000, 'COP', 'COMPLETADA',  '2026-09-18 12:00:00', NULL),
('C-013', 'elena', 'filtro-aceite',     80000, 'COP', 'COMPLETADA',  '2026-09-19 19:30:00', NULL),
('C-014', 'ana',   'escape-deportivo', 300000, 'COP', 'COMPLETADA',  '2026-09-22 15:15:00', NULL),
('C-015', 'bruno', 'pastillas-freno',  250000, 'COP', 'COMPLETADA',  '2026-09-25 13:25:00', NULL),
('C-016', 'carla', 'filtro-aceite',     80000, 'COP', 'PENDIENTE',   '2026-09-28 17:50:00', NULL);

-- ---------- Vendedores de prueba ----------
INSERT INTO vendedores (id, nombre, especialidad, verificado) VALUES
('vendedor1', 'Moto Parts Colombia', 'REPUESTOS_ORIGINALES', TRUE),
('vendedor2', 'Garage Racing',       'SUSPENSION',           FALSE);

-- ---------- Repuestos de prueba (los mismos ids que usan las compras) ----------
INSERT INTO repuestos (id, vendedor_id, nombre, precio_monto, precio_moneda, meses_garantia, condiciones_garantia, eliminado) VALUES
('pastillas-freno',  'vendedor1', 'Pastillas de freno deportivas', 250000, 'COP', 12, 'Garantia de fabrica', FALSE),
('filtro-aceite',    'vendedor1', 'Filtro de aceite premium',       80000, 'COP',  6, 'Garantia de fabrica', FALSE),
('escape-deportivo', 'vendedor2', 'Escape deportivo de acero',     300000, 'COP', 12, 'Garantia de fabrica', FALSE),
('kit-arrastre',     'vendedor1', 'Kit de arrastre reforzado',      50000, 'COP',  6, 'Garantia de fabrica', FALSE),
('espejos',          'vendedor2', 'Espejos retrovisores',          120000, 'COP',  3, 'Garantia de fabrica', FALSE),
('baul-antiguo',     'vendedor1', 'Baul trasero descontinuado',     90000, 'COP',  0, 'Sin garantia',        TRUE);

-- ---------- Modelos compatibles de cada repuesto ----------
INSERT INTO repuesto_modelos (repuesto_id, marca, version_modelo, anio, cilindraje_cc) VALUES
('pastillas-freno',  'Yamaha',   'MT-07',     2022,  689),
('pastillas-freno',  'Kawasaki', 'Z900',      2023,  948),
('filtro-aceite',    'Yamaha',   'MT-07',     2022,  689),
('filtro-aceite',    'Honda',    'CBR500R',   2021,  471),
('filtro-aceite',    'Ducati',   'Monster',   2022,  937),
('escape-deportivo', 'Kawasaki', 'Z900',      2023,  948),
('kit-arrastre',     'Honda',    'CBR500R',   2021,  471),
('kit-arrastre',     'BMW',      'R 1250 GS', 2022, 1254),
('espejos',          'Yamaha',   'MT-07',     2022,  689),
('espejos',          'Kawasaki', 'Z900',      2023,  948),
('espejos',          'Honda',    'CBR500R',   2021,  471),
('espejos',          'Ducati',   'Monster',   2022,  937),
('espejos',          'BMW',      'R 1250 GS', 2022, 1254),
('baul-antiguo',     'BMW',      'R 1250 GS', 2022, 1254);
