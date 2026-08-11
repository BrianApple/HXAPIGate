/*
 =====================================================================
  HXAPIGate 管理平台初始化脚本（HXBootShiro）
 ---------------------------------------------------------------------
  包含：建库、全部 9 张表结构、核心测试数据（用户/角色/API资源/应用）
  兼容：MySQL 5.7 / 8.0，字符集 utf8mb4
  用法：mysql -uroot -p < hxapigate.sql
       或 mysql -uroot -p -e "source /path/hxapigate.sql"

  📌 测试账号：
     超级管理员  admin    / admin123  （管理员角色 role_admin）
     测试用户    testuser / 123456    （用户角色 role_user）
     测试用户    user02   / 123456    （用户角色 role_user）

  ⚠️ 说明：
     1. 脚本会 DROP 已存在的同名表后重建，请勿在生产库执行
     2. auth_operation_log（操作日志）为空表，运行时自动写入
     3. 数据库名 hxapigate，连接参数见 application.yml（dev: 127.0.0.1:13306）
 =====================================================================
*/

CREATE DATABASE IF NOT EXISTS `hxapigate` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `hxapigate`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `auth_account_log`;
CREATE TABLE `auth_account_log` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '用户账户操作日志主键',
  `LOG_NAME` varchar(255) DEFAULT NULL COMMENT '日志名称(login,register,logout)',
  `USER_ID` varchar(32) DEFAULT NULL COMMENT '用户id',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `SUCCEED` int DEFAULT NULL COMMENT '是否执行成功(0失败1成功)',
  `MESSAGE` varchar(255) DEFAULT NULL COMMENT '具体消息',
  `IP` varchar(255) DEFAULT NULL COMMENT '登录ip',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=249 DEFAULT CHARSET=utf8mb4  COMMENT='登录注册登出记录';
