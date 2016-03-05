
DROP DATABASE IF EXISTS `eep_leaftech`;
CREATE DATABASE `eep_leaftech`;
USE `eep_leaftech`;

DROP TABLE IF EXISTS `order_items`;
DROP TABLE IF EXISTS `orders`;
DROP TABLE IF EXISTS `products`;
DROP TABLE IF EXISTS `users`;
DROP TABLE IF EXISTS `security_audits`;

CREATE TABLE `orders` (
        `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
        `date` varchar(30),
        `first_name` varchar(20),
        `last_name` varchar(20),
        `address` varchar(80),
        `phone` varchar(15),
        `message` varchar(255),
        `total_cost` float(10,2),
        `shipped` tinyint(1),
        PRIMARY KEY (`id`))
 ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `order_items` (
                    `order_id` int(10) unsigned NOT NULL,
                    `product_id` varchar(10) NOT NULL,
                    INDEX `product_id` (`product_id`))
ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `products` (`id` varchar(10) NOT NULL,
                       `description` varchar(80),
                       `quantity` int,
                       `price` float(10,2),
                       `type` varchar(10), PRIMARY KEY (`id`),
                       INDEX `type` (`type`))
ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `order_items` ADD FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ;
ALTER TABLE `order_items` ADD FOREIGN KEY (`product_id`) REFERENCES `products` (`id`);

DROP TABLE IF EXISTS `security_audit`;

CREATE TABLE `security_audit` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `userid` int(10) NOT NULL,
  `time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `action` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `userid` int(10) NOT NULL,
  `username` varchar(30) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `inventoryflag` tinyint(1) DEFAULT 0,
  `orderflag` tinyint(1) DEFAULT 0,
  `shippingflag` tinyint(1) DEFAULT 0,
  PRIMARY KEY (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;

INSERT INTO `users` (`userid`, `username`, `password`, `inventoryflag`, `orderflag`, `shippingflag`)
VALUES
  (1,'inventoryuser','$2a$04$v0l1Ve7PIKqGHQmmwksTpO.FxzASlEsrYeM8B8QCom2DVptKwzDRu',1,0,0),
  (2,'orderuser','$2a$04$v0l1Ve7PIKqGHQmmwksTpO.FxzASlEsrYeM8B8QCom2DVptKwzDRu', 0,1,0),
  (3,'shippinguser','$2a$04$v0l1Ve7PIKqGHQmmwksTpO.FxzASlEsrYeM8B8QCom2DVptKwzDRu',0,0,1),
  (4,'admin','$2a$04$v0l1Ve7PIKqGHQmmwksTpO.FxzASlEsrYeM8B8QCom2DVptKwzDRu',1,1,1);

/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;