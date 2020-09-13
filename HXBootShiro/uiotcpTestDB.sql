/*
Navicat MariaDB Data Transfer

Source Server Version : 50564
Source Database       : uiotcpTestDB

Target Server Type    : MariaDB
Target Server Version : 50564
File Encoding         : 65001

Date: 2020-04-02 16:54:23
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
) ENGINE=InnoDB AUTO_INCREMENT=729 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='登录注册登出记录';

-- ----------------------------
-- Records of auth_account_log
-- ----------------------------

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='操作日志';

-- ----------------------------
-- Records of auth_operation_log
-- ----------------------------

-- ----------------------------
-- Table structure for auth_resource
-- ----------------------------
DROP TABLE IF EXISTS `auth_resource`;
CREATE TABLE `auth_resource` (
  `ID` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '资源ID',
  `CODE` varchar(30) DEFAULT NULL COMMENT '资源名称',
  `NAME` varchar(50) DEFAULT NULL COMMENT '资源描述',
  `PARENT_ID` int(11) DEFAULT NULL COMMENT '父资源编码->菜单',
  `URI` varchar(100) DEFAULT NULL COMMENT '访问地址URL',
  `TYPE` smallint(4) DEFAULT NULL COMMENT '类型 1:菜单menu 2:资源element(rest-api) 3:资源分类',
  `METHOD` varchar(10) DEFAULT NULL COMMENT '访问方式 GET POST PUT DELETE PATCH',
  `NEED_AUTH` tinyint(4) DEFAULT NULL COMMENT '网关是否鉴权',
  `ROUTE_INFO` text COMMENT '路由信息（json串）',
  `ICON` varchar(100) DEFAULT NULL COMMENT '图标',
  `STATUS` smallint(4) DEFAULT '1' COMMENT '状态   1:正常、9：禁用',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=172 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='资源信息表(菜单,资源)';

-- ----------------------------
-- Records of auth_resource
-- ----------------------------
INSERT INTO `auth_resource` VALUES ('101', 'ACCOUNT_LOGIN', '用户登录（/account/*所有路径默认所有用户可以访问，不用给角色分配）', '103', '/account/login', '2', 'POST', null, null, null, '1', '2020-04-02 15:10:03', null);
INSERT INTO `auth_resource` VALUES ('103', 'GROUP_ACCOUNT', '账户系列', '110', '', '3', 'POST', null, null, null, '1', '2020-04-02 15:10:03', null);
INSERT INTO `auth_resource` VALUES ('104', 'USER_MAGE', '用户管理', '-1', '', '1', 'POST', null, null, 'fa fa-user', '1', '2020-04-02 15:10:03', null);
INSERT INTO `auth_resource` VALUES ('106', 'RESOURCE_MAGE', '资源配置', '-1', '', '1', 'POST', null, null, 'fa fa-pie-chart', '1', '2020-04-02 15:10:03', null);
INSERT INTO `auth_resource` VALUES ('107', 'MENU_MANAGE', '菜单管理', '106', '/index/menu', '1', 'POST', null, null, 'fa fa-th', '1', '2020-04-02 15:10:03', null);
INSERT INTO `auth_resource` VALUES ('109', 'API_MANAGE', 'API管理', '106', '/index/api', '1', null, null, null, 'fa fa-share', '1', '2018-04-07 09:40:24', '2018-04-07 09:40:24');
INSERT INTO `auth_resource` VALUES ('110', 'CATEGORY_GROUP', '分类集合(API类别请放入此集合)', '-1', null, '3', null, null, null, null, '1', '2018-04-07 14:27:58', '2018-04-07 14:27:58');
INSERT INTO `auth_resource` VALUES ('112', 'ACCOUNT_REGISTER', '用户注册（/account/*所有路径默认所有用户可以访问，不用给角色分配）', '103', '/account/register', '2', 'POST', null, null, null, '1', '2018-04-07 16:21:45', '2018-04-07 16:21:45');
INSERT INTO `auth_resource` VALUES ('115', 'GROUP_USER', '用户系列', '110', '', '3', 'GET', null, null, null, '1', '2018-04-07 16:31:01', '2018-04-07 16:31:01');
INSERT INTO `auth_resource` VALUES ('117', 'ROLE_MANAGE', '角色/应用管理', '106', '/index/role', '1', null, null, null, 'fa fa-adjust', '1', '2018-04-08 05:36:31', '2018-04-08 05:36:31');
INSERT INTO `auth_resource` VALUES ('118', 'GROUP_RESOURCE', '资源系列', '110', null, '3', null, null, null, null, '1', '2018-04-09 02:29:14', '2018-04-09 02:29:14');
INSERT INTO `auth_resource` VALUES ('119', 'USER_ROLE_APPID', '获取对应用户角色', '115', '/user/role/*', '2', 'GET', null, null, null, '1', '2018-04-12 03:07:22', '2018-04-12 03:07:22');
INSERT INTO `auth_resource` VALUES ('120', 'USER_LIST', '获取用户列表', '115', '/user/list/*/*', '2', 'GET', null, null, null, '1', '2018-04-12 03:08:30', '2018-04-12 03:08:30');
INSERT INTO `auth_resource` VALUES ('121', 'USER_AUTHORITY_ROLE', '给用户授权添加角色', '115', '/user/authority/role', '2', 'POST', null, null, null, '1', '2018-04-12 03:15:56', '2018-04-12 03:15:56');
INSERT INTO `auth_resource` VALUES ('122', 'USER_AUTHORITY_ROLE', '删除已经授权的用户角色', '115', '/user/authority/role', '2', 'DELETE', null, null, null, '1', '2018-04-12 03:29:03', '2018-04-12 03:29:03');
INSERT INTO `auth_resource` VALUES ('123', 'RESOURCE_AUTORITYMENU', '获取用户被授权菜单', '118', '/resource/authorityMenu', '2', 'GET', null, null, null, '1', '2018-04-12 05:30:03', '2018-04-12 05:30:03');
INSERT INTO `auth_resource` VALUES ('124', 'RESOURCE_MENUS', '获取全部菜单列', '118', '/resource/menus', '2', 'GET', null, null, null, '1', '2018-04-12 05:42:46', '2018-04-12 05:42:46');
INSERT INTO `auth_resource` VALUES ('125', 'RESOURCE_MENU', '增加菜单', '118', '/resource/menu', '2', 'POST', null, null, null, '1', '2018-04-12 06:15:39', '2018-04-12 06:15:39');
INSERT INTO `auth_resource` VALUES ('126', 'RESOURCE_MENU', '修改菜单', '118', '/resource/menu', '2', 'PUT', null, null, null, '1', '2018-04-12 06:16:35', '2018-04-12 06:16:35');
INSERT INTO `auth_resource` VALUES ('127', 'RESOURCE_MENU', '删除菜单', '118', '/resource/menu', '2', 'DELETE', null, null, null, '1', '2018-04-12 06:17:18', '2018-04-12 06:17:18');
INSERT INTO `auth_resource` VALUES ('128', 'RESOURCE_API', '获取API list', '118', '/resource/api/*/*/*', '2', 'GET', null, null, null, '1', '2018-04-12 06:18:02', '2018-04-12 06:18:02');
INSERT INTO `auth_resource` VALUES ('129', 'RESOURCE_API', '增加API', '118', '/resource/api', '2', 'POST', null, null, null, '1', '2018-04-12 06:18:42', '2018-04-12 06:18:42');
INSERT INTO `auth_resource` VALUES ('130', 'RESOURCE_API', '修改API', '118', '/resource/api', '2', 'PUT', null, null, null, '1', '2018-04-12 06:19:32', '2018-04-12 06:19:32');
INSERT INTO `auth_resource` VALUES ('131', 'RESOURCE_API', '删除API', '118', '/resource/api', '2', 'DELETE', null, null, null, '1', '2018-04-12 06:20:03', '2018-04-12 06:20:03');
INSERT INTO `auth_resource` VALUES ('132', 'GROUP_ROLE', '角色系列', '110', null, '3', null, null, null, null, '1', '2018-04-12 06:22:02', '2018-04-12 06:22:02');
INSERT INTO `auth_resource` VALUES ('133', 'ROLE_USER', '获取角色关联用户列表', '132', '/role/user/*/*/*', '2', 'GET', null, null, null, '1', '2018-04-12 06:22:59', '2018-04-12 06:22:59');
INSERT INTO `auth_resource` VALUES ('134', 'ROLE_USER', '获取角色未关联用户列表', '132', '/role/user/-/*/*/*', '2', 'GET', null, null, null, '1', '2018-04-12 06:24:09', '2018-04-12 06:24:09');
INSERT INTO `auth_resource` VALUES ('135', 'ROLE_API', '获取角色关联API资源', '132', '/role/api/*/*/*', '2', 'GET', null, null, null, '1', '2018-04-12 06:25:32', '2018-04-12 06:25:32');
INSERT INTO `auth_resource` VALUES ('136', 'ROLE_API', '获取角色未关联API资源', '132', '/role/api/-/*/*/*', '2', 'GET', null, null, null, '1', '2018-04-12 06:26:12', '2018-04-12 06:26:12');
INSERT INTO `auth_resource` VALUES ('137', 'ROLE_MENU', '获取角色关联菜单资源', '132', '/role/menu/*/*/*', '2', 'GET', null, null, null, '1', '2018-04-12 06:27:20', '2018-04-12 06:27:20');
INSERT INTO `auth_resource` VALUES ('138', 'ROLE_MENU', '获取角色未关联菜单资源', '132', '/role/menu/-/*/*/*', '2', 'GET', null, null, null, '1', '2018-04-12 06:27:56', '2018-04-12 06:27:56');
INSERT INTO `auth_resource` VALUES ('139', 'ROLE_AUTHORITY_RESOURCE', '授权资源给角色', '132', '/role/authority/resource', '2', 'POST', null, null, null, '1', '2018-04-12 06:29:45', '2018-04-12 06:29:45');
INSERT INTO `auth_resource` VALUES ('140', 'ROLE_AUTHORITY_RESOURCE', '删除角色的授权资源', '132', '/role/authority/resource', '2', 'DELETE', null, null, null, '1', '2018-04-12 06:31:12', '2018-04-12 06:31:12');
INSERT INTO `auth_resource` VALUES ('141', 'ROLE', '获取角色LIST', '132', '/role/*/*', '2', 'GET', null, null, null, '1', '2018-04-12 06:32:34', '2018-04-12 06:32:34');
INSERT INTO `auth_resource` VALUES ('142', 'ROLE', '添加角色', '132', '/role', '2', 'POST', null, null, null, '1', '2018-04-12 06:33:25', '2018-04-12 06:33:25');
INSERT INTO `auth_resource` VALUES ('143', 'USER', '更新角色', '132', '/role', '2', 'PUT', null, null, null, '1', '2018-04-12 06:34:27', '2018-04-12 06:34:27');
INSERT INTO `auth_resource` VALUES ('144', 'ROLE', '删除角色', '132', '/role', '2', 'DELETE', null, null, null, '1', '2018-04-12 06:35:15', '2018-04-12 06:35:15');
INSERT INTO `auth_resource` VALUES ('145', 'LOG_WATCH', '日志记录', '104', '/index/log', '1', null, null, null, 'fa fa-rss-square', '1', '2018-04-22 08:12:24', '2018-04-22 08:12:24');
INSERT INTO `auth_resource` VALUES ('146', 'USER_AUTHORIZE', '用户授权', '104', 'user/authorize', '1', null, null, null, 'fa fa-wrench', '9', '2020-04-02 15:09:52', null);
INSERT INTO `auth_resource` VALUES ('167', 'PASSWORD_UPDATE', '密码修改(/user/accountupdate)', '103', '/user/accountupdate', '2', 'PUT', null, null, null, '1', '2020-04-02 15:10:03', null);

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
) ENGINE=InnoDB AUTO_INCREMENT=105 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='角色信息表';

