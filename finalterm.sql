-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: localhost    Database: finalterm
-- ------------------------------------------------------
-- Server version	8.0.45

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `cart`
--

DROP TABLE IF EXISTS `cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `quantity` int NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `product_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKmg7gch7630wv3d0mif6gtnsy0` (`user_id`,`product_id`),
  KEY `FKpu4bcbluhsxagirmbdn7dilm5` (`product_id`),
  CONSTRAINT `FKg5uhi8vpsuy0lgloxk2h4w5o6` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKpu4bcbluhsxagirmbdn7dilm5` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart`
--

LOCK TABLES `cart` WRITE;
/*!40000 ALTER TABLE `cart` DISABLE KEYS */;
INSERT INTO `cart` VALUES (2,'2026-05-22 18:58:32.863722',1,'2026-05-22 18:58:32.863722',22,1),(4,'2026-05-23 05:33:41.890465',1,'2026-05-23 05:33:41.890465',29,1),(5,'2026-05-23 07:11:34.794925',1,'2026-05-23 07:11:34.794925',21,1);
/*!40000 ALTER TABLE `cart` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `description` text,
  `name` varchar(255) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `icon` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_t8o6pivur7nn124jehx7cygw5` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (1,NULL,'Giày thể thao, Sneaker, dép thời trang nam nữ chất lượng cao','Giày dép',NULL,'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=200'),(2,NULL,'Đồng hồ đeo tay thông minh, đồng hồ cơ, đồng hồ thể thao nam tính','Đồng hồ',NULL,'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=200'),(3,NULL,'Tai nghe không dây, chuột máy tính, phụ kiện công nghệ thông minh','Đồ điện tử',NULL,'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=200'),(4,NULL,'Sách kỹ năng sống, tiểu thuyết hiện đại, truyện tranh hấp dẫn','Sách & Truyện',NULL,'https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=200'),(5,NULL,'Thảm tập yoga, dụng cụ tập gym tại gia, phụ kiện dã ngoại tiện lợi','Thể thao',NULL,'https://images.unsplash.com/photo-1517838277536-f5f99be501cd?w=200'),(6,NULL,'Quần áo thời thượng, áo khoác, phụ kiện xu hướng nam nữ','Thời trang',NULL,'https://images.unsplash.com/photo-1483985988355-763728e1935b?w=200'),(7,NULL,'Đồ dùng nhà bếp thông minh, máy pha cafe, thiết bị tiện ích gia đình','Gia dụng',NULL,'https://images.unsplash.com/photo-1556911220-e15b29be8c8f?w=200'),(8,NULL,'Kem chống nắng, nước hoa, sản phẩm chăm sóc da nam nữ chính hãng','Mỹ phẩm',NULL,'https://images.unsplash.com/photo-1596462502278-27bfdc403348?w=200'),(9,NULL,'Bàn làm việc gỗ, ghế công thái học, đèn LED decor phòng cực chill','Nội thất',NULL,'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR1bQ-JuL-2YMbUuiBOcC12dLrzHnpq2KAVbQ&s'),(10,NULL,'Tẩu sạc nhanh xe hơi, máy hút bụi mini, nước hoa ô tô cao cấp','Phụ kiện ô tô',NULL,'https://images.unsplash.com/photo-1549399542-7e3f8b79c341?w=200');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `title` varchar(255) NOT NULL,
  `message` text NOT NULL,
  `is_read` tinyint(1) DEFAULT '0',
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_notifications_user` (`user_id`),
  CONSTRAINT `FK_notifications_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
