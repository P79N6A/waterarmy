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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账号信息表';

/*Data for the table `account` */

insert  into `account`(`id`,`UUID`,`user_name`,`full_name`,`password`,`mobile`,`email`,`level`,`platform`,`task_count`,`create_time`,`update_time`,`status`,`creator`,`updater`) values (1,'2459e240-7d6a-43a8-ae03-cf4368b41311','18927512986','18927512986','admin123456','18927512986','1206401391@qq.com',0,'XCAR',0,'2018-10-06 20:54:51','2018-10-07 20:59:32',1,'xiaoa','xiaoa'),(2,'315f3fc5-6e71-41f7-a9ae-2714cc0702d3','18927512986','18927512986','admin123456','18927512986','1206401391@qq.com',0,'YICHE',0,'2018-10-07 07:41:22','2018-10-07 07:41:22',0,'xiaoa','xiaoa'),(3,'2b09d3b3-e429-4603-8220-027ccad7c6b5','18927512986','18927512986','admin123456','18927512986','1206401391@qq.com',0,'YICHE',0,'2018-10-07 07:43:12','2018-10-07 07:43:12',0,'xiaoa','xiaoa'),(4,'37b1f778-89c7-4273-9511-12b198929244','18927512986','18927512986','admin123456','18927512986','1206401391@qq.com',0,'YICHE',0,'2018-10-07 07:46:26','2018-10-07 07:46:36',0,'xiaoa','xiaoa');

/*Table structure for table `comment_info` */

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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `comment_info` */

insert  into `comment_info`(`id`,`platform`,`userId`,`outUserName`,`createTime`,`modifyTime`,`status`,`targetUrl`,`detailResult`) values (1,'YICHE',2,'18927512986','2018-10-07','2018-10-07',1,'http://baa.bitauto.com/langdong/thread-15767549-goto179753059.html','{\"isReply\":false,\"msgHTML\":\"吉利汽车很赞\",\"postTime\":\"2018-10-07 10:17\",\"pid\":179753190,\"floor\":2,\"recommendTopicUrl\":\"http://baa.bitauto.com/changancs75/thread-15766371.html\",\"recommendTopicTitle\":\"【深圳长安车友会】340蜗牛DVD头枕-后排乘客的福音\",\"recommendCutTopicTitle\":\"【深圳长安车友会】340蜗牛DVD头枕-后排乘客的福音\",\"replies\":1,\"editUrlPice\":\"reply\",\"tid\":15767549,\"forumApp\":\"langdong\",\"posterid\":34189771,\"postguid\":\"11c49378-fef7-4d1a-8aad-d79694cb28a9\",\"isReload\":false,\"returnUrl\":\"http://baa.bitauto.com/langdong/thread-15767549-goto179753190.html\"}'),(2,'YICHE',2,'18927512986','2018-10-07','2018-10-07',1,'http://baa.bitauto.com/langdong/thread-15767549-goto179753059.html','{\"isReply\":false,\"msgHTML\":\"吉利汽车安全性能好\",\"postTime\":\"2018-10-07 10:27\",\"pid\":179753235,\"floor\":3,\"recommendTopicUrl\":\"http://baa.bitauto.com/eado/thread-15765756.html\",\"recommendTopicTitle\":\"【重庆长安家族】秋意浓 自驾行 逸动用车之古镇穿越行\",\"recommendCutTopicTitle\":\"【重庆长安家族】秋意浓 自驾行 逸动用车之古镇穿越行\",\"replies\":2,\"editUrlPice\":\"reply\",\"tid\":15767549,\"forumApp\":\"langdong\",\"posterid\":34189771,\"postguid\":\"012906fa-27fa-4e1d-b51e-3aa7c1901da0\",\"isReload\":false,\"returnUrl\":\"http://baa.bitauto.com/langdong/thread-15767549-goto179753235.html\"}'),(3,'PCAUTO',1,'18927512986','2018-10-07','2018-10-07',1,'https://bbs.pcauto.com.cn/topic-17520788.html','{\"message\":\"吉利汽车很赞\",\"need_captcha\":false,\"pid\":157015789,\"qid\":0,\"status\":0,\"tid\":17520788,\"userId\":47453699,\"userName\":\"mb47453699\"}'),(4,'PCAUTO',1,'18927512986','2018-10-07','2018-10-07',1,'https://bbs.pcauto.com.cn/topic-13409615.html','{\"message\":\"吉利汽车性价比高\",\"need_captcha\":false,\"pid\":157015817,\"qid\":0,\"status\":0,\"tid\":13409615,\"userId\":47453699,\"userName\":\"mb47453699\"}');

/*Table structure for table `content_info` */

DROP TABLE IF EXISTS `content_info`;

