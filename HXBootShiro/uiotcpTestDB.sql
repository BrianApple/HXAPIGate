/*
Navicat MariaDB Data Transfer

Source Server         : 47.104.100.254
Source Server Version : 50564
Source Host           : 47.104.100.254:3306
Source Database       : uiotcpTestDB

Target Server Type    : MariaDB
Target Server Version : 50564
File Encoding         : 65001

Date: 2020-04-02 16:54:23
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for app_group_rel
-- ----------------------------
DROP TABLE IF EXISTS `app_group_rel`;
CREATE TABLE `app_group_rel` (
  `group_id` varchar(100) NOT NULL COMMENT '感知组id',
  `app_id` varchar(100) NOT NULL COMMENT '应用id',
  `state` varchar(3) NOT NULL COMMENT '状态',
  `user_id` varchar(3) DEFAULT NULL COMMENT '所属用户id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of app_group_rel
-- ----------------------------

-- ----------------------------
-- Table structure for app_term_rel
-- ----------------------------
DROP TABLE IF EXISTS `app_term_rel`;
CREATE TABLE `app_term_rel` (
  `term_id` varchar(100) NOT NULL COMMENT '感知设备id',
  `app_id` varchar(100) NOT NULL COMMENT '应用id',
  `state` varchar(3) NOT NULL COMMENT '状态',
  `user_id` varchar(20) DEFAULT NULL COMMENT '所属用户id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备应用关系表';

-- ----------------------------
-- Records of app_term_rel
-- ----------------------------
INSERT INTO `app_term_rel` VALUES ('感知id', '11ddddd', '0', '111');
INSERT INTO `app_term_rel` VALUES ('7b16126b715a4ab0a030317f0b9ce6b1', '38c1762afec2469182ec5071de6f500f', '0', 'lcy7111');
INSERT INTO `app_term_rel` VALUES ('9dd6411e2c4c44febba8ea2b556abf05', 'c2fd07b1c27342dd90068c544d368bde', '0', 'lcy7111');
INSERT INTO `app_term_rel` VALUES ('9dd6411e2c4c44febba8ea2b556abf05', '23473f1318b54e4b95d5327ed20d42b7', '0', 'lcy7111');
INSERT INTO `app_term_rel` VALUES ('7b16126b715a4ab0a030317f0b9ce6b1', '556ece07ca2e47cea06fbdb8e399eae0', '0', 'lcy7111');
INSERT INTO `app_term_rel` VALUES ('9619df7d4ed246e994d15188318081c5', '41601e36cd8a47bd9ac4bdda5e529bfa', '1', 'lcy7111');
INSERT INTO `app_term_rel` VALUES ('9dd6411e2c4c44febba8ea2b556abf05', 'e481d3845de14f51afaf6e32bc47a79c', '0', 'lcy7111');
INSERT INTO `app_term_rel` VALUES ('9dd6411e2c4c44febba8ea2b556abf05', 'cbb18184a4fe4d4ea1eb58b5ed50818e', '2', 'lcy7111');
INSERT INTO `app_term_rel` VALUES ('9619df7d4ed246e994d15188318081c5', '333f6a256329422183a9af23d04aa65a', '1', 'lcy7111');

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
INSERT INTO `auth_resource` VALUES ('147', 'PASTORALDOG_API', '牧羊犬API', '110', null, '3', null, null, null, null, '1', '2020-04-02 15:09:55', null);
INSERT INTO `auth_resource` VALUES ('148', 'specific', '获取用户对应规约', '147', '/specific/*', '2', 'GET', null, null, null, '1', '2020-04-02 15:09:58', null);
INSERT INTO `auth_resource` VALUES ('149', 'specific', '新增规约', '147', '/specific/*', '2', 'POST', null, null, null, '1', '2020-04-02 15:10:00', null);
INSERT INTO `auth_resource` VALUES ('150', 'specific', '删除/修改用户对应的某个规约', '147', '/specific/*', '2', 'PUT', null, null, null, '1', '2020-04-02 15:10:03', null);
INSERT INTO `auth_resource` VALUES ('152', 'terminalGroup', '新增设备组', '147', '/terminalGroup/*', '2', 'POST', null, null, null, '1', '2020-04-02 15:10:03', null);
INSERT INTO `auth_resource` VALUES ('153', 'terminalGroup', '查询设备组', '147', '/terminalGroup/*', '2', 'GET', null, null, null, '1', '2020-04-02 15:10:03', null);
INSERT INTO `auth_resource` VALUES ('154', 'terminalGroup', '删除设备组/修改设备组', '147', '/terminalGroup/*', '2', 'PUT', null, null, null, '1', '2020-04-02 15:10:03', null);
INSERT INTO `auth_resource` VALUES ('156', 'termInfo', '新增感知设备', '147', '/termInfo/*', '2', 'POST', null, null, null, '1', '2020-04-02 15:10:03', null);
INSERT INTO `auth_resource` VALUES ('158', 'termInfo', '修改/删除设备', '147', '/termInfo/*', '2', 'PUT', null, null, null, '1', '2020-04-02 15:10:03', null);
INSERT INTO `auth_resource` VALUES ('159', 'termInfo', '查询设备', '147', '/termInfo/*', '2', 'GET', null, null, null, '1', '2020-04-02 15:10:03', null);
INSERT INTO `auth_resource` VALUES ('160', 'commonService', '标准代码API接口', '147', '/commonService/*', '2', 'GET', null, null, null, '1', '2020-04-02 15:10:03', null);
INSERT INTO `auth_resource` VALUES ('161', 'termInfo', '新增设备类型', '147', '/termInfo/*', '2', 'POST', null, null, null, '1', '2020-04-02 15:10:03', null);
INSERT INTO `auth_resource` VALUES ('162', 'termInfo', '查询设备类型', '147', '/termInfo/*', '2', 'GET', null, null, null, '1', '2020-04-02 15:10:03', null);
INSERT INTO `auth_resource` VALUES ('163', 'termInfo', '删除设备类型', '147', '/termInfo/*', '2', 'PUT', null, null, null, '1', '2020-04-02 15:10:03', null);
INSERT INTO `auth_resource` VALUES ('164', 'application', '创建授权应用', '147', '/application/*', '2', 'POST', null, null, null, '1', '2020-04-02 15:10:03', null);
INSERT INTO `auth_resource` VALUES ('165', 'application', '查询用户的授权应用', '147', '/application/*', '2', 'GET', null, null, null, '1', '2020-04-02 15:10:03', null);
INSERT INTO `auth_resource` VALUES ('166', 'application', '启用授权应用/删除授权应用', '147', '/application/*', '2', 'PUT', null, null, null, '1', '2020-04-02 15:10:03', null);
INSERT INTO `auth_resource` VALUES ('167', 'PASSWORD_UPDATE', '密码修改(/user/accountupdate)', '103', '/user/accountupdate', '2', 'PUT', null, null, null, '1', '2020-04-02 15:10:03', null);
INSERT INTO `auth_resource` VALUES ('168', 'userDefType', '查看用户自定义类型（编辑也一样）', '147', '/userDefType/*', '2', 'GET', null, null, null, '1', '2020-04-02 15:10:03', null);
INSERT INTO `auth_resource` VALUES ('169', 'userDefType', '新增用户自定义类型', '147', '/userDefType/*', '2', 'POST', null, null, null, '1', '2020-04-02 15:10:03', null);
INSERT INTO `auth_resource` VALUES ('170', 'userDefType', '修改（删除）自定义类型', '147', '/userDefType/*', '2', 'PUT', null, null, null, '1', '2020-04-02 15:10:03', null);

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
INSERT INTO `auth_user` VALUES ('98357806441', '物新驱动', '7B33CE1E4E90BBB5F49B5A9274301B84', '64ameg', null, null, '15268115207', '953706441@qq.com', null, '1', '2020-03-16 20:52:28', null, null);
INSERT INTO `auth_user` VALUES ('admin', '超级管理员', '181715F2A54ED81DD2826DC3F6E7F848', 'gfkqj9', null, null, null, null, null, '1', '2019-10-26 15:20:21', null, null);
INSERT INTO `auth_user` VALUES ('lcy7111', '罗阿萨大', '3512EB96DF45BA079F8C518569D27BA6', 'xxo379', null, null, '15715762139', '1261211982@qq.com', null, '1', '2019-11-30 23:47:54', null, null);
INSERT INTO `auth_user` VALUES ('yangcheng1', '杨承', 'E9D4A168745CA9BE0DD757D4F48965D3', 'fxfn80', null, null, '17682489836', null, null, '1', '2020-03-18 15:34:09', null, null);
INSERT INTO `auth_user` VALUES ('yifeng', 'xby716', '0C25336E148D047D6B13ED382DBCC898', 'c04sk2', null, null, '18053524567', null, null, '1', '2020-01-15 10:33:24', null, null);

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
INSERT INTO `auth_user_role` VALUES ('58', 'lcy7111', '102', '2019-11-30 23:47:54', '2019-11-30 23:47:54');
INSERT INTO `auth_user_role` VALUES ('68', 'yifeng', '102', '2020-01-15 10:33:24', '2020-01-15 10:33:24');
INSERT INTO `auth_user_role` VALUES ('70', '98357806441', '102', '2020-03-16 20:52:28', '2020-03-16 20:52:28');
INSERT INTO `auth_user_role` VALUES ('73', 'yangcheng1', '102', '2020-03-18 15:34:09', '2020-03-18 15:34:09');

-- ----------------------------
-- Table structure for dataitem
-- ----------------------------
DROP TABLE IF EXISTS `dataitem`;
CREATE TABLE `dataitem` (
  `dataitem_id` varchar(10) NOT NULL COMMENT '数据项标识',
  `dataitem_name` varchar(20) NOT NULL COMMENT '数据项名称',
  `dataitem_stru` varchar(5000) DEFAULT NULL COMMENT '数据项结构',
  `specific_id` varchar(5) NOT NULL COMMENT '规约标识',
  `state` varchar(2) NOT NULL COMMENT '状态',
  PRIMARY KEY (`dataitem_id`,`specific_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of dataitem
-- ----------------------------
INSERT INTO `dataitem` VALUES ('0410', '04F10', '{\"iName0\":\"111\",\"dataItemName0\":\"数据项名称\",\"dataItemKey0\":\"userType_hjj111\",\"iName1\":\"222\",\"dataItemName1\":\"Integer16BitType\",\"dataItemKey1\":\"basicType_10\",\"iName2\":\"333\",\"dataItemName2\":\"Enum\",\"dataItemKey2\":\"basicType_15\",\"type0\":0,\"type1\":1,\"type2\":1}', 'g697', '1');

-- ----------------------------
-- Table structure for dlink_strategy_configure
-- ----------------------------
DROP TABLE IF EXISTS `dlink_strategy_configure`;
CREATE TABLE `dlink_strategy_configure` (
  `state` tinyint(4) NOT NULL COMMENT '状态'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='下行策略配置';

-- ----------------------------
-- Records of dlink_strategy_configure
-- ----------------------------

-- ----------------------------
-- Table structure for equip_type_info
-- ----------------------------
DROP TABLE IF EXISTS `equip_type_info`;
CREATE TABLE `equip_type_info` (
  `equip_type_id` varchar(40) NOT NULL COMMENT '设备类型id，主键',
  `type_name` varchar(255) NOT NULL COMMENT '类型名称',
  `user_id` varchar(255) NOT NULL COMMENT '所属用户id',
  `state` varchar(255) NOT NULL COMMENT '状态',
  `createTime` timestamp NULL DEFAULT NULL COMMENT '新增时间',
  `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`equip_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备类型';

-- ----------------------------
-- Records of equip_type_info
-- ----------------------------
INSERT INTO `equip_type_info` VALUES ('1d91cb74c2f849e284ca0915eed383c9', 'qweq', 'lcy7111', '1', '2019-12-30 17:52:18', null);
INSERT INTO `equip_type_info` VALUES ('286e992296024f58949ea895dc246088', 'qweff', 'lcy7111', '1', '2019-12-30 17:52:25', null);
INSERT INTO `equip_type_info` VALUES ('3129d52a8ceb46ad8631bbc7cd4ba606', 'aass', 'lcy7111', '0', '2019-12-30 17:50:22', '2019-12-30 17:51:41');
INSERT INTO `equip_type_info` VALUES ('480aef2f1a3f4c3b85f9a8fbf485f45c', 'asd', 'lcy7111', '1', '2019-12-30 17:51:46', null);
INSERT INTO `equip_type_info` VALUES ('5bbe0bbb928847b19b4ac8699ba65313', 'asdff', 'lcy7111', '1', '2019-12-30 17:52:15', null);
INSERT INTO `equip_type_info` VALUES ('6b3d6a52d16f475ba22df938e7f92c06', 'qweggw', 'lcy7111', '1', '2019-12-30 17:52:35', null);
INSERT INTO `equip_type_info` VALUES ('7e1fd4eed396412ea88204f12d06f25a', 'asdf', 'lcy7111', '1', '2019-12-30 17:52:29', null);
INSERT INTO `equip_type_info` VALUES ('89b9685d16544157b0795707932a46a7', 'qwegg', 'lcy7111', '1', '2019-12-30 17:52:32', null);
INSERT INTO `equip_type_info` VALUES ('95931dbedaeb430e855d2424ccf966b8', '设备类型名称', '222', '1', '2020-01-02 11:00:49', null);
INSERT INTO `equip_type_info` VALUES ('a4da23c470804c8a885185fbef05fa7d', 'qwewq2', 'lcy7111', '1', '2019-12-30 17:52:43', null);
INSERT INTO `equip_type_info` VALUES ('a782bd83f7b941d0bba2e7b8a0386d8d', 'eqwf', 'lcy7111', '1', '2019-12-30 17:52:22', null);
INSERT INTO `equip_type_info` VALUES ('afe9a21002f740b9b1b0adda7778dac3', 'fewgb', 'lcy7111', '0', '2019-12-30 17:52:39', '2020-01-12 11:32:42');
INSERT INTO `equip_type_info` VALUES ('eeaf2dfa02ee414e9ffdc3952be49ee3', '名称1', '112', '1', '2019-12-31 15:23:53', null);
INSERT INTO `equip_type_info` VALUES ('f26d676e0d2f4cb7b9c80ab4c0d57a72', '名称1', '222', '0', '2019-12-14 13:55:19', '2019-12-14 14:19:18');
INSERT INTO `equip_type_info` VALUES ('fb8859d703b6421eae2a7425a5e3e32d', '376.1集中器', 'yc', '1', '2020-01-17 15:28:45', null);
INSERT INTO `equip_type_info` VALUES ('fc7f40b1b24b4f8fa0d201a83ccce6c7', 'rwq213', 'lcy7111', '0', '2019-12-30 17:52:49', '2020-01-14 10:58:25');

-- ----------------------------
-- Table structure for license_application
-- ----------------------------
DROP TABLE IF EXISTS `license_application`;
CREATE TABLE `license_application` (
  `app_name` varchar(255) DEFAULT NULL COMMENT '应用名称',
  `is_group_accredit` varchar(255) DEFAULT NULL COMMENT '是否以感知组为单位授权',
  `appid` varchar(255) DEFAULT NULL COMMENT 'appid',
  `license_keys` varchar(255) DEFAULT NULL COMMENT '授权密钥',
  `valid_time` timestamp NULL DEFAULT NULL COMMENT '有效时间',
  `state` tinyint(4) DEFAULT NULL COMMENT '状态',
  `user_id` varchar(255) DEFAULT NULL COMMENT '所属用户id',
  `createTime` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `updateTime` datetime DEFAULT NULL COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='授权应用';

-- ----------------------------
-- Records of license_application
-- ----------------------------
INSERT INTO `license_application` VALUES ('应用名称', 'false', '11ddddd', '121', '2019-12-15 00:00:00', '0', '111', '2019-12-15 14:11:44', '2019-12-29 13:04:29');
INSERT INTO `license_application` VALUES ('123', '0', '38c1762afec2469182ec5071de6f500f', 'de04c49c615d46319d3d8c25cde03b95', '2020-01-17 23:59:59', '0', 'lcy7111', '2020-01-03 16:08:29', '2020-01-05 14:23:42');
INSERT INTO `license_application` VALUES ('1233334', '0', 'c2fd07b1c27342dd90068c544d368bde', 'aef51b2d547f403aa9e3f8fda797bb56', '2020-01-17 23:59:59', '0', 'lcy7111', '2020-01-03 16:11:43', '2020-01-07 11:21:36');
INSERT INTO `license_application` VALUES ('qwe', '0', '23473f1318b54e4b95d5327ed20d42b7', '63686a8ac0714fa998f5b40697ebbca2', '2020-01-25 23:59:59', '0', 'lcy7111', '2020-01-05 14:20:48', '2020-01-07 15:40:02');
INSERT INTO `license_application` VALUES ('213', '0', '556ece07ca2e47cea06fbdb8e399eae0', '4c7d52d147a64e748b50dfd4a1f14eff======', '2020-01-25 23:59:59', '0', 'lcy7111', '2020-01-07 11:22:11', '2020-01-07 15:52:39');
INSERT INTO `license_application` VALUES ('1231', '0', '41601e36cd8a47bd9ac4bdda5e529bfa', '668e836bb6e64e35a3e739d5c2d5a3e0======', '2020-01-31 23:59:59', '1', 'lcy7111', '2020-01-07 15:04:35', '2020-03-16 15:57:37');
INSERT INTO `license_application` VALUES ('23123e2222', '0', 'e481d3845de14f51afaf6e32bc47a79c', '3375e77bed4c41bbafe9ad7528064b56======', '2020-01-25 23:59:59', '0', 'lcy7111', '2020-01-07 15:36:09', '2020-01-07 15:56:06');
INSERT INTO `license_application` VALUES ('www', '0', 'cbb18184a4fe4d4ea1eb58b5ed50818e', 'e266cc6a48714619b68b292d71da7650======', '2020-01-31 23:59:59', '2', 'lcy7111', '2020-01-07 15:52:54', '2020-03-16 15:57:40');
INSERT INTO `license_application` VALUES ('21313ddfff', '0', '333f6a256329422183a9af23d04aa65a', 'b3c972c8d4c3484c80f98bc2373b5889======', '2020-01-24 23:59:59', '1', 'lcy7111', '2020-01-07 17:51:12', '2020-01-07 17:51:21');

-- ----------------------------
-- Table structure for specific_manage
-- ----------------------------
DROP TABLE IF EXISTS `specific_manage`;
CREATE TABLE `specific_manage` (
  `id` varchar(255) NOT NULL COMMENT '规约id',
  `name` varchar(255) NOT NULL COMMENT '规约名',
  `user_id` varchar(255) NOT NULL COMMENT '所属用户id',
  `state` tinyint(4) NOT NULL COMMENT '状态',
  `createTime` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
  `is_bitendian` tinyint(4) NOT NULL COMMENT '是否是大端模式（1是、0否）',
  `elements` varchar(5000) DEFAULT NULL COMMENT '组成元素',
  `specific_type` tinyint(4) DEFAULT NULL COMMENT '规约类型(0：官方规约 1：自定义规约）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='规约管理';

-- ----------------------------
-- Records of specific_manage
-- ----------------------------
INSERT INTO `specific_manage` VALUES ('1', '贵贵贵', 'hjj', '2', '2019-12-18 19:33:33', null, '0', '', '1');
INSERT INTO `specific_manage` VALUES ('1111', '规约名称', 'hjj01', '1', '2020-03-20 14:12:43', null, '1', 'function finalAssembly(pId,dataItemId,byteBuffer){↵ asdasd↵}↵function translate(byteBuffer){↵asddw↵}', '1');
INSERT INTO `specific_manage` VALUES ('1231', '测试规约', '222', '2', '2019-12-14 09:44:20', null, '0', '', '1');
INSERT INTO `specific_manage` VALUES ('222', '修改1229', '222', '0', '2019-12-14 09:44:53', '2019-12-31 17:23:05', '0', '', '1');
INSERT INTO `specific_manage` VALUES ('23wq', 'support', 'lcy7111', '1', '2020-03-19 11:08:10', null, '1', '', '1');
INSERT INTO `specific_manage` VALUES ('3761', '国网376', 'yc', '1', '2020-01-17 15:15:17', null, '0', '', '1');
INSERT INTO `specific_manage` VALUES ('9999', '规约名称', 'hjj01', '1', '2020-01-09 17:49:22', null, '1', '', '1');
INSERT INTO `specific_manage` VALUES ('as2', 'support', 'lcy7111', '0', '2020-03-17 10:31:04', '2020-03-19 11:07:57', '1', '', '1');
INSERT INTO `specific_manage` VALUES ('asd', '123', 'lcy7111', '1', '2020-03-17 10:29:45', null, '0', '', '1');
INSERT INTO `specific_manage` VALUES ('asdf', '测试规约001', 'lcy7111', '1', '2019-12-30 09:40:14', '2020-03-17 10:37:43', '0', '', '1');
INSERT INTO `specific_manage` VALUES ('asdw', 'lcy2019122901', 'lcy7111', '1', '2019-12-30 09:40:05', '2020-01-14 16:14:19', '0', '', '1');
INSERT INTO `specific_manage` VALUES ('cc00', '测试规约3.10-1', 'lcy7111', '1', '2020-03-10 10:11:18', null, '0', '', '1');
INSERT INTO `specific_manage` VALUES ('cly9', 'lcy2019122901', 'lcy7111', '1', '2019-12-30 09:40:00', '2020-01-14 16:14:22', '0', '', '1');
INSERT INTO `specific_manage` VALUES ('ddcc', '测试规约3.10-2', 'lcy7111', '0', '2020-03-10 10:11:43', '2020-03-10 10:58:26', '0', '', '1');
INSERT INTO `specific_manage` VALUES ('e2w1', 'support22', 'lcy7111', '1', '2020-03-19 11:07:08', null, '1', '', '1');
INSERT INTO `specific_manage` VALUES ('ee34', 'support2', 'lcy7111', '1', '2020-03-19 11:11:06', null, '1', '', '1');
INSERT INTO `specific_manage` VALUES ('er33', '测试规约3.10', 'lcy7111', '1', '2020-03-10 10:10:40', null, '0', '', '1');
INSERT INTO `specific_manage` VALUES ('g376', 'g3761', 'lcy7111', '1', '2020-01-05 20:49:01', '2020-01-14 16:14:25', '0', '', '1');
INSERT INTO `specific_manage` VALUES ('g697', '测试001', 'yangcheng1', '1', '2020-03-21 09:59:57', null, '1', 'function finalAssembly(pId,dataItemId,byteBuffer){\n        alert(\"1\");    \n}\nfunction translate(byteBuffer){\n\n}', '1');
INSERT INTO `specific_manage` VALUES ('g698', '国网698规约', 'root', '1', '2020-03-17 16:29:54', null, '0', '', '0');
INSERT INTO `specific_manage` VALUES ('lcy1', 'lcy2019122901', 'lcy7111', '1', '2019-12-29 15:17:30', '2020-01-14 16:14:27', '0', '', '1');
INSERT INTO `specific_manage` VALUES ('lcy2', 'lcy2019122902', 'lcy7111', '1', '2019-12-29 15:17:51', '2020-01-16 10:22:31', '0', '', '1');
INSERT INTO `specific_manage` VALUES ('lcy3', 'lcy2019122903', 'lcy7111', '0', '2019-12-29 22:58:48', '2020-01-07 16:49:35', '0', '', '1');
INSERT INTO `specific_manage` VALUES ('lcy4', 'lcy2019122904', 'lcy7111', '1', '2019-12-29 22:59:53', '2020-01-16 10:22:34', '0', '', '1');
INSERT INTO `specific_manage` VALUES ('lcy5', 'lcy20200114', 'lcy7111', '0', '2019-12-29 23:00:07', '2020-01-14 15:57:03', '1', '', '1');
INSERT INTO `specific_manage` VALUES ('lcy6', 'lcy2019122901', 'lcy7111', '1', '2019-12-30 09:39:35', '2020-01-16 10:22:36', '0', '', '1');
INSERT INTO `specific_manage` VALUES ('lcy7', 'lcy2019122901', 'lcy7111', '0', '2019-12-30 09:39:45', '2020-01-14 15:54:09', '0', '', '1');
INSERT INTO `specific_manage` VALUES ('lcy8', 'lcy2019122901', 'lcy7111', '0', '2019-12-30 09:39:53', '2020-01-15 08:49:37', '0', '', '1');
INSERT INTO `specific_manage` VALUES ('llll', 'pworiw33', 'lcy7111', '1', '2020-03-20 14:36:19', '2020-03-20 15:42:26', '1', 'function finalAssembly(pId,dataItemId,byteBuffer){\n            wobuky2245\n}\nfunction translate(byteBuffer){\n      俺啥zhuajinnideyanjing\n}', '1');
INSERT INTO `specific_manage` VALUES ('mmmm', 'support', 'lcy7111', '0', '2020-01-07 16:54:53', '2020-01-07 16:54:57', '0', '', '1');
INSERT INTO `specific_manage` VALUES ('qqqq', 'qqqqqqq', 'lcy7111', '1', '2020-03-10 18:00:06', null, '0', '', '1');
INSERT INTO `specific_manage` VALUES ('rrr3', 'support2', 'lcy7111', '1', '2020-03-19 11:12:47', null, '1', '', '1');
INSERT INTO `specific_manage` VALUES ('sdcv', 'support666', 'lcy7111', '1', '2020-03-20 09:49:38', null, '1', 'function finalAssembly(pId,dataItemId,byteBuffer){↵ asdasd↵}↵function translate(byteBuffer){↵asddw↵}', '1');
INSERT INTO `specific_manage` VALUES ('sdqq', 'aaaaa', 'lcy7111', '0', '2019-12-30 16:37:53', '2020-01-14 16:12:11', '0', '', '1');
INSERT INTO `specific_manage` VALUES ('xxcv', '测试规约3.10-3', 'lcy7111', '1', '2020-03-10 11:01:25', null, '0', '', '1');

-- ----------------------------
-- Table structure for standardCode
-- ----------------------------
DROP TABLE IF EXISTS `standardCode`;
CREATE TABLE `standardCode` (
  `id` tinyint(4) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `state` tinyint(4) DEFAULT '1',
  `pid` tinyint(4) DEFAULT NULL COMMENT '0为父节点',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of standardCode
-- ----------------------------
INSERT INTO `standardCode` VALUES ('1', '接线方式', '1', '0');
INSERT INTO `standardCode` VALUES ('2', 'WIFI', '1', '1');
INSERT INTO `standardCode` VALUES ('3', 'GPRS', '1', '1');
INSERT INTO `standardCode` VALUES ('4', '485线', '1', '1');
INSERT INTO `standardCode` VALUES ('9', '状态', '1', '0');
INSERT INTO `standardCode` VALUES ('10', '删除', '1', '9');
INSERT INTO `standardCode` VALUES ('11', '启动', '1', '9');
INSERT INTO `standardCode` VALUES ('12', '不启动', '1', '9');
INSERT INTO `standardCode` VALUES ('13', '自定义数据类型', '1', '0');
INSERT INTO `standardCode` VALUES ('14', '普通类型', '1', '13');
INSERT INTO `standardCode` VALUES ('15', '数组类型', '1', '13');
INSERT INTO `standardCode` VALUES ('16', '脚本类型', '1', '13');

-- ----------------------------
-- Table structure for terminal_group
-- ----------------------------
DROP TABLE IF EXISTS `terminal_group`;
CREATE TABLE `terminal_group` (
  `group_id` varchar(40) NOT NULL COMMENT '感知设备组id',
  `group_name` varchar(255) NOT NULL COMMENT '组名称',
  `specific_id` varchar(255) NOT NULL COMMENT '规约id',
  `equipmax_num` int(11) NOT NULL COMMENT '设备最大数量',
  `network_way` varchar(255) NOT NULL COMMENT '联网方式',
  `state` tinyint(4) NOT NULL COMMENT '状态',
  `user_id` varchar(255) NOT NULL COMMENT '所属用户id',
  `createTime` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='感知设备组';

-- ----------------------------
-- Records of terminal_group
-- ----------------------------
INSERT INTO `terminal_group` VALUES ('0193c134bbb4420fb28702363228998c', '测试规约001设备组01', 'asdf', '20', '3', '2', 'lcy7111', '2020-04-02 10:05:15', null);
INSERT INTO `terminal_group` VALUES ('4ee8dc74f0734a98a1dd8b0b3c296c9d', '测试设备组3.10', 'asdf', '20', '2', '2', 'lcy7111', '2020-03-10 10:19:11', null);
INSERT INTO `terminal_group` VALUES ('5eee2ba568eb4a3c9f0e076c073a5bc4', '222222xxxx', 'asdf', '20', '13', '0', 'lcy7111', '2020-01-07 17:04:31', '2020-01-13 21:54:26');
INSERT INTO `terminal_group` VALUES ('6e34503689a446b7b7fe72733b933ff3', '20200115', 'asdf', '20', '13', '0', 'lcy7111', '2020-01-15 09:54:51', '2020-03-10 10:19:51');
INSERT INTO `terminal_group` VALUES ('7db85849ace147ebb7b326cd7ee6ecaf', '11', 'asdf', '20', '1', '0', 'lcy7111', '2019-12-31 17:33:37', '2019-12-31 17:33:45');
INSERT INTO `terminal_group` VALUES ('868fe9e03639449ab85eac1473dd4072', 'dsas', 'asdf', '20', '1', '0', 'lcy7111', '2019-12-31 17:34:28', '2019-12-31 17:34:32');
INSERT INTO `terminal_group` VALUES ('a743dbae5e23434e8ef1591c8c6557bd', '杨承测试组', 'asdf', '2000', '1', '1', 'lcy7111', '2020-01-06 08:57:50', null);
INSERT INTO `terminal_group` VALUES ('be392e989ca84155aeb66c11054272e7', 'aaa', 'asdf', '20', '1', '1', 'lcy7111', '2019-12-31 17:29:37', null);
INSERT INTO `terminal_group` VALUES ('d0eaaac849314f9da6fff04e7a2e4192', '国网376设备组', '3761', '20', '1', '2', 'yc', '2020-01-17 15:23:17', null);
INSERT INTO `terminal_group` VALUES ('f19121a24c20443ba4403185f6e0c70b', '1112', 'asdf', '12', '1', '0', 'lcy7111', '2019-12-31 16:41:05', '2020-03-10 10:19:38');
INSERT INTO `terminal_group` VALUES ('fccf39a728f44507a21d0be2dc456a95', '12', 'asdf', '20', '1', '0', 'lcy7111', '2020-01-13 14:39:11', '2020-03-10 10:19:36');

-- ----------------------------
-- Table structure for terminal_info
-- ----------------------------
DROP TABLE IF EXISTS `terminal_info`;
CREATE TABLE `terminal_info` (
  `group_id` varchar(255) NOT NULL COMMENT '所属感知足id',
  `user_id` varchar(255) NOT NULL COMMENT '所属用户id',
  `equip_name` varchar(255) NOT NULL COMMENT '设备名称',
  `equip_number` varchar(40) NOT NULL COMMENT '设备编号，主键',
  `longitude` double NOT NULL COMMENT '经度',
  `lat` double NOT NULL COMMENT '纬度',
  `street_info` varchar(255) DEFAULT NULL COMMENT '街道信息',
  `ishave_meter` tinyint(4) NOT NULL COMMENT '有无嗅点',
  `equip_type` varchar(255) NOT NULL COMMENT '设备类型',
  `state` tinyint(4) DEFAULT NULL COMMENT '状态',
  `comment` varchar(255) DEFAULT NULL COMMENT '备注',
  `par_terminal_id` varchar(255) DEFAULT NULL COMMENT '父感知id',
  `ter_addr` varchar(255) DEFAULT NULL COMMENT '通信地址',
  `createTime` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`equip_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='感知设备';

-- ----------------------------
-- Records of terminal_info
-- ----------------------------
INSERT INTO `terminal_info` VALUES ('be392e989ca84155aeb66c11054272e7', '\"lcy7111\"', '\"111\"', '0c12184eb236417baa4c3e8bddfa6d05', '221', '321', '\"123\"', '0', '\"12\"', '1', '\"123\"', '\"123\"', '\"12\"', '2020-03-11 11:08:31', null);
INSERT INTO `terminal_info` VALUES ('a743dbae5e23434e8ef1591c8c6557bd', 'lcy7111', 'qq', '0f84d5c4a5e541daaf499e0648c408c6', '11', '123', '123122', '1', 'a4da23c470804c8a885185fbef05fa7d', '1', '1111222sss', '123', '1112w', '2020-01-07 11:30:54', null);
INSERT INTO `terminal_info` VALUES ('be392e989ca84155aeb66c11054272e7', 'lcy7111', '123', '19618cc6133e4640bfdccc0aa0dc7ab0', '13', '123', '123', '0', '123', '1', '123', '13', '123', '2020-01-02 16:51:30', null);
INSERT INTO `terminal_info` VALUES ('d0eaaac849314f9da6fff04e7a2e4192', 'yc', '测试集中器', '1a5848615cc145d1b1bd1574cb13dbca', '111', '222', '北京天安门', '0', 'fb8859d703b6421eae2a7425a5e3e32d', '1', '3761集中器', '1', '123456', '2020-01-17 15:41:35', null);
INSERT INTO `terminal_info` VALUES ('4ee8dc74f0734a98a1dd8b0b3c296c9d', 'lcy7111', '新增设备测试3.10-45', '3d63663ceb4347aa8e35e34cedc47b2f', '0', '0', null, '0', '1d91cb74c2f849e284ca0915eed383c9', '1', null, null, '3333.2302.2367', '2020-03-11 11:19:21', '2020-03-13 09:32:08');
INSERT INTO `terminal_info` VALUES ('d0eaaac849314f9da6fff04e7a2e4192', 'yc', '测试集中器', '4316f4148a8d4c5a9758d8d2260d5eb1', '111', '222', '北京天安门', '0', 'fb8859d703b6421eae2a7425a5e3e32d', '0', '3761集中器', '2', '123456', '2020-01-17 15:41:45', '2020-01-17 15:43:56');
INSERT INTO `terminal_info` VALUES ('4ee8dc74f0734a98a1dd8b0b3c296c9d', 'lcy7111', '新增设备测试3.10-1', '4534da5a1bd844afaf74619d48d3341a', '224', '0', '', '0', '1d91cb74c2f849e284ca0915eed383c9', '1', '', '', '3333.44', '2020-03-10 15:20:49', '2020-03-10 17:58:53');
INSERT INTO `terminal_info` VALUES ('4ee8dc74f0734a98a1dd8b0b3c296c9d', 'lcy7111', '新增设备测试3.99', '5c0681a16c5f44d292b988bdb663e8cf', '0', '0', null, '0', '1d91cb74c2f849e284ca0915eed383c9', '1', null, null, '3333.2302.9999', '2020-03-11 11:18:41', null);
INSERT INTO `terminal_info` VALUES ('4ee8dc74f0734a98a1dd8b0b3c296c9d', 'lcy7111', '新增设备测试3.10-2', '5e98be1a393d45f38e2c19d6f1a3687c', '0', '0', '', '0', '1d91cb74c2f849e284ca0915eed383c9', '1', '', '', '3333.230244', '2020-03-10 15:50:02', null);
INSERT INTO `terminal_info` VALUES ('4ee8dc74f0734a98a1dd8b0b3c296c9d', 'lcy7111', '新增设备测试3.10', '68f4315b1e8e4f59badb2ed9cee90be2', '0', '0', '', '0', '1d91cb74c2f849e284ca0915eed383c9', '1', '', '', '123221', '2020-03-11 11:14:27', null);
INSERT INTO `terminal_info` VALUES ('4ee8dc74f0734a98a1dd8b0b3c296c9d', 'lcy7111', '新增设备测试3.10', '6bc94294dca24bf5b4d98cca1ec2c42d', '0', '0', '', '0', '1d91cb74c2f849e284ca0915eed383c9', '1', '', '', '3333.2302.23', '2020-03-10 15:15:58', null);
INSERT INTO `terminal_info` VALUES ('be392e989ca84155aeb66c11054272e7', 'lcy7111', '123', '7b16126b715a4ab0a030317f0b9ce6b1', '123', '3123', '123', '0', '286e992296024f58949ea895dc246088', '1', '1111', '123', '3213', '2020-01-03 10:38:26', '2020-01-03 11:12:26');
INSERT INTO `terminal_info` VALUES ('be392e989ca84155aeb66c11054272e7', 'lcy7111', '45', '82812fe233584086926dd446640f222a', '345', '345', '53', '0', '345', '1', '345', '35', '35', '2020-01-02 16:54:41', null);
INSERT INTO `terminal_info` VALUES ('d0eaaac849314f9da6fff04e7a2e4192', 'yc', '测试集中器', '93818dfc238b4a6cb1f62c798e7dde3d', '111', '222', '北京天安门', '1', 'fb8859d703b6421eae2a7425a5e3e32d', '0', '3761集中器', '2', '123456', '2020-01-17 15:42:35', '2020-01-17 15:44:08');
INSERT INTO `terminal_info` VALUES ('be392e989ca84155aeb66c11054272e7', 'lcy7111', '2343', '9619df7d4ed246e994d15188318081c5', '3333', '222', '333333', '0', '1d91cb74c2f849e284ca0915eed383c9', '1', '333333333', '2222', '333333333333', '2020-01-03 10:39:22', null);
INSERT INTO `terminal_info` VALUES ('4ee8dc74f0734a98a1dd8b0b3c296c9d', 'lcy7111', '新增设备测试3.10999', '9beff07ce3284503a8f418fb6a3919be', '0', '0', '', '0', '1d91cb74c2f849e284ca0915eed383c9', '1', '', '', '3333.230.000', '2020-03-11 11:18:06', null);
INSERT INTO `terminal_info` VALUES ('be392e989ca84155aeb66c11054272e7', 'lcy7111', '23123', '9dd6411e2c4c44febba8ea2b556abf05', '4234', '1234', '234', '0', '1d91cb74c2f849e284ca0915eed383c9', '1', '2341231', '432', '234', '2020-01-03 10:39:07', null);
INSERT INTO `terminal_info` VALUES ('4ee8dc74f0734a98a1dd8b0b3c296c9d', 'lcy7111', '新增设备测试3.10-1.在线', 'a1510bc8a8814bb481dbecea91e03ae5', '0', '0', '', '0', '1d91cb74c2f849e284ca0915eed383c9', '1', '', '', '333323', '2020-03-10 18:01:03', null);
INSERT INTO `terminal_info` VALUES ('d0eaaac849314f9da6fff04e7a2e4192', 'yc', '测试集中器', 'c2b43535629f4e9bab0fa23c77a7e2c3', '111', '222', '北京天安门', '1', 'fb8859d703b6421eae2a7425a5e3e32d', '0', '3761集中器', '2', '123456', '2020-01-17 15:42:36', '2020-01-17 15:44:06');
INSERT INTO `terminal_info` VALUES ('be392e989ca84155aeb66c11054272e7', 'lcy7111', '444', 'f9b183b5909646d48be3153d5b792355', '44444444', '44444', '444', '0', '480aef2f1a3f4c3b85f9a8fbf485f45c', '0', '444', '444', '44', '2020-01-03 10:39:38', '2020-01-03 11:25:05');
INSERT INTO `terminal_info` VALUES ('d0eaaac849314f9da6fff04e7a2e4192', 'yc', '测试集中器', 'fe8676d50158496188044b7c4e211f7e', '111', '222', '北京天安门', '0', 'fb8859d703b6421eae2a7425a5e3e32d', '0', '3761集中器', '2', '123456', '2020-01-17 15:41:42', '2020-01-17 15:44:04');
INSERT INTO `terminal_info` VALUES ('be392e989ca84155aeb66c11054272e7', 'lcy7111', '5555555', 'fffe1bd9a3dd4a82a34ed830c360d8bd', '5', '555', '55', '0', '1d91cb74c2f849e284ca0915eed383c9', '0', '55', '55', '55', '2020-01-03 10:40:14', '2020-01-06 09:07:48');

-- ----------------------------
-- Table structure for user_def_type
-- ----------------------------
DROP TABLE IF EXISTS `user_def_type`;
CREATE TABLE `user_def_type` (
  `type_id` varchar(100) NOT NULL COMMENT '类型id',
  `type_name` varchar(100) NOT NULL COMMENT '类型名称',
  `elements` varchar(5000) NOT NULL COMMENT '组成元素',
  `state` tinyint(4) NOT NULL COMMENT '状态（删除0、正常1、停用2）',
  `user_id` varchar(50) NOT NULL COMMENT '所属用户id',
  `specific_id` varchar(100) NOT NULL COMMENT '规约标识',
  `category` varchar(10) DEFAULT NULL COMMENT '类别',
  PRIMARY KEY (`type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户自定义类型表';

-- ----------------------------
-- Records of user_def_type
-- ----------------------------
INSERT INTO `user_def_type` VALUES ('basicType_0', 'NULL', '', '1', '0', '0', '');
INSERT INTO `user_def_type` VALUES ('basicType_10', 'Integer16BitType', '', '1', '0', '0', '');
INSERT INTO `user_def_type` VALUES ('basicType_11', 'ByteUnsigned', '', '1', '0', '0', '');
INSERT INTO `user_def_type` VALUES ('basicType_12', 'Integer16BitUnsigned', '', '1', '0', '0', '');
INSERT INTO `user_def_type` VALUES ('basicType_13', 'Long64', '', '1', '0', '0', '');
INSERT INTO `user_def_type` VALUES ('basicType_14', 'Long64-unsigned', '', '1', '0', '0', '');
INSERT INTO `user_def_type` VALUES ('basicType_15', 'Enum', '', '1', '0', '0', '');
INSERT INTO `user_def_type` VALUES ('basicType_16', 'Float32Bit', '', '1', '0', '0', '');
INSERT INTO `user_def_type` VALUES ('basicType_17', 'Float64Bit', '', '1', '0', '0', '');
INSERT INTO `user_def_type` VALUES ('basicType_18', 'DateTime', '', '1', '0', '0', '');
INSERT INTO `user_def_type` VALUES ('basicType_2', 'Bool', '', '1', '0', '0', '');
INSERT INTO `user_def_type` VALUES ('basicType_3', 'Bit-string', '', '1', '0', '0', '');
INSERT INTO `user_def_type` VALUES ('basicType_4', 'Integer32Bit', '', '1', '0', '0', '');
INSERT INTO `user_def_type` VALUES ('basicType_5', 'Integer32BitUnsigned', '', '1', '0', '0', '');
INSERT INTO `user_def_type` VALUES ('basicType_6', 'Octet-string', '', '1', '0', '0', '');
INSERT INTO `user_def_type` VALUES ('basicType_7', 'Ascii-string', '', '1', '0', '0', '');
INSERT INTO `user_def_type` VALUES ('basicType_9', 'ByteType', '', '1', '0', '0', '');
INSERT INTO `user_def_type` VALUES ('scriptType_hjjj', 'hjj111', '\"function forge(byteBuffer,argArray){\\n\\n}\\nfunction translate(byteBuffer){\\n\\n}\"', '2', 'yangcheng1', 'g697', '16');
INSERT INTO `user_def_type` VALUES ('userType_321', '123', '{\"bName0\":\"12e\",\"dataTypeName0\":\"Long64\",\"dataTypeKey0\":\"basicType_13\",\"bName1\":\"asds\",\"dataTypeName1\":\"ByteUnsigned\",\"dataTypeKey1\":\"basicType_11\",\"type0\":0,\"type1\":1}', '2', 'lcy7111', '23wq', '14');
INSERT INTO `user_def_type` VALUES ('userType_hjj111', '数据项名称', '{\"bName0\":\"1\",\"dataTypeName0\":\"Integer16BitType\",\"dataTypeKey0\":\"basicType_10\",\"bName1\":\"2\",\"dataTypeName1\":\"Long64\",\"dataTypeKey1\":\"basicType_13\",\"bName2\":\"3\",\"dataTypeName2\":\"ByteUnsigned\",\"dataTypeKey2\":\"basicType_11\",\"type0\":0,\"type1\":1,\"type2\":0}', '2', 'yangcheng1', 'g697', '14');
INSERT INTO `user_def_type` VALUES ('userType_test1', '测试1', '{\"bName0\":\"1\",\"dataTypeName0\":\"Integer16BitType\",\"dataTypeKey0\":\"basicType_10\",\"bName1\":\"\",\"dataTypeName1\":\"Long64-unsigned\",\"dataTypeKey1\":\"basicType_14\",\"type0\":0,\"type1\":1}', '2', 'yangcheng1', 'g697', '14');