INSERT INTO `notifications` VALUES (1,1,'? Đặt hàng thành công!','Đơn hàng mã số #1 với tổng trị giá 450,000đ đã được hệ thống tiếp nhận thành công và đang chờ xử lý.',0,'2026-05-22 18:58:23.826945','2026-05-22 18:58:23.826945'),(2,4,'? Đặt hàng thành công!','Đơn hàng mã số #2 với tổng trị giá 2,940,000đ đã được hệ thống tiếp nhận thành công và đang chờ xử lý.',0,'2026-05-23 05:04:20.855510','2026-05-23 05:04:20.855510'),(3,4,'? Đặt hàng thành công!','Đơn hàng mã số #3 với tổng trị giá 7,490,000đ đã được hệ thống tiếp nhận thành công và đang chờ xử lý.',0,'2026-05-23 05:05:05.247522','2026-05-23 05:05:05.247522'),(4,1,'? Đặt hàng thành công!','Đơn hàng mã số #4 với tổng trị giá 1,850,000đ đã được hệ thống tiếp nhận thành công và đang chờ xử lý.',0,'2026-05-23 05:29:58.523276','2026-05-23 05:29:58.523276'),(5,1,'? Đặt hàng thành công!','Đơn hàng mã số #5 với tổng trị giá 8,900,000đ đã được hệ thống tiếp nhận thành công và đang chờ xử lý.',0,'2026-05-23 05:30:12.721873','2026-05-23 05:30:12.721873'),(6,1,'? Đặt hàng thành công!','Đơn hàng mã số #6 với tổng trị giá 350,000đ đã được hệ thống tiếp nhận thành công và đang chờ xử lý.',0,'2026-05-23 05:31:53.621366','2026-05-23 05:31:53.621366');
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `price` decimal(38,2) NOT NULL,
  `quantity` int NOT NULL,
  `order_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKbioxgbv59vetrxe0ejfubep1w` (`order_id`),
  KEY `FKocimc7dtr037rh4ls4l95nlfi` (`product_id`),
  CONSTRAINT `FKbioxgbv59vetrxe0ejfubep1w` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `FKocimc7dtr037rh4ls4l95nlfi` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items`
--

LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
INSERT INTO `order_items` VALUES (1,'2026-05-22 18:58:23.320050',450000.00,1,1,56),(2,'2026-05-23 05:04:20.655257',490000.00,6,2,24),(3,'2026-05-23 05:05:04.996376',7490000.00,1,3,29),(4,'2026-05-23 05:29:57.966212',1850000.00,1,4,21),(5,'2026-05-23 05:30:12.547935',8900000.00,1,5,27),(6,'2026-05-23 05:31:53.400575',350000.00,1,6,23);
/*!40000 ALTER TABLE `order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `payment_method` varchar(255) DEFAULT NULL,
  `shipping_address` text,
  `status` varchar(255) NOT NULL,
  `total_price` decimal(38,2) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK32ql8ubntj5uh44ph9659tiih` (`user_id`),
  CONSTRAINT `FK32ql8ubntj5uh44ph9659tiih` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,'2026-05-22 18:58:22.992828','COD','Quảng Bình','cancelled',450000.00,'2026-05-22 18:58:22.992828',1,'0782794280'),(2,'2026-05-23 05:04:20.514444','COD','quảng trị','cancelled',2940000.00,'2026-05-23 05:04:20.514444',4,'0783782498'),(3,'2026-05-23 05:05:04.817821','COD','quảng trị','delivered',7490000.00,'2026-05-23 05:05:04.817821',4,'0783782498'),(4,'2026-05-23 05:29:57.143492','COD','Quảng Bình 73','pending',1850000.00,'2026-05-23 05:29:57.143492',1,'0782794280'),(5,'2026-05-23 05:30:12.489750','COD','Quảng Bình 73','delivered',8900000.00,'2026-05-23 05:30:12.489750',1,'0782794280'),(6,'2026-05-23 05:31:53.132638','COD','Quảng Bình 73','delivered',350000.00,'2026-05-23 05:31:53.132638',1,'0782794280');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `description` text,
  `discount` int DEFAULT NULL,
  `eta` varchar(255) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `price` decimal(38,2) NOT NULL,
  `rating` float DEFAULT NULL,
  `shop_name` varchar(255) DEFAULT NULL,
  `sold` int DEFAULT NULL,
  `stock` int NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `category_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKog2rp4qthbtt2lfyhfo32lsw9` (`category_id`),
  CONSTRAINT `FKog2rp4qthbtt2lfyhfo32lsw9` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (21,'2026-05-23 01:55:25.000000','Kiểu dáng cá tính, chất vải canvas bền bỉ, dễ phối đồ.',5,'2-3 ngày','https://images.unsplash.com/photo-1525966222134-fcfa99b8ae77?w=500','Giày Thể Thao Vans Old Skool',1850000.00,NULL,'Vans Flagship',NULL,80,'2026-05-23 01:55:25.000000',1),(22,'2026-05-23 01:55:25.000000','Da bò thật nguyên tấm, lót trong êm ái, sang trọng.',10,'3 ngày','https://images.unsplash.com/photo-1533867617858-e7b97e060509?w=500','Giày Lười Da Nam Công Sở',1250000.00,NULL,'Đồ Da Tâm Anh',NULL,45,'2026-05-23 01:55:25.000000',1),(23,'2026-05-23 01:55:25.000000','Sandal cao gót 5cm, quai mảnh tôn dáng, thanh lịch.',0,'2 ngày','https://cdn.hstatic.net/products/1000040357/1640-sandal-quai-ngang-5cm-khoa-nude__1__9106aa09866e4644bd2707402cadbb49_1024x1024.jpg','Sandal Nữ Quai Mảnh',350000.00,NULL,'Juno Store',NULL,120,'2026-05-23 01:55:25.000000',1),(24,'2026-05-23 01:55:25.000000','Mũi nhọn đính đá sang chảnh, gót nhọn 7cm quyến rũ.',15,'3 ngày','https://images.unsplash.com/photo-1543163521-1bf539c55dd2?w=500','Giày Cao Gót Mũi Nhọn',490000.00,NULL,'ELLY Fashion',NULL,60,'2026-05-23 01:55:25.000000',1),(25,'2026-05-23 01:55:25.000000','Dép đi trong nhà hoặc dạo phố, chất liệu nhựa EVA siêu bền.',0,'1-2 ngày','https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=500','Dép Quai Ngang Unisex',990000.00,NULL,'Dép Sài Gòn',NULL,300,'2026-05-23 01:55:25.000000',1),(26,'2026-05-23 01:55:25.000000','Thiết kế mặt tròn cổ điển, pin trâu 14 ngày, màn hình AMOLED.',10,'2-3 ngày','https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?w=500','Đồng hồ Huawei Watch GT 4',5500000.00,NULL,'Huawei Official',NULL,40,'2026-05-23 01:55:25.000000',2),(27,'2026-05-23 01:55:25.000000','Kính sapphire chống trầy, hở van tim cơ học góc 9h đầy nghệ thuật.',5,'3-4 ngày','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR4WpPhCiuqETjbhnhWQ45gDL2m9WQmhBv7fQ&s','Đồng hồ Nam Orient Sun & Moon',8900000.00,NULL,'Orient Japan',NULL,12,'2026-05-23 01:55:25.000000',2),(28,'2026-05-23 01:55:25.000000','Dây lưới kim loại màu vàng hồng thời thượng, mặt siêu mỏng.',20,'2 ngày','https://danielwellingtons.com.vn/wp-content/uploads/2022/10/z3767577528005_0a8eb28bd8805129b39d32190e2fd19a.jpg','Đồng hồ Nữ Daniel Wellington',3800000.00,NULL,'DW Việt Nam',NULL,55,'2026-05-23 01:55:25.000000',2),(29,'2026-05-23 01:55:25.000000','Định vị GPS độc lập chuyên dụng cho người chạy bộ và ba môn phối hợp.',0,'1-2 ngày','https://images.unsplash.com/photo-1510017803434-a899398421b3?w=500','Đồng hồ Thể Thao Garmin Forerunner',7490000.00,NULL,'Garmin Phong Vũ',NULL,20,'2026-05-23 01:55:25.000000',2),(30,'2026-05-23 01:55:25.000000','Huyền thoại mạ vàng cổ điển, đèn nền LED, báo thức bấm giờ.',8,'2 ngày','https://images.unsplash.com/photo-1547996160-81dfa63595aa?w=500','Đồng hồ Điện Tử Casio Vintage',950000.00,NULL,'Casio Flagship',NULL,110,'2026-05-23 01:55:25.000000',2),(31,'2026-05-23 01:55:25.000000','Kích thước 800x300mm, bề mặt vải mượt, đèn LED viền 14 chế độ.',10,'2 ngày','https://images.unsplash.com/photo-1616440347437-b1c73416efc2?w=500','Bàn Di Chuột Cỡ Lớn RGB',250000.00,NULL,'Phụ Kiện Gaming',NULL,200,'2026-05-23 01:55:25.000000',3),(32,'2026-05-23 01:55:25.000000','Công nghệ sạc nhanh GaN siêu nhỏ gọn, 3 cổng sạc cho cả Laptop.',15,'1-2 ngày','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRbH8cxgAfqAILbp0x4o_TZqUX4kJLzhuuWXQ&s','Củ Sạc Nhanh Anker GaN 65W',750000.00,NULL,'Anker Official Store',NULL,85,'2026-05-23 01:55:25.000000',3),(33,'2026-05-23 01:55:25.000000','Sạc đồng thời Điện thoại, Tai nghe và Đồng hồ thông minh tiện lợi.',0,'2-3 ngày','https://product.hstatic.net/200000890439/product/0194_123779abdf5ac041e89900461189a66d_cf3b5ce2a3bf42b69524e16d63225ddd_d961247bd1d047ea80e48a11fd2463db_master.jpg','Đế Sạc Không Dây 3 Trong 1',450000.00,NULL,'Baseus Mall',NULL,70,'2026-05-23 01:55:25.000000',3),(34,'2026-05-23 01:55:25.000000','Micro chuyên dụng cho Streamer và Podcast, âm thanh ấm, LED RGB.',12,'3 ngày','https://images.unsplash.com/photo-1590602847861-f357a9332bbc?w=500','Micro Thu Âm HyperX QuadCast',3290000.00,NULL,'HyperX Authentic',NULL,18,'2026-05-23 01:55:25.000000',3),(35,'2026-05-23 01:55:25.000000','Chất liệu nhôm tản nhiệt tốt, chỉnh độ cao 6 cấp độ chống mỏi cổ.',5,'2 ngày','https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=500','Giá Đỡ Laptop Hợp Kim Nhôm',199000.00,NULL,'Gia Dụng Thông Minh',NULL,140,'2026-05-23 01:55:25.000000',3),(36,'2026-05-23 01:55:25.000000','Bí quyết thành công của Napoleon Hill dựa trên tư duy tích cực.',15,'2 ngày','https://images.unsplash.com/photo-1512820790803-83ca734da794?w=500','Sách Nghĩ Giàu Và Làm Giàu',110000.00,NULL,'Nhà Sách Nhã Nam',NULL,320,'2026-05-23 01:55:25.000000',4),(37,'2026-05-23 01:55:25.000000','Tập hợp những bài viết hài hước đầy trải nghiệm của tác giả Tony Buổi Sáng.',10,'1-2 ngày','https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=500','Sách Cafe Cùng Tony',95000.00,NULL,'NXB Trẻ',NULL,210,'2026-05-23 01:55:25.000000',4),(38,'2026-05-23 01:55:25.000000','Cuốn sách khơi dậy đam mê, hành trang chuẩn bị cho người trẻ vào đời.',5,'1-2 ngày','https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=500','Sách Trên Đường Băng',88000.00,NULL,'NXB Trẻ',NULL,290,'2026-05-23 01:55:25.000000',4),(39,'2026-05-23 01:55:25.000000','Nhận diện và giải thoát bản thân khỏi những mối quan hệ độc hại bị kiểm soát.',20,'3 ngày','https://images.unsplash.com/photo-1589829085413-56de8ae18c73?w=500','Sách Thao Túng Tâm Lý',135000.00,NULL,'BizBooks Official',NULL,180,'2026-05-23 01:55:25.000000',4),(40,'2026-05-23 01:55:25.000000','Kiệt tác văn học nổi tiếng thế giới đầy hoài niệm của Haruki Murakami.',0,'2-3 ngày','https://images.unsplash.com/photo-1541963463532-d68292c34b19?w=500','Tiểu Thuyết Rừng Na Uy',160000.00,NULL,'Nhà Sách Nhã Nam',NULL,150,'2026-05-23 01:55:25.000000',4),(41,'2026-05-23 01:55:25.000000','Mắt kính tráng gương chống tia UV, đệm silicone chống tràn nước.',10,'2 ngày','https://supersports.com.vn/cdn/shop/files/8-002331A273-tech-vn_1024x1024.jpg?v=1775011951','Kính Bơi Chống Mờ Speedo',420000.00,NULL,'Speedo Authentic',NULL,65,'2026-05-23 01:55:25.000000',5),(42,'2026-05-23 01:55:25.000000','Chất liệu da PU êm chân, đường may sắc nét, độ nảy tiêu chuẩn FIFA.',0,'2-3 ngày','https://cdn.yousport.vn/Media/Products/231216102604/qua-bong-da-dong-luc-ucv-3-05-size-5-3.jpg','Quả Bóng Đá Size 5 Động Lực',380000.00,NULL,'Động Lực Sport',NULL,100,'2026-05-23 01:55:25.000000',5),(43,'2026-05-23 01:55:25.000000','Vải co giãn thoáng khí, đệm giảm chấn bảo vệ dây chằng khi chơi thể thao.',15,'1-2 ngày','https://chogym.vn/wp-content/uploads/2023/07/dai-quan-goi-co-day-chang-co-dinh-2.jpg','Băng Cuốn Bảo Vệ Khớp Gối',150000.00,NULL,'Aolikes Mall',NULL,140,'2026-05-23 01:55:25.000000',5),(44,'2026-05-23 01:55:25.000000','Khung hợp kim nhôm chịu lực, bánh xe có đèn LED tự động phát sáng.',20,'3 ngày','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSPIF8xuj4SJ7kWWmoRuku0L5gH2Ksxe11lsw&s','Giày Patin Trẻ Em Phát Sáng',890000.00,NULL,'Thế Giới Patin',NULL,35,'2026-05-23 01:55:25.000000',5),(45,'2026-05-23 01:55:25.000000','Tay cầm bọc mút êm ái, màn hình LCD tự động đếm vòng nhảy tiêu hao calo.',0,'2 ngày','https://bizweb.dktcdn.net/100/509/297/products/7-4b967bb0-b99f-4897-a51b-de75ca4d3417.png?v=1725875764860','Dây Nhảy Thể Lực Có Đếm Số',120000.00,NULL,'Gym King Store',NULL,220,'2026-05-23 01:55:25.000000',5),(46,'2026-05-23 01:56:26.000000','Chất vải nỉ dạ dày dặn, tay phối da mềm cá tính, phong cách Streetwear.',10,'2-3 ngày','https://images.unsplash.com/photo-1551028719-00167b16eac5?w=500','Áo Khoác Bomber Varsity',450000.00,NULL,'Teelab Studio',NULL,85,'2026-05-23 01:56:26.000000',6),(47,'2026-05-23 01:56:26.000000','Chất jean denim co giãn nhẹ, form suông đứng tôn dáng, không phai màu.',5,'3 ngày','https://images.unsplash.com/photo-1541099649105-f69ad21f3246?w=500','Quần Jeans Ống Suông Nam',380000.00,NULL,'Levis Authentic',NULL,120,'2026-05-23 01:56:26.000000',6),(48,'2026-05-23 01:56:26.000000','Váy voan tơ mềm mại, họa tiết hoa nhí nữ tính, có lót trong tinh tế.',0,'2 ngày','https://images.unsplash.com/photo-1595777457583-95e059d581b8?w=500','Váy Hoa Nhí Dáng Dài',290000.00,NULL,'Marc Fashion',NULL,60,'2026-05-23 01:56:26.000000',6),(49,'2026-05-23 01:56:26.000000','Áp suất 15 bar chuẩn vị Espresso, tự động ngắt điện an toàn, nhỏ gọn.',15,'2-3 ngày','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSH_X3Ywy9PsACyfBmOHMfBgrvqSKqBmPMkNw&s','Máy Pha Cà Phê Viên Nén',2150000.00,NULL,'Nespresso Mall',NULL,30,'2026-05-23 01:56:26.000000',7),(50,'2026-05-23 01:56:26.000000','Dung tích lớn 5.6L, công nghệ Rapid Air giảm 90% dầu mỡ thừa.',20,'1-2 ngày','https://bizweb.dktcdn.net/thumb/1024x1024/100/386/618/products/philips-hd9280-90-62-lit-1.png?v=1684820186830','Nồi Chiên Không Dầu Philips',3450000.00,NULL,'Philips Gia Dụng',NULL,25,'2026-05-23 01:56:26.000000',7),(51,'2026-05-23 01:56:26.000000','Chất liệu thủy tinh chịu nhiệt Borosilicate, đèn LED xanh khi đun sôi.',0,'2 ngày','https://product.hstatic.net/200000333181/product/kt-2179g_0ca40c2f600347d0b75e65d51f83eaed.png','Ấm Siêu Tốc Thủy Tinh Khóa',299000.00,NULL,'Lock&Lock Store',NULL,90,'2026-05-23 01:56:26.000000',7),(52,'2026-05-23 01:56:26.000000','Kiểm soát dầu nhờn hiệu quả, màng lọc XL-Protect chống tia UV tối ưu.',10,'2 ngày','https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=500','Kem Chống Nắng La Roche-Posay',480000.00,NULL,'La Roche-Posay Official',NULL,200,'2026-05-23 01:56:26.000000',8),(53,'2026-05-23 01:56:26.000000','Hương thơm gỗ thơm nồng nàn, nam tính sang trọng, lưu hương 8-12 tiếng.',0,'3 ngày','https://images.unsplash.com/photo-1541643600914-78b084683601?w=500','Nước Hoa Nam Bleu De Chanel',3950000.00,NULL,'Chanel Boutique',NULL,15,'2026-05-23 01:56:26.000000',8),(54,'2026-05-23 01:56:26.000000','Chất son mịn mượt môi, bảng màu trendy tôn da, giữ màu suốt cả ngày.',15,'1-2 ngày','https://images.unsplash.com/photo-1586495777744-4413f21062fa?w=500','Son Kem Lì Black Rouge',165000.00,NULL,'Black Rouge Authentic',NULL,150,'2026-05-23 01:56:26.000000',8),(55,'2026-05-23 01:56:26.000000','Hỗ trợ đệm lưng điều chỉnh cột sống, lưới thoáng khí, ngả lưng 135 độ.',12,'3-4 ngày','https://images.unsplash.com/photo-1505797149-43b0069ec26b?w=500','Ghế Công Thái Học Ergonomic',2890000.00,NULL,'Vua Ghế Văn Phòng',NULL,40,'2026-05-23 01:56:26.000000',9),(56,'2026-05-23 01:56:26.000000','Mặt gỗ MDF phủ Melamine chống trầy, khung sắt hộp sơn tĩnh điện chắc chắn.',0,'2-3 ngày','https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?w=500','Bàn Làm Việc Gỗ Chữ K',450000.00,NULL,'Nội Thất Hiện Đại',NULL,75,'2026-05-23 01:56:26.000000',9),(57,'2026-05-23 01:56:26.000000','Tạo hiệu ứng ánh sáng hoàng hôn siêu chill, xoay 360 độ chụp ảnh sống ảo.',5,'2 ngày','https://images.unsplash.com/photo-1565814636199-ae8133055c1c?w=500','Đèn LED Hoàng Hôn Decor',120000.00,NULL,'Xưởng Đèn Decor',NULL,180,'2026-05-23 01:56:26.000000',9),(58,'2026-05-23 01:56:26.000000','Chất liệu kim loại cao cấp, màn hình LED hiển thị điện áp, 2 cổng sạc nhanh.',10,'2 ngày','https://product.hstatic.net/1000152881/product/z3437965909500_1ff5c10fc1ab64ba5465820b1270f350_005a29ab6c7a48ae9920c40a63799e98_large.jpg','Tẩu Sạc Nhanh Ô Tô Baseus 65W',280000.00,NULL,'Baseus Car Mall',NULL,110,'2026-05-23 01:56:26.000000',10),(59,'2026-05-23 01:56:26.000000','Lực hút mạnh mẽ 6000Pa, thiết kế không dây pin sạc, hút sạch ngóc ngách xe.',15,'2-3 ngày','https://hadoauto.com/wp-content/uploads/2019/10/may-hut-bui-o-to-mini-cam-tay-vacuum-cleaner.jpg','Máy Hút Bụi Cầm Tay Mini',350000.00,NULL,'Phụ Kiện Xe Hơi Pro',NULL,65,'2026-05-23 01:56:26.000000',10),(60,'2026-05-23 01:56:26.000000','Chiết xuất thiên nhiên nhập khẩu Bulgaria, hương thơm dịu nhẹ chống say xe.',0,'3 ngày','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS2fre329Iz66e1MjQdLnql0tsrVfDPTKRpIQ&s','Nước Hoa Khử Mùi Ô Tô Areon',220000.00,NULL,'Areon Official',NULL,130,'2026-05-23 01:56:26.000000',10);
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reviews`
--