CREATE TABLE `content_info` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '内容ID',
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容标题',
  `content` longtext COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容',
  `content_info_repositories_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '内容库id',
  `content_repositories_type` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容类型，详见ContentRepositoriesEnum',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `creator` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建者',
  `updater` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '修改者',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容信息表';

/*Data for the table `content_info` */

insert  into `content_info`(`id`,`title`,`content`,`content_info_repositories_id`,`content_repositories_type`,`create_time`,`update_time`,`creator`,`updater`) values (1,'吉利汽车试驾体验','吉利汽车试驾体验',1,'POSIED','2018-10-06 21:02:01','2018-10-06 21:02:01','xiaoa','xiaoa'),(2,'吉利汽车预算','吉利汽车预算',1,'POSIED','2018-10-06 21:02:01','2018-10-06 21:02:01','xiaoa','xiaoa'),(3,'吉利汽车10w公里路测','吉利汽车10w公里路测',2,'COMMENT','2018-10-06 21:02:01','2018-10-06 21:02:01','xiaoa','xiaoa'),(13,'吉利汽车很赞','吉利汽车很赞',2,'COMMENT','2018-10-07 09:07:19','2018-10-07 09:07:18','xiaoa','xiaoa'),(14,'吉利汽车性价比高','吉利汽车性价比高',2,'COMMENT','2018-10-07 09:07:19','2018-10-07 09:07:18','xiaoa','xiaoa'),(15,'吉利汽车安全性能好','吉利汽车安全性能好',2,'COMMENT','2018-10-07 09:07:19','2018-10-07 09:07:18','xiaoa','xiaoa'),(16,'aikafatie','aikafatie',0,'POSIED','2018-10-07 20:54:30','2018-10-07 20:54:29','xiaoa','xiaoa');

/*Table structure for table `content_info_repositories` */

DROP TABLE IF EXISTS `content_info_repositories`;

CREATE TABLE `content_info_repositories` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '内容库ID',
  `name` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容库名称',
  `type` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容库类型，详见ContentRepositoriesEnum',
  `count` int(10) NOT NULL DEFAULT '0' COMMENT '数量',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `creator` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建者',
  `updater` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '修改者',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容库表';

/*Data for the table `content_info_repositories` */

insert  into `content_info_repositories`(`id`,`name`,`type`,`count`,`create_time`,`update_time`,`creator`,`updater`) values (1,'吉利汽车发帖内容库','POSIED',0,'2018-10-06 21:01:48','2018-10-06 21:01:48','xiaoa','xiaoa'),(2,'吉利汽车评论内容库','COMMENT',0,'2018-10-07 08:41:03','2018-10-07 08:41:03','xiaoa','xiaoa'),(3,'爱卡汽车发帖内容库','POSIED',0,'2018-10-07 20:52:52','2018-10-07 20:53:02','xiaoa','xiaoa'),(4,'爱卡汽车评论内容库','COMMENT',0,'2018-10-07 20:53:15','2018-10-07 20:53:15','xiaoa','xiaoa');

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
  `name` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '平台名称',
  `desc` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '平台描述',
  `module` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模块，详见PlatFormModuleEnum',
  `task_type` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务类型，详见TaskTypeEnum',
  `status` tinyint(6) NOT NULL COMMENT '状态，详见PlatformStatus',
  `remark` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `example_link` text COLLATE utf8mb4_unicode_ci COMMENT '示例链接',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `creator` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建者账号名',
  `updater` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '更新者账号名',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='平台信息表';

/*Data for the table `platform_config` */

/*Table structure for table `publish_info` */

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
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `publish_info` */

insert  into `publish_info`(`id`,`platform`,`userId`,`outUserName`,`createTime`,`modifyTime`,`status`,`targetUrl`,`detailResult`,`title`,`body`) values (1,'YICHE',1,'18927512986','2018-10-06 22:16:26','2018-10-06 22:16:26',1,NULL,'{\"error\":\"需要验证码\"}','吉利汽车试驾体验','吉利汽车试驾体验'),(2,'YICHE',1,'18927512986','2018-10-07 07:31:03','2018-10-07 07:31:03',1,NULL,'{\"error\":\"需要验证码\"}','吉利汽车试驾体验','吉利汽车试驾体验'),(3,'YICHE',1,'18927512986','2018-10-07 07:35:45','2018-10-07 07:35:45',1,NULL,'{\"error\":\"需要验证码\"}','吉利汽车试驾体验','吉利汽车试驾体验'),(4,'YICHE',1,'18927512986','2018-10-07 07:40:39','2018-10-07 07:40:39',1,NULL,'{\"error\":\"需要验证码\"}','吉利汽车10w公里路测','吉利汽车10w公里路测'),(5,'YICHE',2,'18927512986','2018-10-07 07:43:03','2018-10-07 07:43:03',1,NULL,'{\"error\":\"需要验证码\"}','吉利汽车试驾体验','吉利汽车试驾体验'),(6,'YICHE',3,'18927512986','2018-10-07 07:46:14','2018-10-07 07:46:14',1,NULL,'{\"error\":\"需要验证码\"}','吉利汽车10w公里路测','吉利汽车10w公里路测'),(7,'YICHE',3,'18927512986','2018-10-07 08:10:52','2018-10-07 08:10:52',1,NULL,'{\"error\":\"需要验证码\"}','吉利汽车预算','吉利汽车预算'),(8,'YICHE',1,'18927512986','2018-10-07 08:10:55','2018-10-07 08:10:55',1,NULL,'{\"error\":\"需要验证码\"}','吉利汽车10w公里路测','吉利汽车10w公里路测'),(9,'YICHE',2,'18927512986','2018-10-07 08:36:47','2018-10-07 08:36:47',1,NULL,'{\"error\":\"需要验证码\"}','吉利汽车预算','吉利汽车预算'),(10,'YICHE',4,'18927512986','2018-10-07 08:40:44','2018-10-07 08:40:44',1,NULL,'{\"error\":\"需要验证码\"}','吉利汽车预算','吉利汽车预算'),(11,'YICHE',3,'18927512986','2018-10-07 09:54:51','2018-10-07 09:54:51',1,NULL,'{\"error\":\"需要验证码\"}','吉利汽车10w公里路测','吉利汽车10w公里路测'),(12,'YICHE',2,'18927512986','2018-10-07 10:02:01','2018-10-07 10:02:01',1,'http://baa.bitauto.com/langdong/thread-15767549-goto179753059.html','{\"tid\":15767549,\"pid\":179753059,\"returnUrl\":\"http://baa.bitauto.com/langdong/thread-15767549-goto179753059.html\"}','吉利汽车试驾体验','吉利汽车试驾体验'),(13,'PCAUTO',1,'18927512986','2018-10-07 20:44:13','2018-10-07 20:44:13',1,'https://bbs.pcauto.com.cn/topic-17520788.html','{\"status\":0,\"tid\":17520788}','吉利汽车试驾体验','吉利汽车试驾体验');

/*Table structure for table `rule_info` */

DROP TABLE IF EXISTS `rule_info`;

CREATE TABLE `rule_info` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '规则ID',
  `name` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '规则名称',
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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `rule_info` */

insert  into `rule_info`(`id`,`name`,`is_random_select_link`,`is_random_select_content`,`start_time_interval`,`end_time_interval`,`pv_stay_time`,`create_time`,`update_time`,`creator`,`updater`) values (1,'发帖规则',1,1,10,40,4,'2018-10-06 21:11:24','2018-10-06 21:11:24','xiaoa','xiaoa'),(2,'评论规则',1,1,10,50,4,'2018-10-07 10:48:35','2018-10-07 10:48:35','xiaoa','xiaoa'),(3,'阅读规则',1,0,10,20,3,'2018-10-07 11:29:42','2018-10-07 11:29:42','xiaoa','xiaoa');

/*Table structure for table `task_execute_log` */

DROP TABLE IF EXISTS `task_execute_log`;

CREATE TABLE `task_execute_log` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '任务log ID',
  `task_info_id` bigint(20) NOT NULL COMMENT '任务ID，对应task_info表主键',
  `content_info_id` bigint(20) DEFAULT NULL COMMENT '内容ID，对应content_info表主键',
  `executor` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '执行账号名',
  `execute_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '任务执行状态，0 失败 1成功,详见ExcuteStatusEnum',
  `handler_result` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '执行结果返还值',
  `create_time` datetime NOT NULL COMMENT '完成时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务执行记录表';

