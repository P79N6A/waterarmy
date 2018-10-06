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
  `full_name` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '账号中文名',
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账号信息表';

/*Data for the table `account` */

insert  into `account`(`id`,`UUID`,`user_name`,`full_name`,`password`,`mobile`,`email`,`level`,`platform`,`task_count`,`create_time`,`update_time`,`status`,`creator`,`updater`) values (1,'1','xiaoa','小a','123456','18927512986','1206401391@qq.com',2,'PCAUTO',100,'2018-10-01 15:54:32','2018-10-01 15:54:34',1,'xiaob','xiaob');

/*Table structure for table `content_info` */

DROP TABLE IF EXISTS `content_info`;

CREATE TABLE `content_info` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` longtext COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容',
  `count` int(10) NOT NULL DEFAULT '0' COMMENT '数量',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `creator` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建者',
  `updater` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '修改者',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `content_info` */

/*Table structure for table `link_info` */

DROP TABLE IF EXISTS `link_info`;

CREATE TABLE `link_info` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '链接ID',
  `name` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '名称',
  `link` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '链接',
  `count` int(11) NOT NULL COMMENT '数量',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `creator` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建者',
  `updater` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '修改者',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `link_info` */

/*Table structure for table `platform_config` */

DROP TABLE IF EXISTS `platform_config`;

CREATE TABLE `platform_config` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `module` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模块，详见PlatFormModuleEnum',
  `task_type` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务类型，详见TaskTypeEnum',
  `status` tinyint(6) NOT NULL COMMENT '状态，详见PlatformStatus',
  `remark` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `creator` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建者账号名',
  `updater` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '更新者账号名',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='平台信息表';

/*Data for the table `platform_config` */

/*Table structure for table `rule_info` */

DROP TABLE IF EXISTS `rule_info`;

CREATE TABLE `rule_info` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '规则ID',
  `is_random_select_link` tinyint(2) DEFAULT NULL COMMENT '是否随机选择链接，0 否 1 是',
  `is_random_select_content` tinyint(2) DEFAULT NULL COMMENT '是否随机选择内容，0 否 1 是',
  `start_time_interval` int(10) DEFAULT NULL COMMENT '开始时间间隔（随机），单位秒',
  `end_time_interval` int(10) DEFAULT NULL COMMENT '结束时间间隔（随机），单位秒',
  `pv_stay_time` int(10) DEFAULT NULL COMMENT 'PV停留时间，单位秒',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `creator` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建者',
  `updater` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '修改者',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `rule_info` */

/*Table structure for table `task_execute_log` */

DROP TABLE IF EXISTS `task_execute_log`;

CREATE TABLE `task_execute_log` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '任务log ID',
  `task_info_id` bigint(20) NOT NULL COMMENT '任务ID，对应task_info表主键',
  `executor` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '执行账号名',
  `excute_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '任务执行状态，0 失败 1成功,详见ExecuteStatusEnum',
  `create_time` datetime NOT NULL COMMENT '完成时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务执行记录表';

/*Data for the table `task_execute_log` */

/*Table structure for table `task_info` */

DROP TABLE IF EXISTS `task_info`;