DROP TABLE IF EXISTS `reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reviews` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `comment` text,
  `created_at` datetime(6) DEFAULT NULL,
  `rating` int NOT NULL,
  `product_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKpl51cejpw4gy5swfar8br9ngi` (`product_id`),
  KEY `FKcgy7qjc1r99dp117y9en6lxye` (`user_id`),
  CONSTRAINT `FKcgy7qjc1r99dp117y9en6lxye` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKpl51cejpw4gy5swfar8br9ngi` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reviews`
--

LOCK TABLES `reviews` WRITE;
/*!40000 ALTER TABLE `reviews` DISABLE KEYS */;
INSERT INTO `reviews` VALUES (1,'gbivracufrzk',NULL,5,24,1,'Nguyễn Văn Kiên'),(2,'nxnfjf',NULL,5,29,1,'Nguyễn Văn Kiên');
/*!40000 ALTER TABLE `reviews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` text,
  `avatar` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `role` varchar(255) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UK_du5v5sr43g5bfnji4vb8hg5s3` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'Quảng Bình 73','https://theselfishmeme.co.uk/wp-content/uploads/2025/11/anh-avatar-meme-cute-de-thuong-1.webp','2026-05-21 20:52:18.184021','vankien55@gmail.com','Van Kien','$2a$10$aN/bZSpVuZIk7sVys6leautB1I//agN../J8nJrlKI3gjPbfpVatS','0782794280','user','2026-05-22 18:59:06.598970'),(2,'tphcm',NULL,'2026-05-22 03:34:02.799050','vantuan55@gmail.com','nguyen van ','$2a$10$m8IxussD3He59j5wNKc.5.hnXy070cQ/a02gcPNti8rQjxSNbY9fi','09873468379','user','2026-05-22 03:34:02.799050'),(3,'hanoi',NULL,'2026-05-22 03:40:06.021862','vankiet55@gmail.com','vankiet','$2a$10$ib61CHto2ZGEvQ2xYEskM.iBzHsrrlbcAytZFrNwnkWnFLGrP5opa','0785673849','user','2026-05-22 03:40:06.021862'),(4,'quảng trị','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ5bzJ2eaNLOqrxoXx7HVeolS4lHJTpuIr-Sw&s','2026-05-23 04:35:52.772655','anhthu55@gmail.com','anhthu','$2a$10$J2WXG1Iudo/bRuCh9SuM7u2p3S54BmymAJkgEVZhfP2O9dKqsmIsC','0783782498','user','2026-05-23 04:35:52.772655');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-23 15:21:57
