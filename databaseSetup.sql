
CREATE DATABASE blooprint;

USE blooprint;

-- create MySQLdatabase tables

CREATE TABLE `blooprint`.`_calibration` ( 
	`id` INT NOT NULL AUTO_INCREMENT , 
	`ax` INT NOT NULL , 
	`ay` INT NOT NULL , 
	`bx` INT NOT NULL , 
	`by_` INT NOT NULL , 
	`cx` INT NOT NULL , 
	`cy` INT NOT NULL , 
	`dx` INT NOT NULL , 
	`dy` INT NOT NULL , 
	`fx` INT NOT NULL , 
	`fy` INT NOT NULL , 
	`gx` INT NOT NULL , 
	`hy` INT NOT NULL , 
	`aax` INT NOT NULL , 
	`aay` INT NOT NULL , 
	`bbx` INT NOT NULL , 
	`bby` INT NOT NULL , 
	`ccx` INT NOT NULL , 
	`ccy` INT NOT NULL , 
	`ddx` INT NOT NULL , 
	`ddy` INT NOT NULL , 
	`unit_aax` DOUBLE NOT NULL , 
	`unit_aay` DOUBLE NOT NULL , 
	`unit_bbx` DOUBLE NOT NULL , 
	`unit_bby` DOUBLE NOT NULL , 
	`unit_ccx` DOUBLE NOT NULL , 
	`unit_ccy` DOUBLE NOT NULL , 
	`unit_ddx` DOUBLE NOT NULL , 
	`unit_ddy` DOUBLE NOT NULL , 
	`mA` DOUBLE NOT NULL , 
	`mB` DOUBLE NOT NULL , 
	`mC` DOUBLE NOT NULL , 
	`mD` DOUBLE NOT NULL , 
	`xCenterIN` DOUBLE NOT NULL , 
	`yCenterIN` DOUBLE NOT NULL , 
	`xCenterOUT` DOUBLE NOT NULL , 
	`yCenterOUT` DOUBLE NOT NULL , 
	PRIMARY KEY (`id`));

CREATE TABLE `blooprint`.`_clientclicks` ( 
	`id` INT NOT NULL AUTO_INCREMENT , 
	`ulx` INT NOT NULL , 
	`uly` INT NOT NULL , 
	`urx` INT NOT NULL , 
	`ury` INT NOT NULL , 
	`llx` INT NOT NULL , 
	`lly` INT NOT NULL , 
	`lrx` INT NOT NULL , 
	`lry` INT NOT NULL , 
	`width` INT NOT NULL , 
	`height` INT NOT NULL , 
	PRIMARY KEY (`id`)) ENGINE = InnoDB;

CREATE TABLE `blooprint`.`_sketches` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `image` LONGBLOB NOT NULL,
  PRIMARY KEY (`id`));

CREATE TABLE `blooprint`.`testbp` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `blooprint` LONGBLOB NOT NULL,
  PRIMARY KEY (`id`));

-- table name = [blooprint title]+"_blips"
CREATE TABLE `blooprint`.`testbp_blips` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `x` DOUBLE NOT NULL,
  `y` DOUBLE NOT NULL,
  `width` DOUBLE NOT NULL,
  `height` DOUBLE NOT NULL,
  PRIMARY KEY (`id`));