CREATE TABLE `task_info` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `task_publish_id` bigint(20) NOT NULL COMMENT '发布任务ID，对应task_publish表主键',
  `link_info_id` bigint(20) NOT NULL COMMENT '链接ID',
  `content_info_id` bigint(20) DEFAULT NULL COMMENT '内容ID',
  `rule_info_id` bigint(20) NOT NULL COMMENT '规则ID',
  `name` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务名称',
  `platform` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '平台，详见PlatformEnum',
  `module` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '板块，详见PlatFormModuleEnum',
  `task_type` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务类型，详见TaskTypeEnum',
  `execute_count` int(10) NOT NULL DEFAULT '0' COMMENT '执行数量',
  `finish_count` int(10) NOT NULL DEFAULT '0' COMMENT '完成数量',
  `finish_time` datetime DEFAULT NULL COMMENT '任务完成时间',
  `status` tinyint(6) NOT NULL COMMENT '任务状态，详见TaskStatusEnum',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `creator` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建者账号名',
  `updater` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '更新者账号名',
  PRIMARY KEY (`id`,`task_publish_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务记录表';

/*Data for the table `task_info` */

insert  into `task_info`(`id`,`task_publish_id`,`link_info_id`,`content_info_id`,`rule_info_id`,`name`,`platform`,`module`,`task_type`,`execute_count`,`finish_count`,`finish_time`,`status`,`create_time`,`update_time`,`creator`,`updater`) values (1,0,0,0,0,'易车网刷帖任务','YICHE','NEWS','READ',134,0,'0000-00-00 00:00:00',1,'2018-10-02 09:41:28','2018-10-02 09:41:32','xiaoa','xiaoa');

/*Table structure for table `task_publish` */

DROP TABLE IF EXISTS `task_publish`;

CREATE TABLE `task_publish` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `platform` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '平台，详见PlatformEnum',
  `module` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '板块，详见PlatFormModuleEnum',
  `task_type` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务类型，详见TaskTypeEnum',
  `status` tinyint(6) NOT NULL COMMENT '状态，详见PlatformStatus',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `creator` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建者账号名',
  `updater` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '更新者账号名',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='发布任务表';

/*Data for the table `task_publish` */

insert  into `task_publish`(`id`,`platform`,`module`,`task_type`,`status`,`create_time`,`update_time`,`creator`,`updater`) values (1,'QCTT','NEWS','COMMENT',1,'2018-10-02 21:22:45','2018-10-02 21:22:47','xiaoa','xiaoa');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
# ************************************************************
# Sequel Pro SQL dump
# Version 4541
#
# http://www.sequelpro.com/
# https://github.com/sequelpro/sequelpro
#
# Host: 127.0.0.1 (MySQL 5.6.15)
# Database: shuijun
# Generation Time: 2018-10-04 13:04:00 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table comment_info
# ------------------------------------------------------------

DROP TABLE IF EXISTS `comment_info`;

CREATE TABLE `comment_info` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
  `platform` char(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `userId` int(11) DEFAULT NULL,
  `outUserName` varchar(80) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `createTime` date NOT NULL,
  `modifyTime` date NOT NULL,
  `status` int(11) NOT NULL,
  `targetUrl` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `detailResult` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

LOCK TABLES `comment_info` WRITE;
/*!40000 ALTER TABLE `CommentInfo` DISABLE KEYS */;

INSERT INTO `comment_info` (`id`, `platform`, `userId`, `outUserName`, `createTime`, `modifyTime`, `status`, `targetUrl`, `detailResult`)
VALUES
	(1,'PCAUTO',2,'18482193356','2018-10-04','2018-10-04',1,'https://bbs.pcauto.com.cn/topic-17473908.html','{\"message\":\"天还未冷，尚有余热，鸡皮已上身\",\"need_captcha\":false,\"pid\":156974128,\"qid\":0,\"status\":0,\"tid\":17473908,\"userId\":47446648,\"userName\":\"eiaxal8142\"}');

/*!40000 ALTER TABLE `CommentInfo` ENABLE KEYS */;
UNLOCK TABLES;


# ************************************************************
# Sequel Pro SQL dump
# Version 4541
#
# http://www.sequelpro.com/
# https://github.com/sequelpro/sequelpro
#
# Host: 127.0.0.1 (MySQL 5.6.15)
# Database: shuijun
# Generation Time: 2018-10-04 16:42:59 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table publish_info
# ------------------------------------------------------------

DROP TABLE IF EXISTS `publish_info`;

CREATE TABLE `publish_info` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
  `platform` char(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `userId` int(11) DEFAULT NULL,
  `outUserName` varchar(80) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `createTime` datetime NOT NULL,
  `modifyTime` datetime NOT NULL,
  `status` int(11) NOT NULL,
  `targetUrl` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `detailResult` text COLLATE utf8mb4_unicode_ci,
  `title` varchar(300) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `body` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

LOCK TABLES `publish_info` WRITE;
/*!40000 ALTER TABLE `publish_info` DISABLE KEYS */;

INSERT INTO `publish_info` (`id`, `platform`, `userId`, `outUserName`, `createTime`, `modifyTime`, `status`, `targetUrl`, `detailResult`, `title`, `body`)
VALUES
	(1,'PCAUTO',3,'15283867540','2018-10-05 00:42:06','2018-10-05 00:42:06',1,'https://bbs.pcauto.com.cn/topic-17512107.html','{\"status\":0,\"tid\":17512107}','这车什么时候量产上市','我很想知道这车什么时候上市');

/*!40000 ALTER TABLE `publish_info` ENABLE KEYS */;
UNLOCK TABLES;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