/*Data for the table `task_execute_log` */

insert  into `task_execute_log`(`id`,`task_info_id`,`content_info_id`,`executor`,`execute_status`,`handler_result`,`create_time`) values (9,1,2,'18927512986',0,'{\"errorCode\":4,\"errorMessage\":\"登录失败\",\"success\":false}','2018-10-06 21:49:41'),(10,1,2,'18927512986',1,'{\"data\":{\"detailResult\":\"{\\\"error\\\":\\\"需要验证码\\\"}\",\"handleType\":\"POSIED\",\"platform\":\"YICHE\",\"userId\":1,\"userLoginId\":\"18927512986\"},\"errorCode\":0,\"success\":true}','2018-10-06 21:50:28'),(11,1,3,'18927512986',1,'{\"data\":{\"detailResult\":\"{\\\"error\\\":\\\"需要验证码\\\"}\",\"handleType\":\"POSIED\",\"platform\":\"YICHE\",\"userId\":1,\"userLoginId\":\"18927512986\"},\"errorCode\":0,\"success\":true}','2018-10-06 21:57:22'),(12,1,1,'18927512986',1,'{\"data\":{\"detailResult\":\"{\\\"error\\\":\\\"需要验证码\\\"}\",\"handleType\":\"POSIED\",\"platform\":\"YICHE\",\"userId\":1,\"userLoginId\":\"18927512986\"},\"errorCode\":0,\"success\":true}','2018-10-06 22:16:26'),(13,1,1,'18927512986',1,'{\"data\":{\"detailResult\":\"{\\\"error\\\":\\\"需要验证码\\\"}\",\"handleType\":\"POSIED\",\"platform\":\"YICHE\",\"userId\":1,\"userLoginId\":\"18927512986\"},\"errorCode\":0,\"success\":true}','2018-10-07 07:31:03'),(14,1,3,'18927512986',0,'{\"errorCode\":4,\"errorMessage\":\"登录失败\",\"success\":false}','2018-10-07 07:33:42'),(15,1,1,'18927512986',1,'{\"data\":{\"detailResult\":\"{\\\"error\\\":\\\"需要验证码\\\"}\",\"handleType\":\"POSIED\",\"platform\":\"YICHE\",\"userId\":1,\"userLoginId\":\"18927512986\"},\"errorCode\":0,\"success\":true}','2018-10-07 07:35:45'),(16,1,3,'18927512986',1,'{\"data\":{\"detailResult\":\"{\\\"error\\\":\\\"需要验证码\\\"}\",\"handleType\":\"POSIED\",\"platform\":\"YICHE\",\"userId\":1,\"userLoginId\":\"18927512986\"},\"errorCode\":0,\"success\":true}','2018-10-07 07:40:39'),(17,1,1,'18927512986',1,'{\"data\":{\"detailResult\":\"{\\\"error\\\":\\\"需要验证码\\\"}\",\"handleType\":\"POSIED\",\"platform\":\"YICHE\",\"userId\":2,\"userLoginId\":\"18927512986\"},\"errorCode\":0,\"success\":true}','2018-10-07 07:43:06'),(18,1,3,'18927512986',1,'{\"data\":{\"detailResult\":\"{\\\"error\\\":\\\"需要验证码\\\"}\",\"handleType\":\"POSIED\",\"platform\":\"YICHE\",\"userId\":3,\"userLoginId\":\"18927512986\"},\"errorCode\":0,\"success\":true}','2018-10-07 07:46:14'),(19,1,2,'18927512986',1,'{\"data\":{\"detailResult\":\"{\\\"error\\\":\\\"需要验证码\\\"}\",\"handleType\":\"POSIED\",\"platform\":\"YICHE\",\"userId\":3,\"userLoginId\":\"18927512986\"},\"errorCode\":0,\"success\":true}','2018-10-07 08:10:53'),(20,1,3,'18927512986',1,'{\"data\":{\"detailResult\":\"{\\\"error\\\":\\\"需要验证码\\\"}\",\"handleType\":\"POSIED\",\"platform\":\"YICHE\",\"userId\":1,\"userLoginId\":\"18927512986\"},\"errorCode\":0,\"success\":true}','2018-10-07 08:10:55'),(21,1,2,'18927512986',1,'{\"data\":{\"detailResult\":\"{\\\"error\\\":\\\"需要验证码\\\"}\",\"handleType\":\"POSIED\",\"platform\":\"YICHE\",\"userId\":2,\"userLoginId\":\"18927512986\"},\"errorCode\":0,\"success\":true}','2018-10-07 08:36:47'),(22,1,2,'18927512986',1,'{\"data\":{\"detailResult\":\"{\\\"error\\\":\\\"需要验证码\\\"}\",\"handleType\":\"POSIED\",\"platform\":\"YICHE\",\"userId\":4,\"userLoginId\":\"18927512986\"},\"errorCode\":0,\"success\":true}','2018-10-07 08:40:44'),(23,1,3,'18927512986',1,'{\"data\":{\"detailResult\":\"{\\\"error\\\":\\\"需要验证码\\\"}\",\"handleType\":\"POSIED\",\"platform\":\"YICHE\",\"userId\":3,\"userLoginId\":\"18927512986\"},\"errorCode\":0,\"success\":true}','2018-10-07 09:54:51'),(24,1,1,'18927512986',1,'{\"data\":{\"detailResult\":\"{\\\"tid\\\":15767549,\\\"pid\\\":179753059,\\\"returnUrl\\\":\\\"http://baa.bitauto.com/langdong/thread-15767549-goto179753059.html\\\"}\",\"handleType\":\"POSIED\",\"platform\":\"YICHE\",\"targetUrl\":\"http://baa.bitauto.com/langdong/thread-15767549-goto179753059.html\",\"userId\":2,\"userLoginId\":\"18927512986\"},\"errorCode\":0,\"success\":true}','2018-10-07 10:02:02'),(25,2,15,'18927512986',0,'{\"errorCode\":4,\"errorMessage\":\"登录失败\",\"success\":false}','2018-10-07 10:13:06'),(26,2,13,'18927512986',1,'{\"data\":{\"detailResult\":\"{\\\"isReply\\\":false,\\\"msgHTML\\\":\\\"吉利汽车很赞\\\",\\\"postTime\\\":\\\"2018-10-07 10:17\\\",\\\"pid\\\":179753190,\\\"floor\\\":2,\\\"recommendTopicUrl\\\":\\\"http://baa.bitauto.com/changancs75/thread-15766371.html\\\",\\\"recommendTopicTitle\\\":\\\"【深圳长安车友会】340蜗牛DVD头枕-后排乘客的福音\\\",\\\"recommendCutTopicTitle\\\":\\\"【深圳长安车友会】340蜗牛DVD头枕-后排乘客的福音\\\",\\\"replies\\\":1,\\\"editUrlPice\\\":\\\"reply\\\",\\\"tid\\\":15767549,\\\"forumApp\\\":\\\"langdong\\\",\\\"posterid\\\":34189771,\\\"postguid\\\":\\\"11c49378-fef7-4d1a-8aad-d79694cb28a9\\\",\\\"isReload\\\":false,\\\"returnUrl\\\":\\\"http://baa.bitauto.com/langdong/thread-15767549-goto179753190.html\\\"}\",\"handleType\":\"COMMENT\",\"platform\":\"YICHE\",\"userId\":2,\"userLoginId\":\"18927512986\"},\"errorCode\":0,\"success\":true}','2018-10-07 10:17:26'),(27,2,15,'18927512986',1,'{\"data\":{\"detailResult\":\"{\\\"isReply\\\":false,\\\"msgHTML\\\":\\\"吉利汽车安全性能好\\\",\\\"postTime\\\":\\\"2018-10-07 10:27\\\",\\\"pid\\\":179753235,\\\"floor\\\":3,\\\"recommendTopicUrl\\\":\\\"http://baa.bitauto.com/eado/thread-15765756.html\\\",\\\"recommendTopicTitle\\\":\\\"【重庆长安家族】秋意浓 自驾行 逸动用车之古镇穿越行\\\",\\\"recommendCutTopicTitle\\\":\\\"【重庆长安家族】秋意浓 自驾行 逸动用车之古镇穿越行\\\",\\\"replies\\\":2,\\\"editUrlPice\\\":\\\"reply\\\",\\\"tid\\\":15767549,\\\"forumApp\\\":\\\"langdong\\\",\\\"posterid\\\":34189771,\\\"postguid\\\":\\\"012906fa-27fa-4e1d-b51e-3aa7c1901da0\\\",\\\"isReload\\\":false,\\\"returnUrl\\\":\\\"http://baa.bitauto.com/langdong/thread-15767549-goto179753235.html\\\"}\",\"handleType\":\"COMMENT\",\"platform\":\"YICHE\",\"userId\":2,\"userLoginId\":\"18927512986\"},\"errorCode\":0,\"success\":true}','2018-10-07 10:29:49'),(28,7,3,'18927512986',0,'{\"errorCode\":4,\"errorMessage\":\"登录失败\",\"success\":false}','2018-10-07 20:26:38'),(29,7,3,'18927512986',0,'{\"errorCode\":4,\"errorMessage\":\"登录失败\",\"success\":false}','2018-10-07 20:32:26'),(30,7,1,'18927512986',0,'{\"errorCode\":4,\"errorMessage\":\"登录失败\",\"success\":false}','2018-10-07 20:33:11'),(31,7,1,'18927512986',0,'{\"errorCode\":4,\"errorMessage\":\"登录失败\",\"success\":false}','2018-10-07 20:37:14'),(32,7,2,'18927512986',0,'{\"errorCode\":4,\"errorMessage\":\"登录失败\",\"success\":false}','2018-10-07 20:38:42'),(33,7,2,'18927512986',0,'{\"errorCode\":4,\"errorMessage\":\"登录失败\",\"success\":false}','2018-10-07 20:42:17'),(34,7,1,'18927512986',1,'{\"data\":{\"detailResult\":\"{\\\"status\\\":0,\\\"tid\\\":17520788}\",\"handleType\":\"POSIED\",\"platform\":\"PCAUTO\",\"targetUrl\":\"https://bbs.pcauto.com.cn/topic-17520788.html\",\"userId\":1,\"userLoginId\":\"18927512986\"},\"errorCode\":0,\"success\":true}','2018-10-07 20:44:25'),(35,8,13,'18927512986',1,'{\"data\":{\"detailResult\":\"{\\\"message\\\":\\\"吉利汽车很赞\\\",\\\"need_captcha\\\":false,\\\"pid\\\":157015789,\\\"qid\\\":0,\\\"status\\\":0,\\\"tid\\\":17520788,\\\"userId\\\":47453699,\\\"userName\\\":\\\"mb47453699\\\"}\",\"handleType\":\"COMMENT\",\"platform\":\"PCAUTO\",\"userId\":1,\"userLoginId\":\"18927512986\"},\"errorCode\":0,\"success\":true}','2018-10-07 20:50:33'),(36,3,14,'18927512986',1,'{\"data\":{\"detailResult\":\"{\\\"message\\\":\\\"吉利汽车性价比高\\\",\\\"need_captcha\\\":false,\\\"pid\\\":157015817,\\\"qid\\\":0,\\\"status\\\":0,\\\"tid\\\":13409615,\\\"userId\\\":47453699,\\\"userName\\\":\\\"mb47453699\\\"}\",\"handleType\":\"COMMENT\",\"platform\":\"PCAUTO\",\"userId\":1,\"userLoginId\":\"18927512986\"},\"errorCode\":0,\"success\":true}','2018-10-07 20:50:50'),(37,9,2,'18927512986',0,'{\"errorCode\":6,\"errorMessage\":\"处理失败\",\"success\":false}','2018-10-07 21:01:59'),(38,9,16,'18927512986',0,'{\"errorCode\":4,\"errorMessage\":\"登录失败\",\"success\":false}','2018-10-07 21:04:50'),(39,9,16,'18927512986',0,'{\"errorCode\":4,\"errorMessage\":\"登录失败\",\"success\":false}','2018-10-07 21:37:07'),(40,9,16,'18927512986',0,'{\"errorCode\":6,\"errorMessage\":\"处理失败\",\"success\":false}','2018-10-08 23:08:15'),(41,4,NULL,'xiaoa',1,'{\"data\":{\"detailResult\":\"{success:true,url:https://bbs.pcauto.com.cn/topic-13409615.html\",\"handleType\":\"READ\",\"platform\":\"PCAUTO\",\"targetUrl\":\"https://bbs.pcauto.com.cn/topic-13409615.html\"},\"errorCode\":0,\"success\":true}','2018-10-08 23:08:22'),(42,9,16,'18927512986',0,'{\"errorCode\":6,\"errorMessage\":\"处理失败\",\"success\":false}','2018-10-08 23:08:31'),(43,4,NULL,'xiaoa',1,'{\"data\":{\"detailResult\":\"{success:true,url:https://bbs.pcauto.com.cn/topic-13409615.html\",\"handleType\":\"READ\",\"platform\":\"PCAUTO\",\"targetUrl\":\"https://bbs.pcauto.com.cn/topic-13409615.html\"},\"errorCode\":0,\"success\":true}','2018-10-08 23:08:34'),(44,4,NULL,'xiaoa',1,'{\"data\":{\"detailResult\":\"{success:true,url:https://bbs.pcauto.com.cn/topic-13409615.html\",\"handleType\":\"READ\",\"platform\":\"PCAUTO\",\"targetUrl\":\"https://bbs.pcauto.com.cn/topic-13409615.html\"},\"errorCode\":0,\"success\":true}','2018-10-08 23:12:57'),(45,4,NULL,'xiaoa',1,'{\"data\":{\"detailResult\":\"{success:true,url:https://bbs.pcauto.com.cn/topic-13409615.html\",\"handleType\":\"READ\",\"platform\":\"PCAUTO\",\"targetUrl\":\"https://bbs.pcauto.com.cn/topic-13409615.html\"},\"errorCode\":0,\"success\":true}','2018-10-08 23:13:04'),(46,4,NULL,'xiaoa',1,'{\"data\":{\"detailResult\":\"{success:true,url:https://bbs.pcauto.com.cn/topic-13409615.html\",\"handleType\":\"READ\",\"platform\":\"PCAUTO\",\"targetUrl\":\"https://bbs.pcauto.com.cn/topic-13409615.html\"},\"errorCode\":0,\"success\":true}','2018-10-08 23:13:39'),(47,4,NULL,'xiaoa',1,'{\"data\":{\"detailResult\":\"{success:true,url:https://bbs.pcauto.com.cn/topic-13409615.html\",\"handleType\":\"READ\",\"platform\":\"PCAUTO\",\"targetUrl\":\"https://bbs.pcauto.com.cn/topic-13409615.html\"},\"errorCode\":0,\"success\":true}','2018-10-08 23:17:57'),(48,4,NULL,'xiaoa',1,'{\"data\":{\"detailResult\":\"{success:true,url:https://bbs.pcauto.com.cn/topic-13409615.html\",\"handleType\":\"READ\",\"platform\":\"PCAUTO\",\"targetUrl\":\"https://bbs.pcauto.com.cn/topic-13409615.html\"},\"errorCode\":0,\"success\":true}','2018-10-08 23:18:52'),(49,4,NULL,'xiaoa',1,'{\"data\":{\"detailResult\":\"{success:true,url:https://bbs.pcauto.com.cn/topic-13409615.html\",\"handleType\":\"READ\",\"platform\":\"PCAUTO\",\"targetUrl\":\"https://bbs.pcauto.com.cn/topic-13409615.html\"},\"errorCode\":0,\"success\":true}','2018-10-08 23:19:48'),(50,4,NULL,'xiaoa',1,'{\"data\":{\"detailResult\":\"{success:true,url:https://bbs.pcauto.com.cn/topic-13409615.html\",\"handleType\":\"READ\",\"platform\":\"PCAUTO\",\"targetUrl\":\"https://bbs.pcauto.com.cn/topic-13409615.html\"},\"errorCode\":0,\"success\":true}','2018-10-08 23:20:48'),(51,4,NULL,'xiaoa',1,'{\"data\":{\"detailResult\":\"{success:true,url:https://bbs.pcauto.com.cn/topic-13409615.html\",\"handleType\":\"READ\",\"platform\":\"PCAUTO\",\"targetUrl\":\"https://bbs.pcauto.com.cn/topic-13409615.html\"},\"errorCode\":0,\"success\":true}','2018-10-08 23:21:48');

