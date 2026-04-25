-- MySQL dump 10.13  Distrib 8.4.6, for Win64 (x86_64)
--
-- Host: localhost    Database: tea_db
-- ------------------------------------------------------
-- Server version	8.4.6

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `tea_banner`
--

DROP TABLE IF EXISTS `tea_banner`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tea_banner` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `image_url` varchar(255) DEFAULT NULL COMMENT '图片路径',
  `title` varchar(100) DEFAULT NULL COMMENT '标题',
  `sort` int DEFAULT '0' COMMENT '排序',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tea_banner`
--

LOCK TABLES `tea_banner` WRITE;
/*!40000 ALTER TABLE `tea_banner` DISABLE KEYS */;
INSERT INTO `tea_banner` VALUES (1,'https://images.unsplash.com/photo-1597481499750-3e6b22637e12?w=1200','源头好茶',1);
/*!40000 ALTER TABLE `tea_banner` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tea_cart`
--

DROP TABLE IF EXISTS `tea_cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tea_cart` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `tea_id` bigint NOT NULL COMMENT '茶叶商品ID',
  `count` int DEFAULT '1' COMMENT '加入数量',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_tea` (`user_id`,`tea_id`) COMMENT '联合唯一索引，防重复写入'
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tea_cart`
--

LOCK TABLES `tea_cart` WRITE;
/*!40000 ALTER TABLE `tea_cart` DISABLE KEYS */;
INSERT INTO `tea_cart` VALUES (7,1,7,1,'2026-04-09 14:34:35'),(11,3,7,1,'2026-04-09 15:14:23');
/*!40000 ALTER TABLE `tea_cart` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tea_category`
--

DROP TABLE IF EXISTS `tea_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tea_category` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `sort` int DEFAULT '0' COMMENT '排序权重',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tea_category`
--

LOCK TABLES `tea_category` WRITE;
/*!40000 ALTER TABLE `tea_category` DISABLE KEYS */;
INSERT INTO `tea_category` VALUES (1,'绿茶',0,'2026-04-09 14:26:26'),(2,'红茶',1,'2026-04-09 14:26:32');
/*!40000 ALTER TABLE `tea_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tea_culture`
--

DROP TABLE IF EXISTS `tea_culture`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tea_culture` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(200) DEFAULT NULL,
  `category` varchar(50) DEFAULT NULL,
  `content` text,
  `video_url` varchar(500) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tea_culture`
--

LOCK TABLES `tea_culture` WRITE;
/*!40000 ALTER TABLE `tea_culture` DISABLE KEYS */;
INSERT INTO `tea_culture` VALUES (1,'TEST','历史','茶叶历史','https://www.bilibili.com/video/BV1eN4y1J7oN/?spm_id_from=333.337.search-card.all.click','2026-01-26 14:07:52'),(2,'茶的历史','茶道知识','请你喝一杯好茶',NULL,'2026-04-09 15:47:08');
/*!40000 ALTER TABLE `tea_culture` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tea_message`
--

DROP TABLE IF EXISTS `tea_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tea_message` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sender_id` bigint NOT NULL COMMENT '发送者ID',
  `receiver_id` bigint NOT NULL COMMENT '接收者ID',
  `content` varchar(1000) NOT NULL COMMENT '消息正文',
  `is_read` tinyint DEFAULT '0' COMMENT '读取状态: 0=未读, 1=已读',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_chat` (`sender_id`,`receiver_id`) COMMENT '双向查询联合索引，加速轮询'
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='客服互动消息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tea_message`
--

LOCK TABLES `tea_message` WRITE;
/*!40000 ALTER TABLE `tea_message` DISABLE KEYS */;
INSERT INTO `tea_message` VALUES (1,1,3,'雅客您好，我是系统主理人，请问有什么可以帮您？',1,'2026-04-10 11:36:48'),(2,3,1,'你好',0,'2026-04-10 11:47:27'),(3,3,1,'请问您有什么问题',0,'2026-04-10 11:47:37'),(4,3,1,'为什么不能下单',0,'2026-04-10 11:48:37'),(5,1,1,'你好',0,'2026-04-10 11:49:01'),(6,3,1,'你好',0,'2026-04-10 11:49:52'),(7,3,1,'您好',0,'2026-04-10 11:55:53'),(8,3,1,'您好',0,'2026-04-10 12:07:28');
/*!40000 ALTER TABLE `tea_message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tea_order`
--

DROP TABLE IF EXISTS `tea_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tea_order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(50) NOT NULL COMMENT '订单号',
  `tea_id` bigint NOT NULL COMMENT '商品ID',
  `tea_name` varchar(100) DEFAULT NULL COMMENT '商品名称',
  `count` int DEFAULT '1' COMMENT '购买数量',
  `amount` decimal(10,2) DEFAULT NULL COMMENT '金额',
  `status` int DEFAULT '0' COMMENT '0:待支付, 1:已支付, 2:已取消',
  `is_sandbox` int DEFAULT '0' COMMENT '是否沙箱订单: 0=否, 1=是',
  `pay_type` varchar(20) DEFAULT 'SANDBOX' COMMENT '支付类型: REAL/SANDBOX',
  `user_id` bigint DEFAULT '1' COMMENT '模拟用户ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `express_info` varchar(100) DEFAULT '待发货' COMMENT '物流信息',
  `is_deleted` tinyint DEFAULT '0' COMMENT '0:未删除, 1:已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tea_order`
--

LOCK TABLES `tea_order` WRITE;
/*!40000 ALTER TABLE `tea_order` DISABLE KEYS */;
INSERT INTO `tea_order` VALUES (1,'TEA1769331560097',2,'武夷大红袍',1,1200.00,0,0,'SANDBOX',1,'2026-01-25 16:59:20','待发货',0),(2,'TEA1769332269208',2,'武夷大红袍',1,1200.00,1,0,'SANDBOX',1,'2026-01-25 17:11:09','待发货',0),(3,'TEA1769332272679',2,'武夷大红袍',1,1200.00,1,0,'SANDBOX',1,'2026-01-25 17:11:13','待发货',0),(4,'TEA1769406572626',2,'武夷大红袍',1,1200.00,1,1,'SANDBOX',1,'2026-01-26 13:49:33','待发货',0),(5,'TEA1769406576139',2,'武夷大红袍',1,1200.00,2,1,'SANDBOX',1,'2026-01-26 13:49:36','待发货',0),(6,'TEA1769407107063',2,'武夷大红袍',1,1200.00,4,1,'SANDBOX',2,'2026-01-26 13:58:27','待发货',0),(7,'TEA1769413021687',2,NULL,1,1200.00,2,0,'SANDBOX',1,'2026-01-26 15:37:02','待发货',0),(8,'TEA1769414353439',2,NULL,1,1200.00,2,0,'SANDBOX',1,'2026-01-26 15:59:13','待发货',0),(9,'TEA1771598659506',1,NULL,1,888.00,2,0,'SANDBOX',1,'2026-02-20 22:44:20','顺丰速运: SF71598659506 [运输中]',0),(10,'TEA1771598680876',1,NULL,1,888.00,2,0,'SANDBOX',2,'2026-02-20 22:44:41','顺丰速运: SF71598680876 [运输中]',0),(11,'TEA1773981256368',2,NULL,1,1200.00,2,0,'SANDBOX',1,'2026-03-20 12:34:16','顺丰速运: SF73981256368 [运输中]',0),(12,'TEA1774192527469',1,NULL,1,888.00,1,0,'SANDBOX',1,'2026-03-22 23:15:27','【顺丰速运】单号：SF1774192528410 \n[发件] 福建省武夷山市原产地发货仓 \n[收件] 用户默认收货地址 \n[预计送达] 2026-03-25 23:15 \n[当前状态] 揽件包装中',0),(13,'TEA1774192529479',2,NULL,1,1200.00,1,0,'SANDBOX',1,'2026-03-22 23:15:29','【顺丰速运】单号：SF1774192530110 \n[发件] 福建省武夷山市原产地发货仓 \n[收件] 用户默认收货地址 \n[预计送达] 2026-03-25 23:15 \n[当前状态] 揽件包装中',0),(14,'TEA1775319113067',2,NULL,1,1200.00,1,0,'SANDBOX',1,'2026-04-05 00:11:53','【顺丰速运】单号：SF1775319114351 \n[发件] 福建省武夷山市原产地发货仓 \n[收件] 用户默认收货地址 \n[预计送达] 2026-04-08 00:11 \n[当前状态] 揽件包装中',0),(15,'TEA1775320456398',2,NULL,1,1200.00,1,0,'SANDBOX',3,'2026-04-05 00:34:16','【顺丰速运】单号：SF1775320458709 \n[发件] 福建省武夷山市原产地发货仓 \n[收件] 用户默认收货地址 \n[预计送达] 2026-04-08 00:34 \n[当前状态] 揽件包装中',0),(16,'TEA1775320496395',2,NULL,1,1200.00,0,0,'SANDBOX',1,'2026-04-05 00:34:56','待发货',0),(17,'TEA1775389980302',2,NULL,1,1200.00,1,0,'SANDBOX',3,'2026-04-05 19:53:00','【顺丰速运】单号：SF1775389995507 \n[发件] 福建省武夷山市原产地发货仓 \n[收件] 用户默认收货地址 \n[预计送达] 2026-04-08 19:53 \n[当前状态] 揽件包装中',0),(18,'TEA131442771674380081',1,'西湖龙井',1,888.00,4,0,'BALANCE',3,'2026-04-05 20:08:14','待发货',1),(19,'TEA131442772381780095',2,'武夷大红袍',2,2400.00,4,0,'BALANCE',3,'2026-04-05 20:08:14','待发货',0),(21,'TEA131470746887510038',2,'武夷大红袍',1,1200.00,2,0,'BALANCE',3,'2026-04-05 20:12:53','待发货',0),(22,'TEA1775391237238',2,NULL,1,1200.00,0,0,'SANDBOX',1,'2026-04-05 20:13:57','待发货',0),(23,'TEA1775391256280',1,NULL,1,888.00,1,0,'BALANCE',1,'2026-04-05 20:14:16','待发货',0),(24,'TEA1775469071443',2,NULL,1,1200.00,0,0,'SANDBOX',3,'2026-04-06 17:51:11','待发货',0),(25,'TEA1775469077041',2,NULL,1,1200.00,0,0,'SANDBOX',3,'2026-04-06 17:51:17','待发货',0),(26,'TEA13930119785221002',2,'武夷大红袍',1,1200.00,4,0,'BALANCE',3,'2026-04-06 17:57:56','待发货',1),(27,'TEA1775481886614',2,NULL,1,1200.00,4,0,'BALANCE',3,'2026-04-06 21:24:47','待发货',1),(28,'TEA140544218018130092',4,'a',1,100.00,3,0,'BALANCE',3,'2026-04-06 21:25:06','待发货',0),(29,'TEA1775711996517',1,NULL,1,888.00,1,0,'BALANCE',3,'2026-04-09 13:19:57','待发货',0),(30,'TEA1775716502974',7,NULL,1,1000.00,2,0,'BALANCE',1,'2026-04-09 14:35:03','待发货',0),(31,'TEA1775716505148',6,NULL,1,100.00,2,0,'BALANCE',1,'2026-04-09 14:35:05','待发货',0),(32,'TEA1775718284771',7,'优质红茶',1,1000.00,2,0,'SANDBOX',3,'2026-04-09 15:04:45','【顺丰速运】单号：SF1775718284802 \n[发件] 福建省武夷山市原产地发货仓 \n[收件] 用户默认收货地址 \n[预计送达] 2026-04-12 15:04 \n[当前状态] 揽件包装中',0),(33,'TEA1775718308678',7,'优质红茶',1,1000.00,3,0,'SANDBOX',3,'2026-04-09 15:05:09','【顺丰速运】单号：SF1775718308692 \n[发件] 福建省武夷山市原产地发货仓 \n[收件] 用户默认收货地址 \n[预计送达] 2026-04-12 15:05 \n[当前状态] 揽件包装中',0),(34,'TEA1775718308710',6,'红茶',2,200.00,2,0,'SANDBOX',3,'2026-04-09 15:05:09','【顺丰速运】单号：SF1775718308723 \n[发件] 福建省武夷山市原产地发货仓 \n[收件] 用户默认收货地址 \n[预计送达] 2026-04-12 15:05 \n[当前状态] 揽件包装中',0),(35,'TEA1775786254866',7,NULL,1,1000.00,1,0,'BALANCE',1,'2026-04-10 09:57:35','待发货',0),(36,'TEA1775788956975',7,NULL,1,1000.00,2,0,'BALANCE',1,'2026-04-10 10:42:37','待发货',0);
/*!40000 ALTER TABLE `tea_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tea_product`
--

DROP TABLE IF EXISTS `tea_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tea_product` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `category_id` bigint DEFAULT NULL,
  `origin` varchar(100) DEFAULT NULL,
  `price` decimal(10,2) DEFAULT NULL,
  `stock` int DEFAULT '0',
  `trace_code` varchar(100) DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL COMMENT '图片地址',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `growth_env` text COMMENT '生长环境',
  `planting_process` text COMMENT '种植过程',
  `manufacture` text COMMENT '制作工艺',
  `test_report` text COMMENT '检测报告',
  `logistics` text COMMENT '物流信息',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tea_product`
--

LOCK TABLES `tea_product` WRITE;
/*!40000 ALTER TABLE `tea_product` DISABLE KEYS */;
INSERT INTO `tea_product` VALUES (6,'红茶',2,'云南',100.00,299,'TR1775716070245','/uploads/62bf26c2-df8c-4d7a-be9c-b68b37623161.jpg','2026-04-09 14:27:50',NULL,NULL,NULL,NULL,NULL),(7,'优质红茶',2,'云南',1000.00,97,'TR1775716102614','/uploads/23c70f96-b37f-4a36-91d4-331def9add6c.jpg','2026-04-09 14:28:23',NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `tea_product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tea_review`
--

DROP TABLE IF EXISTS `tea_review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tea_review` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `username` varchar(50) DEFAULT NULL COMMENT '冗余用户名',
  `tea_id` bigint NOT NULL COMMENT '商品ID',
  `rating` int DEFAULT '5' COMMENT '星级1-5',
  `content` varchar(500) DEFAULT NULL COMMENT '评价内容',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tea_review`
--

LOCK TABLES `tea_review` WRITE;
/*!40000 ALTER TABLE `tea_review` DISABLE KEYS */;
INSERT INTO `tea_review` VALUES (1,3,'test2',2,5,'非常好','2026-04-06 17:51:50'),(2,3,'test2',2,1,'发货慢','2026-04-06 17:58:10'),(3,3,'test2',4,5,'非常好','2026-04-09 13:04:23'),(4,3,'test2',4,5,'a','2026-04-09 13:19:48'),(5,3,'test2',7,5,'非常好','2026-04-09 15:15:27');
/*!40000 ALTER TABLE `tea_review` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tea_user`
--

DROP TABLE IF EXISTS `tea_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tea_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `role` varchar(20) DEFAULT 'TEST_USER',
  `balance` decimal(10,2) DEFAULT '0.00',
  `status` int DEFAULT '0' COMMENT '状态: 0=正常, 1=禁用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tea_user`
--

LOCK TABLES `tea_user` WRITE;
/*!40000 ALTER TABLE `tea_user` DISABLE KEYS */;
INSERT INTO `tea_user` VALUES (1,'admin','123456','ADMIN',19124.88,0,'2026-01-24 13:09:52'),(2,'test','123456','USER',10312.00,0,'2026-01-26 13:58:17'),(3,'test2','123456','USER',20362.00,0,'2026-01-26 14:41:00');
/*!40000 ALTER TABLE `tea_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'tea_db'
--

--
-- Dumping routines for database 'tea_db'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-13  0:51:42