-- ----------------------------
-- Records of auth_role
-- ----------------------------
INSERT INTO `auth_role` VALUES ('100', 'role_admin', '管理员角色', '1', null, null);
INSERT INTO `auth_role` VALUES ('102', 'role_user', '用户角色', '1', null, null);
INSERT INTO `auth_role` VALUES ('103', 'role_guest', '访客角色（只有查询权限）', '1', null, null);
INSERT INTO `auth_role` VALUES ('104', 'role_anon', '非角色（不需要任何权限就可访问）', '1', null, null);

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
) ENGINE=InnoDB AUTO_INCREMENT=263 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='资源角色关联表';

-- ----------------------------
-- Records of auth_role_resource
-- ----------------------------
INSERT INTO `auth_role_resource` VALUES ('9', '102', '103', '2020-04-02 15:00:11', null);
INSERT INTO `auth_role_resource` VALUES ('25', '103', '103', '2018-04-09 21:51:46', '2018-04-09 21:51:46');
INSERT INTO `auth_role_resource` VALUES ('27', '101', '112', '2018-04-09 22:21:02', '2018-04-09 22:21:02');
INSERT INTO `auth_role_resource` VALUES ('28', '101', '103', '2018-04-09 22:21:06', '2018-04-09 22:21:06');
INSERT INTO `auth_role_resource` VALUES ('30', '101', '101', '2018-04-09 22:39:28', '2018-04-09 22:39:28');
INSERT INTO `auth_role_resource` VALUES ('32', '103', '104', '2018-04-09 22:46:26', '2018-04-09 22:46:26');
INSERT INTO `auth_role_resource` VALUES ('33', '103', '106', '2018-04-09 22:46:28', '2018-04-09 22:46:28');
INSERT INTO `auth_role_resource` VALUES ('40', '104', '103', '2018-04-09 22:49:55', '2018-04-09 22:49:55');
INSERT INTO `auth_role_resource` VALUES ('41', '100', '103', '2018-04-09 22:51:56', '2018-04-09 22:51:56');
INSERT INTO `auth_role_resource` VALUES ('43', '102', '104', '2018-04-17 14:47:36', '2018-04-17 14:47:36');
INSERT INTO `auth_role_resource` VALUES ('44', '102', '106', '2018-04-17 14:47:40', '2018-04-17 14:47:40');
INSERT INTO `auth_role_resource` VALUES ('45', '102', '107', '2018-04-17 14:47:46', '2018-04-17 14:47:46');
INSERT INTO `auth_role_resource` VALUES ('46', '102', '117', '2018-04-17 14:47:50', '2018-04-17 14:47:50');
INSERT INTO `auth_role_resource` VALUES ('47', '102', '109', '2018-04-17 14:47:54', '2018-04-17 14:47:54');
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
INSERT INTO `auth_role_resource` VALUES ('119', '103', '128', '2018-04-25 18:58:42', '2018-04-25 18:58:42');
INSERT INTO `auth_role_resource` VALUES ('123', '103', '133', '2018-04-25 18:59:00', '2018-04-25 18:59:00');
INSERT INTO `auth_role_resource` VALUES ('124', '103', '134', '2018-04-25 18:59:04', '2018-04-25 18:59:04');
INSERT INTO `auth_role_resource` VALUES ('125', '103', '135', '2018-04-25 18:59:08', '2018-04-25 18:59:08');
INSERT INTO `auth_role_resource` VALUES ('126', '103', '136', '2018-04-25 18:59:12', '2018-04-25 18:59:12');
INSERT INTO `auth_role_resource` VALUES ('127', '103', '137', '2018-04-25 18:59:16', '2018-04-25 18:59:16');
INSERT INTO `auth_role_resource` VALUES ('128', '103', '138', '2018-04-25 18:59:21', '2018-04-25 18:59:21');
INSERT INTO `auth_role_resource` VALUES ('131', '103', '141', '2018-04-25 18:59:33', '2018-04-25 18:59:33');
INSERT INTO `auth_role_resource` VALUES ('135', '100', '148', '2018-04-26 16:05:43', '2018-04-26 16:05:43');
INSERT INTO `auth_role_resource` VALUES ('136', '100', '147', '2018-04-26 17:26:55', '2018-04-26 17:26:55');
INSERT INTO `auth_role_resource` VALUES ('142', '102', '149', '2018-04-27 12:46:04', '2018-04-27 12:46:04');
INSERT INTO `auth_role_resource` VALUES ('146', '102', '119', '2018-05-22 19:59:31', '2018-05-22 19:59:31');
INSERT INTO `auth_role_resource` VALUES ('147', '102', '120', '2018-05-22 19:59:35', '2018-05-22 19:59:35');
INSERT INTO `auth_role_resource` VALUES ('150', '106', '117', '2018-05-24 09:52:50', '2018-05-24 09:52:50');
INSERT INTO `auth_role_resource` VALUES ('153', '104', '154', '2018-05-24 11:29:36', '2018-05-24 11:29:36');
INSERT INTO `auth_role_resource` VALUES ('155', '100', '153', '2018-05-24 12:47:53', '2018-05-24 12:47:53');
INSERT INTO `auth_role_resource` VALUES ('158', '103', '101', '2018-05-24 17:02:15', '2018-05-24 17:02:15');
INSERT INTO `auth_role_resource` VALUES ('159', '102', '156', '2018-05-24 17:38:16', '2018-05-24 17:38:16');
INSERT INTO `auth_role_resource` VALUES ('160', '100', '150', '2018-05-24 22:20:00', '2018-05-24 22:20:00');
INSERT INTO `auth_role_resource` VALUES ('161', '102', '147', '2018-05-31 10:16:45', '2018-05-31 10:16:45');
INSERT INTO `auth_role_resource` VALUES ('162', '102', '150', '2018-05-31 10:16:49', '2018-05-31 10:16:49');
INSERT INTO `auth_role_resource` VALUES ('164', '102', '158', '2018-05-31 10:16:58', '2018-05-31 10:16:58');
INSERT INTO `auth_role_resource` VALUES ('166', '104', '156', '2018-05-31 10:19:00', '2018-05-31 10:19:00');
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
INSERT INTO `auth_role_resource` VALUES ('186', '110', '146', '2018-06-02 01:09:48', '2018-06-02 01:09:48');
INSERT INTO `auth_role_resource` VALUES ('187', '100', '164', '2018-06-02 10:22:06', '2018-06-02 10:22:06');
INSERT INTO `auth_role_resource` VALUES ('188', '109', '141', '2018-06-03 06:45:46', '2018-06-03 06:45:46');
INSERT INTO `auth_role_resource` VALUES ('189', '102', '166', '2018-07-10 08:52:53', '2018-07-10 08:52:53');
INSERT INTO `auth_role_resource` VALUES ('190', '109', '125', '2018-07-10 13:50:03', '2018-07-10 13:50:03');
INSERT INTO `auth_role_resource` VALUES ('191', '100', '162', '2018-07-10 22:41:41', '2018-07-10 22:41:41');
INSERT INTO `auth_role_resource` VALUES ('192', '111', '109', '2018-07-10 22:42:42', '2018-07-10 22:42:42');
INSERT INTO `auth_role_resource` VALUES ('197', '100', '149', '2018-10-16 16:25:08', '2018-10-16 16:25:08');
INSERT INTO `auth_role_resource` VALUES ('200', '100', '168', '2018-11-12 14:14:52', '2018-11-12 14:14:52');
INSERT INTO `auth_role_resource` VALUES ('201', '100', '167', '2018-11-12 14:19:35', '2018-11-12 14:19:35');
INSERT INTO `auth_role_resource` VALUES ('202', '100', '169', '2018-11-12 14:21:29', '2018-11-12 14:21:29');
INSERT INTO `auth_role_resource` VALUES ('203', '102', '167', '2018-11-19 14:45:31', '2018-11-19 14:45:31');
INSERT INTO `auth_role_resource` VALUES ('204', '102', '170', '2018-11-20 12:51:56', '2018-11-20 12:51:56');
INSERT INTO `auth_role_resource` VALUES ('218', '103', '107', '2018-12-04 09:23:35', '2018-12-04 09:23:35');
INSERT INTO `auth_role_resource` VALUES ('221', '104', '142', '2018-12-04 15:11:07', '2018-12-04 15:11:07');
INSERT INTO `auth_role_resource` VALUES ('222', '104', '143', '2018-12-04 15:11:13', '2018-12-04 15:11:13');
INSERT INTO `auth_role_resource` VALUES ('224', '113', '120', '2018-12-05 10:38:16', '2018-12-05 10:38:16');
INSERT INTO `auth_role_resource` VALUES ('225', '113', '128', '2018-12-05 10:38:20', '2018-12-05 10:38:20');
INSERT INTO `auth_role_resource` VALUES ('226', '102', '154', '2019-02-18 03:14:01', '2019-02-18 03:14:01');
INSERT INTO `auth_role_resource` VALUES ('230', '105', '101', '2019-10-26 16:20:25', '2019-10-26 16:20:25');
INSERT INTO `auth_role_resource` VALUES ('231', '105', '119', '2019-10-26 16:21:20', '2019-10-26 16:21:20');
INSERT INTO `auth_role_resource` VALUES ('232', '105', '112', '2019-10-26 16:22:45', '2019-10-26 16:22:45');
INSERT INTO `auth_role_resource` VALUES ('233', '105', '145', '2019-10-26 19:32:32', '2019-10-26 19:32:32');
INSERT INTO `auth_role_resource` VALUES ('234', '102', '148', '2020-01-02 20:37:11', '2020-01-02 20:37:11');
INSERT INTO `auth_role_resource` VALUES ('235', '102', '152', '2020-01-02 20:37:19', '2020-01-02 20:37:19');
INSERT INTO `auth_role_resource` VALUES ('236', '102', '153', '2020-01-02 20:37:25', '2020-01-02 20:37:25');
INSERT INTO `auth_role_resource` VALUES ('237', '102', '159', '2020-01-02 20:37:30', '2020-01-02 20:37:30');
INSERT INTO `auth_role_resource` VALUES ('238', '102', '160', '2020-01-02 20:37:35', '2020-01-02 20:37:35');
INSERT INTO `auth_role_resource` VALUES ('239', '102', '165', '2020-01-02 20:37:39', '2020-01-02 20:37:39');
INSERT INTO `auth_role_resource` VALUES ('240', '100', '160', '2020-01-02 21:00:08', '2020-01-02 21:00:08');
INSERT INTO `auth_role_resource` VALUES ('242', '100', '152', '2020-01-02 21:00:15', '2020-01-02 21:00:15');
INSERT INTO `auth_role_resource` VALUES ('243', '100', '165', '2020-01-02 21:00:23', '2020-01-02 21:00:23');
INSERT INTO `auth_role_resource` VALUES ('244', '100', '154', '2020-01-02 21:00:26', '2020-01-02 21:00:26');
INSERT INTO `auth_role_resource` VALUES ('246', '100', '156', '2020-01-02 21:00:33', '2020-01-02 21:00:33');
INSERT INTO `auth_role_resource` VALUES ('247', '100', '159', '2020-01-02 21:00:36', '2020-01-02 21:00:36');
INSERT INTO `auth_role_resource` VALUES ('248', '100', '158', '2020-01-02 21:00:40', '2020-01-02 21:00:40');
INSERT INTO `auth_role_resource` VALUES ('249', '100', '161', '2020-01-02 21:00:43', '2020-01-02 21:00:43');
INSERT INTO `auth_role_resource` VALUES ('250', '100', '166', '2020-01-02 21:00:46', '2020-01-02 21:00:46');
INSERT INTO `auth_role_resource` VALUES ('251', '100', '145', '2020-03-10 10:35:36', '2020-03-10 10:35:36');
INSERT INTO `auth_role_resource` VALUES ('254', '102', '168', '2020-04-01 15:18:44', '2020-04-01 15:18:44');
INSERT INTO `auth_role_resource` VALUES ('255', '102', '169', '2020-04-01 15:18:55', '2020-04-01 15:18:55');
INSERT INTO `auth_role_resource` VALUES ('256', '103', '148', '2020-04-02 14:35:49', '2020-04-02 14:35:49');
INSERT INTO `auth_role_resource` VALUES ('257', '103', '153', '2020-04-02 14:35:55', '2020-04-02 14:35:55');
INSERT INTO `auth_role_resource` VALUES ('258', '103', '165', '2020-04-02 14:36:06', '2020-04-02 14:36:06');
INSERT INTO `auth_role_resource` VALUES ('259', '103', '159', '2020-04-02 14:36:11', '2020-04-02 14:36:11');
INSERT INTO `auth_role_resource` VALUES ('260', '103', '160', '2020-04-02 14:36:14', '2020-04-02 14:36:14');
INSERT INTO `auth_role_resource` VALUES ('261', '103', '162', '2020-04-02 14:36:19', '2020-04-02 14:36:19');
INSERT INTO `auth_role_resource` VALUES ('262', '103', '168', '2020-04-02 14:36:25', '2020-04-02 14:36:25');

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
) ENGINE=InnoDB AUTO_INCREMENT=74 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='用户角色关联表';

-- ----------------------------
-- Records of auth_user_role
-- ----------------------------
INSERT INTO `auth_user_role` VALUES ('39', 'admin', '100', '2019-10-26 15:20:21', '2019-10-26 15:20:21');
