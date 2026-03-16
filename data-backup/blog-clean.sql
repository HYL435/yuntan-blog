
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

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `blog-article` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `blog-article`;
DROP TABLE IF EXISTS `article`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article` (
  `id` bigint NOT NULL COMMENT '主键ID雪花',
  `title` varchar(128) DEFAULT NULL COMMENT '文章标题',
  `summary` varchar(512) DEFAULT '' COMMENT '文章摘要，列表页展示',
  `cover_img` varchar(255) DEFAULT '' COMMENT '文章封面图URL',
  `mongo_id` varchar(24) DEFAULT NULL COMMENT 'MongoDB的id',
  `author_id` bigint NOT NULL COMMENT '关联用户表ID（作者）',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '文章状态：0-草稿，1-已发布，2-私密（默认草稿）',
  `is_top` tinyint NOT NULL DEFAULT '0' COMMENT '是否置顶：0-否，1-是（默认0否）',
  `is_original` tinyint NOT NULL DEFAULT '1' COMMENT '是否原创：0-转载，1-原创（默认1原创）',
  `view_count` int NOT NULL DEFAULT '0' COMMENT '文章浏览量（缓存同步）',
  `like_count` int NOT NULL DEFAULT '0' COMMENT '文章点赞数（缓存同步）',
  `collect_count` int NOT NULL DEFAULT '0' COMMENT '文章收藏数（缓存同步）',
  `comment_count` int NOT NULL DEFAULT '0' COMMENT '文章评论数（缓存同步）',
  `keywords` varchar(128) DEFAULT '' COMMENT '文章关键词（SEO用）',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间（草稿转发布时填充，允许为空）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '软删除：0-未删，1-已删',
  PRIMARY KEY (`id`),
  KEY `idx_author_id` (`author_id`) COMMENT '按作者查询文章索引',
  KEY `idx_status` (`status`) COMMENT '按文章状态查询索引',
  KEY `idx_is_top` (`is_top`) COMMENT '按置顶状态查询索引',
  KEY `idx_publish_time` (`publish_time`) COMMENT '按发布时间排序索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `article` WRITE;
/*!40000 ALTER TABLE `article` DISABLE KEYS */;
INSERT INTO `article` VALUES (1719288200000000001,'SpringBoot整合MyBatis完整教程','详细讲解SpringBoot如何整合MyBatis，包括配置、CRUD等','https://img.example.com/1.jpg','gjygiuho5555',1719288900000000001,0,0,1,0,0,0,0,'SpringBoot,MyBatis','2026-01-29 21:54:03','2026-01-27 16:59:03','2026-01-29 21:54:08',0),(1719288200000000002,'MySQL索引优化实战','从实战角度讲解MySQL索引失效场景及优化方法','https://img.example.com/2.jpg',NULL,1719288900000000001,1,1,1,1250,89,67,32,'MySQL,索引优化,性能','2026-01-20 16:59:03','2026-01-20 16:59:03','2026-02-24 02:34:54',0),(1719288200000000003,'个人项目架构设计笔记','记录个人项目的架构设计思路和踩坑点','https://img.example.com/3.jpg',NULL,1719288900000000002,1,1,1,56,12,8,3,'架构设计,微服务','2026-01-17 16:59:03','2026-01-17 16:59:03','2026-02-24 20:35:47',0),(1719288200000000004,'Vue2迁移Vue3核心变化','总结Vue2升级到Vue3的核心语法变化和注意事项','https://img.example.com/4.jpg',NULL,1719288900000000003,2,0,1,890,56,42,18,'Vue2,Vue3,迁移','2026-01-12 16:59:03','2026-01-12 16:59:03','2026-01-27 16:59:03',0),(1719288200000000005,'分布式锁的几种实现方式','讲解基于Redis、Zookeeper、数据库的分布式锁实现','https://img.example.com/5.jpg',NULL,1719288900000000001,1,0,1,780,45,33,15,'分布式锁,Redis,Zookeeper','2026-01-22 16:59:03','2026-01-22 16:59:03','2026-01-27 16:59:03',0),(1719288200000000006,'SpringBoot性能调优技巧','从JVM、连接池、缓存等方面优化SpringBoot应用','https://img.example.com/6.jpg',NULL,1719288900000000002,1,0,1,650,38,29,11,'SpringBoot,性能调优,JVM','2026-01-24 16:59:03','2026-01-24 16:59:03','2026-01-27 16:59:03',0),(1719288200000000007,'MySQL事务隔离级别详解','深入理解MySQL的4种事务隔离级别及幻读、脏读问题','https://img.example.com/7.jpg',NULL,1719288900000000003,1,0,1,920,67,48,23,'MySQL,事务,隔离级别','2026-01-19 16:59:03','2026-01-19 16:59:03','2026-01-27 16:59:03',0),(1719288200000000008,'Vue3组合式API实战','用组合式API重构Vue2项目，提升代码复用性','https://img.example.com/8.jpg',NULL,1719288900000000001,1,0,0,850,52,37,19,'Vue3,组合式API,重构','2026-01-23 16:59:03','2026-01-23 16:59:03','2026-01-27 16:59:03',0),(1719288200000000009,'微服务网关Gateway配置','SpringCloud Gateway的路由、过滤器、跨域配置','https://img.example.com/9.jpg',NULL,1719288900000000002,1,0,1,580,29,21,9,'Gateway,微服务,网关','2026-01-21 16:59:03','2026-01-21 16:59:03','2026-01-27 16:59:03',0),(1719288200000000010,'前端工程化之Webpack配置','从零配置Webpack，实现代码分割、热更新等','https://img.example.com/10.jpg',NULL,1719288900000000003,1,0,1,720,41,30,14,'Webpack,前端工程化','2026-01-18 16:59:03','2026-01-18 16:59:03','2026-01-27 16:59:03',0),(1719288200000000011,'Redis缓存穿透解决方案','分析缓存穿透原因，给出布隆过滤器、空值缓存等方案','https://img.example.com/11.jpg',NULL,1719288900000000001,1,0,1,810,53,39,20,'Redis,缓存穿透,布隆过滤器','2026-01-25 16:59:03','2026-01-25 16:59:03','2026-01-27 16:59:03',0),(1719288200000000012,'Java8 Stream API实战','通过案例讲解Stream API的常用操作和性能优化','https://img.example.com/12.jpg',NULL,1719288900000000002,1,0,1,690,42,28,12,'Java8,Stream,流式编程','2026-01-16 16:59:03','2026-01-16 16:59:03','2026-01-27 16:59:03',0),(1719288200000000013,'SpringBoot整合Redis缓存','实现SpringBoot中Redis的缓存注解和手动缓存','https://img.example.com/13.jpg',NULL,1719288900000000003,1,0,1,750,47,34,17,'SpringBoot,Redis,缓存','2026-01-26 16:59:03','2026-01-26 16:59:03','2026-01-27 16:59:03',0),(1719288200000000014,'MySQL分库分表实战','基于Sharding-JDBC实现MySQL的分库分表','https://img.example.com/14.jpg',NULL,1719288900000000001,1,0,1,950,71,52,25,'MySQL,分库分表,Sharding-JDBC','2026-01-15 16:59:03','2026-01-15 16:59:03','2026-01-27 16:59:03',0),(1719288200000000015,'Vue3+Vite项目搭建','快速搭建Vue3+Vite+Pinia的前端项目','https://img.example.com/15.jpg',NULL,1719288900000000002,1,0,0,880,59,41,21,'Vue3,Vite,Pinia','2026-01-14 16:59:03','2026-01-14 16:59:03','2026-01-27 16:59:03',0),(1719288200000000016,'分布式事务解决方案','讲解Seata、TCC、SAGA等分布式事务方案','https://img.example.com/16.jpg',NULL,1719288900000000003,1,0,1,820,63,45,22,'分布式事务,Seata,TCC','2026-01-13 16:59:03','2026-01-13 16:59:03','2026-01-27 16:59:03',0),(1719288200000000017,'SpringBoot接口防刷实现','基于Redis实现接口限流和防刷功能','https://img.example.com/17.jpg',NULL,1719288900000000001,1,0,1,730,44,32,16,'SpringBoot,接口防刷,限流','2026-01-11 16:59:03','2026-01-11 16:59:03','2026-01-27 16:59:03',0),(1719288200000000018,'MySQL慢查询优化','定位和优化MySQL慢查询的完整流程','https://img.example.com/18.jpg',NULL,1719288900000000002,1,0,1,870,68,49,24,'MySQL,慢查询,优化','2026-01-10 16:59:03','2026-01-10 16:59:03','2026-01-27 16:59:03',0),(1719288200000000019,'前端性能优化之懒加载','实现图片、组件的懒加载，提升页面加载速度','https://img.example.com/19.jpg',NULL,1719288900000000003,1,0,1,670,39,27,13,'前端,性能优化,懒加载','2026-01-09 16:59:03','2026-01-09 16:59:03','2026-01-27 16:59:03',0),(1719288200000000020,'Java并发编程之线程池','深入理解线程池的核心参数和使用场景','https://img.example.com/20.jpg',NULL,1719288900000000001,1,0,1,910,75,55,27,'Java,并发,线程池','2026-01-08 16:59:03','2026-01-08 16:59:03','2026-01-27 16:59:03',0),(2024501931031597057,'Spring Boot入门指南','本文介绍了Spring Boot的基础知识...','https://yuntan-blog.oss-cn-hangzhou.aliyuncs.com/default/cover/default-article-cover.jpg','5f9a1b9b0f9a1b9b0f9a1b9b',2023314822666526722,1,1,1,0,0,0,0,'spring boot,java,framework','2026-02-19 15:17:04','2026-02-19 15:10:48','2026-02-24 15:45:47',0),(2025233002412310529,'Spring Boot 3.2 从入门到精通：核心特性与实战开发','本文详细讲解了Spring Boot 3.2的核心特性，包括自动配置、Starter机制、外部化配置、Actuator监控等，并通过多个实战案例演示如何快速构建企业级应用，同时涵盖了性能优化、安全防护和部署上线的最佳实践。','https://yuntan-blog.oss-cn-hangzhou.aliyuncs.com/default/cover/default-article-cover.jpg','6999d0d58aaad30ac42fad21',2023314822666526722,1,1,1,0,0,0,0,'Spring Boot,Java,后端开发,自动配置,Starter,RESTful API,性能优化,部署','2026-02-21 23:36:36','2026-02-21 15:35:49','2026-02-24 02:09:56',0);
/*!40000 ALTER TABLE `article` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `article_audit_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article_audit_log` (
  `id` bigint NOT NULL COMMENT '主键雪花ID',
  `user_id` bigint NOT NULL COMMENT '操作人ID，空字符串=系统操作（适配雪花ID）',
  `action` varchar(100) NOT NULL COMMENT '操作动作：ARTICLE_ADD、ARTICLE_UPDATE、ARTICLE_DELETE、CATEGORY_EDIT',
  `action_details` json DEFAULT (json_array()) COMMENT '操作详情，JSON格式存储',
  `operate_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `ip` varchar(32) DEFAULT '' COMMENT '操作IP地址',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`) COMMENT '按操作人查询索引',
  KEY `idx_operate_time` (`operate_time`) COMMENT '按操作时间查询索引',
  KEY `idx_action` (`action`) COMMENT '按操作类型查询索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `article_audit_log` WRITE;
/*!40000 ALTER TABLE `article_audit_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `article_audit_log` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `article_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article_category` (
  `id` bigint NOT NULL COMMENT '主键，雪花ID',
  `article_id` bigint NOT NULL COMMENT '关联文章表ID（唯一）',
  `category_id` bigint NOT NULL COMMENT '关联分类表ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关联创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_article_id` (`article_id`) COMMENT '一篇文章只能关联一个分类',
  KEY `idx_category_id` (`category_id`) COMMENT '查询某个分类下的所有文章'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文章-分类关联表（仅支持单分类）';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `article_category` WRITE;
/*!40000 ALTER TABLE `article_category` DISABLE KEYS */;
INSERT INTO `article_category` VALUES (1719288300000000001,1719288200000000001,1719288000000000004,'2026-01-27 15:38:57'),(1719288300000000002,1719288200000000002,1719288000000000002,'2026-01-20 15:38:57'),(1719288300000000003,1719288200000000003,1719288000000000001,'2026-01-17 15:38:57'),(1719288300000000004,1719288200000000004,1719288000000000003,'2026-01-12 15:38:57'),(1719288300000000005,1719288200000000005,1719288000000000001,'2026-01-22 15:38:57'),(1719288300000000006,1719288200000000006,1719288000000000004,'2026-01-24 15:38:57'),(1719288300000000007,1719288200000000007,1719288000000000002,'2026-01-19 15:38:57'),(1719288300000000008,1719288200000000008,1719288000000000003,'2026-01-23 15:38:57'),(1719288300000000009,1719288200000000009,1719288000000000001,'2026-01-21 15:38:57'),(1719288300000000010,1719288200000000010,1719288000000000003,'2026-01-18 15:38:57'),(1719288300000000011,1719288200000000011,1719288000000000001,'2026-01-25 15:38:57'),(1719288300000000012,1719288200000000012,1719288000000000001,'2026-01-16 15:38:57'),(1719288300000000013,1719288200000000013,1719288000000000004,'2026-01-26 15:38:57'),(1719288300000000014,1719288200000000014,1719288000000000002,'2026-01-15 15:38:57'),(1719288300000000015,1719288200000000015,1719288000000000003,'2026-01-14 15:38:57'),(1719288300000000016,1719288200000000016,1719288000000000001,'2026-01-13 15:38:57'),(1719288300000000017,1719288200000000017,1719288000000000004,'2026-01-11 15:38:57'),(1719288300000000018,1719288200000000018,1719288000000000002,'2026-01-10 15:38:57'),(1719288300000000019,1719288200000000019,1719288000000000003,'2026-01-09 15:38:57'),(1719288300000000020,1719288200000000020,1719288000000000001,'2026-01-08 15:38:57'),(2025233002827546626,2025233002412310529,1719288000000000001,'2026-02-21 15:35:50'),(2026201304698376193,2024501931031597057,1719288000000000002,'2026-02-24 07:43:31');
/*!40000 ALTER TABLE `article_category` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `article_collect`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article_collect` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '关联用户表id',
  `article_id` bigint NOT NULL COMMENT '关联文章表id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_article` (`user_id`,`article_id`) COMMENT '防同一用户重复收藏同一文章',
  KEY `idx_user_id` (`user_id`) COMMENT '查询用户收藏列表索引',
  KEY `idx_article_id` (`article_id`) COMMENT '查询文章收藏列表索引'
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `article_collect` WRITE;
/*!40000 ALTER TABLE `article_collect` DISABLE KEYS */;
/*!40000 ALTER TABLE `article_collect` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `article_like`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article_like` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '关联用户表id',
  `article_id` bigint NOT NULL COMMENT '关联文章表id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_article` (`user_id`,`article_id`) COMMENT '防同一用户重复点赞同一文章',
  KEY `idx_article_id` (`article_id`) COMMENT '查询文章点赞列表索引'
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `article_like` WRITE;
/*!40000 ALTER TABLE `article_like` DISABLE KEYS */;
/*!40000 ALTER TABLE `article_like` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `article_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article_tag` (
  `id` bigint NOT NULL COMMENT '主键雪花ID',
  `article_id` bigint NOT NULL COMMENT '关联文章表ID',
  `tag_id` bigint NOT NULL COMMENT '关联标签表ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关联创建时间（绑定标签的时间）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_article_tag` (`article_id`,`tag_id`) COMMENT '防重复关联',
  KEY `idx_article_id` (`article_id`) COMMENT '查询文章的所有标签',
  KEY `idx_tag_id` (`tag_id`) COMMENT '查询标签下的所有文章',
  KEY `idx_create_time` (`create_time`) COMMENT '按绑定时间筛选（可选，非刚需）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文章-标签关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `article_tag` WRITE;
/*!40000 ALTER TABLE `article_tag` DISABLE KEYS */;
INSERT INTO `article_tag` VALUES (1719288400000000001,1719288200000000001,1719288100000000001,'2026-01-27 15:38:58'),(1719288400000000002,1719288200000000001,1719288100000000002,'2026-01-27 15:38:58'),(1719288400000000003,1719288200000000002,1719288100000000003,'2026-01-20 15:38:58'),(1719288400000000004,1719288200000000002,1719288100000000008,'2026-01-20 15:38:58'),(1719288400000000005,1719288200000000003,1719288100000000005,'2026-01-17 15:38:58'),(1719288400000000006,1719288200000000003,1719288100000000006,'2026-01-17 15:38:58'),(1719288400000000007,1719288200000000004,1719288100000000004,'2026-01-12 15:38:58'),(1719288400000000008,1719288200000000004,1719288100000000007,'2026-01-12 15:38:58'),(1719288400000000009,1719288200000000005,1719288100000000005,'2026-01-22 15:38:58'),(1719288400000000010,1719288200000000005,1719288100000000008,'2026-01-22 15:38:58'),(1719288400000000011,1719288200000000006,1719288100000000002,'2026-01-24 15:38:58'),(1719288400000000012,1719288200000000006,1719288100000000008,'2026-01-24 15:38:58'),(1719288400000000013,1719288200000000007,1719288100000000003,'2026-01-19 15:38:58'),(1719288400000000014,1719288200000000007,1719288100000000008,'2026-01-19 15:38:58'),(1719288400000000015,1719288200000000008,1719288100000000004,'2026-01-23 15:38:58'),(1719288400000000016,1719288200000000008,1719288100000000007,'2026-01-23 15:38:58'),(1719288400000000017,1719288200000000009,1719288100000000006,'2026-01-21 15:38:58'),(1719288400000000018,1719288200000000009,1719288100000000005,'2026-01-21 15:38:58'),(1719288400000000019,1719288200000000010,1719288100000000007,'2026-01-18 15:38:58'),(1719288400000000020,1719288200000000010,1719288100000000008,'2026-01-18 15:38:58'),(1719288400000000021,1719288200000000011,1719288100000000005,'2026-01-25 15:38:58'),(1719288400000000022,1719288200000000011,1719288100000000008,'2026-01-25 15:38:58'),(1719288400000000023,1719288200000000012,1719288100000000001,'2026-01-16 15:38:58'),(1719288400000000024,1719288200000000012,1719288100000000008,'2026-01-16 15:38:58'),(1719288400000000025,1719288200000000013,1719288100000000002,'2026-01-26 15:38:58'),(1719288400000000026,1719288200000000013,1719288100000000008,'2026-01-26 15:38:58'),(1719288400000000027,1719288200000000014,1719288100000000003,'2026-01-15 15:38:58'),(1719288400000000028,1719288200000000014,1719288100000000008,'2026-01-15 15:38:58'),(1719288400000000029,1719288200000000015,1719288100000000004,'2026-01-14 15:38:58'),(1719288400000000030,1719288200000000015,1719288100000000007,'2026-01-14 15:38:58'),(1719288400000000031,1719288200000000016,1719288100000000005,'2026-01-13 15:38:58'),(1719288400000000032,1719288200000000016,1719288100000000006,'2026-01-13 15:38:58'),(1719288400000000033,1719288200000000017,1719288100000000002,'2026-01-11 15:38:58'),(1719288400000000034,1719288200000000017,1719288100000000008,'2026-01-11 15:38:58'),(1719288400000000035,1719288200000000018,1719288100000000003,'2026-01-10 15:38:58'),(1719288400000000036,1719288200000000018,1719288100000000008,'2026-01-10 15:38:58'),(1719288400000000037,1719288200000000019,1719288100000000004,'2026-01-09 15:38:58'),(1719288400000000038,1719288200000000019,1719288100000000007,'2026-01-09 15:38:58'),(1719288400000000039,1719288200000000020,1719288100000000001,'2026-01-08 15:38:58'),(1719288400000000040,1719288200000000020,1719288100000000008,'2026-01-08 15:38:58'),(2025233002877878273,2025233002412310529,1719288100000000001,'2026-02-21 15:35:50'),(2025233002898849793,2025233002412310529,1719288100000000002,'2026-02-21 15:35:50'),(2026201304719347713,2024501931031597057,1719288100000000001,'2026-02-24 07:43:31'),(2026201304727736321,2024501931031597057,1719288100000000002,'2026-02-24 07:43:31');
/*!40000 ALTER TABLE `article_tag` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `article_view`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article_view` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint DEFAULT NULL COMMENT '关联用户表id，空=匿名用户',
  `article_id` bigint NOT NULL COMMENT '关联文章表id',
  `ip` varchar(32) NOT NULL DEFAULT '' COMMENT '访客IP，匿名用户去重依据',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
  PRIMARY KEY (`id`),
  KEY `idx_article_id` (`article_id`) COMMENT '查询文章浏览列表索引',
  KEY `idx_create_time` (`create_time`) COMMENT '按浏览时间统计索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `article_view` WRITE;
/*!40000 ALTER TABLE `article_view` DISABLE KEYS */;
/*!40000 ALTER TABLE `article_view` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `id` bigint NOT NULL COMMENT '主键雪花ID',
  `category_name` varchar(32) NOT NULL COMMENT '分类名称',
  `sort` bigint NOT NULL COMMENT '排序权重，越大越靠前',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '软删除：0-未删，1-已删',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_category_name` (`category_name`) COMMENT '分类名称唯一'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文章分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (1719288000000000001,'Java后端',2,1,'2026-01-27 15:38:57','2026-02-24 12:49:04',0),(1719288000000000002,'MySQL',2,1,'2026-01-27 15:38:57','2026-01-28 18:43:05',0),(1719288000000000003,'前端开发',3,1,'2026-01-27 15:38:57','2026-01-28 18:43:05',0),(1719288000000000004,'SpringBoot',4,1,'2026-01-27 15:38:57','2026-01-28 18:43:05',0),(1719288000000000005,'Vue',5,0,'2026-01-27 15:38:57','2026-01-28 18:43:05',0),(2026209418411692033,'11',0,1,'2026-02-24 08:15:45','2026-02-24 12:51:40',1);
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tag` (
  `id` bigint NOT NULL COMMENT '主键雪花ID',
  `tag_name` varchar(32) NOT NULL COMMENT '标签名称',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：1-启用，0-禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '软删除：0-未删，1-已删',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tag_name` (`tag_name`) COMMENT '标签名称唯一'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文章标签表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `tag` WRITE;
/*!40000 ALTER TABLE `tag` DISABLE KEYS */;
INSERT INTO `tag` VALUES (1719288100000000001,'Java',1,'2026-01-27 15:38:57','2026-02-24 21:25:45',0),(1719288100000000002,'SpringBoot',1,'2026-01-27 15:38:57','2026-01-27 15:38:57',0),(1719288100000000003,'MySQL优化',1,'2026-01-27 15:38:57','2026-01-27 15:38:57',0),(1719288100000000004,'Vue3',1,'2026-01-27 15:38:57','2026-01-27 15:38:57',0),(1719288100000000005,'分布式',1,'2026-01-27 15:38:57','2026-01-27 15:38:57',0),(1719288100000000006,'微服务',1,'2026-01-27 15:38:57','2026-01-27 15:38:57',0),(1719288100000000007,'前端工程化',1,'2026-01-27 15:38:57','2026-01-27 15:38:57',0),(1719288100000000008,'性能优化',1,'2026-01-27 15:38:57','2026-01-27 15:38:57',0),(2026284921420169218,'11',1,'2026-02-24 13:15:47','2026-02-24 13:15:50',1),(2026285551140388865,'33',1,'2026-02-24 13:18:17','2026-02-24 13:18:23',0);
/*!40000 ALTER TABLE `tag` ENABLE KEYS */;
UNLOCK TABLES;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `blog-comment` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `blog-comment`;
DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment` (
  `id` bigint NOT NULL COMMENT '主键，雪花ID',
  `article_id` bigint NOT NULL COMMENT '关联文章表ID',
  `user_id` bigint NOT NULL COMMENT '评论人ID（关联用户表）',
  `parent_id` bigint DEFAULT NULL COMMENT '父评论ID：NULL=根评论，非空=回复某条评论',
  `to_user_id` bigint DEFAULT NULL COMMENT '回复目标用户ID：NULL=根评论，非空=回复某用户',
  `content` text NOT NULL COMMENT '评论内容',
  `image` varchar(255) NOT NULL DEFAULT '' COMMENT '评论附带图片',
  `like_count` int NOT NULL DEFAULT '0' COMMENT '评论点赞数',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '审核状态：0-待审核，1-审核通过，2-审核拒绝',
  `ip` varchar(32) NOT NULL DEFAULT '0.0.0.0' COMMENT '评论人IP地址',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间（修改评论内容时更新）',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '软删除：0-未删，1-已删',
  PRIMARY KEY (`id`),
  KEY `idx_article_status_time` (`article_id`,`status`,`create_time`) COMMENT '查询某篇文章下已审核的评论并按时间排序',
  KEY `idx_parent_id` (`parent_id`) COMMENT '查询某条评论的所有回复',
  KEY `idx_user_id` (`user_id`) COMMENT '查询某用户的所有评论',
  KEY `idx_create_time` (`create_time`) COMMENT '按评论时间排序（全站最新评论）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='评论主表（支持多级回复）';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
INSERT INTO `comment` VALUES (2026667221897490433,2025233002412310529,2023314822666526722,NULL,NULL,'1','',0,1,'172.20.0.7','2026-02-25 14:34:54','2026-02-25 14:34:54',0),(2026667372565278722,2025233002412310529,2023314822666526722,NULL,NULL,'111','',0,1,'172.20.0.7','2026-02-25 14:35:30','2026-02-25 14:35:30',0),(2026736531047256065,2025233002412310529,2023314822666526722,NULL,NULL,'2222','',0,1,'172.20.0.5','2026-02-25 19:10:19','2026-02-25 19:10:19',0),(2026736556288577538,2025233002412310529,2023314822666526722,2026736531047256065,NULL,'222','',0,2,'172.20.0.5','2026-02-25 19:10:25','2026-03-08 06:09:50',0),(2026736600811114498,2025233002412310529,2023314822666526722,2026736531047256065,NULL,'2222','',0,1,'172.20.0.5','2026-02-25 19:10:35','2026-02-25 19:10:35',0),(2026736623426801665,2025233002412310529,2023314822666526722,2026736531047256065,NULL,'333','',0,1,'172.20.0.5','2026-02-25 19:10:41','2026-02-25 19:10:41',0);
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `comment_audit_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment_audit_log` (
  `id` bigint NOT NULL COMMENT '主键，雪花ID',
  `comment_id` bigint NOT NULL COMMENT '关联评论表ID',
  `operator_id` bigint NOT NULL COMMENT '操作人ID（管理员/博主）',
  `action` varchar(50) NOT NULL COMMENT '操作类型：COMMENT_CHECK_PASS、COMMENT_CHECK_REJECT、COMMENT_DELETE',
  `action_details` json DEFAULT (json_array()) COMMENT '操作详情：如拒绝原因,默认为空数组[]',
  `ip` varchar(32) DEFAULT '' COMMENT '操作IP',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `idx_comment_id` (`comment_id`) COMMENT '查询某条评论的操作日志',
  KEY `idx_operator_id` (`operator_id`) COMMENT '查询某管理员的操作日志',
  KEY `idx_create_time` (`create_time`) COMMENT '按操作时间筛选'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='评论操作审计日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `comment_audit_log` WRITE;
/*!40000 ALTER TABLE `comment_audit_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `comment_audit_log` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `comment_like`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment_like` (
  `id` bigint NOT NULL COMMENT '主键，雪花ID',
  `comment_id` bigint NOT NULL COMMENT '关联评论表ID',
  `user_id` bigint NOT NULL COMMENT '点赞用户ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_comment_user` (`comment_id`,`user_id`) COMMENT '同一用户不能重复点赞同一条评论',
  KEY `idx_comment_id` (`comment_id`) COMMENT '查询某条评论的所有点赞',
  KEY `idx_user_id` (`user_id`) COMMENT '查询某用户的所有评论点赞'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='评论点赞关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `comment_like` WRITE;
/*!40000 ALTER TABLE `comment_like` DISABLE KEYS */;
/*!40000 ALTER TABLE `comment_like` ENABLE KEYS */;
UNLOCK TABLES;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `blog-interact` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `blog-interact`;
DROP TABLE IF EXISTS `article_collect`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article_collect` (
  `id` bigint NOT NULL COMMENT '主键，雪花ID',
  `user_id` bigint NOT NULL COMMENT '关联用户表id',
  `article_id` bigint NOT NULL COMMENT '关联文章表id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_article` (`user_id`,`article_id`) COMMENT '防同一用户重复收藏同一文章',
  KEY `idx_user_id` (`user_id`) COMMENT '查询用户收藏列表索引',
  KEY `idx_article_id` (`article_id`) COMMENT '查询文章收藏列表索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `article_collect` WRITE;
/*!40000 ALTER TABLE `article_collect` DISABLE KEYS */;
/*!40000 ALTER TABLE `article_collect` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `article_like`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article_like` (
  `id` bigint NOT NULL COMMENT '主键，雪花ID',
  `user_id` bigint NOT NULL COMMENT '关联用户表id',
  `article_id` bigint NOT NULL COMMENT '关联文章表id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_article` (`user_id`,`article_id`) COMMENT '防同一用户重复点赞同一文章',
  KEY `idx_article_id` (`article_id`) COMMENT '查询文章点赞列表索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `article_like` WRITE;
/*!40000 ALTER TABLE `article_like` DISABLE KEYS */;
/*!40000 ALTER TABLE `article_like` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `article_view`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article_view` (
  `id` bigint NOT NULL COMMENT '主键，雪花ID',
  `user_id` bigint DEFAULT NULL COMMENT '关联用户表id，空字符串=匿名用户',
  `article_id` bigint NOT NULL COMMENT '关联文章表id',
  `ip` varchar(32) NOT NULL DEFAULT '' COMMENT '访客IP，匿名用户去重依据',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ip_article` (`ip`,`article_id`) COMMENT '匿名用户防重复浏览',
  UNIQUE KEY `uk_user_article` (`user_id`,`article_id`) COMMENT '登录用户防重复浏览',
  KEY `idx_article_id` (`article_id`) COMMENT '查询文章浏览列表索引',
  KEY `idx_create_time` (`create_time`) COMMENT '按浏览时间统计索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `article_view` WRITE;
/*!40000 ALTER TABLE `article_view` DISABLE KEYS */;
/*!40000 ALTER TABLE `article_view` ENABLE KEYS */;
UNLOCK TABLES;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `blog-notify` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `blog-notify`;
DROP TABLE IF EXISTS `audit_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audit_log` (
  `id` bigint NOT NULL COMMENT '主键雪花ID',
  `user_id` bigint NOT NULL COMMENT '操作人ID，空字符串=系统操作（适配雪花ID）',
  `action` varchar(100) NOT NULL COMMENT '操作动作：EMAIL_SEND、SMS_SEND、MSG_PUSH',
  `action_details` json DEFAULT (json_array()) COMMENT '操作详情，JSON格式存储',
  `operate_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `ip` varchar(32) DEFAULT '' COMMENT '操作IP地址',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`) COMMENT '按操作人查询索引',
  KEY `idx_operate_time` (`operate_time`) COMMENT '按操作时间查询索引',
  KEY `idx_action` (`action`) COMMENT '按操作类型查询索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `audit_log` WRITE;
/*!40000 ALTER TABLE `audit_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `audit_log` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `msg_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `msg_template` (
  `id` bigint NOT NULL COMMENT '主键雪花ID',
  `template_code` varchar(50) NOT NULL COMMENT '模版唯一编码，如ARTICLE_LIKE',
  `template_name` varchar(100) NOT NULL COMMENT '模版名称',
  `msg_content` text NOT NULL COMMENT '模版内容，支持占位符：{userNick}、{articleTitle}',
  `msg_type` tinyint NOT NULL COMMENT '消息类型：1系统通知，2互动通知，3邮件，4短信',
  `send_channel` tinyint NOT NULL COMMENT '发送渠道：1-站内信，2-邮件，3-短信，4-多渠道',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0停用，1启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_template_code` (`template_code`) COMMENT '模板编码唯一',
  KEY `idx_msg_type` (`msg_type`) COMMENT '按消息类型查询索引',
  KEY `idx_status` (`status`) COMMENT '按模版状态查询索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `msg_template` WRITE;
/*!40000 ALTER TABLE `msg_template` DISABLE KEYS */;
/*!40000 ALTER TABLE `msg_template` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `user_notify`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_notify` (
  `id` bigint NOT NULL COMMENT '主键雪花ID',
  `user_id` bigint NOT NULL COMMENT '接收通知用户ID',
  `template_code` varchar(50) NOT NULL COMMENT '关联消息模板编码',
  `msg_content` text NOT NULL COMMENT '实际发送的消息内容（替换占位符后）',
  `msg_type` tinyint NOT NULL COMMENT '消息类型：1系统通知，2互动通知，3邮件，4短信',
  `send_status` tinyint NOT NULL DEFAULT '0' COMMENT '发送状态：0-待发送，1-发送成功，2-发送失败',
  `send_time` datetime DEFAULT NULL COMMENT '发送时间',
  `read_status` tinyint NOT NULL DEFAULT '0' COMMENT '阅读状态：0-未读，1-已读',
  `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`) COMMENT '按用户查询通知索引',
  KEY `idx_send_status` (`send_status`) COMMENT '按发送状态查询索引',
  KEY `idx_read_status` (`read_status`) COMMENT '按阅读状态查询索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户通知记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `user_notify` WRITE;
/*!40000 ALTER TABLE `user_notify` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_notify` ENABLE KEYS */;
UNLOCK TABLES;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `blog-user` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `blog-user`;
DROP TABLE IF EXISTS `use_audit_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `use_audit_log` (
  `id` bigint NOT NULL COMMENT '主键雪花ID',
  `user_id` bigint NOT NULL COMMENT '操作人ID，空=系统操作（适配雪花ID）',
  `action` varchar(100) NOT NULL COMMENT '操作动作：LOGIN_SUCCESS、LOGIN_FAIL、REGISTER、PWD_UPDATE',
  `action_details` json DEFAULT (json_array()) COMMENT '操作详情，JSON格式存储',
  `operate_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `ip` varchar(32) DEFAULT '' COMMENT '操作IP地址',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`) COMMENT '按操作人查询索引',
  KEY `idx_operate_time` (`operate_time`) COMMENT '按操作时间查询索引',
  KEY `idx_action` (`action`) COMMENT '按操作类型查询索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `use_audit_log` WRITE;
/*!40000 ALTER TABLE `use_audit_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `use_audit_log` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL COMMENT '唯一主键标识，雪花ID',
  `username` varchar(32) NOT NULL COMMENT '用户名',
  `password` varchar(64) NOT NULL COMMENT '密码，BCrypt加密后存储',
  `nickname` varchar(32) DEFAULT NULL COMMENT '博客展示名称，区别于用户名',
  `image` varchar(255) NOT NULL DEFAULT '' COMMENT '用户头像URL',
  `email` varchar(45) NOT NULL COMMENT '邮箱，找回密码+接收通知',
  `phone` varchar(11) DEFAULT NULL COMMENT '手机号',
  `intro` varchar(200) DEFAULT '' COMMENT '个人简介',
  `role` tinyint NOT NULL DEFAULT '2' COMMENT '权限：0博主，1管理员，2普通访客（默认为2普通访客）',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '账号状态（1-正常，2-禁用）',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '软删除（1-已删，0-未删）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`) COMMENT '用户名唯一，加速登录/按用户名查询',
  UNIQUE KEY `uk_email` (`email`) COMMENT '邮箱唯一，加速邮箱登录/找回密码',
  UNIQUE KEY `uk_phone` (`phone`) COMMENT '手机号唯一，加速按手机号查询',
  UNIQUE KEY `uk_nickname` (`nickname`) COMMENT '昵称唯一，加速前台按昵称搜索',
  KEY `idx_role` (`role`) COMMENT '按角色筛选（博主/管理员），提速后台查询',
  KEY `idx_status` (`status`) COMMENT '按账号状态筛选（正常/禁用），提速登录校验/后台查询'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (2015047941551136769,'admain1571','$2a$10$lIP9aWq6RHsrO0d7uN03ceVJlmUSKgZIer78xEdCbxxitgafGI7jK','lm1sls1@126.com','https://yuntan-blog.oss-cn-hangzhou.aliyuncs.com/default/avatar/default-avatar.jpg','lm1sls1@126.com',NULL,'',1,1,'2026-01-25 14:09:48','2026-01-24 21:04:02','2026-02-23 14:11:04',0),(2015686935931133953,'k8qscpiul81','$2a$10$Y8olwvm/ckRREHC/AeSjUOKYEvBR7wAFTFTGBoDHVXdA3xI2GNFr6','lm1sds81@126.com','https://yuntan-blog.oss-cn-hangzhou.aliyuncs.com/default/avatar/default-avatar.jpg','lm1sds81@126.com',NULL,'',2,1,'2026-02-16 07:52:23','2026-01-26 07:23:10','2026-02-24 12:32:34',0),(2023314822666526722,'duanqiaohuayuluo','$2a$10$GyyUeOZiSsEPT1DJ5CPquOzuaCMkmpCTKklAH74Fe87B0A3v4lof.','苜玥','https://yuntan-blog.oss-cn-hangzhou.aliyuncs.com/default/avatar/default-avatar.jpg','2929028544@qq.com','','博主',0,1,'2026-03-08 05:45:49','2026-02-16 08:33:40','2026-03-08 05:45:49',0),(2027410641132322818,'ksdhjcisudvhkjv','$2a$10$t8fWnDCxuaP5BfKahJq54u4601LjC8maa0r75svPdblohrExTzMi2','2758226728@qq.com','https://yuntan-blog.oss-cn-hangzhou.aliyuncs.com/default/avatar/default-avatar.jpg','2758226728@qq.com',NULL,'',2,1,'2026-02-27 15:54:54','2026-02-27 15:48:59','2026-02-27 15:54:54',0),(2027411995133345794,'quwtigdb','$2a$10$ILf3yyqTcyUX7wNMZYMNm.LetKKCInivENnTGS2jPJCMkEQLs/HWK','25256459866@qq.com','https://yuntan-blog.oss-cn-hangzhou.aliyuncs.com/default/avatar/default-avatar.jpg','25256459866@qq.com',NULL,'',2,1,'2026-02-27 15:54:22','2026-02-27 15:54:22','2026-02-27 15:54:22',0);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