DROP TABLE IF EXISTS `auth_app`;
CREATE TABLE `auth_app` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '应用ID',
  `APP_ID` varchar(64) NOT NULL COMMENT '应用唯一标识(调用网关API时作为userId请求头)',
  `APP_NAME` varchar(64) DEFAULT NULL COMMENT '应用名称',
  `APP_SECRET` varchar(128) DEFAULT NULL COMMENT '应用密钥(生成license签名用)',
  `DESCRIPTION` varchar(255) DEFAULT NULL COMMENT '应用描述',
  `STATUS` int DEFAULT '1' COMMENT '状态 1:启用 0:停用',
  `LICENSE` varchar(4096) DEFAULT NULL COMMENT '当前有效License(JWT)',
  `LICENSE_EXPIRE_AT` bigint DEFAULT NULL COMMENT 'License过期时间戳(0=永久)',
  `LICENSE_GENERATED_AT` bigint DEFAULT NULL COMMENT 'License最近生成时间戳',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `uk_app_id` (`APP_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4  COMMENT='应用信息表';
DROP TABLE IF EXISTS `auth_app_role`;
CREATE TABLE `auth_app_role` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `APP_ID` varchar(64) NOT NULL COMMENT '应用标识',
  `ROLE_ID` int NOT NULL COMMENT '角色ID',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`ID`),
  KEY `idx_app_id` (`APP_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4  COMMENT='应用角色关联表';
DROP TABLE IF EXISTS `auth_operation_log`;
CREATE TABLE `auth_operation_log` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '用户操作日志主键',
  `LOG_NAME` varchar(255) DEFAULT NULL COMMENT '日志名称',
  `USER_ID` varchar(32) DEFAULT NULL COMMENT '用户id',
  `API` varchar(255) DEFAULT NULL COMMENT 'api名称',
  `METHOD` varchar(255) DEFAULT NULL COMMENT '方法名称',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `SUCCEED` int DEFAULT NULL COMMENT '是否执行成功(0失败1成功)',
  `MESSAGE` varchar(255) DEFAULT NULL COMMENT '具体消息备注',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2206 DEFAULT CHARSET=utf8mb4  COMMENT='操作日志';
DROP TABLE IF EXISTS `auth_resource`;
CREATE TABLE `auth_resource` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '资源ID',
  `CODE` varchar(32) DEFAULT NULL COMMENT '资源名称',
  `NAME` varchar(128) DEFAULT NULL COMMENT '资源描述',
  `PARENT_ID` int DEFAULT NULL COMMENT '父资源编码->菜单',
  `URI` varchar(128) DEFAULT NULL COMMENT '访问地址URL',
  `VERSION` varchar(20) DEFAULT NULL COMMENT '资源版本信息',
  `TYPE` int DEFAULT NULL COMMENT '类型 0:内部资源（不走API网关）, 1:菜单 ,  2:资源element(rest-api) 3:资源分类',
  `METHOD` varchar(8) DEFAULT NULL COMMENT '访问方式 GET POST PUT DELETE PATCH',
  `NEED_AUTH` int DEFAULT NULL COMMENT '网关是否鉴权',
  `ROUTE_INFO` text COMMENT '路由信息（json串）',
  `ICON` varchar(128) DEFAULT NULL COMMENT '图标',
  `STATUS` int DEFAULT NULL COMMENT '状态   1:正常、9：禁用',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=243 DEFAULT CHARSET=utf8mb4  COMMENT='资源信息表(菜单,资源)';
DROP TABLE IF EXISTS `auth_role`;
CREATE TABLE `auth_role` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `CODE` varchar(32) NOT NULL COMMENT '角色编码',
  `NAME` varchar(32) DEFAULT NULL COMMENT '角色名称',
  `STATUS` int DEFAULT NULL COMMENT '状态   1:正常、9：禁用',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=106 DEFAULT CHARSET=utf8mb4  COMMENT='角色信息表';
DROP TABLE IF EXISTS `auth_role_resource`;
CREATE TABLE `auth_role_resource` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `ROLE_ID` int NOT NULL COMMENT '角色ID',
  `RESOURCE_ID` int NOT NULL COMMENT '资源ID',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=302 DEFAULT CHARSET=utf8mb4  COMMENT='资源角色关联表';
DROP TABLE IF EXISTS `auth_user`;
CREATE TABLE `auth_user` (
  `UID` varchar(32) NOT NULL COMMENT 'uid,用户账号,主键',
  `USERNAME` varchar(32) NOT NULL COMMENT '用户名(nick_name)',
  `PASSWORD` varchar(64) NOT NULL COMMENT '密码(MD5(密码+盐))',
  `SALT` varchar(32) DEFAULT NULL COMMENT '盐',
  `REAL_NAME` varchar(32) DEFAULT NULL COMMENT '用户真名',
  `AVATAR` varchar(128) DEFAULT NULL COMMENT '头像',
  `PHONE` varchar(32) DEFAULT NULL COMMENT '电话号码(唯一)',
  `EMAIL` varchar(64) DEFAULT NULL COMMENT '邮件地址(唯一)',
  `SEX` int DEFAULT NULL COMMENT '性别(1.男 2.女)',
  `STATUS` int DEFAULT NULL COMMENT '账户状态(1.正常 2.锁定 3.删除 4.非法)',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `CREATE_WHERE` int DEFAULT NULL COMMENT '创建来源(1.web 2.android 3.ios 4.win 5.macos 6.ubuntu)',
  PRIMARY KEY (`UID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COMMENT='用户信息表';
DROP TABLE IF EXISTS `auth_user_role`;
CREATE TABLE `auth_user_role` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '用户角色关联表主键',
  `USER_ID` varchar(32) NOT NULL COMMENT '用户UID',
  `ROLE_ID` int NOT NULL COMMENT '角色ID',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8mb4  COMMENT='用户角色关联表';



-- 登录日志示例（可清空，不影响功能）
INSERT INTO `auth_account_log` VALUES (1, '用户登录日志', 'admin', NOW(), 1, '登录成功', '127.0.0.1');
INSERT INTO `auth_account_log` VALUES (2, '用户登录日志', 'testuser', NOW(), 1, '登录成功', '127.0.0.1');
INSERT INTO `auth_account_log` VALUES (3, '用户注册日志', 'user02', NOW(), 1, '注册成功', '127.0.0.1');


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

LOCK TABLES `auth_user` WRITE;
/*!40000 ALTER TABLE `auth_user` DISABLE KEYS */;
INSERT INTO `auth_user` VALUES ('admin','超级管理员','751E2F5B071EA1E070677B7D66CC3ABA','gfkqj9','超级管理员',NULL,'',NULL,NULL,NULL,'2022-08-10 14:56:01',NULL,NULL),('testuser','测试用户V2','179915E7A1D567BE49F092B5BD3E10C2','mnasxp','张三',NULL,'13800000002','test2@hxapi.com',2,3,'2026-08-11 13:50:57','2026-08-11 13:56:12',NULL),('user02','二号用户','EEF99B36F0FC6754CBEF11E614A57318','8nazdy',NULL,NULL,NULL,NULL,NULL,1,'2026-08-11 13:57:26','2026-08-11 13:57:26',NULL);
/*!40000 ALTER TABLE `auth_user` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `auth_role` WRITE;
/*!40000 ALTER TABLE `auth_role` DISABLE KEYS */;
INSERT INTO `auth_role` VALUES (100,'role_admin','管理员角色',1,NULL,'2026-08-11 11:28:06'),(102,'role_user','用户角色',1,NULL,'2026-08-11 11:22:22'),(103,'role_guest','访客角色（只有查询权限）',1,NULL,NULL),(104,'role_anon','非角色（不需要任何权限就可访问）',1,NULL,NULL);
/*!40000 ALTER TABLE `auth_role` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `auth_resource` WRITE;
/*!40000 ALTER TABLE `auth_resource` DISABLE KEYS */;
INSERT INTO `auth_resource` VALUES (103,'GROUP_ACCOUNT','HXBS系列',110,'','v1.0.0',3,'POST',0,'{}',NULL,1,'2020-04-02 15:10:03','2022-08-21 03:31:25'),(110,'CATEGORY_GROUP','默认资源类型(API类别请放入此集合)',-1,NULL,'v1.0.0',3,NULL,NULL,NULL,NULL,1,'2018-04-07 14:27:58','2018-04-07 14:27:58'),(120,'USER_LIST','用户-获取用户列表',103,'/user/list/*/*','v1.0.0',2,'GET',1,'{\"pType\": \"http\", \"isAuth\": \"1\", \"all_tps\": \"500\", \"balance\": \"ROUND_ROBIN\", \"routeNum\": \"1\", \"rout_tps1\": \"200\", \"cb_timeout\": \"3000\", \"rout_port1\": \"18080\", \"api_version\": \"v1.0.0\", \"rout_ipAddr1\": \"127.0.0.1\", \"rout_weight1\": \"1\", \"cb_fail_threshold\": \"10\", \"cb_success_threshold\": \"5\"}',NULL,1,'2018-04-12 03:08:30','2026-08-10 08:23:54'),(121,'USER_AUTHORITY_ROLE','用户-给用户授权添加角色',103,'/user/authority/role','v1.0.0',2,'POST',1,'{\"all_tps\":\"20\",\"api_version\":\"v1.0.0\",\"api_version_balance\":\"1\",\"balance\":\"1\",\"isAuth\":\"1\",\"pType\":\"http\",\"rout_ipAddr1\":\"127.0.0.1\",\"rout_order1\":\"1\",\"rout_port1\":\"18080\",\"rout_tps1\":\"10\",\"rout_weight1\":\"2\"}',NULL,1,'2018-04-12 03:15:56','2022-08-20 15:59:51'),(122,'USER_AUTHORITY_ROLE','用户-删除已授权用户角色',103,'/user/authority/role','v1.0.0',2,'DELETE',1,'{\"all_tps\":\"20\",\"api_version\":\"v1.0.0\",\"api_version_balance\":\"1\",\"balance\":\"1\",\"isAuth\":\"1\",\"pType\":\"http\",\"rout_ipAddr1\":\"127.0.0.1\",\"rout_order1\":\"1\",\"rout_port1\":\"18080\",\"rout_tps1\":\"10\",\"rout_weight1\":\"2\"}',NULL,1,'2018-04-12 03:29:03','2022-08-20 15:59:58'),(167,'PASSWORD_UPDATE','用户-密码修改',103,'/user/accountupdate','v1.0.0',2,'PUT',1,'{\"all_tps\":\"20\",\"api_version\":\"v1.0.0\",\"api_version_balance\":\"1\",\"balance\":\"1\",\"isAuth\":\"1\",\"pType\":\"http\",\"rout_ipAddr1\":\"127.0.0.1\",\"rout_order1\":\"1\",\"rout_port1\":\"18080\",\"rout_tps1\":\"10\",\"rout_weight1\":\"2\"}',NULL,1,'2020-04-02 15:10:03','2022-08-20 16:00:06'),(171,NULL,'用户-登录',103,'/account/login','v1.0.0',2,'POST',0,'{\"all_tps\":\"19\",\"api_version\":\"v1.0.0\",\"api_version_balance\":\"1\",\"balance\":\"1\",\"isAuth\":\"0\",\"pType\":\"http\",\"rout_ipAddr1\":\"127.0.0.1\",\"rout_order1\":\"1\",\"rout_port1\":\"18080\",\"rout_tps1\":\"10\",\"rout_weight1\":\"2\"}',NULL,1,'2021-01-05 21:31:48','2022-08-21 03:38:01'),(219,NULL,'网关内部API',110,NULL,NULL,3,NULL,0,'{}',NULL,1,'2022-08-18 07:05:24','2022-08-21 03:31:28'),(220,NULL,'账户登录',219,'/inner/user/**','v1.0.0',2,'POST',1,'{\"all_tps\":\"10\",\"api_version\":\"v1.0.0\",\"api_version_balance\":\"1\",\"balance\":\"1\",\"isAuth\":\"1\",\"pType\":\"http\",\"rout_ipAddr1\":\"\",\"rout_order1\":\"\",\"rout_port1\":\"\",\"rout_tps1\":\"\",\"rout_weight1\":\"\"}',NULL,1,'2022-08-18 07:07:36','2022-08-21 03:31:07'),(221,NULL,'API资源管理',219,'/inner/api/**','v1.0.0',1,'POST',1,'{\"all_tps\":\"1000\",\"api_version\":\"v1.0.0\",\"api_version_balance\":\"1\",\"balance\":\"1\",\"cb_fail_threshold\":\"5\",\"cb_success_threshold\":\"2\",\"cb_timeout\":\"60000\",\"isAuth\":\"1\",\"pType\":\"http\",\"rout_ipAddr1\":\"127.0.0.1\",\"rout_port1\":\"18080\",\"rout_tps1\":\"100\",\"rout_weight1\":\"1\",\"routeNum\":\"1\",\"route_tps\":\"100\"}',NULL,1,'2022-08-18 07:08:39','2026-08-10 09:49:10'),(222,NULL,'角色/资源管理',219,'/inner/role/**','v1.0.0',2,'POST',1,'{\"all_tps\":\"10\",\"api_version\":\"v1.0.0\",\"api_version_balance\":\"1\",\"balance\":\"1\",\"isAuth\":\"1\",\"pType\":\"http\",\"rout_ipAddr1\":\"\",\"rout_order1\":\"\",\"rout_port1\":\"\",\"rout_tps1\":\"\",\"rout_weight1\":\"\"}',NULL,1,'2022-08-18 07:09:18','2022-08-21 03:31:18'),(226,NULL,'用户-注册',103,'/account/register','v1.0.0',2,'POST',0,'{\"all_tps\":\"20\",\"api_version\":\"v1.0.0\",\"api_version_balance\":\"1\",\"balance\":\"1\",\"isAuth\":\"0\",\"pType\":\"http\",\"rout_ipAddr1\":\"127.0.0.1\",\"rout_order1\":\"1\",\"rout_port1\":\"18080\",\"rout_tps1\":\"10\",\"rout_weight1\":\"1\"}',NULL,1,'2022-08-19 09:32:03','2022-08-20 16:00:34'),(227,NULL,'用户-获取当前用户角色',103,'/user/role/*','v1.0.0',2,'POST',1,'{\"all_tps\":\"20\",\"api_version\":\"v1.0.0\",\"api_version_balance\":\"1\",\"balance\":\"1\",\"isAuth\":\"1\",\"pType\":\"http\",\"rout_ipAddr1\":\"127.0.0.1\",\"rout_order1\":\"1\",\"rout_port1\":\"18080\",\"rout_tps1\":\"10\",\"rout_weight1\":\"1\"}',NULL,1,'2022-08-19 09:59:34','2022-08-20 16:00:40'),(228,NULL,'用户-退出登录',103,'/user/exit','v1.0.0',2,'POST',1,'{\"all_tps\":\"20\",\"api_version\":\"v1.0.0\",\"api_version_balance\":\"1\",\"balance\":\"1\",\"isAuth\":\"1\",\"pType\":\"http\",\"rout_ipAddr1\":\"127.0.0.1\",\"rout_order1\":\"1\",\"rout_port1\":\"18080\",\"rout_tps1\":\"10\",\"rout_weight1\":\"1\"}',NULL,1,'2022-08-19 10:04:55','2022-08-20 16:00:47'),(229,NULL,'日志-登录日志',103,'/log/accountLog/**','v1.0.0',2,'POST',1,'{\"all_tps\":\"20\",\"api_version\":\"v1.0.0\",\"api_version_balance\":\"1\",\"balance\":\"1\",\"isAuth\":\"1\",\"pType\":\"http\",\"rout_ipAddr1\":\"127.0.0.1\",\"rout_order1\":\"1\",\"rout_port1\":\"18080\",\"rout_tps1\":\"10\",\"rout_weight1\":\"1\"}',NULL,1,'2022-08-20 14:47:51','2022-08-20 16:00:57'),(230,NULL,'日志-用户操作日志查询',103,'/log/operationLog/**','v1.0.0',2,'POST',1,'{\"all_tps\":\"20\",\"api_version\":\"v1.0.0\",\"api_version_balance\":\"1\",\"balance\":\"1\",\"isAuth\":\"1\",\"pType\":\"http\",\"rout_ipAddr1\":\"127.0.0.1\",\"rout_order1\":\"1\",\"rout_port1\":\"18080\",\"rout_tps1\":\"10\",\"rout_weight1\":\"1\"}',NULL,1,'2022-08-20 14:48:56','2022-08-20 16:01:05'),(238,NULL,'WS-Echo测试',219,'/ws/echo','v1.0',1,'WS',0,'{\"all_tps\":\"100\",\"api_version\":\"v1.0\",\"balance\":\"ROUND_ROBIN\",\"isAuth\":\"0\",\"pType\":\"websocket\",\"rout_ipAddr1\":\"127.0.0.1\",\"rout_port1\":\"18085\",\"rout_tps1\":\"50\",\"rout_weight1\":\"1\",\"routeNum\":\"1\",\"route_tps\":\"50\"}',NULL,1,'2026-08-10 13:49:34',NULL),(240,'upload_echo_test','文件上传透传测试',0,'/upload/echo','v1.0.0',2,'POST',0,'{\"all_tps\":\"100\",\"api_version\":\"v1.0.0\",\"api_version_balance\":\"1\",\"balance\":\"1\",\"isAuth\":\"0\",\"pType\":\"http\",\"rout_ipAddr1\":\"127.0.0.1\",\"rout_order1\":\"1\",\"rout_port1\":\"19001\",\"rout_tps1\":\"50\",\"rout_weight1\":\"1\"}','',1,'2026-08-11 04:58:50','2026-08-11 04:58:50'),(241,'inner_usermgr','用户管理接口',219,'/inner/sysuser/**','1.0',2,'POST',0,NULL,NULL,1,'2026-08-11 05:49:38','2026-08-11 05:53:33'),(242,'inner_app','应用管理接口',219,'/inner/app/**','1.0',2,'POST',0,NULL,NULL,1,'2026-08-11 05:49:38','2026-08-11 05:49:38');
/*!40000 ALTER TABLE `auth_resource` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `auth_role_resource` WRITE;
/*!40000 ALTER TABLE `auth_role_resource` DISABLE KEYS */;
INSERT INTO `auth_role_resource` VALUES (266,105,101,'2022-08-17 22:57:29','2022-08-17 22:57:29'),(267,100,101,'2022-08-17 23:07:06','2022-08-17 23:07:06'),(280,100,220,'2022-08-18 16:58:09','2022-08-18 16:58:09'),(281,100,221,'2022-08-18 16:58:14','2022-08-18 16:58:14'),(282,100,222,'2022-08-18 18:55:33','2022-08-18 18:55:33'),(283,102,125,'2022-08-19 11:22:24','2022-08-19 11:22:24'),(284,102,124,'2022-08-19 11:32:05','2022-08-19 11:32:05'),(285,102,119,'2022-08-19 12:33:42','2022-08-19 12:33:42'),(289,100,120,'2026-08-11 04:27:47','2026-08-11 04:27:47'),(290,100,121,'2026-08-11 04:27:47','2026-08-11 04:27:47'),(291,100,122,'2026-08-11 04:27:47','2026-08-11 04:27:47'),(292,100,167,'2026-08-11 04:27:47','2026-08-11 04:27:47'),(293,100,171,'2026-08-11 04:27:47','2026-08-11 04:27:47'),(294,100,226,'2026-08-11 04:27:47','2026-08-11 04:27:47'),(295,100,227,'2026-08-11 04:27:47','2026-08-11 04:27:47'),(296,100,228,'2026-08-11 04:27:47','2026-08-11 04:27:47'),(297,100,229,'2026-08-11 04:27:47','2026-08-11 04:27:47'),(298,100,230,'2026-08-11 04:27:47','2026-08-11 04:27:47'),(299,100,241,'2026-08-11 05:49:38','2026-08-11 05:49:38'),(300,100,242,'2026-08-11 05:49:38','2026-08-11 05:49:38'),(301,100,240,'2026-08-11 14:05:08','2026-08-11 14:05:08');
/*!40000 ALTER TABLE `auth_role_resource` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `auth_user_role` WRITE;
/*!40000 ALTER TABLE `auth_user_role` DISABLE KEYS */;
INSERT INTO `auth_user_role` VALUES (51,'admin',100,'2026-08-11 05:52:00','2026-08-11 05:52:00'),(53,'user02',102,'2026-08-11 13:57:27','2026-08-11 13:57:27'),(54,'testuser',100,'2026-08-11 14:05:53','2026-08-11 14:05:53');
/*!40000 ALTER TABLE `auth_user_role` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `auth_app` WRITE;
/*!40000 ALTER TABLE `auth_app` DISABLE KEYS */;
INSERT INTO `auth_app` VALUES (4,'app_mu49tzy3usrc','脱敏测试应用','90362nh9jg8235mqzba9py8p',NULL,1,'eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOiIwMzZlNzJjNi0yODUxLTQ1MTYtOWM4Ny00OTMxYmM2ZWJhMjQiLCJzdWIiOiJhcHBfbXU0OXR6eTN1c3JjIiwiaXNzIjoiVUlPVENQX0JPT1RTSElST19QUk8iLCJpYXQiOjE3ODY0Mjk1NTcsInJvbGVzIjoicm9sZV9hZG1pbiJ9.Fc95Dompcahy7VpPyrbt51Y9oTpSm95OpXFgJVi9_q3PYXbkaQptJZHADObLJ6sacBMi916kZOm23iAs88mSPQ',0,1786429557481,'2026-08-11 14:25:57','2026-08-11 14:25:57');
/*!40000 ALTER TABLE `auth_app` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `auth_app_role` WRITE;
/*!40000 ALTER TABLE `auth_app_role` DISABLE KEYS */;
INSERT INTO `auth_app_role` VALUES (12,'app_mu49tzy3usrc',100,'2026-08-11 14:25:57','2026-08-11 14:25:57');
/*!40000 ALTER TABLE `auth_app_role` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;


SET FOREIGN_KEY_CHECKS = 1;
