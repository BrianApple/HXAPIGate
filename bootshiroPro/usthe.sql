/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50640
Source Host           : localhost:3306
Source Database       : usthe

Target Server Type    : MYSQL
Target Server Version : 50640
File Encoding         : 65001

Date: 2019-10-27 18:58:47
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for auth_account_log
-- ----------------------------
DROP TABLE IF EXISTS `auth_account_log`;
CREATE TABLE `auth_account_log` (
  `ID` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '用户账户操作日志主键',
  `LOG_NAME` varchar(255) DEFAULT NULL COMMENT '日志名称(login,register,logout)',
  `USER_ID` varchar(30) DEFAULT NULL COMMENT '用户id',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `SUCCEED` tinyint(4) DEFAULT NULL COMMENT '是否执行成功(0失败1成功)',
  `MESSAGE` varchar(255) DEFAULT NULL COMMENT '具体消息',
  `IP` varchar(255) DEFAULT NULL COMMENT '登录ip',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=107 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='登录注册登出记录';

-- ----------------------------
-- Records of auth_account_log
-- ----------------------------
INSERT INTO `auth_account_log` VALUES ('17', '用户登录日志', 'tom', '2018-04-22 13:22:39', '1', null, '10.0.75.2');
INSERT INTO `auth_account_log` VALUES ('92', '用户登录日志', '1', '2019-10-26 15:19:46', '1', '登录成功', '0:0:0:0:0:0:0:1');
INSERT INTO `auth_account_log` VALUES ('93', '用户注册日志', 'admin', '2019-10-26 15:20:21', '1', '注册成功', '0:0:0:0:0:0:0:1');
INSERT INTO `auth_account_log` VALUES ('94', '用户登录日志', 'admin', '2019-10-26 15:20:27', '1', '登录成功', '0:0:0:0:0:0:0:1');
INSERT INTO `auth_account_log` VALUES ('95', '用户登录日志', 'admin', '2019-10-26 15:44:13', '1', '登录成功', '0:0:0:0:0:0:0:1');
INSERT INTO `auth_account_log` VALUES ('96', '用户登录日志', 'admin', '2019-10-26 15:44:35', '1', '登录成功', '0:0:0:0:0:0:0:1');
INSERT INTO `auth_account_log` VALUES ('97', '用户登录日志', 'admin', '2019-10-26 15:45:30', '1', '登录成功', '0:0:0:0:0:0:0:1');
INSERT INTO `auth_account_log` VALUES ('98', '用户登录日志', 'admin', '2019-10-26 16:10:14', '1', '登录成功', '0:0:0:0:0:0:0:1');
INSERT INTO `auth_account_log` VALUES ('99', '用户登录日志', '1', '2019-10-26 16:24:35', '1', '登录成功', '0:0:0:0:0:0:0:1');
INSERT INTO `auth_account_log` VALUES ('100', '用户登录日志', '2', '2019-10-26 16:24:52', '1', '登录成功', '0:0:0:0:0:0:0:1');
INSERT INTO `auth_account_log` VALUES ('101', '用户登录日志', 'admin', '2019-10-26 19:04:01', '1', '登录成功', '0:0:0:0:0:0:0:1');
INSERT INTO `auth_account_log` VALUES ('102', '用户登录日志', 'admin', '2019-10-26 20:01:59', '1', '登录成功', '0:0:0:0:0:0:0:1');
INSERT INTO `auth_account_log` VALUES ('103', '用户登录日志', '1', '2019-10-27 09:12:57', '1', '登录成功', '0:0:0:0:0:0:0:1');
INSERT INTO `auth_account_log` VALUES ('104', '用户登录日志', '1', '2019-10-27 09:21:16', '1', '登录成功', '0:0:0:0:0:0:0:1');
INSERT INTO `auth_account_log` VALUES ('105', '用户登录日志', 'admin', '2019-10-27 09:27:22', '1', '登录成功', '0:0:0:0:0:0:0:1');
INSERT INTO `auth_account_log` VALUES ('106', '用户登录日志', 'admin', '2019-10-27 16:37:22', '1', '登录成功', '0:0:0:0:0:0:0:1');

-- ----------------------------
-- Table structure for auth_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `auth_operation_log`;
CREATE TABLE `auth_operation_log` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户操作日志主键',
  `LOG_NAME` varchar(255) DEFAULT NULL COMMENT '日志名称',
  `USER_ID` varchar(30) DEFAULT NULL COMMENT '用户id',
  `API` varchar(255) DEFAULT NULL COMMENT 'api名称',
  `METHOD` varchar(255) DEFAULT NULL COMMENT '方法名称',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `SUCCEED` tinyint(4) DEFAULT NULL COMMENT '是否执行成功(0失败1成功)',
  `MESSAGE` varchar(255) DEFAULT NULL COMMENT '具体消息备注',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1152 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='操作日志';

-- ----------------------------
-- Records of auth_operation_log
-- ----------------------------
INSERT INTO `auth_operation_log` VALUES ('17', '业务操作日志', 'tom', '/resource/menus', 'GET', '2018-04-22 16:05:05', '1', null);
INSERT INTO `auth_operation_log` VALUES ('18', '业务操作日志', 'tom', '/resource/menus', 'GET', '2018-04-22 16:05:09', '1', null);
INSERT INTO `auth_operation_log` VALUES ('19', '业务操作日志', 'tom', '/resource/api/-1/1/10', 'GET', '2018-04-22 16:08:15', '1', null);
INSERT INTO `auth_operation_log` VALUES ('610', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 15:19:31', '1', null);
INSERT INTO `auth_operation_log` VALUES ('611', '业务操作日志', '1', '/resource/menus', 'GET', '2019-10-26 15:19:51', '1', null);
INSERT INTO `auth_operation_log` VALUES ('612', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 15:20:32', '1', null);
INSERT INTO `auth_operation_log` VALUES ('613', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 15:41:56', '1', null);
INSERT INTO `auth_operation_log` VALUES ('614', '业务操作日志', 'admin', '/resource/menu', 'PUT', '2019-10-26 15:42:12', '1', null);
INSERT INTO `auth_operation_log` VALUES ('615', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 15:42:13', '1', null);
INSERT INTO `auth_operation_log` VALUES ('616', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 15:42:27', '1', null);
INSERT INTO `auth_operation_log` VALUES ('617', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 15:43:57', '1', null);
INSERT INTO `auth_operation_log` VALUES ('618', '业务操作日志', null, '/resource/authorityMenu', 'GET', '2019-10-26 15:44:30', '1', null);
INSERT INTO `auth_operation_log` VALUES ('619', '业务操作日志', 'admin', '/resource/authorityMenu', 'GET', '2019-10-26 15:44:35', '1', null);
INSERT INTO `auth_operation_log` VALUES ('620', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 15:44:40', '1', null);
INSERT INTO `auth_operation_log` VALUES ('621', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 15:44:40', '1', null);
INSERT INTO `auth_operation_log` VALUES ('622', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 15:44:41', '1', null);
INSERT INTO `auth_operation_log` VALUES ('623', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 15:44:42', '1', null);
INSERT INTO `auth_operation_log` VALUES ('624', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 15:44:42', '1', null);
INSERT INTO `auth_operation_log` VALUES ('625', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 15:44:43', '1', null);
INSERT INTO `auth_operation_log` VALUES ('626', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 15:44:43', '1', null);
INSERT INTO `auth_operation_log` VALUES ('627', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 15:44:43', '1', null);
INSERT INTO `auth_operation_log` VALUES ('628', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 15:44:44', '1', null);
INSERT INTO `auth_operation_log` VALUES ('629', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 15:44:45', '1', null);
INSERT INTO `auth_operation_log` VALUES ('630', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 15:44:45', '1', null);
INSERT INTO `auth_operation_log` VALUES ('631', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 15:44:45', '1', null);
INSERT INTO `auth_operation_log` VALUES ('632', '业务操作日志', 'admin', '/resource/menu', 'PUT', '2019-10-26 15:44:59', '1', null);
INSERT INTO `auth_operation_log` VALUES ('633', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 15:45:00', '1', null);
INSERT INTO `auth_operation_log` VALUES ('634', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 15:45:05', '1', null);
INSERT INTO `auth_operation_log` VALUES ('635', '业务操作日志', null, '/resource/authorityMenu', 'GET', '2019-10-26 15:45:25', '1', null);
INSERT INTO `auth_operation_log` VALUES ('636', '业务操作日志', null, '/resource/menus', 'GET', '2019-10-26 15:45:25', '1', null);
INSERT INTO `auth_operation_log` VALUES ('637', '业务操作日志', 'admin', '/resource/authorityMenu', 'GET', '2019-10-26 15:45:30', '1', null);
INSERT INTO `auth_operation_log` VALUES ('638', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 15:45:34', '1', null);
INSERT INTO `auth_operation_log` VALUES ('639', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 15:45:35', '1', null);
INSERT INTO `auth_operation_log` VALUES ('640', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 15:45:35', '1', null);
INSERT INTO `auth_operation_log` VALUES ('641', '业务操作日志', 'admin', '/resource/api/0/2/10', 'GET', '2019-10-26 15:45:48', '1', null);
INSERT INTO `auth_operation_log` VALUES ('642', '业务操作日志', 'admin', '/resource/api/0/3/10', 'GET', '2019-10-26 15:45:49', '1', null);
INSERT INTO `auth_operation_log` VALUES ('643', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 15:45:51', '1', null);
INSERT INTO `auth_operation_log` VALUES ('644', '业务操作日志', 'admin', '/resource/api/0/2/10', 'GET', '2019-10-26 15:45:52', '1', null);
INSERT INTO `auth_operation_log` VALUES ('645', '业务操作日志', 'admin', '/resource/api/0/3/10', 'GET', '2019-10-26 15:45:53', '1', null);
INSERT INTO `auth_operation_log` VALUES ('646', '业务操作日志', 'admin', '/resource/api/0/2/10', 'GET', '2019-10-26 15:45:54', '1', null);
INSERT INTO `auth_operation_log` VALUES ('647', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 15:45:55', '1', null);
INSERT INTO `auth_operation_log` VALUES ('648', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 15:46:01', '1', null);
INSERT INTO `auth_operation_log` VALUES ('649', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 15:46:02', '1', null);
INSERT INTO `auth_operation_log` VALUES ('650', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 15:46:02', '1', null);
INSERT INTO `auth_operation_log` VALUES ('651', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 15:46:02', '1', null);
INSERT INTO `auth_operation_log` VALUES ('652', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 15:46:03', '1', null);
INSERT INTO `auth_operation_log` VALUES ('653', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 15:46:03', '1', null);
INSERT INTO `auth_operation_log` VALUES ('654', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 15:46:04', '1', null);
INSERT INTO `auth_operation_log` VALUES ('655', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 15:46:04', '1', null);
INSERT INTO `auth_operation_log` VALUES ('656', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 15:46:04', '1', null);
INSERT INTO `auth_operation_log` VALUES ('657', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 15:46:05', '1', null);
INSERT INTO `auth_operation_log` VALUES ('658', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 15:46:05', '1', null);
INSERT INTO `auth_operation_log` VALUES ('659', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 15:46:05', '1', null);
INSERT INTO `auth_operation_log` VALUES ('660', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 15:46:05', '1', null);
INSERT INTO `auth_operation_log` VALUES ('661', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 15:46:06', '1', null);
INSERT INTO `auth_operation_log` VALUES ('662', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 15:46:06', '1', null);
INSERT INTO `auth_operation_log` VALUES ('663', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 15:46:06', '1', null);
INSERT INTO `auth_operation_log` VALUES ('664', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 15:46:07', '1', null);
INSERT INTO `auth_operation_log` VALUES ('665', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 15:46:07', '1', null);
INSERT INTO `auth_operation_log` VALUES ('666', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 15:46:07', '1', null);
INSERT INTO `auth_operation_log` VALUES ('667', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 15:46:07', '1', null);
INSERT INTO `auth_operation_log` VALUES ('668', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 15:46:07', '1', null);
INSERT INTO `auth_operation_log` VALUES ('669', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 15:46:08', '1', null);
INSERT INTO `auth_operation_log` VALUES ('670', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 15:46:09', '1', null);
INSERT INTO `auth_operation_log` VALUES ('671', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 15:46:09', '1', null);
INSERT INTO `auth_operation_log` VALUES ('672', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 15:46:09', '1', null);
INSERT INTO `auth_operation_log` VALUES ('673', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 15:46:10', '1', null);
INSERT INTO `auth_operation_log` VALUES ('674', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 15:46:10', '1', null);
INSERT INTO `auth_operation_log` VALUES ('675', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 15:46:29', '1', null);
INSERT INTO `auth_operation_log` VALUES ('676', '业务操作日志', 'admin', '/role/api/100/1/10', 'GET', '2019-10-26 15:46:41', '1', null);
INSERT INTO `auth_operation_log` VALUES ('677', '业务操作日志', 'admin', '/role/api/102/1/10', 'GET', '2019-10-26 15:46:43', '1', null);
INSERT INTO `auth_operation_log` VALUES ('678', '业务操作日志', 'admin', '/role/api/103/1/10', 'GET', '2019-10-26 15:46:45', '1', null);
INSERT INTO `auth_operation_log` VALUES ('679', '业务操作日志', 'admin', '/role/api/104/1/10', 'GET', '2019-10-26 15:46:46', '1', null);
INSERT INTO `auth_operation_log` VALUES ('680', '业务操作日志', 'admin', '/role/api/103/1/10', 'GET', '2019-10-26 15:46:51', '1', null);
INSERT INTO `auth_operation_log` VALUES ('681', '业务操作日志', 'admin', '/role/api/102/1/10', 'GET', '2019-10-26 15:46:51', '1', null);
INSERT INTO `auth_operation_log` VALUES ('682', '业务操作日志', 'admin', '/role/api/100/1/10', 'GET', '2019-10-26 15:46:52', '1', null);
INSERT INTO `auth_operation_log` VALUES ('683', '业务操作日志', 'admin', '/role/api/102/1/10', 'GET', '2019-10-26 15:46:52', '1', null);
INSERT INTO `auth_operation_log` VALUES ('684', '业务操作日志', 'admin', '/role/api/103/1/10', 'GET', '2019-10-26 15:46:53', '1', null);
INSERT INTO `auth_operation_log` VALUES ('685', '业务操作日志', 'admin', '/role/api/104/1/10', 'GET', '2019-10-26 15:46:53', '1', null);
INSERT INTO `auth_operation_log` VALUES ('686', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 16:01:02', '1', null);
INSERT INTO `auth_operation_log` VALUES ('687', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 16:01:54', '1', null);
INSERT INTO `auth_operation_log` VALUES ('688', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 16:01:55', '1', null);
INSERT INTO `auth_operation_log` VALUES ('689', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 16:01:55', '1', null);
INSERT INTO `auth_operation_log` VALUES ('690', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 16:01:56', '1', null);
INSERT INTO `auth_operation_log` VALUES ('691', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 16:01:59', '1', null);
INSERT INTO `auth_operation_log` VALUES ('692', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 16:01:59', '1', null);
INSERT INTO `auth_operation_log` VALUES ('693', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 16:02:00', '1', null);
INSERT INTO `auth_operation_log` VALUES ('694', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 16:02:01', '1', null);
INSERT INTO `auth_operation_log` VALUES ('695', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 16:02:01', '1', null);
INSERT INTO `auth_operation_log` VALUES ('696', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 16:02:01', '1', null);
INSERT INTO `auth_operation_log` VALUES ('697', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 16:02:02', '1', null);
INSERT INTO `auth_operation_log` VALUES ('698', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 16:02:02', '1', null);
INSERT INTO `auth_operation_log` VALUES ('699', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 16:02:03', '1', null);
INSERT INTO `auth_operation_log` VALUES ('700', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 16:02:04', '1', null);
INSERT INTO `auth_operation_log` VALUES ('701', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 16:02:04', '1', null);
INSERT INTO `auth_operation_log` VALUES ('702', '业务操作日志', 'admin', '/resource/api/110/1/10', 'GET', '2019-10-26 16:02:18', '1', null);
INSERT INTO `auth_operation_log` VALUES ('703', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 16:02:18', '1', null);
INSERT INTO `auth_operation_log` VALUES ('704', '业务操作日志', 'admin', '/resource/api/110/1/10', 'GET', '2019-10-26 16:02:43', '1', null);
INSERT INTO `auth_operation_log` VALUES ('705', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 16:02:43', '1', null);
INSERT INTO `auth_operation_log` VALUES ('706', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 16:03:36', '1', null);
INSERT INTO `auth_operation_log` VALUES ('707', '业务操作日志', 'admin', '/resource/api/103/1/10', 'GET', '2019-10-26 16:03:36', '1', null);
INSERT INTO `auth_operation_log` VALUES ('708', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 16:03:45', '1', null);
INSERT INTO `auth_operation_log` VALUES ('709', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 16:03:46', '1', null);
INSERT INTO `auth_operation_log` VALUES ('710', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 16:03:46', '1', null);
INSERT INTO `auth_operation_log` VALUES ('711', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 16:03:49', '1', null);
INSERT INTO `auth_operation_log` VALUES ('712', '业务操作日志', 'admin', '/resource/api/103/1/10', 'GET', '2019-10-26 16:03:49', '1', null);
INSERT INTO `auth_operation_log` VALUES ('713', '业务操作日志', 'admin', '/resource/api/103/1/10', 'GET', '2019-10-26 16:03:55', '1', null);
INSERT INTO `auth_operation_log` VALUES ('714', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 16:03:55', '1', null);
INSERT INTO `auth_operation_log` VALUES ('715', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 16:03:57', '1', null);
INSERT INTO `auth_operation_log` VALUES ('716', '业务操作日志', 'admin', '/resource/api/110/1/10', 'GET', '2019-10-26 16:03:57', '1', null);
INSERT INTO `auth_operation_log` VALUES ('717', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 16:03:59', '1', null);
INSERT INTO `auth_operation_log` VALUES ('718', '业务操作日志', 'admin', '/resource/api/115/1/10', 'GET', '2019-10-26 16:03:59', '1', null);
INSERT INTO `auth_operation_log` VALUES ('719', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 16:04:01', '1', null);
INSERT INTO `auth_operation_log` VALUES ('720', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 16:04:01', '1', null);
INSERT INTO `auth_operation_log` VALUES ('721', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 16:04:05', '1', null);
INSERT INTO `auth_operation_log` VALUES ('722', '业务操作日志', 'admin', '/resource/api/103/1/10', 'GET', '2019-10-26 16:04:05', '1', null);
INSERT INTO `auth_operation_log` VALUES ('723', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 16:04:09', '1', null);
INSERT INTO `auth_operation_log` VALUES ('724', '业务操作日志', 'admin', '/resource/api/118/1/10', 'GET', '2019-10-26 16:04:09', '1', null);
INSERT INTO `auth_operation_log` VALUES ('725', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 16:04:11', '1', null);
INSERT INTO `auth_operation_log` VALUES ('726', '业务操作日志', 'admin', '/resource/api/132/1/10', 'GET', '2019-10-26 16:04:11', '1', null);
INSERT INTO `auth_operation_log` VALUES ('727', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 16:04:13', '1', null);
INSERT INTO `auth_operation_log` VALUES ('728', '业务操作日志', 'admin', '/resource/api/103/1/10', 'GET', '2019-10-26 16:04:13', '1', null);
INSERT INTO `auth_operation_log` VALUES ('729', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 16:04:14', '1', null);
INSERT INTO `auth_operation_log` VALUES ('730', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 16:04:14', '1', null);
INSERT INTO `auth_operation_log` VALUES ('731', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 16:05:15', '1', null);
INSERT INTO `auth_operation_log` VALUES ('732', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 16:05:15', '1', null);
INSERT INTO `auth_operation_log` VALUES ('733', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 16:05:15', '1', null);
INSERT INTO `auth_operation_log` VALUES ('734', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 16:05:16', '1', null);
INSERT INTO `auth_operation_log` VALUES ('735', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 16:05:16', '1', null);
INSERT INTO `auth_operation_log` VALUES ('736', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 16:05:16', '1', null);
INSERT INTO `auth_operation_log` VALUES ('737', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 16:05:17', '1', null);
INSERT INTO `auth_operation_log` VALUES ('738', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 16:05:18', '1', null);
INSERT INTO `auth_operation_log` VALUES ('739', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 16:05:18', '1', null);
INSERT INTO `auth_operation_log` VALUES ('740', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 16:05:19', '1', null);
INSERT INTO `auth_operation_log` VALUES ('741', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 16:05:19', '1', null);
INSERT INTO `auth_operation_log` VALUES ('742', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 16:05:19', '1', null);
INSERT INTO `auth_operation_log` VALUES ('743', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 16:05:20', '1', null);
INSERT INTO `auth_operation_log` VALUES ('744', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 16:05:20', '1', null);
INSERT INTO `auth_operation_log` VALUES ('745', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 16:05:20', '1', null);
INSERT INTO `auth_operation_log` VALUES ('746', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 16:05:21', '1', null);
INSERT INTO `auth_operation_log` VALUES ('747', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 16:05:21', '1', null);
INSERT INTO `auth_operation_log` VALUES ('748', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 16:05:21', '1', null);
INSERT INTO `auth_operation_log` VALUES ('749', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 16:05:21', '1', null);
INSERT INTO `auth_operation_log` VALUES ('750', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 16:05:22', '1', null);
INSERT INTO `auth_operation_log` VALUES ('751', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 16:05:22', '1', null);
INSERT INTO `auth_operation_log` VALUES ('752', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 16:05:22', '1', null);
INSERT INTO `auth_operation_log` VALUES ('753', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 16:05:38', '1', null);
INSERT INTO `auth_operation_log` VALUES ('754', '业务操作日志', 'admin', '/resource/api/118/1/10', 'GET', '2019-10-26 16:05:38', '1', null);
INSERT INTO `auth_operation_log` VALUES ('755', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 16:05:40', '1', null);
INSERT INTO `auth_operation_log` VALUES ('756', '业务操作日志', 'admin', '/resource/api/115/1/10', 'GET', '2019-10-26 16:05:40', '1', null);
INSERT INTO `auth_operation_log` VALUES ('757', '业务操作日志', 'admin', '/resource/api/103/1/10', 'GET', '2019-10-26 16:05:52', '1', null);
INSERT INTO `auth_operation_log` VALUES ('758', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 16:05:52', '1', null);
INSERT INTO `auth_operation_log` VALUES ('759', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 16:06:28', '1', null);
INSERT INTO `auth_operation_log` VALUES ('760', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 16:06:41', '1', null);
INSERT INTO `auth_operation_log` VALUES ('761', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 16:06:41', '1', null);
INSERT INTO `auth_operation_log` VALUES ('762', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 16:06:41', '1', null);
INSERT INTO `auth_operation_log` VALUES ('763', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 16:06:45', '1', null);
INSERT INTO `auth_operation_log` VALUES ('764', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 16:06:45', '1', null);
INSERT INTO `auth_operation_log` VALUES ('765', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 16:08:34', '1', null);
INSERT INTO `auth_operation_log` VALUES ('766', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 16:09:06', '1', null);
INSERT INTO `auth_operation_log` VALUES ('767', '业务操作日志', 'admin', '/resource/menu', 'PUT', '2019-10-26 16:09:29', '1', null);
INSERT INTO `auth_operation_log` VALUES ('768', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 16:09:29', '1', null);
INSERT INTO `auth_operation_log` VALUES ('769', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 16:09:35', '1', null);
INSERT INTO `auth_operation_log` VALUES ('770', '业务操作日志', null, '/resource/authorityMenu', 'GET', '2019-10-26 16:10:07', '1', null);
INSERT INTO `auth_operation_log` VALUES ('771', '业务操作日志', null, '/resource/menus', 'GET', '2019-10-26 16:10:07', '1', null);
INSERT INTO `auth_operation_log` VALUES ('772', '业务操作日志', 'admin', '/resource/authorityMenu', 'GET', '2019-10-26 16:10:14', '1', null);
INSERT INTO `auth_operation_log` VALUES ('773', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 16:10:18', '1', null);
INSERT INTO `auth_operation_log` VALUES ('774', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 16:11:28', '1', null);
INSERT INTO `auth_operation_log` VALUES ('775', '业务操作日志', 'admin', '/role/user/104/1/10', 'GET', '2019-10-26 16:11:41', '1', null);
INSERT INTO `auth_operation_log` VALUES ('776', '业务操作日志', 'admin', '/role/user/103/1/10', 'GET', '2019-10-26 16:11:41', '1', null);
INSERT INTO `auth_operation_log` VALUES ('777', '业务操作日志', 'admin', '/role/user/102/1/10', 'GET', '2019-10-26 16:11:42', '1', null);
INSERT INTO `auth_operation_log` VALUES ('778', '业务操作日志', 'admin', '/role/user/100/1/10', 'GET', '2019-10-26 16:11:42', '1', null);
INSERT INTO `auth_operation_log` VALUES ('779', '业务操作日志', 'admin', '/role/user/103/1/10', 'GET', '2019-10-26 16:11:44', '1', null);
INSERT INTO `auth_operation_log` VALUES ('780', '业务操作日志', 'admin', '/role/user/105/1/10', 'GET', '2019-10-26 16:11:44', '1', null);
INSERT INTO `auth_operation_log` VALUES ('781', '业务操作日志', 'admin', '/role/user/104/1/10', 'GET', '2019-10-26 16:11:46', '1', null);
INSERT INTO `auth_operation_log` VALUES ('782', '业务操作日志', 'admin', '/role/user/105/1/10', 'GET', '2019-10-26 16:11:46', '1', null);
INSERT INTO `auth_operation_log` VALUES ('783', '业务操作日志', 'admin', '/role/user/104/1/10', 'GET', '2019-10-26 16:11:47', '1', null);
INSERT INTO `auth_operation_log` VALUES ('784', '业务操作日志', 'admin', '/role/user/105/1/10', 'GET', '2019-10-26 16:11:47', '1', null);
INSERT INTO `auth_operation_log` VALUES ('785', '业务操作日志', 'admin', '/role/user/104/1/10', 'GET', '2019-10-26 16:11:49', '1', null);
INSERT INTO `auth_operation_log` VALUES ('786', '业务操作日志', 'admin', '/role/user/103/1/10', 'GET', '2019-10-26 16:11:50', '1', null);
INSERT INTO `auth_operation_log` VALUES ('787', '业务操作日志', 'admin', '/role/user/102/1/10', 'GET', '2019-10-26 16:11:50', '1', null);
INSERT INTO `auth_operation_log` VALUES ('788', '业务操作日志', 'admin', '/role/user/100/1/10', 'GET', '2019-10-26 16:11:50', '1', null);
INSERT INTO `auth_operation_log` VALUES ('789', '业务操作日志', 'admin', '/role/user/102/1/10', 'GET', '2019-10-26 16:11:51', '1', null);
INSERT INTO `auth_operation_log` VALUES ('790', '业务操作日志', 'admin', '/role/user/103/1/10', 'GET', '2019-10-26 16:11:51', '1', null);
INSERT INTO `auth_operation_log` VALUES ('791', '业务操作日志', 'admin', '/role/user/104/1/10', 'GET', '2019-10-26 16:11:52', '1', null);
INSERT INTO `auth_operation_log` VALUES ('792', '业务操作日志', 'admin', '/role/user/105/1/10', 'GET', '2019-10-26 16:11:52', '1', null);
INSERT INTO `auth_operation_log` VALUES ('793', '业务操作日志', 'admin', '/role/api/105/1/10', 'GET', '2019-10-26 16:11:58', '1', null);
INSERT INTO `auth_operation_log` VALUES ('794', '业务操作日志', 'admin', '/role/api/-/105/1/10', 'GET', '2019-10-26 16:12:05', '1', null);
INSERT INTO `auth_operation_log` VALUES ('795', '业务操作日志', 'admin', '/role/api/-/105/2/10', 'GET', '2019-10-26 16:12:12', '1', null);
INSERT INTO `auth_operation_log` VALUES ('796', '业务操作日志', 'admin', '/role/api/-/105/3/10', 'GET', '2019-10-26 16:12:13', '1', null);
INSERT INTO `auth_operation_log` VALUES ('797', '业务操作日志', 'admin', '/role/api/-/105/1/10', 'GET', '2019-10-26 16:12:15', '1', null);
INSERT INTO `auth_operation_log` VALUES ('798', '业务操作日志', 'admin', '/role/authority/resource', 'POST', '2019-10-26 16:12:30', '1', null);
INSERT INTO `auth_operation_log` VALUES ('799', '业务操作日志', 'admin', '/role/api/105/1/10', 'GET', '2019-10-26 16:12:33', '1', null);
INSERT INTO `auth_operation_log` VALUES ('800', '业务操作日志', 'admin', '/role/api/105/1/10', 'GET', '2019-10-26 16:12:34', '1', null);
INSERT INTO `auth_operation_log` VALUES ('801', '业务操作日志', 'admin', '/role/api/-/105/1/10', 'GET', '2019-10-26 16:12:35', '1', null);
INSERT INTO `auth_operation_log` VALUES ('802', '业务操作日志', 'admin', '/role/authority/resource', 'POST', '2019-10-26 16:12:38', '1', null);
INSERT INTO `auth_operation_log` VALUES ('803', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 16:13:34', '1', null);
INSERT INTO `auth_operation_log` VALUES ('804', '业务操作日志', 'admin', '/role/api/-/105/1/10', 'GET', '2019-10-26 16:13:38', '1', null);
INSERT INTO `auth_operation_log` VALUES ('805', '业务操作日志', 'admin', '/role/authority/resource', 'POST', '2019-10-26 16:13:42', '1', null);
INSERT INTO `auth_operation_log` VALUES ('806', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 16:14:37', '1', null);
INSERT INTO `auth_operation_log` VALUES ('807', '业务操作日志', 'admin', '/role/api/-/105/1/10', 'GET', '2019-10-26 16:14:43', '1', null);
INSERT INTO `auth_operation_log` VALUES ('808', '业务操作日志', 'admin', '/role/authority/resource', 'POST', '2019-10-26 16:14:45', '1', null);
INSERT INTO `auth_operation_log` VALUES ('809', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 16:15:14', '1', null);
INSERT INTO `auth_operation_log` VALUES ('810', '业务操作日志', 'admin', '/role/api/-/105/1/10', 'GET', '2019-10-26 16:15:19', '1', null);
INSERT INTO `auth_operation_log` VALUES ('811', '业务操作日志', 'admin', '/role/authority/resource', 'POST', '2019-10-26 16:15:21', '1', null);
INSERT INTO `auth_operation_log` VALUES ('812', '业务操作日志', 'admin', '/role/api/104/1/10', 'GET', '2019-10-26 16:17:13', '1', null);
INSERT INTO `auth_operation_log` VALUES ('813', '业务操作日志', 'admin', '/role/api/-/104/1/10', 'GET', '2019-10-26 16:17:16', '1', null);
INSERT INTO `auth_operation_log` VALUES ('814', '业务操作日志', 'admin', '/role/authority/resource', 'POST', '2019-10-26 16:17:19', '1', null);
INSERT INTO `auth_operation_log` VALUES ('815', '业务操作日志', 'admin', '/role/api/105/1/10', 'GET', '2019-10-26 16:17:36', '1', null);
INSERT INTO `auth_operation_log` VALUES ('816', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 16:19:12', '1', null);
INSERT INTO `auth_operation_log` VALUES ('817', '业务操作日志', 'admin', '/role/api/-/105/1/10', 'GET', '2019-10-26 16:19:17', '1', null);
INSERT INTO `auth_operation_log` VALUES ('818', '业务操作日志', 'admin', '/role/authority/resource', 'POST', '2019-10-26 16:19:20', '1', null);
INSERT INTO `auth_operation_log` VALUES ('819', '业务操作日志', 'admin', '/role/api/105/1/10', 'GET', '2019-10-26 16:20:18', '1', null);
INSERT INTO `auth_operation_log` VALUES ('820', '业务操作日志', 'admin', '/role/api/-/105/1/10', 'GET', '2019-10-26 16:20:19', '1', null);
INSERT INTO `auth_operation_log` VALUES ('821', '业务操作日志', 'admin', '/role/authority/resource', 'POST', '2019-10-26 16:20:21', '1', null);
INSERT INTO `auth_operation_log` VALUES ('822', '业务操作日志', 'admin', '/role/api/105/1/10', 'GET', '2019-10-26 16:20:25', '1', null);
INSERT INTO `auth_operation_log` VALUES ('823', '业务操作日志', 'admin', '/role/api/105/1/10', 'GET', '2019-10-26 16:20:41', '1', null);
INSERT INTO `auth_operation_log` VALUES ('824', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 16:20:54', '1', null);
INSERT INTO `auth_operation_log` VALUES ('825', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 16:20:58', '1', null);
INSERT INTO `auth_operation_log` VALUES ('826', '业务操作日志', 'admin', '/role/api/-/105/1/10', 'GET', '2019-10-26 16:21:13', '1', null);
INSERT INTO `auth_operation_log` VALUES ('827', '业务操作日志', 'admin', '/role/authority/resource', 'POST', '2019-10-26 16:21:17', '1', null);
INSERT INTO `auth_operation_log` VALUES ('828', '业务操作日志', 'admin', '/role/api/105/1/10', 'GET', '2019-10-26 16:21:20', '1', null);
INSERT INTO `auth_operation_log` VALUES ('829', '业务操作日志', 'admin', '/role/api/-/105/1/10', 'GET', '2019-10-26 16:21:30', '1', null);
INSERT INTO `auth_operation_log` VALUES ('830', '业务操作日志', 'admin', '/role/api/-/105/2/10', 'GET', '2019-10-26 16:21:43', '1', null);
INSERT INTO `auth_operation_log` VALUES ('831', '业务操作日志', 'admin', '/role/api/-/105/3/10', 'GET', '2019-10-26 16:21:49', '1', null);
INSERT INTO `auth_operation_log` VALUES ('832', '业务操作日志', 'admin', '/role/api/-/105/2/10', 'GET', '2019-10-26 16:21:54', '1', null);
INSERT INTO `auth_operation_log` VALUES ('833', '业务操作日志', 'admin', '/role/api/-/105/1/10', 'GET', '2019-10-26 16:21:56', '1', null);
INSERT INTO `auth_operation_log` VALUES ('834', '业务操作日志', 'admin', '/role/api/-/105/2/10', 'GET', '2019-10-26 16:22:05', '1', null);
INSERT INTO `auth_operation_log` VALUES ('835', '业务操作日志', 'admin', '/role/api/-/105/3/10', 'GET', '2019-10-26 16:22:27', '1', null);
INSERT INTO `auth_operation_log` VALUES ('836', '业务操作日志', 'admin', '/role/api/-/105/3/10', 'GET', '2019-10-26 16:22:36', '1', null);
INSERT INTO `auth_operation_log` VALUES ('837', '业务操作日志', 'admin', '/role/api/-/105/2/10', 'GET', '2019-10-26 16:22:40', '1', null);
INSERT INTO `auth_operation_log` VALUES ('838', '业务操作日志', 'admin', '/role/api/-/105/1/10', 'GET', '2019-10-26 16:22:41', '1', null);
INSERT INTO `auth_operation_log` VALUES ('839', '业务操作日志', 'admin', '/role/authority/resource', 'POST', '2019-10-26 16:22:45', '1', null);
INSERT INTO `auth_operation_log` VALUES ('840', '业务操作日志', 'admin', '/role/api/105/1/10', 'GET', '2019-10-26 16:22:45', '1', null);
INSERT INTO `auth_operation_log` VALUES ('841', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 16:22:49', '1', null);
INSERT INTO `auth_operation_log` VALUES ('842', '业务操作日志', null, '/role/1/10', 'GET', '2019-10-26 16:23:10', '1', null);
INSERT INTO `auth_operation_log` VALUES ('843', '业务操作日志', null, '/resource/authorityMenu', 'GET', '2019-10-26 16:23:10', '1', null);
INSERT INTO `auth_operation_log` VALUES ('844', '业务操作日志', '1', '/resource/authorityMenu', 'GET', '2019-10-26 16:24:35', '1', null);
INSERT INTO `auth_operation_log` VALUES ('845', '业务操作日志', '2', '/resource/authorityMenu', 'GET', '2019-10-26 16:24:52', '1', null);
INSERT INTO `auth_operation_log` VALUES ('846', '业务操作日志', '2', '/role/1/10', 'GET', '2019-10-26 16:25:29', '1', null);
INSERT INTO `auth_operation_log` VALUES ('847', '业务操作日志', '2', '/role/api/103/1/10', 'GET', '2019-10-26 16:25:36', '1', null);
INSERT INTO `auth_operation_log` VALUES ('848', '业务操作日志', '2', '/role/api/100/1/10', 'GET', '2019-10-26 16:25:44', '1', null);
INSERT INTO `auth_operation_log` VALUES ('849', '业务操作日志', '2', '/role/user/105/1/10', 'GET', '2019-10-26 16:25:49', '1', null);
INSERT INTO `auth_operation_log` VALUES ('850', '业务操作日志', '2', '/role/user/105/1/10', 'GET', '2019-10-26 16:26:00', '1', null);
INSERT INTO `auth_operation_log` VALUES ('851', '业务操作日志', '2', '/role/api/105/1/10', 'GET', '2019-10-26 16:26:06', '1', null);
INSERT INTO `auth_operation_log` VALUES ('852', '业务操作日志', '2', '/role/api/104/1/10', 'GET', '2019-10-26 16:26:07', '1', null);
INSERT INTO `auth_operation_log` VALUES ('853', '业务操作日志', '2', '/role/api/105/1/10', 'GET', '2019-10-26 16:26:07', '1', null);
INSERT INTO `auth_operation_log` VALUES ('854', '业务操作日志', '2', '/role/api/104/1/10', 'GET', '2019-10-26 16:26:11', '1', null);
INSERT INTO `auth_operation_log` VALUES ('855', '业务操作日志', '2', '/role/api/105/1/10', 'GET', '2019-10-26 16:26:12', '1', null);
INSERT INTO `auth_operation_log` VALUES ('856', '业务操作日志', '2', '/role/api/104/1/10', 'GET', '2019-10-26 16:26:13', '1', null);
INSERT INTO `auth_operation_log` VALUES ('857', '业务操作日志', '2', '/role/api/103/1/10', 'GET', '2019-10-26 16:26:13', '1', null);
INSERT INTO `auth_operation_log` VALUES ('858', '业务操作日志', '2', '/role/api/102/1/10', 'GET', '2019-10-26 16:26:14', '1', null);
INSERT INTO `auth_operation_log` VALUES ('859', '业务操作日志', '2', '/role/api/100/1/10', 'GET', '2019-10-26 16:26:15', '1', null);
INSERT INTO `auth_operation_log` VALUES ('860', '业务操作日志', '2', '/role/api/103/1/10', 'GET', '2019-10-26 16:26:15', '1', null);
INSERT INTO `auth_operation_log` VALUES ('861', '业务操作日志', '2', '/role/api/104/1/10', 'GET', '2019-10-26 16:26:16', '1', null);
INSERT INTO `auth_operation_log` VALUES ('862', '业务操作日志', '2', '/role/api/105/1/10', 'GET', '2019-10-26 16:26:16', '1', null);
INSERT INTO `auth_operation_log` VALUES ('863', '业务操作日志', '2', '/role/1/10', 'GET', '2019-10-26 16:26:23', '1', null);
INSERT INTO `auth_operation_log` VALUES ('864', '业务操作日志', '2', '/role/api/104/1/10', 'GET', '2019-10-26 16:26:30', '1', null);
INSERT INTO `auth_operation_log` VALUES ('865', '业务操作日志', '2', '/role/api/105/1/10', 'GET', '2019-10-26 16:26:33', '1', null);
INSERT INTO `auth_operation_log` VALUES ('866', '业务操作日志', '2', '/role/api/104/1/10', 'GET', '2019-10-26 16:26:34', '1', null);
INSERT INTO `auth_operation_log` VALUES ('867', '业务操作日志', '2', '/role/api/103/1/10', 'GET', '2019-10-26 16:26:35', '1', null);
INSERT INTO `auth_operation_log` VALUES ('868', '业务操作日志', '2', '/role/api/102/1/10', 'GET', '2019-10-26 16:26:36', '1', null);
INSERT INTO `auth_operation_log` VALUES ('869', '业务操作日志', '2', '/role/api/100/1/10', 'GET', '2019-10-26 16:26:37', '1', null);
INSERT INTO `auth_operation_log` VALUES ('870', '业务操作日志', '2', '/role/1/10', 'GET', '2019-10-26 16:26:40', '1', null);
INSERT INTO `auth_operation_log` VALUES ('871', '业务操作日志', '2', '/role/api/102/1/10', 'GET', '2019-10-26 16:26:45', '1', null);
INSERT INTO `auth_operation_log` VALUES ('872', '业务操作日志', '2', '/role/api/100/1/10', 'GET', '2019-10-26 16:26:46', '1', null);
INSERT INTO `auth_operation_log` VALUES ('873', '业务操作日志', '2', '/role/api/102/1/10', 'GET', '2019-10-26 16:26:47', '1', null);
INSERT INTO `auth_operation_log` VALUES ('874', '业务操作日志', '2', '/role/api/103/1/10', 'GET', '2019-10-26 16:26:47', '1', null);
INSERT INTO `auth_operation_log` VALUES ('875', '业务操作日志', '2', '/role/api/104/1/10', 'GET', '2019-10-26 16:26:48', '1', null);
INSERT INTO `auth_operation_log` VALUES ('876', '业务操作日志', '2', '/role/api/105/1/10', 'GET', '2019-10-26 16:26:48', '1', null);
INSERT INTO `auth_operation_log` VALUES ('877', '业务操作日志', '2', '/role/api/104/1/10', 'GET', '2019-10-26 16:26:49', '1', null);
INSERT INTO `auth_operation_log` VALUES ('878', '业务操作日志', '2', '/role/api/103/1/10', 'GET', '2019-10-26 16:26:49', '1', null);
INSERT INTO `auth_operation_log` VALUES ('879', '业务操作日志', '2', '/role/api/102/1/10', 'GET', '2019-10-26 16:26:49', '1', null);
INSERT INTO `auth_operation_log` VALUES ('880', '业务操作日志', '2', '/role/api/100/1/10', 'GET', '2019-10-26 16:26:50', '1', null);
INSERT INTO `auth_operation_log` VALUES ('881', '业务操作日志', '2', '/role/api/102/1/10', 'GET', '2019-10-26 16:26:50', '1', null);
INSERT INTO `auth_operation_log` VALUES ('882', '业务操作日志', '2', '/role/api/103/1/10', 'GET', '2019-10-26 16:26:51', '1', null);
INSERT INTO `auth_operation_log` VALUES ('883', '业务操作日志', '2', '/role/api/104/1/10', 'GET', '2019-10-26 16:26:51', '1', null);
INSERT INTO `auth_operation_log` VALUES ('884', '业务操作日志', '2', '/role/api/105/1/10', 'GET', '2019-10-26 16:26:52', '1', null);
INSERT INTO `auth_operation_log` VALUES ('885', '业务操作日志', '2', '/role/api/104/1/10', 'GET', '2019-10-26 16:26:52', '1', null);
INSERT INTO `auth_operation_log` VALUES ('886', '业务操作日志', '2', '/role/api/103/1/10', 'GET', '2019-10-26 16:26:52', '1', null);
INSERT INTO `auth_operation_log` VALUES ('887', '业务操作日志', '2', '/role/api/102/1/10', 'GET', '2019-10-26 16:26:53', '1', null);
INSERT INTO `auth_operation_log` VALUES ('888', '业务操作日志', '2', '/role/api/100/1/10', 'GET', '2019-10-26 16:26:53', '1', null);
INSERT INTO `auth_operation_log` VALUES ('889', '业务操作日志', '2', '/role/api/102/1/10', 'GET', '2019-10-26 16:26:53', '1', null);
INSERT INTO `auth_operation_log` VALUES ('890', '业务操作日志', '2', '/role/api/103/1/10', 'GET', '2019-10-26 16:26:54', '1', null);
INSERT INTO `auth_operation_log` VALUES ('891', '业务操作日志', '2', '/role/api/104/1/10', 'GET', '2019-10-26 16:26:54', '1', null);
INSERT INTO `auth_operation_log` VALUES ('892', '业务操作日志', '2', '/role/api/105/1/10', 'GET', '2019-10-26 16:26:54', '1', null);
INSERT INTO `auth_operation_log` VALUES ('893', '业务操作日志', '2', '/role/api/105/1/10', 'GET', '2019-10-26 16:26:57', '1', null);
INSERT INTO `auth_operation_log` VALUES ('894', '业务操作日志', '2', '/role/1/10', 'GET', '2019-10-26 18:08:37', '1', null);
INSERT INTO `auth_operation_log` VALUES ('895', '业务操作日志', '2', '/role/api/104/1/10', 'GET', '2019-10-26 18:08:42', '1', null);
INSERT INTO `auth_operation_log` VALUES ('896', '业务操作日志', '2', '/role/api/105/1/10', 'GET', '2019-10-26 18:08:42', '1', null);
INSERT INTO `auth_operation_log` VALUES ('897', '业务操作日志', '2', '/role/api/-/105/1/10', 'GET', '2019-10-26 18:08:51', '1', null);
INSERT INTO `auth_operation_log` VALUES ('898', '业务操作日志', '2', '/role/1/10', 'GET', '2019-10-26 18:55:08', '1', null);
INSERT INTO `auth_operation_log` VALUES ('899', '业务操作日志', '2', '/role/api/104/1/10', 'GET', '2019-10-26 18:55:24', '1', null);
INSERT INTO `auth_operation_log` VALUES ('900', '业务操作日志', '2', '/role/api/105/1/10', 'GET', '2019-10-26 18:55:25', '1', null);
INSERT INTO `auth_operation_log` VALUES ('901', '业务操作日志', '2', '/role/user/-/105/1/10', 'GET', '2019-10-26 18:55:53', '1', null);
INSERT INTO `auth_operation_log` VALUES ('902', '业务操作日志', '2', '/role/user/105/1/10', 'GET', '2019-10-26 18:56:01', '1', null);
INSERT INTO `auth_operation_log` VALUES ('903', '业务操作日志', '2', '/role/user/105/1/10', 'GET', '2019-10-26 18:56:02', '1', null);
INSERT INTO `auth_operation_log` VALUES ('904', '业务操作日志', '2', '/role/user/-/105/1/10', 'GET', '2019-10-26 18:56:19', '1', null);
INSERT INTO `auth_operation_log` VALUES ('905', '业务操作日志', '2', '/user/authority/role', 'POST', '2019-10-26 18:56:21', '1', null);
INSERT INTO `auth_operation_log` VALUES ('906', '业务操作日志', '2', '/role/1/10', 'GET', '2019-10-26 18:56:30', '1', null);
INSERT INTO `auth_operation_log` VALUES ('907', '业务操作日志', '2', '/role/user/-/105/1/10', 'GET', '2019-10-26 18:56:47', '1', null);
INSERT INTO `auth_operation_log` VALUES ('908', '业务操作日志', '2', '/user/authority/role', 'POST', '2019-10-26 18:56:49', '1', null);
INSERT INTO `auth_operation_log` VALUES ('909', '业务操作日志', '2', '/role/user/-/105/1/10', 'GET', '2019-10-26 18:57:44', '1', null);
INSERT INTO `auth_operation_log` VALUES ('910', '业务操作日志', '2', '/user/authority/role', 'POST', '2019-10-26 18:57:46', '1', null);
INSERT INTO `auth_operation_log` VALUES ('911', '业务操作日志', '2', '/role/user/105/1/10', 'GET', '2019-10-26 18:57:46', '1', null);
INSERT INTO `auth_operation_log` VALUES ('912', '业务操作日志', '2', '/resource/menus', 'GET', '2019-10-26 18:57:53', '1', null);
INSERT INTO `auth_operation_log` VALUES ('913', '业务操作日志', '2', '/resource/api/-1/1/10', 'GET', '2019-10-26 18:57:53', '1', null);
INSERT INTO `auth_operation_log` VALUES ('914', '业务操作日志', '2', '/resource/api/0/1/10', 'GET', '2019-10-26 18:57:53', '1', null);
INSERT INTO `auth_operation_log` VALUES ('915', '业务操作日志', '2', '/role/1/10', 'GET', '2019-10-26 18:57:54', '1', null);
INSERT INTO `auth_operation_log` VALUES ('916', '业务操作日志', '2', '/resource/api/-1/1/10', 'GET', '2019-10-26 18:57:54', '1', null);
INSERT INTO `auth_operation_log` VALUES ('917', '业务操作日志', '2', '/resource/api/0/1/10', 'GET', '2019-10-26 18:57:54', '1', null);
INSERT INTO `auth_operation_log` VALUES ('918', '业务操作日志', '2', '/resource/menus', 'GET', '2019-10-26 18:57:55', '1', null);
INSERT INTO `auth_operation_log` VALUES ('919', '业务操作日志', '2', '/resource/api/-1/1/10', 'GET', '2019-10-26 18:57:55', '1', null);
INSERT INTO `auth_operation_log` VALUES ('920', '业务操作日志', '2', '/resource/api/0/1/10', 'GET', '2019-10-26 18:57:55', '1', null);
INSERT INTO `auth_operation_log` VALUES ('921', '业务操作日志', '2', '/role/1/10', 'GET', '2019-10-26 18:57:56', '1', null);
INSERT INTO `auth_operation_log` VALUES ('922', '业务操作日志', '2', '/resource/api/-1/1/10', 'GET', '2019-10-26 18:57:56', '1', null);
INSERT INTO `auth_operation_log` VALUES ('923', '业务操作日志', '2', '/resource/api/0/1/10', 'GET', '2019-10-26 18:57:56', '1', null);
INSERT INTO `auth_operation_log` VALUES ('924', '业务操作日志', '2', '/resource/menus', 'GET', '2019-10-26 18:57:56', '1', null);
INSERT INTO `auth_operation_log` VALUES ('925', '业务操作日志', '2', '/role/1/10', 'GET', '2019-10-26 18:57:57', '1', null);
INSERT INTO `auth_operation_log` VALUES ('926', '业务操作日志', '2', '/resource/api/-1/1/10', 'GET', '2019-10-26 18:57:57', '1', null);
INSERT INTO `auth_operation_log` VALUES ('927', '业务操作日志', '2', '/resource/api/0/1/10', 'GET', '2019-10-26 18:57:57', '1', null);
INSERT INTO `auth_operation_log` VALUES ('928', '业务操作日志', '2', '/resource/menus', 'GET', '2019-10-26 18:57:57', '1', null);
INSERT INTO `auth_operation_log` VALUES ('929', '业务操作日志', '2', '/resource/api/-1/1/10', 'GET', '2019-10-26 18:57:58', '1', null);
INSERT INTO `auth_operation_log` VALUES ('930', '业务操作日志', '2', '/resource/api/0/1/10', 'GET', '2019-10-26 18:57:58', '1', null);
INSERT INTO `auth_operation_log` VALUES ('931', '业务操作日志', '2', '/role/1/10', 'GET', '2019-10-26 18:57:58', '1', null);
INSERT INTO `auth_operation_log` VALUES ('932', '业务操作日志', '2', '/resource/api/-1/1/10', 'GET', '2019-10-26 18:57:59', '1', null);
INSERT INTO `auth_operation_log` VALUES ('933', '业务操作日志', '2', '/resource/api/0/1/10', 'GET', '2019-10-26 18:57:59', '1', null);
INSERT INTO `auth_operation_log` VALUES ('934', '业务操作日志', '2', '/resource/menus', 'GET', '2019-10-26 18:57:59', '1', null);
INSERT INTO `auth_operation_log` VALUES ('935', '业务操作日志', '2', '/resource/api/0/1/10', 'GET', '2019-10-26 18:57:59', '1', null);
INSERT INTO `auth_operation_log` VALUES ('936', '业务操作日志', '2', '/resource/api/-1/1/10', 'GET', '2019-10-26 18:57:59', '1', null);
INSERT INTO `auth_operation_log` VALUES ('937', '业务操作日志', '2', '/role/1/10', 'GET', '2019-10-26 18:58:00', '1', null);
INSERT INTO `auth_operation_log` VALUES ('938', '业务操作日志', '2', '/resource/api/-1/1/10', 'GET', '2019-10-26 18:58:00', '1', null);
INSERT INTO `auth_operation_log` VALUES ('939', '业务操作日志', '2', '/resource/api/0/1/10', 'GET', '2019-10-26 18:58:00', '1', null);
INSERT INTO `auth_operation_log` VALUES ('940', '业务操作日志', '2', '/resource/menus', 'GET', '2019-10-26 18:58:00', '1', null);
INSERT INTO `auth_operation_log` VALUES ('941', '业务操作日志', '2', '/resource/api/0/1/10', 'GET', '2019-10-26 18:58:01', '1', null);
INSERT INTO `auth_operation_log` VALUES ('942', '业务操作日志', '2', '/resource/api/-1/1/10', 'GET', '2019-10-26 18:58:01', '1', null);
INSERT INTO `auth_operation_log` VALUES ('943', '业务操作日志', '2', '/role/1/10', 'GET', '2019-10-26 18:58:01', '1', null);
INSERT INTO `auth_operation_log` VALUES ('944', '业务操作日志', '2', '/resource/api/0/1/10', 'GET', '2019-10-26 18:58:02', '1', null);
INSERT INTO `auth_operation_log` VALUES ('945', '业务操作日志', '2', '/resource/api/-1/1/10', 'GET', '2019-10-26 18:58:02', '1', null);
INSERT INTO `auth_operation_log` VALUES ('946', '业务操作日志', '2', '/resource/menus', 'GET', '2019-10-26 18:58:02', '1', null);
INSERT INTO `auth_operation_log` VALUES ('947', '业务操作日志', '2', '/resource/api/0/1/10', 'GET', '2019-10-26 18:58:03', '1', null);
INSERT INTO `auth_operation_log` VALUES ('948', '业务操作日志', '2', '/resource/api/-1/1/10', 'GET', '2019-10-26 18:58:03', '1', null);
INSERT INTO `auth_operation_log` VALUES ('949', '业务操作日志', '2', '/role/1/10', 'GET', '2019-10-26 18:58:03', '1', null);
INSERT INTO `auth_operation_log` VALUES ('950', '业务操作日志', '2', '/resource/api/-1/1/10', 'GET', '2019-10-26 18:58:03', '1', null);
INSERT INTO `auth_operation_log` VALUES ('951', '业务操作日志', '2', '/resource/api/0/1/10', 'GET', '2019-10-26 18:58:03', '1', null);
INSERT INTO `auth_operation_log` VALUES ('952', '业务操作日志', '2', '/role/1/10', 'GET', '2019-10-26 18:58:04', '1', null);
INSERT INTO `auth_operation_log` VALUES ('953', '业务操作日志', '2', '/resource/menus', 'GET', '2019-10-26 18:58:05', '1', null);
INSERT INTO `auth_operation_log` VALUES ('954', '业务操作日志', '2', '/resource/api/-1/1/10', 'GET', '2019-10-26 18:58:05', '1', null);
INSERT INTO `auth_operation_log` VALUES ('955', '业务操作日志', '2', '/resource/api/0/1/10', 'GET', '2019-10-26 18:58:05', '1', null);
INSERT INTO `auth_operation_log` VALUES ('956', '业务操作日志', '2', '/role/1/10', 'GET', '2019-10-26 18:58:05', '1', null);
INSERT INTO `auth_operation_log` VALUES ('957', '业务操作日志', '2', '/resource/api/-1/1/10', 'GET', '2019-10-26 18:58:07', '1', null);
INSERT INTO `auth_operation_log` VALUES ('958', '业务操作日志', '2', '/resource/api/0/1/10', 'GET', '2019-10-26 18:58:07', '1', null);
INSERT INTO `auth_operation_log` VALUES ('959', '业务操作日志', '2', '/role/1/10', 'GET', '2019-10-26 18:58:28', '1', null);
INSERT INTO `auth_operation_log` VALUES ('960', '业务操作日志', '2', '/role/api/102/1/10', 'GET', '2019-10-26 18:58:47', '1', null);
INSERT INTO `auth_operation_log` VALUES ('961', '业务操作日志', '2', '/role/api/100/1/10', 'GET', '2019-10-26 18:58:47', '1', null);
INSERT INTO `auth_operation_log` VALUES ('962', '业务操作日志', '2', '/role/api/102/1/10', 'GET', '2019-10-26 18:59:01', '1', null);
INSERT INTO `auth_operation_log` VALUES ('963', '业务操作日志', '2', '/role/api/103/1/10', 'GET', '2019-10-26 18:59:02', '1', null);
INSERT INTO `auth_operation_log` VALUES ('964', '业务操作日志', '2', '/role/api/104/1/10', 'GET', '2019-10-26 18:59:02', '1', null);
INSERT INTO `auth_operation_log` VALUES ('965', '业务操作日志', '2', '/role/api/105/1/10', 'GET', '2019-10-26 18:59:03', '1', null);
INSERT INTO `auth_operation_log` VALUES ('966', '业务操作日志', '2', '/role/user/104/1/10', 'GET', '2019-10-26 18:59:10', '1', null);
INSERT INTO `auth_operation_log` VALUES ('967', '业务操作日志', '2', '/role/user/105/1/10', 'GET', '2019-10-26 18:59:11', '1', null);
INSERT INTO `auth_operation_log` VALUES ('968', '业务操作日志', '2', '/role/user/104/1/10', 'GET', '2019-10-26 18:59:12', '1', null);
INSERT INTO `auth_operation_log` VALUES ('969', '业务操作日志', '2', '/role/user/103/1/10', 'GET', '2019-10-26 18:59:12', '1', null);
INSERT INTO `auth_operation_log` VALUES ('970', '业务操作日志', '2', '/role/user/102/1/10', 'GET', '2019-10-26 18:59:13', '1', null);
INSERT INTO `auth_operation_log` VALUES ('971', '业务操作日志', '2', '/role/user/100/1/10', 'GET', '2019-10-26 18:59:14', '1', null);
INSERT INTO `auth_operation_log` VALUES ('972', '业务操作日志', '2', '/role/user/105/1/10', 'GET', '2019-10-26 18:59:15', '1', null);
INSERT INTO `auth_operation_log` VALUES ('973', '业务操作日志', '2', '/role/user/100/1/10', 'GET', '2019-10-26 19:02:22', '1', null);
INSERT INTO `auth_operation_log` VALUES ('974', '业务操作日志', '2', '/role/user/102/1/10', 'GET', '2019-10-26 19:02:22', '1', null);
INSERT INTO `auth_operation_log` VALUES ('975', '业务操作日志', '2', '/role/user/103/1/10', 'GET', '2019-10-26 19:02:23', '1', null);
INSERT INTO `auth_operation_log` VALUES ('976', '业务操作日志', '2', '/role/user/104/1/10', 'GET', '2019-10-26 19:02:23', '1', null);
INSERT INTO `auth_operation_log` VALUES ('977', '业务操作日志', '2', '/role/user/105/1/10', 'GET', '2019-10-26 19:02:24', '1', null);
INSERT INTO `auth_operation_log` VALUES ('978', '业务操作日志', '2', '/role/user/100/1/10', 'GET', '2019-10-26 19:02:25', '1', null);
INSERT INTO `auth_operation_log` VALUES ('979', '业务操作日志', '2', '/role/user/103/1/10', 'GET', '2019-10-26 19:02:25', '1', null);
INSERT INTO `auth_operation_log` VALUES ('980', '业务操作日志', '2', '/role/user/100/1/10', 'GET', '2019-10-26 19:02:26', '1', null);
INSERT INTO `auth_operation_log` VALUES ('981', '业务操作日志', '2', '/role/user/102/1/10', 'GET', '2019-10-26 19:02:26', '1', null);
INSERT INTO `auth_operation_log` VALUES ('982', '业务操作日志', '2', '/role/user/103/1/10', 'GET', '2019-10-26 19:02:26', '1', null);
INSERT INTO `auth_operation_log` VALUES ('983', '业务操作日志', '2', '/role/user/104/1/10', 'GET', '2019-10-26 19:02:27', '1', null);
INSERT INTO `auth_operation_log` VALUES ('984', '业务操作日志', '2', '/role/user/105/1/10', 'GET', '2019-10-26 19:02:27', '1', null);
INSERT INTO `auth_operation_log` VALUES ('985', '业务操作日志', '2', '/role/user/103/1/10', 'GET', '2019-10-26 19:02:28', '1', null);
INSERT INTO `auth_operation_log` VALUES ('986', '业务操作日志', '2', '/role/user/103/1/10', 'GET', '2019-10-26 19:02:29', '1', null);
INSERT INTO `auth_operation_log` VALUES ('987', '业务操作日志', '2', '/role/user/102/1/10', 'GET', '2019-10-26 19:02:29', '1', null);
INSERT INTO `auth_operation_log` VALUES ('988', '业务操作日志', '2', '/role/user/100/1/10', 'GET', '2019-10-26 19:02:30', '1', null);
INSERT INTO `auth_operation_log` VALUES ('989', '业务操作日志', '2', '/resource/menus', 'GET', '2019-10-26 19:02:31', '1', null);
INSERT INTO `auth_operation_log` VALUES ('990', '业务操作日志', '2', '/resource/api/-1/1/10', 'GET', '2019-10-26 19:02:33', '1', null);
INSERT INTO `auth_operation_log` VALUES ('991', '业务操作日志', '2', '/resource/api/0/1/10', 'GET', '2019-10-26 19:02:33', '1', null);
INSERT INTO `auth_operation_log` VALUES ('992', '业务操作日志', '2', '/role/1/10', 'GET', '2019-10-26 19:02:34', '1', null);
INSERT INTO `auth_operation_log` VALUES ('993', '业务操作日志', '2', '/resource/api/0/1/10', 'GET', '2019-10-26 19:02:34', '1', null);
INSERT INTO `auth_operation_log` VALUES ('994', '业务操作日志', '2', '/resource/api/-1/1/10', 'GET', '2019-10-26 19:02:34', '1', null);
INSERT INTO `auth_operation_log` VALUES ('995', '业务操作日志', '2', '/resource/menus', 'GET', '2019-10-26 19:02:34', '1', null);
INSERT INTO `auth_operation_log` VALUES ('996', '业务操作日志', '2', '/resource/menus', 'GET', '2019-10-26 19:02:43', '1', null);
INSERT INTO `auth_operation_log` VALUES ('997', '业务操作日志', '2', '/resource/menu', 'PUT', '2019-10-26 19:03:27', '1', null);
INSERT INTO `auth_operation_log` VALUES ('998', '业务操作日志', '2', '/resource/menus', 'GET', '2019-10-26 19:03:27', '1', null);
INSERT INTO `auth_operation_log` VALUES ('999', '业务操作日志', '2', '/resource/menu', 'PUT', '2019-10-26 19:03:42', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1000', '业务操作日志', '2', '/resource/menus', 'GET', '2019-10-26 19:03:43', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1001', '业务操作日志', null, '/resource/authorityMenu', 'GET', '2019-10-26 19:03:56', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1002', '业务操作日志', null, '/resource/menus', 'GET', '2019-10-26 19:03:56', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1003', '业务操作日志', 'admin', '/resource/authorityMenu', 'GET', '2019-10-26 19:04:01', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1004', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 19:04:07', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1005', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 19:04:20', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1006', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 19:04:20', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1007', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 19:04:20', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1008', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 19:04:21', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1009', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 19:04:23', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1010', '业务操作日志', 'admin', '/resource/menu', 'PUT', '2019-10-26 19:04:35', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1011', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 19:04:35', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1012', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 19:04:39', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1013', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 19:04:43', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1014', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 19:04:44', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1015', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 19:04:44', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1016', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 19:04:44', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1017', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 19:04:45', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1018', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 19:04:45', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1019', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 19:04:45', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1020', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 19:09:04', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1021', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 19:09:04', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1022', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 19:09:04', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1023', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 19:09:21', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1024', '业务操作日志', 'admin', '/role/api/103/1/10', 'GET', '2019-10-26 19:09:33', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1025', '业务操作日志', 'admin', '/role/api/104/1/10', 'GET', '2019-10-26 19:09:33', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1026', '业务操作日志', 'admin', '/role/api/105/1/10', 'GET', '2019-10-26 19:09:34', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1027', '业务操作日志', 'admin', '/role/api/-/105/1/10', 'GET', '2019-10-26 19:09:37', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1028', '业务操作日志', 'admin', '/role/api/105/1/10', 'GET', '2019-10-26 19:09:49', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1029', '业务操作日志', 'admin', '/role/api/100/1/10', 'GET', '2019-10-26 19:10:39', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1030', '业务操作日志', 'admin', '/role/user/100/1/10', 'GET', '2019-10-26 19:10:45', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1031', '业务操作日志', 'admin', '/role/user/102/1/10', 'GET', '2019-10-26 19:10:49', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1032', '业务操作日志', 'admin', '/role/user/103/1/10', 'GET', '2019-10-26 19:10:50', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1033', '业务操作日志', 'admin', '/role/user/104/1/10', 'GET', '2019-10-26 19:10:51', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1034', '业务操作日志', 'admin', '/role/user/105/1/10', 'GET', '2019-10-26 19:10:53', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1035', '业务操作日志', 'admin', '/role/user/104/1/10', 'GET', '2019-10-26 19:10:54', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1036', '业务操作日志', 'admin', '/role/user/103/1/10', 'GET', '2019-10-26 19:10:54', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1037', '业务操作日志', 'admin', '/role/user/102/1/10', 'GET', '2019-10-26 19:10:54', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1038', '业务操作日志', 'admin', '/role/user/100/1/10', 'GET', '2019-10-26 19:10:56', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1039', '业务操作日志', 'admin', '/role/user/102/1/10', 'GET', '2019-10-26 19:10:58', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1040', '业务操作日志', 'admin', '/role/user/100/1/10', 'GET', '2019-10-26 19:11:11', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1041', '业务操作日志', 'admin', '/role/user/102/1/10', 'GET', '2019-10-26 19:11:12', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1042', '业务操作日志', 'admin', '/role/user/103/1/10', 'GET', '2019-10-26 19:11:12', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1043', '业务操作日志', 'admin', '/role/user/104/1/10', 'GET', '2019-10-26 19:11:12', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1044', '业务操作日志', 'admin', '/role/user/105/1/10', 'GET', '2019-10-26 19:11:13', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1045', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 19:11:14', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1046', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 19:11:14', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1047', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 19:11:14', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1048', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 19:11:14', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1049', '业务操作日志', 'admin', '/role/menu/-/105/1/10', 'GET', '2019-10-26 19:15:09', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1050', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 19:15:41', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1051', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 19:15:42', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1052', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 19:15:42', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1053', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 19:15:48', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1054', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 19:16:43', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1055', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 19:17:18', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1056', '业务操作日志', 'admin', '/role/api/104/1/10', 'GET', '2019-10-26 19:17:22', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1057', '业务操作日志', 'admin', '/role/api/105/1/10', 'GET', '2019-10-26 19:17:22', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1058', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 19:28:35', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1059', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 19:28:35', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1060', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 19:28:35', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1061', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 19:31:54', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1062', '业务操作日志', 'admin', '/role/menu/-/105/1/10', 'GET', '2019-10-26 19:32:01', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1063', '业务操作日志', 'admin', '/role/menu/105/1/10', 'GET', '2019-10-26 19:32:11', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1064', '业务操作日志', 'admin', '/role/user/104/1/10', 'GET', '2019-10-26 19:32:24', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1065', '业务操作日志', 'admin', '/role/user/105/1/10', 'GET', '2019-10-26 19:32:24', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1066', '业务操作日志', 'admin', '/role/menu/-/105/1/10', 'GET', '2019-10-26 19:32:28', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1067', '业务操作日志', 'admin', '/role/authority/resource', 'POST', '2019-10-26 19:32:32', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1068', '业务操作日志', 'admin', '/role/menu/105/1/10', 'GET', '2019-10-26 19:32:33', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1069', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 19:32:41', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1070', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 19:32:41', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1071', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 19:32:42', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1072', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 19:32:42', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1073', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 19:32:42', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1074', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 19:32:43', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1075', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 19:32:43', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1076', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 19:32:43', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1077', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 19:32:43', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1078', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 19:32:44', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1079', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 19:32:44', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1080', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 19:32:44', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1081', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 19:32:44', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1082', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 19:32:44', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1083', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 19:32:45', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1084', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 19:32:46', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1085', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 19:32:46', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1086', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 19:32:47', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1087', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 19:32:47', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1088', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 19:32:47', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1089', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 19:32:48', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1090', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 19:32:48', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1091', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 19:32:48', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1092', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 19:32:48', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1093', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 19:32:49', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1094', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 19:32:49', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1095', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 19:32:49', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1096', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 19:32:51', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1097', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 19:32:51', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1098', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 19:32:51', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1099', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 19:32:51', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1100', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 19:32:51', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1101', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 19:32:52', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1102', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 19:32:52', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1103', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 19:32:52', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1104', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 19:32:53', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1105', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 19:32:53', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1106', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 19:32:53', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1107', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 19:32:53', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1108', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 20:02:02', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1109', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 20:02:03', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1110', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 20:02:03', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1111', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 20:02:04', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1112', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 20:02:05', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1113', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 20:02:05', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1114', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 20:02:05', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1115', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 20:02:06', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1116', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 20:02:09', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1117', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 20:02:09', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1118', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 20:02:09', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1119', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 20:08:05', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1120', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 20:08:05', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1121', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 20:08:05', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1122', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 20:08:08', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1123', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 20:08:08', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1124', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 20:08:08', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1125', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 20:08:09', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1126', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 20:08:09', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1127', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 20:08:09', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1128', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 20:08:10', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1129', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 20:08:10', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1130', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 20:08:10', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1131', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 20:08:11', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1132', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 20:08:11', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1133', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-26 20:08:11', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1134', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-26 20:08:12', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1135', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-26 20:08:12', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1136', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-26 20:08:12', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1137', '业务操作日志', 'admin', '/role/1/10', 'GET', '2019-10-27 08:44:06', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1138', '业务操作日志', '1', '/resource/menus', 'GET', '2019-10-27 09:13:16', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1139', '业务操作日志', '1', '/resource/api/-1/1/10', 'GET', '2019-10-27 09:13:17', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1140', '业务操作日志', '1', '/resource/api/0/1/10', 'GET', '2019-10-27 09:13:17', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1141', '业务操作日志', '1', '/role/1/10', 'GET', '2019-10-27 09:13:18', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1142', '业务操作日志', '1', '/resource/api/-1/1/10', 'GET', '2019-10-27 09:13:19', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1143', '业务操作日志', '1', '/resource/api/0/1/10', 'GET', '2019-10-27 09:13:19', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1144', '业务操作日志', '1', '/resource/menus', 'GET', '2019-10-27 09:13:20', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1145', '业务操作日志', '1', '/resource/authorityMenu', 'GET', '2019-10-27 09:21:16', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1146', '业务操作日志', 'admin', '/resource/authorityMenu', 'GET', '2019-10-27 09:27:22', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1147', '业务操作日志', 'admin', '/resource/menus', 'GET', '2019-10-27 16:37:30', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1148', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-27 17:17:43', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1149', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-27 17:20:55', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1150', '业务操作日志', 'admin', '/resource/api/-1/1/10', 'GET', '2019-10-27 18:40:28', '1', null);
INSERT INTO `auth_operation_log` VALUES ('1151', '业务操作日志', 'admin', '/resource/api/0/1/10', 'GET', '2019-10-27 18:45:20', '1', null);

-- ----------------------------
-- Table structure for auth_resource
-- ----------------------------
DROP TABLE IF EXISTS `auth_resource`;
CREATE TABLE `auth_resource` (
  `ID` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '资源ID',
  `CODE` varchar(30) DEFAULT NULL COMMENT '资源名称',
  `NAME` varchar(30) DEFAULT NULL COMMENT '资源描述',
  `PARENT_ID` int(11) DEFAULT NULL COMMENT '父资源编码->菜单',
  `URI` varchar(100) DEFAULT NULL COMMENT '访问地址URL',
  `TYPE` smallint(4) DEFAULT NULL COMMENT '类型 1:菜单menu 2:资源element(rest-api) 3:资源分类',
  `METHOD` varchar(10) DEFAULT NULL COMMENT '访问方式 GET POST PUT DELETE PATCH',
  `ICON` varchar(100) DEFAULT NULL COMMENT '图标',
  `STATUS` smallint(4) DEFAULT '1' COMMENT '状态   1:正常、9：禁用',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=146 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='资源信息表(菜单,资源)';

-- ----------------------------
-- Records of auth_resource
-- ----------------------------
INSERT INTO `auth_resource` VALUES ('101', 'ACCOUNT_LOGIN', '用户登录', '103', '/account/login', '2', 'POST', null, '1', null, null);
INSERT INTO `auth_resource` VALUES ('103', 'GROUP_ACCOUNT', '账户系列', '110', '', '3', 'POST', null, '1', null, null);
INSERT INTO `auth_resource` VALUES ('104', 'USER_MAGE', '用户管理', '-1', '', '1', 'POST', 'fa fa-user', '1', null, null);
INSERT INTO `auth_resource` VALUES ('106', 'RESOURCE_MAGE', '资源配置', '-1', '', '1', 'POST', 'fa fa-pie-chart', '1', null, null);
INSERT INTO `auth_resource` VALUES ('107', 'MENU_MANAGE', '菜单管理', '106', '/index/menu', '1', 'POST', 'fa fa-th', '1', null, null);
INSERT INTO `auth_resource` VALUES ('109', 'API_MANAGE', 'API管理', '106', '/index/api', '1', null, 'fa fa-share', '1', '2018-04-07 09:40:24', '2018-04-07 09:40:24');
INSERT INTO `auth_resource` VALUES ('110', 'CATEGORY_GROUP', '分类集合(API类别请放入此集合)', '-1', null, '3', null, null, '1', '2018-04-07 14:27:58', '2018-04-07 14:27:58');
INSERT INTO `auth_resource` VALUES ('112', 'ACCOUNT_REGISTER', '用户注册', '103', '/account/register', '2', 'POST', null, '1', '2018-04-07 16:21:45', '2018-04-07 16:21:45');
INSERT INTO `auth_resource` VALUES ('115', 'GROUP_USER', '用户系列', '110', '', '3', 'GET', null, '1', '2018-04-07 16:31:01', '2018-04-07 16:31:01');
INSERT INTO `auth_resource` VALUES ('117', 'ROLE_MANAGE', '角色/应用管理', '106', '/index/role', '1', null, 'fa fa-adjust', '1', '2018-04-08 05:36:31', '2018-04-08 05:36:31');
INSERT INTO `auth_resource` VALUES ('118', 'GROUP_RESOURCE', '资源系列', '110', null, '3', null, null, '1', '2018-04-09 02:29:14', '2018-04-09 02:29:14');
INSERT INTO `auth_resource` VALUES ('119', 'USER_ROLE_APPID', '获取对应用户角色', '115', '/user/role/*', '2', 'GET', null, '1', '2018-04-12 03:07:22', '2018-04-12 03:07:22');
INSERT INTO `auth_resource` VALUES ('120', 'USER_LIST', '获取用户列表', '115', '/user/list', '2', 'GET', null, '1', '2018-04-12 03:08:30', '2018-04-12 03:08:30');
INSERT INTO `auth_resource` VALUES ('121', 'USER_AUTHORITY_ROLE', '给用户授权添加角色', '115', '/user/authority/role', '2', 'POST', null, '1', '2018-04-12 03:15:56', '2018-04-12 03:15:56');
INSERT INTO `auth_resource` VALUES ('122', 'USER_AUTHORITY_ROLE', '删除已经授权的用户角色', '115', '/user/authority/role', '2', 'DELETE', null, '1', '2018-04-12 03:29:03', '2018-04-12 03:29:03');
INSERT INTO `auth_resource` VALUES ('123', 'RESOURCE_AUTORITYMENU', '获取用户被授权菜单', '118', '/resource/authorityMenu', '2', 'GET', null, '1', '2018-04-12 05:30:03', '2018-04-12 05:30:03');
INSERT INTO `auth_resource` VALUES ('124', 'RESOURCE_MENUS', '获取全部菜单列', '118', '/resource/menus', '2', 'GET', null, '1', '2018-04-12 05:42:46', '2018-04-12 05:42:46');
INSERT INTO `auth_resource` VALUES ('125', 'RESOURCE_MENU', '增加菜单', '118', '/resource/menu', '2', 'POST', null, '1', '2018-04-12 06:15:39', '2018-04-12 06:15:39');
INSERT INTO `auth_resource` VALUES ('126', 'RESOURCE_MENU', '修改菜单', '118', '/resource/menu', '2', 'PUT', null, '1', '2018-04-12 06:16:35', '2018-04-12 06:16:35');
INSERT INTO `auth_resource` VALUES ('127', 'RESOURCE_MENU', '删除菜单', '118', '/resource/menu', '2', 'DELETE', null, '1', '2018-04-12 06:17:18', '2018-04-12 06:17:18');
INSERT INTO `auth_resource` VALUES ('128', 'RESOURCE_API', '获取API list', '118', '/resource/api/*/*/*', '2', 'GET', null, '1', '2018-04-12 06:18:02', '2018-04-12 06:18:02');
INSERT INTO `auth_resource` VALUES ('129', 'RESOURCE_API', '增加API', '118', '/resource/api', '2', 'POST', null, '1', '2018-04-12 06:18:42', '2018-04-12 06:18:42');
INSERT INTO `auth_resource` VALUES ('130', 'RESOURCE_API', '修改API', '118', '/resource/api', '2', 'PUT', null, '1', '2018-04-12 06:19:32', '2018-04-12 06:19:32');
INSERT INTO `auth_resource` VALUES ('131', 'RESOURCE_API', '删除API', '118', '/resource/api', '2', 'DELETE', null, '1', '2018-04-12 06:20:03', '2018-04-12 06:20:03');
INSERT INTO `auth_resource` VALUES ('132', 'GROUP_ROLE', '角色系列', '110', null, '3', null, null, '1', '2018-04-12 06:22:02', '2018-04-12 06:22:02');
INSERT INTO `auth_resource` VALUES ('133', 'ROLE_USER', '获取角色关联用户列表', '132', '/role/user/*/*/*', '2', 'GET', null, '1', '2018-04-12 06:22:59', '2018-04-12 06:22:59');
INSERT INTO `auth_resource` VALUES ('134', 'ROLE_USER', '获取角色未关联用户列表', '132', '/role/user/-/*/*/*', '2', 'GET', null, '1', '2018-04-12 06:24:09', '2018-04-12 06:24:09');
INSERT INTO `auth_resource` VALUES ('135', 'ROLE_API', '获取角色关联API资源', '132', '/role/api/*/*/*', '2', 'GET', null, '1', '2018-04-12 06:25:32', '2018-04-12 06:25:32');
INSERT INTO `auth_resource` VALUES ('136', 'ROLE_API', '获取角色未关联API资源', '132', '/role/api/-/*/*/*', '2', 'GET', null, '1', '2018-04-12 06:26:12', '2018-04-12 06:26:12');
INSERT INTO `auth_resource` VALUES ('137', 'ROLE_MENU', '获取角色关联菜单资源', '132', '/role/menu/*/*/*', '2', 'GET', null, '1', '2018-04-12 06:27:20', '2018-04-12 06:27:20');
INSERT INTO `auth_resource` VALUES ('138', 'ROLE_MENU', '获取角色未关联菜单资源', '132', '/role/menu/-/*/*/*', '2', 'GET', null, '1', '2018-04-12 06:27:56', '2018-04-12 06:27:56');
INSERT INTO `auth_resource` VALUES ('139', 'ROLE_AUTHORITY_RESOURCE', '授权资源给角色', '132', '/role/authority/resource', '2', 'POST', null, '1', '2018-04-12 06:29:45', '2018-04-12 06:29:45');
INSERT INTO `auth_resource` VALUES ('140', 'ROLE_AUTHORITY_RESOURCE', '删除角色的授权资源', '132', '/role/authority/resource', '2', 'DELETE', null, '1', '2018-04-12 06:31:12', '2018-04-12 06:31:12');
INSERT INTO `auth_resource` VALUES ('141', 'ROLE', '获取角色LIST', '132', '/role/*/*', '2', 'GET', null, '1', '2018-04-12 06:32:34', '2018-04-12 06:32:34');
INSERT INTO `auth_resource` VALUES ('142', 'ROLE', '添加角色', '132', '/role', '2', 'POST', null, '1', '2018-04-12 06:33:25', '2018-04-12 06:33:25');
INSERT INTO `auth_resource` VALUES ('143', 'USER', '更新角色', '132', '/role', '2', 'PUT', null, '1', '2018-04-12 06:34:27', '2018-04-12 06:34:27');
INSERT INTO `auth_resource` VALUES ('144', 'ROLE', '删除角色', '132', '/role', '2', 'DELETE', null, '1', '2018-04-12 06:35:15', '2018-04-12 06:35:15');
INSERT INTO `auth_resource` VALUES ('145', 'LOG_WATCH', '日志记录', '106', '/index/log', '1', null, 'fa fa-rss-square', '1', '2018-04-22 08:12:24', '2018-04-22 08:12:24');

-- ----------------------------
-- Table structure for auth_role
-- ----------------------------
DROP TABLE IF EXISTS `auth_role`;
CREATE TABLE `auth_role` (
  `ID` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `CODE` varchar(30) NOT NULL COMMENT '角色编码',
  `NAME` varchar(30) DEFAULT NULL COMMENT '角色名称',
  `STATUS` smallint(4) DEFAULT '1' COMMENT '状态   1:正常、9：禁用',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=106 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='角色信息表';

-- ----------------------------
-- Records of auth_role
-- ----------------------------
INSERT INTO `auth_role` VALUES ('100', 'role_admin', '管理员角色', '1', null, null);
INSERT INTO `auth_role` VALUES ('102', 'role_user', '用户角色', '1', null, null);
INSERT INTO `auth_role` VALUES ('103', 'role_guest', '访客角色', '1', null, null);
INSERT INTO `auth_role` VALUES ('104', 'role_anon', '非角色（拥有所有权限）', '1', null, null);
INSERT INTO `auth_role` VALUES ('105', 'role_application_uiotcp_portal', '泛在物联平台门户系统', '1', null, null);

-- ----------------------------
-- Table structure for auth_role_resource
-- ----------------------------
DROP TABLE IF EXISTS `auth_role_resource`;
CREATE TABLE `auth_role_resource` (
  `ID` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `ROLE_ID` int(11) NOT NULL COMMENT '角色ID',
  `RESOURCE_ID` int(11) NOT NULL COMMENT '资源ID',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`ID`) USING BTREE,
  UNIQUE KEY `ROLE_ID` (`ROLE_ID`,`RESOURCE_ID`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=234 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='资源角色关联表';

-- ----------------------------
-- Records of auth_role_resource
-- ----------------------------
INSERT INTO `auth_role_resource` VALUES ('1', '100', '100', null, null);
INSERT INTO `auth_role_resource` VALUES ('3', '102', '100', null, null);
INSERT INTO `auth_role_resource` VALUES ('6', '101', '100', null, null);
INSERT INTO `auth_role_resource` VALUES ('9', '102', '103', null, null);
INSERT INTO `auth_role_resource` VALUES ('10', '103', '105', '2018-04-01 12:50:19', '2018-04-01 12:50:19');
INSERT INTO `auth_role_resource` VALUES ('21', '102', '102', '2018-04-09 21:09:09', '2018-04-09 21:09:09');
INSERT INTO `auth_role_resource` VALUES ('24', '103', '102', '2018-04-09 21:51:43', '2018-04-09 21:51:43');
INSERT INTO `auth_role_resource` VALUES ('25', '103', '103', '2018-04-09 21:51:46', '2018-04-09 21:51:46');
INSERT INTO `auth_role_resource` VALUES ('26', '103', '112', '2018-04-09 21:51:49', '2018-04-09 21:51:49');
INSERT INTO `auth_role_resource` VALUES ('27', '101', '112', '2018-04-09 22:21:02', '2018-04-09 22:21:02');
INSERT INTO `auth_role_resource` VALUES ('28', '101', '103', '2018-04-09 22:21:06', '2018-04-09 22:21:06');
INSERT INTO `auth_role_resource` VALUES ('29', '100', '102', '2018-04-09 22:25:09', '2018-04-09 22:25:09');
INSERT INTO `auth_role_resource` VALUES ('30', '101', '101', '2018-04-09 22:39:28', '2018-04-09 22:39:28');
INSERT INTO `auth_role_resource` VALUES ('31', '103', '100', '2018-04-09 22:45:00', '2018-04-09 22:45:00');
INSERT INTO `auth_role_resource` VALUES ('32', '103', '104', '2018-04-09 22:46:26', '2018-04-09 22:46:26');
INSERT INTO `auth_role_resource` VALUES ('33', '103', '106', '2018-04-09 22:46:28', '2018-04-09 22:46:28');
INSERT INTO `auth_role_resource` VALUES ('36', '103', '116', '2018-04-09 22:46:37', '2018-04-09 22:46:37');
INSERT INTO `auth_role_resource` VALUES ('39', '104', '102', '2018-04-09 22:49:52', '2018-04-09 22:49:52');
INSERT INTO `auth_role_resource` VALUES ('40', '104', '103', '2018-04-09 22:49:55', '2018-04-09 22:49:55');
INSERT INTO `auth_role_resource` VALUES ('41', '100', '103', '2018-04-09 22:51:56', '2018-04-09 22:51:56');
INSERT INTO `auth_role_resource` VALUES ('43', '102', '104', '2018-04-17 14:47:36', '2018-04-17 14:47:36');
INSERT INTO `auth_role_resource` VALUES ('44', '102', '106', '2018-04-17 14:47:40', '2018-04-17 14:47:40');
INSERT INTO `auth_role_resource` VALUES ('45', '102', '107', '2018-04-17 14:47:46', '2018-04-17 14:47:46');
INSERT INTO `auth_role_resource` VALUES ('46', '102', '117', '2018-04-17 14:47:50', '2018-04-17 14:47:50');
INSERT INTO `auth_role_resource` VALUES ('47', '102', '109', '2018-04-17 14:47:54', '2018-04-17 14:47:54');
INSERT INTO `auth_role_resource` VALUES ('48', '100', '112', '2018-04-25 09:58:27', '2018-04-25 09:58:27');
INSERT INTO `auth_role_resource` VALUES ('49', '100', '119', '2018-04-25 09:58:34', '2018-04-25 09:58:34');
INSERT INTO `auth_role_resource` VALUES ('50', '100', '120', '2018-04-25 09:58:38', '2018-04-25 09:58:38');
INSERT INTO `auth_role_resource` VALUES ('51', '100', '121', '2018-04-25 09:58:43', '2018-04-25 09:58:43');
INSERT INTO `auth_role_resource` VALUES ('52', '100', '122', '2018-04-25 09:58:48', '2018-04-25 09:58:48');
INSERT INTO `auth_role_resource` VALUES ('53', '100', '123', '2018-04-25 09:58:53', '2018-04-25 09:58:53');
INSERT INTO `auth_role_resource` VALUES ('54', '100', '124', '2018-04-25 09:58:57', '2018-04-25 09:58:57');
INSERT INTO `auth_role_resource` VALUES ('55', '100', '125', '2018-04-25 09:59:01', '2018-04-25 09:59:01');
INSERT INTO `auth_role_resource` VALUES ('56', '100', '126', '2018-04-25 09:59:06', '2018-04-25 09:59:06');
INSERT INTO `auth_role_resource` VALUES ('57', '100', '127', '2018-04-25 10:00:18', '2018-04-25 10:00:18');
INSERT INTO `auth_role_resource` VALUES ('58', '100', '128', '2018-04-25 10:00:22', '2018-04-25 10:00:22');
INSERT INTO `auth_role_resource` VALUES ('59', '100', '129', '2018-04-25 10:00:27', '2018-04-25 10:00:27');
INSERT INTO `auth_role_resource` VALUES ('60', '100', '130', '2018-04-25 10:00:31', '2018-04-25 10:00:31');
INSERT INTO `auth_role_resource` VALUES ('61', '100', '131', '2018-04-25 10:00:36', '2018-04-25 10:00:36');
INSERT INTO `auth_role_resource` VALUES ('62', '100', '133', '2018-04-25 10:00:43', '2018-04-25 10:00:43');
INSERT INTO `auth_role_resource` VALUES ('66', '102', '121', '2018-04-25 11:35:02', '2018-04-25 11:35:02');
INSERT INTO `auth_role_resource` VALUES ('67', '102', '122', '2018-04-25 11:35:07', '2018-04-25 11:35:07');
INSERT INTO `auth_role_resource` VALUES ('68', '102', '123', '2018-04-25 11:35:12', '2018-04-25 11:35:12');
INSERT INTO `auth_role_resource` VALUES ('69', '102', '124', '2018-04-25 11:35:17', '2018-04-25 11:35:17');
INSERT INTO `auth_role_resource` VALUES ('70', '102', '139', '2018-04-25 11:35:30', '2018-04-25 11:35:30');
INSERT INTO `auth_role_resource` VALUES ('71', '102', '140', '2018-04-25 11:35:35', '2018-04-25 11:35:35');
INSERT INTO `auth_role_resource` VALUES ('72', '102', '136', '2018-04-25 11:35:49', '2018-04-25 11:35:49');
INSERT INTO `auth_role_resource` VALUES ('73', '100', '134', '2018-04-25 11:36:16', '2018-04-25 11:36:16');
INSERT INTO `auth_role_resource` VALUES ('74', '100', '135', '2018-04-25 11:36:21', '2018-04-25 11:36:21');
INSERT INTO `auth_role_resource` VALUES ('75', '100', '136', '2018-04-25 11:36:25', '2018-04-25 11:36:25');
INSERT INTO `auth_role_resource` VALUES ('76', '100', '137', '2018-04-25 11:36:32', '2018-04-25 11:36:32');
INSERT INTO `auth_role_resource` VALUES ('77', '100', '138', '2018-04-25 11:36:36', '2018-04-25 11:36:36');
INSERT INTO `auth_role_resource` VALUES ('78', '100', '139', '2018-04-25 11:36:41', '2018-04-25 11:36:41');
INSERT INTO `auth_role_resource` VALUES ('79', '100', '140', '2018-04-25 11:36:45', '2018-04-25 11:36:45');
INSERT INTO `auth_role_resource` VALUES ('80', '100', '141', '2018-04-25 11:36:50', '2018-04-25 11:36:50');
INSERT INTO `auth_role_resource` VALUES ('81', '100', '142', '2018-04-25 11:36:54', '2018-04-25 11:36:54');
INSERT INTO `auth_role_resource` VALUES ('82', '100', '143', '2018-04-25 11:36:58', '2018-04-25 11:36:58');
INSERT INTO `auth_role_resource` VALUES ('83', '100', '144', '2018-04-25 11:37:02', '2018-04-25 11:37:02');
INSERT INTO `auth_role_resource` VALUES ('84', '100', '104', '2018-04-25 12:22:01', '2018-04-25 12:22:01');
INSERT INTO `auth_role_resource` VALUES ('85', '100', '106', '2018-04-25 12:22:06', '2018-04-25 12:22:06');
INSERT INTO `auth_role_resource` VALUES ('86', '100', '107', '2018-04-25 12:22:10', '2018-04-25 12:22:10');
INSERT INTO `auth_role_resource` VALUES ('87', '100', '109', '2018-04-25 12:22:15', '2018-04-25 12:22:15');
INSERT INTO `auth_role_resource` VALUES ('88', '100', '117', '2018-04-25 12:22:19', '2018-04-25 12:22:19');
INSERT INTO `auth_role_resource` VALUES ('89', '100', '146', '2018-04-25 12:22:22', '2018-04-25 12:22:22');
INSERT INTO `auth_role_resource` VALUES ('90', '102', '146', '2018-04-25 18:55:27', '2018-04-25 18:55:27');
INSERT INTO `auth_role_resource` VALUES ('92', '102', '125', '2018-04-25 18:55:50', '2018-04-25 18:55:50');
INSERT INTO `auth_role_resource` VALUES ('93', '102', '126', '2018-04-25 18:55:54', '2018-04-25 18:55:54');
INSERT INTO `auth_role_resource` VALUES ('94', '102', '127', '2018-04-25 18:55:59', '2018-04-25 18:55:59');
INSERT INTO `auth_role_resource` VALUES ('95', '102', '128', '2018-04-25 18:56:03', '2018-04-25 18:56:03');
INSERT INTO `auth_role_resource` VALUES ('97', '102', '130', '2018-04-25 18:56:12', '2018-04-25 18:56:12');
INSERT INTO `auth_role_resource` VALUES ('98', '102', '131', '2018-04-25 18:56:16', '2018-04-25 18:56:16');
INSERT INTO `auth_role_resource` VALUES ('99', '103', '119', '2018-04-25 18:56:47', '2018-04-25 18:56:47');
INSERT INTO `auth_role_resource` VALUES ('100', '103', '121', '2018-04-25 18:56:51', '2018-04-25 18:56:51');
INSERT INTO `auth_role_resource` VALUES ('101', '103', '122', '2018-04-25 18:56:55', '2018-04-25 18:56:55');
INSERT INTO `auth_role_resource` VALUES ('102', '103', '120', '2018-04-25 18:57:00', '2018-04-25 18:57:00');
INSERT INTO `auth_role_resource` VALUES ('104', '102', '129', '2018-04-25 18:57:35', '2018-04-25 18:57:35');
INSERT INTO `auth_role_resource` VALUES ('105', '102', '135', '2018-04-25 18:57:43', '2018-04-25 18:57:43');
INSERT INTO `auth_role_resource` VALUES ('106', '102', '133', '2018-04-25 18:57:47', '2018-04-25 18:57:47');
INSERT INTO `auth_role_resource` VALUES ('107', '102', '134', '2018-04-25 18:57:51', '2018-04-25 18:57:51');
INSERT INTO `auth_role_resource` VALUES ('108', '102', '137', '2018-04-25 18:57:55', '2018-04-25 18:57:55');
INSERT INTO `auth_role_resource` VALUES ('109', '102', '138', '2018-04-25 18:57:59', '2018-04-25 18:57:59');
INSERT INTO `auth_role_resource` VALUES ('110', '102', '141', '2018-04-25 18:58:03', '2018-04-25 18:58:03');
INSERT INTO `auth_role_resource` VALUES ('111', '102', '142', '2018-04-25 18:58:07', '2018-04-25 18:58:07');
INSERT INTO `auth_role_resource` VALUES ('112', '102', '143', '2018-04-25 18:58:11', '2018-04-25 18:58:11');
INSERT INTO `auth_role_resource` VALUES ('113', '102', '144', '2018-04-25 18:58:14', '2018-04-25 18:58:14');
INSERT INTO `auth_role_resource` VALUES ('114', '103', '123', '2018-04-25 18:58:20', '2018-04-25 18:58:20');
INSERT INTO `auth_role_resource` VALUES ('115', '103', '124', '2018-04-25 18:58:24', '2018-04-25 18:58:24');
INSERT INTO `auth_role_resource` VALUES ('116', '103', '125', '2018-04-25 18:58:29', '2018-04-25 18:58:29');
INSERT INTO `auth_role_resource` VALUES ('119', '103', '128', '2018-04-25 18:58:42', '2018-04-25 18:58:42');
INSERT INTO `auth_role_resource` VALUES ('120', '103', '129', '2018-04-25 18:58:46', '2018-04-25 18:58:46');
INSERT INTO `auth_role_resource` VALUES ('121', '103', '130', '2018-04-25 18:58:51', '2018-04-25 18:58:51');
INSERT INTO `auth_role_resource` VALUES ('123', '103', '133', '2018-04-25 18:59:00', '2018-04-25 18:59:00');
INSERT INTO `auth_role_resource` VALUES ('124', '103', '134', '2018-04-25 18:59:04', '2018-04-25 18:59:04');
INSERT INTO `auth_role_resource` VALUES ('125', '103', '135', '2018-04-25 18:59:08', '2018-04-25 18:59:08');
INSERT INTO `auth_role_resource` VALUES ('126', '103', '136', '2018-04-25 18:59:12', '2018-04-25 18:59:12');
INSERT INTO `auth_role_resource` VALUES ('127', '103', '137', '2018-04-25 18:59:16', '2018-04-25 18:59:16');
INSERT INTO `auth_role_resource` VALUES ('128', '103', '138', '2018-04-25 18:59:21', '2018-04-25 18:59:21');
INSERT INTO `auth_role_resource` VALUES ('129', '103', '139', '2018-04-25 18:59:24', '2018-04-25 18:59:24');
INSERT INTO `auth_role_resource` VALUES ('131', '103', '141', '2018-04-25 18:59:33', '2018-04-25 18:59:33');
INSERT INTO `auth_role_resource` VALUES ('132', '103', '142', '2018-04-25 18:59:37', '2018-04-25 18:59:37');
INSERT INTO `auth_role_resource` VALUES ('133', '103', '143', '2018-04-25 18:59:41', '2018-04-25 18:59:41');
INSERT INTO `auth_role_resource` VALUES ('135', '100', '148', '2018-04-26 16:05:43', '2018-04-26 16:05:43');
INSERT INTO `auth_role_resource` VALUES ('136', '100', '147', '2018-04-26 17:26:55', '2018-04-26 17:26:55');
INSERT INTO `auth_role_resource` VALUES ('139', '103', '150', '2018-04-26 23:12:29', '2018-04-26 23:12:29');
INSERT INTO `auth_role_resource` VALUES ('141', '102', '151', '2018-04-27 12:45:59', '2018-04-27 12:45:59');
INSERT INTO `auth_role_resource` VALUES ('142', '102', '149', '2018-04-27 12:46:04', '2018-04-27 12:46:04');
INSERT INTO `auth_role_resource` VALUES ('143', '103', '126', '2018-05-22 19:59:16', '2018-05-22 19:59:16');
INSERT INTO `auth_role_resource` VALUES ('144', '103', '127', '2018-05-22 19:59:21', '2018-05-22 19:59:21');
INSERT INTO `auth_role_resource` VALUES ('146', '102', '119', '2018-05-22 19:59:31', '2018-05-22 19:59:31');
INSERT INTO `auth_role_resource` VALUES ('147', '102', '120', '2018-05-22 19:59:35', '2018-05-22 19:59:35');
INSERT INTO `auth_role_resource` VALUES ('148', '102', '101', '2018-05-22 19:59:39', '2018-05-22 19:59:39');
INSERT INTO `auth_role_resource` VALUES ('149', '100', '101', '2018-05-23 17:08:33', '2018-05-23 17:08:33');
INSERT INTO `auth_role_resource` VALUES ('150', '106', '117', '2018-05-24 09:52:50', '2018-05-24 09:52:50');
INSERT INTO `auth_role_resource` VALUES ('153', '104', '154', '2018-05-24 11:29:36', '2018-05-24 11:29:36');
INSERT INTO `auth_role_resource` VALUES ('155', '100', '153', '2018-05-24 12:47:53', '2018-05-24 12:47:53');
INSERT INTO `auth_role_resource` VALUES ('156', '100', '155', '2018-05-24 16:26:33', '2018-05-24 16:26:33');
INSERT INTO `auth_role_resource` VALUES ('157', '103', '152', '2018-05-24 17:01:42', '2018-05-24 17:01:42');
INSERT INTO `auth_role_resource` VALUES ('158', '103', '101', '2018-05-24 17:02:15', '2018-05-24 17:02:15');
INSERT INTO `auth_role_resource` VALUES ('159', '102', '156', '2018-05-24 17:38:16', '2018-05-24 17:38:16');
INSERT INTO `auth_role_resource` VALUES ('160', '100', '150', '2018-05-24 22:20:00', '2018-05-24 22:20:00');
INSERT INTO `auth_role_resource` VALUES ('161', '102', '147', '2018-05-31 10:16:45', '2018-05-31 10:16:45');
INSERT INTO `auth_role_resource` VALUES ('162', '102', '150', '2018-05-31 10:16:49', '2018-05-31 10:16:49');
INSERT INTO `auth_role_resource` VALUES ('163', '102', '157', '2018-05-31 10:16:54', '2018-05-31 10:16:54');
INSERT INTO `auth_role_resource` VALUES ('164', '102', '158', '2018-05-31 10:16:58', '2018-05-31 10:16:58');
INSERT INTO `auth_role_resource` VALUES ('165', '103', '157', '2018-05-31 10:18:05', '2018-05-31 10:18:05');
INSERT INTO `auth_role_resource` VALUES ('166', '104', '156', '2018-05-31 10:19:00', '2018-05-31 10:19:00');
INSERT INTO `auth_role_resource` VALUES ('169', '104', '155', '2018-05-31 17:14:28', '2018-05-31 17:14:28');
INSERT INTO `auth_role_resource` VALUES ('170', '110', '101', '2018-06-01 10:52:20', '2018-06-01 10:52:20');
INSERT INTO `auth_role_resource` VALUES ('171', '110', '128', '2018-06-01 10:52:32', '2018-06-01 10:52:32');
INSERT INTO `auth_role_resource` VALUES ('172', '100', '163', '2018-06-01 10:55:56', '2018-06-01 10:55:56');
INSERT INTO `auth_role_resource` VALUES ('173', '102', '163', '2018-06-01 10:56:19', '2018-06-01 10:56:19');
INSERT INTO `auth_role_resource` VALUES ('175', '104', '163', '2018-06-01 10:56:41', '2018-06-01 10:56:41');
INSERT INTO `auth_role_resource` VALUES ('176', '109', '163', '2018-06-01 10:56:48', '2018-06-01 10:56:48');
INSERT INTO `auth_role_resource` VALUES ('177', '110', '163', '2018-06-01 10:56:54', '2018-06-01 10:56:54');
INSERT INTO `auth_role_resource` VALUES ('178', '102', '162', '2018-06-01 11:09:41', '2018-06-01 11:09:41');
INSERT INTO `auth_role_resource` VALUES ('179', '102', '161', '2018-06-01 11:09:46', '2018-06-01 11:09:46');
INSERT INTO `auth_role_resource` VALUES ('180', '110', '106', '2018-06-01 17:08:03', '2018-06-01 17:08:03');
INSERT INTO `auth_role_resource` VALUES ('181', '110', '160', '2018-06-01 17:08:15', '2018-06-01 17:08:15');
INSERT INTO `auth_role_resource` VALUES ('182', '102', '164', '2018-06-01 18:34:44', '2018-06-01 18:34:44');
INSERT INTO `auth_role_resource` VALUES ('184', '104', '101', '2018-06-01 18:35:25', '2018-06-01 18:35:25');
INSERT INTO `auth_role_resource` VALUES ('185', '103', '156', '2018-06-02 01:09:37', '2018-06-02 01:09:37');
INSERT INTO `auth_role_resource` VALUES ('186', '110', '146', '2018-06-02 01:09:48', '2018-06-02 01:09:48');
INSERT INTO `auth_role_resource` VALUES ('187', '100', '164', '2018-06-02 10:22:06', '2018-06-02 10:22:06');
INSERT INTO `auth_role_resource` VALUES ('188', '109', '141', '2018-06-03 06:45:46', '2018-06-03 06:45:46');
INSERT INTO `auth_role_resource` VALUES ('189', '102', '166', '2018-07-10 08:52:53', '2018-07-10 08:52:53');
INSERT INTO `auth_role_resource` VALUES ('190', '109', '125', '2018-07-10 13:50:03', '2018-07-10 13:50:03');
INSERT INTO `auth_role_resource` VALUES ('191', '100', '162', '2018-07-10 22:41:41', '2018-07-10 22:41:41');
INSERT INTO `auth_role_resource` VALUES ('192', '111', '109', '2018-07-10 22:42:42', '2018-07-10 22:42:42');
INSERT INTO `auth_role_resource` VALUES ('194', '103', '140', '2018-07-11 17:40:50', '2018-07-11 17:40:50');
INSERT INTO `auth_role_resource` VALUES ('196', '103', '131', '2018-10-15 21:55:22', '2018-10-15 21:55:22');
INSERT INTO `auth_role_resource` VALUES ('197', '100', '149', '2018-10-16 16:25:08', '2018-10-16 16:25:08');
INSERT INTO `auth_role_resource` VALUES ('199', '102', '155', '2018-10-17 10:44:46', '2018-10-17 10:44:46');
INSERT INTO `auth_role_resource` VALUES ('200', '100', '168', '2018-11-12 14:14:52', '2018-11-12 14:14:52');
INSERT INTO `auth_role_resource` VALUES ('201', '100', '167', '2018-11-12 14:19:35', '2018-11-12 14:19:35');
INSERT INTO `auth_role_resource` VALUES ('202', '100', '169', '2018-11-12 14:21:29', '2018-11-12 14:21:29');
INSERT INTO `auth_role_resource` VALUES ('203', '102', '167', '2018-11-19 14:45:31', '2018-11-19 14:45:31');
INSERT INTO `auth_role_resource` VALUES ('204', '102', '170', '2018-11-20 12:51:56', '2018-11-20 12:51:56');
INSERT INTO `auth_role_resource` VALUES ('210', '103', '149', '2018-11-20 23:18:54', '2018-11-20 23:18:54');
INSERT INTO `auth_role_resource` VALUES ('211', '103', '151', '2018-11-20 23:18:59', '2018-11-20 23:18:59');
INSERT INTO `auth_role_resource` VALUES ('212', '103', '171', '2018-11-20 23:19:06', '2018-11-20 23:19:06');
INSERT INTO `auth_role_resource` VALUES ('213', '103', '172', '2018-11-20 23:19:13', '2018-11-20 23:19:13');
INSERT INTO `auth_role_resource` VALUES ('214', '103', '173', '2018-11-20 23:19:25', '2018-11-20 23:19:25');
INSERT INTO `auth_role_resource` VALUES ('215', '103', '176', '2018-11-20 23:19:31', '2018-11-20 23:19:31');
INSERT INTO `auth_role_resource` VALUES ('216', '103', '175', '2018-11-20 23:19:37', '2018-11-20 23:19:37');
INSERT INTO `auth_role_resource` VALUES ('217', '103', '174', '2018-11-20 23:19:42', '2018-11-20 23:19:42');
INSERT INTO `auth_role_resource` VALUES ('218', '103', '107', '2018-12-04 09:23:35', '2018-12-04 09:23:35');
INSERT INTO `auth_role_resource` VALUES ('219', '103', '144', '2018-12-04 09:24:04', '2018-12-04 09:24:04');
INSERT INTO `auth_role_resource` VALUES ('220', '102', '171', '2018-12-04 11:14:56', '2018-12-04 11:14:56');
INSERT INTO `auth_role_resource` VALUES ('221', '104', '142', '2018-12-04 15:11:07', '2018-12-04 15:11:07');
INSERT INTO `auth_role_resource` VALUES ('222', '104', '143', '2018-12-04 15:11:13', '2018-12-04 15:11:13');
INSERT INTO `auth_role_resource` VALUES ('223', '102', '173', '2018-12-05 08:44:10', '2018-12-05 08:44:10');
INSERT INTO `auth_role_resource` VALUES ('224', '113', '120', '2018-12-05 10:38:16', '2018-12-05 10:38:16');
INSERT INTO `auth_role_resource` VALUES ('225', '113', '128', '2018-12-05 10:38:20', '2018-12-05 10:38:20');
INSERT INTO `auth_role_resource` VALUES ('226', '102', '154', '2019-02-18 03:14:01', '2019-02-18 03:14:01');
INSERT INTO `auth_role_resource` VALUES ('227', '102', '179', '2019-02-20 05:51:05', '2019-02-20 05:51:05');
INSERT INTO `auth_role_resource` VALUES ('228', '102', '178', '2019-02-20 07:52:28', '2019-02-20 07:52:28');
INSERT INTO `auth_role_resource` VALUES ('229', '102', '180', '2019-02-20 07:56:28', '2019-02-20 07:56:28');
INSERT INTO `auth_role_resource` VALUES ('230', '105', '101', '2019-10-26 16:20:25', '2019-10-26 16:20:25');
INSERT INTO `auth_role_resource` VALUES ('231', '105', '119', '2019-10-26 16:21:20', '2019-10-26 16:21:20');
INSERT INTO `auth_role_resource` VALUES ('232', '105', '112', '2019-10-26 16:22:45', '2019-10-26 16:22:45');
INSERT INTO `auth_role_resource` VALUES ('233', '105', '145', '2019-10-26 19:32:32', '2019-10-26 19:32:32');

-- ----------------------------
-- Table structure for auth_user
-- ----------------------------
DROP TABLE IF EXISTS `auth_user`;
CREATE TABLE `auth_user` (
  `uid` varchar(30) NOT NULL COMMENT 'uid,用户账号,主键',
  `username` varchar(30) NOT NULL COMMENT '用户名(nick_name)',
  `password` varchar(50) NOT NULL COMMENT '密码(MD5(密码+盐))',
  `salt` varchar(20) DEFAULT NULL COMMENT '盐',
  `real_name` varchar(30) DEFAULT NULL COMMENT '用户真名',
  `avatar` varchar(100) DEFAULT NULL COMMENT '头像',
  `phone` varchar(20) DEFAULT NULL COMMENT '电话号码(唯一)',
  `email` varchar(50) DEFAULT NULL COMMENT '邮件地址(唯一)',
  `sex` tinyint(4) DEFAULT NULL COMMENT '性别(1.男 2.女)',
  `status` tinyint(4) DEFAULT NULL COMMENT '账户状态(1.正常 2.锁定 3.删除 4.非法)',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `CREATE_WHERE` tinyint(4) DEFAULT NULL COMMENT '创建来源(1.web 2.android 3.ios 4.win 5.macos 6.ubuntu)',
  PRIMARY KEY (`uid`) USING BTREE,
  UNIQUE KEY `phone` (`phone`) USING BTREE,
  UNIQUE KEY `email` (`email`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='用户信息表';

-- ----------------------------
-- Records of auth_user
-- ----------------------------
INSERT INTO `auth_user` VALUES ('1', '1', '55EC49B5AEF567AFDDC25D322E9FD644', '79sz6j', null, null, null, null, null, '1', '2018-04-26 19:21:04', '2018-04-26 11:21:04', null);
INSERT INTO `auth_user` VALUES ('admin', '超级管理员', '181715F2A54ED81DD2826DC3F6E7F848', 'gfkqj9', null, null, null, null, null, '1', '2019-10-26 15:20:21', null, null);

-- ----------------------------
-- Table structure for auth_user_role
-- ----------------------------
DROP TABLE IF EXISTS `auth_user_role`;
CREATE TABLE `auth_user_role` (
  `ID` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '用户角色关联表主键',
  `USER_ID` varchar(30) NOT NULL COMMENT '用户UID',
  `ROLE_ID` int(11) NOT NULL COMMENT '角色ID',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`ID`) USING BTREE,
  UNIQUE KEY `USER_ID` (`USER_ID`,`ROLE_ID`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='用户角色关联表';

-- ----------------------------
-- Records of auth_user_role
-- ----------------------------
INSERT INTO `auth_user_role` VALUES ('15', '282870345', '103', '2018-04-09 22:44:47', '2018-04-09 22:44:47');
INSERT INTO `auth_user_role` VALUES ('39', 'admin', '100', '2019-10-26 15:20:21', '2019-10-26 15:20:21');
INSERT INTO `auth_user_role` VALUES ('40', '1', '105', '2019-10-26 18:57:46', '2019-10-26 18:57:46');
