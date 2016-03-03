-- MySQL dump 10.13  Distrib 5.1.42, for Win32 (ia32)
--
-- Host: localhost    Database: inventory
-- ------------------------------------------------------
-- Server version	5.1.42-community

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

DROP DATABASE IF EXISTS `users`;
CREATE DATABASE `users`;
USE `users`;

DROP TABLE IF EXISTS `security_audit`;

CREATE TABLE `security_audit` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `userid` int(10) NOT NULL,
  `time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `action` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


# Dump of table users
# ------------------------------------------------------------

DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `userid` int(10) NOT NULL,
  `username` varchar(30) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `inventoryflag` varchar(5) DEFAULT NULL,
  `orderflag` varchar(5) DEFAULT NULL,
  `shippingflag` varchar(5) DEFAULT NULL,
  PRIMARY KEY (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;

INSERT INTO `users` (`userid`, `username`, `password`, `inventoryflag`, `orderflag`, `shippingflag`)
VALUES
  (1,'inventoryuser','$2a$04$v0l1Ve7PIKqGHQmmwksTpO.FxzASlEsrYeM8B8QCom2DVptKwzDRu','true','false','false'),
  (2,'orderuser','$2a$04$v0l1Ve7PIKqGHQmmwksTpO.FxzASlEsrYeM8B8QCom2DVptKwzDRu','false','true','false'),
  (3,'shippinguser','$2a$04$v0l1Ve7PIKqGHQmmwksTpO.FxzASlEsrYeM8B8QCom2DVptKwzDRu','false','false','true');

/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;