/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1-mysql
 Source Server Type    : MySQL
 Source Server Version : 50735
 Source Host           : 127.0.0.1:3306
 Source Schema         : hxapigate

 Target Server Type    : MySQL
 Target Server Version : 50735
 File Encoding         : 65001

 Date: 20/08/2022 23:52:48
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for auth_account_log
-- ----------------------------
DROP TABLE IF EXISTS `auth_account_log`;
CREATE TABLE `auth_account_log` (
  `ID` int(32) NOT NULL AUTO_INCREMENT COMMENT '用户账户操作日志主键',
  `LOG_NAME` varchar(255) DEFAULT NULL COMMENT '日志名称(login,register,logout)',
  `USER_ID` varchar(32) DEFAULT NULL COMMENT '用户id',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `SUCCEED` int(3) DEFAULT NULL COMMENT '是否执行成功(0失败1成功)',
  `MESSAGE` varchar(255) DEFAULT NULL COMMENT '具体消息',
  `IP` varchar(255) DEFAULT NULL COMMENT '登录ip',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COMMENT='登录注册登出记录';

-- ----------------------------
-- Records of auth_account_log
-- ----------------------------
BEGIN;
INSERT INTO `auth_account_log` VALUES (1, '用户登录日志', 'admin', '2022-08-10 15:06:53', 1, '登录成功', NULL);
INSERT INTO `auth_account_log` VALUES (40, '用户登录日志', 'admin', '2022-08-20 23:32:17', 1, '登录成功', NULL);
INSERT INTO `auth_account_log` VALUES (41, '用户登录日志', 'admin', '2022-08-20 23:46:46', 1, '登录成功', NULL);
INSERT INTO `auth_account_log` VALUES (42, '用户登录日志', 'admin', '2022-08-20 23:47:04', 1, '登录成功', NULL);
COMMIT;

-- ----------------------------
-- Table structure for auth_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `auth_operation_log`;
CREATE TABLE `auth_operation_log` (
  `ID` int(10) NOT NULL AUTO_INCREMENT COMMENT '用户操作日志主键',
  `LOG_NAME` varchar(255) DEFAULT NULL COMMENT '日志名称',
  `USER_ID` varchar(32) DEFAULT NULL COMMENT '用户id',
  `API` varchar(255) DEFAULT NULL COMMENT 'api名称',
  `METHOD` varchar(255) DEFAULT NULL COMMENT '方法名称',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `SUCCEED` int(1) DEFAULT NULL COMMENT '是否执行成功(0失败1成功)',
  `MESSAGE` varchar(255) DEFAULT NULL COMMENT '具体消息备注',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1262 DEFAULT CHARSET=utf8mb4 COMMENT='操作日志';

-- ----------------------------
-- Records of auth_operation_log
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for auth_resource
-- ----------------------------
DROP TABLE IF EXISTS `auth_resource`;
CREATE TABLE `auth_resource` (
  `ID` int(32) NOT NULL AUTO_INCREMENT COMMENT '资源ID',
  `CODE` varchar(32) DEFAULT NULL COMMENT '资源名称',
  `NAME` varchar(128) DEFAULT NULL COMMENT '资源描述',
  `PARENT_ID` int(32) DEFAULT NULL COMMENT '父资源编码->菜单',
  `URI` varchar(128) DEFAULT NULL COMMENT '访问地址URL',
  `VERSION` varchar(20) DEFAULT NULL COMMENT '资源版本信息',
  `TYPE` int(3) DEFAULT NULL COMMENT '类型 0:内部资源（不走API网关）, 1:菜单 ,  2:资源element(rest-api) 3:资源分类',
  `METHOD` varchar(8) DEFAULT NULL COMMENT '访问方式 GET POST PUT DELETE PATCH',
  `NEED_AUTH` int(3) DEFAULT NULL COMMENT '网关是否鉴权',
  `ROUTE_INFO` text COMMENT '路由信息（json串）',
  `ICON` varchar(128) DEFAULT NULL COMMENT '图标',
  `STATUS` int(1) DEFAULT NULL COMMENT '状态   1:正常、9：禁用',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=231 DEFAULT CHARSET=utf8mb4 COMMENT='资源信息表(菜单,资源)';

-- ----------------------------
-- Records of auth_resource
-- ----------------------------
BEGIN;
INSERT INTO `auth_resource` VALUES (103, 'GROUP_ACCOUNT', 'HXBS系列', 110, '', 'v1.0.0', 3, 'POST', 0, '{}', NULL, 1, '2020-04-02 15:10:03', '2022-08-19 09:37:28');
INSERT INTO `auth_resource` VALUES (110, 'CATEGORY_GROUP', '默认资源类型(API类别请放入此集合)', -1, NULL, 'v1.0.0', 3, NULL, NULL, NULL, NULL, 1, '2018-04-07 14:27:58', '2018-04-07 14:27:58');
INSERT INTO `auth_resource` VALUES (120, 'USER_LIST', '用户-获取用户列表', 103, '/user/list/*/*', 'v1.0.0', 2, 'GET', 1, '{\"all_tps\":\"20\",\"api_version\":\"v1.0.0\",\"api_version_balance\":\"1\",\"balance\":\"1\",\"isAuth\":\"1\",\"pType\":\"http\",\"rout_ipAddr1\":\"127.0.0.1\",\"rout_order1\":\"1\",\"rout_port1\":\"8081\",\"rout_tps1\":\"10\",\"rout_weight1\":\"2\"}', NULL, 1, '2018-04-12 03:08:30', '2022-08-19 09:59:50');
INSERT INTO `auth_resource` VALUES (121, 'USER_AUTHORITY_ROLE', '用户-给用户授权添加角色', 103, '/user/authority/role', 'v1.0.0', 2, 'POST', 1, '{\"all_tps\":\"20\",\"api_version\":\"v1.0.0\",\"api_version_balance\":\"1\",\"balance\":\"1\",\"isAuth\":\"1\",\"pType\":\"http\",\"rout_ipAddr1\":\"127.0.0.1\",\"rout_order1\":\"1\",\"rout_port1\":\"8081\",\"rout_tps1\":\"10\",\"rout_weight1\":\"2\"}', NULL, 1, '2018-04-12 03:15:56', '2022-08-19 10:03:24');
INSERT INTO `auth_resource` VALUES (122, 'USER_AUTHORITY_ROLE', '用户-删除已授权用户角色', 103, '/user/authority/role', 'v1.0.0', 2, 'DELETE', 1, '{\"all_tps\":\"20\",\"api_version\":\"v1.0.0\",\"api_version_balance\":\"1\",\"balance\":\"1\",\"isAuth\":\"1\",\"pType\":\"http\",\"rout_ipAddr1\":\"127.0.0.1\",\"rout_order1\":\"1\",\"rout_port1\":\"8081\",\"rout_tps1\":\"10\",\"rout_weight1\":\"2\"}', NULL, 1, '2018-04-12 03:29:03', '2022-08-19 10:03:51');
INSERT INTO `auth_resource` VALUES (167, 'PASSWORD_UPDATE', '用户-密码修改', 103, '/user/accountupdate', 'v1.0.0', 2, 'PUT', 1, '{\"all_tps\":\"20\",\"api_version\":\"v1.0.0\",\"api_version_balance\":\"1\",\"balance\":\"1\",\"isAuth\":\"1\",\"pType\":\"http\",\"rout_ipAddr1\":\"127.0.0.1\",\"rout_order1\":\"1\",\"rout_port1\":\"8081\",\"rout_tps1\":\"10\",\"rout_weight1\":\"2\"}', NULL, 1, '2020-04-02 15:10:03', '2022-08-19 09:36:35');
INSERT INTO `auth_resource` VALUES (171, NULL, '用户-登录', 103, '/account/login', 'v1.0.0', 2, 'POST', 0, '{\"all_tps\":\"20\",\"api_version\":\"v1.0.0\",\"api_version_balance\":\"1\",\"balance\":\"1\",\"isAuth\":\"0\",\"pType\":\"http\",\"rout_ipAddr1\":\"127.0.0.1\",\"rout_order1\":\"1\",\"rout_port1\":\"8081\",\"rout_tps1\":\"10\",\"rout_weight1\":\"2\"}', NULL, 1, '2021-01-05 21:31:48', '2022-08-19 09:36:50');
INSERT INTO `auth_resource` VALUES (219, NULL, '网关内部API', 110, NULL, NULL, 3, NULL, 0, '{}', NULL, 1, '2022-08-18 07:05:24', '2022-08-18 07:09:43');
INSERT INTO `auth_resource` VALUES (220, NULL, '账户登录', 219, '/inner/user/**', 'v1.0.0', 0, 'POST', 1, '{\"all_tps\":\"10\",\"api_version\":\"v1.0.0\",\"api_version_balance\":\"1\",\"balance\":\"1\",\"isAuth\":\"1\",\"pType\":\"http\",\"rout_ipAddr1\":\"\",\"rout_order1\":\"\",\"rout_port1\":\"\",\"rout_tps1\":\"\",\"rout_weight1\":\"\"}', NULL, 1, '2022-08-18 07:07:36', '2022-08-18 07:07:53');
INSERT INTO `auth_resource` VALUES (221, NULL, 'API资源管理', 219, '/inner/api/**', 'v1.0.0', 0, 'POST', 1, '{\"all_tps\":\"10\",\"api_version\":\"v1.0.0\",\"api_version_balance\":\"1\",\"balance\":\"1\",\"isAuth\":\"1\",\"pType\":\"http\",\"rout_ipAddr1\":\"\",\"rout_order1\":\"\",\"rout_port1\":\"\",\"rout_tps1\":\"\",\"rout_weight1\":\"\"}', NULL, 1, '2022-08-18 07:08:39', NULL);
INSERT INTO `auth_resource` VALUES (222, NULL, '角色/资源管理', 219, '/inner/role/**', 'v1.0.0', 0, 'POST', 1, '{\"all_tps\":\"10\",\"api_version\":\"v1.0.0\",\"api_version_balance\":\"1\",\"balance\":\"1\",\"isAuth\":\"1\",\"pType\":\"http\",\"rout_ipAddr1\":\"\",\"rout_order1\":\"\",\"rout_port1\":\"\",\"rout_tps1\":\"\",\"rout_weight1\":\"\"}', NULL, 1, '2022-08-18 07:09:18', NULL);
INSERT INTO `auth_resource` VALUES (226, NULL, '用户-注册', 103, '/account/register', 'v1.0.0', 2, 'POST', 0, '{\"all_tps\":\"20\",\"api_version\":\"v1.0.0\",\"api_version_balance\":\"1\",\"balance\":\"1\",\"isAuth\":\"0\",\"pType\":\"http\",\"rout_ipAddr1\":\"127.0.0.1\",\"rout_order1\":\"1\",\"rout_port1\":\"8081\",\"rout_tps1\":\"10\",\"rout_weight1\":\"1\"}', NULL, 1, '2022-08-19 09:32:03', '2022-08-19 09:37:03');
INSERT INTO `auth_resource` VALUES (227, NULL, '用户-获取当前用户角色', 103, '/user/role/*', 'v1.0.0', 2, 'POST', 1, '{\"all_tps\":\"20\",\"api_version\":\"v1.0.0\",\"api_version_balance\":\"1\",\"balance\":\"1\",\"isAuth\":\"1\",\"pType\":\"http\",\"rout_ipAddr1\":\"127.0.0.1\",\"rout_order1\":\"1\",\"rout_port1\":\"8081\",\"rout_tps1\":\"10\",\"rout_weight1\":\"1\"}', NULL, 1, '2022-08-19 09:59:34', '2022-08-19 10:02:53');
INSERT INTO `auth_resource` VALUES (228, NULL, '用户-退出登录', 103, '/user/exit', 'v1.0.0', 2, 'POST', 1, '{\"all_tps\":\"20\",\"api_version\":\"v1.0.0\",\"api_version_balance\":\"1\",\"balance\":\"1\",\"isAuth\":\"1\",\"pType\":\"http\",\"rout_ipAddr1\":\"127.0.0.1\",\"rout_order1\":\"1\",\"rout_port1\":\"8081\",\"rout_tps1\":\"10\",\"rout_weight1\":\"1\"}', NULL, 1, '2022-08-19 10:04:55', NULL);
INSERT INTO `auth_resource` VALUES (229, NULL, '日志-登录日志', 103, '/log/accountLog/**', 'v1.0.0', 2, 'POST', 1, '{\"all_tps\":\"20\",\"api_version\":\"v1.0.0\",\"api_version_balance\":\"1\",\"balance\":\"1\",\"isAuth\":\"1\",\"pType\":\"http\",\"rout_ipAddr1\":\"127.0.0.1\",\"rout_order1\":\"1\",\"rout_port1\":\"8081\",\"rout_tps1\":\"10\",\"rout_weight1\":\"1\"}', NULL, 1, '2022-08-20 14:47:51', NULL);
INSERT INTO `auth_resource` VALUES (230, NULL, '日志-用户操作日志查询', 103, '/log/operationLog/**', 'v1.0.0', 2, 'POST', 1, '{\"all_tps\":\"20\",\"api_version\":\"v1.0.0\",\"api_version_balance\":\"1\",\"balance\":\"1\",\"isAuth\":\"1\",\"pType\":\"http\",\"rout_ipAddr1\":\"127.0.0.1\",\"rout_order1\":\"1\",\"rout_port1\":\"8081\",\"rout_tps1\":\"10\",\"rout_weight1\":\"1\"}', NULL, 1, '2022-08-20 14:48:56', NULL);
COMMIT;

-- ----------------------------
-- Table structure for auth_role
-- ----------------------------
DROP TABLE IF EXISTS `auth_role`;
CREATE TABLE `auth_role` (
  `ID` int(32) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `CODE` varchar(32) NOT NULL COMMENT '角色编码',
  `NAME` varchar(32) DEFAULT NULL COMMENT '角色名称',
  `STATUS` int(1) DEFAULT NULL COMMENT '状态   1:正常、9：禁用',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=105 DEFAULT CHARSET=utf8mb4 COMMENT='角色信息表';

-- ----------------------------
-- Records of auth_role
-- ----------------------------
BEGIN;
INSERT INTO `auth_role` VALUES (100, 'role_admin', '管理员角色', 1, NULL, NULL);
INSERT INTO `auth_role` VALUES (102, 'role_user', '用户角色', 1, NULL, NULL);
INSERT INTO `auth_role` VALUES (103, 'role_guest', '访客角色（只有查询权限）', 1, NULL, NULL);
INSERT INTO `auth_role` VALUES (104, 'role_anon', '非角色（不需要任何权限就可访问）', 1, NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for auth_role_resource
-- ----------------------------
DROP TABLE IF EXISTS `auth_role_resource`;
CREATE TABLE `auth_role_resource` (
  `ID` int(32) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `ROLE_ID` int(32) NOT NULL COMMENT '角色ID',
  `RESOURCE_ID` int(32) NOT NULL COMMENT '资源ID',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=286 DEFAULT CHARSET=utf8mb4 COMMENT='资源角色关联表';

-- ----------------------------
-- Records of auth_role_resource
-- ----------------------------
BEGIN;
INSERT INTO `auth_role_resource` VALUES (266, 105, 101, '2022-08-17 22:57:29', '2022-08-17 22:57:29');
INSERT INTO `auth_role_resource` VALUES (267, 100, 101, '2022-08-17 23:07:06', '2022-08-17 23:07:06');
INSERT INTO `auth_role_resource` VALUES (280, 100, 220, '2022-08-18 16:58:09', '2022-08-18 16:58:09');
INSERT INTO `auth_role_resource` VALUES (281, 100, 221, '2022-08-18 16:58:14', '2022-08-18 16:58:14');
INSERT INTO `auth_role_resource` VALUES (282, 100, 222, '2022-08-18 18:55:33', '2022-08-18 18:55:33');
INSERT INTO `auth_role_resource` VALUES (283, 102, 125, '2022-08-19 11:22:24', '2022-08-19 11:22:24');
INSERT INTO `auth_role_resource` VALUES (284, 102, 124, '2022-08-19 11:32:05', '2022-08-19 11:32:05');
INSERT INTO `auth_role_resource` VALUES (285, 102, 119, '2022-08-19 12:33:42', '2022-08-19 12:33:42');
COMMIT;

-- ----------------------------
-- Table structure for auth_user
-- ----------------------------
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
  `SEX` int(1) DEFAULT NULL COMMENT '性别(1.男 2.女)',
  `STATUS` int(3) DEFAULT NULL COMMENT '账户状态(1.正常 2.锁定 3.删除 4.非法)',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `CREATE_WHERE` int(3) DEFAULT NULL COMMENT '创建来源(1.web 2.android 3.ios 4.win 5.macos 6.ubuntu)',
  PRIMARY KEY (`UID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- ----------------------------
-- Records of auth_user
-- ----------------------------
BEGIN;
INSERT INTO `auth_user` VALUES ('admin', '超级管理员', '7ABAF42833062862D42D20E3A2CAE439', 'gfkqj9', '超级管理员', NULL, '', NULL, NULL, NULL, '2022-08-10 14:56:01', NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for auth_user_role
-- ----------------------------
DROP TABLE IF EXISTS `auth_user_role`;
CREATE TABLE `auth_user_role` (
  `ID` int(32) NOT NULL AUTO_INCREMENT COMMENT '用户角色关联表主键',
  `USER_ID` varchar(32) NOT NULL COMMENT '用户UID',
  `ROLE_ID` int(32) NOT NULL COMMENT '角色ID',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- ----------------------------
-- Records of auth_user_role
-- ----------------------------
BEGIN;
INSERT INTO `auth_user_role` VALUES (39, 'admin', 100, '2019-10-26 15:20:21', '2019-10-26 15:20:21');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
