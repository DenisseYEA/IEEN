 USE `INE`;
 
/*
SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- CALL `ine`.`sp_update_statusProducto`('ASIGNADO', 'CMP0011012');

-- CALL `ine`.`sp_get_equiposComputo`("DISPONIBLE");


DELETE FROM inicio WHERE `Usuario` = 'root';
INSERT INTO inicio values ("root","localhost","1234","ACTIVO");
UPDATE inicio 
	SET `password` = 'deuspass' 
		WHERE `Usuario` = 'root';
*/
SET SQL_SAFE_UPDATES = 0;
DELETE FROM inicio WHERE `Usuario` = 'localhost';
INSERT INTO inicio values ("","","deuspass","");
select * from User;
SELECT * FROM inicio;
SELECT * FROM modulos;
SELECT * FROM puestos;
SELECT * FROM permisos_puesto;
SELECT * FROM Inventario;

UPDATE `INE`.`Inventario` 
		SET `Inventario`.`Estatus` = 'ASIGNADO' 
			WHERE `Inventario`.`ID_Producto` = `QUery`;
            
UPDATE inicio 
	SET `password` = 'deuspass' 
		WHERE `Usuario` = 'root';


SELECT `Inventario`.`Numero` 
		FROM `INE`.`Inventario` 
			ORDER BY `Inventario`.`Numero` DESC LIMIT 1;