/*Table structure for table `task_info` */

DROP TABLE IF EXISTS `task_info`;

CREATE TABLE `task_info` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `task_publish_id` bigint(20) NOT NULL COMMENT '发布任务ID，对应task_publish表主键',
  `rule_info_id` bigint(20) NOT NULL COMMENT '规则id，对应rule_info表主键',
  `name` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务名称',
  `platform` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '平台，详见PlatformEnum',
  `module` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '板块，详见PlatFormModuleEnum',
  `task_type` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务类型，详见TaskTypeEnum',
  `link` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '执行任务链接',
  `link_title` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '链接标题',
  `content_repositories_type` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容库类型，详见ContentRepositoriesEnum',
  `content_repositories_name` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容库名称',
  `like_content` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '需要点赞内容',
  `execute_count` int(10) NOT NULL DEFAULT '0' COMMENT '执行数量',
  `finish_count` int(10) NOT NULL DEFAULT '0' COMMENT '完成数量',
  `finish_time` datetime DEFAULT NULL COMMENT '任务完成时间',
  `status` tinyint(6) NOT NULL COMMENT '任务状态，详见TaskStatusEnum',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `creator` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建者账号名',
  `updater` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '更新者账号名',
  PRIMARY KEY (`id`,`task_publish_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务记录表';

/*Data for the table `task_info` */

insert  into `task_info`(`id`,`task_publish_id`,`rule_info_id`,`name`,`platform`,`module`,`task_type`,`link`,`link_title`,`content_repositories_type`,`content_repositories_name`,`like_content`,`execute_count`,`finish_count`,`finish_time`,`status`,`create_time`,`update_time`,`creator`,`updater`) values (1,1,1,'吉利汽车发帖任务','YICHE','FORUM','POSIED','http://baa.bitauto.com/langdong/','','POSIED','吉利汽车试驾体验',NULL,10,3,NULL,2,'2018-10-06 21:12:37','2018-10-06 21:12:37','xiaoa','xiaoa'),(2,1,1,'吉利汽车评论','YICHE','FORUM','COMMENT','http://baa.bitauto.com/langdong/thread-15767549-goto179753059.html','','COMMENT','吉利汽车很赞',NULL,20,2,NULL,2,'2018-10-07 09:16:39','2018-10-07 09:16:39','xiaoa','xiaoa'),(3,2,2,'太平洋评论任务','PCAUTO','FORUM','COMMENT','https://bbs.pcauto.com.cn/topic-13409615.html','','COMMENT','吉利汽车性价比高',NULL,10,1,'2018-10-07 20:50:50',2,'2018-10-07 11:18:49','2018-10-07 11:18:49','xiaoa','xiaoa'),(4,3,3,'太平洋汽车阅读规则','PCAUTO','FORUM','READ','https://bbs.pcauto.com.cn/topic-13409615.html','','','',NULL,10,10,'2018-10-08 23:21:48',2,'2018-10-07 11:31:41','2018-10-07 11:31:41','xiaoa','xiaoa'),(5,4,1,'太平洋汽车论坛视频播放任务','PCAUTO','FORUM','PLAY','https://bbs.pcauto.com.cn/topic-13297957.html','','','',NULL,10,0,NULL,0,'2018-10-07 11:35:10','2018-10-07 11:35:10','xiaoa','xiaoa'),(6,5,2,'太平洋论坛评论点赞','PCAUTO','FORUM','LIKE','https://bbs.pcauto.com.cn/topic-13409615.html','','','','帖子被删或被屏蔽了',10,0,NULL,0,'2018-10-07 12:15:15','2018-10-07 12:15:15','xiaoa','xiaoa'),(7,2,1,'太平洋发帖任务','PCAUTO','FORUM','POSIED','https://bbs.pcauto.com.cn/forum-17724.html','','POSIED','吉利汽车试驾体验',NULL,1,1,'2018-10-07 20:44:25',2,'2018-10-07 20:25:22','2018-10-07 20:25:22','xiaoa','xiaoa'),(8,5,2,'太平洋汽车评论任务','PCAUTO','FORUM','COMMENT','https://bbs.pcauto.com.cn/topic-17520788.html','','COMMENT','吉利汽车安全性能好',NULL,1,1,'2018-10-07 20:50:33',2,'2018-10-07 20:48:18','2018-10-07 20:48:18','xiaoa','xiaoa'),(9,6,1,'爱卡发帖任务','XCAR','FORUM','POSIED','http://www.xcar.com.cn/bbs/forumdisplay.php?fid=1394','','POSIED','aikafatie',NULL,1,0,NULL,0,'2018-10-07 20:55:40','2018-10-07 20:55:40','xiaoa','xiaoa'),(10,7,2,'第一电动评论测试任务','D1EV','FORUM','COMMENT','https://www.d1ev.com/news/ziben/77782','','COMMENT','吉利汽车评论内容库',NULL,3,0,NULL,0,'2018-10-08 23:19:29','2018-10-08 23:19:29','xiaoa','xiaoa');

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
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='发布任务表';

/*Data for the table `task_publish` */

insert  into `task_publish`(`id`,`platform`,`module`,`task_type`,`status`,`create_time`,`update_time`,`creator`,`updater`) values (1,'YICHE','FORUM','POSIED',1,'2018-10-06 21:03:58','2018-10-07 20:27:24','xiaoa','xiaoa'),(2,'PCAUTO','FORUM','POSIED',1,'2018-10-07 10:46:24','2018-10-07 20:45:31','xiaoa','xiaoa'),(3,'PCAUTO','FORUM','READ',1,'2018-10-07 11:28:48','2018-10-07 20:51:47','xiaoa','xiaoa'),(4,'PCAUTO','FORUM','PLAY',1,'2018-10-07 11:34:07','2018-10-07 20:51:44','xiaoa','xiaoa'),(5,'PCAUTO','FORUM','COMMENT',1,'2018-10-07 12:12:22','2018-10-07 20:51:36','xiaoa','xiaoa'),(6,'XCAR','FORUM','POSIED',0,'2018-10-07 20:52:08','2018-10-07 20:52:08','xiaoa','xiaoa'),(7,'XCAR','FORUM','COMMENT',1,'2018-10-07 20:52:25','2018-10-07 20:52:25','xiaoa','xiaoa');

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
# Generation Time: 2018-10-13 14:24:12 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table praise_info
# ------------------------------------------------------------

DROP TABLE IF EXISTS `praise_info`;

CREATE TABLE `praise_info` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
  `platform` char(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `userId` int(11) DEFAULT NULL,
  `outUserName` varchar(80) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `createTime` datetime NOT NULL,
  `modifyTime` datetime NOT NULL,
  `status` int(11) NOT NULL,
  `targetUrl` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `detailResult` text COLLATE utf8mb4_unicode_ci,
  `commentId` varchar(80) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `commentContent` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

LOCK TABLES `praise_info` WRITE;
/*!40000 ALTER TABLE `praise_info` DISABLE KEYS */;

INSERT INTO `praise_info` (`id`, `platform`, `userId`, `outUserName`, `createTime`, `modifyTime`, `status`, `targetUrl`, `detailResult`, `commentId`, `commentContent`)
VALUES
	(1,'YICHE',4,'18383849422','2018-10-13 22:22:21','2018-10-13 22:22:21',1,'http://news.bitauto.com/qichewenhua/20181008/1008346394.html','/**/jQuery18006913486550558714_1539440355589({\"code\":0,\"msg\":\"OK\",\"result\":null,\"extend\":null});','257971069489512448','666');

/*!40000 ALTER TABLE `praise_info` ENABLE KEYS */;
UNLOCK TABLES;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

