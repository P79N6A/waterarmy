/*
SQLyog Ultimate v11.42 (64 bit)
MySQL - 5.7.17-log : Database - shuijun
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`shuijun` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;

USE `shuijun`;

/*Table structure for table `account` */

DROP TABLE IF EXISTS `account`;

CREATE TABLE `account` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `UUID` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账号UUID',
  `user_name` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账号名',
  `full_name` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账号中文名',
  `password` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账号密码',
  `mobile` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `email` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '账号邮箱',
  `level` tinyint(4) NOT NULL DEFAULT '0' COMMENT '账号等级，详见AccountLevelEnum',
  `platform` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '平台，详见PlatformEnum',
  `task_count` int(10) NOT NULL DEFAULT '0' COMMENT '累计有效任务次数',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `status` tinyint(2) DEFAULT '1' COMMENT '账号状态(0 无效;1 有效)',
  `creator` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建者账号名',
  `updater` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '更新者账号名',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账号信息表';

/*Data for the table `account` */

/*Table structure for table `platform_config` */

DROP TABLE IF EXISTS `platform_config`;

CREATE TABLE `platform_config` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `module` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板，详见PlantFormModuleEnum',
  `task_type` tinyint(8) NOT NULL COMMENT '任务类型，详见TaskTypeEnum',
  `status` tinyint(6) NOT NULL COMMENT '状态，详见PlatformStatus',
  `remark` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `creator` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建者账号名',
  `updater` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '更新者账号名',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `platform_config` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
