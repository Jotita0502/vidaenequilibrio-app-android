-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1:3309
-- Tiempo de generación: 11-06-2026 a las 19:32:38
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `vida_en_equilibrio`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `logros`
--

CREATE TABLE `logros` (
  `id` int(11) NOT NULL,
  `usuario_id` int(11) NOT NULL,
  `clave` varchar(100) NOT NULL,
  `desbloqueado` tinyint(1) DEFAULT 0,
  `fecha_desbloqueo` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `metricas`
--

CREATE TABLE `metricas` (
  `id` int(11) NOT NULL,
  `usuario_id` int(11) NOT NULL,
  `fecha` date NOT NULL,
  `salvados` int(11) DEFAULT 0,
  `desechados` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `productos`
--

CREATE TABLE `productos` (
  `id` int(11) NOT NULL,
  `usuario_id` int(11) NOT NULL,
  `nombre` varchar(150) NOT NULL,
  `cantidad` int(11) DEFAULT 1,
  `fecha_caducidad` date NOT NULL,
  `estado` enum('activo','consumido','desechado') DEFAULT 'activo',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `categoria` varchar(50) NOT NULL DEFAULT 'otros'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `productos`
--

INSERT INTO `productos` (`id`, `usuario_id`, `nombre`, `cantidad`, `fecha_caducidad`, `estado`, `created_at`, `categoria`) VALUES
(4, 2, 'ACEITUNA', 6, '2025-11-26', 'activo', '2025-11-22 19:57:14', 'otros'),
(5, 2, 'leche', 5, '2025-11-03', 'desechado', '2025-11-22 20:36:48', 'otros'),
(16, 5, 'pan', 5, '2025-11-30', 'activo', '2025-11-24 15:33:36', 'otros'),
(17, 5, 'leche', 4, '2025-11-19', 'desechado', '2025-11-24 15:35:35', 'otros'),
(25, 7, 'manzana', 4, '2025-12-27', 'activo', '2025-12-04 13:44:30', 'frutas'),
(26, 7, 'carne', 5, '2025-12-01', 'desechado', '2025-12-04 13:44:47', 'carnes'),
(27, 7, 'leche', 5, '2025-12-17', 'activo', '2025-12-04 13:45:26', 'lacteos');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `recetas`
--

CREATE TABLE `recetas` (
  `id` int(11) NOT NULL,
  `nombre` varchar(150) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `ingredientes` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `recetas`
--

INSERT INTO `recetas` (`id`, `nombre`, `descripcion`, `ingredientes`) VALUES
(1, 'Tortilla de huevos', '1. Bate los huevos en un tazón y agrega sal. \r\n2. Calienta una sartén con un poco de aceite. \r\n3. Vierte los huevos batidos y cocina a fuego medio. \r\n4. Dobla la tortilla y cocina 1 minuto adicional. \r\n5. Sirve caliente.\r\n', 'huevo,leche,sal'),
(2, 'Sopa de verduras', '1. Corta todas las verduras en cubos pequeños.  \r\n2. Sofríe cebolla y apio por 2 minutos.  \r\n3. Agrega zanahoria, papa y agua o caldo.  \r\n4. Cocina a fuego medio por 20 minutos.  \r\n5. Sazona al gusto y sirve caliente.\r\n', 'zanahoria,cebolla,apio,patata'),
(3, 'Sándwich mixto', '1. Tuesta ligeramente el pan.  \r\n2. Coloca el queso sobre el pan caliente para que derrita.  \r\n3. Agrega las lonjas de jamón.  \r\n4. Cierra el sándwich y corta a la mitad.  \r\n5. Puedes calentarlo en sartén para mejor textura.\r\n', 'pan,queso,jamon'),
(4, 'Pasta Alfredo', '1. Cocina la pasta en agua con sal hasta que esté al dente.  \r\n2. En una sartén derrite mantequilla y agrega leche o crema.  \r\n3. Añade queso y mezcla hasta obtener una salsa cremosa.  \r\n4. Incorpora la pasta y mezcla bien.  \r\n5. Sirve caliente con más queso encima.\r\n', 'pasta,leche,mantequilla,queso'),
(5, 'Pasta con atún', '1. Cocina la pasta en agua hirviendo con sal.  \r\n2. En una sartén calienta aceite y añade el atún.  \r\n3. Agrega un chorrito de leche para darle cremosidad.  \r\n4. Mezcla la salsa con la pasta cocida.  \r\n5. Sirve inmediatamente.\r\n', 'pasta,atun,aceite,sal'),
(6, 'Pollo al horno', '1. Sazona el pollo con ajo, sal y pimienta.  \r\n2. Colócalo en una bandeja y agrega un chorrito de limón.  \r\n3. Hornea a 180°C durante 45–50 minutos.  \r\n4. Deja reposar 5 minutos antes de servir.\r\n', 'pollo,ajo,sal,pimienta'),
(7, 'Estofado de carne', '1. Corta la carne en cubos y dóralos en una olla.  \r\n2. Añade cebolla, zanahoria y papa.  \r\n3. Vierte agua o caldo hasta cubrir.  \r\n4. Cocina a fuego lento por 40 minutos.  \r\n5. Sazona y sirve caliente.\r\n', 'carne,papa,zanahoria,cebolla'),
(8, 'Arroz con pollo', '1. Sofríe pollo trozado hasta dorar.  \r\n2. Agrega cebolla y zanahoria rallada.  \r\n3. Incorpora arroz y mezcla bien.  \r\n4. Añade agua y cocina 15–20 minutos.  \r\n5. Deja reposar antes de servir.\r\n', 'pollo,arroz,zanahoria,ajo'),
(9, 'Ensalada fresca', '1. Lava y corta la lechuga, tomate, pepino y agrega aceite.  \r\n2. Mezcla todos los ingredientes en un bowl.  \r\n3. Agrega sal al gusto.  \r\n4. Sirve fría.\r\n', 'lechuga,tomate,pepino,aceite'),
(10, 'Ensalada de atún', '1. Mezcla la lechuga con tomate picado.  \r\n2. Incorpora el atún escurrido.  \r\n3. Añade huevo cocido en rodajas.  \r\n4. Sazona con sal, aceite y limón.  \r\n5. Sirve fría.\r\n', 'atun,lechuga,tomate,huevo'),
(11, 'Verduras salteadas', '1. Corta todas las verduras en tiras.  \r\n2. Calienta aceite en una sartén.  \r\n3. Saltea todo durante 5–7 minutos.  \r\n4. Agrega sal y un toque de salsa de soja si deseas.  \r\n5. Sirve caliente.\r\n', 'zanahoria,brocoli,cebolla,aceite'),
(12, 'Tortilla de huevo con queso', '1. Bate los huevos con sal.  \r\n2. Calienta una sartén con aceite.  \r\n3. Vierte la mezcla y añade queso encima.  \r\n4. Dobla la tortilla y deja que derrita el queso.  \r\n5. Sirve caliente.\r\n', 'huevo,queso,aceite,sal'),
(13, 'Pan con huevo', '1. Tuesta el pan ligeramente.  \r\n2. Cocina un huevo frito o revuelto.  \r\n3. Colócalo sobre el pan con un toque de mantequilla.  \r\n4. Sirve al instante.\r\n', 'pan,huevo,mantequilla'),
(14, 'Avena con leche', '1. Calienta la leche en una olla.  \r\n2. Agrega avena y azúcar.  \r\n3. Cocina 5 minutos a fuego bajo.  \r\n4. Sirve caliente.\r\n', 'avena,leche,azucar'),
(15, 'Arroz chaufa', '1. Sofríe pollo en cubos.  \r\n2. Agrega huevo batido y mezcla tipo scramble.  \r\n3. Añade arroz cocido y cebolla china.  \r\n4. Saltea todo a fuego fuerte.  \r\n5. Sirve caliente.\r\n', 'arroz,huevo,pollo,cebolla'),
(16, 'Sandwich de queso', '1. Coloca queso entre dos rebanadas de pan.  \r\n2. Calienta en sartén hasta dorar ambos lados.  \r\n3. Sirve tibio con queso derretido.\r\n', 'pan,queso,mantequilla'),
(17, 'Sopa de pollo', '1. Hierve las piezas de pollo por 20 minutos.  \r\n2. Agrega verduras picadas y fideos.  \r\n3. Cocina 10 minutos adicionales.  \r\n4. Sazona al gusto y sirve caliente.\r\n', 'pollo,papa,zanahoria,fideos'),
(18, 'Pescado frito con arroz', '1. Sazona el pescado y fríelo en aceite caliente.  \r\n2. Sirve acompañado de arroz blanco recién hecho.  \r\n', 'pescado,arroz,aceite'),
(19, 'Atún salteado con verduras', '1. Sofríe cebolla y tomate en una sartén.  \r\n2. Agrega el atún y mezcla.  \r\n3. Añade vegetales picados y saltea 3–5 minutos.  \r\n4. Sirve caliente.\r\n', 'atun,cebolla,tomate,aceite'),
(20, 'Pan tostado francés', '1. Mezcla huevo, leche y azúcar.  \r\n2. Sumerge el pan en la mezcla.  \r\n3. Dora en sartén por ambos lados.  \r\n4. Sirve con miel o azúcar.\r\n', 'pan,huevo,leche,azucar'),
(21, 'Pescado al vapor', '1. Sazona el pescado con sal y limón.  \r\n2. Cocina al vapor 10–12 minutos.  \r\n3. Agrega un toque de aceite al servir.\r\n', 'pescado,ajo,sal,limon'),
(22, 'Crema de verduras', '1. Cocina las verduras hasta ablandar.  \r\n2. Licúa con un poco de agua o leche.  \r\n3. Regresa a la olla y cocina 5 minutos más.  \r\n4. Sirve caliente.\r\n', 'zanahoria,papa,apio,leche'),
(23, 'Fideos saltados', '1. Cocina los fideos según el empaque.  \r\n2. Saltea cebolla, zanahoria y pollo.  \r\n3. Incorpora los fideos y mezcla.  \r\n4. Sazona con sal o salsa de soja.  \r\n5. Sirve caliente.\r\n', 'fideos,pollo,cebolla,zanahoria');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

CREATE TABLE `usuarios` (
  `id` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `email` varchar(150) NOT NULL,
  `password` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`id`, `nombre`, `email`, `password`, `created_at`) VALUES
(1, 'joaquin Espiritu', 'j@gmail.com', 'jo', '2025-11-01 16:11:00'),
(2, 'jack Rusell', 'jack1@gmail.com', 'JACK1', '2025-11-01 16:23:44'),
(3, 'vale', 'val@gmail.com', 'VAL', '2025-11-08 18:41:51'),
(4, 'Ricardo', 'ricardo@gmail.com', 'ricardo', '2025-11-20 16:33:46'),
(5, 'cristiano', 'cristiano@gmail.com', 'CRISTIANO', '2025-11-24 15:30:05'),
(6, 'dios', 'dios@gmail.com', 'DIOS', '2025-11-25 15:42:17'),
(7, 'ricardo', 'joa@gmail.com', 'jota1', '2025-12-04 13:44:00'),
(8, 'jacksito', 'jacksito@gmail.com', 'jacksito1', '2026-06-11 17:26:37');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `logros`
--
ALTER TABLE `logros`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `usuario_id_clave` (`usuario_id`,`clave`);

--
-- Indices de la tabla `metricas`
--
ALTER TABLE `metricas`
  ADD PRIMARY KEY (`id`),
  ADD KEY `usuario_id` (`usuario_id`);

--
-- Indices de la tabla `productos`
--
ALTER TABLE `productos`
  ADD PRIMARY KEY (`id`),
  ADD KEY `usuario_id` (`usuario_id`);

--
-- Indices de la tabla `recetas`
--
ALTER TABLE `recetas`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `logros`
--
ALTER TABLE `logros`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `metricas`
--
ALTER TABLE `metricas`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `productos`
--
ALTER TABLE `productos`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=28;

--
-- AUTO_INCREMENT de la tabla `recetas`
--
ALTER TABLE `recetas`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- AUTO_INCREMENT de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `logros`
--
ALTER TABLE `logros`
  ADD CONSTRAINT `logros_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `metricas`
--
ALTER TABLE `metricas`
  ADD CONSTRAINT `metricas_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `productos`
--
ALTER TABLE `productos`
  ADD CONSTRAINT `productos_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
